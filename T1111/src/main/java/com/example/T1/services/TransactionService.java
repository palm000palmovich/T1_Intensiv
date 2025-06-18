package com.example.T1.services;

import com.example.T1.component.RedisCacheUtils;

import com.example.T1.dto.TransactionMessage;
import com.example.T1.enums.AccountStatus;
import com.example.T1.enums.TransactionStatus;
import com.example.T1.exceptions.AccountNotFoundException;
import com.example.T1.model.Account;
import com.example.T1.model.Client;
import com.example.T1.model.Transaction;
import com.example.T1.repository.AccountRepository;
import com.example.T1.repository.ClientRepository;
import com.example.T1.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.example.dto.BlackListCheckResponse;
import org.example.dto.TransactionAcceptEvent;
import org.example.enums.ClientStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {
    private final KafkaTemplate<String, TransactionAcceptEvent> kafkaTemplate;
    private final AccountRepository accountRepository;
    private final RedisCacheUtils cacheUtils;
    private final Service2Client service2Client;
    private final ClientRepository clientRepository;
    private final TransactionRepository transactionRepository;
    @Value("${spring.cache.redis.time-to-lived}")
    private Long limitTime;
    private final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    @Value("${transaction.limit.count}")
    private int transactionLimitCount;
    @Value("${transaction.limit.time.seconds}")
    private long transactionLimitTimeSeconds;

    public TransactionService(KafkaTemplate<String, TransactionAcceptEvent> kafkaTemplate,
                               AccountRepository accountRepository,
                               RedisCacheUtils cacheUtils,
                              Service2Client service2Client,
                              ClientRepository clientRepository,
                              TransactionRepository transactionRepository){
        this.kafkaTemplate = kafkaTemplate;
        this.accountRepository = accountRepository;
        this.cacheUtils = cacheUtils;
        this.service2Client = service2Client;
        this.clientRepository = clientRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public void processTransaction(TransactionMessage transactionMessage) {
        logger.info("Начало обработки транзакции: {}", transactionMessage);

        Account account = accountRepository.getAccountByThroughId(transactionMessage.getAccId())
                .orElseThrow(() -> new AccountNotFoundException(transactionMessage.getAccId()));

        logger.info("Найденный акк: " + account.getId() + " " + account.getBalance() + " " + account.getClient().getId() + " " +
                account.getType() + " " + account.getAccountId() + " " + account.getStatus() + " " + account.getFrozenAmount());

        if (!AccountStatus.OPEN.equals(account.getStatus())) {
            logger.warn("Счет закрыт или заблокирован");
            return;
        }

        Client client = account.getClient();

        if (client.getStatus() == null){
            BlackListCheckResponse statusDto = service2Client
                    .checkClientStatus(client.getClientId(), account.getAccountId());
            client.setStatus(statusDto.getStatus());
            clientRepository.save(client);
        }

        ClientStatus currentStatus = client.getStatus();
        switch (currentStatus) {
            case OPEN -> {
                // Проверка на превышение лимита REJECTED-транзакций
                LocalDateTime timeAgo = LocalDateTime.now().minusSeconds(transactionLimitTimeSeconds);
                List<Transaction> recentRejected = transactionRepository
                        .findByAccountAndStatusAndTimestampAfter(account, TransactionStatus.REJECTED, timeAgo);

                if (recentRejected.size() >= transactionLimitCount) {
                    account.setStatus(AccountStatus.ARRESTED);
                    account.setFrozenAmount(account.getBalance());
                    accountRepository.save(account);
                    logger.warn("Счет {} переведен в статус ARRESTED из-за превышения лимита REJECTED-транзакций", account.getId());

                    // Проставляем текущей транзакции REJECTED
                    Transaction rejectedTransaction = new Transaction();
                    rejectedTransaction.setValue(transactionMessage.getValue());
                    rejectedTransaction.setTimestamp(LocalDateTime.now());
                    rejectedTransaction.setTransactionId(transactionMessage.getTransactionId());
                    rejectedTransaction.setStatus(TransactionStatus.REJECTED);
                    rejectedTransaction.setAccount(account);
                    account.addTransaction(rejectedTransaction);
                    accountRepository.save(account);

                    return;
                }

                sendAcceptEvent(account, transactionMessage);
            }
            case BLOCKED -> handleBlockedClient(account, transactionMessage);
            default -> throw new IllegalStateException("Неизвестный статус клиента: " + currentStatus);
        }

        }

    private void handleBlockedClient(Account account, TransactionMessage transactionMessage) {
        logger.info("Клиент со статусом BLOCKED");

        Transaction transaction = new Transaction();
        transaction.setValue(transactionMessage.getValue());
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setTransactionId(transactionMessage.getTransactionId());
        transaction.setStatus(TransactionStatus.REJECTED);
        transaction.setAccount(account);

        account.setStatus(AccountStatus.BLOCKED);
        account.addTransaction(transaction);
        accountRepository.save(account);

        logger.info("Транзакция отклонена, счет переведен в BLOCKED.");
    }

    private void sendAcceptEvent(Account account, TransactionMessage transactionMessage) {
        Transaction transaction = new Transaction();
        transaction.setValue(transactionMessage.getValue());
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setTransactionId(transactionMessage.getTransactionId());
        transaction.setStatus(TransactionStatus.REQUESTED);
        transaction.setAccount(account);

        long newBalance = account.getBalance() - transactionMessage.getValue();
        account.setBalance(newBalance);
        account.addTransaction(transaction);

        //Сохр-е инфы по счету в бд
        accountRepository.save(account);
        logger.info("Транзакция сохранена, баланс обновлен");
        TransactionAcceptEvent event = new TransactionAcceptEvent(
                account.getClient().getClientId(),
                account.getAccountId(),
                transaction.getTransactionId(),
                transaction.getTimestamp(),
                transaction.getValue(),
                newBalance
        );
        logger.info("Попытка отправки сущности {} в топик...", event.toString());
        try {
            kafkaTemplate.send(
                    "t1_demo_transaction_accept",
                    account.getAccountId().toString(),
                    event
            );

            logger.info("Информация об изменении счета отправлена в топик t1_demo_transaction_accept");
        } catch (Exception e) {
            logger.error("Проблема отправки сообщения в топик t1_demo_transaction_accept: {}",
                    e.getMessage());
        }
    }
}
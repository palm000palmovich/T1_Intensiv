package com.example.T1.services;

import com.example.T1.component.RedisCacheUtils;

import com.example.T1.dto.TransactionMessage;
import com.example.T1.enums.TransactionStatus;
import com.example.T1.exceptions.AccountNotFoundException;
import com.example.T1.model.Account;
import com.example.T1.model.Transaction;
import com.example.T1.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.example.dto.TransactionAcceptEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TransactionService {
    private final KafkaTemplate<String, TransactionAcceptEvent> kafkaTemplate;
    private final AccountRepository accountRepository;
    private final RedisCacheUtils cacheUtils;
    private final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    @Value("${spring.cache.redis.time-to-lived}")
    private Long limitTime;

    public TransactionService(KafkaTemplate<String, TransactionAcceptEvent> kafkaTemplate,
                               AccountRepository accountRepository,
                               RedisCacheUtils cacheUtils){
        this.kafkaTemplate = kafkaTemplate;
        this.accountRepository = accountRepository;
        this.cacheUtils = cacheUtils;
    }

    @Transactional
    public void processTransaction(TransactionMessage transactionMessage) {
        logger.info("Начало обработки транзакции: {}", transactionMessage);

        Account account = accountRepository.getAccountByThroughId(transactionMessage.getAccId())
                .orElseThrow(() -> new AccountNotFoundException(transactionMessage.getAccId()));

        logger.info("Акк для сохранения: " + account.getId() + " " + account.getBalance() + " " + account.getClient().getId() + " " +
                account.getType() + " " + account.getAccountId() + " " + account.getStatus() + " " + account.getFrozenAmount());

        //Проверка статуса счета
        if (account.getStatus().toString().equals("OPEN")) {
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


            //отправка в топик в топик t1_demo_transaction_accept
            sendAcceptEvent(account, transaction, newBalance);

        }
    }


    private void sendAcceptEvent(Account account, Transaction transaction, Long newBalance) {
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


    /*private Account findAcc(TransactionMessage transactionMessage){
        Long accountId = transactionMessage.getAccId();
        Account foundAccount = new Account();

        //Проверка кеша
        String fullKey = "accountDto::" + accountId;

        if (cacheUtils.hasKey(fullKey)){
            AccountDto accountDto = cacheUtils.getValue(fullKey, AccountDto.class);
            logger.info("Счет транзакции {} есть в кеше", accountDto.toString());
            foundAccount.setId(accountDto.getPrimaryKey());
            foundAccount.setType(accountDto.getAccountType());
            foundAccount.setBalance(accountDto.getBalance());
            foundAccount.setAccountId(accountDto.getAccountId());
            foundAccount.setStatus(accountDto.getStatus());
            foundAccount.setFrozenAmount(accountDto.getFrozenAmount());

            Client client = new Client();
            client.setId(accountDto.getClientId());
            foundAccount.setClient(client);


            return foundAccount;

        } else{
            foundAccount = accountRepository.getAccountByThroughId(accountId)
                    .orElseThrow(() -> new AccountNotFoundException(accountId));


            if (foundAccount.getClient() == null){
                logger.error("У аккаунта не найден клиент.");
            }

            AccountDto accountDto = new AccountDto(
                    foundAccount.getId(),
                    foundAccount.getType(),
                    foundAccount.getBalance(),
                    foundAccount.getAccountId(),
                    foundAccount.getStatus(),
                    foundAccount.getFrozenAmount(),
                    foundAccount.getClient().getId()
            );

            logger.info("Найденный аккаунт: {}", accountDto.toString());

            //Попытка кеширования
            try {
                cacheUtils.putValue(fullKey, accountDto, limitTime);
                logger.info("Сущность была кеширована");
            } catch(RuntimeException exception){
                logger.error("Проблемы с кешированием: {}", exception.getMessage());
            }

        }

        return foundAccount;
    }*/
    //TODO сделать прогон через кеш редиса, сейчас работает говняно
}
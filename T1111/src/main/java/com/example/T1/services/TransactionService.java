package com.example.T1.services;

import com.example.T1.component.RedisCacheUtils;
import com.example.T1.dto.AccountDto;
import com.example.T1.dto.TransactionAcceptEvent;
import com.example.T1.dto.TransactionMessage;
import com.example.T1.enums.TransactionStatus;
import com.example.T1.exceptions.AccountNotFoundException;
import com.example.T1.exceptions.InsufficientFundsException;
import com.example.T1.model.Account;
import com.example.T1.model.Transaction;
import com.example.T1.repository.AccountRepository;
import jakarta.transaction.Transactional;
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
    public void processTransaction(TransactionMessage transactionMessage){
        try {
            Account account = findAcc(transactionMessage);

            //Проверка статуса счета
            if (account.getStatus().toString().equals("OPEN")){
                Transaction transaction = new Transaction();
                transaction.setValue(transactionMessage.getValue());
                transaction.setTimestamp(LocalDateTime.now());
                transaction.setTransactionId(transactionMessage.getTransactionId());
                transaction.setStatus(TransactionStatus.REQUESTED);
                transaction.setAccount(account);

                if (account.getBalance() < transactionMessage.getValue()){
                    throw new InsufficientFundsException(account.getId(), account.getBalance(),
                            transactionMessage.getValue());
                }
                long newBalance = account.getBalance() - transactionMessage.getValue();
                account.setBalance(newBalance);
                account.addTransaction(transaction);

                //Сохр-е инфы по счету в бд
                accountRepository.save(account);

                cacheUtils.putValue(
                        "accounts::" + account.getAccountId(),
                        account,
                        limitTime
                );

                //отправка в топик в топик t1_demo_transaction_accept
                sendAcceptEvent(account, transaction, newBalance);

            }

        } catch(AccountNotFoundException exep){
            logger.error("Вылетело исключение: {}", exep.getMessage());
            throw exep;
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


    private Account findAcc(TransactionMessage transactionMessage){
        Long accountId = transactionMessage.getAccId();
        //Проверка кеша
        Account foundAccount = new Account();
        String fullKey = "accounts::" + accountId;
        if (cacheUtils.hasKey(fullKey)){
            logger.info("Счет транзакции {} есть в кеше", transactionMessage.toString());
            foundAccount = cacheUtils.getValue(fullKey, Account.class);
        } else{
            foundAccount = accountRepository.findById(accountId).orElseThrow(() ->
                    new AccountNotFoundException(accountId));

            //Попытка кеширования
            try {
                cacheUtils.putValue(fullKey, foundAccount, limitTime);
                logger.info("Сущность была кеширована");
            } catch(RuntimeException exception){
                logger.error("Проблемы с кешированием: {}", exception.getMessage());
            }

            AccountDto accountDto = new AccountDto(foundAccount.getType(), foundAccount.getBalance(),
                    foundAccount.getAccountId(), foundAccount.getStatus(), foundAccount.getFrozenAmount());
            logger.info("Найденный аккаунт: " + accountDto.toString());
        }

        return foundAccount;
    }
}
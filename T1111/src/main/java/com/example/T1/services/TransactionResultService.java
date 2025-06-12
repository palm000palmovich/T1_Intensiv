package com.example.T1.services;

import com.example.T1.dto.TransactionDto;
import com.example.T1.enums.AccountStatus;
import com.example.T1.enums.TransactionStatus;
import com.example.T1.exceptions.AccountNotFoundException;
import com.example.T1.exceptions.TransactionNotFoundException;
import com.example.T1.mappers.TransactionMapper;
import com.example.T1.model.Account;
import com.example.T1.model.Transaction;
import com.example.T1.repository.AccountRepository;
import com.example.T1.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.example.dto.TransactionResult;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TransactionResultService {
    private Logger logger = LoggerFactory.getLogger(TransactionResultService.class);
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;

    public TransactionResultService(TransactionRepository transactionRepository,
                                    AccountRepository accountRepository,
                                    TransactionMapper transactionMapper){
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.transactionMapper = transactionMapper;
    }

    @Transactional
    public void processTransactionResult(TransactionResult transactionResult){
        Long transactionThroughId = transactionResult.getTransactionId();
        Transaction transaction = transactionRepository.getTransactionByThroughId(transactionThroughId)
                .orElseThrow(() -> new TransactionNotFoundException(transactionThroughId));

        Account accountForProcess = accountRepository.getAccountByThroughId(transactionResult.getAccountId())
                .orElseThrow(() -> new AccountNotFoundException(transactionResult.getAccountId()));

        TransactionDto transactionDto = transactionMapper.entityToDto(transaction, accountForProcess.getId());

        logger.info("Обработка результирующей транзакции: {}...", transactionDto.toString());

        if (transactionResult.getStatus().equals(TransactionStatus.ACCEPTED.toString())){ //Если ACCEPTED
            logger.info("Обработка ACCEPTED-транзакции...");
            transaction.setStatus(TransactionStatus.ACCEPTED);
            transactionRepository.save(transaction);
            logger.info("Изменился статус транзакции {} на ACCEPTED", transactionDto);
        } else if (transactionResult.getStatus().equals(TransactionStatus.BLOCKED.toString())){ //Если Blocked
            logger.info("Обработка BLOCKED-транзакции...");
            transaction.setStatus(TransactionStatus.BLOCKED);

            accountForProcess.setStatus(AccountStatus.BLOCKED);
            accountForProcess.setFrozenAmount(accountForProcess.getFrozenAmount() + transactionDto.getValue());

            transaction.setAccount(accountForProcess);
            accountRepository.save(accountForProcess);
            logger.info("Счет с внешним айди {} заблокирован.", accountForProcess.getAccountId());
        } else if(transactionResult.getStatus().equals(TransactionStatus.REJECTED.toString())){
            logger.info("Обработка REJECTED-транзакции...");
            transaction.setStatus(TransactionStatus.REJECTED);
            accountForProcess.setBalance(accountForProcess.getBalance() + transaction.getValue());
            transaction.setAccount(accountForProcess);

            accountRepository.save(accountForProcess);
            logger.info("На счете с внешним айди {} недостаточно средств. Транзакция не прошла", accountForProcess.getAccountId());
        }
    }

}

package com.example.T1.services;

import com.example.T1.annotations.Cached;
import com.example.T1.annotations.LogDataSourceError;
import com.example.T1.annotations.Metric;
import com.example.T1.dto.AccountDto;
import com.example.T1.exceptions.AccountNotFoundException;
import com.example.T1.exceptions.UserNotFoundException;
import com.example.T1.mappers.AccountMapper;
import com.example.T1.model.Account;
import com.example.T1.model.Client;
import com.example.T1.repository.AccountRepository;
import com.example.T1.repository.ClientRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AccountService {

    private AccountRepository accountRepository;

    private AccountMapper accountMapper;

    private ClientRepository clientRepository;

    public AccountService(AccountRepository accountRepository,
                          AccountMapper accountMapper,
                          ClientRepository clientRepository){
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
        this.clientRepository = clientRepository;
    }

    private final Logger logger = LoggerFactory.getLogger(AccountService.class);

    @LogDataSourceError
    @Metric
    public Account createAccount(Long clientId, AccountDto accountDto) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> {
                    logger.error("Client not found with ID: {}", clientId);
                    return new UserNotFoundException(clientId);
                });

        Account account = accountMapper.dtoToModel(accountDto);
        logger.info("Received: " + account.toString());
        account.setClient(client);

        return accountRepository.save(account);
    }

    @LogDataSourceError
    @Cached(cacheName = "accounts", key = "#id")
    @Metric
    public Account getAccountById(Long id) {
        logger.info("Fetching account with ID: {}", id);

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Account not found with ID: {}", id);
                    return new AccountNotFoundException(id);
                });

        return account;
    }


    @LogDataSourceError
    @Cached(cacheName = "accounts", key = "#id")
    @Metric
    public Account updateAccount(Long id, AccountDto accountDto) {
        logger.info("Updating account with ID: {}", id);

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Account not found with ID: {}", id);
                    return new AccountNotFoundException(id);
                });

        account.setType(accountDto.getAccountType());
        account.setBalance(accountDto.getBalance());

        return accountRepository.save(account);
    }


    @LogDataSourceError
    @Cached(cacheName = "accounts", key = "#id")
    @Metric
    public Account deleteAccount(Long id) {
        logger.info("Deleting account with ID: {}", id);

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Account not found with ID: {}", id);
                    return new AccountNotFoundException(id);
                });

        accountRepository.delete(account);

        return account;
    }
}

package com.example.T1.services;

import com.example.T1.annotations.Cached;
import com.example.T1.annotations.LogDataSourceError;
import com.example.T1.annotations.Metric;
import com.example.T1.dto.AccountDto;
import com.example.T1.dto.CreateAccount;
import com.example.T1.exceptions.AccountNotFoundException;
import com.example.T1.exceptions.UserNotFoundException;
import com.example.T1.mappers.AccountMapper;
import com.example.T1.mappers.ClientMapper;
import com.example.T1.model.Account;
import com.example.T1.model.Client;
import com.example.T1.repository.AccountRepository;
import com.example.T1.repository.ClientRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    public AccountService(AccountRepository accountRepository,
                          AccountMapper accountMapper,
                          ClientRepository clientRepository,
                          ClientMapper clientMapper){
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
    }

    private final Logger logger = LoggerFactory.getLogger(AccountService.class);

    @LogDataSourceError
    @Metric
    public Account createAccount(Long primaryClientId, CreateAccount createAccount) {
        //TODO добавить проверку на jwt
        //TODO добавить кеширование клиента
        Client client = clientRepository.findById(primaryClientId)
                .orElseThrow(() -> {
                    logger.error("Client not found with ID: {}", primaryClientId);
                    return new UserNotFoundException(primaryClientId);
                });
        logger.info("Найденный клиент: {}", clientMapper.entityToDto(client));

        Account accountForSaving = accountMapper.dtoToEntity(createAccount);
        accountForSaving.setClient(client);

        return accountRepository.save(accountForSaving);
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

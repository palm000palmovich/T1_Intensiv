package com.example.T1.mappers;

import com.example.T1.dto.AccountDto;
import com.example.T1.dto.CreateAccount;
import com.example.T1.enums.AccountStatus;
import com.example.T1.model.Account;
import org.springframework.stereotype.Component;


@Component
public class AccountMapper {

    public Account dtoToEntity(CreateAccount createAccount){
        Account account = new Account();

        account.setType(createAccount.getAccType());
        account.setBalance(createAccount.getBalance());
        account.setAccountId(createAccount.getAccountId());
        account.setStatus(AccountStatus.OPEN);
        account.setFrozenAmount(0L);

        return account;
    }

}
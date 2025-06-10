package com.example.T1.mappers;

import com.example.T1.dto.AccountDto;
import com.example.T1.model.Account;
import org.springframework.stereotype.Component;


@Component
public class AccountMapper {

    public Account dtoToModel(AccountDto accountDto){
        Account account = new Account();

        account.setType(accountDto.getAccountType());
        account.setBalance(accountDto.getBalance());

        return account;
    }

}
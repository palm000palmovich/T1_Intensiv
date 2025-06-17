package com.example.T1.dto;

import com.example.T1.enums.AccountStatus;
import com.example.T1.enums.Type;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class CreateAccount {
    @Enumerated(EnumType.STRING)
    private Type accType;
    private Long balance;
    private Long accountId;

    public CreateAccount(){}

    public CreateAccount(Long balance, Type accType, Long accountId) {
        this.balance = balance;
        this.accType = accType;
        this.accountId = accountId;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public Type getAccType() {
        return accType;
    }

    public void setAccType(Type accType) {
        this.accType = accType;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    @Override
    public String toString() {
        return "CreateAccount{" +
                "balance=" + balance +
                ", accType=" + accType +
                ", accountId=" + accountId +
                '}';
    }
}

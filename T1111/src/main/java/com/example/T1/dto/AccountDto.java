package com.example.T1.dto;

import com.example.T1.enums.AccountStatus;
import com.example.T1.enums.Type;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class AccountDto {
    private Long primaryKey;
    @Enumerated(EnumType.STRING)
    private Type accountType;
    private Long balance;
    private Long accountId;
    @Enumerated(EnumType.STRING)
    private AccountStatus status;
    private Long frozenAmount;
    private Long clientId;


    public AccountDto(Long primaryKey,Type accountType, Long balance,
                      Long accountId, AccountStatus status, Long
                              frozenAmount, Long clientId) {
        this.primaryKey = primaryKey;
        this.accountType = accountType;
        this.balance = balance;
        this.accountId = accountId;
        this.status = status;
        this.frozenAmount = frozenAmount;
        this.clientId = clientId;
    }

    public AccountDto(Type accountType, Long balance, Long accountId,
                      AccountStatus status, Long frozenAmount) {
        this.accountType = accountType;
        this.balance = balance;
        this.accountId = accountId;
        this.status = status;
        this.frozenAmount = frozenAmount;
    }

    public AccountDto(){}

    public Type getAccountType() {
        return accountType;
    }

    public void setAccountType(Type accountType) {
        this.accountType = accountType;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public Long getFrozenAmount() {
        return frozenAmount;
    }

    public void setFrozenAmount(Long frozenAmount) {
        this.frozenAmount = frozenAmount;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(Long primaryKey) {
        this.primaryKey = primaryKey;
    }

    @Override
    public String toString() {
        return "AccountDto{" +
                "primaryKey=" + primaryKey +
                ", accountType=" + accountType +
                ", balance=" + balance +
                ", accountId=" + accountId +
                ", status=" + status +
                ", frozenAmount=" + frozenAmount +
                ", clientId=" + clientId +
                '}';
    }
}

package com.example.T1.dto;

import java.time.LocalDateTime;

public class TransactionAcceptEvent {
    private Long clientId;
    private Long accountId;
    private Long transactionId;
    private LocalDateTime timestamp;
    private Long amount;
    private Long balance;

    public TransactionAcceptEvent(){}

    public TransactionAcceptEvent(Long clientId, Long accountId,
                                  Long transactionId, LocalDateTime timestamp,
                                  Long amount, Long balance) {
        this.clientId = clientId;
        this.accountId = accountId;
        this.transactionId = transactionId;
        this.timestamp = timestamp;
        this.amount = amount;
        this.balance = balance;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }
}
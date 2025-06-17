package com.example.T1.dto;

import com.example.T1.enums.TransactionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalDateTime;

public class TransactionDto {
    private Long id;
    private Long value;
    private LocalDateTime timestamp;
    private Long transactionId;
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
    private Long accountId;

    public TransactionDto(){}

    public TransactionDto(Long id, Long value, LocalDateTime timestamp,
                          Long transactionId, TransactionStatus status,
                          Long accountId) {
        this.id = id;
        this.value = value;
        this.timestamp = timestamp;
        this.transactionId = transactionId;
        this.status = status;
        this.accountId = accountId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    @Override
    public String toString() {
        return "TransactionDto{" +
                "id=" + id +
                ", value=" + value +
                ", timestamp=" + timestamp +
                ", transactionId=" + transactionId +
                ", status=" + status +
                ", accountId=" + accountId +
                '}';
    }
}

package org.example.dto;

import java.io.Serializable;

public class TransactionResult implements Serializable {
    private Long transactionId;
    private Long accountId;
    private String status;

    public TransactionResult(Long transactionId, Long accountId, String status) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.status = status;
    }

    public TransactionResult(){}

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "TransactionResult{" +
                "transactionId=" + transactionId +
                ", accountId=" + accountId +
                ", status='" + status + '\'' +
                '}';
    }
}
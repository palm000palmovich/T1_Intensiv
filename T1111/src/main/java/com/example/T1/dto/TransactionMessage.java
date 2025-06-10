package com.example.T1.dto;

import javax.validation.constraints.NotNull;

public class TransactionMessage {
    @NotNull(message = "Сумма транзакции не может быть нулевой.")
    private Long value;
    private Long transactionId;
    private Long accId;

    public TransactionMessage(Long value, Long transactionId, Long accId) {
        this.value = value;
        this.transactionId = transactionId;
        this.accId = accId;
    }

    public TransactionMessage(){}

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getAccId() {
        return accId;
    }

    public void setAccId(Long accId) {
        this.accId = accId;
    }

    @Override
    public String toString() {
        return "TransactionMessage{" +
                "value=" + value +
                ", transactionId=" + transactionId +
                ", accId=" + accId +
                '}';
    }
}
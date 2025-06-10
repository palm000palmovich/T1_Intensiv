package com.example.T1.exceptions;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(Long accountId, Long currentBalance, Long requiredAmount) {

      super(String.format("Account %s has insufficient funds: %d < %d",
              accountId, currentBalance, requiredAmount));
    }
}
package com.example.T1.mappers;

import com.example.T1.dto.TransactionDto;
import com.example.T1.model.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionDto entityToDto(Transaction transaction){
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setId(transaction.getId());
        transactionDto.setValue(transaction.getValue());
        transactionDto.setTimestamp(transaction.getTimestamp());
        transactionDto.setTransactionId(transaction.getTransactionId());
        transactionDto.setStatus(transaction.getStatus());
        transactionDto.setAccountId(transaction.getAccount().getId());

        return transactionDto;
    }

    //Overloaded method
    public TransactionDto entityToDto(Transaction transaction, Long accontId){
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setId(transaction.getId());
        transactionDto.setValue(transaction.getValue());
        transactionDto.setTimestamp(transaction.getTimestamp());
        transactionDto.setTransactionId(transaction.getTransactionId());
        transactionDto.setStatus(transaction.getStatus());
        transactionDto.setAccountId(accontId);

        return transactionDto;
    }
}

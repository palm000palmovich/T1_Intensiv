package com.example.T1.kafka;

import com.example.T1.exceptions.AccountNotFoundException;
import com.example.T1.exceptions.TransactionNotFoundException;
import com.example.T1.services.TransactionResultService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.dto.TransactionResult;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class TransactionResultConsumer {
    private Logger logger = LoggerFactory.getLogger(TransactionResultConsumer.class);
    private final TransactionResultService transactionResultService;

    public TransactionResultConsumer(TransactionResultService transactionResultService){
        this.transactionResultService = transactionResultService;
    }

    @KafkaListener(topics = "t1_demo_transaction_result", groupId = "transaction-consumer-result",
            containerFactory = "kafkaListenerContainerFactory1")
    public void processTransactionResult(ConsumerRecord<String, TransactionResult> record){
        TransactionResult transactionResult = record.value();

        logger.info("Получен результат обработки транзакции: {}", transactionResult.toString());

        try{
            transactionResultService.processTransactionResult(transactionResult);
        } catch (TransactionNotFoundException exep){
            logger.error(exep.getMessage());
        } catch (AccountNotFoundException ex){
            logger.error("Ошыпка: {}",ex.getMessage());
        }
    }
}
package com.example.T1.kafka;

import com.example.T1.dto.TransactionMessage;
import com.example.T1.exceptions.InsufficientFundsException;
import com.example.T1.services.TransactionService;
import jakarta.transaction.Transactional;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;

@Component
@Transactional
public class TransactionConsumer {
    private final Logger logger = LoggerFactory.getLogger(TransactionConsumer.class);
    private final TransactionService transactionService;

    public TransactionConsumer(TransactionService transactionService){
        this.transactionService = transactionService;
    }

    @KafkaListener(topics = "t1_demo_transactions", groupId = "transaction-consumer-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void processTransaction(ConsumerRecord<String, TransactionMessage> record){
        TransactionMessage transactionMessage = record.value();
        logger.info("Транзакция из топика t1_demo_transactions: {}", transactionMessage.toString());

        try{
            transactionService.processTransaction(transactionMessage);
        } catch(InsufficientFundsException e){
            logger.error(e.getMessage());
        }

    }


}
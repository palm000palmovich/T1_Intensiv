package org.example.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import org.example.dto.TransactionAcceptEvent;
import org.example.services.TransactionCheckService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
public class TransactionEventAcceptConsumer {
    private Logger logger = LoggerFactory.getLogger(TransactionEventAcceptConsumer.class);
    private final TransactionCheckService transactionCheckService;

    public TransactionEventAcceptConsumer(TransactionCheckService transactionCheckService){
        this.transactionCheckService = transactionCheckService;
    }

    @KafkaListener(topics = "t1_demo_transaction_accept", groupId = "transaction-consumer-group",
    containerFactory = "kafkaListenerContainerFactory")
    public void processTransactionEvent(ConsumerRecord<String, TransactionAcceptEvent> record) {
        TransactionAcceptEvent transactionAcceptEvent= record.value();

        logger.info("Получена информация по транзакции: {}",transactionAcceptEvent.toString());
        transactionCheckService.processTransactionEvent(transactionAcceptEvent);
    }

}
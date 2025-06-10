package com.example.T1.controllers;

import com.example.T1.dto.TransactionMessage;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping(path = "/transaction")
public class TransactionController {
    private final KafkaTemplate<String, TransactionMessage> kafkaTemplate;
    private Logger logger = LoggerFactory.getLogger(TransactionController.class);

    public TransactionController(KafkaTemplate<String, TransactionMessage> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping(path = "/send")
    public ResponseEntity<String> sendTransaction(@Valid @RequestBody TransactionMessage transactionMessage){
        logger.info("Получено сообщение: {}", transactionMessage.toString());
        try{
            kafkaTemplate.send("t1_demo_transactions",
                            transactionMessage.getTransactionId().toString(),
                            transactionMessage);
            logger.info("Транзакция отправлена: {}", transactionMessage);
            return ResponseEntity.ok("Транзакция успешно отправлена в Kafka");
        } catch(RuntimeException ex){
            logger.error("Ошибка при отправке транзакции", ex);
            return ResponseEntity.internalServerError()
                    .body("Ошибка при отправке транзакции: " + ex.getMessage());
        }
    }
}
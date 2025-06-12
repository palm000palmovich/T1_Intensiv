package org.example.services;

import org.example.dto.TransactionAcceptEvent;
import org.example.dto.TransactionResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class TransactionCheckService {
    private final KafkaTemplate<String, TransactionResult> kafkaTemplate;
    @Value("${transaction.limit.count}")
    private int transactionLimitCount;
    @Value("${transaction.limit.time.seconds}")
    private long transactionLimitTimeSeconds;
    private Logger logger = LoggerFactory.getLogger(TransactionCheckService.class);

    private final Map<Long, List<TransactionAcceptEvent>> clientAccountTransactions = new ConcurrentHashMap<>();

    public TransactionCheckService(KafkaTemplate<String, TransactionResult> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    public void processTransactionEvent(TransactionAcceptEvent event){

        //Проверка баланса
        if (event.getAmount() > event.getBalance()) {
            sendTransactionResult(event.getTransactionId(), event.getAccountId(), "REJECTED");
            return;
        }

        if (checkTransactionLimit(event)){
            return;
        }

        logger.info("Транзакция одобрена.");
        sendTransactionResult(event.getTransactionId(), event.getAccountId(), "ACCEPTED");
    }

    private boolean checkTransactionLimit(TransactionAcceptEvent event){
        logger.info("Проверка частоты транзакций.");

        Long clientAccountKey = generateUniqueKey(event.getClientId(), event.getAccountId());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime timeThreshold = now.minusSeconds(transactionLimitTimeSeconds);

        clientAccountTransactions.computeIfAbsent(clientAccountKey,
                k -> new ArrayList<>()).add(event);

        List<TransactionAcceptEvent> recentTransactions = clientAccountTransactions.get(clientAccountKey).stream()
                .filter(t -> t.getTimestamp().isAfter(timeThreshold))
                .collect(Collectors.toList());

        // Если превышен лимит
        if (recentTransactions.size() >= transactionLimitCount) {
            logger.info("Лимит по транзакциям превышен.");
            // Блокируем все транзакции в этом периоде
            recentTransactions.forEach(t ->
                    sendTransactionResult(t.getTransactionId(), t.getAccountId(), "BLOCKED")
            );

            clientAccountTransactions.remove(clientAccountKey);
            return true;
        }

        logger.info("Частота транзакций соответствует требованиям: не более {} операций за {} секунд", transactionLimitCount, transactionLimitTimeSeconds);
        return false;
    }

    private Long generateUniqueKey(Long clientId, Long accountId){
        return clientId * 31 + accountId;
    }

    private void sendTransactionResult(Long tranId, Long accId, String status){
        TransactionResult transactionResult = new TransactionResult(tranId, accId, status);

        try{
            kafkaTemplate.send("t1_demo_transaction_result", transactionResult);
            logger.info("В топик с результатом было отправлено: {}", transactionResult.toString());
        } catch (Exception ex){
            logger.error(ex.getMessage());
        }
    }
}

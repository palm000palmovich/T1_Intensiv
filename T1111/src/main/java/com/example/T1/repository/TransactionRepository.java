package com.example.T1.repository;

import com.example.T1.enums.TransactionStatus;
import com.example.T1.model.Account;
import com.example.T1.model.Transaction;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query(value = "select * from transaction t where t.transactionid = :tranId", nativeQuery = true)
    Optional<Transaction> getTransactionByThroughId(@Param("tranId") Long tranId);

//    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.account_id = :accountId AND t.status = 'REJECTED' AND t.timestamp >= :fromDate")
//    long countRejectedTransactions(@Param("accountId") Long accountId, @Param("fromDate") LocalDateTime fromDate);

    List<Transaction> findByAccountAndStatusAndTimestampAfter(
            Account account, TransactionStatus status, LocalDateTime timestamp);

}
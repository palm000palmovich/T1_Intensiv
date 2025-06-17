package com.example.T1.repository;

import com.example.T1.model.Transaction;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query(value = "select * from transaction t where t.transactionid = :tranId", nativeQuery = true)
    Optional<Transaction> getTransactionByThroughId(@Param("tranId") Long tranId);
}
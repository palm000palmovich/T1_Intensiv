package com.example.T1.repository;

import com.example.T1.dto.AccountDto;
import com.example.T1.model.Account;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query(value = "select * from account a where a.accountId = :accId", nativeQuery = true)
    Optional<Account> getAccountByThroughId(@Param("accId") Long accId);
}
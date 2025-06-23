package com.example.T1.repository;

import com.example.T1.model.Account;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query(value = "select * from account a where a.accountId = :accId", nativeQuery = true)
    Optional<Account> getAccountByThroughId(@Param("accId") Long accId);

    @Query(value = "select * from account order by id desc limit 1", nativeQuery = true)
    Optional<Account> getLastAccount();

    @Query(value = "select accountid from account a where a.status = 'BLOCKED' limit :limit", nativeQuery = true)
    List<Long> getAccountsForUnblocking(@Param("limit") int limit);

    @Query(value = "SELECT COUNT(*) FROM account a WHERE a.status = 'ARRESTED'", nativeQuery = true)
    Long countFrozenAccounts();
}
package org.example.repositories;

import org.example.model.BlackList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlackListRepository extends JpaRepository<BlackList, Long> {
    @Query(value = "select * from blackList bl where bl.clientid = :clId " +
            "and bl.accountid = :accId limit 1", nativeQuery = true)
    Optional<BlackList> getBlackListByAllIds(@Param("clId") Long clId, @Param("accId") Long accId);
}

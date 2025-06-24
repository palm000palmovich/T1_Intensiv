package com.example.T1.repository;

import com.example.T1.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    @Query(value = "select * from clients order by id desc limit 1", nativeQuery = true)
    Optional<Client> getLastClient();

    @Query(value = "select clientid from clients c where c.status='BLOCKED' limit :limit", nativeQuery = true)
    List<Long> getClientsIdsForUnblocking(@Param("limit") int limit);

    @Query(value = "select * from clients c where c.clientid = :clId", nativeQuery = true)
    Optional<Client> getClientByThroughId(@Param("clId") Long clId);

    @Query(value = "SELECT COUNT(*) FROM clients c WHERE c.status = 'BLOCKED'", nativeQuery = true)
    Long countBlockedClients();
}

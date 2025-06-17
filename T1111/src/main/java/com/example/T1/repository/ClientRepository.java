package com.example.T1.repository;

import com.example.T1.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    @Query(value = "select * from clients order by id desc limit 1", nativeQuery = true)
    Optional<Client> getLastClient();
}

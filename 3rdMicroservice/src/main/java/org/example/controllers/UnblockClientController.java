package org.example.controllers;

import org.example.dto.UnblockedEntity;
import org.example.services.UnblockEntityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping(path = "/api/client")
public class UnblockClientController {
    private final UnblockEntityService unblockClientService;
    private final Logger logger = LoggerFactory.getLogger(UnblockClientController.class);

    public UnblockClientController(UnblockEntityService unblockClientService){
        this.unblockClientService = unblockClientService;
    }

    @PostMapping(path = "/unblock")
    public ResponseEntity<UnblockedEntity> unblockClient(@RequestBody(required = true) List<Long> ids,
                                                         @RequestHeader("Authorization") String token){
        try{
            UnblockedEntity unblockedClients = unblockClientService.unblockEntity(ids, token);
            return ResponseEntity.ok(unblockedClients);
        } catch (RuntimeException ex){
            logger.info("Ошибка: {}", ex.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

}

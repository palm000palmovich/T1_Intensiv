package org.example.controllers;

import org.example.dto.UnblockedEntity;
import org.example.services.UnblockEntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/account")
public class UnblockAccountController {
    private final UnblockEntityService unblockAccountService;
    private final Logger logger = LoggerFactory.getLogger(UnblockAccountController.class);

    public UnblockAccountController(UnblockEntityService unblockAccountService){
        this.unblockAccountService = unblockAccountService;
    }

    @PostMapping(path = "/unblock")
    public ResponseEntity<UnblockedEntity> unblockClient(@RequestBody(required = true) List<Long> ids,
                                                         @RequestHeader("Authorization") String token){
        try{
            UnblockedEntity unblockedAccount = unblockAccountService.unblockEntity(ids, token);
            return ResponseEntity.ok(unblockedAccount);
        } catch (RuntimeException ex){
            logger.info("Ошибка: {}", ex.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}

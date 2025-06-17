package org.example.controllers;

import org.example.dto.BlackListCheck;
import org.example.dto.BlackListCheckResponse;
import org.example.services.BlackListService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping(path = "/api/client")
public class BlackListController {
    private final BlackListService blackListService;
    private final Logger logger = LoggerFactory.getLogger(BlackListController.class);

    public BlackListController(BlackListService blackListService){
        this.blackListService = blackListService;
    }

    @PostMapping("/status")
    public ResponseEntity<BlackListCheckResponse> getClientStatus(
            @RequestBody(required = true) BlackListCheck dto,
            @RequestHeader("Authorization") String token) {

        try{
            BlackListCheckResponse blackListCheckResponse = blackListService.isBlack(dto, token);
            logger.info("Ответ сервису: {}", blackListService.isBlack(dto, token));
            return ResponseEntity.ok(blackListCheckResponse);
        } catch (RuntimeException exep){
            logger.info("Ошибка: {}", exep.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}

package org.example.controllers;

import org.example.component.JwtUtil;
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
    private final JwtUtil jwtUtil;
    private final Logger logger = LoggerFactory.getLogger(BlackListController.class);

    public BlackListController(BlackListService blackListService,
                               JwtUtil jwtUtil){
        this.blackListService = blackListService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/status")
    public ResponseEntity<BlackListCheckResponse> getClientStatus(
            @RequestBody(required = true) BlackListCheck dto,
            @RequestHeader("Authorization") String token) {

        logger.info("Полученное дто: {}", dto.toString());
        //TODO это перенести в сервис
        String pureToken = token.replace("Bearer ", "");
        if (!jwtUtil.validateServiceToken(pureToken)) {
            logger.error("Проблема с межсервисной авторизацией");
            throw new RuntimeException("Unauthorized");
        }

        return ResponseEntity.ok(blackListService.isBlack(dto));
    }
}

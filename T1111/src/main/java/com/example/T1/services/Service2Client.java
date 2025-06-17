package com.example.T1.services;

import com.example.T1.component.JwtUtil;
import org.example.dto.BlackListCheck;
import org.example.dto.BlackListCheckResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class Service2Client {
    private final RestTemplate restTemplate;
    private final JwtUtil jwtUtil;
    private final Logger logger = LoggerFactory.getLogger(Service2Client.class);

    public Service2Client(RestTemplate restTemplate, JwtUtil jwtUtil){
        this.restTemplate = restTemplate;
        this.jwtUtil = jwtUtil;
    }

    private static final String SERVICE_2_URL = "http://localhost:8081/api/client/status";

    public BlackListCheckResponse checkClientStatus(Long clientId, Long accountId) {
        String serviceToken = jwtUtil.generateServiceToken();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + serviceToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        BlackListCheck blackListCheckForPost = new BlackListCheck(clientId, accountId);
        logger.info("Полученная сущность для проверки: {}", blackListCheckForPost.toString());

        HttpEntity<BlackListCheck> request = new HttpEntity<>(
                blackListCheckForPost, headers);

        ResponseEntity<BlackListCheckResponse> response = restTemplate.postForEntity(
                SERVICE_2_URL,
                request,
                BlackListCheckResponse.class);

        logger.info("Полученный ответ от второго сервиса: {}", response.getBody().toString());
        return response.getBody();
    }
}

package com.example.T1.services;

import com.example.T1.component.JwtUtil;
import com.example.T1.model.Client;
import com.example.T1.repository.ClientRepository;
import org.example.dto.UnblockedEntity;
import org.example.enums.ClientStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class UnblockClientService {
    private final Logger logger = LoggerFactory.getLogger(UnblockClientService.class);
    private final JwtUtil jwtUtil;
    private final ClientRepository clientRepository;
    private final RestTemplate restTemplate;
    public static final String UNBLOCK_CLIENT_URL = "http://localhost:8082/api/client/unblock";

    @Value("${unblock.client.count}")
    private int N;

    public UnblockClientService(JwtUtil jwtUtil,
                                ClientRepository clientRepository,
                                RestTemplate restTemplate){
        this.jwtUtil = jwtUtil;
        this.clientRepository = clientRepository;
        this.restTemplate=restTemplate;
    }

    @Scheduled(fixedRate = 60 * 1000) //Каждую минуту
    public void unlockClient(){
        String serviceToken = jwtUtil.generateServiceToken();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + serviceToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        List<Long> clientsForUnblocking = clientRepository.getClientsIdsForUnblocking(N);

        if (clientsForUnblocking.size() > 0){
            HttpEntity<List<Long>> request = new HttpEntity<>(
                    clientsForUnblocking, headers);

            ResponseEntity<UnblockedEntity> response = restTemplate.postForEntity(
                    UNBLOCK_CLIENT_URL,
                    request,
                    UnblockedEntity.class);

            logger.info("Полученный ответ от третьего сервиса: {}", response.getBody());

            List<Long> clientsIds = response.getBody().getUnblockedEntityList();
            for (int i = 0; i < clientsIds.size(); ++i){
                Optional<Client> unblockedClient = clientRepository.getClientByThroughId(clientsIds.get(i));

                if (unblockedClient.isPresent()){
                    logger.info("Смена статуса клиента с clientId: {}", clientsIds.get(i));
                    Client client = unblockedClient.get();
                    client.setStatus(ClientStatus.OPEN);

                    clientRepository.save(client);
                    logger.info("Статус клиента изменен.");
                }
            }

        } else{
            logger.info("Нет заблокированных пользователей.");
        }
    }
}

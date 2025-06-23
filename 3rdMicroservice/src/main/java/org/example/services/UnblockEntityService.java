package org.example.services;

import org.example.component.JwtUtil;


import org.example.dto.UnblockedEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;;

@Service
public class UnblockEntityService {
    private Logger logger = LoggerFactory.getLogger(UnblockEntityService.class);
    private static final Random random = new Random();
    private final JwtUtil jwtUtil;

    public UnblockEntityService(JwtUtil jwtUtil){
        this.jwtUtil = jwtUtil;
    }

    public UnblockedEntity unblockEntity(List<Long> idList, String token){
        logger.info("Получено: {}", idList.toString());
        String pureToken = token.replace("Bearer ", "");
        if (!jwtUtil.validateServiceToken(pureToken)) {
            logger.error("Проблема с межсервисной авторизацией");
            throw new RuntimeException("Unauthorized");
        }
        UnblockedEntity unblockedClients = new UnblockedEntity();
        unblockedClients.setUnblockedEntityList(randomUnblockingDecision(idList));
        return unblockedClients;
    }

    private List<Long> randomUnblockingDecision(List<Long> idList){
        List<Long> unblockedClients = new ArrayList<>();
        for (final Long id : idList){
            int randomNum = random.nextInt(100);
            if (randomNum >= 50){  //Вероятность разблокировки 50%
                unblockedClients.add(id);
            }
        }

        return unblockedClients;
    }

}

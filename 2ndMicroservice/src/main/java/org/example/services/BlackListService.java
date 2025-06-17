package org.example.services;

import org.example.component.JwtUtil;
import org.example.dto.BlackListCheck;
import org.example.dto.BlackListCheckResponse;
import org.example.model.BlackList;
import org.example.repositories.BlackListRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
public class BlackListService {
    private final BlackListRepository blackListRepository;
    private final JwtUtil jwtUtil;
    private Logger logger = LoggerFactory.getLogger(BlackListService.class);

    public  BlackListService(BlackListRepository blackListRepository,
                             JwtUtil jwtUtil){
        this.blackListRepository = blackListRepository;
        this.jwtUtil = jwtUtil;
    }

    public BlackListCheckResponse isBlack(BlackListCheck blackListCheck, String token){
        logger.info("Полученный дто: {}", blackListCheck.toString());
        String pureToken = token.replace("Bearer ", "");
        if (!jwtUtil.validateServiceToken(pureToken)) {
            logger.error("Проблема с межсервисной авторизацией");
            throw new RuntimeException("Unauthorized");
        }

        Optional<BlackList> blackList = blackListRepository.getBlackListByAllIds(blackListCheck
                .getClientId(), blackListCheck.getAccountId());

        BlackListCheckResponse blackListCheckResponse = new BlackListCheckResponse("OPEN");
        if (blackList.isPresent()){
            blackListCheckResponse.setStatus("BLOCKED");
        }

        return blackListCheckResponse;
    }
}

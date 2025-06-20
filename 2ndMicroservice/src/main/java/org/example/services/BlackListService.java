package org.example.services;

import org.example.component.JwtUtil;
import org.example.dto.BlackListCheck;
import org.example.dto.BlackListCheckResponse;
import org.example.enums.ClientStatus;
import org.example.model.BlackList;
import org.example.repositories.BlackListRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Random;

@Service
public class BlackListService {
    private final BlackListRepository blackListRepository;
    private final JwtUtil jwtUtil;
    private Logger logger = LoggerFactory.getLogger(BlackListService.class);
    private static final Random random = new Random();

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

        BlackListCheckResponse blackListCheckResponse = new BlackListCheckResponse(ClientStatus.OPEN);
        if (randomDistributionToBlackList(blackListCheck)){
            blackListCheckResponse.setStatus(ClientStatus.BLOCKED);
        }


        return blackListCheckResponse;
    }

    private boolean randomDistributionToBlackList(BlackListCheck blackListCheck){
        int randomNum = random.nextInt(100);
        Optional<BlackList> blackList = blackListRepository
                .getBlackListByAllIds(blackListCheck.getClientId(), blackListCheck.getAccountId());

        if (!blackList.isPresent()){
            //Вероятоность попадания в чс - 10%
            if (randomNum < 10){
                BlackList newBlackList = new BlackList();
                newBlackList.setClientId(blackListCheck.getClientId());
                newBlackList.setAccountId(blackListCheck.getAccountId());

                blackListRepository.save(newBlackList);
                logger.info("{} был рандомно отправлен в бан.", blackList.toString());
                return true;
            } else{
                return false;
            }
        }

        return true;
    }

}

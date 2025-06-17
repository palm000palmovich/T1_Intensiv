package org.example.services;

import org.example.dto.BlackListCheck;
import org.example.dto.BlackListCheckResponse;
import org.springframework.stereotype.Service;

@Service
public class BlackListService {

    public BlackListCheckResponse isBlack(BlackListCheck blackListCheck){
        return new BlackListCheckResponse("Я получил твой запрос: " +  blackListCheck.toString());
    }
}

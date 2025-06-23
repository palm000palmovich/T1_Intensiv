package com.example.T1.services;

import com.example.T1.component.JwtUtil;
import com.example.T1.enums.AccountStatus;
import com.example.T1.model.Account;
import com.example.T1.repository.AccountRepository;
import org.example.dto.UnblockedEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class UnblockAccountService {
    private Logger logger = LoggerFactory.getLogger(UnblockAccountService.class);
    private final JwtUtil jwtUtil;
    private final AccountRepository accountRepository;
    private final RestTemplate restTemplate;
    public static final String UNBLOCK_ACCOUNT_URL = "http://localhost:8082/api/account/unblock";

    @Value("${unblock.account.count}")
    private int M;

    public UnblockAccountService(JwtUtil jwtUtil,
                                 AccountRepository accountRepository,
                                RestTemplate restTemplate){
        this.jwtUtil = jwtUtil;
        this.accountRepository = accountRepository;
        this.restTemplate=restTemplate;
    }

    @Scheduled(fixedRate = 90 * 1000) //Каждые полторы минуты
    public void unlockAccount(){
        String serviceToken = jwtUtil.generateServiceToken();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + serviceToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        List<Long> accountsForUnblocking = accountRepository.getAccountsForUnblocking(M);

        if (accountsForUnblocking.size() > 0){
            HttpEntity<List<Long>> request = new HttpEntity<>(
                    accountsForUnblocking, headers);

            ResponseEntity<UnblockedEntity> response = restTemplate.postForEntity(
                    UNBLOCK_ACCOUNT_URL,
                    request,
                    UnblockedEntity.class);

            logger.info("Полученный ответ от третьего сервиса: {}", response.getBody());

            List<Long> accountIds = response.getBody().getUnblockedEntityList();

            for (int i = 0; i < accountIds.size(); ++i){
                Optional<Account> unblockedAccount = accountRepository.getAccountByThroughId(accountIds.get(i));
                if (unblockedAccount.isPresent()){
                    logger.info("Смена статуса счета с accountId: {}", accountIds.get(i));
                    Account account = unblockedAccount.get();
                    account.setStatus(AccountStatus.OPEN);

                    accountRepository.save(account);
                    logger.info("Статус счета изменен.");
                }
            }

        } else{
            logger.info("Нет заблокированных счетов.");
        }
    }
}


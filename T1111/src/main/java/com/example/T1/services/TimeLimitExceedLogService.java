package com.example.T1.services;

import com.example.T1.model.TimeLimitExceedLog;
import com.example.T1.repository.TimeLimitExceedLogRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TimeLimitExceedLogService {
    private final TimeLimitExceedLogRepository timeLimitExceedLogRepository;
    private Logger logger = LoggerFactory.getLogger(TimeLimitExceedLogService.class);

    @Value("${limit.method.time}")
    private Long limitTime;


    public TimeLimitExceedLogService(TimeLimitExceedLogRepository timeLimitExceedLogRepository){
        this.timeLimitExceedLogRepository = timeLimitExceedLogRepository;
    }

    public void checkSlowMethod(String methodSignature, Long methodTime){
        Long difference = methodTime - limitTime;

        if (difference > 0){
            logger.info("Метод {} медленнeе установленного лимита в {} мс. на {} мс.",
                    methodSignature, limitTime, difference);

            try{
                TimeLimitExceedLog timeLimitExceedLog = new TimeLimitExceedLog();

                timeLimitExceedLog.setMethodSignature(methodSignature);
                timeLimitExceedLog.setDifference(difference);


                logger.info("Медленный метод был сохранен в бд: {}",
                        timeLimitExceedLogRepository.save(timeLimitExceedLog));
            } catch (Exception e){
                logger.error("Проблемы с сохранением медленного метода.");
            }
        }
    }

}
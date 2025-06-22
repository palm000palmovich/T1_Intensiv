package org.example.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.dto.TimeLimitExceedDto;
import org.example.model.TimeLimitExceedLog;
import org.example.repository.TimeLimitExceedLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TimeLimitExceedLogAspect {
    private final TimeLimitExceedLogRepository timeLimitExceedLogRepository;
    private final KafkaTemplate<String, TimeLimitExceedDto> kafkaTemplate;
    private Logger logger = LoggerFactory.getLogger(TimeLimitExceedLogAspect.class);

    @Value("${limit.method.time}")
    private Long limitTime;

    public TimeLimitExceedLogAspect(TimeLimitExceedLogRepository timeLimitExceedLogRepository,
                                    KafkaTemplate<String, TimeLimitExceedDto> kafkaTemplate){
        this.timeLimitExceedLogRepository = timeLimitExceedLogRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Around("@annotation(org.example.annotations.Metric)")
    public Object checkMethodRunningTime(ProceedingJoinPoint joinPoint) throws Throwable{
        logger.info("Аспект сканирует метод {}", joinPoint.getSignature().toShortString());
        Long startTime = System.currentTimeMillis();
        try{
            return joinPoint.proceed();
        } finally{
            Long endTime = System.currentTimeMillis();
            Long duration = endTime - startTime;
            logger.info("Метод {} выполнился за {} мс",
                    joinPoint.getSignature().toShortString(),
                    duration);

            if (duration > limitTime){
                TimeLimitExceedDto timeLimitExceedDto = new TimeLimitExceedDto();
                timeLimitExceedDto.setMethodSignature(joinPoint.getSignature().toShortString());
                timeLimitExceedDto.setDuration(duration);
                timeLimitExceedDto.setErrorType("METRICS");
                try{
                    kafkaTemplate.send("t1_demo_metrics",
                            joinPoint.getSignature().toShortString(),
                            timeLimitExceedDto);
                    logger.info("Сообщение успешно отправлено из аспекта.");
                } catch(RuntimeException ex){
                    logger.error("Kafka не смогла отправить сообщение");
                    checkSlowMethod(joinPoint.getSignature().toShortString(),
                            duration);
                }
            }

            logger.info("Аспект отработал.");
        }
    }

    private void checkSlowMethod(String methodSignature, Long methodTime){
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

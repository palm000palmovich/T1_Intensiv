package com.example.T1.aspects;


import com.example.T1.dto.TimeLimitExceedDto;
import com.example.T1.services.TimeLimitExceedLogService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
@Component
public class TimeLimitExceedLogAspect {
    private final TimeLimitExceedLogService timeLimitExceedLogService;
    private final KafkaTemplate<String, TimeLimitExceedDto> kafkaTemplate;
    private Logger logger = LoggerFactory.getLogger(TimeLimitExceedLogAspect.class);

    @Value("${limit.method.time}")
    private Long limitTime;

    public TimeLimitExceedLogAspect(TimeLimitExceedLogService timeLimitExceedLogService,
                                    KafkaTemplate<String, TimeLimitExceedDto> kafkaTemplate){
        this.timeLimitExceedLogService = timeLimitExceedLogService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Around("@annotation(com.example.T1.annotations.Metric)")
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
                    timeLimitExceedLogService.checkSlowMethod(joinPoint.getSignature().toShortString(),
                            duration);
                }
            }

            logger.info("Аспект отработал.");
        }
    }

}
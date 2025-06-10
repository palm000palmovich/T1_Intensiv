package com.example.T1.aspects;

import com.example.T1.dto.DataErrorDto;
import com.example.T1.services.DataSourseErrorLogService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@Aspect
@Component
public class DataSourceErrorLoggingAspect {
    private final DataSourseErrorLogService dataSourseErrorLogService;
    private KafkaTemplate<String, DataErrorDto> kafkaTemplate;
    private Logger logger = LoggerFactory.getLogger(DataSourceErrorLoggingAspect.class);

    public DataSourceErrorLoggingAspect(DataSourseErrorLogService dataSourseErrorLogService,
                                        KafkaTemplate<String, DataErrorDto> kafkaTemplate){
        this.dataSourseErrorLogService = dataSourseErrorLogService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Around("@annotation(com.example.T1.annotations.LogDataSourceError)")
    public Object logDataSourceError(ProceedingJoinPoint joinPoint) throws Throwable{
        try{
            return joinPoint.proceed();
        } catch (Exception ex){
            logger.error("Аспект перехватил исключение: {}", ex.getMessage());

            DataErrorDto error = new DataErrorDto();
            error.setMethodSignature(joinPoint.getSignature().toShortString());
            error.setExceptionMessage(ex.getMessage());
            error.setLocalDateTime(LocalDateTime.now());
            error.setErrorType("DATA_SOURCE");
            try{
                kafkaTemplate.send("t1_demo_metrics", joinPoint.getSignature().toShortString(), error);
                logger.info("{} успешно обработан и отправлен в топик t1_demo_metrics.", error.toString());
            } catch(Exception exep){
                logger.error("Ошибка отправки сообщения в топик: {}", exep.getMessage());
                dataSourseErrorLogService.saveErrorToBd(ex,
                        joinPoint.getSignature().toShortString());
            }
            logger.info("Аспект отработал.");
            throw ex;
        }
    }

}
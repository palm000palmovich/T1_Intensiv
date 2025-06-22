package org.example.aspects;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.dto.DataErrorDto;
import org.example.model.DataSourceErrorLog;
import org.example.repository.DataSourceErrorLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
public class DataSourceErrorLoggingAspect {
    private final DataSourceErrorLogRepository dataSourceErrorLogRepository;
    private KafkaTemplate<String, DataErrorDto> kafkaTemplate;
    private Logger logger = LoggerFactory.getLogger(DataSourceErrorLoggingAspect.class);

    public DataSourceErrorLoggingAspect(DataSourceErrorLogRepository dataSourceErrorLogRepository,
                                        KafkaTemplate<String, DataErrorDto> kafkaTemplate){
        this.dataSourceErrorLogRepository = dataSourceErrorLogRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Around("@annotation(org.example.annotations.LogDataSourceError)")
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
                saveErrorToBd(ex,
                        joinPoint.getSignature().toShortString());
            }
            logger.info("Аспект отработал.");
            throw ex;
        }
    }

    private void saveErrorToBd(Exception ex, String methodSignature){
        DataSourceErrorLog errorLog = new DataSourceErrorLog();
        errorLog.setStackTrace(ExceptionUtils.getStackTrace(ex));
        errorLog.setMessage(ex.getMessage());
        errorLog.setMethodSignature(methodSignature);

        try{
            logger.info("Ошибка {} была сохранена в бд ",
                    dataSourceErrorLogRepository.save(errorLog));
        } catch(Exception e){
            logger.error("Ошибка в сохранении ошибки метода {} ", methodSignature);
        }
    }

}

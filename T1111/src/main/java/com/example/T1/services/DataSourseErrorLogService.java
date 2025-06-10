package com.example.T1.services;

import com.example.T1.model.DataSourceErrorLog;
import com.example.T1.repository.DataSourceErrorLogRepository;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class DataSourseErrorLogService {
    private Logger logger = LoggerFactory.getLogger(DataSourseErrorLogService.class);
    private final DataSourceErrorLogRepository dataSourceErrorLogRepository;

    public DataSourseErrorLogService(DataSourceErrorLogRepository dataSourceErrorLogRepository){
        this.dataSourceErrorLogRepository = dataSourceErrorLogRepository;
    }

    public void saveErrorToBd(Exception ex, String methodSignature){

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

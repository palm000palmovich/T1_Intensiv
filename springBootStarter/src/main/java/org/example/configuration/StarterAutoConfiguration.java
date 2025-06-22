package org.example.configuration;

import org.example.aspects.DataSourceErrorLoggingAspect;
import org.example.aspects.TimeLimitExceedLogAspect;
import org.example.dto.DataErrorDto;
import org.example.dto.TimeLimitExceedDto;
import org.example.repository.DataSourceErrorLogRepository;
import org.example.repository.TimeLimitExceedLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@Import(KafkaConfig.class)
@EnableJpaRepositories(basePackages = {"org.example.repository", "com.example.T1.repository"})
@EntityScan(basePackages = {"org.example.model", "com.example.T1.model"})
public class StarterAutoConfiguration {
    @Autowired
    private TimeLimitExceedLogRepository timeLimitExceedLogRepository;
    @Autowired
    private KafkaTemplate<String, TimeLimitExceedDto> kafkaTemplate1;
    @Autowired
    private DataSourceErrorLogRepository dataSourceErrorLogRepository;
    @Autowired
    KafkaTemplate<String, DataErrorDto> kafkaTemplate2;


    @Bean
    public TimeLimitExceedLogAspect timeLimitExceedLogAspect(){
        return new TimeLimitExceedLogAspect(timeLimitExceedLogRepository, kafkaTemplate1);
    }

    @Bean
    public DataSourceErrorLoggingAspect dataSourceErrorLoggingAspect(){
        return new DataSourceErrorLoggingAspect(dataSourceErrorLogRepository, kafkaTemplate2);
    }
}

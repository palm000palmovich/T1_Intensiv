package com.example.T1.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class RedisCacheUtils {
    private final RedisTemplate<String, Object> redisTemplate;
    private Logger logger = LoggerFactory.getLogger(RedisCacheUtils.class);

    public RedisCacheUtils(RedisTemplate<String, Object> redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    public void putValue(String key, Object value, long timeoutInSeconds) {
        redisTemplate.opsForValue().set(key, value, timeoutInSeconds, TimeUnit.SECONDS);
    }


    public <T> T getValue(String key, Class<T> type) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value instanceof LinkedHashMap) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
                String json = objectMapper.writeValueAsString(value);
                return objectMapper.readValue(json, type);
            } catch (Exception e) {
                logger.info("Ошибка при десериализации объекта");
            }
        }
        return type.cast(value);
    }

    // Удаление данных из кэша
    public void deleteValue(String key) {
        redisTemplate.delete(key);
    }

    // Проверка наличия ключа в кэше
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}

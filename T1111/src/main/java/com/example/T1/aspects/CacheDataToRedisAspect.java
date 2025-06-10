package com.example.T1.aspects;

import com.example.T1.annotations.Cached;
import com.example.T1.component.RedisCacheUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Aspect
public class CacheDataToRedisAspect {
    private Logger logger = LoggerFactory.getLogger(CacheDataToRedisAspect.class);
    private final RedisCacheUtils cacheUtils;
    private final ExpressionParser parser = new SpelExpressionParser();

    @Value("${spring.cache.redis.time-to-lived}")
    private Long limitTime;

    public CacheDataToRedisAspect(RedisCacheUtils cacheUtils){
        this.cacheUtils = cacheUtils;
    }


    @Around("@annotation(cached)")
    public Object cachedAnnotation(ProceedingJoinPoint joinPoint, Cached cached) throws Throwable{
        logger.info("Аспект перехватил метод для попытки кеширования.");

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Object[] args = joinPoint.getArgs();

        String cacheName = cached.cacheName();
        String keyExpression = cached.key();

        String fullKey = cacheName + "::" + generateKey(signature, args, keyExpression);

        logger.info("Получен объект кеша: {}", fullKey);

        if (cacheUtils.hasKey(fullKey)){
            logger.info("Объект есть в кеше.");
            Class<?> returnType = signature.getReturnType();
            return cacheUtils.getValue(fullKey, returnType);
        }


        try{
            Object result = joinPoint.proceed();
            cacheUtils.putValue(fullKey, result, limitTime);
            logger.info("Объект был кеширован.");

            return result;
        } catch(Exception ex){
            logger.error("Метод выбросил исключение: {}", ex.getMessage());

            throw ex;
        } finally {
            logger.info("Аспект отработал.");
        }


    }

    private String generateKey(MethodSignature signature, Object[] args,
                               String keyExpression){

        StandardEvaluationContext context = new StandardEvaluationContext();
        String[] parameterNames = signature.getParameterNames();

        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        return parser.parseExpression(keyExpression).getValue(context, String.class);
    }

}
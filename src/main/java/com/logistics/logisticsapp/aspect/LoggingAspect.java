package com.logistics.logisticsapp.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingAspect.class);


    @Pointcut("execution(* com.logistics.logisticsapp.service.*.*(..))")
    public void serviceMethods() {
    }


    @Around("serviceMethods()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();

            long time = System.currentTimeMillis() - start;

            if (LOG.isInfoEnabled()) {
                LOG.info("Method {} executed in {} ms",
                    joinPoint.getSignature().toShortString(),
                    time);
            }

            return result;

        } catch (Exception ex) {

            long time = System.currentTimeMillis() - start;

            if (LOG.isErrorEnabled()) {
                LOG.error("ERROR [{}] in {} after {} ms: {}",
                    "INTERNAL_ERROR",
                    joinPoint.getSignature().toShortString(),
                    time,
                    ex.getMessage(),
                    ex);
            }
            throw ex;
        }
    }
}
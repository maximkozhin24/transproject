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

        Object result;
        Throwable error = null;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable ex) {
            error = ex;
            throw ex;
        } finally {
            long time = System.currentTimeMillis() - start;

            if (error == null) {
                if (LOG.isInfoEnabled()) {
                    LOG.info("Method {} executed in {} ms",
                        joinPoint.getSignature().toShortString(),
                        time);
                }
            } else {
                if (LOG.isInfoEnabled()) {
                    LOG.info("Method {} executed in {} ms",
                        joinPoint.getSignature().toShortString(),
                        error);
                }
            }
        }

    }
}
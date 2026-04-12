package com.logistics.logisticsapp.aspect;

import com.logistics.logisticsapp.exception.ServiceExecutionException;
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

            String method = joinPoint.getSignature().toShortString();

            LOG.error("Method {} failed after {} ms with error: {}",
                joinPoint.getSignature().toShortString(),
                time,
                ex.getMessage(),
                ex);

            throw new ServiceExecutionException(
                "Failure in method " + method + " after " + time + " ms",
                ex
            );
        }
    }
}
package com.wine.to.up.am.parser.service.util;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Aspect
@Component
@Slf4j
public class ExecutionTimeTracker {

    private static final String DURATION = " duration= ";
    private static final String SECONDS = " seconds";

    @Around("@annotation(com.wine.to.up.am.parser.service.util.TrackExecutionTime)")
    public Object trackTime(ProceedingJoinPoint pjp) throws Throwable {
        ZoneOffset zone = ZoneOffset.of("Z");
        LocalDateTime startDate = LocalDateTime.now();
        String funcName = pjp.getSignature().getName();
        log.info("start {} method at {}", funcName, startDate);
        Object obj=pjp.proceed();
        LocalDateTime endDate = LocalDateTime.now();
        log.info("end {} method at {}{}{}{}", funcName, endDate, DURATION, (endDate.toEpochSecond(zone) - startDate.toEpochSecond(zone)), SECONDS);
        return obj;
    }

}
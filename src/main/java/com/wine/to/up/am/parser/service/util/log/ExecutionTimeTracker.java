package com.wine.to.up.am.parser.service.util.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

@Aspect
@Component
@Slf4j
public class ExecutionTimeTracker {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss.SSS");

    @Around("@annotation(com.wine.to.up.am.parser.service.util.log.TrackExecutionTime)")
    public Object trackTime(ProceedingJoinPoint point) throws Throwable {

        Method method = ((MethodSignature) point.getSignature()).getMethod();
        String description = method.getAnnotation(TrackExecutionTime.class).description();
        if (description.equals("")) {
            description = method.getName();
        }

        long start = System.currentTimeMillis();
        log.info("Starting {} at {}", description, formatter.format(new Date(start)));

        Object result = point.proceed();

        long end = System.currentTimeMillis();
        log.info("Finished {} at {} in {}s.", description, formatter.format(new Date(end)), (end - start) / 100);

        return result;
    }

}
package com.nhn.demo.aspect;

import java.util.HashMap;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Component
@Aspect
@Slf4j
public class LogAspect {
    @Before("@annotation(com.nhn.demo.annotation.Logging)")
    public void before(final JoinPoint joinPoint) {
        final MethodSignature         signature      = (MethodSignature) joinPoint.getSignature();
        final String[]                parameterNames = signature.getParameterNames();
        final Object[]                args           = joinPoint.getArgs();
        final HashMap<String, Object> paramMap       = new HashMap<>();
        for (int i = 0; i < parameterNames.length; i++) {
            paramMap.put(parameterNames[i], args[i]);
        }
        final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        final String             method  = request.getMethod();
        final String             url     = request.getRequestURI();
        log.info("[{}]: {}, Request: {}", method, url, paramMap);
    }

    @AfterReturning(value = "@annotation(com.nhn.demo.annotation.Logging)", returning = "res")
    public void afterControllers(final Object res) {
        log.info("Response：{}", res);
    }
}

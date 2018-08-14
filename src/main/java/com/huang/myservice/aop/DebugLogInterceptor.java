package com.huang.myservice.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class DebugLogInterceptor {
	private static final Logger LOGGER = LoggerFactory.getLogger(DebugLogInterceptor.class);

	@Before("execution(* com.huang.myservice.controller.DataController.*(..))")
	public void logBefore(JoinPoint joinPoint) {
		String className = joinPoint.getTarget().getClass().getName();
		String methodName = joinPoint.getSignature().getName();
		Object[] args = joinPoint.getArgs();
		LOGGER.info("aop before class : {} method : {} args : {}", className, methodName, args);
	}

	@After("execution(* com.huang.myservice.controller.DataController.*(..))")
	public void logAfter(JoinPoint joinPoint) {
		String className = joinPoint.getTarget().getClass().getName();
		String methodName = joinPoint.getSignature().getName();
		LOGGER.info("aop after  class : {} method : {}", className, methodName);
	}

	@Around("execution(* com.huang.myservice.controller.DataController.*(..))")
	public void logAround(ProceedingJoinPoint joinPoint) throws Throwable {
		String className = joinPoint.getTarget().getClass().getName();
		String methodName = joinPoint.getSignature().getName();
		Object[] args = joinPoint.getArgs();
		LOGGER.info("aop around before class : {} method : {} args : {}", className, methodName, args);
		joinPoint.proceed();
		LOGGER.info("aop around after class : {} method : {} ", className, methodName);
	}
}

package com.unsc.shard.common.aop.impl;

import com.unsc.shard.bean.User;
import com.unsc.shard.common.aop.annotation.Visit;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.net.URI;

/**
 *
 * @author DELL
 * @date 2018/12/20
 */
@Aspect
@Component
@Slf4j
@Order(2)
public class VisitAspect {

    @Pointcut(value = "@annotation(com.unsc.shard.common.aop.annotation.Visit)")
    private void execute() {

    }

    /*@Pointcut(value = "@within(org.springframework.stereotype.Controller) || @within(org.springframework.web.bind.annotation.RestController)")
    private void interceptor() {

    }*/

    @Before("execute()")
    public void doBefore(JoinPoint point) {
        String className = point.getTarget().getClass().getSimpleName();
        log.info("访问了 controller: {}", className);
    }

    @Around(value = "execute()")
    public Object toExecute(ProceedingJoinPoint joinPoint) throws Throwable {
        //从ProceedingJoinPoint中获取到对应的MethodSignature后 调用获取注解的 得到注解对象
        Signature signature = joinPoint.getSignature();
        MethodSignature ms = (MethodSignature) signature;
        Visit omg = ms.getMethod().getAnnotation(Visit.class);
        Object[] args = joinPoint.getArgs();
        //获取到注解中定义的方法的入参
        String value = omg.method();

        log.info("访问了 controller中的 {} 方法", ms.getMethod().getName() + " value: " +value);
        return joinPoint.proceed(args);
    }

    /*@Around(value = "interceptor()")
    public Object handle(ProceedingJoinPoint joinPoint) throws Throwable {

        Object proceed = joinPoint.proceed();
  *//*      RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        ServerRequest request = (ServerRequest) attributes;*//*
        Object request = joinPoint.getArgs()[0];
        URI uri = request.uri();
        String path = request.path();
        String requireMethod = request.methodName();
        log.info("URI: {} , Path: {}, RequireMethod: {}", uri, path, requireMethod);
        return proceed;
    }*/
}

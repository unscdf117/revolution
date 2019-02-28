package com.unsc.shard.common.aop.impl;

import com.unsc.shard.common.aop.annotation.RevolutionLock;
import com.unsc.shard.common.exts.DistributedLockCallback;
import com.unsc.shard.common.exts.DistributedLockTemplate;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 革命锁 自定义注解实现
 * @author DELL
 * @date 2018/1/2
 */
@Component
@Aspect
public class RevolutionLockImpl {

    @Resource
    private DistributedLockTemplate template;

    @Pointcut("@annotation(com.unsc.shard.common.aop.annotation.RevolutionLock)")
    public void aim() {}

    @Around("aim()")
    public Object execute(ProceedingJoinPoint pjp) throws Throwable {
        //切点所在的类
        Class targetClass = pjp.getTarget().getClass();
        //使用了注解的方法
        String methodName = pjp.getSignature().getName();
        Class[] parameterTypes = ((MethodSignature)pjp.getSignature()).getMethod().getParameterTypes();
        Method method = targetClass.getMethod(methodName, parameterTypes);
        Object[] arguments = pjp.getArgs();
        final String lockName = getLockName(method, arguments);
        return lock(pjp, method, lockName);
    }

    @AfterThrowing(value = "aim()", throwing="ex")
    public void afterThrowing(Throwable ex) {
        throw new RuntimeException(ex);
    }

    public String getLockName(Method method, Object[] args) {
        Objects.requireNonNull(method);
        RevolutionLock annotation = method.getAnnotation(RevolutionLock.class);
        var lockName = annotation.name();
        var param = annotation.param();

        if (isEmpty(lockName)) {
            if (args.length > 0) {
                //有参
                if (isNotEmpty(param)) {
                    Object arg;
                    if (annotation.argNum() > 0) {
                        arg = args[annotation.argNum() - 1];
                    } else {
                        arg = args[0];
                    }
                    lockName = getParam(arg, param).toString();
                } else if (annotation.argNum() > 0) {
                    //无参
                    lockName = args[annotation.argNum() - 1].toString();
                }
            }
        }

        if (isNotEmpty(lockName)) {
            var preLockName = annotation.preffix();
            var postLockName = annotation.suffix();
            var separator = annotation.separator();
            StringBuilder lName = new StringBuilder();
            if (isNotEmpty(preLockName)) {
                lName.append(preLockName).append(separator);
            }
            lName.append(lockName);
            if (isNotEmpty(postLockName)) {
                lName.append(separator).append(postLockName);
            }
            lockName = lName.toString();
            return lockName;
        }
        throw new IllegalArgumentException("生成 or 获取锁失败...");
    }

    /**
     * 从方法参数获取数据
     * @param param 参数列表
     * @param arg 方法的参数数组
     * @return
     */
    public Object getParam(Object arg, String param) {
        if (isNotEmpty(param) && arg != null) {
            try {
                PropertyUtils propertyUtils = PropertyUtils.class.getConstructor().newInstance();
                return propertyUtils.getProperty((Class) arg, param);
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException(arg + "没有属性" + param + "或未实现get方法。", e);
            } catch (Exception e) {
                throw new RuntimeException("", e);
            }
        }
        return null;
    }

    public Object lock(ProceedingJoinPoint pjp, Method method, final String lockName) {

        RevolutionLock annotation = method.getAnnotation(RevolutionLock.class);
        boolean fairLock = annotation.fairLock();
        boolean tryLock = annotation.tryLock();

        if (tryLock) {
            return tryLock(pjp, annotation, lockName, fairLock);
        } else {
            return lock(pjp,lockName, fairLock);
        }
    }

    public Object lock(ProceedingJoinPoint pjp, final String lockName, boolean fairLock) {
        return template.lock(new DistributedLockCallback<Object>() {
            @Override
            public Object process() {
                return proceed(pjp);
            }

            @Override
            public String getLockName() {
                return lockName;
            }
        }, fairLock);
    }

    public Object tryLock(ProceedingJoinPoint pjp, RevolutionLock annotation, final String lockName, boolean fairLock) {

        long waitTime = annotation.waitTime(),
                leaseTime = annotation.exipreTime();
        TimeUnit timeUnit = annotation.timeUnit();

        return template.tryLock(new DistributedLockCallback<Object>() {
            @Override
            public Object process() {
                return proceed(pjp);
            }

            @Override
            public String getLockName() {
                return lockName;
            }
        }, waitTime, leaseTime, timeUnit, fairLock);
    }

    public Object proceed(ProceedingJoinPoint pjp) {
        try {
            return pjp.proceed();
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    private boolean isEmpty(Object str) {
        return str == null || "".equals(str);
    }

    private boolean isNotEmpty(Object str) {
        return !isEmpty(str);
    }
}

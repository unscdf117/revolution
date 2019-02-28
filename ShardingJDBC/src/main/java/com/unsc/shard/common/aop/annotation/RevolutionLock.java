package com.unsc.shard.common.aop.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 革命 锁 注解
 * @author DELL
 * @date 2018/1/2
 */
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RevolutionLock {

    /**
     * 锁的名称。
     * 如果lockName可以确定，直接设置该属性。
     */
    String name() default "revolution";

    /**
     * lock 名称前缀
     */
    String preffix() default "lock_";
    /**
     * lock 名称后缀
     */
    String suffix() default "_lock";

    /**
     * 获得锁名拼接前后缀的分隔符 ^ 就是那个异或
     * @return
     */
    String separator() default "^";
    /**
     * 获取注解的方法参数列表的某个参数对象的某个属性值来作为锁名(因为有时候lockName是不固定的)
     * 当param不为空时 可以通过argNum参数来设置具体是参数列表的第几个参数 不设置则默认取第一个。
     */
    String param() default "";
    /**
     * 将方法第argNum个参数作为锁
     */
    int argNum() default 0;
    /**
     * 是否使用公平锁
     */
    boolean fairLock() default false;
    /**
     * 是否使用尝试锁。
     */
    boolean tryLock() default false;
    /**
     * 最长等待时间 该字段只有当tryLock()返回true才有效 默认30秒
     */
    long waitTime() default 30L;
    /**
     * 锁超时时间 到达超时时间后释放 默认5秒
     */
    long exipreTime() default 5L;
    /**
     * 时间单位 默认为秒
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

}

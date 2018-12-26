package com.unsc.shard.common.aop.annotation;

import java.lang.annotation.*;

/**
 * Created by DELL on 2018/12/20.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Visit {

    String method() default "";

}

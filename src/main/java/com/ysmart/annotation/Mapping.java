package com.ysmart.annotation;

import java.lang.annotation.*;


/**
 * Mapping  持久层注解
 * @author 于成航 ychhh
 * @data 2020.8.15
 * @email 627387735@qq.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Mapping {

    String value() default "";

}

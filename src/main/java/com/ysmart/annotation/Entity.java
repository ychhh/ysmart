package com.ysmart.annotation;

import com.ysmart.enums.BeanScopeType;

import java.lang.annotation.*;
/**
 * Entity 实体类注解
 * @author 于成航 ychhh
 * @data 2020.8.15
 * @email 627387735@qq.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Entity {
    BeanScopeType BeanScope() default BeanScopeType.Singleton;
    String value() default "";
}

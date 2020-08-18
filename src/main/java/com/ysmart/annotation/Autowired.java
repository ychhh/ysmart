package com.ysmart.annotation;

import java.lang.annotation.*;

/**
 * Autowired  对象注入注解
 * @author 于成航 ychhh
 * @data 2020.8.15
 * @email 627387735@qq.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Autowired {

    String value() default "";

}

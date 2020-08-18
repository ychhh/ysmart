package com.ysmart.annotation;

import java.lang.annotation.*;

/**
 * Value 注入属性值
 * @author 于成航 ychhh
 * @data 2020.8.15
 * @email 627387735@qq.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Value {

    String value();

}

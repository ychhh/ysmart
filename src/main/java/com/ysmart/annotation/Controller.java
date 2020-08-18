package com.ysmart.annotation;

import java.lang.annotation.*;

/**
 * Entity 控制器注解
 * @author 于成航 ychhh
 * @data 2020.8.15
 * @email 627387735@qq.com
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Controller {

}

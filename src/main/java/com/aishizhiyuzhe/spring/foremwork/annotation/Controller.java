package com.aishizhiyuzhe.spring.foremwork.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Controller {
    String value() default "";
}

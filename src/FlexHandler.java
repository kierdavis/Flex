package com.kierdavis.flex;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface FlexHandler {
    String value(); // Path
    String permission() default null;
    boolean playerOnly() default false;
    String[] argNames() default null;
}

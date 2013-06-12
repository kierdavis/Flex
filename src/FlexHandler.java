package com.kierdavis.flex;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface FlexHandler {
    String path();
}

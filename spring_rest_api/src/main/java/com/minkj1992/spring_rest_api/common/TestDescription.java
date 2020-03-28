package com.minkj1992.spring_rest_api.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // method에 대해 검증
@Retention(RetentionPolicy.SOURCE)    //보유,유지: life cycle how long
public @interface TestDescription {

    String value();
}

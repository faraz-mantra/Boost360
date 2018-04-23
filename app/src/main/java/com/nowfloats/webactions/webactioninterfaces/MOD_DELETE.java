package com.nowfloats.webactions.webactioninterfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import retrofit.http.RestMethod;

/**
 * Created by NowFloats on 17-04-2018.
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@RestMethod(value = "DELETE", hasBody = true)
public @interface MOD_DELETE {
    String value();
}
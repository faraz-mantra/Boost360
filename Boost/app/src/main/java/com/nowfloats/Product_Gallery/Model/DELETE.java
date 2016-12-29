package com.nowfloats.Product_Gallery.Model;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import retrofit.http.RestMethod;


import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
/**
 * Created by NowFloats on 07-09-2016.
 */
@Target(METHOD)
@Retention(RUNTIME)
@RestMethod(value = "DELETE", hasBody = true)
public @interface DELETE {
    String value();
}

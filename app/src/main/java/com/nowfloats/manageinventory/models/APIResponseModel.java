package com.nowfloats.manageinventory.models;

/**
 * Created by NowFloats on 17-08-2017.
 */

public class APIResponseModel<T> {
    public T Result;
    public ErrorModel Error;
    public int StatusCode;
}

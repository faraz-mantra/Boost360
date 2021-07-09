package com.nowfloats.webactions.models;

/**
 * Created by NowFloats on 12-04-2018.
 */

public class WebActionUpdateValue<T> {
    private  T value;

    public WebActionUpdateValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}

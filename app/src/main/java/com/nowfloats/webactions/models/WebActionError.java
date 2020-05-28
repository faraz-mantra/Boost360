package com.nowfloats.webactions.models;

/**
 * Created by NowFloats on 09-04-2018.
 */

public class WebActionError {
    private String message;

    public WebActionError(String message) {
        this.message = message;
    }

    public String getErrorMessage() {
        return this.message;
    }

}

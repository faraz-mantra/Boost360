package com.nowfloats.Store.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by NowFloats on 05-12-2016.
 */

public class PaymentTokenResult {
    @SerializedName("Error")
    @Expose
    private Error error;
    @SerializedName("Result")
    @Expose
    private PaymentTokenModel result;
    @SerializedName("StatusCode")
    @Expose
    private Integer statusCode;

    /**
     *
     * @return
     * The error
     */
    public Error getError() {
        return error;
    }

    /**
     *
     * @param error
     * The Error
     */
    public void setError(Error error) {
        this.error = error;
    }

    /**
     *
     * @return
     * The result
     */
    public PaymentTokenModel getResult() {
        return result;
    }

    /**
     *
     * @param result
     * The Result
     */
    public void setResult(PaymentTokenModel result) {
        this.result = result;
    }

    /**
     *
     * @return
     * The statusCode
     */
    public Integer getStatusCode() {
        return statusCode;
    }

    /**
     *
     * @param statusCode
     * The StatusCode
     */
    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }
}

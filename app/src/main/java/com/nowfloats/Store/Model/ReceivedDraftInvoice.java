package com.nowfloats.Store.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReceivedDraftInvoice {

    @SerializedName("Error")
    @Expose
    private Error error;
    @SerializedName("Result")
    @Expose
    private ReceiveDraftInvoiceModel result;
    @SerializedName("StatusCode")
    @Expose
    private Integer statusCode;

    /**
     * @return The error
     */
    public Error getError() {
        return error;
    }

    /**
     * @param error The Error
     */
    public void setError(Error error) {
        this.error = error;
    }

    /**
     * @return The result
     */
    public ReceiveDraftInvoiceModel getResult() {
        return result;
    }

    /**
     * @param result The Result
     */
    public void setResult(ReceiveDraftInvoiceModel result) {
        this.result = result;
    }

    /**
     * @return The statusCode
     */
    public Integer getStatusCode() {
        return statusCode;
    }

    /**
     * @param statusCode The StatusCode
     */
    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

}
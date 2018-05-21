package com.nowfloats.util;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Admin on 16-05-2017.
 */

public class VerifyPhoneNumberAndSendOTP {
    private String PHONE;
    private boolean IsPhoneNumberInUse;
    private boolean IsOTPSent;

    @SerializedName("error_code")
    @Expose
    private String errorCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("errors")
    @Expose
    private Errors errors;
    @SerializedName("success")
    @Expose
    private Boolean success;

    public String getPHONE() {
        return PHONE;
    }

    public boolean isPhoneNumberInUse() {
        return IsPhoneNumberInUse;
    }

    public boolean isOTPSent() {
        return IsOTPSent;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Errors getErrors() {
        return errors;
    }

    public void setErrors(Errors errors) {
        this.errors = errors;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public class Errors {

        @SerializedName("message")
        @Expose
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }
}

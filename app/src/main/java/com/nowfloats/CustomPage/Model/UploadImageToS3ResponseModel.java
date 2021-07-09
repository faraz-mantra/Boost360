package com.nowfloats.CustomPage.Model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UploadImageToS3ResponseModel {

    @SerializedName("statusCode")
    @Expose
    private Integer statusCode;

    @Expose
    private Body body;
    @SerializedName("isBase64Encoded")
    @Expose
    private Boolean isBase64Encoded;

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public Boolean getIsBase64Encoded() {
        return isBase64Encoded;
    }

    public void setIsBase64Encoded(Boolean isBase64Encoded) {
        this.isBase64Encoded = isBase64Encoded;
    }

    public class Body {

        @SerializedName("result")
        @Expose
        private String result;

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

    }

    }
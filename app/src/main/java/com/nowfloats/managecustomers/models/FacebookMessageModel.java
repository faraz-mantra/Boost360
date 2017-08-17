package com.nowfloats.managecustomers.models;

/**
 * Created by Admin on 17-08-2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FacebookMessageModel {

    @SerializedName("identifier")
    @Expose
    private String identifier;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("nowfloats_id")
    @Expose
    private String nowfloatsId;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("content")
    @Expose
    private Content content;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNowfloatsId() {
        return nowfloatsId;
    }

    public void setNowfloatsId(String nowfloatsId) {
        this.nowfloatsId = nowfloatsId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public class Content {

        @SerializedName("text")
        @Expose
        private String text;
        @SerializedName("url")
        @Expose
        private String url;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

    }
}



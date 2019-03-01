package com.nowfloats.Analytics_Screen.model;

import com.google.gson.annotations.SerializedName;

public class AnalyticsResponse
{

    @SerializedName("Keyword")
    private String keyword;


    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}

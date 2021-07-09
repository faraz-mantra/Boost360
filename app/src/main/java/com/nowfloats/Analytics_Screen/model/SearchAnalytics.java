package com.nowfloats.Analytics_Screen.model;

import com.google.gson.annotations.SerializedName;

public class SearchAnalytics
{
    @SerializedName("AveragePosition")
    private int averagePosition;

    @SerializedName("Impressions")
    private int impressions;

    @SerializedName("Clicks")
    private int clicks;

    @SerializedName("CTR")
    private double ctr;

    @SerializedName("Keyword")
    private String keyword;

    @SerializedName("Date")
    private String date;


    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getAveragePosition() {
        return averagePosition;
    }

    public void setAveragePosition(int averagePosition) {
        this.averagePosition = averagePosition;
    }

    public int getImpressions() {
        return impressions;
    }

    public void setImpressions(int impressions) {
        this.impressions = impressions;
    }

    public int getClicks() {
        return clicks;
    }

    public void setClicks(int clicks) {
        this.clicks = clicks;
    }

    public double getCtr() {
        return ctr;
    }

    public void setCtr(double ctr) {
        this.ctr = ctr;
    }
}
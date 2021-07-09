package com.nowfloats.NavigationDrawer.model;

/**
 * Created by Admin on 30-11-2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WildFireKeyStatsModel{

    @SerializedName("AvgCPC")
    @Expose
    private String avgCPC;
    @SerializedName("Cost")
    @Expose
    private String cost;
    @SerializedName("Ctr")
    @Expose
    private String ctr;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("avgPosition")
    @Expose
    private String avgPosition;
    @SerializedName("clicks")
    @Expose
    private String clicks;
    @SerializedName("impressions")
    @Expose
    private String impressions;
    @SerializedName("keyword")
    @Expose
    private String keyword;
    @SerializedName("keywordState")
    @Expose
    private String keywordState;
    @SerializedName("adGroupId")
    @Expose
    private String adGroupId;
    @SerializedName("adGroupName")
    @Expose
    private String adGroupName;
    @SerializedName("campaignName")
    @Expose
    private String campaignName;
    @SerializedName("conversions")
    @Expose
    private String conversions;
    @SerializedName("firstPageCpc")
    @Expose
    private String firstPageCpc;
    @SerializedName("isNegative")
    @Expose
    private Object isNegative;
    @SerializedName("maxCPC")
    @Expose
    private String maxCPC;
    @SerializedName("topPageCPC")
    @Expose
    private String topPageCPC;

    public String getAvgCPC() {
        return avgCPC;
    }

    public void setAvgCPC(String avgCPC) {
        this.avgCPC = avgCPC;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getCtr() {
        return ctr;
    }

    public void setCtr(String ctr) {
        this.ctr = ctr;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvgPosition() {
        return avgPosition;
    }

    public void setAvgPosition(String avgPosition) {
        this.avgPosition = avgPosition;
    }

    public String getClicks() {
        return clicks;
    }

    public void setClicks(String clicks) {
        this.clicks = clicks;
    }

    public String getImpressions() {
        return impressions;
    }

    public void setImpressions(String impressions) {
        this.impressions = impressions;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getKeywordState() {
        return keywordState;
    }

    public void setKeywordState(String keywordState) {
        this.keywordState = keywordState;
    }

    public String getAdGroupId() {
        return adGroupId;
    }

    public void setAdGroupId(String adGroupId) {
        this.adGroupId = adGroupId;
    }

    public String getAdGroupName() {
        return adGroupName;
    }

    public void setAdGroupName(String adGroupName) {
        this.adGroupName = adGroupName;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public String getConversions() {
        return conversions;
    }

    public void setConversions(String conversions) {
        this.conversions = conversions;
    }

    public String getFirstPageCpc() {
        return firstPageCpc;
    }

    public void setFirstPageCpc(String firstPageCpc) {
        this.firstPageCpc = firstPageCpc;
    }

    public Object getIsNegative() {
        return isNegative;
    }

    public void setIsNegative(Object isNegative) {
        this.isNegative = isNegative;
    }

    public String getMaxCPC() {
        return maxCPC;
    }

    public void setMaxCPC(String maxCPC) {
        this.maxCPC = maxCPC;
    }

    public String getTopPageCPC() {
        return topPageCPC;
    }

    public void setTopPageCPC(String topPageCPC) {
        this.topPageCPC = topPageCPC;
    }

}



package com.nowfloats.NavigationDrawer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Admin on 21-12-2017.
 */


public class FacebookWildFireDataModel {

    @SerializedName("clicks")
    @Expose
    private String clicks;
    @SerializedName("cpc")
    @Expose
    private String cpc;
    @SerializedName("cpm")
    @Expose
    private String cpm;
    @SerializedName("ctr")
    @Expose
    private String ctr;
    @SerializedName("impressions")
    @Expose
    private String impressions;
    @SerializedName("reach")
    @Expose
    private String reach;
    @SerializedName("ad_id")
    @Expose
    private String adId;
    @SerializedName("ad_name")
    @Expose
    private String adName;
    @SerializedName("id")
    @Expose
    private Object id;
    @SerializedName("preview_ad")
    @Expose
    private String previewAd;
    @SerializedName("status")
    @Expose
    private Object status;

    public String getClicks() {
        return clicks;
    }

    public void setClicks(String clicks) {
        this.clicks = clicks;
    }

    public String getCpc() {
        return cpc;
    }

    public void setCpc(String cpc) {
        this.cpc = cpc;
    }

    public String getCpm() {
        return cpm;
    }

    public void setCpm(String cpm) {
        this.cpm = cpm;
    }

    public String getCtr() {
        return ctr;
    }

    public void setCtr(String ctr) {
        this.ctr = ctr;
    }

    public String getImpressions() {
        return impressions;
    }

    public void setImpressions(String impressions) {
        this.impressions = impressions;
    }

    public String getReach() {
        return reach;
    }

    public void setReach(String reach) {
        this.reach = reach;
    }

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public String getAdName() {
        return adName;
    }

    public void setAdName(String adName) {
        this.adName = adName;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public String getPreviewAd() {
        return previewAd;
    }

    public void setPreviewAd(String previewAd) {
        this.previewAd = previewAd;
    }

    public Object getStatus() {
        return status;
    }

    public void setStatus(Object status) {
        this.status = status;
    }

}


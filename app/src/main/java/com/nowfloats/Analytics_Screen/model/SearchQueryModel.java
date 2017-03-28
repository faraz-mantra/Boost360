package com.nowfloats.Analytics_Screen.model;

/**
 * Created by RAJA on 20-06-2016.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchQueryModel {

    @SerializedName("SearchRank")
    @Expose
    private Integer searchRank;
    @SerializedName("createdOn")
    @Expose
    private String createdOn;
    @SerializedName("fptag")
    @Expose
    private String fptag;
    @SerializedName("ip")
    @Expose
    private String ip;
    @SerializedName("keyword")
    @Expose
    private String keyword;

    public Integer getSearchRank() {
        return searchRank;
    }

    public void setSearchRank(Integer searchRank) {
        this.searchRank = searchRank;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getFptag() {
        return fptag;
    }

    public void setFptag(String fptag) {
        this.fptag = fptag;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

}
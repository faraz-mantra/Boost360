package com.nowfloats.manufacturing.API.model.GetTeams;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FbURL {

    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("description")
    @Expose
    private String description;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
package com.nowfloats.CustomPage.Model;

import com.google.gson.annotations.SerializedName;

public class Url {

    @SerializedName("description")
    private Object description;

    @SerializedName("updatedon")
    private String updatedon;

    @SerializedName("createdon")
    private String createdon;

    @SerializedName("url")
    private String url;

    public Object getDescription() {
        return description;
    }

    public void setDescription(Object description) {
        this.description = description;
    }

    public String getUpdatedon() {
        return updatedon;
    }

    public void setUpdatedon(String updatedon) {
        this.updatedon = updatedon;
    }

    public String getCreatedon() {
        return createdon;
    }

    public void setCreatedon(String createdon) {
        this.createdon = createdon;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
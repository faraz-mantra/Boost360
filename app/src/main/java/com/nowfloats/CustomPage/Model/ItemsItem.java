package com.nowfloats.CustomPage.Model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ItemsItem {

    @SerializedName("keywords")
    private List<Object> keywords;

    @SerializedName("name")
    private String name;

    @SerializedName("html")
    private String html;

    @SerializedName("id")
    private String id;

    @SerializedName("updatedon")
    private String updatedon;

    @SerializedName("createdon")
    private String createdon;

    @SerializedName("url")
    private Url url;

    public List<Object> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<Object> keywords) {
        this.keywords = keywords;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Url getUrl() {
        return url;
    }

    public void setUrl(Url url) {
        this.url = url;
    }
}
package com.nowfloats.NavigationDrawer.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Section {

    @SerializedName("Text")
    @Expose
    private String text;
    @SerializedName("Id")
    @Expose
    private String id;
    @SerializedName("SectionType")
    @Expose
    private String sectionType;
    @SerializedName("DelayInMs")
    @Expose
    private Integer delayInMs;
    @SerializedName("Hidden")
    @Expose
    private Boolean hidden;
    @SerializedName("CoordinatesSet")
    @Expose
    private List<CoordinatesSet> coordinatesSet = null;
    @SerializedName("X")
    @Expose
    private X x;
    @SerializedName("Y")
    @Expose
    private Y y;
    @SerializedName("Caption")
    @Expose
    private String caption;
    @SerializedName("GraphType")
    @Expose
    private String graphType;
    @SerializedName("Url")
    @Expose
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSectionType() {
        return sectionType;
    }

    public void setSectionType(String sectionType) {
        this.sectionType = sectionType;
    }

    public Integer getDelayInMs() {
        return delayInMs;
    }

    public void setDelayInMs(Integer delayInMs) {
        this.delayInMs = delayInMs;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public List<CoordinatesSet> getCoordinatesSet() {
        return coordinatesSet;
    }

    public void setCoordinatesSet(List<CoordinatesSet> coordinatesSet) {
        this.coordinatesSet = coordinatesSet;
    }

    public X getX() {
        return x;
    }

    public void setX(X x) {
        this.x = x;
    }

    public Y getY() {
        return y;
    }

    public void setY(Y y) {
        this.y = y;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getGraphType() {
        return graphType;
    }

    public void setGraphType(String graphType) {
        this.graphType = graphType;
    }

}
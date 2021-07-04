package com.nowfloats.Image_Gallery.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PurchaseWidgets {

    @SerializedName("featureKey")
    @Expose
    private String featureKey;
    @SerializedName("featureType")
    @Expose
    private Integer featureType;
    @SerializedName("featureState")
    @Expose
    private Integer featureState;
    @SerializedName("properties")
    @Expose
    private List<Property> properties = null;
    @SerializedName("createdDate")
    @Expose
    private String createdDate;
    @SerializedName("activatedDate")
    @Expose
    private String activatedDate;
    @SerializedName("expiryDate")
    @Expose
    private String expiryDate;

    public String getFeatureKey() {
        return featureKey;
    }

    public void setFeatureKey(String featureKey) {
        this.featureKey = featureKey;
    }

    public Integer getFeatureType() {
        return featureType;
    }

    public void setFeatureType(Integer featureType) {
        this.featureType = featureType;
    }

    public Integer getFeatureState() {
        return featureState;
    }

    public void setFeatureState(Integer featureState) {
        this.featureState = featureState;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getActivatedDate() {
        return activatedDate;
    }

    public void setActivatedDate(String activatedDate) {
        this.activatedDate = activatedDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

}

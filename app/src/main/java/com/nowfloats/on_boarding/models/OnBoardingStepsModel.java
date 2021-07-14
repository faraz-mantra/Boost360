package com.nowfloats.on_boarding.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Admin on 22-03-2018.
 */

public class OnBoardingStepsModel {
    @SerializedName("fptag")
    @Expose
    private String fptag;
    @SerializedName("welcome_aboard")
    @Expose
    private boolean welcomeAboard;
    @SerializedName("is_complete")
    @Expose
    private boolean isComplete;
    @SerializedName("site_health")
    @Expose
    private int siteHealth;
    @SerializedName("custom_page")
    @Expose
    private boolean customPage;
    @SerializedName("add_product")
    @Expose
    private boolean addProduct;
    @SerializedName("boost_app")
    @Expose
    private boolean boostApp;
    @SerializedName("share_website")
    @Expose
    private boolean shareWebsite;

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public String getFptag() {
        return fptag;
    }

    public void setFptag(String fptag) {
        this.fptag = fptag;
    }

    public Boolean getWelcomeAboard() {
        return welcomeAboard;
    }

    public void setWelcomeAboard(Boolean welcomeAboard) {
        this.welcomeAboard = welcomeAboard;
    }

    public Integer getSiteHealth() {
        return siteHealth;
    }

    public void setSiteHealth(int siteHealth) {
        this.siteHealth = siteHealth;
    }

    public Boolean getCustomPage() {
        return customPage;
    }

    public void setCustomPage(Boolean customPage) {
        this.customPage = customPage;
    }

    public Boolean getAddProduct() {
        return addProduct;
    }

    public void setAddProduct(Boolean addProduct) {
        this.addProduct = addProduct;
    }

    public Boolean getBoostApp() {
        return boostApp;
    }

    public void setBoostApp(Boolean boostApp) {
        this.boostApp = boostApp;
    }

    public Boolean getShareWebsite() {
        return shareWebsite;
    }

    public void setShareWebsite(Boolean shareWebsite) {
        this.shareWebsite = shareWebsite;
    }
}

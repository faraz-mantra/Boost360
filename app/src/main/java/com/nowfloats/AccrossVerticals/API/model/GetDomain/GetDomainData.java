package com.nowfloats.AccrossVerticals.API.model.GetDomain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetDomainData {

    @SerializedName("ActivatedOn")
    @Expose
    private String activatedOn;
    @SerializedName("ErrorMessage")
    @Expose
    private String errorMessage;
    @SerializedName("ExpiresOn")
    @Expose
    private String expiresOn;
    @SerializedName("NameServers")
    @Expose
    private String nameServers;
    @SerializedName("domainName")
    @Expose
    private String domainName;
    @SerializedName("domainType")
    @Expose
    private String domainType;
    @SerializedName("fpTag")
    @Expose
    private String fpTag;
    @SerializedName("hasDomain")
    @Expose
    private Boolean hasDomain;
    @SerializedName("isActive")
    @Expose
    private Boolean isActive;
    @SerializedName("isExpired")
    @Expose
    private Boolean isExpired;
    @SerializedName("isFailed")
    @Expose
    private Boolean isFailed;
    @SerializedName("isLinked")
    @Expose
    private Boolean isLinked;
    @SerializedName("isPending")
    @Expose
    private Boolean isPending;

    public String getActivatedOn() {
        return activatedOn;
    }

    public void setActivatedOn(String activatedOn) {
        this.activatedOn = activatedOn;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getExpiresOn() {
        return expiresOn;
    }

    public void setExpiresOn(String expiresOn) {
        this.expiresOn = expiresOn;
    }

    public String getNameServers() {
        return nameServers;
    }

    public void setNameServers(String nameServers) {
        this.nameServers = nameServers;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getDomainType() {
        return domainType;
    }

    public void setDomainType(String domainType) {
        this.domainType = domainType;
    }

    public String getFpTag() {
        return fpTag;
    }

    public void setFpTag(String fpTag) {
        this.fpTag = fpTag;
    }

    public Boolean getHasDomain() {
        return hasDomain;
    }

    public void setHasDomain(Boolean hasDomain) {
        this.hasDomain = hasDomain;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsExpired() {
        return isExpired;
    }

    public void setIsExpired(Boolean isExpired) {
        this.isExpired = isExpired;
    }

    public Boolean getIsFailed() {
        return isFailed;
    }

    public void setIsFailed(Boolean isFailed) {
        this.isFailed = isFailed;
    }

    public Boolean getIsLinked() {
        return isLinked;
    }

    public void setIsLinked(Boolean isLinked) {
        this.isLinked = isLinked;
    }

    public Boolean getIsPending() {
        return isPending;
    }

    public void setIsPending(Boolean isPending) {
        this.isPending = isPending;
    }

}
package com.nowfloats.AccrossVerticals.API.model.AddDomain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddDomainData {

    @SerializedName("clientId")
    @Expose
    private String clientId;
    @SerializedName("domainName")
    @Expose
    private String domainName;
    @SerializedName("domainType")
    @Expose
    private String domainType;
    @SerializedName("existingFPTag")
    @Expose
    private String existingFPTag;
    @SerializedName("domainChannelType")
    @Expose
    private Integer domainChannelType;
    @SerializedName("DomainRegService")
    @Expose
    private Integer domainRegService;
    @SerializedName("validityInYears")
    @Expose
    private Integer validityInYears;
    @SerializedName("DomainOrderType")
    @Expose
    private Integer domainOrderType;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
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

    public String getExistingFPTag() {
        return existingFPTag;
    }

    public void setExistingFPTag(String existingFPTag) {
        this.existingFPTag = existingFPTag;
    }

    public Integer getDomainChannelType() {
        return domainChannelType;
    }

    public void setDomainChannelType(Integer domainChannelType) {
        this.domainChannelType = domainChannelType;
    }

    public Integer getDomainRegService() {
        return domainRegService;
    }

    public void setDomainRegService(Integer domainRegService) {
        this.domainRegService = domainRegService;
    }

    public Integer getValidityInYears() {
        return validityInYears;
    }

    public void setValidityInYears(Integer validityInYears) {
        this.validityInYears = validityInYears;
    }

    public Integer getDomainOrderType() {
        return domainOrderType;
    }

    public void setDomainOrderType(Integer domainOrderType) {
        this.domainOrderType = domainOrderType;
    }

}
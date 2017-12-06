package com.nowfloats.NavigationDrawer.model;

/**
 * Created by Admin on 30-11-2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WildFireDataModel {

    @SerializedName("Address")
    @Expose
    private String address;
    @SerializedName("BaseUrl")
    @Expose
    private String baseUrl;
    @SerializedName("Category")
    @Expose
    private String category;
    @SerializedName("City")
    @Expose
    private String city;
    @SerializedName("ClientId")
    @Expose
    private String clientId;
    @SerializedName("Country")
    @Expose
    private String country;
    @SerializedName("CountryIsdCode")
    @Expose
    private String countryIsdCode;
    @SerializedName("Desc")
    @Expose
    private String desc;
    @SerializedName("Email")
    @Expose
    private String email;
    @SerializedName("EndDate")
    @Expose
    private String endDate;
    @SerializedName("ExternalSourceId")
    @Expose
    private String externalSourceId;
    @SerializedName("IsNfStoreFront")
    @Expose
    private Boolean isNfStoreFront;
    @SerializedName("Lat")
    @Expose
    private Integer lat;
    @SerializedName("Lng")
    @Expose
    private Integer lng;
    @SerializedName("LocalAds")
    @Expose
    private List<LocalAd> localAds = null;
    @SerializedName("MaxBudgetPerMonth")
    @Expose
    private Integer maxBudgetPerMonth;
    @SerializedName("MaxRoi")
    @Expose
    private List<MaxRous> maxRoi = null;
    @SerializedName("MaxTotalBudget")
    @Expose
    private Integer maxTotalBudget;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("PrimaryNumber")
    @Expose
    private String primaryNumber;
    @SerializedName("Radius")
    @Expose
    private String radius;
    @SerializedName("StartDate")
    @Expose
    private String startDate;
    @SerializedName("WebsiteUrl")
    @Expose
    private String websiteUrl;
    @SerializedName("CreatedOn")
    @Expose
    private String createdOn;
    @SerializedName("IsArchived")
    @Expose
    private Boolean isArchived;
    @SerializedName("Status")
    @Expose
    private Integer status;
    @SerializedName("_id")
    @Expose
    private String id;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryIsdCode() {
        return countryIsdCode;
    }

    public void setCountryIsdCode(String countryIsdCode) {
        this.countryIsdCode = countryIsdCode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getExternalSourceId() {
        return externalSourceId;
    }

    public void setExternalSourceId(String externalSourceId) {
        this.externalSourceId = externalSourceId;
    }

    public Boolean getIsNfStoreFront() {
        return isNfStoreFront;
    }

    public void setIsNfStoreFront(Boolean isNfStoreFront) {
        this.isNfStoreFront = isNfStoreFront;
    }

    public Integer getLat() {
        return lat;
    }

    public void setLat(Integer lat) {
        this.lat = lat;
    }

    public Integer getLng() {
        return lng;
    }

    public void setLng(Integer lng) {
        this.lng = lng;
    }

    public List<LocalAd> getLocalAds() {
        return localAds;
    }

    public void setLocalAds(List<LocalAd> localAds) {
        this.localAds = localAds;
    }

    public Integer getMaxBudgetPerMonth() {
        return maxBudgetPerMonth;
    }

    public void setMaxBudgetPerMonth(Integer maxBudgetPerMonth) {
        this.maxBudgetPerMonth = maxBudgetPerMonth;
    }

    public List<MaxRous> getMaxRoi() {
        return maxRoi;
    }

    public void setMaxRoi(List<MaxRous> maxRoi) {
        this.maxRoi = maxRoi;
    }

    public Integer getMaxTotalBudget() {
        return maxTotalBudget;
    }

    public void setMaxTotalBudget(Integer maxTotalBudget) {
        this.maxTotalBudget = maxTotalBudget;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrimaryNumber() {
        return primaryNumber;
    }

    public void setPrimaryNumber(String primaryNumber) {
        this.primaryNumber = primaryNumber;
    }

    public String getRadius() {
        return radius;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public Boolean getIsArchived() {
        return isArchived;
    }

    public void setIsArchived(Boolean isArchived) {
        this.isArchived = isArchived;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static class LocalAd {

        @SerializedName("AdChannelType")
        @Expose
        private Integer adChannelType;
        @SerializedName("AdContent")
        @Expose
        private List<String> adContent = null;
        @SerializedName("AdTitle")
        @Expose
        private String adTitle;
        @SerializedName("AdType")
        @Expose
        private Object adType;
        @SerializedName("AdUrl")
        @Expose
        private String adUrl;
        @SerializedName("keywords")
        @Expose
        private List<String> keywords = null;

        public Integer getAdChannelType() {
            return adChannelType;
        }

        public void setAdChannelType(Integer adChannelType) {
            this.adChannelType = adChannelType;
        }

        public List<String> getAdContent() {
            return adContent;
        }

        public void setAdContent(List<String> adContent) {
            this.adContent = adContent;
        }

        public String getAdTitle() {
            return adTitle;
        }

        public void setAdTitle(String adTitle) {
            this.adTitle = adTitle;
        }

        public Object getAdType() {
            return adType;
        }

        public void setAdType(Object adType) {
            this.adType = adType;
        }

        public String getAdUrl() {
            return adUrl;
        }

        public void setAdUrl(String adUrl) {
            this.adUrl = adUrl;
        }

        public List<String> getKeywords() {
            return keywords;
        }

        public void setKeywords(List<String> keywords) {
            this.keywords = keywords;
        }

    }

    public static class MaxRous {

        @SerializedName("Key")
        @Expose
        private Long key;
        @SerializedName("Value")
        @Expose
        private Long value;
        @SerializedName("Weight")
        @Expose
        private Long weight;

        public Long getKey() {
            return key;
        }

        public void setKey(Long key) {
            this.key = key;
        }

        public Long getValue() {
            return value;
        }

        public void setValue(Long value) {
            this.value = value;
        }

        public Long getWeight() {
            return weight;
        }

        public void setWeight(Long weight) {
            this.weight = weight;
        }

    }

}

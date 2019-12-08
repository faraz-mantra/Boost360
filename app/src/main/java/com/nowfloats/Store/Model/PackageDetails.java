package com.nowfloats.Store.Model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by NowFloats on 16-10-2017.
 */

public class PackageDetails implements Comparable<PackageDetails> {
    @SerializedName("CurrencyCode")
    @Expose
    private String currencyCode;
    @SerializedName("Desc")
    @Expose
    private String desc;
    @SerializedName("ExpiryInMths")
    @Expose
    private Double expiryInMths;
    @SerializedName("ExternalApplicationDetails")
    @Expose
    private Object externalApplicationDetails;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Price")
    @Expose
    private Double price;
    @SerializedName("PrimaryImageUri")
    @Expose
    private String primaryImageUri;
    @SerializedName("Priority")
    @Expose
    private Long priority;
    @SerializedName("Screenshots")
    @Expose
    private List<Screenshots> screenshots;
    @SerializedName("Type")
    @Expose
    private Integer type;
    @SerializedName("ValidCity")
    @Expose
    private List<String> validCity;
    @SerializedName("ValidCountry")
    @Expose
    private List<String> validCountry = null;
    @SerializedName("ValidityInMths")
    @Expose
    private Double validityInMths;
    @SerializedName("WidgetPacks")
    @Expose
    private List<WidgetPacks> widgetPacks = null;
    @SerializedName("maxDiscount")
    @Expose
    private MaxDiscount maxDiscount;
    @SerializedName("packageVisibilityType")
    @Expose
    private Integer packageVisibilityType;
    @SerializedName("renewalChannelId")
    @Expose
    private String renewalChannelId;
    @SerializedName("supportChannelId")
    @Expose
    private String supportChannelId;
    @SerializedName("upSellChannelId")
    @Expose
    private String upSellChannelId;
    @SerializedName("CreatedOn")
    @Expose
    private String createdOn;
    @SerializedName("Identifier")
    @Expose
    private String identifier;
    @SerializedName("IsArchived")
    @Expose
    private Boolean isArchived;
    @SerializedName("SupportedPaymentMethods")
    @Expose
    private List<SupportedPaymentMethods> supportedPaymentMethods = null;
    @SerializedName("Taxes")
    @Expose
    private List<TaxDetail> taxes = null;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("packageCategory")
    @Expose
    private List<String> packageCategory = null;
    @SerializedName("productClassification")
    @Expose
    private ProductClassification productClassification;
    @SerializedName("revenueShare")
    @Expose
    private Object revenueShare;
    @SerializedName("NetAmount")
    @Expose
    private Double netAmount;
    @SerializedName("packageName")
    @Expose
    private String packageName;
    @SerializedName("netPackagePrice")
    @Expose
    private Double netPackagePrice;

    private List<String> featureList;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Double getNetPackagePrice() {
        return netPackagePrice;
    }

    public void setNetPackagePrice(Double netPackagePrice) {
        this.netPackagePrice = netPackagePrice;
    }

    public List<String> getFeatureList() {
        return featureList;
    }

    public void setFeatureList(List<String> featureList) {
        this.featureList = featureList;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Double getExpiryInMths() {
        return expiryInMths;
    }

    public void setExpiryInMths(Double expiryInMths) {
        this.expiryInMths = expiryInMths;
    }

    public Object getExternalApplicationDetails() {
        return externalApplicationDetails;
    }

    public void setExternalApplicationDetails(Object externalApplicationDetails) {
        this.externalApplicationDetails = externalApplicationDetails;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getPrimaryImageUri() {
        return primaryImageUri;
    }

    public void setPrimaryImageUri(String primaryImageUri) {
        this.primaryImageUri = primaryImageUri;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    public List<Screenshots> getScreenshots() {
        return screenshots;
    }

    public void setScreenshots(List<Screenshots> screenshots) {
        this.screenshots = screenshots;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public List<String> getValidCity() {
        return validCity;
    }

    public void setValidCity(List<String> validCity) {
        this.validCity = validCity;
    }

    public List<String> getValidCountry() {
        return validCountry;
    }

    public void setValidCountry(List<String> validCountry) {
        this.validCountry = validCountry;
    }

    public Double getValidityInMths() {
        return validityInMths;
    }

    public void setValidityInMths(Double validityInMths) {
        this.validityInMths = validityInMths;
    }

    public List<WidgetPacks> getWidgetPacks() {
        return widgetPacks;
    }

    public void setWidgetPacks(List<WidgetPacks> widgetPacks) {
        this.widgetPacks = widgetPacks;
    }

    public MaxDiscount getMaxDiscount() {
        return maxDiscount;
    }

    public void setMaxDiscount(MaxDiscount maxDiscount) {
        this.maxDiscount = maxDiscount;
    }

    public Integer getPackageVisibilityType() {
        return packageVisibilityType;
    }

    public void setPackageVisibilityType(Integer packageVisibilityType) {
        this.packageVisibilityType = packageVisibilityType;
    }

    public String getRenewalChannelId() {
        return renewalChannelId;
    }

    public void setRenewalChannelId(String renewalChannelId) {
        this.renewalChannelId = renewalChannelId;
    }

    public String getSupportChannelId() {
        return supportChannelId;
    }

    public void setSupportChannelId(String supportChannelId) {
        this.supportChannelId = supportChannelId;
    }

    public String getUpSellChannelId() {
        return upSellChannelId;
    }

    public void setUpSellChannelId(String upSellChannelId) {
        this.upSellChannelId = upSellChannelId;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Boolean getIsArchived() {
        return isArchived;
    }

    public void setIsArchived(Boolean isArchived) {
        this.isArchived = isArchived;
    }

    public List<SupportedPaymentMethods> getSupportedPaymentMethods() {
        return supportedPaymentMethods;
    }

    public void setSupportedPaymentMethods(List<SupportedPaymentMethods> supportedPaymentMethods) {
        this.supportedPaymentMethods = supportedPaymentMethods;
    }

    public List<TaxDetail> getTaxes() {
        return taxes;
    }

    public void setTaxes(List<TaxDetail> taxes) {
        this.taxes = taxes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getPackageCategory() {
        return packageCategory;
    }

    public void setPackageCategory(List<String> packageCategory) {
        this.packageCategory = packageCategory;
    }

    public ProductClassification getProductClassification() {
        return productClassification;
    }

    public void setProductClassification(ProductClassification productClassification) {
        this.productClassification = productClassification;
    }

    public Object getRevenueShare() {
        return revenueShare;
    }

    public void setRevenueShare(Object revenueShare) {
        this.revenueShare = revenueShare;
    }

    public Double getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(Double netAmount) {
        this.netAmount = netAmount;
    }

    @Override
    public int compareTo(@NonNull PackageDetails o) {
        return (this.getPriority() > o.getPriority()) ? -1 : ((this.getPriority().equals(o.getPriority())) ? 0 : 1);
    }
}

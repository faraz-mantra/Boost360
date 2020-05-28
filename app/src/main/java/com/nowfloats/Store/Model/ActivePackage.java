package com.nowfloats.Store.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by NowFloats on 16-10-2017.
 */

public class ActivePackage {
    @SerializedName("ClientId")
    @Expose
    private String clientId;
    @SerializedName("ClientProductId")
    @Expose
    private String clientProductId;
    @SerializedName("CreatedOn")
    @Expose
    private String createdOn;
    @SerializedName("CurrencyCode")
    @Expose
    private String currencyCode;
    @SerializedName("Desc")
    @Expose
    private String desc;
    @SerializedName("Discount")
    @Expose
    private Double discount;
    @SerializedName("FloatingPointId")
    @Expose
    private String floatingPointId;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("PaidAmount")
    @Expose
    private Double paidAmount;
    @SerializedName("PrimaryImageUri")
    @Expose
    private String primaryImageUri;
    @SerializedName("Screenshots")
    @Expose
    private List<Screenshots> screenshots = null;
    @SerializedName("ProductClassification")
    @Expose
    private ProductClassification productClassification;
    @SerializedName("ToBeActivatedOn")
    @Expose
    private String toBeActivatedOn;
    @SerializedName("TotalMonthsValidity")
    @Expose
    private Double totalMonthsValidity;
    @SerializedName("WidgetPacks")
    @Expose
    private List<WidgetPacks> widgetPacks;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("_nfInternalERPId")
    @Expose
    private String nfInternalERPId;
    @SerializedName("isActive")
    @Expose
    private Boolean isActive;
    @SerializedName("createdOn")
    @Expose
    private String paymentDate;
    @SerializedName("_nfInternalClaimId")
    @Expose
    private String claimid;
    @SerializedName("paymentTransactionStatus")
    @Expose
    private int paymentStatus;

    @SerializedName("packageDetails")
    @Expose
    public List<PackageDetails> packageDetails;

    public List<PackageDetails> getPackageDetails() {
        return packageDetails;
    }

    public void setPackageDetails(List<PackageDetails> packageDetails) {
        this.packageDetails = packageDetails;
    }

    private String activeStatus = "";
    private boolean isExpanded;

    private String features;

    public String getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(String activeStatus) {
        this.activeStatus = activeStatus;
    }

    public Boolean getActive() {
        return isActive;
    }

    public List<WidgetPacks> getWidgetPacks() {
        return widgetPacks;
    }

    public void setWidgetPacks(List<WidgetPacks> widgetPacks) {
        this.widgetPacks = widgetPacks;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientProductId() {
        return clientProductId;
    }

    public void setClientProductId(String clientProductId) {
        this.clientProductId = clientProductId;
    }

    public ProductClassification getProductClassification() {
        return productClassification;
    }

    public void setProductClassification(ProductClassification productClassification) {
        this.productClassification = productClassification;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
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

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public String getFloatingPointId() {
        return floatingPointId;
    }

    public void setFloatingPointId(String floatingPointId) {
        this.floatingPointId = floatingPointId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getPrimaryImageUri() {
        return primaryImageUri;
    }

    public void setPrimaryImageUri(String primaryImageUri) {
        this.primaryImageUri = primaryImageUri;
    }

    public List<Screenshots> getScreenshots() {
        return screenshots;
    }

    public void setScreenshots(List<Screenshots> screenshots) {
        this.screenshots = screenshots;
    }

    public String getToBeActivatedOn() {
        return toBeActivatedOn;
    }

    public void setToBeActivatedOn(String toBeActivatedOn) {
        this.toBeActivatedOn = toBeActivatedOn;
    }

    public Double getTotalMonthsValidity() {
        return totalMonthsValidity;
    }

    public void setTotalMonthsValidity(Double totalMonthsValidity) {
        this.totalMonthsValidity = totalMonthsValidity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNfInternalERPId() {
        return nfInternalERPId;
    }

    public void setNfInternalERPId(String nfInternalERPId) {
        this.nfInternalERPId = nfInternalERPId;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getClaimid() {
        return claimid;
    }

    public void setClaimid(String claimid) {
        this.claimid = claimid;
    }

    public int getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(int paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public class ProductClassification {

        @SerializedName("packType")
        @Expose
        private Integer packType;

        public Integer getPackType() {
            return packType;
        }

        public void setPackType(Integer packType) {
            this.packType = packType;
        }

    }
}

package nfkeyboard.models.networkmodels;

import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import nfkeyboard.adapter.BaseAdapterManager;
import nfkeyboard.models.AllSuggestionModel;

/**
 * Created by NowFloats on 27-02-2018.
 */

public class Product {
    @SerializedName("ApplicationId")
    @Expose
    private String applicationId;
    @SerializedName("BuyOnlineLink")
    @Expose
    private String buyOnlineLink;
    @SerializedName("CreatedOn")
    @Expose
    private String createdOn;
    @SerializedName("CurrencyCode")
    @Expose
    private String currencyCode;
    @SerializedName("CustomWidgets")
    @Expose
    private Object customWidgets;
    @SerializedName("Description")
    @Expose
    private String description;
    @SerializedName("DiscountAmount")
    @Expose
    private String discountAmount;
    @SerializedName("ExternalSourceId")
    @Expose
    private Object externalSourceId;
    @SerializedName("FPTag")
    @Expose
    private String fPTag;
    @SerializedName("GPId")
    @Expose
    private Object gPId;
    @SerializedName("GroupProductId")
    @Expose
    private Object groupProductId;
    @SerializedName("ImageUri")
    @Expose
    private String imageUri;
    @SerializedName("Images")
    @Expose
    private Object images;
    @SerializedName("IsArchived")
    @Expose
    private Boolean isArchived;
    @SerializedName("IsAvailable")
    @Expose
    private Boolean isAvailable;
    @SerializedName("IsFreeShipmentAvailable")
    @Expose
    private Boolean isFreeShipmentAvailable;
    @SerializedName("MerchantName")
    @Expose
    private String merchantName;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Price")
    @Expose
    private String price;
    @SerializedName("Priority")
    @Expose
    private Integer priority;
    @SerializedName("ProductIndex")
    @Expose
    private Integer productIndex;
    @SerializedName("ProductUrl")
    @Expose
    private String productUrl;
    @SerializedName("ShipmentDuration")
    @Expose
    private Integer shipmentDuration;
    @SerializedName("TileImageUri")
    @Expose
    private Object tileImageUri;
    @SerializedName("TotalQueries")
    @Expose
    private Integer totalQueries;
    @SerializedName("UpdatedOn")
    @Expose
    private String updatedOn;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("_keywords")
    @Expose
    private List<String> keywords = null;
    @SerializedName("availableUnits")
    @Expose
    private Integer availableUnits;
    @SerializedName("sharedPlatforms")
    @Expose
    private List<Object> sharedPlatforms = null;

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getBuyOnlineLink() {
        return buyOnlineLink;
    }

    public void setBuyOnlineLink(String buyOnlineLink) {
        this.buyOnlineLink = buyOnlineLink;
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

    public Object getCustomWidgets() {
        return customWidgets;
    }

    public void setCustomWidgets(Object customWidgets) {
        this.customWidgets = customWidgets;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(String discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Object getExternalSourceId() {
        return externalSourceId;
    }

    public void setExternalSourceId(Object externalSourceId) {
        this.externalSourceId = externalSourceId;
    }

    public String getFPTag() {
        return fPTag;
    }

    public void setFPTag(String fPTag) {
        this.fPTag = fPTag;
    }

    public Object getGPId() {
        return gPId;
    }

    public void setGPId(Object gPId) {
        this.gPId = gPId;
    }

    public Object getGroupProductId() {
        return groupProductId;
    }

    public void setGroupProductId(Object groupProductId) {
        this.groupProductId = groupProductId;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public Object getImages() {
        return images;
    }

    public void setImages(Object images) {
        this.images = images;
    }

    public Boolean getIsArchived() {
        return isArchived;
    }

    public void setIsArchived(Boolean isArchived) {
        this.isArchived = isArchived;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Boolean getIsFreeShipmentAvailable() {
        return isFreeShipmentAvailable;
    }

    public void setIsFreeShipmentAvailable(Boolean isFreeShipmentAvailable) {
        this.isFreeShipmentAvailable = isFreeShipmentAvailable;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getProductIndex() {
        return productIndex;
    }

    public void setProductIndex(Integer productIndex) {
        this.productIndex = productIndex;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public Integer getShipmentDuration() {
        return shipmentDuration;
    }

    public void setShipmentDuration(Integer shipmentDuration) {
        this.shipmentDuration = shipmentDuration;
    }

    public Object getTileImageUri() {
        return tileImageUri;
    }

    public void setTileImageUri(Object tileImageUri) {
        this.tileImageUri = tileImageUri;
    }

    public Integer getTotalQueries() {
        return totalQueries;
    }

    public void setTotalQueries(Integer totalQueries) {
        this.totalQueries = totalQueries;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public Integer getAvailableUnits() {
        return availableUnits;
    }

    public void setAvailableUnits(Integer availableUnits) {
        this.availableUnits = availableUnits;
    }

    public List<Object> getSharedPlatforms() {
        return sharedPlatforms;
    }

    public void setSharedPlatforms(List<Object> sharedPlatforms) {
        this.sharedPlatforms = sharedPlatforms;
    }

    public AllSuggestionModel toAllSuggestion() {
        AllSuggestionModel model = new AllSuggestionModel(name, imageUri);
        model.setDescription(description);
        model.setDiscount(discountAmount);
        model.setPrice(price);
        model.setUrl(productUrl);
        model.setAvailableUnits(availableUnits);
        model.setId(id);
        model.setFpTag(fPTag);
        model.setClientId(applicationId);
        model.setCurrencyCode(currencyCode);
        model.setMaxUsage((availableUnits > 0) ? availableUnits : 10);
        model.setP_id(id);
        model.setTypeEnum(BaseAdapterManager.SectionTypeEnum.Product);
        model.setEditTextValueTemp((model.getEditTextValueTemp() == null || model.getEditTextValueTemp().equalsIgnoreCase("")) ? price : model.getEditTextValueTemp());
        return model;
    }
}

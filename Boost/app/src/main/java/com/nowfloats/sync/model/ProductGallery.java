package com.nowfloats.sync.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RAJA on 20-06-2016.
 */
public class ProductGallery {
    private int Id;
    private String buyOnlineLink;
    private String currencyCode;
    private String description;
    private String discountAmount;
    private String externalSourceId;
    private String isArchived;
    private String isAvailable;
    private String name;
    private long priority;
    private int shipmentDuration;
    private String applicationId;
    private List<String> customWidgets;
    private String fpTag;
    private ArrayList<String> images; // need to check
    private String imageUrl;
    private String merchantName;
    private String tileImageUri;
    private String serverId;
    private String gpId;
    private int totalQueries;
    private String createdOn;
    private String updatedOn;
    private String price;

    public int getId() {
        return Id;
    }

    public ProductGallery setId(int id) {
        Id = id;
        return this;
    }

    public String getBuyOnlineLink() {
        return buyOnlineLink;
    }

    public ProductGallery setBuyOnlineLink(String buyOnlineLink) {
        this.buyOnlineLink = buyOnlineLink;
        return this;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public ProductGallery setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ProductGallery setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getDiscountAmount() {
        return discountAmount;
    }

    public ProductGallery setDiscountAmount(String discountAmount) {
        this.discountAmount = discountAmount;
        return this;
    }

    public String getExternalSourceId() {
        return externalSourceId;
    }

    public ProductGallery setExternalSourceId(String externalSourceId) {
        this.externalSourceId = externalSourceId;
        return this;
    }

    public String getIsArchived() {
        return isArchived;
    }

    public ProductGallery setIsArchived(String isArchived) {
        this.isArchived = isArchived;
        return this;
    }

    public String getIsAvailable() {
        return isAvailable;
    }

    public ProductGallery setIsAvailable(String isAvailable) {
        this.isAvailable = isAvailable;
        return this;
    }

    public String getName() {
        return name;
    }

    public ProductGallery setName(String name) {
        this.name = name;
        return this;
    }

    public long getPriority() {
        return priority;
    }

    public ProductGallery setPriority(long priority) {
        this.priority = priority;
        return this;
    }

    public int getShipmentDuration() {
        return shipmentDuration;
    }

    public ProductGallery setShipmentDuration(int shipmentDuration) {
        this.shipmentDuration = shipmentDuration;
        return this;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public ProductGallery setApplicationId(String applicationId) {
        this.applicationId = applicationId;
        return this;
    }

    public List<String> getCustomWidgets() {
        return customWidgets;
    }

    public ProductGallery setCustomWidgets(List<String> customWidgets) {
        this.customWidgets = customWidgets;
        return this;
    }

    public String getFpTag() {
        return fpTag;
    }

    public ProductGallery setFpTag(String fpTag) {
        this.fpTag = fpTag;
        return this;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public ProductGallery setImages(ArrayList<String> images) {
        this.images = images;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public ProductGallery setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public ProductGallery setMerchantName(String merchantName) {
        this.merchantName = merchantName;
        return this;
    }

    public String getTileImageUri() {
        return tileImageUri;
    }

    public ProductGallery setTileImageUri(String tileImageUri) {
        this.tileImageUri = tileImageUri;
        return this;
    }

    public String getServerId() {
        return serverId;
    }

    public ProductGallery setServerId(String serverId) {
        this.serverId = serverId;
        return this;
    }

    public String getGpId() {
        return gpId;
    }

    public ProductGallery setGpId(String gpId) {
        this.gpId = gpId;
        return this;
    }

    public int getTotalQueries() {
        return totalQueries;
    }

    public ProductGallery setTotalQueries(int totalQueries) {
        this.totalQueries = totalQueries;
        return this;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public ProductGallery setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
        return this;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public ProductGallery setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
        return this;
    }

    public String getPrice() {
        return price;
    }

    public ProductGallery setPrice(String price) {
        this.price = price;
        return this;
    }
}

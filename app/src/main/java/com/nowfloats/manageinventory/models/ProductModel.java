package com.nowfloats.manageinventory.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProductModel implements Parcelable {

    public static final Creator<ProductModel> CREATOR = new Creator<ProductModel>() {
        @Override
        public ProductModel createFromParcel(Parcel in) {
            return new ProductModel(in);
        }

        @Override
        public ProductModel[] newArray(int size) {
            return new ProductModel[size];
        }
    };
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("Product_id")
    @Expose
    private String productId;
    @SerializedName("quantity")
    @Expose
    private Double quantity;
    @SerializedName("merchant_id")
    @Expose
    private String merchantId;
    @SerializedName("Order_id")
    @Expose
    private String orderId;
    @SerializedName("Product_name")
    @Expose
    private String productName;
    @SerializedName("product_img_url")
    @Expose
    private String productImgUrl;
    @SerializedName("price")
    @Expose
    private Double price;
    @SerializedName("discount")
    @Expose
    private Double discount;
    @SerializedName("final_price")
    @Expose
    private Double finalPrice;
    @SerializedName("remarks")
    @Expose
    private String remarks;
    @SerializedName("currency_code")
    @Expose
    private String currencyCode;
    @SerializedName("length")
    @Expose
    private Double length;
    @SerializedName("height")
    @Expose
    private Double height;
    @SerializedName("width")
    @Expose
    private Double width;
    @SerializedName("weight")
    @Expose
    private Double weight;
    @SerializedName("UserId")
    @Expose
    private String userId;
    @SerializedName("ActionId")
    @Expose
    private String actionId;
    @SerializedName("WebsiteId")
    @Expose
    private String websiteId;
    @SerializedName("CreatedOn")
    @Expose
    private String createdOn;
    @SerializedName("UpdatedOn")
    @Expose
    private String updatedOn;
    @SerializedName("IsArchived")
    @Expose
    private Boolean isArchived;

    protected ProductModel(Parcel in) {
        id = in.readString();
        productId = in.readString();
        quantity = in.readDouble();
        merchantId = in.readString();
        orderId = in.readString();
        productName = in.readString();
        productImgUrl = in.readString();
        price = in.readDouble();
        discount = in.readDouble();
        finalPrice = in.readDouble();
        remarks = in.readString();
        currencyCode = in.readString();
        length = in.readDouble();
        height = in.readDouble();
        width = in.readDouble();
        weight = in.readDouble();
        userId = in.readString();
        actionId = in.readString();
        websiteId = in.readString();
        createdOn = in.readString();
        updatedOn = in.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImgUrl() {
        return productImgUrl;
    }

    public void setProductImgUrl(String productImgUrl) {
        this.productImgUrl = productImgUrl;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(Double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public String getWebsiteId() {
        return websiteId;
    }

    public void setWebsiteId(String websiteId) {
        this.websiteId = websiteId;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }

    public Boolean getIsArchived() {
        return isArchived;
    }

    public void setIsArchived(Boolean isArchived) {
        this.isArchived = isArchived;
    }

    @Override
    public int describeContents() {
        return 22;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(productId);
        dest.writeDouble(quantity);
        dest.writeString(merchantId);
        dest.writeString(orderId);
        dest.writeString(productName);
        dest.writeString(productImgUrl);
        dest.writeDouble(price);
        dest.writeDouble(discount);
        dest.writeDouble(finalPrice);
        dest.writeString(remarks);
        dest.writeString(currencyCode);
        dest.writeDouble(length);
        dest.writeDouble(height);
        dest.writeDouble(width);
        dest.writeDouble(weight);
        dest.writeString(userId);
        dest.writeString(actionId);
        dest.writeString(websiteId);
        dest.writeString(createdOn);
        dest.writeString(updatedOn);
    }
}
package com.nowfloats.ProductGallery.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by NowFloats on 18-08-2017.
 */

public class ShippingMetricsModel implements Parcelable{
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("weight")
    @Expose
    private String weight;
    @SerializedName("height")
    @Expose
    private String height;
    @SerializedName("width")
    @Expose
    private String width;
    @SerializedName("length")
    @Expose
    private String length;
    @SerializedName("shipping_charge")
    @Expose
    private Double shippingCharge;
    @SerializedName("gst_slab")
    @Expose
    private Double gstCharge;
    @SerializedName("product_id")
    @Expose
    private String productId;
    @SerializedName("merchant_id")
    @Expose
    private String merchantId;
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
    @SerializedName("hide_price")
    @Expose
    private Boolean isHidePrice = false;

    public ShippingMetricsModel(){

    }

    protected ShippingMetricsModel(Parcel in) {
        id = in.readString();
        weight = in.readString();
        height = in.readString();
        width = in.readString();
        length = in.readString();
        productId = in.readString();
        merchantId = in.readString();
        userId = in.readString();
        actionId = in.readString();
        websiteId = in.readString();
        createdOn = in.readString();
        updatedOn = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(weight);
        dest.writeString(height);
        dest.writeString(width);
        dest.writeString(length);
        dest.writeString(productId);
        dest.writeString(merchantId);
        dest.writeString(userId);
        dest.writeString(actionId);
        dest.writeString(websiteId);
        dest.writeString(createdOn);
        dest.writeString(updatedOn);
    }

    @Override
    public int describeContents() {
        return 12;
    }

    public static final Creator<ShippingMetricsModel> CREATOR = new Creator<ShippingMetricsModel>() {
        @Override
        public ShippingMetricsModel createFromParcel(Parcel in) {
            return new ShippingMetricsModel(in);
        }

        @Override
        public ShippingMetricsModel[] newArray(int size) {
            return new ShippingMetricsModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public Double getShippingCharge() {
        return shippingCharge;
    }

    public void setShippingCharge(Double shippingCharge) {
        this.shippingCharge = shippingCharge;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
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

    public Double getGstCharge() {
        return gstCharge;
    }

    public void setGstCharge(Double gstCharge) {
        this.gstCharge = gstCharge;
    }

    public Boolean getHidePrice() {
        return isHidePrice;
    }

    public void setHidePrice(Boolean hidePrice) {
        isHidePrice = hidePrice;
    }
}

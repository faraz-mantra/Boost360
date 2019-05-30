package com.nowfloats.ProductGallery.Model;

import com.google.gson.annotations.SerializedName;

public class AssuredPurchase {

    @SerializedName("weight")
    public double weight;
    @SerializedName("height")
    public double height;
    @SerializedName("width")
    public double width;
    @SerializedName("length")
    public double length;
    @SerializedName("gst_slab")
    public double gstCharge;
    @SerializedName("product_id")
    public String productId;
    @SerializedName("merchant_id")
    public String merchantId;
    @SerializedName("WebsiteId")
    public String websiteId;
}

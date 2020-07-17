package com.nowfloats.ProductGallery.Model;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Product implements Serializable {

    @SerializedName(value = "currencycode", alternate = {"CurrencyCode"})
    public String CurrencyCode;
    @SerializedName(value="description", alternate={"Description"})
    public String Description;
    @SerializedName(value="discountAmount", alternate={"DiscountAmount"})
    public double DiscountAmount;
    public String ExternalSourceId;
    public String IsArchived;
    @SerializedName(value="isAvailable", alternate={"IsAvailable"})
    public boolean IsAvailable;
    @SerializedName(value="isFreeShipmentAvailable", alternate={"IsFreeShipmentAvailable"})
    public String IsFreeShipmentAvailable;
    @SerializedName(value="name", alternate={"Name"})
    public String Name;
    @SerializedName(value="price", alternate={"Price"})
    public double Price;
    @SerializedName(value="priority", alternate={"Priority"})
    public String Priority;
    public String ShipmentDuration;
    public int availableUnits = 1;
    public ArrayList<String> _keywords;
    public List<String> tags;
    public String ApplicationId;
    @SerializedName(value = "fpTag", alternate = {"FPTag"})
    public String FPTag;
    @SerializedName(value="clientId", alternate={"ClientId"})
    public String ClientId;

    public String ImageUri;
    public String ProductUrl;
    public ArrayList<ImageListModel> Images;
    public String MerchantName;
    public String TileImageUri;
    @SerializedName("_id")
    public String productId;
    public String GPId;
    public String TotalQueries;
    public String CreatedOn;
    public String ProductIndex;
    public Uri picimageURI =null;
    public String UpdatedOn;
    public boolean isProductSelected;

    public String productType;
    public String paymentType;
    public boolean variants;
    public String brandName;
    public String category;

    @SerializedName("isCodAvailable")
    public boolean codAvailable;
    @SerializedName("maxCodOrders")
    public int maxCodOrders = 10;
    @SerializedName("isPrepaidOnlineAvailable")
    public boolean prepaidOnlineAvailable;
    @SerializedName("maxPrepaidOnlineOrders")
    public int maxPrepaidOnlineAvailable = 10;

    @SerializedName("uniquePaymentUrl")
    public BuyOnlineLink BuyOnlineLink;
    public Specification keySpecification;
    @SerializedName("otherSpecifications")
    public List<Specification> otherSpecification;
    public String pickupAddressReferenceId;

    public static class BuyOnlineLink implements Serializable {
        public String url;
        public String description;
    }

    public static class Specification implements Serializable {
        public String key;
        public String value;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }
}
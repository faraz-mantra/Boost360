package com.nowfloats.Product_Gallery.Model;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Product implements Serializable {

    //public String BuyOnlineLink;
    @SerializedName(value="currencyCode", alternate={"CurrencyCode"})
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
    public int availableUnits;
    public ArrayList<String> _keywords;
    public String ApplicationId;
    @SerializedName(value="fPTag", alternate={"TPTag"})
    public String FPTag;
    @SerializedName(value="clientId", alternate={"ClientId"})
    public String ClientId;

    public String ImageUri;
    public String ProductUrl;
    public ArrayList<ImageListModel> Images;
    public String MerchantName;
    public String TileImageUri;
    public String _id;
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
    public int maxCodOrders;
    @SerializedName("isPrepaidOnlineAvailable")
    public boolean prepaidOnlineAvailable;
    @SerializedName("maxPrepaidOnlineOrders")
    public int maxPrepaidOnlineAvailable;

    public BuyOnlineLink BuyOnlineLink;
    public Specification keySpecification;
    public List<Specification> otherSpecification;


    public static class BuyOnlineLink implements Serializable
    {
        public String url;
        public String description;
    }

    public static class Specification implements Serializable
    {
        public String key;
        public String value;
    }
}
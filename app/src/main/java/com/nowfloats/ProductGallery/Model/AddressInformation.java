package com.nowfloats.ProductGallery.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AddressInformation implements Serializable {

    @SerializedName("_id")
    public String id;
    @SerializedName("WebsiteId")
    public String websiteId;
    @SerializedName("AreaName")
    public String areaName;
    @SerializedName("StreetAddress")
    public String streetAddress;
    @SerializedName("City")
    public String city;
    @SerializedName("State")
    public String state;
    @SerializedName("Country")
    public String country;
    @SerializedName("PinCode")
    public String pinCode;
    @SerializedName("ContactNumber")
    public String contactNumber;
    @SerializedName("AddressProof")
    public String addressProof;


    @Override
    public String toString()
    {
        return new StringBuilder().append(streetAddress).append(", ").append(city).append(", ").append(state).append(", ").append(country).toString();
    }
}
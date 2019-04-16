package com.nowfloats.Product_Gallery.Model;

import com.google.gson.annotations.SerializedName;

public class AddressInformation {

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
}

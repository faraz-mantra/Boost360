package com.nowfloats.hotel.API.model.AddPlacesAround;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ActionData {

    @SerializedName("placeImage")
    @Expose
    private PlaceImage placeImage;
    @SerializedName("placeName")
    @Expose
    private String placeName;
    @SerializedName("placeAddress")
    @Expose
    private String placeAddress;
    @SerializedName("distance")
    @Expose
    private String distance;

    public PlaceImage getPlaceImage() {
        return placeImage;
    }

    public void setPlaceImage(PlaceImage placeImage) {
        this.placeImage = placeImage;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public void setPlaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

}
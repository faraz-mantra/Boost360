package com.nowfloats.hotel.API.model.AddOffer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ActionData {

    @SerializedName("offerImage")
    @Expose
    private OfferImage offerImage;
    @SerializedName("offerTitle")
    @Expose
    private String offerTitle;
    @SerializedName("offerDescription")
    @Expose
    private String offerDescription;
    @SerializedName("orignalPrice")
    @Expose
    private Double orignalPrice;
    @SerializedName("discountedPrice")
    @Expose
    private Double discountedPrice;

    public OfferImage getOfferImage() {
        return offerImage;
    }

    public void setOfferImage(OfferImage offerImage) {
        this.offerImage = offerImage;
    }

    public String getOfferTitle() {
        return offerTitle;
    }

    public void setOfferTitle(String offerTitle) {
        this.offerTitle = offerTitle;
    }

    public String getOfferDescription() {
        return offerDescription;
    }

    public void setOfferDescription(String offerDescription) {
        this.offerDescription = offerDescription;
    }

    public Double getOrignalPrice() {
        return orignalPrice;
    }

    public void setOrignalPrice(Double orignalPrice) {
        this.orignalPrice = orignalPrice;
    }

    public Double getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(Double discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

}
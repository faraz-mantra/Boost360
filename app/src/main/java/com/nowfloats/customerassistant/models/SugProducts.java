package com.nowfloats.customerassistant.models;

import java.io.Serializable;

/**
 * Created by NowFloats on 4/28/2017.
 */

public class SugProducts implements Serializable{
    public String productName;
    public String Image;
    public String buyLink;
    public String productUrl;

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    private boolean isSelected;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getImage() {
        return Image;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }



    public void setImage(String image) {
        Image = image;
    }

    public String getBuyLink() {
        return buyLink;
    }

    public void setBuyLink(String buyLink) {
        this.buyLink = buyLink;
    }
}

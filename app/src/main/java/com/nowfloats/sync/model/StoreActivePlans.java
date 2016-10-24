package com.nowfloats.sync.model;

import java.util.List;

/**
 * Created by RAJA on 20-06-2016.
 */
public class StoreActivePlans {
    private int id;
    private String planName;
    private String planDescr;
    private List<String> imageUrl;
    private long price;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getPlanDescr() {
        return planDescr;
    }

    public void setPlanDescr(String planDescr) {
        this.planDescr = planDescr;
    }

    public List<String> getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(List<String> imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }
}

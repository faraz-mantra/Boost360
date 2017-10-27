package com.nowfloats.Store.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by NowFloats on 16-10-2017.
 */

public class MaxDiscount {
    @SerializedName("discountType")
    @Expose
    private Integer discountType;
    @SerializedName("value")
    @Expose
    private Double value;

    public Integer getDiscountType() {
        return discountType;
    }

    public void setDiscountType(Integer discountType) {
        this.discountType = discountType;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}

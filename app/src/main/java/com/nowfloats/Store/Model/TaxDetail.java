package com.nowfloats.Store.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TaxDetail {

    @SerializedName("AmountType")
    @Expose
    private Integer amountType;
    @SerializedName("Key")
    @Expose
    private String key;
    @SerializedName("Value")
    @Expose
    private Double value;

    /**
     * @return The amountType
     */
    public Integer getAmountType() {
        return amountType;
    }

    /**
     * @param amountType The AmountType
     */
    public void setAmountType(Integer amountType) {
        this.amountType = amountType;
    }

    /**
     * @return The key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key The Key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return The value
     */
    public Double getValue() {
        return value;
    }

    /**
     * @param value The Value
     */
    public void setValue(Double value) {
        this.value = value;
    }

}
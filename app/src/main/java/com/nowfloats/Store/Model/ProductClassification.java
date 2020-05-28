package com.nowfloats.Store.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by NowFloats on 16-10-2017.
 */

public class ProductClassification {

    @SerializedName("packType")
    @Expose
    private Integer packType;
    @SerializedName("productLine")
    @Expose
    private List<Integer> productLine = null;

    public Integer getPackType() {
        return packType;
    }

    public void setPackType(Integer packType) {
        this.packType = packType;
    }

    public List<Integer> getProductLine() {
        return productLine;
    }

    public void setProductLine(List<Integer> productLine) {
        this.productLine = productLine;
    }
}

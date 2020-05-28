package com.nowfloats.Store.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by NowFloats on 16-10-2017.
 */

public class PricingPlansModel {

    @SerializedName("ActivePackages")
    @Expose
    public List<ActivePackage> activePackages;

    @SerializedName("AllPackages")
    @Expose
    public List<AllPackage> allPackages;

}

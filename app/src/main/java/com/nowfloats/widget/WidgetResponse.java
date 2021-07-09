package com.nowfloats.widget;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nowfloats.Store.Model.ActivePackage;
import com.nowfloats.Store.Model.AllPackage;

import java.util.List;

public class WidgetResponse {

    @SerializedName("ActivePackages")
    @Expose
    public List<ActivePackage> activePackages;

    @SerializedName("AllPackages")
    @Expose
    public List<AllPackage> allPackages;


    public List<ActivePackage> getActivePackages()
    {
        return activePackages;
    }

    public void setActivePackages(List<ActivePackage> activePackages)
    {
        this.activePackages = activePackages;
    }
}
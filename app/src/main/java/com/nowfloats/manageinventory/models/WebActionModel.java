package com.nowfloats.manageinventory.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by NowFloats on 16-08-2017.
 */

public class WebActionModel<T> {
    @SerializedName("Data")
    @Expose
    private List<T> data = null;
    @SerializedName("Extra")
    @Expose
    private ExtraModel extra;

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public ExtraModel getExtra() {
        return extra;
    }

    public void setExtra(ExtraModel extra) {
        this.extra = extra;
    }
}

package com.nowfloats.webactions.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WebActionDataResponse<T> {

    @SerializedName("Data")
    @Expose
    private List<T> data = null;
    @SerializedName("Extra")
    @Expose
    private Extra extra;

    public List<T> getData() {
    return data;
    }

    public void setData(List<T> data) {
    this.data = data;
    }

    public Extra getExtra() {
    return extra;
    }

    public void setExtra(Extra extra) {
    this.extra = extra;
    }

}
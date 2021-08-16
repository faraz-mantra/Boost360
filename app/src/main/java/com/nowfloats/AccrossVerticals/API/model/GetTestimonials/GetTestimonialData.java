package com.nowfloats.AccrossVerticals.API.model.GetTestimonials;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetTestimonialData {

    @SerializedName("Data")
    @Expose
    private List<TestimonialData> data = null;
    @SerializedName("Extra")
    @Expose
    private Extra extra;

    public List<TestimonialData> getData() {
        return data;
    }

    public void setData(List<TestimonialData> data) {
        this.data = data;
    }

    public Extra getExtra() {
        return extra;
    }

    public void setExtra(Extra extra) {
        this.extra = extra;
    }

}
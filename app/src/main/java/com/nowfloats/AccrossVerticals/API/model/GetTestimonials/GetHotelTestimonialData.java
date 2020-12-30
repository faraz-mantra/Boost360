package com.nowfloats.AccrossVerticals.API.model.GetTestimonials;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetHotelTestimonialData {

    @SerializedName("Data")
    @Expose
    private List<HotelData> data = null;
    @SerializedName("Extra")
    @Expose
    private Extra extra;

    public List<HotelData> getData() {
        return data;
    }

    public void setData(List<HotelData> data) {
        this.data = data;
    }

    public Extra getExtra() {
        return extra;
    }

    public void setExtra(Extra extra) {
        this.extra = extra;
    }

}
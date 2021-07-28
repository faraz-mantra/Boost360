package com.nowfloats.hotel.API.model.GetOffers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetOffersResponse {

    @SerializedName("Data")
    @Expose
    private List<Data> data = null;
    @SerializedName("Extra")
    @Expose
    private Extra extra;

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public Extra getExtra() {
        return extra;
    }

    public void setExtra(Extra extra) {
        this.extra = extra;
    }

}
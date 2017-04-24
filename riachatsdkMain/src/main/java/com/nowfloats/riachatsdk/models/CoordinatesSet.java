package com.nowfloats.riachatsdk.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CoordinatesSet implements Serializable{

    @SerializedName("CoordinateList")
    @Expose
    private List<CoordinateList> coordinateList = null;
    @SerializedName("LegendName")
    @Expose
    private String legendName;

    public List<CoordinateList> getCoordinateList() {
        return coordinateList;
    }

    public void setCoordinateList(List<CoordinateList> coordinateList) {
        this.coordinateList = coordinateList;
    }

    public String getLegendName() {
        return legendName;
    }

    public void setLegendName(String legendName) {
        this.legendName = legendName;
    }

}
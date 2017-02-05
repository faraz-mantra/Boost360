package com.nowfloats.NavigationDrawer.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CoordinatesSet {

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
package com.nowfloats.riachatsdk.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CoordinateList {

    @SerializedName("X")
    @Expose
    private String x;
    @SerializedName("Y")
    @Expose
    private String y;
    @SerializedName("CoordinateText")
    @Expose
    private String coordinateText;

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public Object getCoordinateText() {
        return coordinateText;
    }

    public void setCoordinateText(String coordinateText) {
        this.coordinateText = coordinateText;
    }

}
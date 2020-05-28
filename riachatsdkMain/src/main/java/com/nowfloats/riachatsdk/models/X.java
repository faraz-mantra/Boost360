package com.nowfloats.riachatsdk.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class X implements Serializable{

    @SerializedName("StartLimit")
    @Expose
    private Double startLimit;
    @SerializedName("EndLimit")
    @Expose
    private Double endLimit;
    @SerializedName("Increments")
    @Expose
    private Double increments;
    @SerializedName("Label")
    @Expose
    private String label;
    @SerializedName("AxisType")
    @Expose
    private String axisType;

    public String getAxisType() {
        return axisType;
    }

    public void setAxisType(String axisType) {
        this.axisType = axisType;
    }

    public Double getStartLimit() {
        return startLimit;
    }

    public void setStartLimit(Double startLimit) {
        this.startLimit = startLimit;
    }

    public Double getEndLimit() {
        return endLimit;
    }

    public void setEndLimit(Double endLimit) {
        this.endLimit = endLimit;
    }

    public Double getIncrements() {
        return increments;
    }

    public void setIncrements(Double increments) {
        this.increments = increments;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}
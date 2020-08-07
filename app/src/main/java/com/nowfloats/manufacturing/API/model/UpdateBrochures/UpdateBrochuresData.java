package com.nowfloats.manufacturing.API.model.UpdateBrochures;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateBrochuresData {

    @SerializedName("Query")
    @Expose
    private String query;
    @SerializedName("UpdateValue")
    @Expose
    private String updateValue;
    @SerializedName("Multi")
    @Expose
    private Boolean multi;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getUpdateValue() {
        return updateValue;
    }

    public void setUpdateValue(String updateValue) {
        this.updateValue = updateValue;
    }

    public Boolean getMulti() {
        return multi;
    }

    public void setMulti(Boolean multi) {
        this.multi = multi;
    }

}
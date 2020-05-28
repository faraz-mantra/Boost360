package com.nowfloats.webactions.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nowfloats.webactions.webactioninterfaces.IFilter;
import com.nowfloats.webactions.webactioninterfaces.IUpdate;

/**
 * Created by NowFloats on 12-04-2018.
 */

public class WebActionUpdateRequestModel {

    @SerializedName("Query")
    @Expose
    private String query;
    @SerializedName("UpdateValue")
    @Expose
    private String updateValue;
    @SerializedName("Multi")
    @Expose
    private Boolean multi;

    public WebActionUpdateRequestModel(IFilter filter, IUpdate update) {
        this.query = filter.toQuery().toString();
        if(update == null){
            this.updateValue = null;
        }else {
            this.updateValue = update.toUpdateString().toString();
        }

    }

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

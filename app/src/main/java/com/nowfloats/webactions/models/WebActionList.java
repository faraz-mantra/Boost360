package com.nowfloats.webactions.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by NowFloats on 09-04-2018.
 */

public class WebActionList {
    @SerializedName("WebActions")
    @Expose
    private List<WebAction> webActions = null;
    @SerializedName("Extra")
    @Expose
    private Extra extra;

    public List<WebAction> getWebActions() {
        return webActions;
    }

    public void setWebActions(List<WebAction> webActions) {
        this.webActions = webActions;
    }

    public Extra getExtra() {
        return extra;
    }

    public void setExtra(Extra extra) {
        this.extra = extra;
    }

}

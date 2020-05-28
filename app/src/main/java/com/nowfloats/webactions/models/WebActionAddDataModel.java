package com.nowfloats.webactions.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by NowFloats on 12-04-2018.
 */

public class WebActionAddDataModel<T> {
    @SerializedName("WebsiteId")
    @Expose
    private String websiteId;
    @SerializedName("ActionData")
    @Expose
    private T actionData;

    public WebActionAddDataModel(String websiteId, T actionData) {
        this.websiteId = websiteId;
        this.actionData = actionData;
    }

    public String getWebsiteId() {
        return websiteId;
    }

    public void setWebsiteId(String websiteId) {
        this.websiteId = websiteId;
    }

    public T getActionData() {
        return actionData;
    }

    public void setActionData(T actionData) {
        this.actionData = actionData;
    }
}

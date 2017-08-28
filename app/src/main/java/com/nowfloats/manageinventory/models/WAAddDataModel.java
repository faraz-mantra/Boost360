package com.nowfloats.manageinventory.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by NowFloats on 18-08-2017.
 */

public class WAAddDataModel<T> {
    @SerializedName("WebsiteId")
    @Expose
    private String websiteId;
    @SerializedName("ActionData")
    @Expose
    private T actionData;

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

package com.nowfloats.manufacturing.API.model.AddBrochures;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddBrochuresData {

    @SerializedName("ActionData")
    @Expose
    private ActionData actionData;
    @SerializedName("WebsiteId")
    @Expose
    private String websiteId;

    public ActionData getActionData() {
        return actionData;
    }

    public void setActionData(ActionData actionData) {
        this.actionData = actionData;
    }

    public String getWebsiteId() {
        return websiteId;
    }

    public void setWebsiteId(String websiteId) {
        this.websiteId = websiteId;
    }

}
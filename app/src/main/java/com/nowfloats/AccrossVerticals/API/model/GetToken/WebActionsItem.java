package com.nowfloats.AccrossVerticals.API.model.GetToken;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WebActionsItem {

    @SerializedName("WebsiteId")
    private String websiteId;

    @SerializedName("ActionId")
    private String actionId;

    @SerializedName("UserName")
    private String userName;

    @SerializedName("Description")
    private String description;

    @SerializedName("UserId")
    private String userId;

    @SerializedName("DisplayName")
    private String displayName;

    @SerializedName("UpdatedOn")
    private String updatedOn;

    @SerializedName("Properties")
    private List<PropertiesItem> properties;

    @SerializedName("Name")
    private String name;

    public String getWebsiteId() {
        return websiteId;
    }

    public void setWebsiteId(String websiteId) {
        this.websiteId = websiteId;
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }

    public List<PropertiesItem> getProperties() {
        return properties;
    }

    public void setProperties(List<PropertiesItem> properties) {
        this.properties = properties;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
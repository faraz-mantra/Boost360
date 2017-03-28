package com.nowfloats.NavigationDrawer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Button {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("ButtonName")
    @Expose
    private String buttonName;
    @SerializedName("ButtonText")
    @Expose
    private String buttonText;
    @SerializedName("Emotion")
    @Expose
    private String emotion;
    @SerializedName("ButtonType")
    @Expose
    private String buttonType;
    @SerializedName("DeepLinkUrl")
    @Expose
    private String deepLinkUrl;
    @SerializedName("NextNodeId")
    @Expose
    private String nextNodeId;
    @SerializedName("DefaultButton")
    @Expose
    private Boolean defaultButton;
    @SerializedName("Hidden")
    @Expose
    private Boolean hidden;
    @SerializedName("Url")
    @Expose
    private String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getButtonName() {
        return buttonName;
    }

    public void setButtonName(String buttonName) {
        this.buttonName = buttonName;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public String getButtonType() {
        return buttonType;
    }

    public void setButtonType(String buttonType) {
        this.buttonType = buttonType;
    }

    public String getDeepLinkUrl() {
        return deepLinkUrl;
    }

    public void setDeepLinkUrl(String deepLinkUrl) {
        this.deepLinkUrl = deepLinkUrl;
    }

    public String getNextNodeId() {
        return nextNodeId;
    }

    public void setNextNodeId(String nextNodeId) {
        this.nextNodeId = nextNodeId;
    }

    public Boolean getDefaultButton() {
        return defaultButton;
    }

    public void setDefaultButton(Boolean defaultButton) {
        this.defaultButton = defaultButton;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
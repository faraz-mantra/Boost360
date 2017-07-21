package com.nowfloats.riachatsdk.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Button implements Serializable{

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
    @SerializedName("BounceTimeout")
    @Expose
    private long bounceTimeout;
    @SerializedName("VariableValue")
    @Expose
    private String variableValue;

    @SerializedName("PlaceholderText")
    @Expose
    private String placeholderText;

    @SerializedName("PrefixText")
    @Expose
    private String prefixText;

    @SerializedName("PostfixText")
    @Expose
    private String postfixText;

    @SerializedName("ConfirmInput")
    @Expose
    private boolean confirmInput;

    @SerializedName("PostToChat")
    @Expose
    private boolean postToChat;

    @SerializedName("APIResponseMatchKey")
    @Expose
    private String apiResponseMatchKey;

    @SerializedName("APIResponseMatchValue")
    @Expose
    private String apiResponseMatchValue;

    public String getApiResponseMatchKey() {
        return apiResponseMatchKey;
    }

    public void setApiResponseMatchKey(String apiResponseMatchKey) {
        this.apiResponseMatchKey = apiResponseMatchKey;
    }

    public String getApiResponseMatchValue() {
        return apiResponseMatchValue;
    }

    public void setApiResponseMatchValue(String apiResponseMatchValue) {
        this.apiResponseMatchValue = apiResponseMatchValue;
    }

    public boolean isPostToChat() {
        return postToChat;
    }

    public void setPostToChat(boolean postToChat) {
        this.postToChat = postToChat;
    }

    public Boolean getDefaultButton() {
        return defaultButton;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public boolean isConfirmInput() {
        return confirmInput;
    }

    public void setConfirmInput(boolean confirmInput) {
        this.confirmInput = confirmInput;
    }

    public String getPrefixText() {
        return prefixText;
    }

    public void setPrefixText(String prefixText) {
        this.prefixText = prefixText;
    }

    public String getPostfixText() {
        return postfixText;
    }

    public void setPostfixText(String postfixText) {
        this.postfixText = postfixText;
    }

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

    public Boolean isDefaultButton() {
        return defaultButton;
    }

    public void setDefaultButton(Boolean defaultButton) {
        this.defaultButton = defaultButton;
    }

    public Boolean isHidden() {
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

    public long getBounceTimeout() {
        return bounceTimeout;
    }

    public void setBounceTimeout(long bounceTimeout) {
        this.bounceTimeout = bounceTimeout;
    }

    public String getVariableValue() {
        return variableValue;
    }

    public void setVariableValue(String variableValue) {
        this.variableValue = variableValue;
    }

    public String getPlaceholderText() {
        return placeholderText;
    }

    public void setPlaceholderText(String placeholderText) {
        this.placeholderText = placeholderText;
    }
}
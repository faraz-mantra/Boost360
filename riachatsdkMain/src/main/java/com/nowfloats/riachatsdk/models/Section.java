package com.nowfloats.riachatsdk.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Section implements Serializable {

    @SerializedName("Text")
    @Expose
    private String text;
    @SerializedName("Id")
    @Expose
    private String id;
    @SerializedName("SectionType")
    @Expose
    private String sectionType;
    @SerializedName("DelayInMs")
    @Expose
    private Integer delayInMs;
    @SerializedName("Hidden")
    @Expose
    private Boolean hidden;
    @SerializedName("CoordinatesSet")
    @Expose
    private List<CoordinatesSet> coordinatesSet = null;
    @SerializedName("X")
    @Expose
    private X x;
    @SerializedName("Y")
    @Expose
    private Y y;
    @SerializedName("Caption")
    @Expose
    private String caption;
    @SerializedName("GraphType")
    @Expose
    private String graphType;
    @SerializedName("Url")
    @Expose
    private String url;
    @SerializedName("Title")
    @Expose
    private String title;
    @SerializedName("DurationInSeconds")
    @Expose
    private Integer durationInSeconds;
    @SerializedName("Format")
    @Expose
    private String format;
    @SerializedName("EncodingType")
    @Expose
    private String encodingType;
    @SerializedName("Downloadable")
    @Expose
    private Boolean downloadable;
    @SerializedName("Buffer")
    @Expose
    private Boolean buffer;
    @SerializedName("AnimateEmotion")
    @Expose
    private Boolean animateEmotion;
    @SerializedName("IsAuthenticationRequired")
    @Expose
    private Boolean isAuthenticationRequired;
    @SerializedName("Username")
    @Expose
    private String username;
    @SerializedName("Password")
    @Expose
    private String password;
    @SerializedName("PreFetch")
    @Expose
    private Boolean preFetch;
    @SerializedName("AltText")
    @Expose
    private String altText;
    @SerializedName("HeightInPixels")
    @Expose
    private Integer heightInPixels;
    @SerializedName("WidthInPixels")
    @Expose
    private Integer widthInPixels;
    @SerializedName("SizeInKb")
    @Expose
    private Double sizeInKb;
    @SerializedName("DurationInSec")
    @Expose
    private Integer durationInSec;
    @SerializedName("Length")
    @Expose
    private Integer otpLength;
    @SerializedName("DisplayOpenInBrowserButton")
    @Expose
    private boolean displayOpenInBrowserButton;
    @SerializedName("IsLoading")
    @Expose
    private boolean isLoading;
    @SerializedName("ShowDdate")
    @Expose
    private boolean showDate;
    @SerializedName("DateTime")
    @Expose
    private String dateTime;
    @SerializedName("Items")
    @Expose
    private List<Items> items = null;

    private int cardPos;
    private RiaCardModel cardModel;
    private boolean isAnimApplied = false;
    private boolean isFromRia = true;

    public List<Items> getItems() {
        return items;
    }

    public void setItems(List<Items> items) {
        this.items = items;
    }

    public Integer getOtpLength() {
        return otpLength;
    }

    public void setOtpLength(Integer otpLength) {
        this.otpLength = otpLength;
    }

    public RiaCardModel getCardModel() {
        return cardModel;
    }

    public void setCardModel(RiaCardModel cardModel) {
        this.cardModel = cardModel;
    }

    public boolean isAnimApplied() {
        return isAnimApplied;
    }

    public void setIsAnimApplied(boolean isAnimApplied) {
        this.isAnimApplied = isAnimApplied;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public boolean isShowDate() {
        return showDate;
    }

    public void setShowDate(boolean showDate) {
        this.showDate = showDate;
    }

    public int getCardPos() {
        return cardPos;
    }

    public void setCardPos(int cardPos) {
        this.cardPos = cardPos;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public boolean isDisplayOpenInBrowserButton() {
        return displayOpenInBrowserButton;
    }

    public void setDisplayOpenInBrowserButton(boolean displayOpenInBrowserButton) {
        this.displayOpenInBrowserButton = displayOpenInBrowserButton;
    }

    public boolean isFromRia() {
        return isFromRia;
    }

    public void setFromRia(boolean fromRia) {
        isFromRia = fromRia;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSectionType() {
        return sectionType;
    }

    public void setSectionType(String sectionType) {
        this.sectionType = sectionType;
    }

    public Integer getDelayInMs() {
        return delayInMs;
    }

    public void setDelayInMs(Integer delayInMs) {
        this.delayInMs = delayInMs;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public List<CoordinatesSet> getCoordinatesSet() {
        return coordinatesSet;
    }

    public void setCoordinatesSet(List<CoordinatesSet> coordinatesSet) {
        this.coordinatesSet = coordinatesSet;
    }

    public X getX() {
        return x;
    }

    public void setX(X x) {
        this.x = x;
    }

    public Y getY() {
        return y;
    }

    public void setY(Y y) {
        this.y = y;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getGraphType() {
        return graphType;
    }

    public void setGraphType(String graphType) {
        this.graphType = graphType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getDurationInSeconds() {
        return durationInSeconds;
    }

    public void setDurationInSeconds(Integer durationInSeconds) {
        this.durationInSeconds = durationInSeconds;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getEncodingType() {
        return encodingType;
    }

    public void setEncodingType(String encodingType) {
        this.encodingType = encodingType;
    }

    public Boolean getDownloadable() {
        return downloadable;
    }

    public void setDownloadable(Boolean downloadable) {
        this.downloadable = downloadable;
    }

    public Boolean getBuffer() {
        return buffer;
    }

    public void setBuffer(Boolean buffer) {
        this.buffer = buffer;
    }

    public Boolean getAnimateEmotion() {
        return animateEmotion;
    }

    public void setAnimateEmotion(Boolean animateEmotion) {
        this.animateEmotion = animateEmotion;
    }

    public Boolean getAuthenticationRequired() {
        return isAuthenticationRequired;
    }

    public void setAuthenticationRequired(Boolean authenticationRequired) {
        isAuthenticationRequired = authenticationRequired;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getPreFetch() {
        return preFetch;
    }

    public void setPreFetch(Boolean preFetch) {
        this.preFetch = preFetch;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }

    public Integer getHeightInPixels() {
        return heightInPixels;
    }

    public void setHeightInPixels(Integer heightInPixels) {
        this.heightInPixels = heightInPixels;
    }

    public Integer getWidthInPixels() {
        return widthInPixels;
    }

    public void setWidthInPixels(Integer widthInPixels) {
        this.widthInPixels = widthInPixels;
    }

    public Double getSizeInKb() {
        return sizeInKb;
    }

    public void setSizeInKb(Double sizeInKb) {
        this.sizeInKb = sizeInKb;
    }

    public Integer getDurationInSec() {
        return durationInSec;
    }

    public void setDurationInSec(Integer durationInSec) {
        this.durationInSec = durationInSec;
    }
}
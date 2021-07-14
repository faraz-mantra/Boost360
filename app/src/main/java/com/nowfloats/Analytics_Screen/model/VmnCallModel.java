package com.nowfloats.Analytics_Screen.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Created by Admin on 27-04-2017.
 */


public class VmnCallModel {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("applicationId")
    @Expose
    private String applicationId;
    @SerializedName("callDateTime")
    @Expose
    private String callDateTime;
    @SerializedName("callDuration")
    @Expose
    private Integer callDuration;
    @SerializedName("callRecordingUri")
    @Expose
    private String callRecordingUri;
    @SerializedName("callStatus")
    @Expose
    private String callStatus;
    @SerializedName("callerNumber")
    @Expose
    private String callerNumber;
    @SerializedName("externalTrackingId")
    @Expose
    private String externalTrackingId;
    @SerializedName("fpId")
    @Expose
    private String fpId;
    @SerializedName("viewType")
    @Expose
    private int viewType;
    @SerializedName("fpTag")
    @Expose
    private Object fpTag;
    @SerializedName("merchantActualNumber")
    @Expose
    private String merchantActualNumber;
    @SerializedName("virtualNumber")
    @Expose
    private String virtualNumber;

    private int audioPosition = 0;
    private int audioLength = 0;
    private boolean audioPlayState = false;

    public boolean isAudioPlayState() {
        return audioPlayState;
    }

    public void setAudioPlayState(boolean audioPlayState) {
        this.audioPlayState = audioPlayState;
    }

    public int getAudioLength() {
        return audioLength;
    }

    public void setAudioLength(int audioLength) {
        this.audioLength = audioLength;
    }

    public int getAudioPosition() {
        return audioPosition;
    }

    public void setAudioPosition(int audioPosition) {
        this.audioPosition = audioPosition;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getCallDateTime() {
        return callDateTime;
    }

    public void setCallDateTime(String callDateTime) {
        this.callDateTime = callDateTime;
    }

    public Integer getCallDuration() {
        return callDuration;
    }

    public void setCallDuration(Integer callDuration) {
        this.callDuration = callDuration;
    }

    public String getCallRecordingUri() {
        return callRecordingUri;
    }

    public void setCallRecordingUri(String callRecordingUri) {
        this.callRecordingUri = callRecordingUri;
    }

    public String getCallStatus() {
        return callStatus;
    }

    public void setCallStatus(String callStatus) {
        this.callStatus = callStatus;
    }

    public String getCallerNumber() {
        return callerNumber;
    }

    public void setCallerNumber(String callerNumber) {
        this.callerNumber = callerNumber;
    }

    public String getExternalTrackingId() {
        return externalTrackingId;
    }

    public void setExternalTrackingId(String externalTrackingId) {
        this.externalTrackingId = externalTrackingId;
    }

    public String getFpId() {
        return fpId;
    }

    public void setFpId(String fpId) {
        this.fpId = fpId;
    }

    public Object getFpTag() {
        return fpTag;
    }

    public void setFpTag(Object fpTag) {
        this.fpTag = fpTag;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public String getMerchantActualNumber() {
        return merchantActualNumber;
    }

    public void setMerchantActualNumber(String merchantActualNumber) {
        this.merchantActualNumber = merchantActualNumber;
    }

    public String getVirtualNumber() {
        return virtualNumber;
    }

    public void setVirtualNumber(String virtualNumber) {
        this.virtualNumber = virtualNumber;
    }

}



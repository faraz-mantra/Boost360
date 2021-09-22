package com.nowfloats.NavigationDrawer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RiaSupportModel {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Gender")
    @Expose
    private Integer gender;
    @SerializedName("Email")
    @Expose
    private String email;
    @SerializedName("PhoneNumber")
    @Expose
    private String phoneNumber;
    @SerializedName("ClientId")
    @Expose
    private String clientId;
    @SerializedName("IsActive")
    @Expose
    private Boolean isActive;
    @SerializedName("ActiveHours")
    @Expose
    private List<ActiveHour> activeHours = null;
    @SerializedName("VideoCallLink")
    @Expose
    private String videoCallLink;
    @SerializedName("ConcurrentMeetingsCount")
    @Expose
    private Integer concurrentMeetingsCount;
    @SerializedName("PartnerUsername")
    @Expose
    private String partnerUsername;
    @SerializedName("SupportTicketConfig")
    @Expose
    private Object supportTicketConfig;
    @SerializedName("CreatedOn")
    @Expose
    private String createdOn;
    @SerializedName("UpdatedOn")
    @Expose
    private String updatedOn;
    @SerializedName("Type")
    @Expose
    private String type;
    @SerializedName("City")
    @Expose
    private Object city;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public List<ActiveHour> getActiveHours() {
        return activeHours;
    }

    public void setActiveHours(List<ActiveHour> activeHours) {
        this.activeHours = activeHours;
    }

    public String getVideoCallLink() {
        return videoCallLink;
    }

    public void setVideoCallLink(String videoCallLink) {
        this.videoCallLink = videoCallLink;
    }

    public Integer getConcurrentMeetingsCount() {
        return concurrentMeetingsCount;
    }

    public void setConcurrentMeetingsCount(Integer concurrentMeetingsCount) {
        this.concurrentMeetingsCount = concurrentMeetingsCount;
    }

    public String getPartnerUsername() {
        return partnerUsername;
    }

    public void setPartnerUsername(String partnerUsername) {
        this.partnerUsername = partnerUsername;
    }

    public Object getSupportTicketConfig() {
        return supportTicketConfig;
    }

    public void setSupportTicketConfig(Object supportTicketConfig) {
        this.supportTicketConfig = supportTicketConfig;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getCity() {
        return city;
    }

    public void setCity(Object city) {
        this.city = city;
    }

    public static class ActiveHour {

        @SerializedName("From")
        @Expose
        private String from;
        @SerializedName("To")
        @Expose
        private String to;
        @SerializedName("WeekDay")
        @Expose
        private Integer weekDay;

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public Integer getWeekDay() {
            return weekDay;
        }

        public void setWeekDay(Integer weekDay) {
            this.weekDay = weekDay;
        }

    }

}




package com.nowfloats.sync.model;

import com.nowfloats.NotificationCenter.Model.Notification_data;

import java.util.ArrayList;

/**
 * Created by RAJA on 20-06-2016.
 */
public class Alerts {
    public ArrayList<Notification_data> NotificationData = new ArrayList<>();
    private String ID;
    private String channel;
    private String createdOn;
    private String imageUrl;
    private String message;
    private String notificationImageUrl;
    private String notificationStatus;
    private int notificationType;
    private String sendOn;
    private String isRead;
    private String isTargetAchieved;

    public String getID() {
        return ID;
    }

    public Alerts setID(String ID) {
        this.ID = ID;
        return this;
    }

    public ArrayList<Notification_data> getNotificationData() {
        return NotificationData;
    }

    public Alerts setNotificationData(ArrayList<Notification_data> notificationData) {
        for (Notification_data notification : notificationData) {
            NotificationData.add(notification);
        }
        return this;
    }

    public String getChannel() {
        return channel;
    }

    public Alerts setChannel(String channel) {
        this.channel = channel;
        return this;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public Alerts setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Alerts setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Alerts setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getNotificationImageUrl() {
        return notificationImageUrl;
    }

    public Alerts setNotificationImageUrl(String notificationImageUrl) {
        this.notificationImageUrl = notificationImageUrl;
        return this;
    }

    public String getNotificationStatus() {
        return notificationStatus;
    }

    public Alerts setNotificationStatus(String notificationStatus) {
        this.notificationStatus = notificationStatus;
        return this;
    }

    public int getNotificationType() {
        return notificationType;
    }

    public Alerts setNotificationType(int notificationType) {
        this.notificationType = notificationType;
        return this;
    }

    public String getSendOn() {
        return sendOn;
    }

    public Alerts setSendOn(String sendOn) {
        this.sendOn = sendOn;
        return this;
    }

    public String getIsRead() {
        return isRead;
    }

    public Alerts setIsRead(String isRead) {
        this.isRead = isRead;
        return this;
    }

    public String getIsTargetAchieved() {
        return isTargetAchieved;
    }

    public Alerts setIsTargetAchieved(String isTargetAchieved) {
        this.isTargetAchieved = isTargetAchieved;
        return this;
    }
}

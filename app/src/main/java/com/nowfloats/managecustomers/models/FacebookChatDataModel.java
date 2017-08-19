package com.nowfloats.managecustomers.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Admin on 17-08-2017.
 */

public class FacebookChatDataModel {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("nowfloats_id")
    @Expose
    private String nowfloatsId;
    @SerializedName("user_data")
    @Expose
    private UserData userData;
    @SerializedName("data")
    @Expose
    private List<Datum> data = null;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNowfloatsId() {
        return nowfloatsId;
    }

    public void setNowfloatsId(String nowfloatsId) {
        this.nowfloatsId = nowfloatsId;
    }

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    public static class Datum {

        @SerializedName("message")
        @Expose
        private Message message;
        @SerializedName("sender")
        @Expose
        private String sender;
        @SerializedName("timestamp")
        @Expose
        private Long timestamp;

        private boolean showCornerBackground;

        public boolean isShowCornerBackground() {
            return showCornerBackground;
        }

        public void setShowCornerBackground(boolean showCornerBackground) {
            this.showCornerBackground = showCornerBackground;
        }

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }

        public String getSender() {
            return sender;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }

        public Long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
        }

    }

    public static class Message {

        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("data")
        @Expose
        private Data data;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

    }

    public static class Data {

        @SerializedName("text")
        @Expose
        private String text;
        @SerializedName("url")
        @Expose
        private String url;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

    }
    public static class UserData {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("profile_pic")
        @Expose
        private String profilePic;
        @SerializedName("last_name")
        @Expose
        private String lastName;
        @SerializedName("first_name")
        @Expose
        private String firstName;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getProfilePic() {
            return profilePic;
        }

        public void setProfilePic(String profilePic) {
            this.profilePic = profilePic;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

    }

}

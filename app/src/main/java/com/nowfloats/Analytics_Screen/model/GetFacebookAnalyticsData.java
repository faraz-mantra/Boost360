package com.nowfloats.Analytics_Screen.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Abhi on 12/6/2016.
 */

public class GetFacebookAnalyticsData {

        @SerializedName("message")
        @Expose
        private String message;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("nowfloats_id")
        @Expose
        private String nowfloatsId;
        @SerializedName("data")
        @Expose
        private List<Datum> data = null;
        @SerializedName("number_of_updates")
        @Expose
        private Integer numberOfUpdates;
        @SerializedName("pictureData")
        @Expose
        private PictureData pictureData;

        /**
         *
         * @return
         * The message
         */
        public String getMessage() {
            return message;
        }

        /**
         *
         * @param message
         * The message
         */
        public void setMessage(String message) {
            this.message = message;
        }

        /**
         *
         * @return
         * The status
         */
        public String getStatus() {
            return status;
        }

        /**
         *
         * @param status
         * The status
         */
        public void setStatus(String status) {
            this.status = status;
        }

        /**
         *
         * @return
         * The nowfloatsId
         */
        public String getNowfloatsId() {
            return nowfloatsId;
        }

        /**
         *
         * @param nowfloatsId
         * The nowfloats_id
         */
        public void setNowfloatsId(String nowfloatsId) {
            this.nowfloatsId = nowfloatsId;
        }

        /**
         *
         * @return
         * The data
         */
        public List<Datum> getData() {
            return data;
        }

        /**
         *
         * @param data
         * The data
         */
        public void setData(List<Datum> data) {
            this.data = data;
        }

        /**
         *
         * @return
         * The numberOfUpdates
         */
        public Integer getNumberOfUpdates() {
            return numberOfUpdates;
        }

        /**
         *
         * @param numberOfUpdates
         * The number_of_updates
         */
        public void setNumberOfUpdates(Integer numberOfUpdates) {
            this.numberOfUpdates = numberOfUpdates;
        }

        /**
         *
         * @return
         * The pictureData
         */
        public PictureData getPictureData() {
            return pictureData;
        }

        /**
         *
         * @param pictureData
         * The pictureData
         */
        public void setPictureData(PictureData pictureData) {
            this.pictureData = pictureData;
        }
    public class Data {

        @SerializedName("is_silhouette")
        @Expose
        private Boolean isSilhouette;
        @SerializedName("url")
        @Expose
        private String url;

        /**
         *
         * @return
         * The isSilhouette
         */
        public Boolean getIsSilhouette() {
            return isSilhouette;
        }

        /**
         *
         * @param isSilhouette
         * The is_silhouette
         */
        public void setIsSilhouette(Boolean isSilhouette) {
            this.isSilhouette = isSilhouette;
        }

        /**
         *
         * @return
         * The url
         */
        public String getUrl() {
            return url;
        }

        /**
         *
         * @param url
         * The url
         */
        public void setUrl(String url) {
            this.url = url;
        }

    }
    public class Datum {

        @SerializedName("identifier")
        @Expose
        private String identifier;
        @SerializedName("values")
        @Expose
        private Values values;

        /**
         *
         * @return
         * The identifier
         */
        public String getIdentifier() {
            return identifier;
        }

        /**
         *
         * @param identifier
         * The identifier
         */
        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        /**
         *
         * @return
         * The values
         */
        public Values getValues() {
            return values;
        }

        /**
         *
         * @param values
         * The values
         */
        public void setValues(Values values) {
            this.values = values;
        }

    }

    public class Picture {

        @SerializedName("data")
        @Expose
        private Data data;

        /**
         *
         * @return
         * The data
         */
        public Data getData() {
            return data;
        }

        /**
         *
         * @param data
         * The data
         */
        public void setData(Data data) {
            this.data = data;
        }

    }

    public class PictureData {

        @SerializedName("link")
        @Expose
        private String link;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("picture")
        @Expose
        private Picture picture;
        @SerializedName("id")
        @Expose
        private String id;

        /**
         *
         * @return
         * The link
         */
        public String getLink() {
            return link;
        }

        /**
         *
         * @param link
         * The link
         */
        public void setLink(String link) {
            this.link = link;
        }

        /**
         *
         * @return
         * The name
         */
        public String getName() {
            return name;
        }

        /**
         *
         * @param name
         * The name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         *
         * @return
         * The picture
         */
        public Picture getPicture() {
            return picture;
        }

        /**
         *
         * @param picture
         * The picture
         */
        public void setPicture(Picture picture) {
            this.picture = picture;
        }

        /**
         *
         * @return
         * The id
         */
        public String getId() {
            return id;
        }

        /**
         *
         * @param id
         * The id
         */
        public void setId(String id) {
            this.id = id;
        }

    }

    public class Values {

        @SerializedName("reactions")
        @Expose
        private Integer reactions;
        @SerializedName("post_impressions")
        @Expose
        private Integer postImpressions;
        @SerializedName("post_consumptions")
        @Expose
        private Integer postConsumptions;
        @SerializedName("post_engaged_users")
        @Expose
        private Integer postEngagedUsers;

        /**
         *
         * @return
         * The reactions
         */
        public Integer getReactions() {
            return reactions;
        }

        /**
         *
         * @param reactions
         * The reactions
         */
        public void setReactions(Integer reactions) {
            this.reactions = reactions;
        }

        /**
         *
         * @return
         * The postImpressions
         */
        public Integer getPostImpressions() {
            return postImpressions;
        }

        /**
         *
         * @param postImpressions
         * The post_impressions
         */
        public void setPostImpressions(Integer postImpressions) {
            this.postImpressions = postImpressions;
        }

        /**
         *
         * @return
         * The postConsumptions
         */
        public Integer getPostConsumptions() {
            return postConsumptions;
        }

        /**
         *
         * @param postConsumptions
         * The post_consumptions
         */
        public void setPostConsumptions(Integer postConsumptions) {
            this.postConsumptions = postConsumptions;
        }

        /**
         *
         * @return
         * The postEngagedUsers
         */
        public Integer getPostEngagedUsers() {
            return postEngagedUsers;
        }

        /**
         *
         * @param postEngagedUsers
         * The post_engaged_users
         */
        public void setPostEngagedUsers(Integer postEngagedUsers) {
            this.postEngagedUsers = postEngagedUsers;
        }

    }

}

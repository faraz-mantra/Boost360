package com.nowfloats.NavigationDrawer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Admin on 01-02-2017.
 */

public class StoreAndGoModel {
    List<PublishStatusModel> publishStatusModelList;

    public List<PublishStatusModel> getPublishStatusModelList() {
        return publishStatusModelList;
    }

    public void setPublishStatusModelList(List<PublishStatusModel> publishStatusModelList) {
        this.publishStatusModelList = publishStatusModelList;
    }

    public static class PublishStatusModel {
        @SerializedName("Key")
        @Expose
        private String key;
        @SerializedName("Value")
        @Expose
        private String value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class ScreenShotsModel {
        @SerializedName("Key")
        @Expose
        private String key;
        @SerializedName("Value")
        @Expose
        private List<String> value = null;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public List<String> getValue() {
            return value;
        }

        public void setValue(List<String> value) {
            this.value = value;
        }
    }
}

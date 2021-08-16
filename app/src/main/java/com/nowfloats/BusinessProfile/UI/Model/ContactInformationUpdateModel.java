package com.nowfloats.BusinessProfile.UI.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by NowFloatsDev on 26/05/2015.
 */
public class ContactInformationUpdateModel {

    @SerializedName("clientId")
    @Expose
    private String clientId;
    @SerializedName("fpTag")
    @Expose
    private String fpTag;
    @SerializedName("updates")
    @Expose
    private List<Update> updates = null;

    /**
     * @return The clientId
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * @param clientId The clientId
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * @return The fpTag
     */
    public String getFpTag() {
        return fpTag;
    }

    /**
     * @param fpTag The fpTag
     */
    public void setFpTag(String fpTag) {
        this.fpTag = fpTag;
    }

    /**
     * @return The updates
     */
    public List<Update> getUpdates() {
        return updates;
    }

    /**
     * @param updates The updates
     */
    public void setUpdates(List<Update> updates) {
        this.updates = updates;
    }

    public static class Update {

        @SerializedName("key")
        @Expose
        private String key;
        @SerializedName("value")
        @Expose
        private String value;

        public Update(String key, String value) {
            this.key = key;
            this.value = value;
        }

        /**
         * @return The key
         */
        public String getKey() {
            return key;
        }

        /**
         * @param key The key
         */
        public void setKey(String key) {
            this.key = key;
        }

        /**
         * @return The value
         */
        public String getValue() {
            return value;
        }

        /**
         * @param value The value
         */
        public void setValue(String value) {
            this.value = value;
        }

    }
}

package com.nowfloats.Analytics_Screen.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abhi on 12/2/2016.
 */

public class NfxGetTokensResponse  {

        @SerializedName("message")
        @Expose
        private String message;
        @SerializedName("nowfloats_id")
        @Expose
        private String nowfloatsId;
        @SerializedName("callLogTimeInterval")
        @Expose
        private String callLogTimeInterval;
        @SerializedName("smsRegex")
        @Expose
        private List<String> smsRegex;
        @SerializedName("NFXAccessTokens")
        @Expose
        private List<NFXAccessToken> nFXAccessTokens = new ArrayList<NFXAccessToken>();

        public List<String> getSmsRegex() {
            return smsRegex;
        }

    public String getCallLogTimeInterval() {
        return callLogTimeInterval;
    }

    public void setCallLogTimeInterval(String callLogTimeInterval) {
        this.callLogTimeInterval = callLogTimeInterval;
    }

    public void setSmsRegex(List<String> smsRegex) {
            this.smsRegex = smsRegex;
        }

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
         * The nFXAccessTokens
         */
        public List<NFXAccessToken> getNFXAccessTokens() {
            return nFXAccessTokens;
        }

        /**
         *
         * @param nFXAccessTokens
         * The NFXAccessTokens
         */
        public void setNFXAccessTokens(List<NFXAccessToken> nFXAccessTokens) {
            this.nFXAccessTokens = nFXAccessTokens;
        }
    public static class NFXAccessToken {

        @SerializedName("Type")
        @Expose
        private String type;
        @SerializedName("Status")
        @Expose
        private String status;
        @SerializedName("UserAccessTokenKey")
        @Expose
        private String userAccessTokenKey;
        @SerializedName("UserAccessTokenSecret")
        @Expose
        private String userAccessTokenSecret;
        @SerializedName("UserAccountId")
        @Expose
        private String userAccountId;
        @SerializedName("UserAccountName")
        @Expose
        private String userAccountName;

        /**
         *
         * @return
         * The type
         */
        public String getType() {
            return type;
        }

        /**
         *
         * @param type
         * The Type
         */
        public void setType(String type) {
            this.type = type;
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
         * The Status
         */
        public void setStatus(String status) {
            this.status = status;
        }

        /**
         *
         * @return
         * The userAccessTokenKey
         */
        public String getUserAccessTokenKey() {
            return userAccessTokenKey;
        }

        /**
         *
         * @param userAccessTokenKey
         * The UserAccessTokenKey
         */
        public void setUserAccessTokenKey(String userAccessTokenKey) {
            this.userAccessTokenKey = userAccessTokenKey;
        }

        /**
         *
         * @return
         * The userAccessTokenSecret
         */
        public String getUserAccessTokenSecret() {
            return userAccessTokenSecret;
        }

        /**
         *
         * @param userAccessTokenSecret
         * The UserAccessTokenSecret
         */
        public void setUserAccessTokenSecret(String userAccessTokenSecret) {
            this.userAccessTokenSecret = userAccessTokenSecret;
        }

        /**
         *
         * @return
         * The userAccountId
         */
        public String getUserAccountId() {
            return userAccountId;
        }

        /**
         *
         * @param userAccountId
         * The UserAccountId
         */
        public void setUserAccountId(String userAccountId) {
            this.userAccountId = userAccountId;
        }

        /**
         *
         * @return
         * The userAccountName
         */
        public String getUserAccountName() {
            return userAccountName;
        }

        /**
         *
         * @param userAccountName
         * The UserAccountName
         */
        public void setUserAccountName(String userAccountName) {
            this.userAccountName = userAccountName;
        }

    }
}

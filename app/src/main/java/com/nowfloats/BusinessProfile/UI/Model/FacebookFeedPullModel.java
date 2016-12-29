package com.nowfloats.BusinessProfile.UI.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Abhi on 12/9/2016.
 */

public class FacebookFeedPullModel {
        public class Registration {
                @SerializedName("AutoPublish")
                @Expose
                private Boolean autoPublish;
                @SerializedName("ClientId")
                @Expose
                private String clientId;
                @SerializedName("Count")
                @Expose
                private Integer count;
                @SerializedName("FacebookPageName")
                @Expose
                private String facebookPageName;
                @SerializedName("IsEnterpriseUpdate")
                @Expose
                private Boolean isEnterpriseUpdate;
                @SerializedName("Tag")
                @Expose
                private String tag;

                /**
                 * @return The autoPublish
                 */
                public Boolean getAutoPublish() {
                        return autoPublish;
                }

                /**
                 * @param autoPublish The AutoPublish
                 */
                public void setAutoPublish(Boolean autoPublish) {
                        this.autoPublish = autoPublish;
                }

                /**
                 * @return The clientId
                 */
                public String getClientId() {
                        return clientId;
                }

                /**
                 * @param clientId The ClientId
                 */
                public void setClientId(String clientId) {
                        this.clientId = clientId;
                }

                /**
                 * @return The count
                 */
                public Integer getCount() {
                        return count;
                }

                /**
                 * @param count The Count
                 */
                public void setCount(Integer count) {
                        this.count = count;
                }

                /**
                 * @return The facebookPageName
                 */
                public String getFacebookPageName() {
                        return facebookPageName;
                }

                /**
                 * @param facebookPageName The FacebookPageName
                 */
                public void setFacebookPageName(String facebookPageName) {
                        this.facebookPageName = facebookPageName;
                }

                /**
                 * @return The isEnterpriseUpdate
                 */
                public Boolean getIsEnterpriseUpdate() {
                        return isEnterpriseUpdate;
                }

                /**
                 * @param isEnterpriseUpdate The IsEnterpriseUpdate
                 */
                public void setIsEnterpriseUpdate(Boolean isEnterpriseUpdate) {
                        this.isEnterpriseUpdate = isEnterpriseUpdate;
                }

                /**
                 * @return The tag
                 */
                public String getTag() {
                        return tag;
                }

                /**
                 * @param tag The Tag
                 */
                public void setTag(String tag) {
                        this.tag = tag;
                }
        }
        public class Update {

                @SerializedName("autoPublish")
                @Expose
                private Boolean autoPublish;
                @SerializedName("clientId")
                @Expose
                private String clientId;
                @SerializedName("facebookPageName")
                @Expose
                private String facebookPageName;
                @SerializedName("fpId")
                @Expose
                private String fpId;

                /**
                 *
                 * @return
                 * The autoPublish
                 */
                public Boolean getAutoPublish() {
                        return autoPublish;
                }

                /**
                 *
                 * @param autoPublish
                 * The autoPublish
                 */
                public void setAutoPublish(Boolean autoPublish) {
                        this.autoPublish = autoPublish;
                }

                /**
                 *
                 * @return
                 * The clientId
                 */
                public String getClientId() {
                        return clientId;
                }

                /**
                 *
                 * @param clientId
                 * The clientId
                 */
                public void setClientId(String clientId) {
                        this.clientId = clientId;
                }

                /**
                 *
                 * @return
                 * The facebookPageName
                 */
                public String getFacebookPageName() {
                        return facebookPageName;
                }

                /**
                 *
                 * @param facebookPageName
                 * The facebookPageName
                 */
                public void setFacebookPageName(String facebookPageName) {
                        this.facebookPageName = facebookPageName;
                }

                /**
                 *
                 * @return
                 * The fpId
                 */
                public String getFpId() {
                        return fpId;
                }

                /**
                 *
                 * @param fpId
                 * The fpId
                 */
                public void setFpId(String fpId) {
                        this.fpId = fpId;
                }

        }
}

package com.nowfloats.Analytics_Screen.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Created by Admin on 17-04-2017.
 */


public class AddSubscriberModel {
    @SerializedName("clientId")
    @Expose
    private String clientId;
    @SerializedName("countryCode")
    @Expose
    private String countryCode;
    @SerializedName("fpTag")
    @Expose
    private String fpTag;
    @SerializedName("userContact")
    @Expose
    private String userContact;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getFpTag() {
        return fpTag;
    }

    public void setFpTag(String fpTag) {
        this.fpTag = fpTag;
    }

    public String getUserContact() {
        return userContact;
    }

    public void setUserContact(String userContact) {
        this.userContact = userContact;
    }
}

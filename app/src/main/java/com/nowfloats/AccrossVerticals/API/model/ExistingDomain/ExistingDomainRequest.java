package com.nowfloats.AccrossVerticals.API.model.ExistingDomain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ExistingDomainRequest {

    @SerializedName("ClientId")
    @Expose
    private String clientId;
    @SerializedName("FPTag")
    @Expose
    private String fPTag;
    @SerializedName("Subject")
    @Expose
    private String subject;
    @SerializedName("Mesg")
    @Expose
    private String mesg;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getFPTag() {
        return fPTag;
    }

    public void setFPTag(String fPTag) {
        this.fPTag = fPTag;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMesg() {
        return mesg;
    }

    public void setMesg(String mesg) {
        this.mesg = mesg;
    }

}
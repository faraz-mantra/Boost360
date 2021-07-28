package com.nowfloats.BusinessProfile.UI.Model;

import com.google.gson.annotations.SerializedName;

public class WhatsAppBusinessNumberModel {

    @SerializedName("_id")
    String id;

    @SerializedName("active_whatsapp_number")
    String whatsAppNumber;

    //@SerializedName("IsArchived")
    //boolean isArchived;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWhatsAppNumber() {
        return this.whatsAppNumber;
    }

    public void setWhatsAppNumber(String whatsAppNumber) {
        this.whatsAppNumber = whatsAppNumber;
    }

    /*public void setIsArchived(boolean isArchived)
    {
        this.isArchived = isArchived;
    }

    public boolean getIsArchived()
    {
        return this.isArchived;
    }*/
}

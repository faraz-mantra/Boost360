package com.nowfloats.Business_Enquiries.Model;

/**
 * Created by guru on 26-05-2015.
 */
public class Entity_model {
    public String CreatedDate;
    public String Customerstatus;
    public String MerchantId;
    public String MerchatResponsestatus;
    public String Message;
    public String Phone;

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    public String getCustomerstatus() {
        return Customerstatus;
    }

    public void setCustomerstatus(String customerstatus) {
        Customerstatus = customerstatus;
    }

    public String getMerchantId() {
        return MerchantId;
    }

    public void setMerchantId(String merchantId) {
        MerchantId = merchantId;
    }

    public String getMerchatResponsestatus() {
        return MerchatResponsestatus;
    }

    public void setMerchatResponsestatus(String merchatResponsestatus) {
        MerchatResponsestatus = merchatResponsestatus;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }
}

package com.nowfloats.manageinventory.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by NowFloats on 17-08-2017.
 */

public class MerchantProfileModel implements Parcelable {
    public static final Creator<MerchantProfileModel> CREATOR = new Creator<MerchantProfileModel>() {
        @Override
        public MerchantProfileModel createFromParcel(Parcel in) {
            return new MerchantProfileModel(in);
        }

        @Override
        public MerchantProfileModel[] newArray(int size) {
            return new MerchantProfileModel[size];
        }
    };
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("merchant_name")
    @Expose
    private String merchantName;
    @SerializedName("bank_name")
    @Expose
    private String bankName;
    @SerializedName("bank_acc_num")
    @Expose
    private String bankAccNum;
    @SerializedName("ifsc_code")
    @Expose
    private String ifscCode;
    @SerializedName("bank_account_type")
    @Expose
    private String bankAccountType;
    @SerializedName("pan_card")
    @Expose
    private String panCard;
    @SerializedName("gstn")
    @Expose
    private String gstn;
    @SerializedName("merchant_id")
    @Expose
    private String merchantId;
    @SerializedName("fpTag")
    @Expose
    private String fpTag;
    @SerializedName("phone_number")
    @Expose
    private String phoneNumber;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("delivery_type")
    @Expose
    private Integer deliveryType;
    @SerializedName("payment_type")
    @Expose
    private Integer paymentType;
    @SerializedName("UserId")
    @Expose
    private String userId;
    @SerializedName("ActionId")
    @Expose
    private String actionId;
    @SerializedName("WebsiteId")
    @Expose
    private String websiteId;
    @SerializedName("CreatedOn")
    @Expose
    private String createdOn;
    @SerializedName("UpdatedOn")
    @Expose
    private String updatedOn;
    @SerializedName("IsArchived")
    @Expose
    private Boolean isArchived;
    @SerializedName("applicable_txn_charge")
    @Expose
    private Double applicableTxnCharge;

    public MerchantProfileModel() {

    }

    protected MerchantProfileModel(Parcel in) {
        id = in.readString();
        merchantName = in.readString();
        bankName = in.readString();
        bankAccNum = in.readString();
        ifscCode = in.readString();
        bankAccountType = in.readString();
        panCard = in.readString();
        gstn = in.readString();
        merchantId = in.readString();
        fpTag = in.readString();
        phoneNumber = in.readString();
        email = in.readString();
        userId = in.readString();
        actionId = in.readString();
        websiteId = in.readString();
        createdOn = in.readString();
        updatedOn = in.readString();
        applicableTxnCharge = in.readDouble();
    }

    public Boolean getArchived() {
        return isArchived;
    }

    public void setArchived(Boolean archived) {
        isArchived = archived;
    }

    public Double getApplicableTxnCharge() {
        return applicableTxnCharge;
    }

    public void setApplicableTxnCharge(Double applicableTxnCharge) {
        this.applicableTxnCharge = applicableTxnCharge;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankAccNum() {
        return bankAccNum;
    }

    public void setBankAccNum(String bankAccNum) {
        this.bankAccNum = bankAccNum;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public String getBankAccountType() {
        return bankAccountType;
    }

    public void setBankAccountType(String bankAccountType) {
        this.bankAccountType = bankAccountType;
    }

    public String getPanCard() {
        return panCard;
    }

    public void setPanCard(String panCard) {
        this.panCard = panCard;
    }

    public String getGstn() {
        return gstn;
    }

    public void setGstn(String gstn) {
        this.gstn = gstn;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getFpTag() {
        return fpTag;
    }

    public void setFpTag(String fpTag) {
        this.fpTag = fpTag;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(Integer deliveryType) {
        this.deliveryType = deliveryType;
    }

    public Integer getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(Integer paymentType) {
        this.paymentType = paymentType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public String getWebsiteId() {
        return websiteId;
    }

    public void setWebsiteId(String websiteId) {
        this.websiteId = websiteId;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }

    public Boolean getIsArchived() {
        return isArchived;
    }

    public void setIsArchived(Boolean isArchived) {
        this.isArchived = isArchived;
    }

    @Override
    public int describeContents() {
        return 17;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(merchantName);
        dest.writeString(bankName);
        dest.writeString(bankAccNum);
        dest.writeString(ifscCode);
        dest.writeString(bankAccountType);
        dest.writeString(panCard);
        dest.writeString(gstn);
        dest.writeString(merchantId);
        dest.writeString(fpTag);
        dest.writeString(phoneNumber);
        dest.writeString(email);
        dest.writeString(userId);
        dest.writeString(actionId);
        dest.writeString(websiteId);
        dest.writeString(createdOn);
        dest.writeString(updatedOn);
        dest.writeDouble(applicableTxnCharge);
    }
}

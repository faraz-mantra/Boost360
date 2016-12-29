package com.nowfloats.Store.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by NowFloats on 30-11-2016.
 */

public class PurchaseDetail {

    @SerializedName("ClientId")
    @Expose
    private String clientId;
    @SerializedName("DurationInMnths")
    @Expose
    private Integer durationInMnths;
    @SerializedName("FPId")
    @Expose
    private String fPId;
    @SerializedName("MRP")
    @Expose
    private Double mRP;
    @SerializedName("MRPCurrencyCode")
    @Expose
    private String mRPCurrencyCode;
    @SerializedName("PackageId")
    @Expose
    private String packageId;
    @SerializedName("ToBeActivatedOn")
    @Expose
    private String toBeActivatedOn;

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
     * The ClientId
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     *
     * @return
     * The durationInMnths
     */
    public Integer getDurationInMnths() {
        return durationInMnths;
    }

    /**
     *
     * @param durationInMnths
     * The DurationInMnths
     */
    public void setDurationInMnths(Integer durationInMnths) {
        this.durationInMnths = durationInMnths;
    }

    /**
     *
     * @return
     * The fPId
     */
    public String getFPId() {
        return fPId;
    }

    /**
     *
     * @param fPId
     * The FPId
     */
    public void setFPId(String fPId) {
        this.fPId = fPId;
    }

    /**
     *
     * @return
     * The mRP
     */
    public Double getMRP() {
        return mRP;
    }

    /**
     *
     * @param mRP
     * The MRP
     */
    public void setMRP(Double mRP) {
        this.mRP = mRP;
    }

    /**
     *
     * @return
     * The mRPCurrencyCode
     */
    public String getMRPCurrencyCode() {
        return mRPCurrencyCode;
    }

    /**
     *
     * @param mRPCurrencyCode
     * The MRPCurrencyCode
     */
    public void setMRPCurrencyCode(String mRPCurrencyCode) {
        this.mRPCurrencyCode = mRPCurrencyCode;
    }

    /**
     *
     * @return
     * The packageId
     */
    public String getPackageId() {
        return packageId;
    }

    /**
     *
     * @param packageId
     * The PackageId
     */
    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    /**
     *
     * @return
     * The toBeActivatedOn
     */
    public String getToBeActivatedOn() {
        return toBeActivatedOn;
    }

    /**
     *
     * @param toBeActivatedOn
     * The ToBeActivatedOn
     */
    public void setToBeActivatedOn(String toBeActivatedOn) {
        this.toBeActivatedOn = toBeActivatedOn;
    }

}
package com.nowfloats.Store.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nowfloats.Store.Discount;

import java.util.List;

/**
 * Created by NowFloats on 30-11-2016.
 */

public class PurchaseDetail {

    @SerializedName("BasePrice")
    @Expose
    private Double basePrice;
    @SerializedName("ClientId")
    @Expose
    private String clientId;
    @SerializedName("Discount")
    @Expose
    private Discount discount;
    @SerializedName("DurationInMnths")
    @Expose
    private Double durationInMnths;
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
    @SerializedName("PackageName")
    @Expose
    private String packageName;
    @SerializedName("TaxDetails")
    @Expose
    private List<TaxDetail> taxDetails = null;

    /**
     * @return The basePrice
     */
    public Double getBasePrice() {
        return basePrice;
    }

    /**
     * @param basePrice The BasePrice
     */
    public void setBasePrice(Double basePrice) {
        this.basePrice = basePrice;
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

    public Discount getDiscount() {
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    /**
     * @return The durationInMnths
     */
    public Double getDurationInMnths() {
        return durationInMnths;
    }

    /**
     * @param durationInMnths The DurationInMnths
     */
    public void setDurationInMnths(Double durationInMnths) {
        this.durationInMnths = durationInMnths;
    }

    /**
     * @return The fPId
     */
    public String getFPId() {
        return fPId;
    }

    /**
     * @param fPId The FPId
     */
    public void setFPId(String fPId) {
        this.fPId = fPId;
    }

    /**
     * @return The mRP
     */
    public Double getMRP() {
        return mRP;
    }

    /**
     * @param mRP The MRP
     */
    public void setMRP(Double mRP) {
        this.mRP = mRP;
    }

    /**
     * @return The mRPCurrencyCode
     */
    public String getMRPCurrencyCode() {
        return mRPCurrencyCode;
    }

    /**
     * @param mRPCurrencyCode The MRPCurrencyCode
     */
    public void setMRPCurrencyCode(String mRPCurrencyCode) {
        this.mRPCurrencyCode = mRPCurrencyCode;
    }

    /**
     * @return The packageId
     */
    public String getPackageId() {
        return packageId;
    }

    /**
     * @param packageId The PackageId
     */
    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    /**
     * @return The packageName
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * @param packageName The PackageName
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    /**
     * @return The taxDetails
     */
    public List<TaxDetail> getTaxDetails() {
        return taxDetails;
    }

    /**
     * @param taxDetails The TaxDetails
     */
    public void setTaxDetails(List<TaxDetail> taxDetails) {
        this.taxDetails = taxDetails;
    }

}
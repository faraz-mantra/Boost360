package com.nowfloats.Store.Model;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SendDraftInvoiceModel {

    @SerializedName("fpUserProfileId")
    @Expose
    private String fpUserProfileId;
    @SerializedName("opc")
    @Expose
    private String opc;
    @SerializedName("purchaseDetails")
    @Expose
    private List<PurchaseDetail> purchaseDetails = new ArrayList<PurchaseDetail>();

    /**
     *
     * @return
     * The fpUserProfileId
     */
    public String getFpUserProfileId() {
        return fpUserProfileId;
    }

    /**
     *
     * @param fpUserProfileId
     * The fpUserProfileId
     */
    public void setFpUserProfileId(String fpUserProfileId) {
        this.fpUserProfileId = fpUserProfileId;
    }

    /**
     *
     * @return
     * The opc
     */
    public String getOpc() {
        return opc;
    }

    /**
     *
     * @param opc
     * The opc
     */
    public void setOpc(String opc) {
        this.opc = opc;
    }

    /**
     *
     * @return
     * The purchaseDetails
     */
    public List<PurchaseDetail> getPurchaseDetails() {
        return purchaseDetails;
    }

    /**
     *
     * @param purchaseDetails
     * The purchaseDetails
     */
    public void setPurchaseDetails(List<PurchaseDetail> purchaseDetails) {
        this.purchaseDetails = purchaseDetails;
    }




}
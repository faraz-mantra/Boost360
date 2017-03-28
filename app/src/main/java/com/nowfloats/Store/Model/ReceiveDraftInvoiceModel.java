package com.nowfloats.Store.Model;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReceiveDraftInvoiceModel {

    @SerializedName("ClientId")
    @Expose
    private String clientId;
    @SerializedName("CreatedOn")
    @Expose
    private String createdOn;
    @SerializedName("FPUserProfileId")
    @Expose
    private String fPUserProfileId;
    @SerializedName("InvoiceId")
    @Expose
    private String invoiceId;
    @SerializedName("PaymentRequestId")
    @Expose
    private String paymentRequestId;
    @SerializedName("PurchaseDetails")
    @Expose
    private List<PurchaseDetail> purchaseDetails = new ArrayList<PurchaseDetail>();
    @SerializedName("Status")
    @Expose
    private Integer status;
    @SerializedName("TPCUri")
    @Expose
    private String tPCUri;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("OPCDetails")
    @Expose
    private ArrayList<KeyValuePair> opcDetails;
    @SerializedName("TanNumber")
    @Expose
    private String tanNumber;
    @SerializedName("TdsAmount")
    @Expose
    private double tdsAmount;
    @SerializedName("TotalPayableAmount")
    @Expose
    private String totalPayableAmount;

    public ArrayList<KeyValuePair> getOpcDetails() {
        return opcDetails;
    }

    public void setOpcDetails(ArrayList<KeyValuePair> opcDetails) {
        this.opcDetails = opcDetails;
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
     * The ClientId
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     *
     * @return
     * The createdOn
     */
    public String getCreatedOn() {
        return createdOn;
    }

    /**
     *
     * @param createdOn
     * The CreatedOn
     */
    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    /**
     *
     * @return
     * The fPUserProfileId
     */
    public String getFPUserProfileId() {
        return fPUserProfileId;
    }

    /**
     *
     * @param fPUserProfileId
     * The FPUserProfileId
     */
    public void setFPUserProfileId(String fPUserProfileId) {
        this.fPUserProfileId = fPUserProfileId;
    }

    /**
     *
     * @return
     * The invoiceId
     */
    public String getInvoiceId() {
        return invoiceId;
    }

    /**
     *
     * @param invoiceId
     * The InvoiceId
     */
    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    /**
     *
     * @return
     * The paymentRequestId
     */
    public String getPaymentRequestId() {
        return paymentRequestId;
    }

    /**
     *
     * @param paymentRequestId
     * The PaymentRequestId
     */
    public void setPaymentRequestId(String paymentRequestId) {
        this.paymentRequestId = paymentRequestId;
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
     * The PurchaseDetails
     */
    public void setPurchaseDetails(List<PurchaseDetail> purchaseDetails) {
        this.purchaseDetails = purchaseDetails;
    }

    /**
     *
     * @return
     * The status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The Status
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     *
     * @return
     * The tPCUri
     */
    public String getTPCUri() {
        return tPCUri;
    }

    /**
     *
     * @param tPCUri
     * The TPCUri
     */
    public void setTPCUri(String tPCUri) {
        this.tPCUri = tPCUri;
    }

    /**
     *
     * @return
     * The id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The _id
     */
    public void setId(String id) {
        this.id = id;
    }


    public class KeyValuePair{
        String Key;
        String Value;

        public String getKey() {
            return Key;
        }

        public void setKey(String key) {
            Key = key;
        }

        public String getValue() {
            return Value;
        }

        public void setValue(String value) {
            Value = value;
        }
    }

    public String getTanNumber() {
        return tanNumber;
    }

    public void setTanNumber(String tanNumber) {
        this.tanNumber = tanNumber;
    }

    public double getTdsAmount() {
        return tdsAmount;
    }

    public void setTdsAmount(double tdsAmount) {
        this.tdsAmount = tdsAmount;
    }

    public String getTotalPayableAmount() {
        return totalPayableAmount;
    }

    public void setTotalPayableAmount(String totalPayableAmount) {
        this.totalPayableAmount = totalPayableAmount;
    }
}
package com.nowfloats.manageinventory.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by NowFloats on 28-08-2017.
 */

public class OrderModel implements Parcelable {
    public static final Creator<OrderModel> CREATOR = new Creator<OrderModel>() {
        @Override
        public OrderModel createFromParcel(Parcel in) {
            return new OrderModel(in);
        }

        @Override
        public OrderModel[] newArray(int size) {
            return new OrderModel[size];
        }
    };
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("order_id")
    @Expose
    private String orderId;
    @SerializedName("merchant_id")
    @Expose
    private String merchantId;
    @SerializedName("parent_id")
    @Expose
    private String parentId;
    @SerializedName("FPTag")
    @Expose
    private String fPTag;
    @SerializedName("user_id")
    @Expose
    private String orderUserId;
    @SerializedName("net_amount")
    @Expose
    private Double netAmount;
    @SerializedName("total_discount")
    @Expose
    private Double totalDiscount;
    @SerializedName("nf_assurance_charge_per")
    @Expose
    private Double nfAssuranceChargePer;
    @SerializedName("nf_assurance_charge_val")
    @Expose
    private Double nfAssuranceChargeVal;
    @SerializedName("total_amount")
    @Expose
    private Double totalAmount;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("DeliveryType")
    @Expose
    private Integer deliveryType;
    @SerializedName("delivery_partner_reference")
    @Expose
    private String deliveryPartnerReference;
    @SerializedName("ext_delivery_partner_ref_id")
    @Expose
    private String extDeliveryPartnerRefId;
    @SerializedName("progressive_order_status")
    @Expose
    private String progressiveOrderStatus;
    @SerializedName("order_status")
    @Expose
    private Integer orderStatus;
    @SerializedName("order_status_payment")
    @Expose
    private Integer orderStatusPayment;
    @SerializedName("order_status_delivery")
    @Expose
    private Integer orderStatusDelivery;
    @SerializedName("order_type")
    @Expose
    private Integer orderType;
    @SerializedName("payment_id")
    @Expose
    private String paymentId;
    @SerializedName("payment_req_id")
    @Expose
    private String paymentReqId;
    @SerializedName("expected_shipping_charge")
    @Expose
    private Double expectedShippingCharge;
    @SerializedName("actual_shipping_charge")
    @Expose
    private Double actualShippingCharge;
    @SerializedName("shipping_charge_nf")
    @Expose
    private Double shippingChargeNf;
    @SerializedName("shipping_charge_customer")
    @Expose
    private Double shippingChargeCustomer;
    @SerializedName("shipping_charge_merchant")
    @Expose
    private Double shippingChargeMerchant;
    @SerializedName("cancelled_by")
    @Expose
    private String cancelledBy;
    @SerializedName("pay_merchant_status")
    @Expose
    private Integer payMerchantStatus;
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
    @SerializedName("expected_delivery_date")
    @Expose
    private String expectedDeliveryDate;
    @SerializedName("actual_delivery_date")
    @Expose
    private String actualDeliveryDate;

    protected OrderModel(Parcel in) {
        id = in.readString();
        orderId = in.readString();
        merchantId = in.readString();
        parentId = in.readString();
        fPTag = in.readString();
        orderUserId = in.readString();
        netAmount = in.readDouble();
        totalDiscount = in.readDouble();
        nfAssuranceChargePer = in.readDouble();
        nfAssuranceChargeVal = in.readDouble();
        totalAmount = in.readDouble();
        currency = in.readString();
        deliveryType = in.readInt();
        deliveryPartnerReference = in.readString();
        extDeliveryPartnerRefId = in.readString();
        progressiveOrderStatus = in.readString();
        orderStatus = in.readInt();
        orderStatusPayment = in.readInt();
        orderStatusDelivery = in.readInt();
        orderType = in.readInt();
        paymentId = in.readString();
        paymentReqId = in.readString();
        expectedShippingCharge = in.readDouble();
        actualShippingCharge = in.readDouble();
        shippingChargeNf = in.readDouble();
        shippingChargeCustomer = in.readDouble();
        shippingChargeMerchant = in.readDouble();
        ;
        cancelledBy = in.readString();
        payMerchantStatus = in.readInt();
        userId = in.readString();
        actionId = in.readString();
        websiteId = in.readString();
        createdOn = in.readString();
        updatedOn = in.readString();
        isArchived = in.readByte() != 0;
        expectedDeliveryDate = in.readString();
        actualDeliveryDate = in.readString();
    }

    public OrderModel() {

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(orderId);
        dest.writeString(merchantId);
        dest.writeString(parentId);
        dest.writeString(fPTag);
        dest.writeString(orderUserId);
        dest.writeDouble(netAmount);
        dest.writeDouble(totalDiscount);
        dest.writeDouble(nfAssuranceChargePer);
        dest.writeDouble(nfAssuranceChargeVal);
        dest.writeDouble(totalAmount);
        dest.writeString(currency);
        dest.writeInt(deliveryType);
        dest.writeString(deliveryPartnerReference);
        dest.writeString(extDeliveryPartnerRefId);
        dest.writeString(progressiveOrderStatus);
        dest.writeInt(orderStatus);
        dest.writeInt(orderStatusPayment);
        dest.writeInt(orderStatusDelivery);
        dest.writeInt(orderType);
        dest.writeString(paymentId);
        dest.writeString(paymentReqId);
        dest.writeDouble(expectedShippingCharge);
        dest.writeDouble(actualShippingCharge);
        dest.writeDouble(shippingChargeNf);
        dest.writeDouble(shippingChargeCustomer);
        dest.writeDouble(shippingChargeMerchant);
        dest.writeString(cancelledBy);
        dest.writeInt(payMerchantStatus);
        dest.writeString(userId);
        dest.writeString(actionId);
        dest.writeString(websiteId);
        dest.writeString(createdOn);
        dest.writeString(updatedOn);
        dest.writeByte((byte) (isArchived ? 1 : 0));
        dest.writeString(expectedDeliveryDate);
        dest.writeString(actualDeliveryDate);
    }

    public String getActualDeliveryDate() {
        return actualDeliveryDate;
    }

    public void setActualDeliveryDate(String actualDeliveryDate) {
        this.actualDeliveryDate = actualDeliveryDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public Object getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getFPTag() {
        return fPTag;
    }

    public void setFPTag(String fPTag) {
        this.fPTag = fPTag;
    }

    public String getOrderUserId() {
        return orderUserId;
    }

    public void setOrderUserId(String orderUserId) {
        this.orderUserId = orderUserId;
    }

    public Double getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(Double netAmount) {
        this.netAmount = netAmount;
    }

    public Double getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(Double totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public Double getNfAssuranceChargePer() {
        return nfAssuranceChargePer;
    }

    public void setNfAssuranceChargePer(Double nfAssuranceChargePer) {
        this.nfAssuranceChargePer = nfAssuranceChargePer;
    }

    public Double getNfAssuranceChargeVal() {
        return nfAssuranceChargeVal;
    }

    public void setNfAssuranceChargeVal(Double nfAssuranceChargeVal) {
        this.nfAssuranceChargeVal = nfAssuranceChargeVal;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(Integer deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getDeliveryPartnerReference() {
        return deliveryPartnerReference;
    }

    public void setDeliveryPartnerReference(String deliveryPartnerReference) {
        this.deliveryPartnerReference = deliveryPartnerReference;
    }

    public String getExtDeliveryPartnerRefId() {
        return extDeliveryPartnerRefId;
    }

    public void setExtDeliveryPartnerRefId(String extDeliveryPartnerRefId) {
        this.extDeliveryPartnerRefId = extDeliveryPartnerRefId;
    }

    public String getProgressiveOrderStatus() {
        return progressiveOrderStatus;
    }

    public void setProgressiveOrderStatus(String progressiveOrderStatus) {
        this.progressiveOrderStatus = progressiveOrderStatus;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Integer getOrderStatusPayment() {
        return orderStatusPayment;
    }

    public void setOrderStatusPayment(Integer orderStatusPayment) {
        this.orderStatusPayment = orderStatusPayment;
    }

    public Integer getOrderStatusDelivery() {
        return orderStatusDelivery;
    }

    public void setOrderStatusDelivery(Integer orderStatusDelivery) {
        this.orderStatusDelivery = orderStatusDelivery;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentReqId() {
        return paymentReqId;
    }

    public void setPaymentReqId(String paymentReqId) {
        this.paymentReqId = paymentReqId;
    }

    public Double getExpectedShippingCharge() {
        return expectedShippingCharge;
    }

    public void setExpectedShippingCharge(Double expectedShippingCharge) {
        this.expectedShippingCharge = expectedShippingCharge;
    }

    public Double getActualShippingCharge() {
        return actualShippingCharge;
    }

    public void setActualShippingCharge(Double actualShippingCharge) {
        this.actualShippingCharge = actualShippingCharge;
    }

    public Double getShippingChargeNf() {
        return shippingChargeNf;
    }

    public void setShippingChargeNf(Double shippingChargeNf) {
        this.shippingChargeNf = shippingChargeNf;
    }

    public Double getShippingChargeCustomer() {
        return shippingChargeCustomer;
    }

    public void setShippingChargeCustomer(Double shippingChargeCustomer) {
        this.shippingChargeCustomer = shippingChargeCustomer;
    }

    public Double getShippingChargeMerchant() {
        return shippingChargeMerchant;
    }

    public void setShippingChargeMerchant(Double shippingChargeMerchant) {
        this.shippingChargeMerchant = shippingChargeMerchant;
    }

    public Object getCancelledBy() {
        return cancelledBy;
    }

    public void setCancelledBy(String cancelledBy) {
        this.cancelledBy = cancelledBy;
    }

    public Integer getPayMerchantStatus() {
        return payMerchantStatus;
    }

    public void setPayMerchantStatus(Integer payMerchantStatus) {
        this.payMerchantStatus = payMerchantStatus;
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

    public String getfPTag() {
        return fPTag;
    }

    public void setfPTag(String fPTag) {
        this.fPTag = fPTag;
    }

    public Boolean getArchived() {
        return isArchived;
    }

    public void setArchived(Boolean archived) {
        isArchived = archived;
    }

    public String getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }

    public void setExpectedDeliveryDate(String expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    @Override
    public int describeContents() {
        return 18;
    }

}

package com.nowfloats.manageinventory.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by NowFloats on 16-08-2017.
 */

public class TransactionModel {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("merchant_id")
    @Expose
    private String merchantId;
    @SerializedName("order_id")
    @Expose
    private String orderId;
    @SerializedName("parent_id")
    @Expose
    private Object parentId;
    @SerializedName("fp_tag")
    @Expose
    private String fpTag;
    @SerializedName("transaction_type")
    @Expose
    private Integer transactionType;
    @SerializedName("amount")
    @Expose
    private Double amount;
    @SerializedName("order_cumulative_amount")
    @Expose
    private Integer orderCumulativeAmount;
    @SerializedName("order_on_hold_amount")
    @Expose
    private Double orderOnHoldAmount;
    @SerializedName("order_transferred_amount")
    @Expose
    private Integer orderTransferredAmount;
    @SerializedName("group_cumulative_amount")
    @Expose
    private Integer groupCumulativeAmount;
    @SerializedName("group_on_hold_amount")
    @Expose
    private Integer groupOnHoldAmount;
    @SerializedName("group_transferred_amount")
    @Expose
    private Integer groupTransferredAmount;
    @SerializedName("total_revenue")
    @Expose
    private Integer totalRevenue;
    @SerializedName("cumulative_group_ids")
    @Expose
    private String cumulativeGroupIds;
    @SerializedName("on_hold_group_ids")
    @Expose
    private String onHoldGroupIds;
    @SerializedName("transferred_group_ids")
    @Expose
    private Object transferredGroupIds;
    @SerializedName("reference_id")
    @Expose
    private String referenceId;
    @SerializedName("reference_type")
    @Expose
    private Integer referenceType;
    @SerializedName("bank_name")
    @Expose
    private Object bankName;
    @SerializedName("bank_acc_num")
    @Expose
    private Object bankAccNum;
    @SerializedName("ifsc_code")
    @Expose
    private Object ifscCode;
    @SerializedName("time_stamp")
    @Expose
    private String timeStamp;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Object getParentId() {
        return parentId;
    }

    public void setParentId(Object parentId) {
        this.parentId = parentId;
    }

    public String getFpTag() {
        return fpTag;
    }

    public void setFpTag(String fpTag) {
        this.fpTag = fpTag;
    }

    public Integer getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(Integer transactionType) {
        this.transactionType = transactionType;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getOrderCumulativeAmount() {
        return orderCumulativeAmount;
    }

    public void setOrderCumulativeAmount(Integer orderCumulativeAmount) {
        this.orderCumulativeAmount = orderCumulativeAmount;
    }

    public Double getOrderOnHoldAmount() {
        return orderOnHoldAmount;
    }

    public void setOrderOnHoldAmount(Double orderOnHoldAmount) {
        this.orderOnHoldAmount = orderOnHoldAmount;
    }

    public Integer getOrderTransferredAmount() {
        return orderTransferredAmount;
    }

    public void setOrderTransferredAmount(Integer orderTransferredAmount) {
        this.orderTransferredAmount = orderTransferredAmount;
    }

    public Integer getGroupCumulativeAmount() {
        return groupCumulativeAmount;
    }

    public void setGroupCumulativeAmount(Integer groupCumulativeAmount) {
        this.groupCumulativeAmount = groupCumulativeAmount;
    }

    public Integer getGroupOnHoldAmount() {
        return groupOnHoldAmount;
    }

    public void setGroupOnHoldAmount(Integer groupOnHoldAmount) {
        this.groupOnHoldAmount = groupOnHoldAmount;
    }

    public Integer getGroupTransferredAmount() {
        return groupTransferredAmount;
    }

    public void setGroupTransferredAmount(Integer groupTransferredAmount) {
        this.groupTransferredAmount = groupTransferredAmount;
    }

    public Integer getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Integer totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public String getCumulativeGroupIds() {
        return cumulativeGroupIds;
    }

    public void setCumulativeGroupIds(String cumulativeGroupIds) {
        this.cumulativeGroupIds = cumulativeGroupIds;
    }

    public String getOnHoldGroupIds() {
        return onHoldGroupIds;
    }

    public void setOnHoldGroupIds(String onHoldGroupIds) {
        this.onHoldGroupIds = onHoldGroupIds;
    }

    public Object getTransferredGroupIds() {
        return transferredGroupIds;
    }

    public void setTransferredGroupIds(Object transferredGroupIds) {
        this.transferredGroupIds = transferredGroupIds;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public Integer getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(Integer referenceType) {
        this.referenceType = referenceType;
    }

    public Object getBankName() {
        return bankName;
    }

    public void setBankName(Object bankName) {
        this.bankName = bankName;
    }

    public Object getBankAccNum() {
        return bankAccNum;
    }

    public void setBankAccNum(Object bankAccNum) {
        this.bankAccNum = bankAccNum;
    }

    public Object getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(Object ifscCode) {
        this.ifscCode = ifscCode;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
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
}

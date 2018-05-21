package com.nowfloats.Store.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
public class MarkAsPaidModel {

    @SerializedName("BaseAmount")
    @Expose
    private Integer baseAmount;
    @SerializedName("ClientId")
    @Expose
    private String clientId;
    @SerializedName("ComboPackageId")
    @Expose
    private Object comboPackageId;
    @SerializedName("ExpectedAmount")
    @Expose
    private Integer expectedAmount;
    @SerializedName("FpId")
    @Expose
    private String fpId;
    @SerializedName("FpTag")
    @Expose
    private String fpTag;
    @SerializedName("IsCustomBundle")
    @Expose
    private Boolean isCustomBundle;
    @SerializedName("IsPartOfComboPlan")
    @Expose
    private Boolean isPartOfComboPlan;
    @SerializedName("TaxAmount")
    @Expose
    private Integer taxAmount;
    @SerializedName("currencyCode")
    @Expose
    private String currencyCode;
    @SerializedName("customerSalesOrderRequest")
    @Expose
    private CustomerSalesOrderRequest customerSalesOrderRequest;
    @SerializedName("packages")
    @Expose
    private List<Package> packages = null;
    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("paymentTransactionId")
    @Expose
    private String paymentTransactionId;

    public Integer getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount(Integer baseAmount) {
        this.baseAmount = baseAmount;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Object getComboPackageId() {
        return comboPackageId;
    }

    public void setComboPackageId(Object comboPackageId) {
        this.comboPackageId = comboPackageId;
    }

    public Integer getExpectedAmount() {
        return expectedAmount;
    }

    public void setExpectedAmount(Integer expectedAmount) {
        this.expectedAmount = expectedAmount;
    }

    public String getFpId() {
        return fpId;
    }

    public void setFpId(String fpId) {
        this.fpId = fpId;
    }

    public String getFpTag() {
        return fpTag;
    }

    public void setFpTag(String fpTag) {
        this.fpTag = fpTag;
    }

    public Boolean getIsCustomBundle() {
        return isCustomBundle;
    }

    public void setIsCustomBundle(Boolean isCustomBundle) {
        this.isCustomBundle = isCustomBundle;
    }

    public Boolean getIsPartOfComboPlan() {
        return isPartOfComboPlan;
    }

    public void setIsPartOfComboPlan(Boolean isPartOfComboPlan) {
        this.isPartOfComboPlan = isPartOfComboPlan;
    }

    public Integer getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(Integer taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public CustomerSalesOrderRequest getCustomerSalesOrderRequest() {
        return customerSalesOrderRequest;
    }

    public void setCustomerSalesOrderRequest(CustomerSalesOrderRequest customerSalesOrderRequest) {
        this.customerSalesOrderRequest = customerSalesOrderRequest;
    }

    public List<Package> getPackages() {
        return packages;
    }

    public void setPackages(List<Package> packages) {
        this.packages = packages;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getPaymentTransactionId() {
        return paymentTransactionId;
    }

    public void setPaymentTransactionId(String paymentTransactionId) {
        this.paymentTransactionId = paymentTransactionId;
    }
    public static class CustomerSalesOrderRequest {

        @SerializedName("_nfInternalERPId")
        @Expose
        private Object nfInternalERPId;
        @SerializedName("customerEmailId")
        @Expose
        private String customerEmailId;
        @SerializedName("discountPercentageValue")
        @Expose
        private Integer discountPercentageValue;
        @SerializedName("invoiceStatus")
        @Expose
        private Integer invoiceStatus;
        @SerializedName("paymentMode")
        @Expose
        private Integer paymentMode;
        @SerializedName("paymentTransactionStatus")
        @Expose
        private Integer paymentTransactionStatus;
        @SerializedName("purchasedUnits")
        @Expose
        private Integer purchasedUnits;
        @SerializedName("sendEmail")
        @Expose
        private Boolean sendEmail;

        public Object getNfInternalERPId() {
            return nfInternalERPId;
        }

        public void setNfInternalERPId(Object nfInternalERPId) {
            this.nfInternalERPId = nfInternalERPId;
        }

        public String getCustomerEmailId() {
            return customerEmailId;
        }

        public void setCustomerEmailId(String customerEmailId) {
            this.customerEmailId = customerEmailId;
        }

        public Integer getDiscountPercentageValue() {
            return discountPercentageValue;
        }

        public void setDiscountPercentageValue(Integer discountPercentageValue) {
            this.discountPercentageValue = discountPercentageValue;
        }

        public Integer getInvoiceStatus() {
            return invoiceStatus;
        }

        public void setInvoiceStatus(Integer invoiceStatus) {
            this.invoiceStatus = invoiceStatus;
        }

        public Integer getPaymentMode() {
            return paymentMode;
        }

        public void setPaymentMode(Integer paymentMode) {
            this.paymentMode = paymentMode;
        }

        public Integer getPaymentTransactionStatus() {
            return paymentTransactionStatus;
        }

        public void setPaymentTransactionStatus(Integer paymentTransactionStatus) {
            this.paymentTransactionStatus = paymentTransactionStatus;
        }

        public Integer getPurchasedUnits() {
            return purchasedUnits;
        }

        public void setPurchasedUnits(Integer purchasedUnits) {
            this.purchasedUnits = purchasedUnits;
        }

        public Boolean getSendEmail() {
            return sendEmail;
        }

        public void setSendEmail(Boolean sendEmail) {
            this.sendEmail = sendEmail;
        }

    }

    public static class Package {

        @SerializedName("packageId")
        @Expose
        private String packageId;
        @SerializedName("quantity")
        @Expose
        private Integer quantity;

        public String getPackageId() {
            return packageId;
        }

        public void setPackageId(String packageId) {
            this.packageId = packageId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

    }
}


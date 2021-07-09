package com.nowfloats.manageinventory.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by NowFloats on 28-08-2017.
 */

public class OrderDataModel {

    @SerializedName("Data")
    @Expose
    private Data data;

    public class Data {
        @SerializedName("Items")
        @Expose
        List<Order> orders;

        public List<Order> getOrders() {
            return orders;
        }

        public void setOrders(List<Order> orders) {
            this.orders = orders;
        }
    }

    public class Order implements Serializable {
        @SerializedName("_id")
        @Expose
        private String orderId;

        @SerializedName("ReferenceNumber")
        @Expose
        private String referenceNumber;

        @SerializedName("Status")
        @Expose
        private String status;

        @SerializedName("Mode")
        @Expose
        private String mode;

        @SerializedName("CreatedOn")
        @Expose
        private String createdOn;

        @SerializedName("Items")
        @Expose
        private List<OrderDetails> orderDetails;

        @SerializedName("BuyerDetails")
        @Expose
        private BuyerDetails buyerDetails;

        @SerializedName("BillingDetails")
        @Expose
        private BillingDetails billingDetails;

        @SerializedName("PaymentDetails")
        @Expose
        private PaymentDetails paymentDetails;

        @SerializedName("LogisticsDetails")
        @Expose
        private LogisticsDetails logisticsDetails;

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public BuyerDetails getBuyerDetails() {
            return buyerDetails;
        }

        public void setBuyerDetails(BuyerDetails buyerDetails) {
            this.buyerDetails = buyerDetails;
        }

        public List<OrderDetails> getOrderDetails() {
            return orderDetails;
        }

        public void setOrderDetails(List<OrderDetails> orderDetails) {
            this.orderDetails = orderDetails;
        }

        public String getCreatedOn() {
            return createdOn;
        }

        public void setCreatedOn(String createdOn) {
            this.createdOn = createdOn;
        }

        public BillingDetails getBillingDetails() {
            return billingDetails;
        }

        public void setBillingDetails(BillingDetails billingDetails) {
            this.billingDetails = billingDetails;
        }

        public PaymentDetails getPaymentDetails() {
            return paymentDetails;
        }

        public void setPaymentDetails(PaymentDetails paymentDetails) {
            this.paymentDetails = paymentDetails;
        }

        public LogisticsDetails getLogisticsDetails() {
            return logisticsDetails;
        }

        public void setLogisticsDetails(LogisticsDetails logisticsDetails) {
            this.logisticsDetails = logisticsDetails;
        }

        public String getReferenceNumber() {
            return referenceNumber;
        }

        public void setReferenceNumber(String referenceNumber) {
            this.referenceNumber = referenceNumber;
        }
    }

    public class DeliveryConfirmationDetails implements Serializable {
        @SerializedName("NotificationSentOn")
        @Expose
        private String notificationSentOn;

        public String getNotificationSentOn() {
            return notificationSentOn;
        }

        public void setNotificationSentOn(String notificationSentOn) {
            this.notificationSentOn = notificationSentOn;
        }
    }

    public class OrderDetails implements Serializable {
        @SerializedName("Type")
        @Expose
        private String type;

        @SerializedName("Product")
        @Expose
        private Product product;

        @SerializedName("ActualPrice")
        @Expose
        private Double actualPrice;

        @SerializedName("SalePrice")
        @Expose
        private Double salePrice;

        @SerializedName("Quantity")
        @Expose
        private Integer quantity;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Product getProduct() {
            return product;
        }

        public void setProduct(Product product) {
            this.product = product;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public Double getActualPrice() {
            return actualPrice;
        }

        public void setActualPrice(Double actualPrice) {
            this.actualPrice = actualPrice;
        }

        public Double getSalePrice() {
            return salePrice;
        }

        public void setSalePrice(Double salePrice) {
            this.salePrice = salePrice;
        }
    }

    public class Product implements Serializable {
        @SerializedName("Name")
        @Expose
        private String name;

        @SerializedName("Price")
        @Expose
        private Double price;

        @SerializedName("DiscountAmount")
        @Expose
        private Double discountAmount;

        @SerializedName("FeaturedImage")
        @Expose
        private FeaturedImage featuredImage;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }

        public Double getDiscountAmount() {
            return discountAmount;
        }

        public void setDiscountAmount(Double discountAmount) {
            this.discountAmount = discountAmount;
        }

        public FeaturedImage getFeaturedImage() {
            return featuredImage;
        }

        public void setFeaturedImage(FeaturedImage featuredImage) {
            this.featuredImage = featuredImage;
        }

    }

    public class FeaturedImage implements Serializable {
        @SerializedName("ImageUri")
        @Expose
        private String imageUri;

        @SerializedName("TileImageUri")
        @Expose
        private String tileImageUri;

        public String getImageUri() {
            return imageUri;
        }

        public void setImageUri(String imageUri) {
            this.imageUri = imageUri;
        }

        public String getTileImageUri() {
            return tileImageUri;
        }

        public void setTileImageUri(String tileImageUri) {
            this.tileImageUri = tileImageUri;
        }
    }


    public class BillingDetails implements Serializable {
        @SerializedName("CurrencyCode")
        @Expose
        private String currencyCode;

        @SerializedName("GrossAmount")
        @Expose
        private Double grossAmount;

        @SerializedName("SellerDeliveryCharges")
        @Expose
        private Double sellerDeliveryCharges;

        @SerializedName("NFDeliveryCharges")
        @Expose
        private Double nfDeliveryCharges;

        @SerializedName("DiscountAmount")
        @Expose
        private Double discountAmount;

        @SerializedName("AssuredPurchaseCharges")
        @Expose
        private Double assuredPurchaseCharges;

        @SerializedName("AmountPayableByBuyer")
        @Expose
        private Double amountPayableByBuyer;

        public String getCurrencyCode() {
            return currencyCode;
        }

        public void setCurrencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
        }

        public Double getGrossAmount() {
            return grossAmount;
        }

        public void setGrossAmount(Double grossAmount) {
            this.grossAmount = grossAmount;
        }

        public Double getDiscountAmount() {
            return discountAmount;
        }

        public void setDiscountAmount(Double discountAmount) {
            this.discountAmount = discountAmount;
        }

        public Double getSellerDeliveryCharges() {
            return sellerDeliveryCharges;
        }

        public void setSellerDeliveryCharges(Double sellerDeliveryCharges) {
            this.sellerDeliveryCharges = sellerDeliveryCharges;
        }

        public Double getNfDeliveryCharges() {
            return nfDeliveryCharges;
        }

        public void setNfDeliveryCharges(Double nfDeliveryCharges) {
            this.nfDeliveryCharges = nfDeliveryCharges;
        }

        public Double getAssuredPurchaseCharges() {
            return assuredPurchaseCharges;
        }

        public void setAssuredPurchaseCharges(Double assuredPurchaseCharges) {
            this.assuredPurchaseCharges = assuredPurchaseCharges;
        }

        public Double getAmountPayableByBuyer() {
            return amountPayableByBuyer;
        }

        public void setAmountPayableByBuyer(Double amountPayableByBuyer) {
            this.amountPayableByBuyer = amountPayableByBuyer;
        }
    }

    public class PaymentDetails implements Serializable {
        @SerializedName("Status")
        @Expose
        private String status;

        @SerializedName("Method")
        @Expose
        private String method;

        @SerializedName("OnlinePaymentProvider")
        @Expose
        private String onlinePaymentProvider;

        @SerializedName("TransactionId")
        @Expose
        private String transactionId;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getOnlinePaymentProvider() {
            return onlinePaymentProvider;
        }

        public void setOnlinePaymentProvider(String onlinePaymentProvider) {
            this.onlinePaymentProvider = onlinePaymentProvider;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }
    }

    public class BuyerDetails implements Serializable {
        @SerializedName("ContactDetails")
        @Expose
        private ContactDetails contactDetails;

        @SerializedName("Address")
        @Expose
        private Address address;

        public ContactDetails getContactDetails() {
            return contactDetails;
        }

        public void setContactDetails(ContactDetails contactDetails) {
            this.contactDetails = contactDetails;
        }

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }
    }

    public class LogisticsDetails implements Serializable {
        @SerializedName("Status")
        @Expose
        private String status;

        @SerializedName("ShippedBy")
        @Expose
        private String shippedBy;

        @SerializedName("DeliveredOn")
        @Expose
        private String deliveredOn;

        @SerializedName("DeliveryConfirmationDetails")
        @Expose
        private DeliveryConfirmationDetails deliveryConfirmationDetails;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getShippedBy() {
            return shippedBy;
        }

        public void setShippedBy(String shippedBy) {
            this.shippedBy = shippedBy;
        }

        public String getDeliveredOn() {
            return deliveredOn;
        }

        public void setDeliveredOn(String deliveredOn) {
            this.deliveredOn = deliveredOn;
        }

        public DeliveryConfirmationDetails getDeliveryConfirmationDetails() {
            return deliveryConfirmationDetails;
        }

        public void setDeliveryConfirmationDetails(DeliveryConfirmationDetails deliveryConfirmationDetails) {
            this.deliveryConfirmationDetails = deliveryConfirmationDetails;
        }
    }

    public class ContactDetails implements Serializable {
        @SerializedName("FullName")
        @Expose
        private String fullName;

        @SerializedName("PrimaryContactNumber")
        @Expose
        private String primaryContactNumber;

        @SerializedName("SecondaryContactNumber")
        @Expose
        private String secondaryContactNumber;

        @SerializedName("EmailId")
        @Expose
        private String emailId;

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getPrimaryContactNumber() {
            return primaryContactNumber;
        }

        public void setPrimaryContactNumber(String primaryContactNumber) {
            this.primaryContactNumber = primaryContactNumber;
        }

        public String getSecondaryContactNumber() {
            return secondaryContactNumber;
        }

        public void setSecondaryContactNumber(String secondaryContactNumber) {
            this.secondaryContactNumber = secondaryContactNumber;
        }

        public String getEmailId() {
            return emailId;
        }

        public void setEmailId(String emailId) {
            this.emailId = emailId;
        }
    }

    public class Address implements Serializable {
        @SerializedName("AddressLine1")
        @Expose
        private String addressLine1;

        @SerializedName("AddressLine2")
        @Expose
        private String addressLine2;

        @SerializedName("City")
        @Expose
        private String city;

        @SerializedName("Region")
        @Expose
        private String region;

        @SerializedName("country")
        @Expose
        private String country;

        @SerializedName("Zipcode")
        @Expose
        private String zipcode;

        public String getAddressLine1() {
            return addressLine1;
        }

        public void setAddressLine1(String addressLine1) {
            this.addressLine1 = addressLine1;
        }

        public String getAddressLine2() {
            return addressLine2;
        }

        public void setAddressLine2(String addressLine2) {
            this.addressLine2 = addressLine2;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getZipcode() {
            return zipcode;
        }

        public void setZipcode(String zipcode) {
            this.zipcode = zipcode;
        }
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}

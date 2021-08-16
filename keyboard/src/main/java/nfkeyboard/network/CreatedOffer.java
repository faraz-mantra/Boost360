package nfkeyboard.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


/**
 * Created by Shimona on 07-06-2018.
 */

public class CreatedOffer {

    @SerializedName("Status")
    @Expose
    String status;
    @SerializedName("Message")
    @Expose
    String message;
    @SerializedName("Data")
    @Expose
    Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {

        @SerializedName("_id")
        @Expose
        String id;
        @SerializedName("Product")
        @Expose
        Product product;
        @SerializedName("Price")
        @Expose
        double price;
        @SerializedName("Quantity")
        @Expose
        int quantity;
        @SerializedName("MaxQuantityPerOrder")
        @Expose
        int maxQuantityPerOrder;
        @SerializedName("Url")
        @Expose
        String url;
        @SerializedName("ExpiresOn")
        @Expose
        String expiresOn;
        @SerializedName("CreatedOn")
        @Expose
        String createdOn;
        @SerializedName("UpdatedOn")
        @Expose
        String updatedOn;
        @SerializedName("IsArchived")
        @Expose
        boolean isArchived;
        @SerializedName("SellerId")
        @Expose
        String sellerId;

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getExpiresOn() {
            return expiresOn;
        }

        public void setExpiresOn(String expiresOn) {
            this.expiresOn = expiresOn;
        }

        public String getCreatedOn() {
            return createdOn;
        }

        public void setCreatedOn(String createdOn) {
            this.createdOn = createdOn;
        }

        public Product getProduct() {
            return product;
        }

        public void setProduct(Product product) {
            this.product = product;
        }

        public static class Product {

            @SerializedName("GroupProductId")
            @Expose
            String groupProductId;
            @SerializedName("CreatedOn")
            @Expose
            String createdOn;
            @SerializedName("UpdatedOn")
            @Expose
            String updatedOn;
            @SerializedName("availableUnits")
            @Expose
            double availableUnits;
            @SerializedName("TotalQueries")
            @Expose
            int totalQueries;
            @SerializedName("GPId")
            @Expose
            String gpId;
            @SerializedName("_id")
            @Expose
            String id;
            @SerializedName("FPTag")
            @Expose
            String fpTag;
            @SerializedName("MerchantName")
            @Expose
            String merchantName;
            @SerializedName("CustomWidgets")
            @Expose
            List<Object> customWidgets;
            @SerializedName("ApplicationId")
            @Expose
            String appId;
            @SerializedName("ProductIndex")
            @Expose
            long prodIndex;
            @SerializedName("FeaturedImage")
            @Expose
            Object featuredImage;
            @SerializedName("ImageGallery")
            @Expose
            List<Object> galleryImages;
            @SerializedName("ExternalSourceId")
            @Expose
            String extSourceId;
            @SerializedName("Name")
            @Expose
            String name;
            @SerializedName("Description")
            @Expose
            String description;
            @SerializedName("Price")
            @Expose
            double price;
            @SerializedName("DiscountAmount")
            @Expose
            double discountAmount;
            @SerializedName("CurrencyCode")
            @Expose
            String currencyCode;
            @SerializedName("Priority")
            @Expose
            long priority;
            @SerializedName("BuyOnlineLink")
            @Expose
            String buyOnlineLink;
            @SerializedName("IsFreeShipmentAvailable")
            @Expose
            boolean isFreeShippingAvailable;
            @SerializedName("ShipmentDuration")
            @Expose
            int shipmentDuration;
            @SerializedName("_keywords")
            @Expose
            List<String> keywords;
            @SerializedName("IsArchived")
            @Expose
            boolean isArchived;
            @SerializedName("IsAvailable")
            @Expose
            boolean isAvailable;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public double getPrice() {
                return price;
            }

            public void setPrice(double price) {
                this.price = price;
            }

            public String getCurrencyCode() {
                return currencyCode;
            }

            public void setCurrencyCode(String currencyCode) {
                this.currencyCode = currencyCode;
            }
        }

    }
}

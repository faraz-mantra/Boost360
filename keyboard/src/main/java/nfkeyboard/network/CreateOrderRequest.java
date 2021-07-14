package nfkeyboard.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Shimona on 28-05-2018.
 */

class CreateOrderRequest {
    @SerializedName("ProductId")
    @Expose
    String ProductId;
    @SerializedName("ExpiresOn")
    @Expose
    String ExpiresOn;
    @SerializedName("Quantity")
    @Expose
    int Quantity;
    @SerializedName("SalePrice")
    @Expose
    double SalePrice;
    @SerializedName("MaxQuantityPerOrder")
    @Expose
    int MaxQuantityPerOrder;
    @SerializedName("Seller")
    @Expose
    Seller Seller;

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        this.ProductId = productId;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        this.Quantity = quantity;
    }

    public double getSalePrice() {
        return SalePrice;
    }

    public void setSalePrice(double salePrice) {
        this.SalePrice = salePrice;
    }

    public Seller getSellers() {
        return Seller;
    }

    public void setSellers(Seller Seller) {
        this.Seller = Seller;
    }

    public String getExpiresOn() {
        return ExpiresOn;
    }

    public void setExpiresOn(String expiresOn) {
        this.ExpiresOn = expiresOn;
    }

    public int getMaxQuantityPerOrder() {
        return MaxQuantityPerOrder;
    }

    public void setMaxQuantityPerOrder(int maxQuantityPerOrder) {
        this.MaxQuantityPerOrder = maxQuantityPerOrder;
    }

    public static class Seller {
        @SerializedName("Identifier")
        @Expose
        String Identifier;
        @SerializedName("ContactDetails")
        @Expose
        ContactDetails ContactDetail;
        @SerializedName("Address")
        @Expose
        Address Address;

        public String getIdentifier() {
            return Identifier;
        }

        public void setIdentifier(String identifier) {
            this.Identifier = identifier;
        }

        public ContactDetails getContactDetails() {
            return ContactDetail;
        }

        public void setContactDetails(ContactDetails contactDetails) {
            this.ContactDetail = contactDetails;
        }

        public Address getAddresses() {
            return Address;
        }

        public void setAddresses(Address addresses) {
            this.Address = addresses;
        }

        public static class Address {
            @SerializedName("AddressLine1")
            @Expose
            String AddressLine1;
            @SerializedName("AddressLine2")
            @Expose
            String AddressLine2;
            @SerializedName("City")
            @Expose
            String City;
            @SerializedName("Region")
            @Expose
            String Region;
            @SerializedName("Country")
            @Expose
            String Country;
            @SerializedName("Zipcode")
            @Expose
            String Zipcode;

            public String getAddressLine1() {
                return AddressLine1;
            }

            public void setAddressLine1(String addressLine1) {
                this.AddressLine1 = addressLine1;
            }

            public String getAddressLine2() {
                return AddressLine2;
            }

            public void setAddressLine2(String addressLine2) {
                this.AddressLine2 = addressLine2;
            }

            public String getCity() {
                return City;
            }

            public void setCity(String city) {
                this.City = city;
            }

            public String getRegion() {
                return Region;
            }

            public void setRegion(String region) {
                this.Region = region;
            }

            public String getCountry() {
                return Country;
            }

            public void setCountry(String country) {
                this.Country = country;
            }

            public String getZipcode() {
                return Zipcode;
            }

            public void setZipcode(String zipcode) {
                this.Zipcode = zipcode;
            }
        }

        public static class ContactDetails {
            @SerializedName("FullName")
            @Expose
            String FullName;
            @SerializedName("PrimaryContactNumber")
            @Expose
            String PrimaryContactNumber;
            @SerializedName("SecondaryContactNumber")
            @Expose
            String SecondaryContactNumber;
            @SerializedName("EmailId")
            @Expose
            String EmailId;

            public String getFullName() {
                return FullName;
            }

            public void setFullName(String FullName) {
                this.FullName = FullName;
            }

            public String getPrimaryContactNumber() {
                return PrimaryContactNumber;
            }

            public void setPrimaryContactNumber(String primaryContactNumber) {
                this.PrimaryContactNumber = primaryContactNumber;
            }

            public String getSecondaryContactNumber() {
                return SecondaryContactNumber;
            }

            public void setSecondaryContactNumber(String secondaryContactNumber) {
                this.SecondaryContactNumber = secondaryContactNumber;
            }

            public String getEmailId() {
                return EmailId;
            }

            public void setEmailId(String emailId) {
                this.EmailId = emailId;
            }
        }
    }
}

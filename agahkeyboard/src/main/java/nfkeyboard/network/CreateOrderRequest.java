package nfkeyboard.network;

/**
 * Created by Shimona on 28-05-2018.
 */

class CreateOrderRequest {
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

    String ProductId;
    String ExpiresOn;
    int Quantity;
    double SalePrice;
    int MaxQuantityPerOrder;
    Seller Seller;

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

        String Identifier;
        ContactDetails ContactDetail;
        Address Address;

        public static class Address {
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

            String AddressLine1;
            String AddressLine2;
            String City;
            String Region;
            String Country;
            String Zipcode;
        }

        public  static class ContactDetails {
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

            String FullName;
            String PrimaryContactNumber;
            String SecondaryContactNumber;
            String EmailId;
        }
    }
}

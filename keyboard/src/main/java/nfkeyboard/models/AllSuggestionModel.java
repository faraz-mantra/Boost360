package nfkeyboard.models;

import android.text.TextUtils;

import nfkeyboard.adapter.BaseAdapterManager;


/**
 * Created by Admin on 23-02-2018.
 */

public class AllSuggestionModel {
    private String text;
    private String imageUrl;
    private String id;
    private String url;
    private String name;
    private String businessName;
    private String phoneNumber;

    public String getSecondaryPhoneNumber() {
        return secondaryPhoneNumber;
    }

    public void setSecondaryPhoneNumber(String secondaryPhoneNumber) {
        this.secondaryPhoneNumber = secondaryPhoneNumber;
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

    private String secondaryPhoneNumber;
    private String city, region, country, zipcode;
    private String website;
    private String email;
    private String address;
    private String location;
    private int availableUnits;
    private String currencyCode;
    private String fpTag;
    private String linkExpiryDateTime;
    private int maxUsage;
    private String p_id;
    private float amount;
    private int quantity;
    private String clientId;
    private String imageUri;
    private boolean selected;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        typeEnum = BaseAdapterManager.SectionTypeEnum.ImageShare;
        this.imageUri = imageUri;
    }

    private BaseAdapterManager.SectionTypeEnum typeEnum = BaseAdapterManager.SectionTypeEnum.Text;


    public AllSuggestionModel(String text, String imageUrl) {
        this.text = text;
        this.imageUrl = imageUrl;
        if (!TextUtils.isEmpty(imageUrl)) {
            typeEnum = BaseAdapterManager.SectionTypeEnum.ImageAndText;
        }
    }

    public AllSuggestionModel(String imageUri) {
        this.imageUri = imageUri;
        typeEnum = BaseAdapterManager.SectionTypeEnum.ImageShare;

    }

    public AllSuggestionModel(String name, String businessName, String website) {
        this.name = name;
        this.businessName = businessName;
        this.website = website;
        typeEnum = BaseAdapterManager.SectionTypeEnum.DetailsShare;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getFpTag() {
        return fpTag;
    }

    public void setFpTag(String fpTag) {
        this.fpTag = fpTag;
    }

    public String getLinkExpiryDateTime() {
        return linkExpiryDateTime;
    }

    public void setLinkExpiryDateTime(String linkExpiryDateTime) {
        this.linkExpiryDateTime = linkExpiryDateTime;
    }

    public int getMaxUsage() {
        return maxUsage;
    }

    public void setMaxUsage(int maxUsage) {
        this.maxUsage = maxUsage;
    }

    public String getP_id() {
        return p_id;
    }

    public void setP_id(String p_id) {
        this.p_id = p_id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTypeEnum(BaseAdapterManager.SectionTypeEnum type) {
        typeEnum = type;
    }

    public String getText() {
        return text;
    }

    public String price;
    public String description;
    public String discount;
    public String brandName;
    public String category;

    public void setText(String text) {
        this.text = text;
    }

    public void setImageUrl(String imageUrl) {
        if (!TextUtils.isEmpty(imageUrl)) {
            typeEnum = BaseAdapterManager.SectionTypeEnum.ImageAndText;
        }
        this.imageUrl = imageUrl;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setAvailableUnits(int availableUnits) {
        this.availableUnits = availableUnits;
    }

    public int getAvailableUnits() {
        return availableUnits;
    }

    public BaseAdapterManager.SectionTypeEnum getTypeEnum() {
        return typeEnum;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
}

package com.nowfloats.signup.UI.Model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by NowFloatsDev on 25/05/2015.
 */
public class Get_FP_Details_Model implements Serializable {

    public String AliasTag;
    public String City;
    public String ExternalSourceId;
    public String ImageUri;
    public String Name;
    public String Tag;
    public String AppExperienceCode;
    public String TileImageUri;
    public String _id;
    public String Address;

    public ArrayList<CategoryModel> Category;
    //    public String Contact;
    public String ContactName;

    public ArrayList<ContactDetailsModel> Contacts;

    public String CountryPhoneCode;
    public String Description;
    public String Email;
    public String ExternalSourceName;
    public String FBPageName;
    public String IsVerified;
    public String OverallRating;
    public String PanaromaId;
    public String ParentId;
    public String PrimaryNumber;
    public String Pwd;
    public String Ratings;
    public String SearchTags;
    public ArrayList<String> SecondaryImages;
    public ArrayList<String> SecondaryTileImages;
    public ArrayList<TimingDetailsModel> Timings;
    public String Uri;
    public String UriDescription;
    public String errorRadius;
    public String height;
    public String lat;
    public String lng;
    public String EnterpriseEmailContact;
    public ArrayList<FPLocal> FPLocalWidgets;
    public ArrayList<String> FPWebWidgets;

    public String IsStoreFront;
    public String LogoUrl;
    public String ParentPrimaryNumber;
    public String RootAliasUri;
    public String TinyLogoUrl;
    public String WebKeywords;
    public String WebTemplateType;
    public String ApplicationId;
    public String Country;
    public String CreatedOn;
    public String FaviconUrl;
    public String GADomain;
    public String GAToken;
    public String IsBulkSubscription;
    public String LanguageCode;
    public ArrayList<NfxTokenModel> NFXAccessTokens;
    public ArrayList<String> PackageIds;
    public String PaymentLevel;
    public String PaymentState;
    public String PinCode;
    public String SMSGatewayUri;
    public String AccountManagerId;
    public String ExpiryDate;
    public String WebTemplateId;
    public String response;
    public String domainName;
    public String domainType;
    public String ProductCategoryVerb;
    public String DomainValidityInYears;

    public String getProductCategoryVerb() {
        return ProductCategoryVerb;
    }

    public void setProductCategoryVerb(String productCategoryVerb) {
        ProductCategoryVerb = productCategoryVerb;
    }

    public String getDomainValidityInYears() {
        return DomainValidityInYears;
    }

    public void setDomainValidityInYears(String domainValidityInYears) {
        DomainValidityInYears = domainValidityInYears;
    }

    public String getAliasTag() {
        return AliasTag;
    }

    public void setAliasTag(String aliasTag) {
        AliasTag = aliasTag;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getExternalSourceId() {
        return ExternalSourceId;
    }

    public void setExternalSourceId(String externalSourceId) {
        ExternalSourceId = externalSourceId;
    }

    public String getImageUri() {
        return ImageUri;
    }

    public void setImageUri(String imageUri) {
        ImageUri = imageUri;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getTag() {
        return Tag;
    }

    public void setTag(String tag) {
        Tag = tag;
    }

    public String getTileImageUri() {
        return TileImageUri;
    }

    public void setTileImageUri(String tileImageUri) {
        TileImageUri = tileImageUri;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public ArrayList<CategoryModel> getCategory() {
        if (Category != null) return Category;
        else return new ArrayList<>();
    }

    public String getFpCategory() {
        if (!getCategory().isEmpty()) return getCategory().get(0).getKey();
        else return "";
    }

    public void setCategory(ArrayList<CategoryModel> category) {
        Category = category;
    }

    public String getContactName() {
        return ContactName;
    }

    public void setContactName(String contactName) {
        ContactName = contactName;
    }

    public ArrayList<ContactDetailsModel> getContacts() {
        return Contacts;
    }

    public void setContacts(ArrayList<ContactDetailsModel> contacts) {
        Contacts = contacts;
    }

    public String getCountryPhoneCode() {
        return CountryPhoneCode;
    }

    public void setCountryPhoneCode(String countryPhoneCode) {
        CountryPhoneCode = countryPhoneCode;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getExternalSourceName() {
        return ExternalSourceName;
    }

    public void setExternalSourceName(String externalSourceName) {
        ExternalSourceName = externalSourceName;
    }

    public String getFBPageName() {
        return FBPageName;
    }

    public void setFBPageName(String FBPageName) {
        this.FBPageName = FBPageName;
    }

    public String getIsVerified() {
        return IsVerified;
    }

    public void setIsVerified(String isVerified) {
        IsVerified = isVerified;
    }

    public String getOverallRating() {
        return OverallRating;
    }

    public void setOverallRating(String overallRating) {
        OverallRating = overallRating;
    }

    public String getPanaromaId() {
        return PanaromaId;
    }

    public void setPanaromaId(String panaromaId) {
        PanaromaId = panaromaId;
    }

    public String getParentId() {
        return ParentId;
    }

    public void setParentId(String parentId) {
        ParentId = parentId;
    }

    public String getPrimaryNumber() {
        return PrimaryNumber;
    }

    public void setPrimaryNumber(String primaryNumber) {
        PrimaryNumber = primaryNumber;
    }

    public String getPwd() {
        return Pwd;
    }

    public void setPwd(String pwd) {
        Pwd = pwd;
    }

    public String getRatings() {
        return Ratings;
    }

    public void setRatings(String ratings) {
        Ratings = ratings;
    }

    public String getSearchTags() {
        return SearchTags;
    }

    public void setSearchTags(String searchTags) {
        SearchTags = searchTags;
    }

    public ArrayList<String> getSecondaryImages() {
        return SecondaryImages;
    }

    public void setSecondaryImages(ArrayList<String> secondaryImages) {
        SecondaryImages = secondaryImages;
    }

    public ArrayList<String> getSecondaryTileImages() {
        return SecondaryTileImages;
    }

    public void setSecondaryTileImages(ArrayList<String> secondaryTileImages) {
        SecondaryTileImages = secondaryTileImages;
    }

    public ArrayList<TimingDetailsModel> getTimings() {
        return Timings;
    }

    public void setTimings(ArrayList<TimingDetailsModel> timings) {
        Timings = timings;
    }

    public String getUri() {
        return Uri;
    }

    public void setUri(String uri) {
        Uri = uri;
    }

    public String getUriDescription() {
        return UriDescription;
    }

    public void setUriDescription(String uriDescription) {
        UriDescription = uriDescription;
    }

    public String getErrorRadius() {
        return errorRadius;
    }

    public void setErrorRadius(String errorRadius) {
        this.errorRadius = errorRadius;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getEnterpriseEmailContact() {
        return EnterpriseEmailContact;
    }

    public void setEnterpriseEmailContact(String enterpriseEmailContact) {
        EnterpriseEmailContact = enterpriseEmailContact;
    }

    public ArrayList<FPLocal> getFPLocalWidgets() {
        return FPLocalWidgets;
    }

    public void setFPLocalWidgets(ArrayList<FPLocal> FPLocalWidgets) {
        this.FPLocalWidgets = FPLocalWidgets;
    }

    public ArrayList<String> getFPWebWidgets() {
        return FPWebWidgets;
    }

    public void setFPWebWidgets(ArrayList<String> FPWebWidgets) {
        this.FPWebWidgets = FPWebWidgets;
    }

    public String getIsStoreFront() {
        return IsStoreFront;
    }

    public void setIsStoreFront(String isStoreFront) {
        IsStoreFront = isStoreFront;
    }

    public String getLogoUrl() {
        return LogoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        LogoUrl = logoUrl;
    }

    public String getParentPrimaryNumber() {
        return ParentPrimaryNumber;
    }

    public void setParentPrimaryNumber(String parentPrimaryNumber) {
        ParentPrimaryNumber = parentPrimaryNumber;
    }

    public String getRootAliasUri() {
        return RootAliasUri;
    }

    public void setRootAliasUri(String rootAliasUri) {
        RootAliasUri = rootAliasUri;
    }

    public String getTinyLogoUrl() {
        return TinyLogoUrl;
    }

    public void setTinyLogoUrl(String tinyLogoUrl) {
        TinyLogoUrl = tinyLogoUrl;
    }

    public String getWebKeywords() {
        return WebKeywords;
    }

    public void setWebKeywords(String webKeywords) {
        WebKeywords = webKeywords;
    }

    public String getWebTemplateType() {
        return WebTemplateType;
    }

    public void setWebTemplateType(String webTemplateType) {
        WebTemplateType = webTemplateType;
    }

    public String getWebTemplateId() {
        return WebTemplateId;
    }

    public void setWebTemplateId(String webTemplateId) {
        WebTemplateId = webTemplateId;
    }

    public String getApplicationId() {
        if (ApplicationId != null) return ApplicationId;
        else return "";
    }

    public void setApplicationId(String applicationId) {
        ApplicationId = applicationId;
    }

    public String getCountry() {
        if (Country != null) return Country;
        else return "";
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getCreatedOn() {
        return CreatedOn;
    }

    public void setCreatedOn(String createdOn) {
        CreatedOn = createdOn;
    }

    public String getFaviconUrl() {
        return FaviconUrl;
    }

    public void setFaviconUrl(String faviconUrl) {
        FaviconUrl = faviconUrl;
    }

    public String getGADomain() {
        return GADomain;
    }

    public void setGADomain(String GADomain) {
        this.GADomain = GADomain;
    }

    public String getGAToken() {
        return GAToken;
    }

    public void setGAToken(String GAToken) {
        this.GAToken = GAToken;
    }

    public String getIsBulkSubscription() {
        return IsBulkSubscription;
    }

    public void setIsBulkSubscription(String isBulkSubscription) {
        IsBulkSubscription = isBulkSubscription;
    }

    public String getLanguageCode() {
        return LanguageCode;
    }

    public void setLanguageCode(String languageCode) {
        LanguageCode = languageCode;
    }

    public ArrayList<NfxTokenModel> getNFXAccessTokens() {
        return NFXAccessTokens;
    }

    public void setNFXAccessTokens(ArrayList<NfxTokenModel> NFXAccessTokens) {
        this.NFXAccessTokens = NFXAccessTokens;
    }

    public ArrayList<String> getPackageIds() {
        return PackageIds;
    }

    public void setPackageIds(ArrayList<String> packageIds) {
        PackageIds = packageIds;
    }

    public String getPaymentLevel() {
        return PaymentLevel;
    }

    public void setPaymentLevel(String paymentLevel) {
        PaymentLevel = paymentLevel;
    }

    public String getPaymentState() {
        return PaymentState;
    }

    public void setPaymentState(String paymentState) {
        PaymentState = paymentState;
    }

    public String getPinCode() {
        return PinCode;
    }

    public void setPinCode(String pinCode) {
        PinCode = pinCode;
    }

    public String getSMSGatewayUri() {
        return SMSGatewayUri;
    }

    public void setSMSGatewayUri(String SMSGatewayUri) {
        this.SMSGatewayUri = SMSGatewayUri;
    }

    public String getAccountManagerId() {
        if (AccountManagerId != null) return AccountManagerId;
        else return "";
    }

    public void setAccountManagerId(String accountManagerId) {
        AccountManagerId = accountManagerId;
    }

    public String getExpiryDate() {
        return ExpiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        ExpiryDate = expiryDate;
    }

    public String getDomainType() {
        return domainType;
    }

    public void setDomainType(String domainType) {
        this.domainType = domainType;
    }
}

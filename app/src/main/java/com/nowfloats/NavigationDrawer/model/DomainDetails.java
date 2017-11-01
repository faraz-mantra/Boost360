package com.nowfloats.NavigationDrawer.model;

/**
 * Created by NowFloats on 04-11-2016.
 */

public class DomainDetails {
    public enum DOMAIN_RESPONSE{
        ERROR,NO_DATA,DATA
    };
    private String primaryNumber;
    public DOMAIN_RESPONSE response;
    private String DomainOwnerId;

    private String state;

    private String lng;

    private String city;

    private String _id;

    private String DomainContactId;

    private String lat;

    private String fpTag;

    private String zip;

    private String regService;

    private String primaryCategory;

    private String ExternalHostedZoneId;

    private String countryCode;

    private String domainName;

    private String existingFPTag;

    private String companyName;

    private String addressLine1;

    private String clientId;

    private String country;

    private String domainType;

    private String ErrorMessage;

    private String email;

    private String DomainRegService;

    private String ActivatedOn;

    private Integer validityInYears;

    public String getActivatedOn() {
        return ActivatedOn;
    }

    public void setActivatedOn(String activatedOn) {
        ActivatedOn = activatedOn;
    }

    public int getValidityInYears() {
        return validityInYears;
    }

    public void setValidityInYears(int validityInYears) {
        this.validityInYears = validityInYears;
    }

    private String DomainRegistrationOrderId;

    private String contactName;

    private String CreatedOn;

    private String ProcessingStatus;

    private String phoneISDCode;

    private boolean isProcessingFailed;

    public String getPrimaryNumber() {
        return primaryNumber;
    }

    public void setPrimaryNumber(String primaryNumber) {
        this.primaryNumber = primaryNumber;
    }

    public String getDomainOwnerId() {
        return DomainOwnerId;
    }

    public void setDomainOwnerId(String DomainOwnerId) {
        this.DomainOwnerId = DomainOwnerId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getDomainContactId() {
        return DomainContactId;
    }

    public void setDomainContactId(String DomainContactId) {
        this.DomainContactId = DomainContactId;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getFpTag() {
        return fpTag;
    }

    public void setFpTag(String fpTag) {
        this.fpTag = fpTag;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getRegService() {
        return regService;
    }

    public void setRegService(String regService) {
        this.regService = regService;
    }

    public String getPrimaryCategory() {
        return primaryCategory;
    }

    public void setPrimaryCategory(String primaryCategory) {
        this.primaryCategory = primaryCategory;
    }

    public String getExternalHostedZoneId() {
        return ExternalHostedZoneId;
    }

    public void setExternalHostedZoneId(String ExternalHostedZoneId) {
        this.ExternalHostedZoneId = ExternalHostedZoneId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getExistingFPTag() {
        return existingFPTag;
    }

    public void setExistingFPTag(String existingFPTag) {
        this.existingFPTag = existingFPTag;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDomainType() {
        return domainType;
    }

    public void setDomainType(String domainType) {
        this.domainType = domainType;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(String ErrorMessage) {
        this.ErrorMessage = ErrorMessage;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDomainRegService() {
        return DomainRegService;
    }

    public void setDomainRegService(String DomainRegService) {
        this.DomainRegService = DomainRegService;
    }

    public String getDomainRegistrationOrderId() {
        return DomainRegistrationOrderId;
    }

    public void setDomainRegistrationOrderId(String DomainRegistrationOrderId) {
        this.DomainRegistrationOrderId = DomainRegistrationOrderId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getCreatedOn() {
        return CreatedOn;
    }

    public void setCreatedOn(String CreatedOn) {
        this.CreatedOn = CreatedOn;
    }

    public String getProcessingStatus() {
        return ProcessingStatus;
    }

    public void setProcessingStatus(String ProcessingStatus) {
        this.ProcessingStatus = ProcessingStatus;
    }

    public String getPhoneISDCode() {
        return phoneISDCode;
    }

    public void setPhoneISDCode(String phoneISDCode) {
        this.phoneISDCode = phoneISDCode;
    }

    public boolean getIsProcessingFailed() {
        return isProcessingFailed;
    }

    public void setIsProcessingFailed(boolean isProcessingFailed) {
        this.isProcessingFailed = isProcessingFailed;
    }

    @Override
    public String toString() {
        return "ClassPojo [primaryNumber = " + primaryNumber + ", DomainOwnerId = " + DomainOwnerId + ", state = " + state + ", lng = " + lng + ", city = " + city + ", _id = " + _id + ", DomainContactId = " + DomainContactId + ", lat = " + lat + ", fpTag = " + fpTag + ", zip = " + zip + ", regService = " + regService + ", primaryCategory = " + primaryCategory + ", ExternalHostedZoneId = " + ExternalHostedZoneId + ", countryCode = " + countryCode + ", domainName = " + domainName + ", existingFPTag = " + existingFPTag + ", companyName = " + companyName + ", addressLine1 = " + addressLine1 + ", clientId = " + clientId + ", country = " + country + ", domainType = " + domainType + ", ErrorMessage = " + ErrorMessage + ", email = " + email + ", DomainRegService = " + DomainRegService + ", DomainRegistrationOrderId = " + DomainRegistrationOrderId + ", contactName = " + contactName + ", CreatedOn = " + CreatedOn + ", ProcessingStatus = " + ProcessingStatus + ", phoneISDCode = " + phoneISDCode + ", isProcessingFailed = " + isProcessingFailed + "]";
    }
}

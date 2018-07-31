package nfkeyboard.models.networkmodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import nfkeyboard.models.AllSuggestionModel;

/**
 * Created by Shimona on 07-06-2018.
 */

public class Details {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("businessName")
    @Expose
    private String businessName;

    @SerializedName("phoneNumber")
    @Expose
    private String phoneNumber;

    @SerializedName("website")
    @Expose
    private String website;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("location")
    @Expose
    private String location;

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


    public AllSuggestionModel toAllSuggestion() {
        AllSuggestionModel model = new AllSuggestionModel(name, businessName, website);
        model.setName(getName());
        model.setBusinessName(getBusinessName());
        model.setEmail(getEmail());
        model.setWebsite(getWebsite());
        model.setPhoneNumber(getPhoneNumber());
        model.setAddress(getAddress());
        model.setLocation(getLocation());
        return model;
    }

}

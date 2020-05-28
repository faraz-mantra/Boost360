package nfkeyboard.network;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CustomerDetails {

    public String AliasTag;
    public String City;
    public String ExternalSourceId;
    public String ImageUri;
    public String Name;
    public String Tag;
    public String Address;
    //    public String Contact;
    public String ContactName;
    public String Email;
    public String PrimaryNumber;
    public double lat;
    public double lng;
    public String EnterpriseEmailContact;
    public ArrayList<String> FPWebWidgets;
    public String Country;

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

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getContactName() {
        return ContactName;
    }

    public void setContactName(String contactName) {
        ContactName = contactName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPrimaryNumber() {
        return PrimaryNumber;
    }

    public void setPrimaryNumber(String primaryNumber) {
        PrimaryNumber = primaryNumber;
    }


    public String getEnterpriseEmailContact() {
        return EnterpriseEmailContact;
    }

    public void setEnterpriseEmailContact(String enterpriseEmailContact) {
        EnterpriseEmailContact = enterpriseEmailContact;
    }

    public ArrayList<String> getFPWebWidgets() {
        return FPWebWidgets;
    }

    public void setFPWebWidgets(ArrayList<String> FPWebWidgets) {
        this.FPWebWidgets = FPWebWidgets;
    }







}
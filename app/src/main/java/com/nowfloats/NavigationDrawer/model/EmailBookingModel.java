package com.nowfloats.NavigationDrawer.model;

import androidx.annotation.NonNull;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nowfloats.AccrossVerticals.domain.DomainDetailsActivity;

import java.util.List;

/**
 * Created by Admin on 22-11-2017.
 */

public class EmailBookingModel {

    @SerializedName("fpTag")
    @Expose
    private String fpTag;
    @SerializedName("emailDomainNames")
    @Expose
    private List<EmailDomainName> emailDomainNames = null;

    public String getFpTag() {
        return fpTag;
    }

    public void setFpTag(String fpTag) {
        this.fpTag = fpTag;
    }

    public List<EmailDomainName> getEmailDomainNames() {
        return emailDomainNames;
    }

    public void setEmailDomainNames(List<EmailDomainName> emailDomainNames) {
        this.emailDomainNames = emailDomainNames;
    }

    public static class EmailDomainName {

        @SerializedName("firstName")
        @Expose
        private String firstName;
        @SerializedName("lastName")
        @Expose
        private String lastName;
        @SerializedName("username")
        @Expose
        private String username;
        @SerializedName("password")
        @Expose
        private String password;

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof EmailDomainName)) {
                return false;
            }
            EmailDomainName emailDomainName = (EmailDomainName) obj;
            if (TextUtils.isEmpty(emailDomainName.getUsername())) {
                return false;
            } else if (!TextUtils.isEmpty(username) && username.equalsIgnoreCase(emailDomainName.getUsername())) {
                return true;
            }
            return false;

        }
    }

    public static class EmailBookingStatus {

        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("passwd")
        @Expose
        private String passwd;
        @SerializedName("isBooked")
        @Expose
        private Boolean isBooked;
        @SerializedName("expiryDate")
        @Expose
        private String expiryDate;
        @SerializedName("bookingStatus")
        @Expose
        private Integer bookingStatus;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPasswd() {
            return passwd;
        }

        public void setPasswd(String passwd) {
            this.passwd = passwd;
        }

        public Boolean getIsBooked() {
            return isBooked;
        }

        public void setIsBooked(Boolean isBooked) {
            this.isBooked = isBooked;
        }

        public String getExpiryDate() {
            return expiryDate;
        }

        public void setExpiryDate(String expiryDate) {
            this.expiryDate = expiryDate;
        }

        public Integer getBookingStatus() {
            return bookingStatus;
        }

        public void setBookingStatus(Integer bookingStatus) {
            this.bookingStatus = bookingStatus;
        }

    }

    public static class AddEmailModel implements Comparable {
        private String emailId;
        private DomainDetailsActivity.EmailType type = DomainDetailsActivity.EmailType.ADDED;

        public AddEmailModel(String emailId, DomainDetailsActivity.EmailType type) {
            this.emailId = emailId;
            this.type = type;
        }

        public DomainDetailsActivity.EmailType getType() {
            return type;
        }

        public void setType(DomainDetailsActivity.EmailType type) {
            this.type = type;
        }

        public String getEmailId() {
            return emailId;
        }

        public void setEmailId(String emailId) {
            this.emailId = emailId;
        }

        @Override
        public int compareTo(@NonNull Object o) {
            if (!(o instanceof AddEmailModel)) {
                return -1;
            } else {
                return type.compareTo(((AddEmailModel) o).getType());
            }
        }
    }

    public static class EmailBookingIds {
        private List<String> emailIds;

        public List<String> getEmailIds() {
            return emailIds;
        }

        public void setEmailIds(List<String> emailIds) {
            this.emailIds = emailIds;
        }
    }

}



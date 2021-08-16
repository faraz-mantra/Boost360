package com.nowfloats.ProductGallery.Model;

import com.google.gson.annotations.SerializedName;

public class BankInformation {

    @SerializedName("GSTN")
    public String gstn;
    @SerializedName("BankAccount")
    public BankAccount bankAccount;
    @SerializedName("SellerId")
    public String sellerId;

    public static class BankAccount {
        @SerializedName("Number")
        public String number;
        @SerializedName("IFSC")
        public String ifsc;
    }
}

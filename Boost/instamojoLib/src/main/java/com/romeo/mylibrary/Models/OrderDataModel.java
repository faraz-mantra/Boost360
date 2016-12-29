package com.romeo.mylibrary.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.MathContext;
import java.util.StringTokenizer;

/**
 * Created by NowFloats on 30-09-2016.
 */

public class OrderDataModel implements Parcelable {

    private String mUsername;
    private String mBusinessName;
    private String mEmail;
    private String mPrice;
    private String mExpires;
    private String mPhNo;
    private String mReason;
    private String mCurrency;

    public OrderDataModel(String mUsername, String mBusinessName, String mEmail, String mPrice, String mExpires, String mPhNo, String mReason, String mCurrency) {
        this.mUsername = mUsername;
        this.mBusinessName = mBusinessName;
        this.mEmail = mEmail;
        this.mPrice = mPrice;
        this.mExpires = mExpires;
        this.mPhNo = mPhNo;
        this.mReason = mReason;
        this.mCurrency = mCurrency;
    }

    public String getUsername() {
        return mUsername;
    }

    public String getBusinessName() {
        return mBusinessName;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getPrice() {
        return mPrice;
    }

    public String getExpires() {
        return mExpires;
    }

    public String getPhNo() {
        return mPhNo;
    }

    public String getReason() {
        return mReason;
    }
    public String getCurrency(){
        return mCurrency;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mUsername);
        parcel.writeString(mBusinessName);
        parcel.writeString(mEmail);
        parcel.writeString(mPrice);
        parcel.writeString(mExpires);
        parcel.writeString(mPhNo);
        parcel.writeString(mReason);
        parcel.writeString(mCurrency);

    }

    public static final Creator<OrderDataModel> CREATOR = new Creator<OrderDataModel>(){

        @Override
        public OrderDataModel createFromParcel(Parcel parcel) {
            return new OrderDataModel(parcel);
        }

        @Override
        public OrderDataModel[] newArray(int i) {
            return new OrderDataModel[i];
        }
    };

    private OrderDataModel(Parcel in){
        this.mUsername = in.readString();
        this.mBusinessName = in.readString();
        this.mEmail = in.readString();
        this.mPrice = in.readString();
        this.mExpires = in.readString();
        this.mPhNo = in.readString();
        this.mReason = in.readString();
        this.mCurrency = in.readString();
    }

}

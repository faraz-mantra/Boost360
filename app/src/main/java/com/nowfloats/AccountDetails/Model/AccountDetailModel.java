package com.nowfloats.AccountDetails.Model;

/**
 * Created by guru on 07-07-2015.
 */
public class AccountDetailModel {
    public String NameOfWidget;
    public String clientId;
    public String clientProductId;
    public String currencyCode;
    public String fpId;
    public String paidAmount;
    public String totalMonthsValidity;
    public String widgetKey;
    public String CreatedOn;
    public String _id;
    public Boolean isActive;
    public String ToBeActivatedOn;
    public PurchasedPackageDetails purchasedPackageDetails;

    public class PurchasedPackageDetails{
        public Integer packType;
    }
}

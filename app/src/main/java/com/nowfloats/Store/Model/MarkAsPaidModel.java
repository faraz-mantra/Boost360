package com.nowfloats.Store.Model;

/**
 * Created by NowFloats on 10-11-2016.
 */

public class MarkAsPaidModel {
    public String ClientId;
    public double ExpectedAmount;
    public String FpId;
    public String FpTag;
    public boolean IsPaid;
    public String currencyCode;
    public int type;
    public String packageId;
    public int validityInMths;
    public ERPRequestModel customerSalesOrderRequest;
}

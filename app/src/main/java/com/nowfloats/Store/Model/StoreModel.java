package com.nowfloats.Store.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guru on 29-04-2015.
 */
public class StoreModel {
    public String CurrencyCode;
    public String Desc;
    public String Name;
    public String Price;
    public String PrimaryImageUri;
    public ArrayList<Screenshots> Screenshots;
    public ArrayList<ProductId> ExternalApplicationDetails;
    public String Type;
    public int ValidityInMths;
    public ArrayList<WidgetPacks> WidgetPacks;
    public String CreatedOn;
    public String ExpiryInMths;
    public String Identifier;
    public String IsArchived;
    public String _id;
    public List<SupportedPaymentMethods> SupportedPaymentMethods;
    public List<TaxDetail> Taxes;
    public String packType;
}

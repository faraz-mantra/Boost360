package com.nowfloats.Product_Gallery.Model;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;

public class Product implements Serializable {

    public String BuyOnlineLink;
    public String CurrencyCode;
    public String Description;
    public String DiscountAmount;
    public String ExternalSourceId;
    public String IsArchived;
    public String IsAvailable;
    public String IsFreeShipmentAvailable;
    public String Name;
    public String Price;
    public String Priority;
    public String ShipmentDuration;
    public int availableUnits;
    public ArrayList<String> _keywords;
    public String ApplicationId;
    public String FPTag;
    public String ImageUri;
    public String ProductUrl;
    public ArrayList<ImageListModel> Images;
    public String MerchantName;
    public String TileImageUri;
    public String _id;
    public String GPId;
    public String TotalQueries;
    public String CreatedOn;
    public String ProductIndex;
    public Uri picimageURI =null;
    public String UpdatedOn;
    public boolean isProductSelected;
}
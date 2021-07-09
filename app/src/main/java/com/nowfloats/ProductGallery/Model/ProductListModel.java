package com.nowfloats.ProductGallery.Model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by guru on 08-06-2015.
 */
public class ProductListModel implements Parcelable {
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(BuyOnlineLink);
        parcel.writeString(CurrencyCode);
        parcel.writeString(Description);
        parcel.writeString(DiscountAmount);
        parcel.writeString(ExternalSourceId);
        parcel.writeString(IsArchived);
        parcel.writeString(ProductUrl);
        parcel.writeString(IsAvailable);
        parcel.writeString(IsFreeShipmentAvailable);
        parcel.writeString(Name);
        parcel.writeString(Price);
        parcel.writeString(Priority);
        parcel.writeString(ShipmentDuration);
        parcel.writeStringList(_keywords);
        parcel.writeString(ApplicationId);
        parcel.writeString(FPTag);
        parcel.writeString(ImageUri);
        parcel.writeTypedList(Images);
        parcel.writeString(MerchantName);
        parcel.writeString(TileImageUri);
        parcel.writeString(_id);
        parcel.writeString(GPId);
        parcel.writeString(TotalQueries);
        parcel.writeString(CreatedOn);
        parcel.writeString(UpdatedOn);
        parcel.writeString(ProductIndex);
        parcel.writeInt(availableUnits);
    }

    public ProductListModel(Parcel in) {
        this.BuyOnlineLink = in.readString();
        this.CurrencyCode = in.readString();
        this.Description = in.readString();
        this.DiscountAmount = in.readString();
        this.ExternalSourceId = in.readString();
        this.IsArchived = in.readString();
        this.IsAvailable = in.readString();
        this.IsFreeShipmentAvailable = in.readString();
        this.Name = in.readString();
        this.Price = in.readString();
        this.ProductUrl = in.readString();
        this.Priority = in.readString();
        this.ShipmentDuration = in.readString();
        this._keywords = in.readArrayList(ProductListModel.this.getClass().getClassLoader());
        this.ApplicationId = in.readString();
        this.FPTag = in.readString();
        this.ImageUri = in.readString();
        this.Images = new ArrayList<ImageListModel>();
        in.readTypedList(Images, ImageListModel.CREATOR);
        this.MerchantName = in.readString();
        this.TileImageUri = in.readString();
        this._id = in.readString();
        this.GPId = in.readString();
        this.TotalQueries = in.readString();
        this.CreatedOn = in.readString();
        this.UpdatedOn = in.readString();
        this.ProductIndex = in.readString();
        this.availableUnits = in.readInt();
    }

    public static final Parcelable.Creator<ProductListModel> CREATOR = new Parcelable.Creator<ProductListModel>() {
        public ProductListModel createFromParcel(Parcel in) {
            return new ProductListModel(in);
        }
        public ProductListModel[] newArray(int size) {
            return new ProductListModel[size];
        }
    };
}

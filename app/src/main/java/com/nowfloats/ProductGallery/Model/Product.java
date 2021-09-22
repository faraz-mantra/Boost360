package com.nowfloats.ProductGallery.Model;

import android.net.Uri;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import org.w3c.dom.Text;

import com.nowfloats.helper.Helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Product implements Serializable {

  @SerializedName(value = "currencycode", alternate = {"CurrencyCode"})
  public String CurrencyCode;
  @SerializedName(value = "description", alternate = {"Description"})
  public String Description;
  @SerializedName(value = "discountAmount", alternate = {"DiscountAmount"})
  public double DiscountAmount;
  public String ExternalSourceId;
  public String IsArchived;
  @SerializedName(value = "isAvailable", alternate = {"IsAvailable"})
  public boolean IsAvailable;
  @SerializedName(value = "isFreeShipmentAvailable", alternate = {"IsFreeShipmentAvailable"})
  public String IsFreeShipmentAvailable;
  @SerializedName(value = "name", alternate = {"Name"})
  public String Name;
  @SerializedName(value = "price", alternate = {"Price"})
  public double Price;
  @SerializedName(value = "priority", alternate = {"Priority"})
  public String Priority;
  public String ShipmentDuration;
  public int availableUnits = 1;
  public ArrayList<String> _keywords;
  public List<String> tags;
  public String ApplicationId;
  @SerializedName(value = "fpTag", alternate = {"FPTag"})
  public String FPTag;
  @SerializedName(value = "clientId", alternate = {"ClientId"})
  public String ClientId;

  public String ImageUri;
  public String ProductUrl;
  public ArrayList<ImageListModel> Images;
  public String MerchantName;
  public String TileImageUri;
  @SerializedName("_id")
  public String productId;
  public String GPId;
  public String TotalQueries;
  public String CreatedOn;
  public String ProductIndex;
  public Uri picimageURI = null;
  public String UpdatedOn;
  public boolean isProductSelected;

  public String productType;
  public String paymentType;
  public boolean variants;
  public String brandName;
  public String category;

  @SerializedName("isCodAvailable")
  public boolean codAvailable = false;
  @SerializedName("maxCodOrders")
  public int maxCodOrders = 1;
  @SerializedName("isPrepaidOnlineAvailable")
  public boolean prepaidOnlineAvailable = false;
  @SerializedName("maxPrepaidOnlineOrders")
  public int maxPrepaidOnlineAvailable = 1;

  @SerializedName("uniquePaymentUrl")
  public BuyOnlineLink BuyOnlineLink;
  public Specification keySpecification;
  @SerializedName("otherSpecifications")
  public List<Specification> otherSpecification;
  public String pickupAddressReferenceId;

  public void setProductType(String productType) {
    this.productType = productType;
  }

  public String getVariantDetail() {
    if (keySpecification != null && !TextUtils.isEmpty(keySpecification.key) && !TextUtils.isEmpty(keySpecification.value)) {
      return "- (" + keySpecification.key + " - " + keySpecification.value + ")";
    } else {
      return "";
    }
  }

  //for sharing data
  public List<String> getAllImage() {
    List<String> images = new ArrayList<>();
    images.add(ImageUri);
    if (Images != null && !Images.isEmpty()) {
      for (ImageListModel imageListModel : Images) {
        if (!TextUtils.isEmpty(imageListModel.ImageUri)) images.add(ImageUri);
      }
    }
    return images;
  }

  //for sharing data
  public String getFinalPriceWithCurrency() {
    return CurrencyCode + " " + Helper.getCurrencyFormatter().format(Price - DiscountAmount);
  }

  //for sharing data
  public String getActualPriceWithCurrency() {
    if (DiscountAmount != 0) {
      return "- ~" + CurrencyCode + " " + Helper.getCurrencyFormatter().format(Price) + "~";
    } else {
      return "";
    }
  }

  public static class BuyOnlineLink implements Serializable {
    public String url;
    public String description;
  }

  public static class Specification implements Serializable {
    public String key;
    public String value;
  }
}
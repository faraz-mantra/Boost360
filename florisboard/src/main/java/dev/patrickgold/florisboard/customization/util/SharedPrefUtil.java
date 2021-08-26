package dev.patrickgold.florisboard.customization.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.onboarding.nowfloats.model.digitalCard.DigitalCardData;
import com.onboarding.nowfloats.model.profile.MerchantProfileResponse;

import java.util.List;

import dev.patrickgold.florisboard.customization.model.response.DigitalCardDataKeyboard;
import dev.patrickgold.florisboard.customization.model.response.Photo;
import dev.patrickgold.florisboard.customization.model.response.Product;
import dev.patrickgold.florisboard.customization.model.response.Updates;
import dev.patrickgold.florisboard.customization.model.response.staff.StaffResult;

/**
 * Created by NowFloats on 26-02-2018.
 */

public class SharedPrefUtil {

  private SharedPreferences sBoostPref;
  private static SharedPrefUtil sPrefUtil;

  private SharedPrefUtil() {

  }

  public static SharedPrefUtil fromBoostPref() {
    if (sPrefUtil == null) {
      sPrefUtil = new SharedPrefUtil();
    }
    return sPrefUtil;
  }

  public SharedPrefUtil getsBoostPref(Context context) {
    sBoostPref = context.getSharedPreferences("nowfloatsPrefs", Context.MODE_PRIVATE);
    return sPrefUtil;
  }

  public String getFpId() {
    if (sBoostPref != null) {
      return sBoostPref.getString("fpid", null);
    }
    return null;
  }

  public String getFpTag() {
    if (sBoostPref != null) {
      return sBoostPref.getString("GET_FP_DETAILS_TAG", null);
    }
    return null;
  }

  public String getFPDetailsCreatedOn() {
    if (sBoostPref != null) {
      return sBoostPref.getString("GET_FP_DETAILS_CREATED_ON", null);
    }
    return null;
  }

  public String getProductVerb() {
    if (sBoostPref != null) {
      return sBoostPref.getString("GET_PRODUCT_CATEGORY_VERB", null);
    }
    return null;
  }

  public boolean isLoggedIn() {
    if (sBoostPref != null) {
      return sBoostPref.getBoolean("IsUserLoggedIn", false);
    }
    return false;
  }

  public String getLat() {
    if (sBoostPref != null) {
      return sBoostPref.getString("latitude", null);
    }
    return null;
  }

  public String getLong() {
    if (sBoostPref != null) {
      return sBoostPref.getString("longitude", null);
    }
    return null;
  }

  public String getName() {
    if (sBoostPref != null) {
      return sBoostPref.getString("GET_FP_DETAILS_CONTACTNAME", null);
    }
    return null;
  }

  public String getBusinessName() {
    if (sBoostPref != null) {
      return sBoostPref.getString("GET_FP_DETAILS_BUSINESS_NAME", null);
    }
    return null;
  }

  public String getPhoneNumber() {
    if (sBoostPref != null) {
      return sBoostPref.getString("GET_FP_DETAILS_PRIMARY_NUMBER", null);
    }
    return null;
  }

  public String getPrimaryContactNumber() {
    if (sBoostPref != null) {
      return sBoostPref.getString("mainPrimaryContactNum", null);
    }
    return null;
  }

  public String getWebsite() {
    if (sBoostPref != null) {
      String rootAlisasURI = sBoostPref.getString("GET_FP_DETAILS_ROOTALIASURI", null);
      if (sBoostPref.getString("GET_FP_DETAILS_TAG", null) != null) {
        String normalURI = "http://" + sBoostPref.getString("GET_FP_DETAILS_TAG", null).toLowerCase() + ".nowfloats.com";
        if (rootAlisasURI != null && !rootAlisasURI.equals("null") && rootAlisasURI.trim().length() > 0) {
          return rootAlisasURI;
        } else {
          return normalURI;
        }
      }
    }
    return null;
  }

  public String getEmail() {
    if (sBoostPref != null) {
      return sBoostPref.getString("GET_FP_DETAILS_EMAIL", null);
    }
    return null;
  }

  public String getAddress() {
    if (sBoostPref != null) {
      return sBoostPref.getString("GET_FP_DETAILS_ADDRESS", null);
    }
    return null;
  }

  public String getSecondaryNumber() {
    if (sBoostPref != null) {
      return sBoostPref.getString("GET_FP_DETAILS_ALTERNATE_NUMBER_1", null);
    }
    return null;
  }

  public String getCity() {
    if (sBoostPref != null) {
      return sBoostPref.getString("GET_FP_DETAILS_CITY", null);
    }
    return null;
  }

  public String getZipcode() {
    if (sBoostPref != null) {
      return sBoostPref.getString("GET_FP_DETAILS_PINCODE", null);
    }
    return null;
  }

  public String getCountry() {
    if (sBoostPref != null) {
      return sBoostPref.getString("GET_FP_DETAILS_COUNTRY", null);
    }
    return null;
  }

  public String getRootAliasURI() {
    if (sBoostPref != null) {
      return sBoostPref.getString("GET_FP_DETAILS_ROOTALIASURI", null);
    }
    return null;
  }

  public String getCountryPhoneCode() {
    if (sBoostPref != null) {
      return sBoostPref.getString("GET_FP_DETAILS_COUNTRYPHONECODE", null);
    }
    return null;
  }


  public Long getLastSyncTime(){
    if (sBoostPref != null) {
     String value= sBoostPref.getString(PrefConstants.INSTANCE.getLAST_SYNC_TIME(), null);
     if (value!=null){
       return Long.valueOf(value);
     }
    }
    return null;
  }
  public void setLastSyncTime(Long timeinmilis){
    if (sBoostPref != null) {
      sBoostPref.edit().putString(PrefConstants.INSTANCE.getLAST_SYNC_TIME(),timeinmilis.toString()).apply();
    }
  }

  public void save(String key,String value){
    if (sBoostPref != null) {
      sBoostPref.edit().putString(key,value).apply();
    }
  }

  public List<Product> getProductList(){
    if (sBoostPref!=null){
      return new Gson().fromJson(sBoostPref.getString(PrefConstants.INSTANCE.getPREF_PRODUCTS(),null),new TypeToken<List<Product>>(){}.getType());
    }else {
      return null;
    }
  }

  public Updates getUpdateList(){
    if (sBoostPref!=null){
      return new Gson().fromJson(sBoostPref.getString(PrefConstants.INSTANCE.getPREF_UPDATES(),null),new TypeToken<Updates>(){}.getType());
    }else {
      return null;
    }
  }


  public List<Photo> getPhotoList(){
    if (sBoostPref!=null){
      return new Gson().fromJson(sBoostPref.getString(PrefConstants.INSTANCE.getPREF_PHOTOS(),null),new TypeToken<List<Photo>>(){}.getType());
    }else {
      return null;
    }
  }

  public MerchantProfileResponse getBusinessCardList(){
    if (sBoostPref!=null){
      return new Gson().fromJson(sBoostPref.getString(PrefConstants.INSTANCE.getPREF_BUSINESS_CARD(),null),new TypeToken<MerchantProfileResponse>(){}.getType());
    }else {
      return null;
    }
  }
  public StaffResult getStaffList(){
    if (sBoostPref!=null){
      return new Gson().fromJson(sBoostPref.getString(PrefConstants.INSTANCE.getPREF_STAFF(),null),new TypeToken<StaffResult>(){}.getType());
    }else {
      return null;
    }
  }
}

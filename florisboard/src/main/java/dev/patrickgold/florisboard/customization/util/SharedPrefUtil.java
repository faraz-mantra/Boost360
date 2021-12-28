package dev.patrickgold.florisboard.customization.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.appservice.model.serviceProduct.service.ServiceSearchListingResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.onboarding.nowfloats.model.profile.MerchantProfileResponse;

import java.util.List;

import dev.patrickgold.florisboard.customization.model.response.Photo;
import dev.patrickgold.florisboard.customization.model.response.ProductResponse;
import dev.patrickgold.florisboard.customization.model.response.Updates;
import dev.patrickgold.florisboard.customization.model.response.staff.StaffResult;

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


  public Long getLastSyncTime() {
    if (sBoostPref != null) {
      String value = sBoostPref.getString(PrefConstants.INSTANCE.getLAST_SYNC_TIME(), null);
      if (value != null) {
        return Long.valueOf(value);
      }
    }
    return null;
  }

  public void setLastSyncTime(Long timeinmilis) {
    if (sBoostPref != null) {
      sBoostPref.edit().putString(PrefConstants.INSTANCE.getLAST_SYNC_TIME(), timeinmilis.toString()).apply();
    }
  }

  public void save(String key, String value) {
    if (sBoostPref != null) {
      sBoostPref.edit().putString(key, value).apply();
    }
  }

  public ProductResponse getProductList() {
    if (sBoostPref != null) {
      return new Gson().fromJson(sBoostPref.getString(PrefConstants.INSTANCE.getPREF_PRODUCTS(), null), new TypeToken<ProductResponse>() {
      }.getType());
    } else {
      return null;
    }
  }

  public ServiceSearchListingResponse getServiceList() {
    if (sBoostPref != null) {
      return new Gson().fromJson(sBoostPref.getString(PrefConstants.INSTANCE.getPREF_SERVICES(), null), new TypeToken<ServiceSearchListingResponse>() {
      }.getType());
    } else {
      return null;
    }
  }

  public Updates getUpdateList() {
    if (sBoostPref != null) {
      return new Gson().fromJson(sBoostPref.getString(PrefConstants.INSTANCE.getPREF_UPDATES(), null), new TypeToken<Updates>() {
      }.getType());
    } else {
      return null;
    }
  }


  public List<Photo> getPhotoList() {
    if (sBoostPref != null) {
      return new Gson().fromJson(sBoostPref.getString(PrefConstants.INSTANCE.getPREF_PHOTOS(), null), new TypeToken<List<Photo>>() {
      }.getType());
    } else {
      return null;
    }
  }

  public MerchantProfileResponse getBusinessCardList() {
    if (sBoostPref != null) {
      return new Gson().fromJson(sBoostPref.getString(PrefConstants.INSTANCE.getPREF_BUSINESS_CARD(), null), new TypeToken<MerchantProfileResponse>() {
      }.getType());
    } else {
      return null;
    }
  }

  public StaffResult getStaffList() {
    if (sBoostPref != null) {
      return new Gson().fromJson(sBoostPref.getString(PrefConstants.INSTANCE.getPREF_STAFF(), null), new TypeToken<StaffResult>() {
      }.getType());
    } else {
      return null;
    }
  }
}

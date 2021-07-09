package com.nowfloats.signup.UI.Model;

import android.app.Activity;
import android.util.Log;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.MixPanelController;

import java.util.ArrayList;

import static com.framework.utils.GsonUtilsKt.convertListObjToString;

/**
 * Created by NowFloatsDev on 25/05/2015.
 */
public class ProcessFPDetails {

  static UserSessionManager session;
  private static String WIDGET_IMAGE_GALLERY = "IMAGEGALLERY";
  private static String WIDGET_IMAGE_TOB = "TOB";
  private static String WIDGET_IMAGE_TIMINGS = "TIMINGS";
  private static String WIDGET_PRODUCT_GALLERY = "PRODUCTCATALOGUE";
  private static String WIDGET_FB_LIKE_BOX = "FbLikeBox";
  private static String WIDGET_CUSTOMPAGES = "CUSTOMPAGES";
  private static final String FP_WEB_WIDGET_DOMAIN = "DOMAINPURCHASE";

  public static void storeFPDetails(Activity activity, Get_FP_Details_Model get_fp_details_model) {
    try {
      MixPanelController.setProperties("ClientId", Constants.clientId);
      session = new UserSessionManager(activity.getApplicationContext(), activity);
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_CITY, get_fp_details_model.City);
      MixPanelController.setProperties("FPCity", get_fp_details_model.City);


      String name = get_fp_details_model.getContactName() == null ? "" : (get_fp_details_model.getContactName().toLowerCase().equals("null") ? "" : get_fp_details_model.getContactName());
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_CONTACTNAME, name);

      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_TAG, get_fp_details_model.Tag);
      session.storeFPDetails(Key_Preferences.GET_FP_EXPERIENCE_CODE, get_fp_details_model.AppExperienceCode);
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS, get_fp_details_model.Address);
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_TILE_IMAGE_URI, get_fp_details_model.TileImageUri);
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_IMAGE_URI, get_fp_details_model.ImageUri);
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_FAVICON_IMAGE_URI, get_fp_details_model.FaviconUrl);
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_CREATED_ON, get_fp_details_model.CreatedOn);
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_EXPIRY_DATE, get_fp_details_model.ExpiryDate);
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE, get_fp_details_model.PaymentState);
      session.storeFPDetails(Key_Preferences.LANGUAGE_CODE, get_fp_details_model.LanguageCode);
      session.storeFPDetails(Key_Preferences.EXTERNAL_SOURCE_ID, get_fp_details_model.ExternalSourceId);
      MixPanelController.setProperties("PaymentState", get_fp_details_model.PaymentState);
      MixPanelController.setProperties("PaymentLevel", get_fp_details_model.PaymentLevel);
      if ("0".equals(get_fp_details_model.lat) && "0".equals(get_fp_details_model.lng)) {
        session.storeFPDetails(Key_Preferences.LATITUDE, "");
        session.storeFPDetails(Key_Preferences.LONGITUDE, "");
      } else {
        session.storeFPDetails(Key_Preferences.LATITUDE, get_fp_details_model.lat);
        session.storeFPDetails(Key_Preferences.LONGITUDE, get_fp_details_model.lng);
      }
      try {
        Constants.latitude = Double.parseDouble(get_fp_details_model.lat);
        Constants.longitude = Double.parseDouble(get_fp_details_model.lng);
      } catch (Exception e) {
        e.printStackTrace();
      }
      if (get_fp_details_model.AccountManagerId != null && !get_fp_details_model.AccountManagerId.equals("null"))
        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ACCOUNTMANAGERID, get_fp_details_model.AccountManagerId);
      //String category = get_fp_details_model.Category.get(0);
      try {
        if (get_fp_details_model.Category != null && get_fp_details_model.Category.size() > 0) {
          session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY, get_fp_details_model.getCategory().get(0).getKey());
          MixPanelController.setProperties("Category", get_fp_details_model.getCategory().get(0).getKey());
        }
      } catch (Exception e) {
        e.printStackTrace();
        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY, "GENERAL");
        MixPanelController.setProperties("Category", "GENERAL");
      }

      //session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_CONTACTNAME, get_fp_details_model.ContactName);

      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL, get_fp_details_model.Email);
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRYPHONECODE, get_fp_details_model.CountryPhoneCode);
      MixPanelController.setProperties("CountryPhoneCode", get_fp_details_model.CountryPhoneCode);
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME, get_fp_details_model.Name);
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_DESCRIPTION, get_fp_details_model.Description);
      session.storeFPDetails(Key_Preferences.GET_FP_WEBTEMPLATE_ID, get_fp_details_model.getWebTemplateId());
      session.storeFpWebTempalteType(get_fp_details_model.WebTemplateType);
      if (get_fp_details_model.Description != null && get_fp_details_model.Description.length() == 0) {
        MixPanelController.setProperties("Business description", "False");
      } else {
        MixPanelController.setProperties("Business description", "True");
      }
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_FBPAGENAME, get_fp_details_model.FBPageName);
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_PARENTID, get_fp_details_model.ParentId);
      session.storeFPDetails(Key_Preferences.PRODUCT_CATEGORY, get_fp_details_model.getProductCategoryVerb());
      MixPanelController.setProperties("_id", get_fp_details_model.ParentId);
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY, get_fp_details_model.Country);
      MixPanelController.setProperties("FPCountry", get_fp_details_model.Country);
      try {
        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTLEVEL, get_fp_details_model.PaymentLevel);

        if (get_fp_details_model.PaymentLevel != null && get_fp_details_model.PaymentLevel.trim().length() > 0 &&
            Integer.parseInt(get_fp_details_model.PaymentLevel) >= 10) {
          MixPanelController.setProperties("NoAds", "True");
        } else {
          MixPanelController.setProperties("NoAds", "False");
        }
      } catch (Exception e) {
        e.printStackTrace();
      }

      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WEBSITE, get_fp_details_model.Uri);
      session.storeFPDetails(Key_Preferences.MAIN_PRIMARY_CONTACT_NUM, get_fp_details_model.PrimaryNumber);
      try {
        Util.GettingBackGroundId(session);
        if (get_fp_details_model.Contacts != null) {
          if (get_fp_details_model.Contacts.size() == 1) {
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER, get_fp_details_model.Contacts.get(0).ContactNumber);
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NAME, get_fp_details_model.Contacts.get(0).ContactName);
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NUMBER_1, "");
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NAME_1, "");
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NUMBER_3, "");
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NAME_3, "");
          } else if (get_fp_details_model.Contacts.size() == 2) {
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER, get_fp_details_model.Contacts.get(0).ContactNumber);
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NAME, get_fp_details_model.Contacts.get(0).ContactName);
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NUMBER_1, get_fp_details_model.Contacts.get(1).ContactNumber);
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NAME_1, get_fp_details_model.Contacts.get(1).ContactName);
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NUMBER_3, "");
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NAME_3, "");
          } else if (get_fp_details_model.Contacts.size() >= 3) {
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER, get_fp_details_model.Contacts.get(0).ContactNumber);
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NAME, get_fp_details_model.Contacts.get(0).ContactName);
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NUMBER_1, get_fp_details_model.Contacts.get(1).ContactNumber);
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NAME_1, get_fp_details_model.Contacts.get(1).ContactName);
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NUMBER_3, get_fp_details_model.Contacts.get(2).ContactNumber);
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NAME_3, get_fp_details_model.Contacts.get(2).ContactName);
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }

      ArrayList<String> widgetsList = get_fp_details_model.getFPWebWidgets();
      Constants.StorePackageIds = get_fp_details_model.PackageIds;
      Constants.StoreWidgets = get_fp_details_model.getFPWebWidgets();
      session.storeFPDetails(Key_Preferences.STORE_WIDGETS, convertListObjToString(get_fp_details_model.getFPWebWidgets()));
      Log.d("Constants.storeWidgets", "widgets : " + Constants.StoreWidgets + " " + Constants.StorePackageIds);
      Constants.storeSecondaryImages = get_fp_details_model.SecondaryTileImages;

//        for(String widget : widgetsList)
//        {
      if (widgetsList.contains(FP_WEB_WIDGET_DOMAIN)) {
        session.storeFPDetails(Key_Preferences.GET_FP_WEB_WIDGET_DOMAIN, FP_WEB_WIDGET_DOMAIN);
        MixPanelController.setProperties("ImageGallery", "True");
      } else {
        MixPanelController.setProperties("ImageGallery", "False");
        session.storeFPDetails(Key_Preferences.GET_FP_WEB_WIDGET_DOMAIN, "");
      }

      if (widgetsList.contains(WIDGET_IMAGE_GALLERY)) {
        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_IMAGE_GALLERY, WIDGET_IMAGE_GALLERY);
        MixPanelController.setProperties("ImageGallery", "True");
      } else {
        MixPanelController.setProperties("ImageGallery", "False");
        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_IMAGE_GALLERY, "");
      }
      if (widgetsList.contains(WIDGET_IMAGE_TIMINGS)) {
        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_IMAGE_TIMINGS, WIDGET_IMAGE_TIMINGS);
        MixPanelController.setProperties("Timings", "True");
      } else {
        MixPanelController.setProperties("Timings", "False");
        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_IMAGE_TIMINGS, "");
      }

      if (widgetsList.contains(WIDGET_IMAGE_TOB)) {
        MixPanelController.setProperties("TTB", "True");
        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_IMAGE_TOB, WIDGET_IMAGE_TOB);
      } else {
        MixPanelController.setProperties("TTB", "False");
        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_IMAGE_TOB, "");
      }

      if (widgetsList.contains(WIDGET_PRODUCT_GALLERY)) {
        MixPanelController.setProperties("Product Gallery", "True");
        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_PRODUCT_GALLERY, WIDGET_PRODUCT_GALLERY);
      } else {
        MixPanelController.setProperties("Product Gallery", "False");
        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_PRODUCT_GALLERY, "");
      }
      if (widgetsList.contains(WIDGET_FB_LIKE_BOX)) {
        MixPanelController.setProperties("FBLikeBox", "True");
        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_FB_LIKE_BOX, WIDGET_FB_LIKE_BOX);
      } else {
        MixPanelController.setProperties("FBLikeBox", "False");
        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_FB_LIKE_BOX, "");
      }
      if (widgetsList.contains(WIDGET_CUSTOMPAGES)) {
        MixPanelController.setProperties("CustomPages", "True");
        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_CUSTOMPAGES, WIDGET_CUSTOMPAGES);
      } else {
        MixPanelController.setProperties("CustomPages", "False");
        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_CUSTOMPAGES, "");
      }
//        }

      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_PINCODE, get_fp_details_model.PinCode);
      //TODO for testing
//            session.storeFacebookPageID(get_fp_details_model.FBPageName);
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI, get_fp_details_model.RootAliasUri);
      MixPanelController.setProperties("RootAlias", get_fp_details_model.RootAliasUri);
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_LogoUrl, get_fp_details_model.LogoUrl);

    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      if (get_fp_details_model.Timings != null) {
        session.storeBooleanDetails(Key_Preferences.IS_BUSINESS_TIME_AVAILABLE, true);

        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_SUNDAY_START_TIME, "00");
        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_SUNDAY_END_TIME, "00");

        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_START_TIME, "00");
        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_END_TIME, "00");

        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_START_TIME, "00");
        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_END_TIME, "00");

        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_START_TIME, "00");
        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_END_TIME, "00");

        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_START_TIME, "00");
        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_END_TIME, "00");

        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_START_TIME, "00");
        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_END_TIME, "00");

        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_START_TIME, "00");
        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_END_TIME, "00");
        for (int i = 0; i < get_fp_details_model.Timings.size(); i++) {
          if (i == 0) {
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_SUNDAY_START_TIME, get_fp_details_model.Timings.get(0).From);
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_SUNDAY_END_TIME, get_fp_details_model.Timings.get(0).To);
            session.setBusinessHours(true);
          } else if (i == 1) {
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_START_TIME, get_fp_details_model.Timings.get(1).From);
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_END_TIME, get_fp_details_model.Timings.get(1).To);
          } else if (i == 2) {
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_START_TIME, get_fp_details_model.Timings.get(2).From);
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_END_TIME, get_fp_details_model.Timings.get(2).To);
          } else if (i == 3) {
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_START_TIME, get_fp_details_model.Timings.get(3).From);
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_END_TIME, get_fp_details_model.Timings.get(3).To);
          } else if (i == 4) {
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_START_TIME, get_fp_details_model.Timings.get(4).From);
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_END_TIME, get_fp_details_model.Timings.get(4).To);
          } else if (i == 5) {
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_START_TIME, get_fp_details_model.Timings.get(5).From);
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_END_TIME, get_fp_details_model.Timings.get(5).To);
          } else if (i == 6) {
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_START_TIME, get_fp_details_model.Timings.get(6).From);
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_END_TIME, get_fp_details_model.Timings.get(6).To);
          }
        }
      }
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_APPLICATION_ID, get_fp_details_model.ApplicationId);
    } catch (Exception e) {
      e.printStackTrace();
    }

        /*
         *Constants.FACEBOOK_USER_ID = pref.getString("fbId", "");
        Constants.FACEBOOK_USER_ACCESS_ID = pref.getString("fbAccessId", "");
        Constants.fbShareEnabled = pref.getBoolean("fbShareEnabled", false);
//        Constants.FACEBOOK_PAGE_ID 			= pref.getString("fbPageId", "");
        Constants.FACEBOOK_PAGE_ACCESS_ID = pref.getString("fbPageAccessId", "");
        Constants.fbPageShareEnabled = pref.getBoolean("fbPageShareEnabled", false);
        Constants.twitterShareEnabled = pref.getBoolean("twitterShareEnabled", false);
        Constants.TWITTER_TOK = pref.getString(OAuth.OAUTH_TOKEN, "");
        Constants.TWITTER_SEC = pref.getString(OAuth.OAUTH_TOKEN_SECRET, "");
        Constants.FbFeedPullAutoPublish = pref.getBoolean("FBFeedPullAutoPublish", false);
        Constants.fbPageFullUrl = pref.getString("fbPageFullUrl", "");
        Constants.fbFromWhichPage = pref.getString("fbFromWhichPage", "");
         */

  }
}
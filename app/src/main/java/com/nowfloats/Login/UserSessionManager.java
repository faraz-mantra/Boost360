package com.nowfloats.Login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.anachat.chatsdk.AnaCore;
import com.appservice.AppServiceApplication;
import com.boost.presignin.AppPreSignInApplication;
import com.boost.presignin.ui.intro.IntroActivity;
import com.dashboard.AppDashboardApplication;
import com.framework.models.firestore.FirestoreManager;
import com.framework.utils.PreferencesUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.inventoryorder.BaseOrderApplication;
import com.nowfloats.Analytics_Screen.Graph.database.SaveDataCounts;
import com.nowfloats.Business_Enquiries.Model.Entity_model;
import com.nowfloats.Login.Model.FloatsMessageModel;
import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.NavigationDrawer.Home_Main_Fragment;
import com.nowfloats.Volley.AppController;
import com.nowfloats.sync.DbController;
import com.nowfloats.test.com.nowfloatsui.buisness.util.DataMap;
import com.nowfloats.twitter.TwitterConnection;
import com.nowfloats.util.Constants;
import com.nowfloats.util.DataBase;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.WebEngageController;
import com.onboarding.nowfloats.BaseBoardingApplication;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.framework.utils.PreferencesUtilsKt.deleteAllData;
import static com.nowfloats.util.Key_Preferences.MAIN_PRIMARY_CONTACT_NUM;

/**
 * Created by Dell on 28-01-2015.
 */
public class UserSessionManager implements Fetch_Home_Data.Fetch_Home_Data_Interface {


  // User name (make variable public to access from outside)
  public static final String KEY_NAME = "name";
  // Email address (make variable public to access from outside)
  public static final String KEY_EMAIL = "email";

  //Search_Queries_Enterprise_API searchQueriesEnterpriseAPI ;
  public static final String KEY_FP_ID = "fpid";
  public static final String KEY_FP_Details = "fpdetails";

  // Sharedpref file name
//    private static final String PREFER_NAME = "AndroidExamplePref";
  // All Shared Preferences Keys
  private static final String IS_USER_LOGIN = "IsUserLoggedIn";
  private static final String KEY_FP_Messages = "fpmessages";
  private static final String KEY_GALLLERY_IMAGES = "gallery";
  private final String PROFILE_ID = "user_profile_id";
  private final String PROFILE_EMAIL = "user_profile_email";
  private final String PROFILE_NUMBER = "user_profile_mobile";
  private final String PROFILE_NAME = "user_profile_name";
  // Shared Preferences reference
  SharedPreferences pref;
  // Editor reference for Shared preferences
  SharedPreferences.Editor editor;
  // Context
  Context _context;
  // Shared pref mode
  int PRIVATE_MODE = 0;
  Fetch_Home_Data fetch_home_data;
  ProgressDialog pd = null;
  private Activity activity;
  private String KEY_FP_NAME = "fpname";
  private String KEY_sourceClientId = "source_clientid";
  private String KEY_Visit_Count = "visitCount";
  private String KEY_Visitors_Count = "visitorsCount";
  private String KEY_Subcribers_Count = "subcribersCount";
  private String KEY_Search_Count = "SearchQueryCount";
  private String KEY_Enq_Count = "EnquiryCount";
  private String KEY_Order_Count = "OrderCount";
  private String KEY_Map_Visits_Count = "MapVisitsCount";
  private String KEY_Call_Count = "VmnCallCount";
  private String KEY_LATEST_ENQ_COUNT = "LatestEnquiryCount";
  private String KEY_LS = "local_store";
  private String KEY_website = "website_share";
  private String KEY_FP_EMAIL = "fpemail";
  private String KEY_LOGO_URI = "fplogouri";
  private String KEY_FP_LOGO;
  private String KEY_FIRST_TIME_Details = "firsttime";
  private String KEY_LAST_TIME = "popuptime";
  private String KEY_SUNDAY = "sunday";
  private String KEY_MONDAY = "monday";
  private String KEY_TUESDAY = "tuesday";
  private String KEY_WEDNESDAY = "wednesday";
  private String KEY_THURSDAY = "thursday";
  private String KEY_FRIDAY = "friday";
  private String KEY_SATURDAY = "saturdaty";
  private String KEY_START_TIME = "starttime";
  private String KEY_END_TIME = "endtime";
  private String KEY_FACEBOOK_NAME = "facebookname";
  private String KEY_FACEBOOK_IMPRESSIONS = "facebook_impressions";
  private String KEY_FACEBOOK_ACCESS_TOKEN = "facebookaccesstoken";
  private String KEY_FACEBOOK_PAGE = "facebookpage";
  private String KEY_FACEBOOK_PAGE_ID = "facebookpageid";
  private String KEY_SHOW_UPDATES = "showupdates";
  private String KEY_PAGE_ACCESS_TOKEN = "pagetoken";
  private String KEY_USER_ACCESS_TOKEN = "usertoken";
  private String KEY_FB_LIKE = "fblikekey";
  private String KEY_IS_ENTERPRISE = "isenterprise";
  private String KEY_IS_RESTRICTED = "ISrestricted";
  private String KEY_IS_AUTO_POST_ENABLED = "IsAutoPostEnabled";
  private String KEY_IS_SIGNUP_FROM_FACEBOOK = "SignUpFacebook";
  private String KEY_FACEBOOK_IMAGE_URL = "FacebookImageURL";
  private String IS_ACCOUNT_SAVE = "isAccountSave";
  private String IS_SELF_BRANDED_KYC_ADD = "isSelfBrandedKycAdd";
  private String KEY_FACEBOOK_PROFILE_DESCRIPTION = "FacebookProfileDescription";
  private String KEY_IS_THINKSITY = "isThinksity";
  private String KEY_IS_FREE_DOMAIN = "isFreeDomain";
  private String KEY_FP_TAG = "fptag";
  private String KEY_WEB_TEMPLATE_TYPE = "webTemplateType";
  private String KEY_BUSINESS_HOURS = "BusinessHoursMainKey";

  //public boolean showUpdates;

  // Constructor
  public UserSessionManager(Context context, Activity activity) {
    this._context = context;
    pref = activity.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    editor = pref.edit();
    this.activity = activity;
    fetch_home_data = new Fetch_Home_Data(activity, 0);
    //searchQueriesEnterpriseAPI = new Search_Queries_Enterprise_API(activity);
  }


  //Create login session
  public void createUserLoginSession(String name, String email) {
    // Storing login value as TRUE
    editor.putBoolean(IS_USER_LOGIN, true);

    // Storing name in pref
    editor.putString(KEY_NAME, name);

    // Storing email in pref
    editor.putString(KEY_EMAIL, email);

    // commit changes
    editor.commit();
  }

  public void storeFPName(String fpName) {
    editor.putString(KEY_FP_NAME, fpName);
    editor.commit();
  }

  public String getUserProfileId() {
    return pref.getString(PROFILE_ID, null);
  }

  public void setUserProfileId(String profileId) {
    editor.putString(PROFILE_ID, profileId);
    editor.commit();
  }

  public String getUserProfileEmail() {
    return pref.getString(PROFILE_EMAIL, null);
  }

  public void setUserProfileEmail(String email) {
    editor.putString(PROFILE_EMAIL, email);
    editor.commit();
  }

  public String getUserProfileMobile() {
    return pref.getString(PROFILE_NUMBER, null);
  }

  public void setUserProfileMobile(String mobile) {
    editor.putString(PROFILE_NUMBER, mobile);
    editor.commit();
  }

  public String getUserPrimaryMobile() {
    return pref.getString(MAIN_PRIMARY_CONTACT_NUM, "");
  }

  public String getUserProfileName() {
    return pref.getString(PROFILE_NAME, null);
  }

  public void setUserProfileName(String name) {
    editor.putString(PROFILE_NAME, name);
    editor.commit();
  }

  public void setUserLogin(boolean val) {
    editor.putBoolean(IS_USER_LOGIN, val).apply();
  }

  public void storeFpWebTempalteType(String type) {
    editor.putString(KEY_WEB_TEMPLATE_TYPE, type);
    editor.commit();
  }

  public String getWebTemplateType() {
    return pref.getString(KEY_WEB_TEMPLATE_TYPE, null);
  }

  public String getVisitsCount() {
    return pref.getString(KEY_Visit_Count, "");
  }

  public void setVisitsCount(String cnt) {
    editor.putString(KEY_Visit_Count, cnt);
    editor.commit();
  }

  public String getVisitorsCount() {
    return pref.getString(KEY_Visitors_Count, "");
  }

  public void setVisitorsCount(String cnt) {
    editor.putString(KEY_Visitors_Count, cnt);
    editor.commit();
  }

  public String getSubcribersCount() {

    return pref.getString(KEY_Subcribers_Count, "");
  }

  public void setSubcribersCount(String cnt) {
    editor.putString(KEY_Subcribers_Count, cnt);
    editor.commit();
  }

  public String getSearchCount() {
    return pref.getString(KEY_Search_Count, "");
  }

  public void setSearchCount(String cnt) {
    editor.putString(KEY_Search_Count, cnt);
    editor.apply();
  }

  public String getEnquiryCount() {
    return pref.getString(KEY_Enq_Count, "");
  }

  public void setEnquiryCount(String count) {
    editor.putString(KEY_Enq_Count, count);
    editor.apply();
  }

  public String getOrderCount() {
    return pref.getString(KEY_Order_Count, "");
  }

  public void setOrderCount(String count) {
    editor.putString(KEY_Order_Count, count);
    editor.apply();
  }

  public String getMapVisitsCount() {
    return pref.getString(KEY_Map_Visits_Count, "");
  }

  public void setMapVisitsCount(String count) {
    editor.putString(KEY_Map_Visits_Count, count);
    editor.apply();
  }

  public String getVmnCallsCount() {
    return pref.getString(KEY_Call_Count, "");
  }

  public void setVmnCallsCount(String count) {
    editor.putString(KEY_Call_Count, count);
    editor.apply();
  }

  public String getLatestEnqCount() {
    return pref.getString(KEY_LATEST_ENQ_COUNT, "0");
  }

  public void setLatestEnqCount(String count) {
    editor.putString(KEY_LATEST_ENQ_COUNT, count);
    editor.apply();
  }

  public void storeFpTag(String tag) {
    editor.putString(KEY_FP_TAG, tag);
    editor.apply();
  }

  public String getLocalStorePurchase() {
    return pref.getString(KEY_LS, "");
  }

  public void setLocalStorePurchase(String cnt) {
    editor.putString(KEY_LS, cnt);
    editor.apply();
  }

  public Boolean getWebsiteshare() {
    return pref.getBoolean(Key_Preferences.WEBSITE_SHARE, false);
  }

  public void setWebsiteshare(boolean cnt) {
    editor.putBoolean(Key_Preferences.WEBSITE_SHARE, cnt);
    editor.apply();
  }

  public Boolean getBusinessHours() {
    return pref.getBoolean(KEY_BUSINESS_HOURS, false);
  }

  public void setBusinessHours(boolean cnt) {
    editor.putBoolean(KEY_BUSINESS_HOURS, cnt);
    editor.apply();
  }

  public void setSignUpFromFacebook(String isSignUpFromFacebook) {
    editor.putString(KEY_IS_SIGNUP_FROM_FACEBOOK, isSignUpFromFacebook);
    editor.apply();
  }

  public String getIsSignUpFromFacebook() {
    return pref.getString(KEY_IS_SIGNUP_FROM_FACEBOOK, "");
  }

  public void storeSourceClientId(String val) {
    editor.putString(KEY_sourceClientId, val);
    editor.apply();
  }

  public String getSourceClientId() {
    return pref.getString(KEY_sourceClientId, "");
  }

  public String getFPName() {

    return pref.getString(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME, null);
  }

  public String getFpTag() {
    return pref.getString(Key_Preferences.GET_FP_DETAILS_TAG, null);
  }

  public String getFP_AppExperienceCode() {
    return pref.getString(Key_Preferences.GET_FP_EXPERIENCE_CODE, null);
  }

  public boolean isNonPhysicalProductExperienceCode() {
    String code = pref.getString(Key_Preferences.GET_FP_EXPERIENCE_CODE, null);
    if (code != null) {
      switch (code) {
        case "RTL":
        case "CAF":
        case "MFG":
          return false;
        default:
          return true;
      }
    }
    return false;
  }

  public Boolean isLoginCheck() {
    return (getFpTag() != null && getFPID() != null && getFP_AppExperienceCode() != null && getUserProfileId() != null);
  }

  public String getFPPrimaryContactNumber() {
    return pref.getString(Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER, null);
  }

  public String getRootAliasURI() {
    return pref.getString(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI, null);
  }

  public boolean getShareWebsite() {
    return pref.getBoolean("shareWebsite", false);
  }

  public void storeISEnterprise(String isEnterprise) {
    editor.putString(KEY_IS_ENTERPRISE, isEnterprise);
    editor.apply();
  }

  public String getISEnterprise() {

    return pref.getString(KEY_IS_ENTERPRISE, "false");
  }

  public void storeIsRestricted(String isRestricted) {
    editor.putString(KEY_IS_RESTRICTED, isRestricted);
    editor.apply();
  }

  public String getIsRestricted() {

    return pref.getString(KEY_IS_RESTRICTED, "false");
  }

  public void storeIsThinksity(String isThinksity) {
    editor.putString(KEY_IS_THINKSITY, isThinksity);
    editor.apply();
  }

  public String getIsThinksity() {

    return pref.getString(KEY_IS_THINKSITY, "false");
  }


  public void storeIsAutoPostEnabled(String isEnabled) {
    editor.putString(KEY_IS_AUTO_POST_ENABLED, isEnabled);
    editor.apply();
  }

  public String getIsAutoPostEnabled() {
    return pref.getString(KEY_IS_AUTO_POST_ENABLED, "false");
  }


  public void storePageAccessToken(String pageAccessToken) {
    editor.putString(KEY_PAGE_ACCESS_TOKEN, pageAccessToken);
    editor.apply();
  }

  public String getPageAccessToken() {
    return pref.getString(KEY_PAGE_ACCESS_TOKEN, null);
  }

  public void storeUserAccessToken(String userAccessToken) {
    editor.putString(KEY_USER_ACCESS_TOKEN, userAccessToken);
    editor.apply();
  }

  public String getUserAccessToken() {
    return pref.getString(KEY_USER_ACCESS_TOKEN, null);
  }


  public void storeFBLikeBoxInfo(String isFBLikeBoxPresent) {
    editor.putString(KEY_FB_LIKE, isFBLikeBoxPresent);
    editor.apply();
  }

  public String getStoreFBLikeBoxInfo() {
    return pref.getString(KEY_FB_LIKE, null);
  }


  public void storeShowUpdates(boolean showUpdates) {
    editor.putBoolean(KEY_SHOW_UPDATES, showUpdates);
    editor.apply();
  }

  public boolean getShowUpdates() {

    return pref.getBoolean(KEY_SHOW_UPDATES, true);
  }

  public void setShowUpdates(boolean val) {
    editor.putBoolean(KEY_SHOW_UPDATES, val);
    editor.apply();
  }


  public void storeFacebookName(String facebookName) {
    editor.putString(KEY_FACEBOOK_NAME, facebookName);
    editor.apply();
  }

  public String getFacebookName() {
    return pref.getString(KEY_FACEBOOK_NAME, null);
  }

  public void storeFacebookImpressions(String facebookImpression) {
    editor.putString(KEY_FACEBOOK_IMPRESSIONS, facebookImpression);
    editor.apply();
  }

  public String getFacebookImpressions() {
    return pref.getString(KEY_FACEBOOK_IMPRESSIONS, null);
  }

  public void storeFacebookAccessToken(String token) {
    editor.putString(KEY_FACEBOOK_ACCESS_TOKEN, token);
    editor.apply();
  }

  public String getFacebookAccessToken() {
    return pref.getString(KEY_FACEBOOK_ACCESS_TOKEN, null);
  }

  public void storeFacebookPage(String facebookName) {
    editor.putString(KEY_FACEBOOK_PAGE, facebookName);
    editor.apply();
  }

  public String getFacebookPage() {
    return pref.getString(KEY_FACEBOOK_PAGE, null);
  }

  public void storeFacebookPageID(String id) {
    editor.putString(KEY_FACEBOOK_PAGE_ID, id);
    editor.apply();
  }

  public String getFacebookPageID() {
    return pref.getString(KEY_FACEBOOK_PAGE_ID, null);
  }

  public String getFPEmail() {
    return pref.getString(Key_Preferences.GET_FP_DETAILS_EMAIL, null);
  }

  public void storeLogoURI(String fpLogoURI) {
    editor.putString(KEY_LOGO_URI, fpLogoURI);
    editor.apply();
  }

  public void storeMondayChecked(boolean value) {
    editor.putBoolean(KEY_MONDAY, value);
    editor.apply();
  }

  public boolean getMonayChecked() {
    return pref.getBoolean(KEY_MONDAY, true);
  }

  public void storeTuesdayChecked(boolean value) {
    editor.putBoolean(KEY_TUESDAY, value);
    editor.apply();
  }

  public boolean getTuesdayChecked() {
    return pref.getBoolean(KEY_TUESDAY, false);
  }

  public void storeWednesdayChecked(boolean value) {
    editor.putBoolean(KEY_WEDNESDAY, value);
    editor.apply();
  }

  public boolean getWednesdayChecked() {
    return pref.getBoolean(KEY_WEDNESDAY, false);
  }

  public void storeThursdayChecked(boolean value) {
    editor.putBoolean(KEY_THURSDAY, value);
    editor.apply();
  }

  public boolean getThursdayChecked() {
    return pref.getBoolean(KEY_THURSDAY, false);
  }

  public void storeFridayChecked(boolean value) {
    editor.putBoolean(KEY_FRIDAY, value);
    editor.apply();
  }

  public boolean getFridayChecked() {
    return pref.getBoolean(KEY_FRIDAY, false);
  }

  public void storeSaturdayChecked(boolean value) {
    editor.putBoolean(KEY_SATURDAY, value);
    editor.apply();
  }

  public boolean getSaturdayChecked() {
    return pref.getBoolean(KEY_SATURDAY, false);
  }

  public void storeSundayChecked(boolean value) {
    editor.putBoolean(KEY_SUNDAY, value);
    editor.apply();
  }

  public boolean getSundayChecked() {
    return pref.getBoolean(KEY_SUNDAY, false);
  }


  public void storeStartTime(String startTime) {
    editor.putString(KEY_START_TIME, startTime);
    editor.apply();
  }

  public String getStartTime() {
    return pref.getString(KEY_START_TIME, "10:00 AM");
  }

  public void storeEndTime(String startTime) {
    editor.putString(KEY_END_TIME, startTime);
    editor.apply();
  }

  public String getEndTime() {
    return pref.getString(KEY_END_TIME, "06:00 PM");
  }

  public String getLogoURI() {
    return pref.getString(KEY_LOGO_URI, null);
  }

  public void storeFPID(String fpID) {

    if (fpID == null) return;

    String fpId = fpID.replaceAll("\"", "");
    // fpId = fpId.replace
    editor.putString(KEY_FP_ID, fpId);
    editor.apply();
  }

  public void isFacebookAuthDone(boolean done) {
    editor.putBoolean("FACEBOOK", done);
    editor.apply();
  }

  public boolean getFacebookAuthDone() {
    return pref.getBoolean("FACEBOOK", false);
  }

  public void isOTPAuthDone(boolean done) {
    editor.putBoolean("OTP", done);
    editor.apply();
  }

  public void isAuthdone(String key, boolean done) {
    editor.putBoolean(key, done);
    editor.apply();
  }

  public boolean getOTPAuthDone() {
    return pref.getBoolean("OTP", false);
  }

  public void isGoogleAuthDone(boolean done) {
    editor.putBoolean("GOOGLE", done);
    editor.apply();
  }

  public boolean getGoogleAuthDone() {
    return pref.getBoolean("GOOGLE", false);
  }

  public String getFPID() {
    return pref.getString(KEY_FP_ID, null);
  }

  public boolean isAllAuthSet() {
    return getGoogleAuthDone() && getOTPAuthDone() && getFacebookAuthDone();
  }

  public void storeFacebookPageURL(String imageURL) {
    editor.putString(KEY_FACEBOOK_IMAGE_URL, imageURL);
    editor.apply();

  }

  public String getFacebookPageURL() {
    return pref.getString(KEY_FACEBOOK_IMAGE_URL, "");
  }


  public void setSelfBrandedKycAdd(Boolean b) {
    editor.putBoolean(IS_SELF_BRANDED_KYC_ADD, b);
    editor.apply();
  }

  public Boolean isSelfBrandedKycAdd() {
    return pref.getBoolean(IS_SELF_BRANDED_KYC_ADD, false);
  }

  public Boolean getAccountSave() {
    return pref.getBoolean(IS_ACCOUNT_SAVE, false);
  }

  public void setAccountSave(Boolean b) {
    editor.putBoolean(IS_ACCOUNT_SAVE, b);
    editor.apply();
  }

  public void storeFacebookProfileDescription(String description) {
    editor.putString(KEY_FACEBOOK_PROFILE_DESCRIPTION, description);
    editor.apply();
  }

  public String getFacebookProfileDescription() {
    return pref.getString(KEY_FACEBOOK_PROFILE_DESCRIPTION, "");
  }

  public void storeIsFreeDomainDisplayed(String isFreeDomain) {

    editor.putString(KEY_IS_FREE_DOMAIN, isFreeDomain);
    editor.apply();
  }

  public String getIsFreeDomainDisplayed() {
    return pref.getString(KEY_IS_FREE_DOMAIN, "");
  }

  public void savePackageStatus(String packegeId, boolean val) {
    try {
      editor.putBoolean(packegeId, val);
      editor.apply();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public boolean getPackageStatus(String packageId) {
    return pref.getBoolean(packageId, false);
  }

  public void storeBooleanDetails(String key, boolean value) {
    editor.putBoolean(key.trim(), value).apply();
  }

  public boolean getBooleanDetails(String key) {
    return pref.getBoolean(key, false);
  }

  public void storeFPDetails(String key, String value) {
    try {
      editor.putString(key.trim(), value == null ? "" : value.trim());
      editor.apply();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public String getFPDetails(String key) {
    return pref.getString(key.trim(), "");
  }

  public List<String> getStoreWidgets() {
    String str = pref.getString(Key_Preferences.STORE_WIDGETS, "");
    if (TextUtils.isEmpty(str)) return new ArrayList();
    return new Gson().fromJson(str, new TypeToken<List<String>>() {
    }.getType());
  }

  public boolean isBoostBubbleEnabled() {
    return pref.getBoolean(Key_Preferences.IS_BOOST_BUBBLE_ENABLED, false);
  }

  public boolean isCustomerAssistantEnabled() {
    return pref.getBoolean(Key_Preferences.IS_CUSTOMER_ASSISTANT_ENABLED, false);
  }

  public void setBubbleStatus(boolean flag) {
    pref.edit().putBoolean(Key_Preferences.IS_BOOST_BUBBLE_ENABLED, flag).apply();
  }

  public void setHeader(String auth) {
    pref.edit().putString(Key_Preferences.AUTHORIZATION, auth).apply();
  }

  public void clearHeader() {
    pref.edit().putString(Key_Preferences.AUTHORIZATION, "").apply();
  }

  public void setCustomerAssistantStatus(boolean flag) {
    pref.edit().putBoolean(Key_Preferences.IS_CUSTOMER_ASSISTANT_ENABLED, flag).apply();
  }

  public void setBubbleShareProducts(boolean flag) {
    pref.edit().putBoolean(Key_Preferences.HAS_BUBBLE_SHARE_PRODUCTS, flag).apply();
  }

  public Long getBubbleTime() {
    return pref.getLong(Key_Preferences.SHOW_BUBBLE_TIME, 0);
  }

  public void setBubbleTime(long time) {
    pref.edit().putLong(Key_Preferences.SHOW_BUBBLE_TIME, time).apply();
  }

  public void storeGalleryImages(String imagePath) {
    editor.putString(KEY_GALLLERY_IMAGES, imagePath);
    editor.apply();
  }

  public String getFPLogo() {
    return pref.getString(Key_Preferences.GET_FP_DETAILS_LogoUrl, null);
  }

  public String getFPParentId() {
    String id = pref.getString(Key_Preferences.GET_FP_DETAILS_PARENTID, null);
    if (TextUtils.isEmpty(id)) id = pref.getString(KEY_FP_ID, null);
    return id;
  }

  public void storeFPLogo(String logo) {
    editor.putString(Key_Preferences.GET_FP_DETAILS_LogoUrl, logo);
    editor.apply();
  }

  public ArrayList<String> getStoredGalleryImages() {
    String imagesList = pref.getString(KEY_GALLLERY_IMAGES, null);
    String replace = imagesList.replace("[", "");
    String replace1 = replace.replace("]", "");
    String replace2 = replace1.replace(" ", "");
    // Log.d("UserSessionManager : getStoredGalleryImages","imagesList : "+replace2);
    ArrayList<String> myList = new ArrayList<>(Arrays.asList(replace2.split(",")));

    return myList;
  }

  /**
   * sent_check login method will check user login status
   * If false it will redirect user to login page
   * Else do anything
   */
  public boolean checkLogin() {
    // sent_check login status
    DataBase db = new DataBase(activity);
    Cursor cursor = db.getLoginStatus();
    boolean isLogin = false;
    if (cursor != null && cursor.getCount() > 0) {
      if (cursor.moveToNext()) {
        String LoginStatus = cursor.getString(0);
        String fpid = cursor.getString(1);
        String facebookName = cursor.getString(2);
        String facebookPage = cursor.getString(3);
        String facebookAccessToken = cursor.getString(4);
        String facebookpageToken = cursor.getString(5);
        String facepageId = cursor.getString(6);
        String isRestricted = cursor.getString(9);
        String isEnterprise = cursor.getString(8);
        if (LoginStatus.equals("true")) {
          isLogin = true;

          storeFPID(fpid);
          storePageAccessToken(facebookpageToken);
          storeIsRestricted(isRestricted);
          storeISEnterprise(isEnterprise);
//                    Constants.FACEBOOK_PAGE_ID = facepageId;
          storeFacebookPageID(facepageId);
          if (facebookName != null && facebookName.trim().length() > 0) {
            storeFacebookName(facebookName);
//                        Constants.FACEBOOK_USER_NAME = facebookName;
          }
          if (facebookPage != null && facebookPage.trim().length() > 0)
            storeFacebookPage(facebookPage);
          if (facebookAccessToken != null && facebookAccessToken.trim().length() > 0) {
//                        Constants.FACEBOOK_ACCESS_TOKEN = facebookAccessToken;
            storeFacebookAccessToken(facebookAccessToken);
          }
        } else isLogin = false;

      }
    }
    setUserLogin(isLogin);
    return isLogin;
  }

   /* private void fetchData() {
        try{
       activity.runOnUiThread(new Runnable() {
           @Override
           public void run() {
               Util.addBackgroundImages();
               pd= ProgressDialog.show(activity, "", "Getting Details ...");
               pd.setCancelable(false);
           }
       });

            getFPDetails_retrofit(activity, getFPID(), Constants.clientId, bus);
            Search_queries_visitor_and_subscriber_Async_Task analyticsScreenCount = new Search_queries_visitor_and_subscriber_Async_Task(activity, getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG));
            analyticsScreenCount.execute();

            fetch_home_data.setFetchDataListener(this);
            fetch_home_data.getMessages(getFPID(), "0");

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }*/

    /*private void getFPDetails_retrofit(Activity activity, String fpId, String clientId, Bus bus) {
        new Get_FP_Details_Service(activity,fpId,clientId,bus);
    }*/

  public void store_FIRST_TIME(boolean value) {
    editor.putBoolean(KEY_FIRST_TIME_Details, value);
    editor.apply();
  }

  public boolean get_FIRST_TIME() {
    return pref.getBoolean(KEY_FIRST_TIME_Details, true);
  }

  public void store_SHOW_POP_UP_TIME(long time) {
    editor.putLong(KEY_LAST_TIME, time);
    editor.apply();
  }

  public long get_SHOW_POP_UP_TIME() {
    return pref.getLong(KEY_LAST_TIME, 0);
  }

  /**
   * Get stored session data
   */
  public HashMap<String, String> getUserDetails() {

    //Use hashmap to store user credentials
    HashMap<String, String> user = new HashMap<String, String>();

    // user name
    user.put(KEY_NAME, pref.getString(KEY_NAME, null));

    // user email id
    user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

    // return user
    return user;
  }

  /**
   * Clear session details
   */
  public void logoutUser() {
    // Clearing all user data from Shared Preferences
    unsubscribeRIA(getFPID(), activity);
  }

  public void unsubscribeRIA(String fpID, final Activity activity) {
    final ProgressDialog pd;
    pd = new ProgressDialog(activity, R.style.AppCompatAlertDialogStyle);
    pd.setCancelable(false);
    pd.setMessage(activity.getString(R.string.logging_out));
    pd.show();
    if (fpID != null && fpID.length() > 2) {
      HashMap<String, String> params = new HashMap<String, String>();
      params.put("clientId", Constants.clientId);
      params.put("fpId", fpID);
      Login_Interface api_login_request = Constants.restAdapter.create(Login_Interface.class);
      api_login_request.logoutUnsubcribeRIA(params, new Callback<String>() {
        @Override
        public void success(String s, Response response) {
          Log.d("Valid Email", "Valid Email Response: " + response);
          if (pd.isShowing()) pd.dismiss();
          processUserSessionDataClear();
        }

        @Override
        public void failure(RetrofitError error) {
          if (pd.isShowing()) pd.dismiss();
          processUserSessionDataClear();
//          Methods.showSnackBarNegative(activity, activity.getString(R.string.unable_to_logout));
        }
      });
    } else {
      if (pd.isShowing()) pd.dismiss();
      processUserSessionDataClear();
    }
  }

  private void processUserSessionDataClear() {
    try {
      FirestoreManager.INSTANCE.reset();

      WebEngageController.logout();
      AnaCore.logoutUser(activity);
      setUserLogin(false);
      DataBase db = new DataBase(activity);
      DbController.getDbController(activity.getApplicationContext()).deleteDataBase();
      db.deleteLoginStatus();
      activity.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE).edit().clear().apply();
      activity.getSharedPreferences(TwitterConnection.PREF_NAME, Context.MODE_PRIVATE).edit().clear().apply();
      deleteAllData(PreferencesUtils.Companion.getInstance());

      AppController.getInstance().clearApplicationData();
      Date date = new Date(System.currentTimeMillis());
      String dateString = date.toString();
      MixPanelController.setProperties("LastLogoutDate", dateString);
      com.facebook.login.LoginManager.getInstance().logOut();
      // After logout redirect user to Login Activity
      Constants.clearStore();
      Constants.StorebizQueries = new ArrayList<>();
      Constants.storeSecondaryImages = null;
      Constants.storeActualSecondaryImages = new ArrayList<>();
      Constants.StoreUserSearch = new DataMap();
      Constants.StorebizEnterpriseQueries = new ArrayList<Entity_model>();
      Constants.StorePackageIds = new ArrayList<>();
      Constants.widgets = new HashSet<String>();
      Constants.StoreWidgets = new ArrayList<>();
      Constants.ImageGalleryWidget = false;
      Constants.BusinessTimingsWidget = false;
      Constants.BusinessEnquiryWidget = false;
      if (HomeActivity.StorebizFloats != null) {
        HomeActivity.StorebizFloats.clear();
        HomeActivity.StorebizFloats = new ArrayList<>();
      }
      if (Home_Main_Fragment.StorebizFloats != null) {
        Home_Main_Fragment.StorebizFloats.clear();
        Home_Main_Fragment.StorebizFloats = new ArrayList<>();
      }
      //Analytics_Fragment.subscriberCount.setText("0");
      //Analytics_Fragment.visitCount.setText("0");
      if (_context != null) {
        _context.deleteDatabase(SaveDataCounts.DATABASE_NAME);
        _context.deleteDatabase("updates_db");  //DELETE MARKETPLACE DB
      }
      clearAuth();
      Intent i = new Intent(activity, IntroActivity.class);
      i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
      activity.startActivity(i);
      activity.overridePendingTransition(0, 0);
      activity.finish();
    } catch (Exception e) {
      e.printStackTrace();
      Log.e("USER_LOGOUT", e.getLocalizedMessage());
    }
  }

  private void clearAuth() {
    try {
      BaseOrderApplication.apiInitialize();
      BaseBoardingApplication.apiInitialize();
      AppServiceApplication.apiInitialize();
      AppDashboardApplication.apiInitialize();
      AppPreSignInApplication.apiInitialize();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public boolean isSiteAppearanceShown() {
    return pref.getBoolean(Key_Preferences.IS_FP_SITE_APPEARNCE_SHOWN, false);
  }

  public void setSiteAppearanceShown(boolean val) {
    editor.putBoolean(Key_Preferences.IS_FP_SITE_APPEARNCE_SHOWN, true);
    editor.apply();
  }


  // sent_check for login
  public boolean isUserLoggedIn() {
    //Log.d("isUserLoggedIn", "isUserLoggedIn : "+IS_USER_LOGIN);
    return pref.getBoolean(IS_USER_LOGIN, false);
  }

  @Override
  public void dataFetched(int skip, boolean isNewMessage) {

    if (pd != null)
      pd.dismiss();

//        API_Business_enquiries businessEnquiries = new API_Business_enquiries(null,this);
//        businessEnquiries.getMessages();

//        GetSearchQueryCountAsyncTask searchQueryCountAsyncTask = new GetSearchQueryCountAsyncTask(activity,this);
//        searchQueryCountAsyncTask.execute();

    Intent i = new Intent(_context, HomeActivity.class);
    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
    //Bundle bundle = new Bundle();
    //bundle.putParcelableArrayList("message",HomeActivity.StorebizFloats);
    //i.putExtras(bundle);
    // Staring Login Activity
    _context.startActivity(i);
  }

  @Override
  public void sendFetched(FloatsMessageModel jsonObject) {
  }

  public int getSiteHealth() {
    return pref.getInt(Key_Preferences.SITE_HEALTH, 0);
  }

  public void setSiteHealth(int siteMeterTotalWeight) {
    editor.putInt(Key_Preferences.SITE_HEALTH, siteMeterTotalWeight).apply();
  }

  public int getProductsCount() {
    return pref.getInt(Key_Preferences.PRODUCTS_COUNT, 0);
  }

  public void setProductsCount(int size) {
    editor.putInt(Key_Preferences.PRODUCTS_COUNT, size).apply();
  }

  public boolean getOnBoardingStatus() {
    return pref.getBoolean(Key_Preferences.ON_BOARDING_STATUS, false);
  }

  public void setOnBoardingStatus(boolean flag) {
    editor.putBoolean(Key_Preferences.ON_BOARDING_STATUS, flag).apply();
  }

  public int getCustomPageCount() {
    return pref.getInt(Key_Preferences.CUSTOM_PAGE, 0);
  }

  public void setCustomPageCount(int size) {
    editor.putInt(Key_Preferences.CUSTOM_PAGE, size).apply();
  }
}

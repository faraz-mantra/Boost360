package com.boost.presignin.model.fpdetail;

import android.app.Activity;
import android.util.Log;


import com.boost.presignin.model.Constants;

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
    public static void storeFPDetails(Activity activity,UserFpDetailsResponse get_fp_details_model)
    {
        try {
            session = new UserSessionManager(activity.getApplicationContext(), activity);
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_CITY, get_fp_details_model.getCity());

            if(!session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG).equals(get_fp_details_model.getTag()))
            {
                String name = get_fp_details_model.getContactName() == null ? "" : (get_fp_details_model.getContactName().toLowerCase().equals("null") ? "" : get_fp_details_model.getContactName());
                session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_CONTACTNAME, name);
            }

            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_TAG, get_fp_details_model.getTag());
            session.storeFPDetails(Key_Preferences.GET_FP_EXPERIENCE_CODE, get_fp_details_model.getAppExperienceCode());
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS, get_fp_details_model.getAddress());
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_TILE_IMAGE_URI, get_fp_details_model.getTileImageUri());
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_IMAGE_URI, get_fp_details_model.getImageUri());
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_FAVICON_IMAGE_URI, get_fp_details_model.getFaviconUrl());
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_CREATED_ON,get_fp_details_model.getCreatedOn());
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_EXPIRY_DATE, get_fp_details_model.getExpiryDate());
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE, get_fp_details_model.getPaymentLevel().toString());
            session.storeFPDetails(Key_Preferences.LANGUAGE_CODE, get_fp_details_model.getLanguageCode());
            session.storeFPDetails(Key_Preferences.EXTERNAL_SOURCE_ID, get_fp_details_model.getExternalSourceId());
            if("0".equals(get_fp_details_model.getLat().toString()) &&"0".equals(get_fp_details_model.getLng().toString())){
                session.storeFPDetails(Key_Preferences.LATITUDE, "");
                session.storeFPDetails(Key_Preferences.LONGITUDE, "");
            }else {
                session.storeFPDetails(Key_Preferences.LATITUDE, get_fp_details_model.getLat().toString());
                session.storeFPDetails(Key_Preferences.LONGITUDE, get_fp_details_model.getLng().toString());
            }
            try {
                Constants.longitude = Double.parseDouble(get_fp_details_model.getLat().toString());
                Constants.longitude = Double.parseDouble(get_fp_details_model.getLng().toString());
            }catch (Exception e){
                e.printStackTrace();
            }
            if (get_fp_details_model.getAccountManagerId()!=null && !get_fp_details_model.getAccountManagerId().equals("null"))
                session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ACCOUNTMANAGERID,get_fp_details_model.getAccountManagerId());
            //String category = get_fp_details_model.Category.get(0);
            try{
                if(get_fp_details_model.getCategory()!=null && get_fp_details_model.getCategory().size()>0){
                    session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY, get_fp_details_model.getCategory().get(0).getKey());
                }
            }catch(Exception e){
                e.printStackTrace();
                session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY, "GENERAL");
            }

            //session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_CONTACTNAME, get_fp_details_model.ContactName);

            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL, get_fp_details_model.getEmail().toString());
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRYPHONECODE, get_fp_details_model.getCountryPhoneCode());
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME, get_fp_details_model.getName());
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_DESCRIPTION, get_fp_details_model.getDescription().toString());
            session.storeFPDetails(Key_Preferences.GET_FP_WEBTEMPLATE_ID, get_fp_details_model.getWebTemplateId());
            session.storeFpWebTempalteType(get_fp_details_model.getWebTemplateType().toString());
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_FBPAGENAME, get_fp_details_model.getFBPageName().toString());
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_PARENTID, get_fp_details_model.getParentId().toString());
            session.storeFPDetails(Key_Preferences.PRODUCT_CATEGORY, get_fp_details_model.getProductCategoryVerb());
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY, get_fp_details_model.getCountry());
            try{
                session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTLEVEL, get_fp_details_model.getPaymentLevel().toString());

                if(get_fp_details_model.getPaymentLevel()!=null && get_fp_details_model.getPaymentLevel().toString().trim().length()>0 &&
                        Integer.parseInt(get_fp_details_model.getPaymentLevel().toString()) >= 10){
                }
                else{
                }
            }catch(Exception e){e.printStackTrace();}

            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WEBSITE, get_fp_details_model.getUri().toString());
            session.storeFPDetails(Key_Preferences.MAIN_PRIMARY_CONTACT_NUM, get_fp_details_model.getPrimaryNumber());
            try {
//                Util.GettingBackGroundId(session);
                if(get_fp_details_model.getContacts() !=null) {
                    if (get_fp_details_model.getContacts().size() == 1) {
                        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER, get_fp_details_model.getContacts().get(0).getContactNumber());
                        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NAME, get_fp_details_model.getContacts().get(0).getContactNumber());
                        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NUMBER_1, "");
                        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NAME_1, "");
                        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NUMBER_3, "");
                        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NAME_3, "");
                    } else if (get_fp_details_model.getContacts().size() == 2) {
                        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER, get_fp_details_model.getContacts().get(0).getContactNumber());
                        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NAME, get_fp_details_model.getContacts().get(0).getContactNumber());
                        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NUMBER_1, get_fp_details_model.getContacts().get(1).getContactNumber());
                        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NAME_1, get_fp_details_model.getContacts().get(1).getContactNumber());
                        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NUMBER_3, "");
                        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NAME_3, "");
                    } else if (get_fp_details_model.getContacts().size() >= 3) {
                        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER, get_fp_details_model.getContacts().get(0).getContactNumber());
                        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NAME, get_fp_details_model.getContacts().get(0).getContactNumber());
                        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NUMBER_1, get_fp_details_model.getContacts().get(1).getContactNumber());
                        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NAME_1, get_fp_details_model.getContacts().get(1).getContactNumber());
                        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NUMBER_3, get_fp_details_model.getContacts().get(2).getContactNumber());
                        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NAME_3, get_fp_details_model.getContacts().get(2).getContactNumber());
                    }
                }
            }catch(Exception e){e.printStackTrace();}

            ArrayList<String> widgetsList = get_fp_details_model.getFPWebWidgets();
            Constants.StorePackageIds = get_fp_details_model.getPackageIds();
            Constants.StoreWidgets = get_fp_details_model.getFPWebWidgets();
            session.storeFPDetails(Key_Preferences.STORE_WIDGETS, convertListObjToString(get_fp_details_model.getFPWebWidgets()));
            Log.d("Constants.storeWidgets", "widgets : " + Constants.StoreWidgets + " "+ Constants.StorePackageIds);
            Constants.storeSecondaryImages = get_fp_details_model.getSecondaryTileImages();

//        for(String widget : widgetsList)
//        {
            if(widgetsList.contains(FP_WEB_WIDGET_DOMAIN)) {
                session.storeFPDetails(Key_Preferences.GET_FP_WEB_WIDGET_DOMAIN,FP_WEB_WIDGET_DOMAIN);
            } else {
                session.storeFPDetails(Key_Preferences.GET_FP_WEB_WIDGET_DOMAIN,"");
            }

            if(widgetsList.contains(WIDGET_IMAGE_GALLERY)) {
                session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_IMAGE_GALLERY,WIDGET_IMAGE_GALLERY);
            } else {
                session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_IMAGE_GALLERY,"");
            }
            if(widgetsList.contains(WIDGET_IMAGE_TIMINGS))
            {
                session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_IMAGE_TIMINGS,WIDGET_IMAGE_TIMINGS);
            }
            else {
                session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_IMAGE_TIMINGS,"");
            }

            if(widgetsList.contains(WIDGET_IMAGE_TOB))
            {
                session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_IMAGE_TOB,WIDGET_IMAGE_TOB);
            }
            else {
                session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_IMAGE_TOB,"");
            }

            if(widgetsList.contains(WIDGET_PRODUCT_GALLERY))
            {
                session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_PRODUCT_GALLERY,WIDGET_PRODUCT_GALLERY);
            }
            else {
                session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_PRODUCT_GALLERY,"");
            }
            if(widgetsList.contains(WIDGET_FB_LIKE_BOX))
            {
                session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_FB_LIKE_BOX,WIDGET_FB_LIKE_BOX);
            }
            else {
                session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_FB_LIKE_BOX,"");
            }
            if(widgetsList.contains(WIDGET_CUSTOMPAGES))
            {
                session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_CUSTOMPAGES,WIDGET_CUSTOMPAGES);
            }
            else {
                session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_CUSTOMPAGES,"");
            }
//        }

            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_PINCODE,get_fp_details_model.getPinCode());
            //TODO for testing
//            session.storeFacebookPageID(get_fp_details_model.FBPageName);
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI,get_fp_details_model.getRootAliasUri());
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_LogoUrl,get_fp_details_model.getLogoUrl());

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        try {
            if (get_fp_details_model.getTimings() != null) {
                session.storeBooleanDetails(Key_Preferences.IS_BUSINESS_TIME_AVAILABLE,true);

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
//                for (int i = 0; i < get_fp_details_model.getTimings().size(); i++) {
//                    if (i==0) {
//                        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_SUNDAY_START_TIME, get_fp_details_model.getTimings().get(0).From);
//                        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_SUNDAY_END_TIME, get_fp_details_model.getTimings().get(0).To);
//                        session.setBusinessHours(true);
//                    }
//                    else if (i==1) {
//                        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_START_TIME, get_fp_details_model.getTimings().get(1).From);
//                        session.storeFPDetails(com.framework.pref.Key_Preferences.GET_FP_DETAILS_MONDAY_END_TIME, get_fp_details_model.getTimings().get(1).To);
//                    }
//                    else if (i==2) {
//                        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_START_TIME, get_fp_details_model.getTimings().get(2).From);
//                        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_END_TIME, get_fp_details_model.getTimings().get(2).To);
//                    }
//                    else if (i==3) {
//                        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_START_TIME, get_fp_details_model.getTimings().get(3).From);
//                        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_END_TIME, get_fp_details_model.getTimings().get(3).To);
//                    }
//                    else if (i==4) {
//                        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_START_TIME, get_fp_details_model.getTimings().get(4).From);
//                        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_END_TIME, get_fp_details_model.getTimings().get(4).To);
//                    }
//                    else if (i==5) {
//                        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_START_TIME, get_fp_details_model.getTimings().get(5).From);
//                        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_END_TIME, get_fp_details_model.getTimings().get(5).To);
//                    }
//                   else if (i==6) {
//                        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_START_TIME, get_fp_details_model.getTimings().get(6).From);
//                        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_END_TIME, get_fp_details_model.getTimings().get(6).To);
//                    }
//                }
            }
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_APPLICATION_ID,get_fp_details_model.getApplicationId());
        }catch(Exception e){e.printStackTrace();}

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
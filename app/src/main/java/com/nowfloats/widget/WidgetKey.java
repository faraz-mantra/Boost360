package com.nowfloats.widget;

import android.util.Log;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.Store.Service.StoreInterface;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class WidgetKey {

    private static final String WIDGET_BOOKING_ENGINE = "BOOKING ENGINE";
    private static final String WIDGET_PACKAGE_PROPERTIES = "PACKAGE-PROPERTIES";
    private static final String WIDGET_TRIPADVISOR_REVIEWS = "TRIPADVISOR REVIEWS";
    private static final String WIDGET_PRICE_COMPARISON = "PRICE COMPARISON";
    private static final String WIDGET_FACILITIES = "FACILITIES";
    private static final String WIDGET_PLACES_TO_LOOK_AROUND = "PLACES TO LOOK AROUND";
    private static final String WIDGET_TOB = "TOB";
    private static final String WIDGET_IMAGE_GALLERY = "IMAGEGALLERY";
    private static final String WIDGET_SITESENSE = "SITESENSE";
    private static final String WIDGET_CONTACT_DETAILS = "CONTACTDETAILS";
    private static final String WIDGET_CUSTOM_PAGES = "CUSTOMPAGES";
    private static final String WIDGET_FB_LIKE_BOX = "FBLIKEBOX";
    private static final String WIDGET_SUBSCRIBER_COUNT = "SUBSCRIBERCOUNT";
    private static final String WIDGET_VISITOR_COUNT = "VISITORCOUNT";
    private static final String WIDGET_SOCIAL_SHARE = "SOCIALSHARE";
    private static final String WIDGET_GALLERY_VIDEO = "GALLERYVIDEO";
    private static final String WIDGET_AUTO_FB_MSG_UPDATE = "AUTOFBMSGUPDATE";
    private static final String WIDGET_RIA_SUPPORT_TEAM = "RIASUPPORTTEAM";
    private static final String WIDGET_SOCIAL_MEDIA = "SOCIALMEDIA";
    private static final String WIDGET_MEDIA_MANAGEMENT = "MEDIAMANAGEMENT";
    private static final String WIDGET_CALL_TRACKER = "CALLTRACKER";
    private static final String WIDGET_DOMAIN_PURCHASE = "DOMAINPURCHASE";
    private static final String WIDGET_PAYMENT_GATEWAY = "PAYMENTGATEWAY";
    private static final String WIDGET_TRANSACTION_FEES = "TRANSACTIONFEES";
    private static final String WIDGET_SUBSCRIPTION = "SUBSCRIPTION";
    private static final String WIDGET_PRE_OWN_DOMAIN_MAPPING = "PREOWNDOMAINMAPPING";
    private static final String WIDGET_SHOPPING_CART = "SHOPPINGCART";
    private static final String WIDGET_EMAIL_ACCOUNTS = "EMAILACCOUNTS";
    private static final String WIDGET_WEBSITE_BANDWIDTH = "WEBSITEBANDWIDTH";
    private static final String WIDGET_LATEST_UPDATES = "LATESTUPDATES";
    private static final String WIDGET_ANALYTICS = "ANALYTICS";
    private static final String WIDGET_PREMIUM_ADDONS = "PREMIUMADDONS";
    private static final String WIDGET_CUSTOMER_SUPPORT = "CUSTOMERSUPPORT";
    private static final String WIDGET_CHATBOT = "CHATBOT";
    private static final String WIDGET_IVR = "IVR";
    private static final String WIDGET_DESIGN_TEMPLATES = "DESIGNTEMPLATES";
    private static final String WIDGET_OFFERS = "OFFERS";
    private static final String WIDGET_RATES_AND_INVENTORY = "RATESANDINVENTORY";
    private static final String WIDGET_REPORT_AND_DASHBOARD = "REPORTANDDASHBOARD";
    private static final String WIDGET_TESTIMONIALS = "TESTIMONIALS";


    //private static final String WIDGET_IMAGE_GALLERY = "IMAGEGALLERY";
    //private static final String WIDGET_IMAGE_TOB = "TOB";
    //private static final String WIDGET_IMAGE_TIMINGS = "TIMINGS";
    //private static final String WIDGET_PRODUCT_GALLERY = "PRODUCTCATALOGUE";
    //private static final String WIDGET_FB_LIKE_BOX = "FbLikeBox";
    //private static final String WIDGET_CUSTOMPAGES = "CUSTOMPAGES";
    //private static final String FP_WEB_WIDGET_DOMAIN = "DOMAINPURCHASE";


    public enum WidgetValue
    {
        UNLIMITED("-1"), FEATURE_NOT_AVAILABLE("0");

        String value;

        WidgetValue(String value)
        {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public static void getWidgets(UserSessionManager mSessionManager) {

        String accId = mSessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_ACCOUNTMANAGERID);
        String appId = mSessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_APPLICATION_ID);
        String country = mSessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY);

        Map<String, String> params = new HashMap<>();

        if (accId.length() > 0)
        {
            params.put("identifier", accId);
        }

        else
        {
            params.put("identifier", appId);
        }

        params.put("clientId", Constants.clientId);
        params.put("fpId", mSessionManager.getFPID());
        params.put("country", country.toLowerCase());
        params.put("fpCategory", mSessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY).toUpperCase());

        Constants.restAdapter.create(StoreInterface.class).getActiveWidgetList(params, new Callback<WidgetResponse>() {

            @Override
            public void success(WidgetResponse widget, Response response)
            {
                Log.d("WIDGET_RESPONSE", "SUCCESS");

                if (widget != null && widget.getActivePackages() != null)
                {
                    Widget.getInstance().setActivePackage(widget.getActivePackages().get(0));
                }
            }

            @Override
            public void failure(RetrofitError error)
            {
                Log.d("Test", error.getMessage());
                Log.d("WIDGET_RESPONSE", "FAIL");
            }
        });
    }
}
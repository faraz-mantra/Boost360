package com.nowfloats.widget;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.Store.Model.ActivePackage;
import com.nowfloats.Store.Model.WidgetPacks;
import com.nowfloats.Store.Service.StoreInterface;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class WidgetKey {

    /**
     * Widget Key
     */
    private static final String WIDGET_BOOKING_ENGINE = "BOOKING ENGINE";
    private static final String WIDGET_PACKAGE_PROPERTIES = "PACKAGE-PROPERTIES";
    private static final String WIDGET_TRIPADVISOR_REVIEWS = "TRIPADVISOR REVIEWS";
    private static final String WIDGET_PRICE_COMPARISON = "PRICE COMPARISON";
    private static final String WIDGET_FACILITIES = "FACILITIES";
    private static final String WIDGET_PLACES_TO_LOOK_AROUND = "PLACES TO LOOK AROUND";
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
    private static final String WIDGET_TOB = "TOB";
    private static final String WIDGET_SITESENSE = "SITESENSE";
    private static final String WIDGET_CONTACT_DETAILS = "CONTACTDETAILS";
    private static final String WIDGET_PRE_OWN_DOMAIN_MAPPING = "PREOWNDOMAINMAPPING";
    private static final String WIDGET_EMAIL_ACCOUNTS = "EMAILACCOUNTS";
    private static final String WIDGET_WEBSITE_BANDWIDTH = "WEBSITEBANDWIDTH";
    private static final String WIDGET_ANALYTICS = "ANALYTICS";
    private static final String WIDGET_PREMIUM_ADDONS = "PREMIUMADDONS";
    private static final String WIDGET_CHATBOT = "CHATBOT";
    private static final String WIDGET_IVR = "IVR";
    private static final String WIDGET_DESIGN_TEMPLATES = "DESIGNTEMPLATES";
    private static final String WIDGET_OFFERS = "OFFERS";
    private static final String WIDGET_RATES_AND_INVENTORY = "RATESANDINVENTORY";
    private static final String WIDGET_REPORT_AND_DASHBOARD = "REPORTANDDASHBOARD";
    private static final String WIDGET_TESTIMONIALS = "TESTIMONIALS";

    public static final String WIDGET_IMAGE_GALLERY = "IMAGEGALLERY";
    public static final String WIDGET_CUSTOM_PAGES = "CUSTOMPAGES";
    public static final String WIDGET_TRANSACTION_FEES = "TRANSACTIONFEES";
    public static final String WIDGET_SUBSCRIPTION = "SUBSCRIPTION";
    public static final String WIDGET_SHOPPING_CART = "SHOPPINGCART";
    public static final String WIDGET_LATEST_UPDATES = "LATESTUPDATES";
    public static final String WIDGET_CUSTOMER_SUPPORT = "CUSTOMERSUPPORT";
    public static final String WIDGET_PRODUCT_CATALOG = "PRODUCTCATALOGUE";

    /**
     * Widget Property Key
     */
    public static final String WIDGET_PROPERTY_MAX = "MAX";
    public static final String WIDGET_PROPERTY_WEBSITE = "Website";
    public static final String WIDGET_PROPERTY_FB_PAGE = "FbPage";
    public static final String WIDGET_PROPERTY_FACEBOOK = "Facebook";
    public static final String WIDGET_PROPERTY_TWITTER = "Twitter";
    public static final String WIDGET_PROPERTY_OTHERS = "Others";
    public static final String WIDGET_PROPERTY_GALLERY = "Gallery";
    public static final String WIDGET_PROPERTY_EMAIL = "Email";
    public static final String WIDGET_PROPERTY_CHAT = "Chat";
    public static final String WIDGET_PROPERTY_PHONE = "Phone";
    public static final String WIDGET_PROPERTY_CART = "Cart";
    public static final String WIDGET_PROPERTY_TRANSACTION_FEES = "TransactionFees";
    public static final String WIDGET_PROPERTY_PAYMENT_GATEWAY = "PaymentGateway";
    public static final String WIDGET_PROPERTY_SUBSCRIPTION = "Subscription";


    //private static final String WIDGET_IMAGE_GALLERY = "IMAGEGALLERY";
    //private static final String WIDGET_IMAGE_TOB = "TOB";
    //private static final String WIDGET_IMAGE_TIMINGS = "TIMINGS";
    //private static final String WIDGET_PRODUCT_GALLERY = "PRODUCTCATALOGUE";
    //private static final String WIDGET_FB_LIKE_BOX = "FbLikeBox";
    //private static final String WIDGET_CUSTOMPAGES = "CUSTOMPAGES";
    //private static final String FP_WEB_WIDGET_DOMAIN = "DOMAINPURCHASE";


    public static boolean isNewPricingPlan = false;
    private OnWidgetListener listener;

    public enum WidgetValue
    {
        UNLIMITED("-1"), FEATURE_NOT_AVAILABLE("0");

        String value;

        WidgetValue(String value)
        {
            this.value = value;
        }

        public String getValue()
        {
            return value;
        }
    }

    public enum WidgetLimit
    {
        UNLIMITED(-1), LIMIT_EXCEEDED(0), FEATURE_NOT_AVAILABLE(-2);

        int value;

        WidgetLimit(int value)
        {
            this.value = value;
        }

        public int getValue()
        {
            return value;
        }
    }

    public void  getWidgetLimit(UserSessionManager mSessionManager, String widgetKey)
    {
        Map<String, String> params = new HashMap<>();

        params.put("clientId", Constants.clientId);
        params.put("fpId", mSessionManager.getFPID());
        params.put("widgets", widgetKey);

        Constants.restAdapterDev1.create(StoreInterface.class).getWidgetLimit(params, new Callback<Object>() {

            @Override
            public void success(Object widget, Response response)
            {
                Log.d("WIDGET_LIMIT_RESPONSE", "SUCCESS");

                String jsonString = new Gson().toJson(widget);

                try
                {
                    JSONObject request = new JSONObject(jsonString);

                    if(request.has(widgetKey))
                    {
                        int value = (int)((double) request.get(widgetKey));
                        listener.onWidgetLimit(value);
                    }
                }

                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error)
            {
                Log.d("WIDGET_LIMIT_RESPONSE", "FAIL");
            }
        });
    }

    public static void getWidgets(UserSessionManager mSessionManager, Context context) {

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
        params.put("newPackages", "true");

        Constants.restAdapterDev1.create(StoreInterface.class).getActiveWidgetList(params, new Callback<WidgetResponse>() {

            @Override
            public void success(WidgetResponse widget, Response response)
            {
                Log.d("WIDGET_RESPONSE", "SUCCESS");

                if (widget != null && widget.getActivePackages() != null)
                {
                    ActivePackage activePackage = getActivePackage(widget.getActivePackages());

                    if(activePackage != null)
                    {
                        Log.d("WIDGET_RESPONSE", "" + activePackage.getId());

                        Widget.getInstance().setActivePackage(getActivePackage(widget.getActivePackages()));
                        isNewPricingPlan = isPackageExists(context, activePackage.getId());
                    }
            }
        }
            @Override
            public void failure(RetrofitError error)
            {
                Log.d("WIDGET_RESPONSE", "FAIL");
            }
        });
    }


    private static ActivePackage getActivePackage(List<ActivePackage> activePackageList)
    {
        ActivePackage activePackage = null;

        for (ActivePackage aPackage: activePackageList)
        {
            if(aPackage.getActive() && aPackage.getProductClassification().getPackType() == 0)
            {
                activePackage = aPackage;
                break;
            }
        }

        return activePackage;
    }


    public static String getPropertyValue(String widgetKey, String key)
    {
        String value = WidgetValue.UNLIMITED.getValue();

        if(Widget.getInstance().getActivePackage() == null)
        {
            return value == null ? WidgetValue.UNLIMITED.getValue() : value;
        }

        if(Widget.getInstance().getActivePackage().getWidgetPacks() == null)
        {
            return value == null ? WidgetValue.UNLIMITED.getValue() : value;
        }

        for(WidgetPacks packs: Widget.getInstance().getActivePackage().getWidgetPacks())
        {
            if(packs.WidgetKey.equalsIgnoreCase(widgetKey))
            {
                for(WidgetPacks.Property property: packs.Properties)
                {
                    if(property.Key.equalsIgnoreCase(key))
                    {
                        value = property.Value;
                        break;
                    }
                }
            }
        }

        return value == null ? WidgetValue.UNLIMITED.getValue() : value;
    }


    private static boolean isPackageExists(Context context, String id)
    {
        boolean flag = false;

        try
        {
            InputStream is = context.getAssets().open("packages.json");

            int size = is.available();
            byte[] buffer = new byte[size];

            is.read(buffer);
            is.close();

            String json = new String(buffer, "UTF-8");

            JSONArray array = new JSONArray(json);

            for(int i=0; i<array.length(); i++)
            {
                JSONObject jsonObject = array.getJSONObject(i);

                if(jsonObject.get("_id").equals(id))
                {
                    flag = true;
                    break;
                }
            }
        }

        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return flag;
    }

    public void setWidgetListener(OnWidgetListener listener)
    {
        this.listener = listener;
    }

    public interface OnWidgetListener
    {
        void onWidgetLimit(int limit);
    }
}
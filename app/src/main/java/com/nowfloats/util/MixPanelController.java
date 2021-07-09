package com.nowfloats.util;

import android.app.Activity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class MixPanelController {

    public static final String WHATS_APP_DIALOG = "WhatsAppDialog";
    public static final String WHATS_APP_DIALOG_CLICKED = "WhatsAppDialogClicked";
    public static final String BUBBLE_IN_APP_DIALOG = "BubbleInAppDialog";
    public static final String BUBBLE_IN_APP_DIALOG_CLICKED = "BubbleInAppDialogClicked";
    public static final String BUBBLE_DIALOG = "BubbleDialogOnWhatsApp";
    public static final String BUBBLE_OVERLAY_PERM = "BubbleOverlayPerm";
    public static final String BUBBLE_DIALOG_SHARE = "BubbleWhatsAppProductShareClicked";
    public static final String BUBBLE_CLOSED = "BubbleClosedByUser";
    public static final String BUBBLE_ENABLED = "BubbleEnabled";
    public static final String PRODUCTIVE_CALLS = "ProductiveCalls";
    public static final String BUBBLE_SERVICE_ENABLED = "BubbleServiceEnabled";
    public static final String BUBBLE_CALL_TRACKER = "BubbleCallTracker";
    public static final String BUBBLE_VIEW_ORDERS = "BubbleViewOrders";
    public static final String BUBBLE_ORDER_DETAIL = "BubbleOrderDetail";
    public static final String BUBBLE_DISMISS_CALL_TRACKER = "BubbleDismissCallTracker";
    public static final String BUBBLE_DISMISS_ENQUIRY = "BubbleDismissEnquiry";
    public static final String BUBBLE_DISMISS_ORDER = "BubbleDismissOrder";
    public static final String BUBBLE_ENQUIRY = "BubbleEnquiry";
    public static final String WHATSAPP_TO_BOOST = "whatsapp_to_boost";
    public static final String LINK_DOMAIN = "LinkDomain";
    public static final String BOOK_DOMAIN = "BookDomain";
    public static final String DOMAIN_SEARCH = "DomainSearch";
    public static final String VMN_CALL_TRACKER = "VmnCallTracker";
    public static final String VMN_CALL_TRACKER_LOGS = "VmnCallTrackerLogs";
    public static final String IMAGE_GALLERY = "ImageGallery";
    public static final String MANAGE_INVENTORY = "ManageInventory";
    public static final String FACEBOOK_REVIEW = "FacebookReview";
    public static final String PRIMARY_NUMBER_CHANGE = "PrimaryNumberChanged";
    public static final String BUSINESS_APP_REQUEST = "BusinessAppRequest";
    public static final String BUSINESS_APP_BUILD_IN_PROCESS = "BusinessAppInProcess";
    public static final String BUSINESS_APP_INTRESTED = "BizAppsIntrested";
    public static final String BUSINESS_APP_PUBLISHED = "BusinessAppPublished";
    public static final String SHARE_BUSINESS_APP = "BusinessAppShare";
    public static final String BUSINESS_APP = "BusinessApps";
    public static final String BUSINESS_ENQUIRY = "BusinessEnquiry";
    public static final String FACEBOOK_PAGE_NOT_FOUND = "FacebookPageNotFound";
    public static final String CREATE_FACEBOOK_PAGE = "CreateFacebookPage";
    public static final String FACEBOOK_PAGE_CREATED_WITH_LOGO = "FacebookPageCreatedWithLogo";
    public static final String FACEBOOK_PAGE_CREATED_WITH_DEFAULT_IMAGE = "FacebookPageCreatedWithDefaultImage";
    public static final String FACEBOOK_PAGE_PROFILE_INCOMPLETE = "FacebookPageProfileIncomplete";
    public static final String FACEBOOK_PAGE_ERROR_IN_CREATE = "FacebookPageErrorInCreate";
    public static final String FACEBOOK_PAGE_INVALID_NAME = "FacebookPageInvalidName";

    public static final String SAM_BUBBLE_NOTIFICATION = "SAMBubbleNotification";
    public static final String SAM_BUBBLE_CLICKED = "SAMBubbleClicked";
    public static final String SAM_BUBBLE_CLICKED_DATA = "SAMBubbleClickedData";
    public static final String SAM_BUBBLE_CLICKED_NO_DATA = "SAMBubbleClickedNoData";
    public static final String SAM_BUBBLE_CLICKED_SERVER_ERROR = "SAMBubbleClickedServerError";
    public static final String SAM_BUBBLE_SELECTED_MESSAGES = "SAMBubbleSelectedMessages";
    public static final String SAM_BUBBLE_ACTION_CALL = "SAMBubbleActionCall";
    public static final String SAM_BUBBLE_ACTION_SHARE = "SAMBubbleActionShare";
    public static final String MY_ORDERS = "MyOrders";
    public static final String ORDER_LIST = "OrderList";
    public static final String ORDER_DETAILS = "OrderDetails";
    public static final String ORDER_ANALYTICS = "OrderAnalytics";
    public static final String REVENUE_ANALYTICS = "RevenueAnalytics";
    public static final String FILTER_ORDER_ANALYTICS = "FilterOrderAnalytics";
    public static final String CONFIRM_ORDER = "ConfirmOrder";
    public static final String CANCEL_ORDER = "CancelOrder";
    public static final String SHIP_ORDER = "ShipOrder";
    public static final String DELIVER_ORDER = "DeliverOrder";
    public static final String ESCALATE_ORDER = "EscalateOrder";
    public static final String SUBSCRIPTIONS = "Subscriptions";
    public static final String SUBSCRIPTION_HISTORY = "SubscriptionHistory";
    public static final String BUY_AND_RENEW = "BuyAndRenew";


    public static final String THIRD_PARTY_DATA = "ThirdPartyData";
    public static final String THIRD_PARTY_RATING = "ThirdPartyRating";
    public static final String THIRD_PARTY_DATA_DETAIL = "ThirdPartyDataDetail";
    public static final String THIRD_PARTY_ACTION_CALL = "ThirdPartyActionCall";
    public static final String THIRD_PARTY_ACTION_SHARE = "ThirdPartyActionShare";
    public static final String DOMAIN_EMAIL_BOOK = "EmailBook";
    public static final String DOMAIN_EMAIL_REQUEST = "EmailRequest";
    public static final String UPDATE_DB_CRASH = "updateDbCrash";
    public static final String DICTATE_CLICK = "DictateClick";
    public static final String WILDFIRE_CLICK = "DictateClick";
    public static final String REQUEST_FOR_WILDFIRE_PLAN = "WildFireRequest";
    public static final String UPDATE_TIPS_CLICK = "UpdateTipsClick";
    public static final String HELP_AND_SUPPORT_CHAT = "HelpAndSupportChat";
    public static final String HELP_AND_SUPPORT_EMAIL = "HelpAndSupportEmail";
    public static final String HELP_AND_SUPPORT_CALL = "HelpAndSupportCall";
    public static final String HELP_AND_SUPPORT_CLICK = "HelpAndSupportClick";

    public static final String KEYBOARD_ACTIVATED = "KeyboardActivated";
    public static final String KEYBOARD_ENABLED = "KeyboardEnabled";


    public static final String ON_BOARDING_COMPLETE = "OnBoardingComplete";
    public static final String ON_BOARDING_SCREEN_SHOW = "OnBoardingShow";
    public static final String ON_BOARDING_SHARE_WEBSITE = "OnBoardingShareWebsite";
    public static final String ON_BOARDING_BOOST_APP = "OnBoardingBoostApp";
    public static final String ON_BOARDING_ADD_PRODUCT = "OnBoardingAddProduct";
    public static final String ON_BOARDING_CUSTOM_PAGE = "OnBoardingAddCustomPage";
    public static final String ON_BOARDING_SITE_HEALTH = "OnBoardingSiteHealth";
    public static final String ON_BOARDING_WELCOME_ABOARD = "OnBoardingWelcomeAboard";
    public static final String SEARCH_RANKING_MAIN = "SearchRankingMain";
    public static final String SEARCH_RANKING_INCREASED = "SearchRankingIncrease";
    public static final String SEARCH_RANKING_DECREASE = "SearchRankingDecrease";
    public static final String SEARCH_RANKING_LOST = "SearchRankingLost";
    public static final String SEARCH_RANKING_NEW = "SearchRankingNew";
    public static final String SEARCH_RANKING_SAME = "SearchRankingSame";

    //    private static MixpanelAPI mixPanel;
    public static String Bhours = "bhours", Signup = "SignUpActivity",
            landingPage = "LandingPage",
            BusinessDetailActivity = "BIZdetailActivity",
            contactInfoActivity = "ContactInfoActivity",
            otherImgsActivity = "OtherImgActivity",
            primaryImgActivity = "PrimaryImageActivity",
            Inbox = "INBOXACTIVITY", loginActivity = "LOGIN",
            mainActivity = "MainActivity", searchActivity = "SearchActivity",
            messageFloat = "MessageFloat", feedback = "SendEmailActivity",
            MessageDetailView = "MessageDetailView",
            FacebookActivity = "FacebookAnalytics";
    private static String KEY = "";
    //    public static MixpanelAPI.People people = null;
    public static String TERM_AND_POLICY_CHECKBOX = "termAndPolicyUnCheck";

    public static void setMixPanel(Activity app, String key) {
        KEY = key;
//        if (mixPanel != null)
//            mixPanel.flush();
        /** Boost App **/
//        mixPanel = MixpanelAPI.getInstance(app, "7d962760bccee86ab026331478d49bab");

        /**New Test Id**/
//        mixPanel = MixpanelAPI.getInstance(app,"21d1bf26130e59cc8a0189372c010c25");

        /**Not been used**/
//        mixPanel = MixpanelAPI.getInstance(app,       // Test Account
//                "b18441b030208ce549b868df8bd2dca0");
//        mixPanel = MixpanelAPI.getInstance(app,
//                "5c0cca3bbce2eba6ad747cef23d6ff61");  // Thinksity

        // sai ram's test projects token 225443802089bae891fde6278c49bd23
        // production mix panel 4e7a96d77810dfe85899a4eb1b3feb2b
        // mixPanel = MixpanelAPI.getInstance(app,
        // "957da88e50221dedf6dac5f189d5db82");
    }

    public static void flushMixPanel(String key) {
//        if (KEY.equals(key))
//            mixPanel.flush();
    }

    public static void track(String event, JSONObject props) {
        try {
//            ApxorSDK.logAppEvent(event, (HashMap<String, String>) jsonToMap(props));
//            if (mixPanel != null)n
//                mixPanel.track(event, props);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> jsonToMap(JSONObject json) throws JSONException {
        Map<String, String> retMap = null;
        if (json != null) {
            retMap = new HashMap<String, String>();
            if (json != JSONObject.NULL) {
                retMap = toMap(json);
            }
        }
        return retMap;
    }

    public static Map<String, String> toMap(JSONObject object) throws JSONException {
        Map<String, String> map = new HashMap<String, String>();

        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

//            if (value instanceof JSONArray) {
//                value = toList((JSONArray) value);
//            } else if (value instanceof JSONObject) {
//                value = toMap((JSONObject) value);
//            } else {
//
//            }
            if (value instanceof String) {
                if (key.contains("$"))
                    key = key.replace("$", "");
                map.put(key, String.valueOf(value));
            }
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }


    public static void identify(String id, JSONObject param, String fpid) {
//        Log.v("mixpanel", id);
        try {

//            ApxorSDK.setUserIdentifier(id);
//            ApxorSDK.setUserCustomInfo((HashMap<String, String>) toMap(param));

//            if (mixPanel == null) return;
//            mixPanel.identify(id);
//            people = mixPanel.getPeople();
//            people.identify(id);
//            people.set(param);
//            //	people.set("$email", Constants.StoreEmail);
//            //  669302602295 - Boost Project ID
//            // 150516431070 - Test Project ID
//            people.initPushHandling("669302602295");
//            // people.initPushHandling("276987746927");
//            people.set("Notification", fpid);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendMixPanelProperties(String storeName, String email, String fpTAG, String dateString) {
        JSONObject store = new JSONObject();
        String dateTime = null;
        try {
            store.put("$name", storeName);
            store.put("$email", email);
            dateString = dateString.replace("/Date(", "").replace(")/", "");
            Long epochTime = Long.parseLong(dateString);
            Date date = new Date(epochTime);
            DateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            format.setTimeZone(TimeZone.getDefault());
            dateTime = format.format(date);
            store.put("$Created On", dateTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MixPanelController.createUser(fpTAG.toUpperCase(), store);
    }


    public static void setProperties(String plan, String status) {
        try {
//            if (mixPanel != null)
//                mixPanel.getPeople().set(plan, status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createUser(String id, JSONObject store) {
//		Log.v("mixpanel", id);
        try {
//            mixPanel.identify(id);
//            people = mixPanel.getPeople();
//            people.identify(id);
//            people.set(store);
            //people.set("$email", Constants.StoreEmail);
            //  669302602295 - Boost Project ID
            // 150516431070 - Test Project ID
//            people.initPushHandling("669302602295");
//            ApxorSDK.setUserIdentifier(id);
//            ApxorSDK.setUserCustomInfo((HashMap<String, String>) toMap(store));
            //people.initPushHandling("276987746927");
            // people.withIdentity(Constants.Store_id);
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

}
package dev.patrickgold.florisboard.customization.util;

import android.content.Context;

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

/**
 * Created by Admin on 02-03-2018.
 */

public class MixPanelUtils {
    public static final String KEYBOARD_IMAGE_SHARING = "KeyboardImageSharing";
    public static final String KEYBOARD_ICON_CLICKED = "KeyboardIconClicked";
    public static final String KEYBOARD_SHOW_UPDATES = "KeyboardShowUpdates";
    public static final String KEYBOARD_SHOW_PRODUCT = "KeyboardShowProducts";
    public static final String KEYBOARD_VOICE_INPUT = "KeyboardVoiceInput";
    public static final String KEYBOARD_SHOW_PHOTOS = "KeyboardShowPhotos";
    public static final String KEYBOARD_CREATE_OFFER = "KeyboardCreateOffer";
    public static final String KEYBOARD_SHARE_OFFER = "KeyboardShareOffer";
    public static final String KEYBOARD_SHOW_DETAILS = "KeyboardShowDetails";
    public static final String KEYBOARD_SPEECH_RESULT = "KeyboardVoiceResult";
    public static final String KEYBOARD_UPDATE_IMAGE_SHARE = "KeyboardUpdateImageShare";
    public static final String KEYBOARD_PRODUCT_SHARE = "KeyboardProductShare";
    public static final String KEYBOARD_UPDATE_SHARE = "KeyboardUpdateShare";
    public static final String KEYBOARD_INPUT_CONNECTION_NULL = "NullInputConnection";
    public static final String SET_HINDI_KEYBOARD = "KeyboardLanguageSetToHindi";
    public static final String SET_ENGLISH_KEYBOARD = "KeyboardLanguageSetToEnglish";
    public static final String KEYBOARD_THEME_CHANGE_TO_LXX_DARK = "KeyboardThemeChangeToLxxDark";
    public static final String KEYBOARD_THEME_CHANGE_TO_LXX_DARK_UNBORDERED = "KeyboardThemeChangeToLxxDarkUnboredered";
    public static final String KEYBOARD_THEME_NAVIGATION_THROUGH_KEYBOARD = "KeyboardThemeNavigationThroughKeyboard";
    private static MixPanelUtils mixPanelUtils = new MixPanelUtils();
    //private static MixpanelAPI mixPanel;
    //public static MixpanelAPI.People people = null;

    private MixPanelUtils() {
    }

    public static MixPanelUtils getInstance() {
        return mixPanelUtils;
    }

    public static void setMixPanel(Context app) {
        /*if (mixPanel != null)
            mixPanel.flush();*/
        /** Boost App **/
        //mixPanel = MixpanelAPI.getInstance(app, "7d962760bccee86ab026331478d49bab");

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

    public static void reset(Context app) {
        /*try {
            if (mixPanel == null)
                mixPanel = MixpanelAPI.getInstance(app, "7d962760bccee86ab026331478d49bab");
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    public static void flushMixPanel() {
        /*if (mixPanel != null)
            mixPanel.flush();*/
    }

    public static void track(String event, JSONObject props) {
        try {
//            ApxorSDK.logAppEvent(event, (HashMap<String, String>) jsonToMap(props));
            /*if (mixPanel != null)
                mixPanel.track(event, props);*/
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
//            if (mixPanel == null) return;
//            mixPanel.identify(id);
//            people = mixPanel.getPeople();
//            people.identify(id);
//            people.set(param);
            //	people.set("$email", Constants.StoreEmail);
            //  669302602295 - Boost Project ID
            // 150516431070 - Test Project ID
            //people.initPushHandling("669302602295");
            // people.initPushHandling("276987746927");
            //people.set("Notification", fpid);

//            ApxorSDK.setUserIdentifier(id);
//            ApxorSDK.setUserCustomInfo((HashMap<String, String>) toMap(param));

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
        MixPanelUtils.createUser(fpTAG.toUpperCase(), store);
    }


    public static void setProperties(String plan, String status) {
        /*try {
            if (mixPanel != null)
                mixPanel.getPeople().set(plan, status);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    public static void createUser(String id, JSONObject store) {
//		Log.v("mixpanel", id);
        try {

//            if (mixPanel == null)
//                return;
//            mixPanel.identify(id);
//            people = mixPanel.getPeople();
//            people.identify(id);
//            people.set(store);
            //people.set("$email", Constants.StoreEmail);
            //  669302602295 - Boost Project ID
            // 150516431070 - Test Project ID
            //people.initPushHandling("669302602295");
//            ApxorSDK.setUserIdentifier(id);
//            ApxorSDK.setUserCustomInfo((HashMap<String, String>) toMap(store));
            //people.initPushHandling("276987746927");
            // people.withIdentity(Constants.Store_id);
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

}
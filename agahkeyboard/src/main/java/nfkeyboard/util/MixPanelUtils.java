package nfkeyboard.util;

import android.content.Context;

import com.apxor.androidsdk.ApxorSDK;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
    private static MixPanelUtils mixPanelUtils = new MixPanelUtils();
    private MixpanelAPI mixPanel;

    private MixPanelUtils() {
    }

    public static MixPanelUtils getInstance() {
        return mixPanelUtils;
    }

    public void setMixPanel(Context app) {
        flush();
        /** Boost App **/
        mixPanel = MixpanelAPI.getInstance(app, "7d962760bccee86ab026331478d49bab");
        //store.put("Tag", SharedPrefUtil.fromBoostPref().getsBoostPref(app).getIdentifier());

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

    public void track(String event, JSONObject Props) {
        try {
            ApxorSDK.logAppEvent(event, (HashMap<String, String>) jsonToMap(Props));
            if (mixPanel != null)
                mixPanel.track(event, Props);
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


    public void flush() {
        if (mixPanel != null) {
            mixPanel.flush();
        }
    }

    public void createUser(String fpTag) {
        flush();
        if (mixPanel != null && mixPanel.getPeople() != null)
            mixPanel.getPeople().identify(fpTag);
    }
}

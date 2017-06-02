package com.nowfloats.util;

import android.app.Activity;
import android.util.Log;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class MixPanelController {

	public static final String WHATS_APP_DIALOG ="WhatsAppDialog" ;
	public static final String WHATS_APP_DIALOG_CLICKED ="WhatsAppDialogClicked" ;
	public static final String BUBBLE_IN_APP_DIALOG = "BubbleInAppDialog";
	public static final String BUBBLE_IN_APP_DIALOG_CLICKED = "BubbleInAppDialogClicked";
	public static final String BUBBLE_DIALOG = "BubbleDialogOnWhatsApp";
	public static final String BUBBLE_DIALOG_SHARE = "BubbleWhatsAppProductShareClicked";
	public static final String BUBBLE_CLOSED = "BubbleClosedByUser";
	public static final String WHATSAPP_TO_BOOST = "whatsapp_to_boost";
    public static final String LINK_DOMAIN = "LinkDomain";
    public static final String BOOK_DOMAIN = "BookDomain";
    public static final String DOMAIN_SEARCH = "DomainSearch";
    public static final String VMN_CALL_TRACKER = "VmnCallTracker";
    public static final String FACEBOOK_REVIEW = "FacebookReview";
    public static final String PRIMARY_NUMBER_CHANGE = "PrimaryNumberChanged";
    public static final String BUSINESS_APP = "BizApps";

    private static MixpanelAPI mixPanel;
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
			FacebookActivity="FacebookAnalytics";
	private static String KEY = "";
	public static MixpanelAPI.People people = null;
    public static String TERM_AND_POLICY_CHECKBOX = "termAndPolicyUnCheck";

    public static void setMixPanel(Activity app, String key) {
		KEY = key;
		if (mixPanel != null)
			mixPanel.flush();
        /** Boost App **/
        mixPanel = MixpanelAPI.getInstance(app, "7d962760bccee86ab026331478d49bab");

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
        if (KEY.equals(key))
            mixPanel.flush();
    }

    public static void track(String event, JSONObject Props) {
        try {
            if (mixPanel != null)
                mixPanel.track(event, Props);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void identify(String id, JSONObject param, String fpid) {
        Log.v("mixpanel", id);
        try {
            if (mixPanel == null) return;
            mixPanel.identify(id);
            people = mixPanel.getPeople();
            people.identify(id);
            people.set(param);
            //	people.set("$email", Constants.StoreEmail);
            //  669302602295 - Boost Project ID
            // 150516431070 - Test Project ID
            people.initPushHandling("669302602295");
            // people.initPushHandling("276987746927");
            people.set("Notification", fpid);
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
            DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            format.setTimeZone(TimeZone.getDefault());
            if (date != null)
                dateTime = format.format(date);
            store.put("$Created On", dateTime);
            MixPanelController.createUser(fpTAG.toUpperCase(), store);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void setProperties(String plan, String status) {
        try {
            if (mixPanel != null)
                mixPanel.getPeople().set(plan, status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createUser(String id, JSONObject store) {
//		Log.v("mixpanel", id);
        try {
            mixPanel.identify(id);
            people = mixPanel.getPeople();
            people.identify(id);
            people.set(store);
            //people.set("$email", Constants.StoreEmail);
            //  669302602295 - Boost Project ID
            // 150516431070 - Test Project ID
            people.initPushHandling("669302602295");
            //people.initPushHandling("276987746927");
            // people.withIdentity(Constants.Store_id);
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }
}
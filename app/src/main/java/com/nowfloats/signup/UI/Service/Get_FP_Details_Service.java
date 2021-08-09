package com.nowfloats.signup.UI.Service;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.framework.models.firestore.FirestoreManager;
import com.google.gson.Gson;
import com.nowfloats.Analytics_Screen.API.NfxFacebbokAnalytics;
import com.nowfloats.Analytics_Screen.model.NfxGetTokensResponse;
import com.nowfloats.BusinessProfile.UI.API.Facebook_Auto_Publish_API;
import com.nowfloats.Login.Login_MainActivity;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.GetAutoPull;
import com.nowfloats.PreSignUp.SplashScreen_Activity;
import com.nowfloats.Store.Model.ActivePackage;
import com.nowfloats.Store.Model.PricingPlansModel;
import com.nowfloats.Store.Model.WidgetPacks;
import com.nowfloats.Store.Service.StoreInterface;
import com.nowfloats.signup.UI.API.Retro_Signup_Interface;
import com.nowfloats.signup.UI.Model.Get_FP_Details_Event;
import com.nowfloats.signup.UI.Model.Get_FP_Details_Model;
import com.nowfloats.signup.UI.Model.ProcessFPDetails;
import com.nowfloats.signup.UI.UI.WebSiteAddressActivity;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.twitter.TwitterConnection;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.WebEngageController;
import com.squareup.otto.Bus;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.framework.utils.GsonUtilsKt.convertListObjToString;
import static com.framework.webengageconstant.EventLabelKt.EVENT_LABEL_ANDROID_FAILED_TO_LOAD_DATA_IN_GET_FP_DETAILS;
import static com.framework.webengageconstant.EventLabelKt.EVENT_LABEL_ANDROID_GET_FP_DETAILS_FP_DATA_NULL;
import static com.framework.webengageconstant.EventLabelKt.EVENT_LABEL_ANDROID_GET_FP_DETAILS_SERVICE_EXCEPTION;
import static com.framework.webengageconstant.EventNameKt.ANDROID_FAILED_TO_LOAD_DATA_IN_GET_FP_DETAILS;
import static com.framework.webengageconstant.EventNameKt.ANDROID_GET_FP_DETAILS_FP_DATA_NULL;
import static com.framework.webengageconstant.EventNameKt.ANDROID_GET_FP_DETAILS_SERVICE_EXCEPTION;
import static com.nfx.leadmessages.Constants.CALL_LOG_TIME_INTERVAL;
import static com.nfx.leadmessages.Constants.SHARED_PREF;
import static com.nfx.leadmessages.Constants.SMS_REGEX;

/**
 * Created by NowFloatsDev on 25/05/2015.
 */
public class Get_FP_Details_Service {

    private static final String TAG = "Get_FP_Details_Service";
    public Get_FP_Details_Service(final Activity activity, String fpID, String clientID, final Bus bus) {
        HashMap<String, String> map = new HashMap<>();
        map.put("clientId", clientID);
        Retro_Signup_Interface getFPDetails = Constants.restAdapter.create(Retro_Signup_Interface.class);
        getFPDetails.post_getFPDetails(fpID, map, new Callback<Get_FP_Details_Model>() {
            @Override
            public void success(Get_FP_Details_Model get_fp_details_model, final Response response) {
                try {
                    Log.i(TAG, "onSuccess: "+ new Gson().toJson(get_fp_details_model));
                    if (get_fp_details_model != null) {
                        ProcessFPDetails.storeFPDetails(activity, get_fp_details_model);
                        UserSessionManager mSession = new UserSessionManager(activity.getApplicationContext(), activity);
                        String accId = get_fp_details_model.getAccountManagerId();
                        String appId = get_fp_details_model.getApplicationId();
                        String country = get_fp_details_model.getCountry();

                        Map<String, String> params = new HashMap<>();
                        if (accId.length() > 0) {
                            params.put("identifier", accId);
                        } else {
                            params.put("identifier", appId);
                        }
                        params.put("clientId", Constants.clientId);
                        params.put("fpId", mSession.getFPID());
                        params.put("country", country.toLowerCase());
                        params.put("fpCategory", get_fp_details_model.getFpCategory());
                        Log.d("getStoreList_fpId: ", mSession.getFPID());
                        FirestoreManager.INSTANCE.initData(mSession.getFpTag(), mSession.getFPID(), Constants.clientId);
                        Constants.restAdapter.create(StoreInterface.class).getStoreList(params, new Callback<PricingPlansModel>() {
                            @Override
                            public void success(PricingPlansModel storeMainModel, Response response) {
                                if (storeMainModel != null) {
                                    preProcessAndDispatchPlans(storeMainModel, mSession);
                                } else {
                                    Log.d("getStoreList_null", String.valueOf(response.getStatus()));
                                }
                                // zeroth screen
                            }

                            @Override
                            public void failure(RetrofitError error) {

                                Log.d("getStoreList_fail", error.getMessage());
                            }
                        });
                        bus.post(new Get_FP_Details_Event(get_fp_details_model));
                    } else {
                        activity.runOnUiThread(() -> {
                            if (activity.isFinishing()) return;
                            Methods.showSnackBarNegative(activity, activity.getString(R.string.something_went_wrong_try_again));
                            WebEngageController.trackEvent(ANDROID_GET_FP_DETAILS_FP_DATA_NULL, EVENT_LABEL_ANDROID_GET_FP_DETAILS_FP_DATA_NULL, fpID);

                            if (WebSiteAddressActivity.pd != null) {
                                WebSiteAddressActivity.pd.dismiss();
                            }
                            if (SplashScreen_Activity.pd != null) {
                                SplashScreen_Activity.pd.dismiss();
                            }

                            if (Login_MainActivity.progressDialog != null && Login_MainActivity.progressDialog.isShowing()) {
                                Login_MainActivity.progressDialog.dismiss();
                            }
                        });
                        bus.post(response);
                    }
                } catch (Exception e) {
                    WebEngageController.trackEvent(ANDROID_GET_FP_DETAILS_SERVICE_EXCEPTION, EVENT_LABEL_ANDROID_GET_FP_DETAILS_SERVICE_EXCEPTION, e.getMessage());
                    bus.post(e.getLocalizedMessage());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("", "" + error.getMessage());
                WebEngageController.trackEvent(ANDROID_FAILED_TO_LOAD_DATA_IN_GET_FP_DETAILS, EVENT_LABEL_ANDROID_FAILED_TO_LOAD_DATA_IN_GET_FP_DETAILS, fpID);
                try {
                    activity.runOnUiThread(() -> {
                        //Methods.showSnackBarNegative(activity, "Something went wrong! Please try again.");
                        if (WebSiteAddressActivity.pd != null) {
                            WebSiteAddressActivity.pd.dismiss();
                        }
                        if (SplashScreen_Activity.pd != null) {
                            try {
                                SplashScreen_Activity.pd.dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    MixPanelController.track(EventKeysWL.GET_FP_FAILURE_EVENT, null);
                    bus.post(error);
                } catch (Exception e) {
                    bus.post(e.getLocalizedMessage());
                }
            }
        });
    }

    public static void autoPull(Activity activity, String fpID) {

        final UserSessionManager session = new UserSessionManager(activity.getApplicationContext(), activity);
        final SharedPreferences pref = activity.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        session.storeFPDetails(Key_Preferences.FB_PULL_PAGE_NAME, null);

        session.storeFPDetails(Key_Preferences.FB_PULL_ENABLED, "false");
        session.storeFPDetails(Key_Preferences.FB_PULL_COUNT, "0");
        pref.edit().putBoolean("FBFeedPullAutoPublish", false).apply();
        Facebook_Auto_Publish_API.autoPullApi apis = Facebook_Auto_Publish_API.getAdapter();
        apis.getFacebookAutoPull(fpID, Constants.clientId, new Callback<GetAutoPull>() {
            @Override
            public void success(GetAutoPull obj, Response response) {
                //Log.v("ggg","page "+obj);
                if (obj == null || obj.toString().length() == 0) return;

                boolean autoPublish = obj.getAutoPublish();
                String facebookPageName = obj.getFacebookPageName();
                int count = obj.getCount();
                pref.edit().putBoolean("FBFeedPullAutoPublish", autoPublish).apply();
                session.storeFPDetails(Key_Preferences.FB_PULL_PAGE_NAME, facebookPageName);
                session.storeFPDetails(Key_Preferences.FB_PULL_ENABLED, String.valueOf(autoPublish));
                session.storeFPDetails(Key_Preferences.FB_PULL_COUNT, String.valueOf(count));
            }

            @Override
            public void failure(RetrofitError error) {
                Log.v("ggg", error + "");
            }
        });
    }

    public static void newNfxTokenDetails(final Activity activity, String fpID, final Bus bus) {
        NfxFacebbokAnalytics.nfxFacebookApis facebookApis = NfxFacebbokAnalytics.getAdapter();
        facebookApis.nfxGetSocialTokens(fpID, new Callback<NfxGetTokensResponse>() {
            @Override
            public void success(NfxGetTokensResponse nfxGetTokensResponse, Response response) {

                if (nfxGetTokensResponse == null || activity == null || activity.isFinishing()) {
                    bus.post(new NfxGetTokensResponse());
                    return;
                }
                SharedPreferences pref = activity.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
                List<String> regexList = nfxGetTokensResponse.getSmsRegex();
                if (regexList != null && regexList.size() > 0) {
                    String s = TextUtils.join(",", regexList);
                    pref.edit().putString(SMS_REGEX, s).apply();
                    pref.edit().putString(CALL_LOG_TIME_INTERVAL, nfxGetTokensResponse.getCallLogTimeInterval()).apply();
                }
                /*String message = nfxGetTokensResponse.getMessage();
                if (message != null && message.equalsIgnoreCase("success")) {*/
                StoreData(activity, nfxGetTokensResponse.getNFXAccessTokens());
                bus.post(nfxGetTokensResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("", "" + error.getMessage());
                try {
                    bus.post(new NfxGetTokensResponse());
                    activity.runOnUiThread(() -> {
                        //Methods.showSnackBarNegative(activity, "Something went wrong! Please try again.");
                        if (WebSiteAddressActivity.pd != null) {
                            WebSiteAddressActivity.pd.dismiss();
                        }
                        if (SplashScreen_Activity.pd != null) {
                            bus.post(new NfxGetTokensResponse());
                            try {
                                SplashScreen_Activity.pd.dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    });
                } catch (Exception e) {

                }
            }
        });
    }

    private static void StoreData(Activity activity, List<NfxGetTokensResponse.NFXAccessToken> nfxGetTokensList) {
        UserSessionManager session = new UserSessionManager(activity.getApplicationContext(), activity);
        SharedPreferences pref = activity.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences twitterPref = activity.getSharedPreferences(TwitterConnection.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("fbShareEnabled", false);
        session.storeFacebookName(null);
        editor.putString("fbAccessId", null);
        editor.putBoolean("fbPageShareEnabled", false);
        session.storeFacebookPage(null);
        editor.putString("fbPageAccessId", null);
        editor.putInt("fbStatus", 0);
        editor.putInt("fbPageStatus", 0);
        editor.putInt("quikrStatus", -1);
        editor.putInt("facebookChatStatus", 0);
        editor.apply();
        SharedPreferences.Editor tPrefEditor = twitterPref.edit();
        tPrefEditor.putBoolean(TwitterConnection.PREF_KEY_TWITTER_LOGIN, false);
        tPrefEditor.putString(TwitterConnection.PREF_USER_NAME, null);
        tPrefEditor.apply();
        for (NfxGetTokensResponse.NFXAccessToken model : nfxGetTokensList) {
            if (model.getType().equalsIgnoreCase("facebookusertimeline")) {
                SharedPreferences.Editor editorFb = pref.edit();
                session.storeFacebookName(model.getUserAccountName());
                editorFb.putInt("fbStatus", Integer.parseInt(model.getStatus()));
                if (!Util.isNullOrEmpty(session.getFacebookName())) {
                    editorFb.putBoolean("fbShareEnabled", true);
                }
                editorFb.putString("fbAccessId", model.getUserAccountId());
                editorFb.apply();
            } else if (model.getType().equalsIgnoreCase("facebookpage")) {
                SharedPreferences.Editor editorFbPage = pref.edit();
                if (TextUtils.isDigitsOnly(model.getStatus()))
                    editorFbPage.putInt("fbPageStatus", Integer.parseInt(model.getStatus()));
                session.storeFacebookPage(model.getUserAccountName());
                if (!Util.isNullOrEmpty(session.getFacebookPage())) {
                    editorFbPage.putBoolean("fbPageShareEnabled", true);
                }
                editorFbPage.putString("fbPageAccessId", model.getUserAccountId());
                editorFbPage.apply();
            } else if (model.getType().equalsIgnoreCase("twitter")) {
                SharedPreferences.Editor twitterPrefEditor = twitterPref.edit();
                if (model.getStatus().equals("1") || model.getStatus().equals("3")) {
                    twitterPrefEditor.putBoolean(TwitterConnection.PREF_KEY_TWITTER_LOGIN, true);
                    Constants.twitterShareEnabled = true;
                }
                twitterPrefEditor.putString(TwitterConnection.PREF_USER_NAME, model.getUserAccountName());
                twitterPrefEditor.apply();
            } else if (model.getType().equalsIgnoreCase("quikr")) {
                pref.edit().putInt("quikrStatus", Integer.parseInt(model.getStatus())).apply();
            } else if (model.getType().equalsIgnoreCase("facebookchat")) {
                pref.edit().putInt("facebookChatStatus", Integer.parseInt(model.getStatus())).apply();
            }
        }
    }

    private void preProcessAndDispatchPlans(final PricingPlansModel storeMainModel, UserSessionManager mSession) {
        Log.i(TAG, "preProcessAndDispatchPlans: ");
        new Thread(() -> {
            for (ActivePackage activePackage : storeMainModel.activePackages) {
                int featuresCount = 0;
                StringBuilder featuresBuilder = new StringBuilder("");
                if (activePackage.getWidgetPacks() != null) {
                    for (WidgetPacks widget : activePackage.getWidgetPacks()) {
                        if (widget.Name != null) {
                            featuresBuilder.append("• " + widget.Name + "\n");

                        }
                    }
                    if (featuresCount > 0) {
                        featuresBuilder.delete(featuresBuilder.lastIndexOf("\n"), featuresBuilder.length());
                    }
                }
                activePackage.setFeatures(featuresBuilder.toString());
                Log.v("getActivatedon", " active: " + getMilliseconds(activePackage.getToBeActivatedOn()) + " current:" + Calendar.getInstance().getTimeInMillis());
                if (Calendar.getInstance().getTimeInMillis() < getMilliseconds(activePackage.getToBeActivatedOn())) {
                } else if (!isPackageExpired(activePackage)) {
                    //Log.v("getcurentPackageID", "IDvalue "+ activePackage.getClientProductId());
                    Constants.currentActivePackageId = activePackage.getClientProductId();
                    if (activePackage.getClientProductId() != null) {
                        if (activePackage.getClientProductId().contains("59ce2ae56431a80b009cb1fa")) {
                            List<String> storeWidgets = mSession.getStoreWidgets();
                            if (storeWidgets == null) storeWidgets = new ArrayList();
                            storeWidgets.add("BOOSTKEYBOARD");
                            Constants.StoreWidgets = (ArrayList<String>) storeWidgets;
                            mSession.storeFPDetails(Key_Preferences.STORE_WIDGETS, convertListObjToString(storeWidgets));
                        }
                    }

                } else {
                }
            }

        }).start();
    }

    private long getMilliseconds(String date) {
        if (date.contains("/Date")) {
            date = date.replace("/Date(", "").replace(")/", "");
        }
        return Long.valueOf(date);
    }

    private boolean isPackageExpired(ActivePackage activePackage) {
        long time = Long.parseLong(activePackage.getToBeActivatedOn().replaceAll("[^\\d]", ""));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        double totalMonthsValidity = activePackage.getTotalMonthsValidity();
        calendar.add(Calendar.MONTH, (int) Math.floor(totalMonthsValidity));
        calendar.add(Calendar.DATE, (int) ((totalMonthsValidity - Math.floor(totalMonthsValidity)) * 30));
        Log.v("isPackageExpired", " time: " + calendar.getTime() + " new date: " + new Date());
        return calendar.getTime().before(new Date());
    }
}
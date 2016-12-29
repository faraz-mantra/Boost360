package com.nowfloats.signup.UI.Service;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.nowfloats.Analytics_Screen.API.NfxFacebbokAnalytics;
import com.nowfloats.Analytics_Screen.model.NfxGetTokensResponse;
import com.nowfloats.BusinessProfile.UI.API.Facebook_Auto_Publish_API;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.GetAutoPull;
import com.nowfloats.PreSignUp.SplashScreen_Activity;
import com.nowfloats.Twitter.TwitterConstants;
import com.nowfloats.signup.UI.API.Retro_Signup_Interface;
import com.nowfloats.signup.UI.Model.Get_FP_Details_Event;
import com.nowfloats.signup.UI.Model.Get_FP_Details_Model;
import com.nowfloats.signup.UI.Model.ProcessFPDetails;
import com.nowfloats.signup.UI.UI.WebSiteAddressActivity;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.squareup.otto.Bus;
import com.thinksity.R;

import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by NowFloatsDev on 25/05/2015.
 */
public class Get_FP_Details_Service {
    public Get_FP_Details_Service(final Activity activity, String fpID, String clientID,final Bus bus)
    {
        newNfxtokenDetails(activity,fpID,bus);
        autoPull(activity,fpID);
        HashMap<String,String> map = new HashMap<>();
        map.put("clientId",clientID);
        Retro_Signup_Interface getFPDetails = Constants.restAdapter.create(Retro_Signup_Interface.class);
        getFPDetails.post_getFPDetails(fpID,map,new Callback<Get_FP_Details_Model>() {
            @Override
            public void success(Get_FP_Details_Model get_fp_details_model, Response response) {
                if (get_fp_details_model!=null){
                    ProcessFPDetails.storeFPDetails(activity, get_fp_details_model);
                    bus.post(new Get_FP_Details_Event(get_fp_details_model));
                }else { activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Methods.showSnackBarNegative(activity, activity.getString(R.string.something_went_wrong_try_again));
                        if (WebSiteAddressActivity.pd != null) {
                            WebSiteAddressActivity.pd.dismiss();
                        }
                        if (SplashScreen_Activity.pd!=null)
                        {
                            SplashScreen_Activity.pd.dismiss();
                        }
                    }
                });}
            }
            @Override
            public void failure(RetrofitError error) {
                Log.d("",""+error.getMessage());
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Methods.showSnackBarNegative(activity, "Something went wrong! Please try again.");
                        if (WebSiteAddressActivity.pd != null) {
                            WebSiteAddressActivity.pd.dismiss();
                        }
                        if (SplashScreen_Activity.pd!=null)
                        {
                            bus.post(new Get_FP_Details_Event(new Get_FP_Details_Model()));
                            try {
                                SplashScreen_Activity.pd.dismiss();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }

                    }
                });
            }
        });
    }
    private void autoPull(Activity activity,String fpID){

        final UserSessionManager session = new UserSessionManager(activity.getApplicationContext(), activity);
        final SharedPreferences pref = activity.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        session.storeFPDetails(Key_Preferences.FB_PULL_PAGE_NAME, null);
        session.storeFPDetails(Key_Preferences.FB_PULL_ENABLED, "false");
        session.storeFPDetails(Key_Preferences.FB_PULL_COUNT, "0");
        Facebook_Auto_Publish_API.autoPullApi apis=Facebook_Auto_Publish_API.getAdapter();
        apis.getFacebookAutoPull(fpID, Constants.clientId, new Callback<GetAutoPull>() {
            @Override
            public void success(GetAutoPull obj, Response response) {
                if(obj==null || obj.toString().length()==0) return;

                    boolean autoPublish=obj.getAutoPublish();
                    String facebookPageName=obj.getFacebookPageName();
                    int count=obj.getCount();
                    pref.edit().putBoolean("FBFeedPullAutoPublish",autoPublish).apply();
                    session.storeFPDetails(Key_Preferences.FB_PULL_PAGE_NAME, facebookPageName);
                    session.storeFPDetails(Key_Preferences.FB_PULL_ENABLED, String.valueOf(autoPublish));
                    session.storeFPDetails(Key_Preferences.FB_PULL_COUNT, String.valueOf(count));
            }

            @Override
            public void failure(RetrofitError error) {
                Log.v("ggg",error+"");
            }
        });
    }
    private void newNfxtokenDetails(final Activity activity, String fpID, final Bus bus){
            NfxFacebbokAnalytics.nfxFacebookApis facebookApis=NfxFacebbokAnalytics.getAdapter();
            facebookApis.nfxGetSocialTokens(fpID, new Callback<NfxGetTokensResponse>() {
                @Override
                public void success(NfxGetTokensResponse nfxGetTokensResponse, Response response) {
                    if (nfxGetTokensResponse == null) return;
                    String message = nfxGetTokensResponse.getMessage();
                    if (message != null && message.equalsIgnoreCase("success")) {
                        Storedata(activity, nfxGetTokensResponse.getNFXAccessTokens());
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d("", "" + error.getMessage());
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Methods.showSnackBarNegative(activity, "Something went wrong! Please try again.");
                            if (WebSiteAddressActivity.pd != null) {
                                WebSiteAddressActivity.pd.dismiss();
                            }
                            if (SplashScreen_Activity.pd != null) {
                                bus.post(new Get_FP_Details_Event(new Get_FP_Details_Model()));
                                try {
                                    SplashScreen_Activity.pd.dismiss();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    });
                }
            });
    }
    private void Storedata(Activity activity,List<NfxGetTokensResponse.NFXAccessToken> nfxGetTokensList){
        UserSessionManager session = new UserSessionManager(activity.getApplicationContext(), activity);
        SharedPreferences pref = activity.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences twitterPref = activity.getSharedPreferences(TwitterConstants.PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("fbShareEnabled", false);
        session.storeFacebookName(null);
        editor.putString("fbAccessId", null);
        editor.putBoolean("fbPageShareEnabled", false);
        session.storeFacebookPage(null);
        editor.putString("fbPageAccessId", null);
        editor.putInt("fbStatus", 3);
        editor.putInt("fbPageStatus",3);
        editor.apply();
        SharedPreferences.Editor tPrefEditor = twitterPref.edit();
        tPrefEditor.putBoolean(TwitterConstants.PREF_KEY_TWITTER_LOGIN, false);
        tPrefEditor.putString(TwitterConstants.PREF_USER_NAME, null);
        tPrefEditor.apply();
        for(NfxGetTokensResponse.NFXAccessToken model: nfxGetTokensList){
            if(model.getType().equalsIgnoreCase("facebookusertimeline")) {
                SharedPreferences.Editor editorFb = pref.edit();
                session.storeFacebookName(model.getUserAccountName());
                editorFb.putInt("fbStatus", Integer.parseInt(model.getStatus()));
                if(!Util.isNullOrEmpty(session.getFacebookName())) {
                    editorFb.putBoolean("fbShareEnabled", true);
                }
                editorFb.putString("fbAccessId", model.getUserAccessTokenKey());
                editorFb.apply();
            }else if(model.getType().equalsIgnoreCase("facebookpage")) {
                SharedPreferences.Editor editorFbPage = pref.edit();
                if(!"null".equalsIgnoreCase(model.getStatus()))
                editorFbPage.putInt("fbPageStatus",Integer.parseInt(model.getStatus()));
                session.storeFacebookPage(model.getUserAccountName());
                if(!Util.isNullOrEmpty(session.getFacebookPage())){
                    editorFbPage.putBoolean("fbPageShareEnabled", true);
                }
                editorFbPage.putString("fbPageAccessId", model.getUserAccessTokenKey());
                editorFbPage.apply();
            }else if(model.getType().equalsIgnoreCase("twitter")) {
                SharedPreferences.Editor twitterPrefEditor = twitterPref.edit();
                twitterPrefEditor.putBoolean(TwitterConstants.PREF_KEY_TWITTER_LOGIN, true);
                twitterPrefEditor.putString(TwitterConstants.PREF_USER_NAME, model.getUserAccountName());
                twitterPrefEditor.apply();
            }
        }
    }
}
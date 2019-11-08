package com.nowfloats.NavigationDrawer.API.twitter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nowfloats.BusinessProfile.UI.Model.FacebookFeedPullModel;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.thinksity.R;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;

public class FacebookFeedPullRegistrationAsyncTask {

    private SharedPreferences pref = null;
    private Activity appContext = null;
    ProgressDialog pd = null;
    TextView fromPage;
    CheckBox checkBox;
    UserSessionManager sessionManager;
    ImageView ivFbPageAutoPull;


    public FacebookFeedPullRegistrationAsyncTask(Activity context, TextView FromPage, ImageView ivFbPageAutoPull, CheckBox Checkbox, UserSessionManager sessionManager) {
        this.appContext = context;
        this.fromPage = FromPage;
        this.checkBox = Checkbox;
        this.sessionManager = sessionManager;
        this.ivFbPageAutoPull = ivFbPageAutoPull;
    }
    public void autoRegister(final FacebookFeedPullModel.Registration obj) {
        pd = ProgressDialog.show(appContext, null, "Please wait");
        pd.setCancelable(true);
        pd.show();
        pref = appContext.getSharedPreferences(Constants.PREF_NAME, Activity.MODE_PRIVATE);
        RestAdapter adapter = new RestAdapter.Builder()/*
                .setLog(new AndroidLog("ggg"))
                .setLogLevel(RestAdapter.LogLevel.FULL)*/
                .setEndpoint(Constants.NOW_FLOATS_API_URL)
                .build();
        pullRegistration reg = adapter.create(pullRegistration.class);
        reg.autoRegistration(obj, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                if (pd != null) {
                    pd.dismiss();
                }
                if (!Util.isNullOrEmpty(s)) {
                    sessionManager.storeFPDetails(Key_Preferences.FB_PULL_ENABLED, String.valueOf(obj.getAutoPublish()));
                    sessionManager.storeFPDetails(Key_Preferences.FB_PULL_PAGE_NAME, obj.getFacebookPageName());
                    sessionManager.storeFPDetails(Key_Preferences.FB_PULL_COUNT, String.valueOf(obj.getCount()));
                    fromPage.setVisibility(View.VISIBLE);
                    fromPage.setText(obj.getFacebookPageName());
                    ivFbPageAutoPull.setImageResource(R.drawable.facebook_page);
                    checkBox.setChecked(obj.getAutoPublish());
                    pref.edit().putBoolean("FBFeedPullAutoPublish", obj.getAutoPublish()).apply();
                }else{
                    checkBox.setChecked(false);
                    pref.edit().putBoolean("FBFeedPullAutoPublish", false).apply();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.v("ggg", error + "");
                if (pd != null) {
                    pd.dismiss();
                }
                checkBox.setChecked(false);
                pref.edit().putBoolean("FBFeedPullAutoPublish", false).apply();
                Util.toast("Uh oh. Something went wrong. Please try again", appContext);
            }
        });
    }
    public void autoUpdate(final FacebookFeedPullModel.Update obj){
        pd = ProgressDialog.show(appContext, null, "Please wait");
        pd.setCancelable(true);
        pd.show();
        pref = appContext.getSharedPreferences(Constants.PREF_NAME,Activity.MODE_PRIVATE);
        RestAdapter adapter = new RestAdapter.Builder()/*
                .setLog(new AndroidLog("ggg"))
                .setLogLevel(RestAdapter.LogLevel.FULL)*/
                .setEndpoint(Constants.NOW_FLOATS_API_URL)
                .build();
        pullRegistration reg= adapter.create(pullRegistration.class);
        reg.autoUpdate(obj, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                if(pd!=null){
                    pd.dismiss();
                }
                if(!Util.isNullOrEmpty(s)) {
                    sessionManager.storeFPDetails(Key_Preferences.FB_PULL_ENABLED, String.valueOf(obj.getAutoPublish()));
                    sessionManager.storeFPDetails(Key_Preferences.FB_PULL_PAGE_NAME, obj.getFacebookPageName());

                    if(!obj.getAutoPublish()){
                        fromPage.setVisibility(View.GONE);
                        Toast.makeText(appContext, "Auto Pull for Updates will be turned OFF", Toast.LENGTH_SHORT).show();
                        ivFbPageAutoPull.setImageDrawable(ContextCompat.getDrawable(appContext, R.drawable.facebookpage_icon_inactive));
                        ivFbPageAutoPull.setColorFilter(ContextCompat.getColor(appContext, R.color.light_gray));
                    }
                    else{
                        fromPage.setVisibility(View.VISIBLE);
                        fromPage.setText(obj.getFacebookPageName());
                        Toast.makeText(appContext, "Auto Pull for Updates will be turned ON", Toast.LENGTH_SHORT).show();
                        ivFbPageAutoPull.setImageDrawable(ContextCompat.getDrawable(appContext, R.drawable.facebook_page));
                        ivFbPageAutoPull.setColorFilter(ContextCompat.getColor(appContext, R.color.primaryColor));
                    }

                    pref.edit().putBoolean("FBFeedPullAutoPublish", obj.getAutoPublish()).apply();
                    checkBox.setChecked(obj.getAutoPublish());
                }else{
                    checkBox.setChecked(!obj.getAutoPublish());
                    pref.edit().putBoolean("FBFeedPullAutoPublish", !obj.getAutoPublish()).apply();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.v("ggg",error+"");
                if(pd!=null){
                    pd.dismiss();
                }
                checkBox.setChecked(!obj.getAutoPublish());
                pref.edit().putBoolean("FBFeedPullAutoPublish", !obj.getAutoPublish()).apply();
                Util.toast("Uh oh. Something went wrong. Please try again", appContext);
            }
        });

    }

    interface pullRegistration{
        @POST("/Discover/v1/FloatingPoint/AutoPublishMessages")
        void autoRegistration(@Body FacebookFeedPullModel.Registration obj, Callback<String> response);

        @POST("/Discover/v1/FloatingPoint/UpdateFacebookPullRegistration/")
        void autoUpdate(@Body FacebookFeedPullModel.Update obj, Callback<String> response);
    }

}

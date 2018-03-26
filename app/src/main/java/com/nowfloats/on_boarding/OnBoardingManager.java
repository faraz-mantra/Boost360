package com.nowfloats.on_boarding;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.manageinventory.models.WebActionModel;
import com.nowfloats.on_boarding.models.OnBoardingDataModel;
import com.nowfloats.on_boarding.models.OnBoardingModel;
import com.nowfloats.on_boarding.models.OnBoardingStepsModel;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.nowfloats.NavigationDrawer.SiteMeter.Site_Meter_Fragment.businessAddressWeight;
import static com.nowfloats.NavigationDrawer.SiteMeter.Site_Meter_Fragment.businessCategoryWeight;
import static com.nowfloats.NavigationDrawer.SiteMeter.Site_Meter_Fragment.businessDescriptionWeight;
import static com.nowfloats.NavigationDrawer.SiteMeter.Site_Meter_Fragment.businessHoursWeight;
import static com.nowfloats.NavigationDrawer.SiteMeter.Site_Meter_Fragment.businessNameWeight;
import static com.nowfloats.NavigationDrawer.SiteMeter.Site_Meter_Fragment.emailWeight;
import static com.nowfloats.NavigationDrawer.SiteMeter.Site_Meter_Fragment.featuredImageWeight;
import static com.nowfloats.NavigationDrawer.SiteMeter.Site_Meter_Fragment.logoWeight;
import static com.nowfloats.NavigationDrawer.SiteMeter.Site_Meter_Fragment.phoneWeight;
import static com.nowfloats.NavigationDrawer.SiteMeter.Site_Meter_Fragment.twitterWeight;
import static com.nowfloats.on_boarding.OnBoardingScreenApis.PRODUCTS;
import static com.nowfloats.on_boarding.OnBoardingScreenApis.UPDATES;

/**
 * Created by Admin on 16-03-2018.
 */

public class OnBoardingManager implements OnBoardingCallback {

    private Context mContext;
    private int siteMeterTotalWeight = 0;
    private SharedPreferences sharedPreferences;
    public OnBoardingManager(Context context){
        mContext = context;
        sharedPreferences = mContext.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }

    public void startOnBoarding(OnBoardingModel model){
        Intent  i =new Intent(mContext, OnBoardingActivity.class);
        i.putExtra("data",model);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(i);
    }

    public int siteMeterCalculation(UserSessionManager session, SharedPreferences pref) {
//        OnBoardingScreenApis apis = new OnBoardingScreenApis(mContext,session.getFpTag(), session.getFPID());
//        apis.setCallbackListener(this);
//        apis.startProcess();
        if (!Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI))) {
                    siteMeterTotalWeight += 10;
        }

        if (!Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER)) && !mContext.getResources().getString(R.string.phoneNumber_percentage).equals("0")) {
            siteMeterTotalWeight += phoneWeight;

        }

        if (!Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY)) && !mContext.getResources().getString(R.string.businessCategory_percentage).equals("0")) {
            siteMeterTotalWeight += businessCategoryWeight;

        }
        if (!Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_IMAGE_URI)) && !mContext.getResources().getString(R.string.featuredImage_percentage).equals("0")) {
            siteMeterTotalWeight += featuredImageWeight;
        }

        if (!Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME)) && !mContext.getResources().getString(R.string.businessName_percentage).equals("0")) {
            siteMeterTotalWeight += businessNameWeight;

        }

        if (!Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_DESCRIPTION)) && !mContext.getResources().getString(R.string.businessdescription_percentage).equals("0")) {
            siteMeterTotalWeight += businessDescriptionWeight;

        }
        if (Constants.twitterShareEnabled && pref.getBoolean("fbShareEnabled", false) && !TextUtils.isEmpty(session.getFacebookPageID())) {
            siteMeterTotalWeight += twitterWeight;
        }
        if (!Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS)) &&
                !Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.LATITUDE)) &&
                !Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.LONGITUDE)) &&
                !mContext.getResources().getString(R.string.address_percentage).equals("0")) {
            siteMeterTotalWeight += businessAddressWeight;
        }
        if (!Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL)) && !mContext.getResources().getString(R.string.email_percentage).equals("0")) {
            siteMeterTotalWeight += emailWeight;
        }

        if (!(Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_LogoUrl))) && !mContext.getResources().getString(R.string.Logo_percentage).equals("0")) {
            siteMeterTotalWeight += logoWeight;
        }

        if (session.getBusinessHours()) {
            siteMeterTotalWeight += businessHoursWeight;
        }

        return siteMeterTotalWeight;
    }


    public void getOnBoardingData(final String fptag){
        OnBoardingWebActionApis apis = Constants.webActionAdapter.create(OnBoardingWebActionApis.class);
        apis.getData(String.format("{fptag:'%s'}", fptag), new Callback<WebActionModel<OnBoardingDataModel>>() {
            @Override
            public void success(WebActionModel<OnBoardingDataModel> model, Response response) {
                OnBoardingStepsModel stepsModel;
                if (model != null && model.getData() != null){
                    OnBoardingModel onBoardingModel = new OnBoardingModel();
                    ArrayList<OnBoardingModel.ScreenData> screenDataArrayList = new ArrayList<>(6);
                    if (model.getData().size()>0){
                        stepsModel = model.getData().get(0);
                    }else{
                        stepsModel = new OnBoardingStepsModel();
                        stepsModel.setFptag(fptag);
                        stepsModel.setSiteHealth(sharedPreferences.getInt(Key_Preferences.SITE_HEALTH,0));
                        stepsModel.setAddProduct(sharedPreferences.getInt(Key_Preferences.PRODUCTS_COUNT,0) > 0);
                        stepsModel.setCustomPage(sharedPreferences.getInt(Key_Preferences.CUSTOM_PAGE,0) > 0);
                        stepsModel.setShareWebsite(sharedPreferences.getBoolean(Key_Preferences.WEBSITE_SHARE,false));
                        stepsModel.setBoostApp(true);
                        stepsModel.setComplete(false);
                        OnBoardingApiCalls.addData(stepsModel);
                    }

                    sharedPreferences.edit().putBoolean(Key_Preferences.ON_BOARDING_STATUS,stepsModel.isComplete()).apply();
                    if (stepsModel.isComplete()){
                        return;
                    }
                    for (int i = 0; i< 6; i++) {
                        OnBoardingModel.ScreenData data = new OnBoardingModel.ScreenData();
                        switch (i){
                            case 0:
                                data.setIsComplete(stepsModel.getWelcomeAboard());
                                break;
                            case 1:
                                data.setIsComplete(stepsModel.getSiteHealth()>=80);
                                data.setValue(String.valueOf(stepsModel.getSiteHealth()));
                                break;
                            case 2:
                                data.setIsComplete(stepsModel.getCustomPage());
                                break;
                            case 3:
                                data.setIsComplete(stepsModel.getAddProduct());
                                break;
                            case 4:
                                if (!stepsModel.getBoostApp()) {
                                    OnBoardingApiCalls.updateData(fptag,"");
                                }
                                data.setIsComplete(true);
                                break;
                            case 5:
                                data.setIsComplete(stepsModel.getShareWebsite());
                                break;
                        }
                        screenDataArrayList.add(data);
                        if (!data.isComplete() && onBoardingModel.getToBeCompletePos() == -1){
                            onBoardingModel.setToBeCompletePos(i);
                        }
                    }

                    onBoardingModel.setScreenDataArrayList(screenDataArrayList);
                    startOnBoarding(onBoardingModel);
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }


    @Override
    public void onBoardingCall(HashMap<String, Integer> map) {
        if (map != null){
            ArrayList<Boolean> arrayList = new ArrayList<>();
            if (map.containsKey(UPDATES)){
                siteMeterTotalWeight += map.get(UPDATES);
            }
            if (map.containsKey(PRODUCTS)){
                map.get(PRODUCTS);
            }
        }
    }
}

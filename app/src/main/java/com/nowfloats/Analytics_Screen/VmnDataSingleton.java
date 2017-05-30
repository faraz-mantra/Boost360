package com.nowfloats.Analytics_Screen;

import android.content.Context;

import com.nowfloats.Analytics_Screen.API.CallTrackerApis;
import com.nowfloats.Analytics_Screen.model.VmnCallModel;
import com.nowfloats.util.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Admin on 30-05-2017.
 */

public class VmnDataSingleton {

    private static LoadVmnData mLoadVmnData;
    private VmnDataSingleton(){

    }
    public static LoadVmnData getInstance(){
        if(mLoadVmnData == null) {
            mLoadVmnData = new LoadVmnData();
        }
        return mLoadVmnData;
    }

    public static class LoadVmnData{
        private ArrayList<VmnCallModel> vmnData = null;
        private boolean isDataLoaded = false;
        LoadVmnData(){

        }

         void requestVmnData(final Context context, String fpId) {
             if (!isDataLoaded) {
                 String endDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
                 Map<String, String> hashMap = new HashMap<>();
                 hashMap.put("clientId", Constants.clientId);
                 hashMap.put("fpid", fpId);
                 hashMap.put("offset", "0");
                 hashMap.put("startDate", "2011-01-01");
                 hashMap.put("endDate", endDate);

                 CallTrackerApis trackerApis = Constants.restAdapter.create(CallTrackerApis.class);
                 trackerApis.getLastCallInfo(hashMap, new Callback<ArrayList<VmnCallModel>>() {
                     @Override
                     public void success(ArrayList<VmnCallModel> vmnCallModels, Response response) {
                         vmnData = vmnCallModels;
                         isDataLoaded = true;
                         ((ConnectToVmnData) context).onDataLoaded(vmnData);
                     }

                     @Override
                     public void failure(RetrofitError error) {

                         ((ConnectToVmnData) context).onDataLoaded(vmnData);
                     }
                 });
             }else {
                 ((ConnectToVmnData) context).onDataLoaded(vmnData);
             }

        }

        public void setVmnDataNull(){
            mLoadVmnData = null;
        }
    }

    interface ConnectToVmnData{

        void onDataLoaded(ArrayList<VmnCallModel> vmnData);
    }
}

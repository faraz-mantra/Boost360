package com.nowfloats.Store.Service;

import android.app.Activity;
import android.util.Log;

import com.nowfloats.Store.Model.StoreEvent;
import com.nowfloats.Store.Model.StoreMainModel;
import com.nowfloats.Store.StoreFragmentTab;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.squareup.otto.Bus;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * Created by guru on 29-04-2015.
 */
public class API_Service {
    public API_Service(final Activity activity, String id, String country, String acc_id
                        ,String fpID, final Bus bus){
        try {
            StoreFragmentTab.activeWidgetModels = new ArrayList<>();
            StoreFragmentTab.additionalWidgetModels = new ArrayList<>();
            Map<String, String> params = new HashMap<String, String>();
            if (acc_id.length()>0){
                params.put("AccountManagerId",acc_id);
                params.put("identifier", acc_id);
            }else{
                params.put("AccountManagerId",id);
                params.put("identifier", id);
            }
            params.put("clientId", Constants.clientId);
            params.put("fpId", fpID);
            params.put("country",country.toLowerCase());
            //Log.i("STORE data", "API call Started");
            StoreInterface storeInterface = Constants.restAdapter.create(StoreInterface.class);
            storeInterface.getStoreList(params,new Callback<StoreMainModel>() {
                @Override
                public void success(StoreMainModel data, Response response) {
                    //Log.d("TestForStoreModel",""+response);
                    bus.post(new StoreEvent(data));
                }

                @Override
                public void failure(RetrofitError error) {
                    Methods.showSnackBarNegative(activity,activity.getString(R.string.something_went_wrong));
                    //Log.i("store list Error",""+error);

                }
            });

        } catch (Exception e) {
            Log.i("STORE data","API Exception:"+e);
            e.printStackTrace();
        }
    }
}
package com.nowfloats.Store.Service;

import android.app.Activity;
import android.util.Log;

import com.nowfloats.Store.Model.PricingPlansModel;
import com.nowfloats.util.Constants;
import com.squareup.otto.Bus;

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
                        ,String fpID, String category, final Bus bus){
        try {
            Map<String, String> params = new HashMap<String, String>();
            if (acc_id.length()>0){
                params.put("identifier", acc_id);
            }else{
                params.put("identifier", id);
            }
            params.put("clientId", Constants.clientId);
            params.put("fpId", fpID);
            params.put("country",country.toLowerCase());
            params.put("fpCategory",category);
            //Log.i("STORE data", "API call Started");
            Constants.restAdapter.create(StoreInterface.class).getStoreList(params, new Callback<PricingPlansModel>() {
                @Override
                public void success(PricingPlansModel storeMainModel, Response response) {
                    if(storeMainModel != null){
                        bus.post(storeMainModel);
                    }
                }

                @Override
                public void failure(RetrofitError error)
                {
                    bus.post(new PricingPlansModel());
                    Log.d("Test", error.getMessage());
                }
            });

        } catch (Exception e) {
            Log.i("STORE data","API Exception:"+e);
            e.printStackTrace();
        }
    }
}
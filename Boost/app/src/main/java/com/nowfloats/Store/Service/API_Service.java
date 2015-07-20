package com.nowfloats.Store.Service;

import android.app.Activity;
import android.util.Log;

import com.nowfloats.Store.Model.StoreEvent;
import com.nowfloats.Store.Model.StoreModel;
import com.nowfloats.util.Constants;
import com.squareup.otto.Bus;

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

    public API_Service(final Activity activity, String id, String country, final Bus bus){
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("identifier", id);
            params.put("clientId", id);
            params.put("country",country.toLowerCase());
            Log.i("STORE data", "API call Started");
            StoreInterface storeInterface = Constants.restAdapter.create(StoreInterface.class);
            storeInterface.getStoreList(params,new Callback<ArrayList<StoreModel>>() {
                @Override
                public void success(ArrayList<StoreModel> storeModels, Response response) {
                    bus.post(new StoreEvent(storeModels));
                }

                @Override
                public void failure(RetrofitError error) {
                    bus.post(new StoreEvent(new ArrayList<StoreModel>()));
                    Log.i("store list Error",""+error);
                }
            });

        } catch (Exception e) {
            Log.i("STORE data","API Exception:"+e);
            e.printStackTrace();
        }
    }
}
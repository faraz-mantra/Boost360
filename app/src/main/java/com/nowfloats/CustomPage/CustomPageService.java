package com.nowfloats.CustomPage;

import android.util.Log;

import com.nowfloats.CustomPage.Model.CustomPageEvent;
import com.nowfloats.CustomPage.Model.CustomPageModel;
import com.squareup.otto.Bus;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by guru on 25/08/2015.
 */
public class CustomPageService {



    public void GetPages(String tag, String id,
                         CustomPageInterface anInterface, final Bus bus){
        try {
            Log.i("CustomPAges data", "API call Started");
            anInterface.getPageList(tag, id, new Callback<ArrayList<CustomPageModel>>() {
                @Override
                public void success(ArrayList<CustomPageModel> data, Response response) {
                    bus.post(new CustomPageEvent(data));
                }

                @Override
                public void failure(RetrofitError error) {
                    bus.post(new CustomPageEvent(new ArrayList<CustomPageModel>()));
                    Log.i("CustomPages list Error", "" + error);
                }
            });
        } catch (Exception e) {
            Log.i("CutomPages data","API Exception:"+e);
            e.printStackTrace();
        }
    }
}
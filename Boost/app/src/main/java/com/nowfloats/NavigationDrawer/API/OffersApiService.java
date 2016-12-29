package com.nowfloats.NavigationDrawer.API;

import com.nowfloats.NavigationDrawer.model.OfferModel;
import com.nowfloats.NavigationDrawer.model.PostOfferEvent;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.squareup.otto.Bus;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by NowFloats on 04-11-2016.
 */

public class OffersApiService {
    Bus mBus;
    public OffersApiService(Bus bus){
        this.mBus = bus;
    }
    public void postOffers(HashMap<String, String> data){
        OffersInterface offersInterface = Constants.restAdapter.create(OffersInterface.class);
        offersInterface.postOffer(data, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                if(s!=null) {
                    mBus.post(new PostOfferEvent(true, s));
                }
            }

            @Override
            public void failure(RetrofitError error) {
                mBus.post(new PostOfferEvent(false, error.getMessage()));
            }
        });
    }

    public void getAllOffers(HashMap<String, String> data){
        OffersInterface offersInterfac = Constants.restAdapter.create(OffersInterface.class);
        offersInterfac.getAllOffers(data, new Callback<OfferModel>() {
            @Override
            public void success(OfferModel offerModel, Response response) {
                if(offerModel!=null) {
                    offerModel.response = true;
                    mBus.post(offerModel);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                BoostLog.d("OffersApiService", error.getMessage());
                OfferModel offerModel = new OfferModel();
                offerModel.response = false;
                mBus.post(offerModel);
            }
        });

    }
}

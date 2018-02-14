package com.nowfloats.Store;

import android.util.Log;

import com.nowfloats.Store.Model.AllPackage;
import com.nowfloats.Store.Model.PackageDetails;
import com.nowfloats.Store.Model.PricingPlansModel;
import com.nowfloats.Store.Service.StoreInterface;
import com.nowfloats.util.Constants;

import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Admin on 14-02-2018.
 */

public class TopUpPlansService {

    private ServiceCallbackListener listener;
    private TopUpPlansService(ServiceCallbackListener listener){
        this.listener = listener;
    }

    public static TopUpPlansService getTopUpService(ServiceCallbackListener listener){
        return new TopUpPlansService(listener);
    }

    public void getTopUpPackages(Map<String,String> map){
        listener.showDialog();
        Constants.restAdapter.create(StoreInterface.class).getStoreList(map, new Callback<PricingPlansModel>() {
            @Override
            public void success(PricingPlansModel storeMainModel, Response response) {
                listener.hideDialog();
                if(storeMainModel != null){
                    preProcessAndDispatchPlans(storeMainModel);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                listener.hideDialog();
                Log.d("Test", error.getMessage());
            }
        });
    }

    private void preProcessAndDispatchPlans(final PricingPlansModel storeMainModel){

        for (AllPackage allPackage : storeMainModel.allPackages) {
            if (allPackage.getKey().equals("TopUp")) {
                listener.onDataReceived(allPackage.getValue());
            }
        }
    }

    interface ServiceCallbackListener{
        void onDataReceived(List<PackageDetails> packages);
        void showDialog();
        void hideDialog();
    }
}

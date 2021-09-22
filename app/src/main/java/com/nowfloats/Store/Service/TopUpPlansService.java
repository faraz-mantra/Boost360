package com.nowfloats.Store.Service;

import android.util.Log;

import com.nowfloats.Store.Model.AllPackage;
import com.nowfloats.Store.Model.PackageDetails;
import com.nowfloats.Store.Model.PricingPlansModel;
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

    private TopUpPlansService(ServiceCallbackListener listener) {
        this.listener = listener;
    }

    public static TopUpPlansService getTopUpService(ServiceCallbackListener listener) {
        return new TopUpPlansService(listener);
    }

    public void getTopUpPackages(Map<String, String> map) {
        listener.startApiCall();
        Constants.restAdapter.create(StoreInterface.class).getStoreList(map, new Callback<PricingPlansModel>() {
            @Override
            public void success(PricingPlansModel storeMainModel, Response response) {
                listener.endApiCall();
                if (storeMainModel != null) {
                    findTopUpCalls(storeMainModel);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                listener.endApiCall();
                Log.d("Test", error.getMessage());
            }
        });
    }

    private void findTopUpCalls(final PricingPlansModel storeMainModel) {

        for (AllPackage allPackage : storeMainModel.allPackages) {
            if (allPackage.getKey().equals("TopUp")) {
                listener.onDataReceived(allPackage.getValue());
                return;
            }
        }
        listener.onDataReceived(null);
    }

    public interface ServiceCallbackListener {
        void onDataReceived(List<PackageDetails> packages);

        void startApiCall();

        void endApiCall();
    }

}

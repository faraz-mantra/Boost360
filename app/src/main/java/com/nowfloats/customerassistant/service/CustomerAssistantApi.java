package com.nowfloats.customerassistant.service;

import android.util.Log;

import com.nowfloats.customerassistant.FirebaseLogger;
import com.nowfloats.customerassistant.models.MessageDO;
import com.nowfloats.customerassistant.models.SMSSuggestions;
import com.nowfloats.util.Constants;
import com.nowfloats.util.MixPanelController;
import com.squareup.otto.Bus;

import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by NowFloats on 04-11-2016.
 */

public class CustomerAssistantApi {
    Bus mBus;

    public CustomerAssistantApi(Bus bus) {
        this.mBus = bus;
    }

    public void getMessages(HashMap<String, String> data, final String fpId, final String appVersion) {
        CustomerAssistantInterface suggestionsInterface = Constants.pluginSuggestionsAdapter.create(CustomerAssistantInterface.class);
        suggestionsInterface.processSMSData(data, new Callback<SMSSuggestions>() {
            @Override
            public void success(SMSSuggestions suggestions, Response response) {
                mBus.post(suggestions);
            }

            @Override
            public void failure(RetrofitError error) {
                MixPanelController.track(MixPanelController.SAM_BUBBLE_CLICKED_SERVER_ERROR, null);
                FirebaseLogger.getInstance().logSAMEvent("", FirebaseLogger.SAMSTATUS.SERVER_ERROR,
                        fpId, appVersion);
                mBus.post(new SMSSuggestions());
            }
        });

    }

    public void updateMessage(List<MessageDO> data) {
        CustomerAssistantInterface suggestionsInterface = Constants.pluginSuggestionsAdapter.create(CustomerAssistantInterface.class);
        suggestionsInterface.saveCallData(data, new Callback<String>() {
            @Override
            public void success(String suggestions, Response response) {
//                mBus.post(suggestions);
                Log.e("suggestions", "suggestions=" + suggestions);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("suggestions", "suggestions=" + error.toString());
            }
        });

    }

    public void updateRating(HashMap<String, String> data) {
        CustomerAssistantInterface suggestionsInterface = Constants.pluginSuggestionsAdapter.create(CustomerAssistantInterface.class);
        suggestionsInterface.updateRating(data, new Callback<String>() {
            @Override
            public void success(String suggestions, Response response) {
                Log.e("suggestions", "suggestions=" + suggestions);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("suggestions", "suggestions=" + error.toString());
            }
        });

    }
}

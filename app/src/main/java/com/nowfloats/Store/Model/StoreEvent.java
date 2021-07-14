package com.nowfloats.Store.Model;

import android.util.Log;

/**
 * Created by guru on 04-05-2015.
 */
public class StoreEvent {
    public StoreMainModel model;

    public StoreEvent(StoreMainModel response) {
        this.model = response;
        Log.i("STORE ", "api value loaded to the event");
    }
}

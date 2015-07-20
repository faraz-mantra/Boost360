package com.nowfloats.Store.Model;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by guru on 04-05-2015.
 */
public class StoreEvent {
   public ArrayList<StoreModel> model = new ArrayList<>();
    public StoreEvent(ArrayList<StoreModel> response){
        this.model = response;
        Log.i("STORE ","api value loaded to the event");
    }
}

package com.nowfloats.signup.UI.Model;

import android.util.Log;

/**
 * Created by NowFloatsDev on 25/05/2015.
 */
public class Get_FP_Details_Event {
    public Get_FP_Details_Model model;

    public Get_FP_Details_Event(Get_FP_Details_Model response) {
        this.model = response;
        Log.i("Get_FP_Details ", "api value loaded to the event");
    }
}

package com.nowfloats.on_boarding;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Admin on 16-03-2018.
 */

public class OnBoardingManager {

    private Context mContext;
    public OnBoardingManager(Context context){
        mContext = context;
    }

    public void startOnBoarding(){
        mContext.startActivity(new Intent(mContext, OnBoardingActivity.class));
    }
}

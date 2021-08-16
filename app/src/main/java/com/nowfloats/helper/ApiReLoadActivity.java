package com.nowfloats.helper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.appservice.AppServiceApplication;
import com.boost.presignin.AppPreSignInApplication;
import com.dashboard.AppDashboardApplication;
import com.inventoryorder.BaseOrderApplication;
import com.onboarding.nowfloats.BaseBoardingApplication;

public class ApiReLoadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            BaseOrderApplication.apiInitialize();
            BaseBoardingApplication.apiInitialize();
            AppServiceApplication.apiInitialize();
            AppDashboardApplication.apiInitialize();
            AppPreSignInApplication.apiInitialize();
        }catch (Exception e){
            e.printStackTrace();
        }
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_OK, new Intent());
        finish();
    }
}
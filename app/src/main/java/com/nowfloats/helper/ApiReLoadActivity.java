package com.nowfloats.helper;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsetsController;

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
    setLightStatusBar(this);
    BaseOrderApplication.apiInitialize();
    BaseBoardingApplication.apiInitialize();
    AppServiceApplication.apiInitialize();
    AppDashboardApplication.apiInitialize();
    AppPreSignInApplication.apiInitialize();
    onBackPressed();
  }

  @Override
  public void onBackPressed() {
    setResult(Activity.RESULT_OK, new Intent());
    finish();
  }

  private void setLightStatusBar(Activity activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      activity.getWindow().getInsetsController().setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
    } else {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
      } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
      }
    }
  }
}
package com.nowfloats.helper;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nowfloats.Login.UserSessionManager;

public class LogoutActivity extends AppCompatActivity {

    private Boolean isShowToast = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("IsShowToast")) {
            isShowToast = bundle.getBoolean("IsShowToast", false);
        }
        if (isShowToast)
            Toast.makeText(this, "Unauthorized user, please login again.", Toast.LENGTH_SHORT).show();
        UserSessionManager sessionManager = new UserSessionManager(this, this);
        sessionManager.logoutUser();
    }
}
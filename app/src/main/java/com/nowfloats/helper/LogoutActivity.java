package com.nowfloats.helper;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nowfloats.Login.UserSessionManager;

public class LogoutActivity extends AppCompatActivity {

    private Boolean isAuthErrorToast = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("isAuthErrorToast")) {
            isAuthErrorToast = bundle.getBoolean("isAuthErrorToast", false);
        }
        if (isAuthErrorToast) Toast.makeText(this, "Unauthorized user, please login again!", Toast.LENGTH_SHORT).show();
        UserSessionManager sessionManager = new UserSessionManager(this, this);
        sessionManager.logoutUser();
    }
}
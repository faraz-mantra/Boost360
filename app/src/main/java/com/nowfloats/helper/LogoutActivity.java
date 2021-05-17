package com.nowfloats.helper;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nowfloats.Login.UserSessionManager;

public class LogoutActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Toast.makeText(this, "Unauthorized user, please login again.", Toast.LENGTH_SHORT).show();
    UserSessionManager sessionManager = new UserSessionManager(this, this);
    sessionManager.logoutUser();
  }
}
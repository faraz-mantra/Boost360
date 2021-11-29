package com.nowfloats.helper;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.invitereferrals.invitereferrals.InviteReferralsApi;
import com.nowfloats.Login.UserSessionManager;
import com.thinksity.R;

import org.json.JSONException;

import static com.nowfloats.util.Constants.REFERRAL_CAMPAIGN_CODE;

public class ReferralTransActivity extends AppCompatActivity {

  private UserSessionManager session;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getPackageName().equalsIgnoreCase("com.jio.online")) {
      Toast.makeText(this, getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();
      this.finish();
      return;
    }
    session = new UserSessionManager(getApplicationContext(), ReferralTransActivity.this);
    String email = "";
    String number = "";
    String username = "";
    if (!TextUtils.isEmpty(session.getFPEmail())) email = session.getFPEmail();
    if (email.isEmpty()) email = session.getUserProfileEmail();

    if (!TextUtils.isEmpty(session.getFPPrimaryContactNumber())) number = session.getFPPrimaryContactNumber();
    if (number.isEmpty()) number = session.getUserPrimaryMobile();
    if (number.isEmpty()) number = session.getUserProfileMobile();
    if (!number.isEmpty() && number.length() > 10) number = number.substring(number.length() - 10, number.length());

    if (!TextUtils.isEmpty(session.getUserProfileName())) username = session.getUserProfileName();
    if (username.isEmpty()) username = session.getFPName();

    if (!email.isEmpty()) {
      Log.d("ReferralTransActivity", "Username: " + username + "Email: " + email + "Number: " + number);
      InviteReferralsApi.getInstance(getApplicationContext()).userDetails(username, email, number, REFERRAL_CAMPAIGN_CODE, null, null);
      inviteReferralLogin();
    } else {
      Toast.makeText(getApplicationContext(), R.string.an_unexpacted_error, Toast.LENGTH_LONG).show();
    }
    finish();
  }

  private void inviteReferralLogin() {
    InviteReferralsApi.getInstance(getApplicationContext()).userDetailListener(jsonObject -> {
      Log.d("Referral Details", jsonObject.toString());
      try {
        String status = jsonObject.get("Authentication").toString();
        if (status.equalsIgnoreCase("success")) {
          InviteReferralsApi.getInstance(getApplicationContext()).inline_btn(REFERRAL_CAMPAIGN_CODE);
        } else {
          Toast.makeText(getApplicationContext(), getString(R.string.auth_failed_try_again), Toast.LENGTH_SHORT).show();
        }
      } catch (JSONException e) {
        Toast.makeText(getApplicationContext(), getString(R.string.auth_failed_try_again), Toast.LENGTH_SHORT).show();
      }
    });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
  }
}
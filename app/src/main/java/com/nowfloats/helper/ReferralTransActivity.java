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
    UserSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new UserSessionManager(getApplicationContext(), ReferralTransActivity.this);
        if (!TextUtils.isEmpty(session.getFPEmail())) {
            InviteReferralsApi.getInstance(getApplicationContext()).userDetails(
                    session.getUserProfileName(),
                    session.getFPEmail(),
                    session.getUserPrimaryMobile(),
                    REFERRAL_CAMPAIGN_CODE, null, null
            );
            inviteReferralLogin();
        } else if (!TextUtils.isEmpty(session.getUserProfileEmail())) {
            InviteReferralsApi.getInstance(getApplicationContext()).userDetails(
                    session.getUserProfileName(),
                    session.getUserProfileEmail(),
                    session.getUserPrimaryMobile(),
                    REFERRAL_CAMPAIGN_CODE, null, null
            );
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
                if (status.toLowerCase().equals("success")) {
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
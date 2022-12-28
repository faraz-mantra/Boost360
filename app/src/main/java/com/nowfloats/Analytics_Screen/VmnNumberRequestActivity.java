package com.nowfloats.Analytics_Screen;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.framework.views.customViews.CustomToolbar;
import com.nowfloats.Analytics_Screen.API.CallTrackerApis;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Admin on 13-09-2017.
 */

@Deprecated
public class VmnNumberRequestActivity extends AppCompatActivity {

    SharedPreferences pref;
    TextView mButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_vmn);
        CustomToolbar toolbar = (CustomToolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        pref = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);

        setTitle("Call Tracker");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        UserSessionManager manager = new UserSessionManager(this, this);
        final String fpTag = manager.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG);
        ImageView imageView = (ImageView) findViewById(R.id.image1);
        TextView mMainText = (TextView) findViewById(R.id.main_text1);
        TextView mDescriptionText = (TextView) findViewById(R.id.message_text2);
        mButton = findViewById(R.id.btn_action);
        if (pref.getBoolean("Call_tracker_requested", false)) {
            mButton.setText("Requested");
            mButton.setBackgroundResource(R.drawable.gray_round_corner);
        } else {
            mButton.setText("Enable");
            mButton.setBackgroundResource(R.drawable.rounded_corner_button);
            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestVmn(fpTag);
                }
            });
        }
        imageView.setImageResource(R.drawable.icon_no_vmn_number);
        mMainText.setText("Call Tracking is not Enabled");
        mDescriptionText.setText("With call tracking you have a list of all your customer calls, " +
                "including any missed ones. You can also listen to call recordings to ensure that" +
                " you have not missed out on any details.");


    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void requestVmn(String fpTag) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("Subject", "Assign VMN to FPTAG: [" + fpTag + "]");
        hashMap.put("Mesg", "Please assign the customer with fpTag " + fpTag + " VMN, call tracker number.");
        CallTrackerApis apis = Constants.riaRestAdapter.create(CallTrackerApis.class);
        apis.requestVmn(hashMap, Constants.clientId, fpTag, new Callback<Boolean>() {
            @Override
            public void success(Boolean aBoolean, Response response) {
                if (aBoolean) {
                    pref.edit().putBoolean("Call_tracker_requested", true).apply();
                    mButton.setText("Requested");
                    mButton.setBackgroundResource(R.drawable.gray_round_corner);
                    Methods.showSnackBarPositive(VmnNumberRequestActivity.this, "Your request has been submitted successfully");
                } else {
                    Methods.showSnackBarNegative(VmnNumberRequestActivity.this, "Your request is not submitted");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Methods.showSnackBarNegative(VmnNumberRequestActivity.this, getString(R.string.something_went_wrong_try_again));
            }
        });
    }
}

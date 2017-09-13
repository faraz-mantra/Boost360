package com.nowfloats.Analytics_Screen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

public class VmnNumberRequestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_facebook_create_update);
        ImageView imageView = (ImageView) findViewById(R.id.create_update_img);
        TextView mMainText  = (TextView) findViewById(R.id.create_update_text_bold);
        TextView mDescriptionText  = (TextView) findViewById(R.id.message);
        Button mButton = (Button) findViewById(R.id.create_update_button);

        imageView.setImageResource(R.drawable.icon_no_vmn_number);
        mMainText.setText("Call Tracking is not Enabled");
        mDescriptionText.setText("With call tracking you have a list of all your customer calls, " +
                "including any missed ones. You can also listen to call recordings to ensure that" +
                " you have not missed out on any details.");
        mButton.setText("Enable");
        UserSessionManager manager = new UserSessionManager(this,this);
        final String fpTag = manager.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            requestVmn(fpTag);
            }
        });
    }


    public void requestVmn(String fpTag){
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("Subject", "Assign VMN to FPTAG: [" + fpTag + "]");
        hashMap.put("Mesg","Please assign the customer with fpTag "+ fpTag +" VMN, call tracker number.");
        CallTrackerApis apis = Constants.riaRestAdapter.create(CallTrackerApis.class);
        apis.requestVmn(hashMap,Constants.clientId,fpTag , new Callback<Boolean>() {
            @Override
            public void success(Boolean aBoolean, Response response) {
                if(aBoolean){
                    Methods.showSnackBarPositive(VmnNumberRequestActivity.this,"Your request has been submitted successfully");
                }else{
                    Methods.showSnackBarNegative(VmnNumberRequestActivity.this,"Your request is not submitted");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Methods.showSnackBarNegative(VmnNumberRequestActivity.this,getString(R.string.something_went_wrong_try_again));
            }
        });
    }
}

package com.nowfloats.Analytics_Screen;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nowfloats.Analytics_Screen.API.CallTrackerApis;
import com.nowfloats.Analytics_Screen.model.VmnCallModel;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Constants;
import com.thinksity.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Admin on 27-04-2017.
 */

public class VmnCallCardsActivity extends AppCompatActivity implements View.OnClickListener{

    ArrayList<VmnCallModel> missedCalls = new ArrayList<>();
    ArrayList<VmnCallModel> receivedCalls = new ArrayList<>();

    UserSessionManager sessionManager;
    ProgressBar mProgressBar;
    LinearLayout missedLayout,receivedLayout;
    TextView missedCount, receivedCount;
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vmn_call_cards);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            setTitle("Call Tracker");
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        sessionManager = new UserSessionManager(this,this);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_vmncall);
        missedLayout = (LinearLayout) findViewById(R.id.missed_call);
        receivedLayout = (LinearLayout) findViewById(R.id.received_calls);
        missedCount = (TextView) findViewById(R.id.missed_count);
        receivedCount = (TextView) findViewById(R.id.received_count);

        missedLayout.setOnClickListener(this);
        receivedLayout.setOnClickListener(this);

        getVmnCalls();
    }
    private void getVmnCalls(){
        mProgressBar.setVisibility(View.VISIBLE);
        String endDate =new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("clientId", Constants.clientId);
        hashMap.put("fpid",sessionManager.getFPID());
        hashMap.put("offset","0");
        hashMap.put("startDate","2011-01-01");
        hashMap.put("endDate",endDate);

        CallTrackerApis trackerApis = Constants.restAdapter.create(CallTrackerApis.class);
        trackerApis.getLastCallInfo(hashMap, new Callback<List<VmnCallModel>>() {
            @Override
            public void success(List<VmnCallModel> vmnCallModels, Response response) {
                mProgressBar.setVisibility(View.GONE);
                if(vmnCallModels == null || vmnCallModels.size() == 0|| response.getStatus() != 200)
                {
                    return;
                }

                for (VmnCallModel model : vmnCallModels){
                    if(model.getCallStatus().equalsIgnoreCase("MISSED")){
                        missedCalls.add(0,model);
                    }
                    else
                    {
                        receivedCalls.add(0,model);
                    }
                }
                missedCount.setText(String.valueOf(missedCalls.size()));
                receivedCount.setText(String.valueOf(receivedCalls.size()));
            }

            @Override
            public void failure(RetrofitError error) {
                Log.v("ggg",error.getMessage());
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(VmnCallCardsActivity.this,ShowVmnCallActivity.class);

        switch (v.getId()){
            case R.id.missed_call:
                i.putExtra("Calls",new Gson().toJson(missedCalls));
                i.putExtra("CallType","Missed Calls");
                break;
            case R.id.received_calls:
                i.putExtra("Calls",new Gson().toJson(receivedCalls));
                i.putExtra("CallType","Received Calls");
                break;
        }
        startActivity(i);
    }
}

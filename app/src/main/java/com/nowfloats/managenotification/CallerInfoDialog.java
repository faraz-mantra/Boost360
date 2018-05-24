package com.nowfloats.managenotification;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;

import com.nowfloats.Analytics_Screen.API.CallTrackerApis;
import com.nowfloats.Analytics_Screen.model.VmnCallModel;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Constants;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * Created by NowFloats on 4/12/2017.
 */

public class CallerInfoDialog extends AppCompatActivity {

    private UserSessionManager sessionManager;

    private CallerInfoAdapter missedCallAdapter, receivedCallAdapter;

    private ArrayList<VmnCallModel> headerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_caller_info);
        initialize();

//        setListAdapter();
//        getCalls();
    }

    private void initialize() {

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenHeight = (int) (metrics.heightPixels * 1);
        int screenWidth = (int) (metrics.widthPixels * 1);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setLayout(screenWidth, screenHeight);

        sessionManager = new UserSessionManager(this, this);
    }

//    private void setListAdapter() {
//
//        LinearLayoutManager missedLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        rvMissed.setLayoutManager(missedLayoutManager);
//        rvMissed.setHasFixedSize(true);
//        missedCallAdapter = new CallerInfoAdapter(CallerInfoDialog.this, headerList);
//        rvMissed.setAdapter(missedCallAdapter);
//
//        LinearLayoutManager receivedLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        rvReceived.setLayoutManager(receivedLayoutManager);
//        rvReceived.setHasFixedSize(true);
//        receivedCallAdapter = new CallerInfoAdapter(CallerInfoDialog.this, headerList);
//        rvReceived.setAdapter(receivedCallAdapter);
//
//    }

    private void getCalls() {
        final String startOffset = String.valueOf(10);
        CallTrackerApis trackerApis = Constants.restAdapter.create(CallTrackerApis.class);
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("clientId", Constants.clientId);
        hashMap.put("fpid", sessionManager.getFPID());
        hashMap.put("offset", startOffset);
        hashMap.put("identifierType", sessionManager.getISEnterprise().equals("true") ? "MULTI" : "SINGLE");
        trackerApis.trackerCalls(hashMap, new Callback<ArrayList<VmnCallModel>>() {
            @Override
            public void success(ArrayList<VmnCallModel> vmnCallModels, Response response) {
                if (vmnCallModels == null || response.getStatus() != 200) {
                    Toast.makeText(CallerInfoDialog.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    return;
                }
                saveWithViewType(vmnCallModels);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(CallerInfoDialog.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveWithViewType(ArrayList<VmnCallModel> list) {
        this.headerList.addAll(list);
        receivedCallAdapter.notifyItemInserted(list.size());
        missedCallAdapter.notifyItemInserted(list.size());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}

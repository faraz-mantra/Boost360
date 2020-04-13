package com.nowfloats.Analytics_Screen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.nowfloats.Analytics_Screen.API.CallTrackerApis;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.WebEngageController;
import com.thinksity.R;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Admin on 27-04-2017.
 */

public class VmnCallCardsActivity extends AppCompatActivity implements View.OnClickListener {

    UserSessionManager sessionManager;
    CardView viewCallLogCard;
    TextView missedCount, receivedCount,totalCount, virtualNumber;
    Toolbar toolbar;
    ProgressDialog vmnProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vmn_call_cards);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        MixPanelController.track(MixPanelController.VMN_CALL_TRACKER,null);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            setTitle("Business Calls");
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        WebEngageController.trackEvent("BUSINESS CALLS","BUSINESS CALLS",null);
        vmnProgressBar = new ProgressDialog(this);
        vmnProgressBar.setIndeterminate(true);
        vmnProgressBar.setMessage(getString(R.string.please_wait));
        vmnProgressBar.setCancelable(false);
        sessionManager = new UserSessionManager(this,this);
        missedCount = (TextView) findViewById(R.id.missed_count);
        receivedCount = (TextView) findViewById(R.id.received_count);
        totalCount = (TextView) findViewById(R.id.total_count);
        virtualNumber = (TextView) findViewById(R.id.tv_virtual_number);
        viewCallLogCard = (CardView) findViewById(R.id.card_view_view_calllog);
        virtualNumber.setText(sessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER));
        setVmnTotalCallCount();
    }

    private void showEmptyScreen(){
        if(totalCount.getText().toString().equals("0")){
            findViewById(R.id.empty_layout).setVisibility(View.VISIBLE);
            ImageView imageView = (ImageView) findViewById(R.id.image_item);
            TextView mMainText  = (TextView) findViewById(R.id.main_text);
            TextView mDescriptionText  = (TextView) findViewById(R.id.description_text);

            imageView.setImageResource(R.drawable.icon_no_calls);
            mMainText.setText("You have no call records yet.");
            mDescriptionText.setText("Your customers haven't contacted\nyou on your call tracking number yet.");
        }else {
            findViewById(R.id.calls_details_layout).setVisibility(View.VISIBLE);
            viewCallLogCard.setVisibility(View.VISIBLE);
            viewCallLogCard.setOnClickListener(this);
        }
    }
    private void showProgress(){
        if(!vmnProgressBar.isShowing() && !isFinishing()){
            vmnProgressBar.show();
        }
    }
    private void hideProgress(){
        if(vmnProgressBar.isShowing() && !isFinishing()){
            vmnProgressBar.dismiss();
        }
    }
    private void setVmnTotalCallCount() {
        showProgress();
        CallTrackerApis trackerApis = Constants.restAdapter.create(CallTrackerApis.class);
        String type = sessionManager.getISEnterprise().equals("true") ? "MULTI" : "SINGLE";

        trackerApis.getVmnSummary(Constants.clientId, sessionManager.getFPID(), type, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                hideProgress();

                if (jsonObject == null || response.getStatus() != 200) {
                    Methods.showSnackBarNegative(VmnCallCardsActivity.this,getString(R.string.something_went_wrong));

                }else
                {
                    if (jsonObject.has("TotalCalls")) {
                        String vmnTotalCalls = jsonObject.get("TotalCalls").getAsString();
                        totalCount.setText(vmnTotalCalls != null && !"null".equalsIgnoreCase(vmnTotalCalls) ? vmnTotalCalls : "0");
                    }
                    if (jsonObject.has("MissedCalls")) {
                        String vmnMissedCalls = jsonObject.get("MissedCalls").getAsString();
                        missedCount.setText(vmnMissedCalls != null && !"null".equalsIgnoreCase(vmnMissedCalls) ? vmnMissedCalls : "0");
                    }
                    if (jsonObject.has("ReceivedCalls")) {
                        String vmnReceivedCalls = jsonObject.get("ReceivedCalls").getAsString();
                        receivedCount.setText(vmnReceivedCalls != null && !"null".equalsIgnoreCase(vmnReceivedCalls) ? vmnReceivedCalls : "0");
                    }
                }
                showEmptyScreen();
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgress();
                showEmptyScreen();
                Methods.showSnackBarNegative(VmnCallCardsActivity.this,getString(R.string.something_went_wrong));
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(VmnCallCardsActivity.this,ShowVmnCallActivity.class);

        switch (v.getId()){
            case R.id.card_view_view_calllog:
                if(totalCount.getText().toString().equals("0")){
                    Methods.showSnackBarNegative(VmnCallCardsActivity.this,"You do not have call logs.");
                    return;
                }
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
        }
    }
}

package com.nowfloats.Analytics_Screen;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nowfloats.Analytics_Screen.model.VmnCallModel;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

import java.util.ArrayList;

/**
 * Created by Admin on 27-04-2017.
 */

public class VmnCallCardsActivity extends AppCompatActivity implements View.OnClickListener,VmnDataSingleton.ConnectToVmnData {

    ArrayList<VmnCallModel> totalCalls = new ArrayList<>();

    UserSessionManager sessionManager;
    ProgressBar mProgressBar;
    CardView viewCallLogCard;
    TextView missedCount, receivedCount,totalCount, virtualNumber;
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vmn_call_cards);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        MixPanelController.track(MixPanelController.VMN_CALL_TRACKER,null);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            setTitle("Call Tracker");
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        sessionManager = new UserSessionManager(this,this);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_vmncall);
        missedCount = (TextView) findViewById(R.id.missed_count);
        receivedCount = (TextView) findViewById(R.id.received_count);
        totalCount = (TextView) findViewById(R.id.total_count);
        virtualNumber = (TextView) findViewById(R.id.tv_virtual_number);
        viewCallLogCard = (CardView) findViewById(R.id.card_view_view_calllog);

        viewCallLogCard.setOnClickListener(this);
        mProgressBar.setVisibility(View.VISIBLE);
        VmnDataSingleton.getInstance().requestVmnData(VmnCallCardsActivity.this,sessionManager.getFPID());
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
                if(totalCalls.size() == 0){
                    Methods.showSnackBarNegative(VmnCallCardsActivity.this,"You do not have call logs.");
                    return;
                }
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
        }
    }

    @Override
    public void onDataLoaded(ArrayList<VmnCallModel> vmnData) {
        mProgressBar.setVisibility(View.GONE);
        if(vmnData == null){
            Methods.showSnackBarNegative(VmnCallCardsActivity.this,getString(R.string.something_went_wrong_try_again));
        }else
        {
            String vNumber = "";
            int missCount =0, receiveCount =0;

            totalCalls = vmnData;
            for (VmnCallModel model : vmnData)
            {
                if(vNumber.equals("")) {
                    vNumber = model.getVirtualNumber();
                }
                if(model.getCallStatus().equalsIgnoreCase("MISSED")){
                    missCount++;
                }
                else
                {
                    receiveCount++;
                }
            }
            totalCount.setText(String.valueOf(totalCalls.size()));
            missedCount.setText(String.valueOf(missCount));
            receivedCount.setText(String.valueOf(receiveCount));
            virtualNumber.setText(vNumber);
        }
    }
}

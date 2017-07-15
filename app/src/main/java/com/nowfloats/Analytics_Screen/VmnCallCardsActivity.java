package com.nowfloats.Analytics_Screen;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

/**
 * Created by Admin on 27-04-2017.
 */

public class VmnCallCardsActivity extends AppCompatActivity implements View.OnClickListener {

    UserSessionManager sessionManager;
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
        missedCount = (TextView) findViewById(R.id.missed_count);
        receivedCount = (TextView) findViewById(R.id.received_count);
        totalCount = (TextView) findViewById(R.id.total_count);
        virtualNumber = (TextView) findViewById(R.id.tv_virtual_number);
        viewCallLogCard = (CardView) findViewById(R.id.card_view_view_calllog);
        virtualNumber.setText(sessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER));

        Intent intent = getIntent();
        totalCount.setText(intent.getStringExtra("TotalCalls"));
        receivedCount.setText(intent.getStringExtra("ReceivedCalls"));
        missedCount.setText(intent.getStringExtra("MissedCalls"));

        viewCallLogCard.setOnClickListener(this);
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

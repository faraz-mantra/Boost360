package com.nowfloats.Analytics_Screen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nowfloats.Analytics_Screen.model.SubscriberModel;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.thinksity.R;

/**
 * Created by Admin on 03-03-2017.
 */

public class SubscriberDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    TextView subscriberEmailId,subscriberDate,subscriberCall,subscriberMessage,unSubscribe;
    private SubscriberModel mSubscriberData;
    LinearLayout layout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_subscriber_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            setTitle("Subscriber Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        Intent i = getIntent();
        mSubscriberData = new Gson().fromJson(i.getStringExtra("data"), SubscriberModel.class);
        if(mSubscriberData == null) return;

        subscriberEmailId = (TextView) findViewById(R.id.subscriber_email);
        subscriberDate = (TextView) findViewById(R.id.subcriber_date);
        subscriberCall = (TextView) findViewById(R.id.call_subscriber);
        subscriberMessage = (TextView) findViewById(R.id.message_subscriber);
        unSubscribe = (TextView) findViewById(R.id.unsubscribe);
        layout = (LinearLayout) findViewById(R.id.parent_layout);
        subscriberCall.setOnClickListener(this);
        subscriberMessage.setOnClickListener(this);
        unSubscribe.setOnClickListener(this);

        String sDate = mSubscriberData.getCreatedOn().replace("/Date(", "").replace(")/", "");
        String[] splitDate = sDate.split("\\+");
        subscriberDate.setText("Subscribed on "+ Methods.getFormattedDate(splitDate[0]));
        subscriberEmailId.setText(mSubscriberData.getUserMobile());
        if(!mSubscriberData.getUserMobile().toLowerCase().contains("@")) {
            subscriberEmailId.setText("+"+mSubscriberData.getUserCountryCode()+" -"+mSubscriberData.getUserMobile());
            subscriberCall.setVisibility(View.VISIBLE);
        }else{
            subscriberEmailId.setText(mSubscriberData.getUserMobile());
            subscriberMessage.setText("Send Email");
        }
        try {
            setSubscriberStatus(Integer.parseInt(mSubscriberData.getSubscriptionStatus()));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void setSubscriberStatus(int status) {
        if(Constants.SubscriberStatus.SUBSCRIBED.value == status) {
            layout.setBackgroundResource(R.color.primary);
            unSubscribe.setText("UnSubscribe");
            unSubscribe.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this,R.drawable.ic_delete_black_24dp),null,null,null);
        }else if(Constants.SubscriberStatus.REQUESTED.value == status) {
            unSubscribe.setText("Subscribe Initiated");
            layout.setBackgroundResource(R.color.gray_transparent);
            unSubscribe.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this,R.drawable.ic_delete_black_24dp),null,null,null);
        }else if(Constants.SubscriberStatus.UNSUBSCRIBED.value == status){
            unSubscribe.setText("Subscribe");
            unSubscribe.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this,R.drawable.ic_add_black_24dp),null,null,null);
            layout.setBackgroundResource(R.color.gray_transparent);
        }
    }

    private void sendSms(){
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.putExtra("sms_body", "write here");
        sendIntent.setType("vnd.android-dir/mms-sms");
        sendIntent.putExtra("address",mSubscriberData.getUserMobile());
        startActivity(Intent.createChooser(sendIntent,"Sms by:"));
    }
    private void makeCall(){
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:"+"+"+mSubscriberData.getUserCountryCode()+mSubscriberData.getUserMobile()));
        startActivity(Intent.createChooser(callIntent,"Call by:"));
    }
    private void sendMail(){
        Intent email = new Intent(Intent.ACTION_SEND);
        email.setData(Uri.parse("mailto:"));
        email.setType("text/plain");
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{mSubscriberData.getUserMobile()});
        startActivity(Intent.createChooser(email,"Email by:"));
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
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
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.call_subscriber:
                makeCall();
                break;
            case R.id.message_subscriber:
                if(mSubscriberData.getUserMobile().contains("@")){
                    sendMail();
                }else{
                    sendSms();
                }
                break;
            case R.id.unsubscribe:
                Toast.makeText(this, "This feature is not available", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
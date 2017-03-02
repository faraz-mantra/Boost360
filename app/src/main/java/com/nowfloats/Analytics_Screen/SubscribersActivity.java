package com.nowfloats.Analytics_Screen;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nowfloats.Analytics_Screen.API.SubscriberApis;
import com.nowfloats.Analytics_Screen.model.SubscriberModel;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.util.Collections;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SubscribersActivity extends AppCompatActivity {


    private UserSessionManager mSessionManager;
    private int mOffset = 0;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private Toolbar toolbar;
    List<SubscriberModel> mSubscriberList = Collections.emptyList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribers);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        TextView titleTextView = (TextView) toolbar.findViewById(R.id.titleTextView);
        titleTextView.setText(getResources().getString(R.string.subscribers));

        mProgressBar = (ProgressBar) findViewById(R.id.pb_subscriber);
        mRecyclerView = (RecyclerView) findViewById(R.id.lv_subscribers);

        mSessionManager = new UserSessionManager(getApplicationContext(), SubscribersActivity.this);
        if (mSessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG) != null) {

        } else {
            Methods.showSnackBarNegative(this, getResources().getString(R.string.could_not_find_fb_tag));
        }
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

            }
        });


        BoostLog.d("Test for Fp Tag: ", mSessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG));

    }
    private void getSubscribersList(String mOffset){

        SubscriberApis mSubscriberApis = Constants.restAdapter.create(SubscriberApis.class);
        mSubscriberApis.getsubscribers(mSessionManager.getFpTag(), Constants.clientId, mOffset, new Callback<List<SubscriberModel>>() {
            @Override
            public void success(List<SubscriberModel> subscriberModels, Response response) {
                if(subscriberModels == null){
                    return;
                }
                for (int i=0;i<subscriberModels.size();i++){
                    mSubscriberList.add(subscriberModels.get(i));

                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {

            BoostLog.d("Back", "Back Pressed");
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            //getSupportFragmentManager().popBackStack();
            //  NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }

}
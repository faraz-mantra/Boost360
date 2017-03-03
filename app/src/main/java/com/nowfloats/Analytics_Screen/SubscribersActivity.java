package com.nowfloats.Analytics_Screen;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nowfloats.Analytics_Screen.API.SubscriberApis;
import com.nowfloats.Analytics_Screen.Search_Query_Adapter.SubscribersAdapter;
import com.nowfloats.Analytics_Screen.model.SubscriberModel;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.util.ArrayList;
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
    ArrayList<SubscriberModel> mSubscriberList = new ArrayList<>();
    SubscribersAdapter mSubscriberAdapter;
    private LinearLayoutManager mLayoutManager;

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

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mSubscriberAdapter = new SubscribersAdapter(this,mSubscriberList);
        mRecyclerView.setAdapter(mSubscriberAdapter);

        mSessionManager = new UserSessionManager(getApplicationContext(), SubscribersActivity.this);
        if (mSessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG) != null) {

        } else {
            Methods.showSnackBarNegative(this, getResources().getString(R.string.could_not_find_fb_tag));
        }
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                int count = recyclerView.getChildCount();
                int visiblePosition = mLayoutManager.findLastVisibleItemPosition();
                /*if(visiblePosition>=count-2){
                    mOffset+=10;
                    getSubscribersList(String.valueOf(String.valueOf(mOffset)));
                }*/
            }
        });

        getSubscribersList(String.valueOf(mOffset));
        BoostLog.d("Test for Fp Tag: ", mSessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG));

    }
    private void getSubscribersList(String mOffset){
        mProgressBar.setVisibility(View.VISIBLE);
        SubscriberApis mSubscriberApis = Constants.restAdapter.create(SubscriberApis.class);
        mSubscriberApis.getsubscribers(mSessionManager.getFpTag(), Constants.clientId, mOffset, new Callback<List<SubscriberModel>>() {
            @Override
            public void success(List<SubscriberModel> subscriberModels, Response response) {
                mProgressBar.setVisibility(View.GONE);
                if(subscriberModels == null){
                    return;
                }
                int count = mSubscriberList.size();

                for (int i=0;i<subscriberModels.size();i++){
                    Log.v("ggg",subscriberModels.get(i).getUserMobile());
                    mSubscriberList.add(subscriberModels.get(i));
                    mSubscriberAdapter.notifyItemChanged(count+i);
                }
                //Log.v("ggg",count+"");
            }

            @Override
            public void failure(RetrofitError error) {
                mProgressBar.setVisibility(View.GONE);

                Methods.showSnackBarNegative(SubscribersActivity.this,getString(R.string.something_went_wrong));
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
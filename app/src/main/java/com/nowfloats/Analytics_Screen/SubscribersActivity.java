package com.nowfloats.Analytics_Screen;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nowfloats.Analytics_Screen.API.SubscriberApis;
import com.nowfloats.Analytics_Screen.Search_Query_Adapter.SubscribersAdapter;
import com.nowfloats.Analytics_Screen.model.SubscriberModel;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SubscribersActivity extends AppCompatActivity implements View.OnClickListener,SubscribersAdapter.MenuItemDelete {


    private UserSessionManager mSessionManager;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private Toolbar toolbar;
    ArrayList<SubscriberModel> mSubscriberList = new ArrayList<>();
    SubscribersAdapter mSubscriberAdapter;
    private LinearLayoutManager mLayoutManager;
    private boolean stop;
    TextView titleTextView;
    EditText searchEditText;
    ImageView deleteImage,searchImage;

    LinearLayout emptyLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribers);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        titleTextView = (TextView) toolbar.findViewById(R.id.titleTextView);
        searchEditText = (EditText) findViewById(R.id.search_edittext);
        deleteImage = (ImageView) findViewById(R.id.delete_image);
        searchImage = (ImageView) findViewById(R.id.search_image);

        deleteImage.setOnClickListener(this);
        searchImage.setOnClickListener(this);

        titleTextView.setText(getResources().getString(R.string.subscribers));
        emptyLayout = (LinearLayout) findViewById(R.id.emplty_layout);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mProgressBar = (ProgressBar) findViewById(R.id.pb_subscriber);
        mRecyclerView = (RecyclerView) findViewById(R.id.lv_subscribers);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mSubscriberAdapter = new SubscribersAdapter(this,mSubscriberList);
        mRecyclerView.setAdapter(mSubscriberAdapter);

        mSessionManager = new UserSessionManager(getApplicationContext(), SubscribersActivity.this);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int count = mLayoutManager.getItemCount();
                int visiblePosition = mLayoutManager.findLastVisibleItemPosition();
                if(visiblePosition>=count-2 && !stop) {//call api when second last item visible
                        getSubscribersList();
                }
            }
        });

        getSubscribersList();
    }
    private void getSubscribersList(){
        stop =true;
        final int count = mSubscriberList.size();
        String offset = String.valueOf(String.valueOf(count+1));

        mProgressBar.setVisibility(View.VISIBLE);
        SubscriberApis mSubscriberApis = Constants.restAdapter.create(SubscriberApis.class);
        mSubscriberApis.getsubscribers(mSessionManager.getFpTag(), Constants.clientId, offset, new Callback<List<SubscriberModel>>() {
            @Override
            public void success(List<SubscriberModel> subscriberModels, Response response) {
                mProgressBar.setVisibility(View.GONE);
                if(subscriberModels == null){
                    return;
                }
                int newItems = subscriberModels.size();

                for (int i=0;i<newItems;i++){
                    //Log.v("ggg",subscriberModels.get(i).getUserMobile());
                    mSubscriberList.add(subscriberModels.get(i));
                    mSubscriberAdapter.notifyItemChanged(count+i);
                }
                if(newItems >=10){
                    stop = false;
                }
                if(mSubscriberList.size() == 0){
                    emptyLayout.setVisibility(View.VISIBLE);
                }
                Log.v("subscribes",count+1+" "+newItems);
            }

            @Override
            public void failure(RetrofitError error) {
                mProgressBar.setVisibility(View.GONE);

                Methods.showSnackBarNegative(SubscribersActivity.this,getString(R.string.something_went_wrong));
            }
        });
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.delete_image:
                //deleteSubscriber();
                break;
            case R.id.search_image:

                if(searchEditText.getVisibility() == View.VISIBLE){
                    //search(editText.getText().toString().trim());
                    Log.v("ggg",searchEditText.getText().toString().trim());
                }else{
                    titleTextView.setVisibility(View.GONE);
                    searchEditText.setVisibility(View.VISIBLE);
                    searchEditText.requestFocus();
                }
                break;
        }
    }
    //method call when view changed from adapter
    @Override
    public void onChangeView(boolean deleteView) {
        titleTextView.setVisibility(View.VISIBLE);
        if(deleteView) {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
            searchImage.setVisibility(View.GONE);
            searchEditText.setVisibility(View.GONE);
            deleteImage.setVisibility(View.VISIBLE);
        }else{
            deleteImage.setVisibility(View.GONE);
            searchImage.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                if(searchEditText.getVisibility() == View.VISIBLE){
                    searchEditText.clearFocus();
                    searchEditText.setVisibility(View.GONE);
                    titleTextView.setVisibility(View.VISIBLE);
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);

                }else
                {
                   onBackPressed();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
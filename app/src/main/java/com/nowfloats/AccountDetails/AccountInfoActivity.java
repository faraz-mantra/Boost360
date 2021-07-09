package com.nowfloats.AccountDetails;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nowfloats.AccountDetails.Model.AccountDetailModel;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.QueryMap;

/**
 * Created by guru on 06-07-2015.
 */
public class AccountInfoActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    Activity activity;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_info);

        toolbar = (Toolbar) findViewById(R.id.accountinfo_action_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity = AccountInfoActivity.this;
        TextView titleTextView = (TextView) toolbar.findViewById(R.id.titleTextView);
        titleTextView.setText(getResources().getString(R.string.account_detail));
        UserSessionManager session = new UserSessionManager(activity.getApplicationContext(),activity);
        TextView tag = (TextView)findViewById(R.id.tag_name);
        tag.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG).toLowerCase());

        recyclerView = (RecyclerView) findViewById(R.id.account_info_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        final LinearLayout progressLayout = (LinearLayout)findViewById(R.id.progress_accinfo_layout);
        progressLayout.setVisibility(View.VISIBLE);
        final LinearLayout zerothLayout = (LinearLayout)findViewById(R.id.zeroth_layout);
        zerothLayout.setVisibility(View.GONE);
        TextView zeroth_tv = (TextView)findViewById(R.id.zeroth_txt);
        if(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY).toLowerCase().equals("india")){
            zeroth_tv.setText(getResources().getString(R.string.demo_plan));
        }else{zeroth_tv.setText(getResources().getString(R.string.free_plan));}

        TextView zeroth_storebtn = (TextView)findViewById(R.id.zeroth_storebtn);
        zeroth_storebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Constants.gotoStore = true;
            }
        });

        try {
            AccInfoInterface infoInterface = Constants.restAdapter.create(AccInfoInterface.class);
            HashMap<String,String> values = new HashMap<>();
            values.put("clientId", Constants.clientId);
            values.put("fpId",session.getFPID());
            infoInterface.getAccDetails(values,new Callback<ArrayList<AccountDetailModel>>() {
                @Override
                public void success(ArrayList<AccountDetailModel> accountDetailModels, Response response) {
                    adapter = new AccountInfoAdapter(activity,accountDetailModels);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    progressLayout.setVisibility(View.GONE);
                    if (accountDetailModels == null ||accountDetailModels.size()==0){
                        zerothLayout.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    progressLayout.setVisibility(View.GONE);
                    zerothLayout.setVisibility(View.VISIBLE);
                }
            });
        }catch (Exception e){e.printStackTrace(); progressLayout.setVisibility(View.GONE);}
    }

    public interface AccInfoInterface{
        //https://api.withfloats.com/Discover/v1/FloatingPoint/GetPaidWidgetDetailsForFP?clientId={clientId}&fpId={fpId}
        // https://api.withfloats.com/Discover/v1/FloatingPoint/GetPaidWidgetDetailsForFP?
        // clientId=""&fpId=""
        @GET("/Discover/v1/FloatingPoint/GetPaidWidgetDetailsForFP")
        public void getAccDetails(@QueryMap Map<String,String> map,Callback<ArrayList<AccountDetailModel>> callback);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id==android.R.id.home ){
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
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
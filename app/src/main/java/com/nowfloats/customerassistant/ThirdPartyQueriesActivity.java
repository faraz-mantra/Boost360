package com.nowfloats.customerassistant;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.customerassistant.adapters.ThirdPartyAdapter;
import com.nowfloats.customerassistant.models.SMSSuggestions;
import com.nowfloats.customerassistant.models.SuggestionsDO;
import com.nowfloats.customerassistant.service.CustomerAssistantApi;
import com.nowfloats.util.BusProvider;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by admin on 10/4/2017.
 */

public class ThirdPartyQueriesActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView rvList;
    ThirdPartyAdapter adapter;
    Bus mBus;
    UserSessionManager sessionManager;
    private List<SuggestionsDO> queriesList = new ArrayList<>();
    private String appVersion = "";
    ProgressDialog progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_party_queries);
        init();
        CustomerAssistantApi customerApis = new CustomerAssistantApi(mBus);
        getMessages(customerApis);

    }

    private void init(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        rvList = (RecyclerView) findViewById(R.id.rv_list);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        sessionManager = new UserSessionManager(this,this);
        mBus = BusProvider.getInstance().getBus();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvList.setHasFixedSize(true);
        progressBar = new ProgressDialog(this);
        progressBar.setIndeterminate(true);
        adapter= new ThirdPartyAdapter(this,queriesList);
        rvList.setLayoutManager(manager);
        rvList.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        rvList.setAdapter(adapter);

        try {
            appVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }
    private void getMessages(CustomerAssistantApi api){
        showProgress();
        HashMap<String, String> offersParam = new HashMap<>();
        offersParam.put("fpId", sessionManager.getFPID());
        api.getMessages(offersParam,sessionManager.getFPID(),appVersion);
    }

    private void showProgress() {
        if(!progressBar.isShowing() && !isFinishing()){
            progressBar.show();
        }
    }

    private void hideProgress(){
        if(progressBar.isShowing()){
            progressBar.dismiss();
        }
    }
    @Subscribe
    public void onMessagesReceived(SMSSuggestions smsSuggestions){
        adapter.refreshListData(smsSuggestions.getSuggestionList());
        hideProgress();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_search:
                return true;
            case R.id.action_filter:
                return true;
            case R.id.action_menu:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mBus.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBus.unregister(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_third_party_query,menu);
        return true;
    }

}

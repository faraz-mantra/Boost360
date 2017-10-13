package com.nowfloats.customerassistant;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
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
import java.util.Collections;
import java.util.Comparator;
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
    SwipeRefreshLayout mSwipeRefreshLayout;

    private enum SortType{
        DATE,CHANNEL,EXPIRE
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_party_queries);
        init();

    }

    private void init(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        rvList = (RecyclerView) findViewById(R.id.rv_list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary_color);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        sessionManager = new UserSessionManager(this,this);
        mBus = BusProvider.getInstance().getBus();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        final CustomerAssistantApi customerApis = new CustomerAssistantApi(mBus);
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

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                getMessages(customerApis);
            }
        });

        getMessages(customerApis);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void getMessages(CustomerAssistantApi api){
        showProgress();
        HashMap<String, String> offersParam = new HashMap<>();
        offersParam.put("fpId", sessionManager.getFPID());
        api.getMessages(offersParam,sessionManager.getFPID(),appVersion);
    }

    private void showProgress() {
        if(!mSwipeRefreshLayout.isRefreshing()&&!progressBar.isShowing() && !isFinishing()){
            progressBar.show();
        }
    }

    private void hideProgress(){
        if(progressBar.isShowing()){
            progressBar.dismiss();
        }
        if(mSwipeRefreshLayout.isRefreshing()){
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
    @Subscribe
    public void onMessagesReceived(SMSSuggestions smsSuggestions){
        if(smsSuggestions != null) {
            sort(smsSuggestions.getSuggestionList(), SortType.EXPIRE);
            adapter.refreshListData(smsSuggestions.getSuggestionList());
        }
        hideProgress();
    }

    private void sort(List<SuggestionsDO> smsSuggestionsList, final SortType sortType){
        Collections.sort(smsSuggestionsList, new Comparator<SuggestionsDO>() {
            @Override
            public int compare(SuggestionsDO o1, SuggestionsDO o2) {
                switch(sortType){
                    case CHANNEL:
                        return o1.getSource().compareToIgnoreCase(o2.getSource());
                    case DATE:
                        if(o1.getDate()<o2.getDate()){
                            return -1;
                        }else if (o1.getDate() == o2.getDate()){
                            return 0;
                        }else{
                            return 1;
                        }
                    case EXPIRE:
                    default:
                        if(o1.getExpiryDate()<o2.getExpiryDate()){
                            return -1;
                        }else if (o1.getExpiryDate() == o2.getExpiryDate()){
                            return 0;
                        }else{
                            return 1;
                        }

                }
            }
        });
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

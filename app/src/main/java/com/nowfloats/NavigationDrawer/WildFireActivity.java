package com.nowfloats.NavigationDrawer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.WildFireApis;
import com.nowfloats.NavigationDrawer.Adapter.TextExpandableAdapter;
import com.nowfloats.NavigationDrawer.Adapter.WildFireAdapter;
import com.nowfloats.NavigationDrawer.model.WildFireDataModel;
import com.nowfloats.NavigationDrawer.model.WildFireKeyStatsModel;
import com.nowfloats.Store.NewPricingPlansActivity;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Admin on 29-11-2017.
 */

public class WildFireActivity extends AppCompatActivity implements View.OnClickListener{

    private ProgressDialog progressDialog;
    private ArrayList<WildFireKeyStatsModel> wildFireList;
    private WildFireAdapter adapter;
    private Menu menu;
    private enum SortType{
        KEYWORD;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wildfire);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("WildFire");
        if (getSupportActionBar() != null){

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.please_wait));
        adapter = new WildFireAdapter(this);
        UserSessionManager manager = new UserSessionManager(this,this);
        getWildFireData(manager.getFPDetails(Key_Preferences.EXTERNAL_SOURCE_ID));
    }

    private void showKeywordsList(){

        if (wildFireList != null && wildFireList.size()>0){
            showMenu();
            setTitle("Keywords ("+wildFireList.size()+")");
            RecyclerView keywordRv = findViewById(R.id.keyword_rv);
            keywordRv.setHasFixedSize(true);
            keywordRv.setVisibility(View.VISIBLE);
            keywordRv.setLayoutManager(new LinearLayoutManager(this));
            adapter.setModelList(wildFireList);
            keywordRv.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
            keywordRv.setAdapter(adapter);
        }else{
            showDefaultPage();
        }
    }
    private void showDefaultPage(){
        ConstraintLayout defaultLayout = findViewById(R.id.default_layout);
        defaultLayout.setVisibility(View.VISIBLE);
        TextView wildfireDefinitionTv = findViewById(R.id.wildfire_definition);
        findViewById(R.id.tv_wildfire).setOnClickListener(this);
        findViewById(R.id.tv_know_more).setOnClickListener(this);
        wildfireDefinitionTv.setText(Methods.fromHtml(getString(R.string.wildfire_definition)));
        ArrayList<ArrayList<String>> childList = new ArrayList<>(3);
        ArrayList<String> parentList =new ArrayList<>(Arrays.asList( getResources().getStringArray(R.array.wildfire_parents)));
        childList.add(new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.wildfire_parent_0))));
        childList.add(new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.wildfire_parent_1))));
        childList.add(new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.wildfire_parent_2))));

        ExpandableListView expandableListView = findViewById(R.id.info_exlv);
        expandableListView.setAdapter(new TextExpandableAdapter(this,childList,parentList));
    }

    private void showMenu() {
        getMenuInflater().inflate(R.menu.wildfire_menu,menu);
        MenuItem myActionMenuItem = menu.findItem( R.id.item_search);
        SearchView searchView = (SearchView) myActionMenuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    private void showProgress(){
        if (!progressDialog.isShowing()){
            progressDialog.show();
        }
    }
    private void hideProgress(){
            if (progressDialog.isShowing()){
                progressDialog.dismiss();
            }
    }
    private void getKeyWordStats(String accId){
        WildFireApis apis = WildFireApis.adapter.create(WildFireApis.class);
        apis.getGoogleStats(Constants.clientId, accId, new Callback<ArrayList<WildFireKeyStatsModel>>() {
            @Override
            public void success(ArrayList<WildFireKeyStatsModel> wildFireKeyStatsModels, Response response) {
                hideProgress();
                if (wildFireKeyStatsModels != null){
                    wildFireList = wildFireKeyStatsModels;
                }
                showKeywordsList();
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgress();
                showKeywordsList();
            }
        });
    }

    private void getWildFireChannels(String clientId, final String accountId){
        WildFireApis apis = WildFireApis.adapter.create(WildFireApis.class);
        apis.getWildFireChannels(clientId, accountId, new Callback<ArrayList<String>>() {
            @Override
            public void success(ArrayList<String> strings, Response response) {
                if (strings != null && strings.contains("google")){
                    getKeyWordStats(accountId);
                }else{
                    hideProgress();
                    showDefaultPage();
                    // show default page
                }
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgress();
                showDefaultPage();
            }
        });
    }
    private void getWildFireData(String sourceId){
        showProgress();
        WildFireApis apis = Constants.restAdapter.create(WildFireApis.class);
        apis.getWildFireData(sourceId, Constants.clientId, new Callback<WildFireDataModel>() {
            @Override
            public void success(WildFireDataModel wildFireDataModel, Response response) {
                if (wildFireDataModel != null){
                    getWildFireChannels(Constants.clientId, wildFireDataModel.getId());
                }else{
                    hideProgress();
                    showDefaultPage();
                    // show default page
                }
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgress();
                showDefaultPage();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_wildfire:
                startActivity(new Intent(WildFireActivity.this, NewPricingPlansActivity.class));
                break;
            case R.id.tv_know_more:
                // email
                break;
        }
    }
    private void sort(List<WildFireKeyStatsModel> keywordModelList, final SortType sortType){
        Collections.sort(keywordModelList, new Comparator<WildFireKeyStatsModel>() {
            @Override
            public int compare(WildFireKeyStatsModel o1, WildFireKeyStatsModel o2) {
                switch(sortType){
                    case KEYWORD:
                        return 1;
                    default:
                        return 0;
                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.item_search:
                break;
            case R.id.item_sort:
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

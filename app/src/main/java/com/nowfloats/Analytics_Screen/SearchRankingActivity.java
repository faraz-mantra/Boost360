package com.nowfloats.Analytics_Screen;

import android.app.ProgressDialog;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.nowfloats.Analytics_Screen.API.SearchQueryApi;
import com.nowfloats.Analytics_Screen.Search_Query_Adapter.SearchRankingRvAdapter;
import com.nowfloats.Analytics_Screen.model.SearchRankModel;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SearchRankingActivity extends AppCompatActivity {

    public static final int FILTER_INCREASED = 0;
    public static final int FILTER_SAME = 1;
    public static final int FILTER_DECREASED = 2;
    public static final int FILTER_NEW = 3;
    public static final int FILTER_LOST = 4;

    Toolbar toolbar;
    RecyclerView rvSearchQuery;
    ProgressDialog pd;
    LinearLayout llRankContainer, llEmptyLayout;
    TextView tvSearchQueryTitle;

    List<SearchRankModel> mSearchRankList;
    List<SearchRankModel> mFilteredList = new ArrayList<>();
    private UserSessionManager mSession;
    private SearchRankingRvAdapter mRvAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_ranking);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.search_ranking));

        llRankContainer = (LinearLayout) findViewById(R.id.ll_rank_container);
        llEmptyLayout = (LinearLayout) findViewById(R.id.ll_empty_msg);
        rvSearchQuery = (RecyclerView) findViewById(R.id.rv_search_query);
        rvSearchQuery.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvSearchQuery.addItemDecoration(new DividerItemDecoration(rvSearchQuery.getContext(), DividerItemDecoration.VERTICAL));
        tvSearchQueryTitle = (TextView) findViewById(R.id.tv_search_query_title);

        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.please_wait));

        mSession = new UserSessionManager(this, this);

        getSearchRankings();
    }


    private void getSearchRankings(){
        if(!isFinishing() && !pd.isShowing()){
            pd.show();
        }
        RestAdapter adapter = new RestAdapter.Builder().setEndpoint(Constants.RIA_MEMORY_API_URL).build();
        SearchQueryApi queryApi = adapter.create(SearchQueryApi.class);
        queryApi.getKeyWordRanks(mSession.getFpTag(), new Callback<List<SearchRankModel>>() {
            @Override
            public void success(List<SearchRankModel> searchRankModels, Response response) {
                if(!isFinishing() && pd.isShowing()) {
                    pd.dismiss();
                }
                if(searchRankModels.size()>0){
                    mSearchRankList = searchRankModels;
                    filterList(FILTER_INCREASED);
                }else {
                    showEmptyMessage();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if(!isFinishing() && pd.isShowing()) {
                    pd.dismiss();
                }
                Methods.showSnackBarNegative(SearchRankingActivity.this, getString(R.string.something_went_wrong));
                showEmptyMessage();
            }
        });
    }


    private void filterList(final int filter){
        if(mSearchRankList==null || mSearchRankList.size()==0){
            return;
        }
        llRankContainer.setVisibility(View.VISIBLE);
        llEmptyLayout.setVisibility(View.INVISIBLE);
        if(!isFinishing() && !pd.isShowing()){
            pd.show();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                mFilteredList.clear();
                switch (filter) {
                    case FILTER_INCREASED:
                        for (SearchRankModel model : mSearchRankList) {
                            if (model.getOldRank() != -1 && model.getNewRank() != -1 &&
                                    getPage(model.getNewRank()) < getPage(model.getOldRank())) {
                                mFilteredList.add(model);
                            }
                        }
                        break;
                    case FILTER_DECREASED:
                        for (SearchRankModel model : mSearchRankList) {
                            if (model.getOldRank() != -1 && model.getNewRank() != -1 &&
                                    getPage(model.getNewRank()) > getPage(model.getOldRank())) {
                                mFilteredList.add(model);
                            }
                        }
                        break;
                    case FILTER_SAME:
                        for (SearchRankModel model : mSearchRankList) {
                            if (model.getOldRank() != -1 && model.getNewRank() != -1 &&
                                    getPage(model.getNewRank()) == getPage(model.getOldRank())) {
                                mFilteredList.add(model);
                            }
                        }
                        break;
                    case FILTER_LOST:
                        for (SearchRankModel model : mSearchRankList) {
                            if (model.getOldRank() != -1 && model.getNewRank() == -1) {
                                mFilteredList.add(model);
                            }
                        }
                        break;
                    case FILTER_NEW:
                        for (SearchRankModel model : mSearchRankList) {
                            if (model.getOldRank() == -1 && model.getNewRank() != -1) {
                                mFilteredList.add(model);
                            }
                        }
                        break;
                }

                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(!isFinishing() && pd.isShowing()){
                            pd.dismiss();
                            if(mFilteredList.size()>0) {
                                tvSearchQueryTitle.setText("Search Queries (" + mFilteredList.size() + ")");
                                mRvAdapter = new SearchRankingRvAdapter(mFilteredList, filter, SearchRankingActivity.this);
                                rvSearchQuery.setAdapter(mRvAdapter);
                                mRvAdapter.notifyDataSetChanged();
                            }else {
                                showEmptyMessage();
                            }
                        }

                    }
                });
            }
        }).start();
    }

    private void showEmptyMessage(){
        llRankContainer.setVisibility(View.INVISIBLE);
        llEmptyLayout.setVisibility(View.VISIBLE);
    }


    private int getPage(int rank){
        return ((rank-1)/10)+1;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_ranking_spinner_menu, menu);

        MenuItem item = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
        //spinner.setBackground(getResources().getDrawable(R.drawable.spinner_bg));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_list_item_array, R.layout.spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterList(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
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
}

package com.nowfloats.Analytics_Screen;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nowfloats.Analytics_Screen.API.SearchQueryApi;
import com.nowfloats.Analytics_Screen.Search_Query_Adapter.SearchRankingRvAdapter;
import com.nowfloats.Analytics_Screen.model.SearchAnalytics;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SearchRankingActivity extends AppCompatActivity {

    //public static final int FILTER_INCREASED = 0;
    //public static final int FILTER_SAME = 1;
    //public static final int FILTER_DECREASED = 2;
    //public static final int FILTER_NEW = 3;
    //public static final int FILTER_LOST = 4;

    private RecyclerView rvSearchQuery;
    private ProgressBar progressBar;

    //private ProgressDialog pd;

    private LinearLayout llRankContainer, llEmptyLayout;
    private TextView tvSearchQueryTitle, tvSearchType;
    private ImageView ivSort;

    private List<SearchAnalytics> mSearchRankList = new ArrayList<>();

    //private List<SearchRankModel> mFilteredList = new ArrayList<>();

    private UserSessionManager mSession;
    private SearchRankingRvAdapter mRvAdapter;

    private boolean stop = false;
    private boolean isLoading = false;
    private Filter filterType = Filter.AVERAGE_POSITION;
    private Sort sortType = Sort.ASCENDING;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_ranking);

        MixPanelController.track(MixPanelController.SEARCH_RANKING_MAIN, null);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.search_ranking));
        }

        llRankContainer = findViewById(R.id.ll_rank_container);
        llEmptyLayout = findViewById(R.id.ll_empty_msg);
        rvSearchQuery = findViewById(R.id.rv_search_query);
        tvSearchQueryTitle = findViewById(R.id.tv_search_query_title);
        tvSearchType = findViewById(R.id.tv_search_type);
        progressBar = findViewById(R.id.progress_bar);
        ivSort = findViewById(R.id.iv_sort);

        //rvSearchQuery.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //rvSearchQuery.addItemDecoration(new DividerItemDecoration(rvSearchQuery.getContext(), DividerItemDecoration.VERTICAL));

        //pd = new ProgressDialog(this);
        //pd.setMessage(getString(R.string.please_wait));

        mSession = new UserSessionManager(this, this);

        this.initRecyclerAdapter();
        this.addListener();
        this.getSearchRanking();
    }


    private void addListener() {
        ivSort.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                changeSortOption();
                changeSortIcon();
                applySort();
            }
        });
    }


    private void changeSortIcon() {
        ivSort.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);

        if (sortType.equals(Sort.ASCENDING)) {
            ivSort.setRotation(0);
        }
        if (sortType.equals(Sort.DESCENDING)) {
            ivSort.setRotation(180);
        }
    }

    private void changeSortOption() {
        if (sortType.equals(Sort.ASCENDING)) {
            sortType = Sort.DESCENDING;
        } else {
            sortType = Sort.ASCENDING;
        }
    }


    private void initRecyclerAdapter() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvSearchQuery.setLayoutManager(layoutManager);
        rvSearchQuery.setItemAnimator(new DefaultItemAnimator());

        mRvAdapter = new SearchRankingRvAdapter(this);
        rvSearchQuery.setAdapter(mRvAdapter);

        rvSearchQuery.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                if (lastVisibleItem >= totalItemCount - 1 && !stop && !isLoading) {
                    getSearchRanking();
                }
            }
        });
    }

    private Map<String, Object> getJsonBody(int offset) {
        List<String> list = new ArrayList<>();
        list.add(mSession.getFPID());

        Map<String, Object> map = new HashMap<>();

        try {
            map.put("WebsiteIds", list);
            map.put("Limit", 50);
            map.put("Offset", offset);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    private void getSearchRanking() {

        isLoading = true;

        final int offset = mSearchRankList.size();

        Map<String, Object> map = getJsonBody(offset);

        progressBar.setVisibility(View.VISIBLE);

        SearchQueryApi searchQueryApi = Constants.restAdapterAnalytics.create(SearchQueryApi.class);

        searchQueryApi.getSearchQueries(map, new Callback<List<SearchAnalytics>>() {

            @Override
            public void success(List<SearchAnalytics> searchQueryModels, Response response) {

                isLoading = false;

                progressBar.setVisibility(View.GONE);

                if (response.getStatus() == 204 && mSearchRankList.isEmpty()) {
                    showEmptyMessage();
                    stop = true;
                    return;
                }

                if (response.getStatus() == 200 && searchQueryModels != null) {
                    llRankContainer.setVisibility(View.VISIBLE);

                    mSearchRankList.addAll(searchQueryModels);
                    applySort();

                    //mRvAdapter.setData(mSearchRankList);
                    //mRvAdapter.notifyItemInserted(offset);

                    tvSearchQueryTitle.setText(String.valueOf("Search Queries (" + mSearchRankList.size() + ")"));
                    return;
                }

                stop = true;
                Toast.makeText(getApplicationContext(), "No More Data", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(RetrofitError error) {
                isLoading = false;

                progressBar.setVisibility(View.GONE);
                Methods.showSnackBarNegative(SearchRankingActivity.this, getString(R.string.something_went_wrong_try_again));
            }
        });
    }


    /*private void getSearchRankings()
    {
        if(!isFinishing() && !pd.isShowing())
        {
            pd.show();
        }

        SearchQueryApi queryApi = Constants.riaMemoryRestAdapter.create(SearchQueryApi.class);
        queryApi.getKeyWordRanks(mSession.getFpTag(), new Callback<List<SearchRankModel>>() {

            @Override
            public void success(List<SearchRankModel> searchRankModels, Response response) {

                if(!isFinishing() && pd.isShowing())
                {
                    pd.dismiss();
                }

                if(searchRankModels.size()>0)
                {
                    mSearchRankList = searchRankModels;
                    filterList(FILTER_INCREASED);
                }

                else
                {
                    showEmptyMessage();
                }
            }

            @Override
            public void failure(RetrofitError error) {

                if(!isFinishing() && pd.isShowing())
                {
                    pd.dismiss();
                }

                Methods.showSnackBarNegative(SearchRankingActivity.this, getString(R.string.something_went_wrong));
                showEmptyMessage();
            }
        });
    }*/


    /*private void filterList(final int filter){

        if(mSearchRankList==null || mSearchRankList.size()==0)
        {
            return;
        }

        llRankContainer.setVisibility(View.VISIBLE);
        llEmptyLayout.setVisibility(View.INVISIBLE);

        if(!isFinishing() && !pd.isShowing())
        {
            pd.show();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                mFilteredList.clear();
                switch (filter) {
                    case FILTER_INCREASED:
                        MixPanelController.track(MixPanelController.SEARCH_RANKING_INCREASED,null);
                        for (SearchRankModel model : mSearchRankList) {
                            if (model.getOldRank() != -1 && model.getNewRank() != -1 &&
                                    getPage(model.getNewRank()) < getPage(model.getOldRank())) {
                                mFilteredList.add(model);
                            }
                        }
                        break;
                    case FILTER_DECREASED:
                        MixPanelController.track(MixPanelController.SEARCH_RANKING_DECREASE,null);
                        for (SearchRankModel model : mSearchRankList) {
                            if (model.getOldRank() != -1 && model.getNewRank() != -1 &&
                                    getPage(model.getNewRank()) > getPage(model.getOldRank())) {
                                mFilteredList.add(model);
                            }
                        }
                        break;
                    case FILTER_SAME:
                        MixPanelController.track(MixPanelController.SEARCH_RANKING_SAME,null);
                        for (SearchRankModel model : mSearchRankList) {
                            if (model.getOldRank() != -1 && model.getNewRank() != -1 &&
                                    getPage(model.getNewRank()) == getPage(model.getOldRank())) {
                                mFilteredList.add(model);
                            }
                        }
                        break;
                    case FILTER_LOST:
                        MixPanelController.track(MixPanelController.SEARCH_RANKING_LOST,null);
                        for (SearchRankModel model : mSearchRankList) {
                            if (model.getOldRank() != -1 && model.getNewRank() == -1) {
                                mFilteredList.add(model);
                            }
                        }
                        break;
                    case FILTER_NEW:
                        MixPanelController.track(MixPanelController.SEARCH_RANKING_NEW,null);
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
    }*/


    private void showEmptyMessage() {
        llRankContainer.setVisibility(View.INVISIBLE);
        llEmptyLayout.setVisibility(View.VISIBLE);
    }


    private int getPage(int rank) {
        return ((rank - 1) / 10) + 1;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_ranking_spinner_menu, menu);

        /*MenuItem item = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);

        final String array[] = getResources().getStringArray(R.array.spinner_list_item_array_search_ranking);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_list_item_array_search_ranking, R.layout.spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                tvSearchType.setText(array[position]);
                mRvAdapter.filter(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        return super.onCreateOptionsMenu(menu);
    }

    private void filter(int position) {
        if (mSearchRankList.size() == 0) {
            Toast.makeText(getApplicationContext(), "No Records Found", Toast.LENGTH_SHORT).show();
            return;
        }

        final String array[] = getResources().getStringArray(R.array.spinner_list_item_array_search_ranking);

        tvSearchType.setText(array[position]);

        changeSortIcon();

        mRvAdapter.filter(mSearchRankList, position);
        applySort();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_avg_position:

                filterType = Filter.AVERAGE_POSITION;
                sortType = Sort.ASCENDING;
                filter(0);
                break;

            case R.id.menu_impressions:

                filterType = Filter.IMPRESSIONS;
                sortType = Sort.ASCENDING;
                filter(1);
                break;

            case R.id.menu_clicks:

                filterType = Filter.CLICKS;
                sortType = Sort.DESCENDING;
                filter(2);
                break;

            case R.id.menu_ctr_percent:

                filterType = Filter.CTR;
                sortType = Sort.DESCENDING;
                filter(3);
                break;

            case android.R.id.home:

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

    private void applySort() {
        Collections.sort(mSearchRankList, new Comparator<SearchAnalytics>() {

            public int compare(SearchAnalytics obj1, SearchAnalytics obj2) {
                if (filterType.equals(Filter.IMPRESSIONS)) {
                    if (sortType == Sort.ASCENDING) {
                        return Integer.valueOf(obj1.getImpressions()).compareTo(obj2.getImpressions());
                    }

                    return Integer.valueOf(obj2.getImpressions()).compareTo(obj1.getImpressions());
                }

                if (filterType.equals(Filter.CLICKS)) {
                    if (sortType == Sort.DESCENDING) {
                        return Integer.valueOf(obj2.getClicks()).compareTo(obj1.getClicks());
                    }

                    return Integer.valueOf(obj1.getClicks()).compareTo(obj2.getClicks());
                }

                if (filterType.equals(Filter.CTR)) {
                    if (sortType == Sort.DESCENDING) {
                        return Double.valueOf(obj2.getCtr()).compareTo(obj1.getCtr());
                    }

                    return Double.valueOf(obj1.getCtr()).compareTo(obj2.getCtr());
                }

                if (sortType == Sort.ASCENDING) {
                    return Integer.valueOf(obj1.getAveragePosition()).compareTo(obj2.getAveragePosition());
                }

                return Integer.valueOf(obj2.getAveragePosition()).compareTo(obj1.getAveragePosition());
            }
        });

        mRvAdapter.setData(mSearchRankList);
    }

    enum Filter {
        AVERAGE_POSITION, IMPRESSIONS, CLICKS, CTR
    }

    enum Sort {
        ASCENDING, DESCENDING
    }
}
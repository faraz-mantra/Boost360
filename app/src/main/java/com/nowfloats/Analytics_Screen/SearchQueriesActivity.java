package com.nowfloats.Analytics_Screen;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.nowfloats.Analytics_Screen.API.SearchQueryApi;
import com.nowfloats.Analytics_Screen.Search_Query_Adapter.SearchQueryAdapter;
import com.nowfloats.Analytics_Screen.model.SearchAnalytics;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NotificationCenter.AlertArchive;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Kamal on 17-02-2015.
 */
public class SearchQueriesActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private LinearLayoutManager layoutManager;
    public LinearLayout emptySearchLayout;
    UserSessionManager session;
    ArrayList<SearchAnalytics> mSearchArrayList = new ArrayList<>();
    JsonObject obj;
    private boolean stop = false;
    private boolean isLoading = false;
    ProgressBar progressBar;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_queries_frame_layout);

        toolbar = (Toolbar) findViewById(R.id.search_queries_action_bar);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        session = new UserSessionManager(getApplicationContext(),SearchQueriesActivity.this);
        TextView titleTextView = (TextView) toolbar.findViewById(R.id.titleTextView);
        titleTextView.setText(getResources().getString(R.string.search_queries));

        emptySearchLayout = (LinearLayout)findViewById(R.id.emptysearchlayout);
        recyclerView = (RecyclerView) findViewById(R.id.search_queries_recycler_view);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new SearchQueryAdapter(SearchQueriesActivity.this,mSearchArrayList);
        new AlertArchive(Constants.alertInterface,"SEARCHQUERIES",session.getFPID());
        recyclerView.setAdapter(adapter);

        //createObj();
        //getSearch();

        getSearchQueries();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                if(lastVisibleItem>=totalItemCount-1 && !stop && !isLoading){
                   //getSearch();
                   getSearchQueries();
                }
        }
        });
    }


    private Map<String, Object> getJsonBody(int offset)
    {
        List<String> list = new ArrayList<>();
        list.add(session.getFPID());

        Map<String, Object> map = new HashMap<>();

        try
        {
            map.put("WebsiteIds", list);
            map.put("Limit", 50);
            map.put("Offset", offset);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        return map;
    }

    private void getSearchQueries(){

        isLoading = true;

        final int offset = mSearchArrayList.size();

        Map<String, Object> map = getJsonBody(offset);

        progressBar.setVisibility(View.VISIBLE);

        SearchQueryApi searchQueryApi = Constants.restAdapterAnalyticsWebApi.create(SearchQueryApi.class);
        searchQueryApi.getSearchQueries(map, new Callback<List<SearchAnalytics>>() {

            @Override
            public void success(List<SearchAnalytics> searchQueryModels, Response response) {

                isLoading = false;

                progressBar.setVisibility(View.GONE);

                if(response.getStatus() == 204 && mSearchArrayList.isEmpty())
                {
                    emptySearchLayout.setVisibility(View.VISIBLE);
                    stop = true;
                    return;
                }

                if(response.getStatus() == 200 && searchQueryModels != null)
                {
                     mSearchArrayList.addAll(searchQueryModels);
                     adapter.notifyItemInserted(offset);
                     return;
                }

                stop = true;
                Toast.makeText(getApplicationContext(), "No More Data", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(RetrofitError error)
            {
                isLoading = false;
                progressBar.setVisibility(View.GONE);
                Methods.showSnackBarNegative(SearchQueriesActivity.this,getString(R.string.something_went_wrong_try_again));
            }
        });
    }


   /* private void createObj() {
        obj = new JsonObject();
        obj.addProperty("clientId", Constants.clientId);
        obj.addProperty("fpIdentifierType", session.getISEnterprise().equals("true")?"MULTI":"SINGLE");
        obj.addProperty("fpTag",session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG));
    }

    private void getSearch(){
        stop = true;
        progressBar.setVisibility(View.VISIBLE);
        final int count = mSearchArrayList.size();
        String offset = String.valueOf(count+1);

        SearchQueryApi searchQueryApi = Constants.restAdapter.create(SearchQueryApi.class);
        searchQueryApi.getQueries(offset, obj, new Callback<List<SearchQueryModel>>() {
            @Override
            public void success(List<SearchQueryModel> searchQueryModels, Response response) {
                progressBar.setVisibility(View.GONE);
                if(searchQueryModels == null || (searchQueryModels.size() == 0 && count == 0)){
                    emptySearchLayout.setVisibility(View.VISIBLE);
                    return;
                }

                for (int i =0; i<searchQueryModels.size() ;i++){
                    mSearchArrayList.add(searchQueryModels.get(i));
                    adapter.notifyItemChanged(count+i);
                }
                adapter.notifyDataSetChanged();
                stop = count>0 ? searchQueryModels.size()<11 : searchQueryModels.size()<10;
            }

            @Override
            public void failure(RetrofitError error) {
                if(count == 0){
                    emptySearchLayout.setVisibility(View.VISIBLE);
                }
                stop = false;
                progressBar.setVisibility(View.GONE);
                Methods.showSnackBarNegative(SearchQueriesActivity.this,getString(R.string.something_went_wrong_try_again));
            }
        });
    }*/
    @Override
    protected void onResume() {
        super.onResume();
        MixPanelController.track(EventKeysWL.SEARCH_QUERIES,null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_queries, menu);
        return true;
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
            //getSupportFragmentManager().popBackStack();
            //  NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}

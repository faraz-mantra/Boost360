package com.nowfloats.Analytics_Screen;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nowfloats.Analytics_Screen.API.SearchQueryApi;
import com.nowfloats.Analytics_Screen.Search_Query_Adapter.SearchQueryAdapter;
import com.nowfloats.Analytics_Screen.model.SearchQueryModel;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NotificationCenter.AlertArchive;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Kamal on 17-02-2015.
 */
public class SearchQueries extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    private static RecyclerView.Adapter adapter;
    private LinearLayoutManager layoutManager;
    public LinearLayout emptySearchLayout;
    UserSessionManager session;
    ArrayList<SearchQueryModel> mSearchArrayList = new ArrayList<>();
    JSONObject obj;
    private boolean stop = false;
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
        session = new UserSessionManager(getApplicationContext(),SearchQueries.this);
        TextView titleTextView = (TextView) toolbar.findViewById(R.id.titleTextView);
        titleTextView.setText(getResources().getString(R.string.search_queries));

        emptySearchLayout = (LinearLayout)findViewById(R.id.emptysearchlayout);
        recyclerView = (RecyclerView) findViewById(R.id.search_queries_recycler_view);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new SearchQueryAdapter(SearchQueries.this,mSearchArrayList);
        new AlertArchive(Constants.alertInterface,"SEARCHQUERIES",session.getFPID());
        recyclerView.setAdapter(adapter);
        createObj();
        getSearch();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                if(lastVisibleItem>=totalItemCount-1 && !stop){
                    Log.v("ggg","hello");
                   getSearch();
                }
            }
        });
    }

    private void createObj() {
        obj = new JSONObject();
        try {
            obj.put("clientId", Constants.clientId);
            obj.put("fpIdentifierType", session.getISEnterprise().equals("true")?"MULTI":"SINGLE");
            obj.put("fpTag",session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG));
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                    Log.v("ggg",i+" adapter");
                    adapter.notifyItemChanged(count+i);
                }
                adapter.notifyDataSetChanged();
                stop = searchQueryModels.size()<11 && count>0;
            }

            @Override
            public void failure(RetrofitError error) {
                if(count == 0){
                    emptySearchLayout.setVisibility(View.VISIBLE);
                }
                stop = false;
                progressBar.setVisibility(View.GONE);
                Methods.showSnackBarNegative(SearchQueries.this,getString(R.string.something_went_wrong_try_again));
            }
        });
    }
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

            BoostLog.d("Back", "Back Pressed");
            finish();
          overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            return true;
            //getSupportFragmentManager().popBackStack();
            //  NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }

}

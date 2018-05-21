package com.nowfloats.Analytics_Screen;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.nowfloats.Analytics_Screen.API.SearchQueryApi;
import com.nowfloats.Analytics_Screen.model.SearchAnalyticsSummaryForFP;
import com.nowfloats.Analytics_Screen.model.SearchQueryModel;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Kamal on 17-02-2015.
 */
public class SearchQueriesActivityNew extends AppCompatActivity implements View.OnClickListener {
    Toolbar toolbar;
    RecyclerView recyclerView;
    private static RecyclerView.Adapter adapter;
    private LinearLayoutManager layoutManager;
    public LinearLayout emptySearchLayout;
    UserSessionManager session;
    ArrayList<SearchQueryModel> mSearchArrayList = new ArrayList<>();
    JsonObject obj;
    private boolean stop = false;
    ProgressBar progressBar;
    private TextView tvTotalSearchQueries;
    private TextView tvTotalImpressions;
    private TextView tvTotalClicks;
    private TextView tvCTR;
    private Button btnViewMore;
    private UserSessionManager mSession;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_queries_frame_layout_new);

        toolbar = (Toolbar) findViewById(R.id.search_queries_action_bar);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportFragmentManager().beginTransaction().add(R.id.fm_search_report, new SearchReportFragment()).commit();
        session = new UserSessionManager(getApplicationContext(), SearchQueriesActivityNew.this);
        TextView titleTextView = (TextView) toolbar.findViewById(R.id.titleTextView);
        titleTextView.setText(getResources().getString(R.string.search_queries));

        tvTotalSearchQueries = findViewById(R.id.tv_total_no_of_searchQueries);
        tvTotalImpressions = findViewById(R.id.tv_Total_no_of_impressions);
        tvTotalClicks = findViewById(R.id.tv_Total_no_of_clicks);
        tvCTR = findViewById(R.id.tv_ctr);
        btnViewMore = findViewById(R.id.btn_view_more);
        btnViewMore.setOnClickListener(this);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
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
        MixPanelController.track(EventKeysWL.SEARCH_QUERIES, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_queries, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_view_more) {

        }
    }
}

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
import android.widget.TextView;

import com.nowfloats.Analytics_Screen.Search_Query_Adapter.SearchQueryAdapter;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NotificationCenter.AlertArchive;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

/**
 * Created by Kamal on 17-02-2015.
 */
public class SearchQueries extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    public static LinearLayout emptySearchLayout;
    UserSessionManager session;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_queries_frame_layout);

        toolbar = (Toolbar) findViewById(R.id.search_queries_action_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        session = new UserSessionManager(getApplicationContext(),SearchQueries.this);
        TextView titleTextView = (TextView) toolbar.findViewById(R.id.titleTextView);
        titleTextView.setText("Search Queries");

        emptySearchLayout = (LinearLayout)findViewById(R.id.emptysearchlayout);
        recyclerView = (RecyclerView) findViewById(R.id.search_queries_recycler_view);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new SearchQueryAdapter(SearchQueries.this);
        new AlertArchive(Constants.alertInterface,"SEARCHQUERIES",session.getFPID());
        if(adapter.getItemCount()==0){
            emptySearchLayout.setVisibility(View.VISIBLE);
        }else {
            emptySearchLayout.setVisibility(View.GONE);
        }
        recyclerView.setAdapter(adapter);
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

            Log.d("Back", "Back Pressed");
            finish();
          overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            //getSupportFragmentManager().popBackStack();
            //  NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }

}

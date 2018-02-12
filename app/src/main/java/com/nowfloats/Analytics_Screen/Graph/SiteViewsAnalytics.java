package com.nowfloats.Analytics_Screen.Graph;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.nowfloats.Analytics_Screen.Graph.fragments.UniqueVisitorsFragment;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static com.nowfloats.Analytics_Screen.Graph.fragments.UniqueVisitorsFragment.pattern;

/**
 * Created by Admin on 17-01-2018.
 */

public class SiteViewsAnalytics extends AppCompatActivity implements UniqueVisitorsFragment.ViewCallback, View.OnClickListener {


    private TextView tvMonth, tvWeek, tvYear;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null) {
            setTitle("Unique Visitors");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        tvMonth = findViewById(R.id.tv_month_tab);
        tvWeek = findViewById(R.id.tv_week_tab);
        tvYear = findViewById(R.id.tv_year_tab);

        tvMonth.setOnClickListener(this);
        tvWeek.setOnClickListener(this);
        tvYear.setOnClickListener(this);

        Bundle b = new Bundle();
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        HashMap<String, String> map = new HashMap<>();
        map.put("endDate", Methods.getFormattedDate(c.getTimeInMillis(),pattern));

        int weekDay = c.get(Calendar.DAY_OF_WEEK);
        int month = c.get(Calendar.MONTH);
        c.add(Calendar.DAY_OF_MONTH,-((weekDay-c.getFirstDayOfWeek()+7)%7));
        if (month == c.get(Calendar.MONTH)){
            map.put("startDate",String.format(Locale.ENGLISH,"%s/%02d/%02d",c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1,c.get(Calendar.DAY_OF_MONTH)));
        }else{
            map.put("startDate",String.format(Locale.ENGLISH,"%s/%02d/%s",c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1,"01"));
        }
        b.putInt("pos",0);
        b.putSerializable("hashmap",map);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fl_analytics_fragment, UniqueVisitorsFragment.getInstance(b))
                .commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_week_tab:
                changeTab("week");
                break;
            case R.id.tv_month_tab:
                changeTab("month");
                break;
            case R.id.tv_year_tab:
                changeTab("year");
                break;
        }
    }

    @TargetApi(21)
    private void changeTab(String viewType) {
        tvWeek.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.white));
        tvMonth.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.white));
        tvYear.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.white));

        tvWeek.setTextColor(ContextCompat.getColor(this, R.color.dark_grey));
        tvMonth.setTextColor(ContextCompat.getColor(this, R.color.dark_grey));
        tvYear.setTextColor(ContextCompat.getColor(this, R.color.dark_grey));

        Bundle b = new Bundle();
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        HashMap<String, String> map = new HashMap<>();
        map.put("endDate", Methods.getFormattedDate(c.getTimeInMillis(),pattern));
        int position = 0;
        switch (viewType) {
            case "week":
                position = 0;
                tvWeek.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.dark_grey));
                tvWeek.setTextColor(ContextCompat.getColor(this, R.color.white));
                int weekDay = c.get(Calendar.DAY_OF_WEEK);
                int month = c.get(Calendar.MONTH);
                c.add(Calendar.DAY_OF_MONTH,-((weekDay-c.getFirstDayOfWeek()+7)%7));
                if (month == c.get(Calendar.MONTH)){
                    map.put("startDate",String.format(Locale.ENGLISH,"%s/%02d/%02d",c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1,c.get(Calendar.DAY_OF_MONTH)));
                }else{
                    map.put("startDate",String.format(Locale.ENGLISH,"%s/%02d/%s",c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1,"01"));
                }
                break;
            case "month":
                position = 1;
                tvMonth.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.dark_grey));
                tvMonth.setTextColor(ContextCompat.getColor(this, R.color.white));
                map.put("startDate",String.format(Locale.ENGLISH,"%s/%02d/%s",c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1,"01"));
                break;
            case "year":
                position = 2;
                tvYear.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.dark_grey));
                tvYear.setTextColor(ContextCompat.getColor(this, R.color.white));
                map.put("startDate",String.format(Locale.ENGLISH,"%s/%s/%s",c.get(Calendar.YEAR),"01","01"));
                break;
        }
        b.putInt("pos",position);
        b.putSerializable("hashmap",map);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_analytics_fragment, UniqueVisitorsFragment.getInstance(b))
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onChartBarClicked(HashMap<String, String> map) {
        FragmentManager manager = getSupportFragmentManager();
        Bundle b = new Bundle();
        b.putInt("pos", UniqueVisitorsFragment.BatchType.valueOf(map.get("batchType")).val);
        b.putSerializable("hashmap",map);
        manager.beginTransaction()
                .add(R.id.fl_analytics_fragment, UniqueVisitorsFragment.getInstance(b),map.get("batchType"))
                .addToBackStack(null)
                .commit();
    }


}

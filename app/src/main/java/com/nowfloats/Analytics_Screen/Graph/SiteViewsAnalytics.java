package com.nowfloats.Analytics_Screen.Graph;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

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

public class SiteViewsAnalytics extends AppCompatActivity implements UniqueVisitorsFragment.ViewCallback {

    String[] tabs;
    PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);
        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null) {
            setTitle("Unique Visitors");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        ViewPager pager = (ViewPager) findViewById(R.id.viewpager_main);
        tabs=getResources().getStringArray(R.array.tabs);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(pager);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {

        PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle b=new Bundle();
            Calendar c= Calendar.getInstance();
            c.setFirstDayOfWeek(Calendar.MONDAY);
            HashMap<String, String> map = new HashMap<>();
            map.put("endDate", Methods.getFormattedDate(c.getTimeInMillis(),pattern));
            switch (position) {
                case 0:
                    int weekDay = c.get(Calendar.DAY_OF_WEEK);
                    int month = c.get(Calendar.MONTH);
                    c.add(Calendar.DAY_OF_MONTH,-((weekDay-c.getFirstDayOfWeek()+7)%7));
                    if (month == c.get(Calendar.MONTH)){
                        map.put("startDate",String.format(Locale.ENGLISH,"%s/%02d/%02d",c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1,c.get(Calendar.DAY_OF_MONTH)));
                    }else{
                        map.put("startDate",String.format(Locale.ENGLISH,"%s/%02d/%s",c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1,"01"));
                    }

                    break;
                case 1:
                    map.put("startDate",String.format(Locale.ENGLISH,"%s/%02d/%s",c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1,"01"));
                    break;
                case 2:
                    map.put("startDate",String.format(Locale.ENGLISH,"%s/%s/%s",c.get(Calendar.YEAR),"01","01"));
                    break;
                default:
                    return null;
            }
            b.putInt("pos",position);
            b.putSerializable("hashmap",map);
            return UniqueVisitorsFragment.getInstance(b);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }

        @Override
        public int getCount() {
            return tabs.length;
        }
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
                .add(R.id.activity_main_analytics, UniqueVisitorsFragment.getInstance(b),map.get("batchType"))
                .addToBackStack(null)
                .commit();
    }


}

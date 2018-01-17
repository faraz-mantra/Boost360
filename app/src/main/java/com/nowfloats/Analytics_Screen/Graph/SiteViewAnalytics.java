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

import com.nowfloats.Analytics_Screen.Graph.api.AnalyticsFetch;
import com.nowfloats.Analytics_Screen.Graph.fragments.UniqueVisitorsFragment;
import com.nowfloats.Analytics_Screen.Graph.model.VisitsModel;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.nowfloats.Analytics_Screen.Graph.fragments.UniqueVisitorsFragment.pattern;

/**
 * Created by Admin on 17-01-2018.
 */

public class SiteViewAnalytics extends AppCompatActivity implements UniqueVisitorsFragment.ViewCallback {

    String[] tabs;
    PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);
        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null) {
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
            b.putInt("pos",position);
            Fragment monthFragment = null;
            switch (position) {
                case 0:
                    monthFragment = UniqueVisitorsFragment.getInstance(b);
                    break;
                case 1:
                    break;
                default:
                    break;
            }
            return monthFragment;
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
    public void callDataApi(HashMap<String, String> map, int position) {
        Calendar c= Calendar.getInstance();
        map.put("endDate", Methods.getFormattedDate(c.getTimeInMillis(),pattern));
        switch (UniqueVisitorsFragment.BatchType.valueOf(map.get("batchType"))){
            case dy:
                map.put("startDate",String.format(Locale.ENGLISH,"%s/%2d/%s",c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1,c.getFirstDayOfWeek()));
                break;
            case yy:
                return;
            case ww:
                map.put("startDate",String.format(Locale.ENGLISH,"%s/%2d/%s",c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1,"01"));
                break;
            case mm:
                map.put("startDate",String.format(Locale.ENGLISH,"%s/%s/%s",c.get(Calendar.YEAR),"01","01"));
                break;
        }

        FragmentData(map,position);
    }

    @Override
    public void onPressChartBar(HashMap<String, String> map) {
        FragmentManager manager = getSupportFragmentManager();
        Bundle b = new Bundle();
        b.putInt("pos", UniqueVisitorsFragment.BatchType.valueOf(map.get("batchType")).ordinal());
        b.putSerializable("hashmap",map);
        manager.beginTransaction()
                .add(UniqueVisitorsFragment.getInstance(b),map.get("batchType"))
                .addToBackStack(null)
                .commit();
    }

    private void FragmentData(HashMap<String,String> map, final int fragPos){
        UserSessionManager manager = new UserSessionManager(this,this);
        map.put("clientId", Constants.clientId);
        map.put("scope",manager.getISEnterprise().equals("true") ? "Enterprise" : "Store");
        final UniqueVisitorsFragment frag = (UniqueVisitorsFragment) pagerAdapter.getItem(fragPos);
        Constants.testRestAdapter.create(AnalyticsFetch.FetchDetails.class)
                .getUniqueVisits(manager.getFpTag(), map, new Callback<VisitsModel>() {
                    @Override
                    public void success(VisitsModel visitsModel, Response response) {

                        frag.updateData(visitsModel);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        frag.updateData(null);
                    }
                });
    }
}

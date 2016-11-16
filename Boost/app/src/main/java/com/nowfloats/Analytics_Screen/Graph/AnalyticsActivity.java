package com.nowfloats.Analytics_Screen.Graph;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.nowfloats.Analytics_Screen.Graph.api.AnalyticsFetch;
import com.nowfloats.Analytics_Screen.Graph.database.SaveDataCounts;
import com.nowfloats.Analytics_Screen.Graph.fragments.MonthFragment;
import com.nowfloats.Analytics_Screen.Graph.model.DashboardDetails;
import com.nowfloats.Analytics_Screen.Graph.model.DashboardResponse;
import com.nowfloats.Analytics_Screen.Graph.model.DatabaseModel;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Constants;
import com.thinksity.R;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AnalyticsActivity extends AppCompatActivity {

    public String[] tabs;
    int[] months;
    int[] weeks;
    int[] days;
    String pattern = "MM-dd-yyyy";
    int curYear,curWeek,curMonth,curDay,curDate;
    List<DashboardResponse.Entity> entityList;
    ViewPager pager;
    public boolean rowExist=true;
    AnalyticsAdapter analyticsAdapter;
    private final static String endpoint = " https://api.withfloats.com";
    public int yearData,monthData,weekData;
    private String startDate="01-01-2016",endDate ;
    ContentLoadingProgressBar progressBar;
    PagerSlidingTabStrip pagerSlidingTabStrip;
    Toolbar toolbar;
    private UserSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);
        inIt();
        final Calendar c = Calendar.getInstance();
        curYear = c.get(Calendar.YEAR);
        curWeek = c.get(Calendar.WEEK_OF_MONTH);
        curMonth = c.get(Calendar.MONTH);
        curDay=c.get(Calendar.DAY_OF_WEEK);
        curDate=c.get(Calendar.DAY_OF_MONTH);
        months=new int[curMonth+1];
        days=new int[curDay];
        weeks=new int[curWeek];

        session = new UserSessionManager(getApplicationContext(), this);

        endDate =new SimpleDateFormat(pattern, Locale.ENGLISH).format(new Date());
        DashboardDetails details=new DashboardDetails();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
        SaveDataCounts saveDataCounts =new SaveDataCounts(AnalyticsActivity.this);
        DatabaseModel modelResponse =saveDataCounts.getDataArrays();
        if(modelResponse==null)
        {
            rowExist=false;
            startDate="01-01-"+curYear;
            details.setData(session.getFpTag(), Constants.clientId,startDate,endDate );
            Log.v("ggg",startDate);
            getEntityList(details);
        }else
        {
            String date=modelResponse.getDate();
            rowExist=true;
            Calendar dbCalender = new SimpleDateFormat(pattern, Locale.ENGLISH).getCalendar();
            startDate =new SimpleDateFormat(pattern, Locale.ENGLISH).format(new Date(Long.valueOf(date)));
            dbCalender.setTimeInMillis(Long.valueOf(date));
            int dbyear = dbCalender.get(Calendar.YEAR);
            int dbweekOfMonth = dbCalender.get(Calendar.WEEK_OF_MONTH);
            int dbdayOfWeek = dbCalender.get(Calendar.DAY_OF_WEEK);
            int dbday = dbCalender.get(Calendar.DAY_OF_MONTH);
            int dbmonth = dbCalender.get(Calendar.MONTH);

            DashboardDetails detail=new DashboardDetails();
            if(curYear!=dbyear)
            {
                startDate = "01-01-" + curYear;
            }else if(curMonth!=dbmonth)
            {
                if(curMonth>dbmonth)
                {
                    months = Arrays.copyOf(modelResponse.getYear(), curMonth + 1);
                    yearData = addArray(months);
                }
                else
                {
                    startDate = curMonth + 1 + "-01-" + curYear;
                    months = Arrays.copyOf(modelResponse.getYear(), curMonth + 1);
                    months[curMonth] = 0;
                    yearData = addArray(months);
                }
            }else if(curWeek!=dbweekOfMonth)
            {
                if(curWeek>dbweekOfMonth)
                {
                    months = Arrays.copyOf(modelResponse.getYear(), curMonth + 1);
                    yearData = addArray(months);
                    weeks= Arrays.copyOf(modelResponse.getMonth(),curWeek);
                    monthData=addArray(weeks);
                }
                else
                {
                    startDate = curMonth + 1 + "-01-" + curYear;
                    months = Arrays.copyOf(modelResponse.getYear(), curMonth + 1);
                    months[curMonth] = 0;
                    yearData = addArray(months);
                }
            }else if(dbdayOfWeek!=curDay)
            {
                if(dbdayOfWeek<curDay)
                {
                    months = Arrays.copyOf(modelResponse.getYear(), curMonth + 1);
                    yearData = addArray(months);
                    weeks= Arrays.copyOf(modelResponse.getMonth(),curWeek);
                    monthData=addArray(weeks);
                    days=Arrays.copyOf(modelResponse.getWeek(),curDay);
                    weekData=addArray(days);
                }
                else
                {
                    startDate = curMonth + 1 + "-01-" + curYear;
                    months = Arrays.copyOf(modelResponse.getYear(), curMonth + 1);
                    months[curMonth] = 0;
                    yearData = addArray(months);
                }
            }else
            {
                weeks= modelResponse.getMonth();
                months= modelResponse.getYear();
                days=modelResponse.getWeek();
                yearData=modelResponse.getYearCount();
                monthData=modelResponse.getMonthCount();
                weekData=modelResponse.getWeekCount();
                setPagerAdapter();
                return;
            }
            Log.v("ggg",startDate);
            detail.setData(session.getFpTag(), Constants.clientId, startDate, endDate);
            getEntityList(detail);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id==android.R.id.home){
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
        return super.onOptionsItemSelected(item);
    }

    private void inIt() {
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.visits));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        pager = (ViewPager) findViewById(R.id.viewpager_main);
        tabs=getResources().getStringArray(R.array.tabs);
        pagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.vpagertabstrip_main);
        pagerSlidingTabStrip.setTextColor(Color.WHITE);
        pagerSlidingTabStrip.setDividerColor(Color.WHITE);
        progressBar= (ContentLoadingProgressBar) findViewById(R.id.progressBar_analytic);
        setProgressVisible();
    }
    private int addArray(int[] array){
        int sum=0;
        for(int x:array)
            sum+=x;
        return sum;
    }
    private void setProgressVisible() {
        progressBar.setVisibility(View.VISIBLE);
    }
    private void setProgressHide() {
        progressBar.setVisibility(View.GONE);
    }

    private void getEntityList(DashboardDetails dashboardDetails) {
        //Log.v("ggg","getentity");
        HashMap<String, String> map=new HashMap<>();
        map.put("clientId",dashboardDetails.getClientId());
        map.put("startDate",dashboardDetails.getStartDate());
        map.put("endDate",dashboardDetails.getEndDate());

        RestAdapter adapter = new RestAdapter.Builder().setEndpoint(endpoint).build();
        AnalyticsFetch.FetchDetails details = adapter.create(AnalyticsFetch.FetchDetails.class);
        details.getDataCount(dashboardDetails.getfpTag(),map, new Callback<DashboardResponse>() {
            @Override
            public void success(final DashboardResponse dashboardResponse, Response response) {
                //Log.v("ggg",startDate+"success"+endDate);
                new Task(dashboardResponse).execute();
            }

            @Override
            public void failure(RetrofitError error) {
                SaveDataCounts saveDataCounts =new SaveDataCounts(AnalyticsActivity.this);
                DatabaseModel modelResponse =saveDataCounts.getDataArrays();
                weeks= modelResponse.getMonth();
                months= modelResponse.getYear();
                days=modelResponse.getWeek();
                yearData=modelResponse.getYearCount();
                monthData=modelResponse.getMonthCount();
                weekData=modelResponse.getWeekCount();
                setPagerAdapter();
                Toast.makeText(AnalyticsActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
               // Log.e("error",error+"");
            }
        });
    }
    class Task extends AsyncTask<Void,Void,Void>{

        DashboardResponse dashboardResponse;
        Task(DashboardResponse dashboardResponse){
            this.dashboardResponse=dashboardResponse;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            if (dashboardResponse == null||dashboardResponse.getEntity()==null){
                return null;
            }
            Log.v("ggg","task");
            for(DashboardResponse.Entity list :dashboardResponse.getEntity()) {

                String s = list.getCreatedDate().substring(list.getCreatedDate().indexOf('(')+1,
                        list.getCreatedDate().indexOf(')') - 5);
                Calendar c= new SimpleDateFormat(pattern, Locale.ENGLISH).getCalendar();
                c.setTimeInMillis(Long.valueOf(s));
                int year = c.get(Calendar.YEAR);
                int weekOfMonth=c.get(Calendar.WEEK_OF_MONTH);
                int dayOfWeek=c.get(Calendar.DAY_OF_WEEK);
                //Log.e("ggg","doinbackground week_of_month ="+ weekOfMonth+"day_of_week = "+dayOfWeek);
                int day = c.get(Calendar.DAY_OF_MONTH);
                int  month = c.get(Calendar.MONTH);
                if(year==curYear)
                {
                    months[month]+=list.getDataCount();
                    yearData+=list.getDataCount();
                    if (month == curMonth)
                    {
                        monthData+=list.getDataCount();
                        weeks[weekOfMonth-1] += list.getDataCount();
                        if (weekOfMonth == curWeek)
                        {
                            weekData+=list.getDataCount();
                            days[dayOfWeek-1] += list.getDataCount();
                        }
                    }
                }
               // Log.v("ggg",c.get(Calendar.DATE)+" month "+month+" day_month "+day+" data "+months[month]);
            }
            DatabaseModel model=new DatabaseModel();
            model.setYear(months);
            model.setMonth(weeks);
            model.setWeek(days);
            model.setMonthCount(monthData);
            model.setYearCount(yearData);
            model.setWeekCount(weekData);
            model.setDate(String.valueOf(Calendar.getInstance().getTimeInMillis()));
            SaveDataCounts saveDataCounts = new SaveDataCounts(AnalyticsActivity.this);
            if(rowExist)
                saveDataCounts.updateDataCount(model);
            else
                saveDataCounts.addDataCount(model);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setPagerAdapter();
        }
    }

    private void setPagerAdapter() {
        setProgressHide();
        analyticsAdapter = new AnalyticsAdapter(getSupportFragmentManager());
        pager.setAdapter(analyticsAdapter);
        pagerSlidingTabStrip.setViewPager(pager);
    }

    class AnalyticsAdapter extends FragmentStatePagerAdapter {

        public AnalyticsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return  MonthFragment.newInstance(days,weekData,0);
                case 1:
                    return MonthFragment.newInstance(weeks,monthData,1);
                default:
                    return  MonthFragment.newInstance(months,yearData,2);
            }
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
}

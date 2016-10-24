package com.nowfloats.Analytics_Screen.Graph;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.thinksity.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by tushar on 20-05-2015.
 */
public class MonthDetails extends ActionBarActivity implements View.OnClickListener{

    static BarChart barChart;
    final int MODE_WEEK = 0;
    final int MODE_MONTH = 1;
    final int MODE_YEAR = 2;
    int currentMode;
    static CallingTask callingTask;
    UserSessionManager session;

    String[] dayNameArray = getResources().getStringArray(R.array.weeks);
    String[] weekNameArray = getResources().getStringArray(R.array.week_with_num);
    String[] monthNameArray = getResources().getStringArray(R.array.months);
    int[] colorArray = { Color.rgb(123,123,123),Color.rgb(100,100,100),Color.rgb(130,130,130),
            Color.rgb(115,115,115),Color.rgb(135,135,135),Color.rgb(135,135,135),
            Color.rgb(105,105,105),Color.rgb(123,120,110) };


    ArrayList<String> dateArray;
    ArrayList<Integer> valueArray;
    ArrayList<String> tempDateArray;
    ArrayList<Integer> tempValueArray;
    TextView currentNumber,dottedLine;
    static TextView[] currentNumbers;

    TextView abTitle;
    ImageButton homeButton;
    int month;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setTheme(R.style.AppTheme);
        setContentView(R.layout.graph_fragment_two);

        session = new UserSessionManager(getApplicationContext(),MonthDetails.this);

        android.support.v7.app.ActionBar ab = getSupportActionBar();
       if(ab != null )
       ab.hide();

        barChart = (BarChart) findViewById(R.id.chart);
        currentNumber = (TextView) findViewById(R.id.currentNumber);
        abTitle = (TextView) findViewById(R.id.abTitle);
        dottedLine = (TextView) findViewById(R.id.dottedLine);
        homeButton = (ImageButton) findViewById(R.id.homeButton);

        abTitle.setText(getMonthName(getIntent().getIntExtra("month",0)));

        homeButton.setOnClickListener(this);


        initialiseGraph(barChart);
        dateArray = getIntent().getStringArrayListExtra("dateArray");

        callingTask = new CallingTask();
        currentMode = MODE_MONTH;
        callingTask.execute(MODE_MONTH+"");





    }

    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.homeButton:
                finish();
                break;
        }
    }

    private String getMonthName(int month)
    {
        String[] months = getResources().getStringArray(R.array.months_with_full_name);

        return months[month-1];
    }

    private void initialiseGraph(BarChart barChart)
    {
        barChart.setDrawGridBackground(false);
        barChart.setDescription("");
        barChart.getLegend().setEnabled(false);
        barChart.setDrawBorders(false);
        barChart.setDragEnabled(false);
        barChart.setScaleEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setDoubleTapToZoomEnabled(false);
        //  barChart.setHighlightEnabled(false);

        XAxis xaxis = barChart.getXAxis();
        YAxis leftAxis = barChart.getAxisLeft();
        YAxis rightAxis = barChart.getAxisRight();

        xaxis.setTextSize(10);
        xaxis.setDrawGridLines(false);
        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xaxis.setLabelsToSkip(0);

        leftAxis.setDrawGridLines(false);
        rightAxis.setDrawGridLines(false);
        leftAxis.setEnabled(false);
        rightAxis.setEnabled(false);
        leftAxis.setTextColor(Color.argb(0, 0, 0, 0));
        rightAxis.setTextColor(Color.argb(0,0,0,0));

    }

    private void setupGraph(int mode,ArrayList<String> dateArray,ArrayList<Integer> valueArray,BarChart barChart)
    {
        if(mode == MODE_WEEK)
        {
            ArrayList<BarEntry> entryList = new ArrayList<BarEntry>();
            int l = valueArray.size();
            if(l>7)
                l = 7;
            for(int i=0; i<l; i++)
                entryList.add(new BarEntry(valueArray.get(i),i));
            for(int i=l; i<7; i++)
                entryList.add(new BarEntry(0,i));

            BarDataSet barDataSet = new BarDataSet(entryList,"");
            barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            barDataSet.setColors(colorArray);
            //barDataSet.setValueTextColor(Color.argb(0,0,0,0));

            ArrayList<BarDataSet> barDataSetArray = new ArrayList<BarDataSet>();
            barDataSetArray.add(barDataSet);

            BarData barData = new BarData(dayNameArray,barDataSetArray);
            barChart.setData(barData);
            barChart.animateY(1200);



        }
        else if(mode == MODE_MONTH)
        {
            ArrayList<BarEntry> entryList = new ArrayList<BarEntry>();
            int[] weekWiseNumbers = { 0,0,0,0 };
            for(int i=0; i<valueArray.size(); i++)
            {
                int k = i/7;
                if(k!=4)
                    weekWiseNumbers[k] += valueArray.get(i);
                else
                    weekWiseNumbers[3] += valueArray.get(i);
            }
            for(int i=0; i<4; i++)
                entryList.add(new BarEntry(weekWiseNumbers[i],i));

            BarDataSet barDataSet = new BarDataSet(entryList,"");
            barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            barDataSet.setColors(colorArray);
            //barDataSet.setValueTextColor(Color.argb(0,0,0,0));


            ArrayList<BarDataSet> barDataSetArray = new ArrayList<BarDataSet>();
            barDataSetArray.add(barDataSet);

            BarData barData = new BarData(weekNameArray,barDataSetArray);
            barData.setValueFormatter(new CustomValueFormatter());

            barChart.setData(barData);
            barChart.animateY(1200);



        }
        else if(mode == MODE_YEAR)
        {
            ArrayList<BarEntry> entryList = new ArrayList<BarEntry>();
            int[] monthWiseNumbers = { 0,0,0,0,0,0,0,0,0,0,0,0 };
            for(int i=0; i<valueArray.size(); i++)
            {
                int k = Integer.parseInt(dateArray.get(i).substring(5,7)) - 1;
                monthWiseNumbers[k] += valueArray.get(i);
            }
            for(int i=0; i<12; i++)
                entryList.add(new BarEntry(monthWiseNumbers[i],i));

            BarDataSet barDataSet = new BarDataSet(entryList,"");
            barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            barDataSet.setColors(colorArray);
            // barDataSet.setValueTextColor(Color.argb(0,0,0,0));


            ArrayList<BarDataSet> barDataSetArray = new ArrayList<BarDataSet>();
            barDataSetArray.add(barDataSet);

            BarData barData = new BarData(monthNameArray,barDataSetArray);
            barChart.setData(barData);
            barChart.animateY(1200);



        }

    }




    private ArrayList<String> getWeekEndPoints()
    {
        String s;

        Calendar calendar = Calendar.getInstance();
        int month  = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int noOfDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int dayNumber = getDayNumber(calendar.get(Calendar.DAY_OF_WEEK));

        int startDay = day - dayNumber;
        int endDay;
        int startMonth,endMonth;
        int startYear,endYear;

        //change in month but not in year
        if(startDay<=0)
        {
            if(noOfDays == 30 || month == 7)
                startDay = 31 + startDay;
            else
                startDay = 30 + startDay;

            endDay = day + (6-dayNumber);

            if(month == Calendar.JANUARY)
            {
                startYear = year-1;
                endYear = year;
                startMonth = 11;
                endMonth = 0;
            }
            else
            {
                startYear = endYear = year;
                startMonth = month-1;
                endMonth = month;
            }

        }
        else if(startDay+6>noOfDays)
        {
            endDay = startDay+6-noOfDays;
            if(month == Calendar.DECEMBER)
            {
                startYear = year;
                endYear = year+1;
                startMonth = 11;
                endMonth = 0;
            }
            else
            {
                startYear = endYear = year;
                startMonth = month;
                endMonth = month+1;
            }
        }
        else
        {
            startDay = day - dayNumber;
            endDay = startDay+6;
            startMonth = endMonth = month;
            startYear = endYear = year;
        }

        s = startDay+"."+(startMonth+1)+"."+startYear+"-"+endDay+"."+(endMonth+1)+"."+endYear;
        // weekButton.setText(s);
        return getDateArray(startDay,startMonth+1,startYear,endDay,endMonth+1,endYear);


    }

    private int getDayNumber(int dn)
    {
        int d = 0;
        switch(dn)
        {
            case Calendar.SUNDAY:
                d = 0;
                break;
            case Calendar.MONDAY:
                d = 1;
                break;
            case Calendar.TUESDAY:
                d = 2;
                break;
            case Calendar.WEDNESDAY:
                d = 3;
                break;
            case Calendar.THURSDAY:
                d = 4;
            case Calendar.FRIDAY:
                d = 5;
                break;
            case Calendar.SATURDAY:
                d = 6;
                break;
        }
        return d;
    }


    private ArrayList<String> getMonthEndPoints()
    {
        Calendar calendar = Calendar.getInstance();
        int month  = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int startDay,endDay;
        int startMonth,endMonth;
        int startYear,endYear;

        startYear = endYear = year;
        startMonth = month+1;
        startDay = 1;
        endDay = day;
        endMonth = month+1;

        //return startDay+"."+(startMonth+1)+"."+startYear+"-"+endDay+"."+(endMonth+1)+"."+endYear;
        return getDateArray(startDay,startMonth,startYear,endDay,endMonth,endYear);

    }


    private ArrayList<String> getYearEndPoints()
    {
        Calendar calendar = Calendar.getInstance();
        int month  = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int startDay,endDay;
        int startMonth,endMonth;
        int startYear,endYear;

        startYear = endYear = year;
        startMonth = 1;
        startDay = 1;
        endDay = day;
        endMonth = month;

        //return startDay+"."+(startMonth)+"."+startYear+"-"+endDay+"."+(endMonth+1)+"."+endYear;
        return getDateArray(startDay,startMonth,startYear,endDay,endMonth+1,endYear);

    }

    private ArrayList<String> getDateArray(int sDay,int sMonth,int sYear,int eDay,int eMonth,int eYear)
    {
        ArrayList<String> dateArray = new ArrayList<String>();
        if(sMonth == eMonth)
        {
            for(int i=sDay; i<=eDay; i++)
                dateArray.add(String.format("%04d-%02d-%02d",sYear,sMonth,i));
        }
        else if(sYear==eYear && sMonth != eMonth )
        {
            for(int i=sMonth; i<=eMonth; i++)
            {
                if(i == sMonth)
                {
                    for(int j=sDay,l=noOfDaysInMonth(sMonth);j<=l; j++)

                        dateArray.add(String.format("%04d-%02d-%02d", sYear, sMonth, j));
                }
                else if(i==eMonth)
                {
                    for(int j=1; j<=eDay; j++)
                        dateArray.add(String.format("%04d-%02d-%02d",sYear,eMonth,j));
                }
                else
                {
                    for(int j=1,l=noOfDaysInMonth(i);j<=l; j++)
                        dateArray.add(String.format("%04d-%02d-%02d",sYear,i,j));
                }

            }
        }
        else
        {
            for(int i=sDay,l=noOfDaysInMonth(sMonth); i<=l; i++)
                dateArray.add(String.format("%04d-%02d-%02d",sYear,sMonth,i));
            for(int i=1; i<=eDay; i++)
                dateArray.add(String.format("%04d-%02d-%02d",eYear,eMonth,i));
        }

        return dateArray;

    }

    private int noOfDaysInMonth(int m)
    {
        switch(m)
        {
            case 2: return 28;
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12: return 31;
            case 4:
            case 6:
            case 9:
            case 11: return 30;

        }

        return 0;
    }


    private class CallingTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... mode) {
            // For storing data from web service

            Calendar calendar = Calendar.getInstance();
            int month  = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            int day = calendar.get(Calendar.DAY_OF_MONTH);


            currentMode = Integer.parseInt(mode[0]);
            String endDate;
            String data = "";

            try {

                String startDate = URLEncoder.encode(dateArray.get(0), "UTF-8");
                if(currentMode == MODE_WEEK && PageFragment.customWeek == false)
                    endDate = URLEncoder.encode(String.format("%04d-%02d-%02d",year,month+1,day), "UTF-8");
                else
                    endDate = URLEncoder.encode(dateArray.get(dateArray.size()-1), "UTF-8");

                String clientID = Constants.clientId;//URLEncoder.encode("DB96EA35A6E44C0F8FB4A6BAA94DB017C0DFBE6F9944B14AA6C3C48641B3D70", "UTF-8");
                String starting = Constants.NOW_FLOATS_API_URL+"/Dashboard/v1/"+session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG)+"/details?";
                String detailsType = URLEncoder.encode("0", "UTF-8");
                String scope = URLEncoder.encode("0", "UTF-8");

                // Building the url to the web service
                String url = starting + "clientId=" + clientID + "&" + "startDate=" + startDate + "&" + "endDate=" + endDate
                        + "&detailstype=" + detailsType + "&scope=" + scope;



                try {
                    // Fetching the data from we service
                    data = downloadUrl(url);
                } catch (Exception e) {
                    BoostLog.d("Background Task", e.toString());
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Creating ParserTask
            //tv.setText("Result: "+result);
            ParserTask pt = new ParserTask();
            pt.execute(result);


        }
    }


    private class ParserTask extends AsyncTask<String, Integer, ArrayList<Integer>>{

        JSONObject jObject;

        @Override
        protected ArrayList<Integer> doInBackground(String... jsonData) {

            ArrayList<Integer> valArray = null;
            ArrayList<String> datesFound = null;

            VisitCountParser visitCountParser = new VisitCountParser();

            try{
                jObject = new JSONObject(jsonData[0]);

                // Getting the parsed data as a List construct
                valArray = visitCountParser.parse(jObject,dateArray);

            }catch(Exception e){
                BoostLog.d("Exception",e.toString());
            }
            return valArray;
        }

        @Override
        protected void onPostExecute(ArrayList<Integer>  result) {

            if(result!=null)
            {
                String s = "";
                for (int i = 0; i < result.size(); i++)
                    s = s + result.get(i) + " " + dateArray.get(i) + "\n";

                valueArray = result;

                int total = 0;
                for (int i : valueArray)
                    total += i;
                currentNumber.setText(total + "");
                if(PageFragment.currentNumbers[currentMode] != null)
                PageFragment.currentNumbers[currentMode].setText(total+"");
                if (currentMode == MODE_WEEK) {
                    dottedLine.setText("............................\n"+getResources().getString(R.string.visit_this_week));
                } else if (currentMode == MODE_MONTH) {
                    dottedLine.setText("............................\n"+getResources().getString(R.string.visit_this_month));
                } else if (currentMode == MODE_YEAR) {
                    dottedLine.setText("............................\n"+getResources().getString(R.string.visit_this_year));
                }

                setupGraph(currentMode, dateArray, valueArray,barChart);
                // tv.setText(s);
            }

        }
    }


    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            e.printStackTrace();
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }





}

package com.nowfloats.Analytics_Screen.Graph;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Highlight;
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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by tushar on 18-05-2015.
 */
public class PageFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";


    static BarChart[] barChart;
    final int MODE_WEEK = 0;
    final int MODE_MONTH = 1;
    final int MODE_YEAR = 2;
    int currentMode;
    static int currentMonth;
    static boolean customWeek = false;
    int totalCount;
    static TextView totalCountTv;
    static CallingTask callingTask;
    LinearLayout progressLayout ;

    String[] dayNameArray;
    String[] weekNameArray;
    String[] monthNameArray;
    int[] colorArray = { Color.rgb(123,123,123),Color.rgb(100,100,100),Color.rgb(130,130,130),
            Color.rgb(115,115,115),Color.rgb(135,135,135),Color.rgb(135,135,135),
            Color.rgb(105,105,105),Color.rgb(123,120,110) };


    ArrayList<String> dateArray;
    ArrayList<Integer> valueArray;
    ArrayList<String> tempDateArray;
    ArrayList<Integer> tempValueArray;
    TextView currentNumber,dottedLine;
    static TextView[] currentNumbers;
    static Context context;
    static ViewPager viewPager;
    UserSessionManager session;

    private int mPage;

    public static PageFragment newInstance(int page,Context c,ViewPager vp) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        context = c;
        barChart = new BarChart[3];
        PageFragment.currentNumbers = new TextView[3];
        viewPager = vp;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
        dayNameArray = getResources().getStringArray(R.array.weeks);
        weekNameArray = getResources().getStringArray(R.array.week_with_num);
        monthNameArray = getResources().getStringArray(R.array.months);
    }

    // Inflate the fragment layout we defined above for this fragment
    // Set the associated text for the title
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.graph_fragment_one, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        currentNumber = (TextView) view.findViewById(R.id.currentNumber);
        dottedLine = (TextView) view.findViewById(R.id.dottedLine);
        totalCountTv = (TextView) view.findViewById(R.id.totalNumber);
        progressLayout = (LinearLayout)view.findViewById(R.id.progress_layout);
        try{
            if(mPage == 1)
            {
                barChart[0] = (BarChart) view.findViewById(R.id.chart);
                PageFragment.currentNumbers[0] = currentNumber = (TextView) view.findViewById(R.id.currentNumber);
                dottedLine = (TextView) view.findViewById(R.id.dottedLine);
                // totalCountTv[0] = (TextView) view.findViewById(R.id.totalNumber);
                initialiseGraph(barChart[0]);
                dateArray = getWeekEndPoints();
//                GetWeekData ct = new CallingTask();
//                ct.execute(MODE_WEEK+"");
                new GetWeekData().execute();
                //GetTotalTask gtt = new GetTotalTask();
                //gtt.execute();
                barChart[0].setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                    @Override
                    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {


                    }


                    @Override
                    public void onNothingSelected() {

                    }
                });




            }
            else if(mPage == 2)
            {
                barChart[1] = (BarChart) view.findViewById(R.id.chart);
                PageFragment.currentNumbers[1] = currentNumber = (TextView) view.findViewById(R.id.currentNumber);

                // totalCountTv[1] = (TextView) view.findViewById(R.id.totalNumber);
                initialiseGraph(barChart[1]);
                dateArray = getMonthEndPoints();
                final CallingTask ct1 = new CallingTask();
                ct1.execute(MODE_MONTH+"");

                //GetTotalTask gtt = new GetTotalTask();
                //gtt.execute();
                barChart[1].setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                    @Override
                    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {}

                    @Override
                    public void onNothingSelected() {}
                });

            }
            else
            {
                barChart[2] = (BarChart) view.findViewById(R.id.chart);
                PageFragment.currentNumbers[2] = currentNumber = (TextView) view.findViewById(R.id.currentNumber);

                //  totalCountTv[2] = (TextView) view.findViewById(R.id.totalNumber);
                initialiseGraph(barChart[2]);
                dateArray = getYearEndPoints();
                CallingTask ct2 = new CallingTask();
                ct2.execute(MODE_YEAR+"");
                barChart[2].setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                    @Override
                    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                        int index = e.getXIndex();
                        PageFragment.currentMonth = index+1;
                        // barChart[1].setVisibility(View.INVISIBLE);

                        dateArray = getYearEndPoints();
                        int dateContainStatus = getDateContainStatus(dateArray,index+1);
                        if(dateContainStatus == 1) //contains start and end dates
                        {
                            dateArray = getMonthStartEndDates(index+1);
                        }
                        else if(dateContainStatus == 2) //contains only start date and not the end date
                        {
                            dateArray = getMonthEndPoints();
                        }
                        else if(dateContainStatus == 3) //doesn't contains the start and end date both
                        {
                            Toast.makeText(context, getResources().getString(R.string.no_info_avail), Toast.LENGTH_LONG).show();
                        }

                        if(dateContainStatus!=3)
                        {

                            Intent i = new Intent("MonthDetails");
                            i.putExtra("dateArray",dateArray);
                            i.putExtra("month",index+1);
                            //startActivity(i);

                        }
                    }

                    @Override
                    public void onNothingSelected() {

                    }
                });
            }
        }catch(Exception e){e.printStackTrace();}
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        session = new UserSessionManager(activity.getApplicationContext(),activity);
    }

    private ArrayList<String> getCustomWeekDates(int month,int week)
    {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        if(week!=4)
        {
            return getDateArray(((week-1)*7)+1,month,year,(((week-1)*7)+7),month,year);
        }
        else
        {
            return getDateArray(22,month,year,noOfDaysInMonth(month),month,year);
        }

    }



    private ArrayList<String> getMonthStartEndDates(int month)
    {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        return getDateArray(1,month,year,noOfDaysInMonth(month),month,year);
    }
    private int getDateContainStatus(ArrayList<String> dateArray,int month)
    {
        String startDate,endDate="";
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        startDate = String.format("%04d-%02d-%02d",year,month,1);
        switch(month)
        {
            case 1:
                endDate = String.format("%04d-%02d-%02d",year,month,31);
                break;
            case 2:
                endDate = String.format("%04d-%02d-%02d",year,month,28);
                break;
            case 3:
                endDate = String.format("%04d-%02d-%02d",year,month,31);
                break;
            case 4:
                endDate = String.format("%04d-%02d-%02d",year,month,30);
                break;
            case 5:
                endDate = String.format("%04d-%02d-%02d",year,month,31);
                break;
            case 6:
                endDate = String.format("%04d-%02d-%02d",year,month,30);
                break;
            case 7:
                endDate = String.format("%04d-%02d-%02d",year,month,31);
                break;
            case 8:
                endDate = String.format("%04d-%02d-%02d",year,month,31);
                break;
            case 9:
                endDate = String.format("%04d-%02d-%02d",year,month,30);
                break;
            case 10:
                endDate = String.format("%04d-%02d-%02d",year,month,31);
                break;
            case 11:
                endDate = String.format("%04d-%02d-%02d",year,month,30);
                break;
            case 12:
                endDate = String.format("%04d-%02d-%02d",year,month,31);
                break;
        }
        if(dateArray.contains(startDate) && dateArray.contains(endDate))
            return 1;
        else if(dateArray.contains(startDate))
            return 2;
        return 3;
    }

    private void initialiseGraph(BarChart barChart)
    {
        progressLayout.setVisibility(View.VISIBLE);


        barChart.setDrawGridBackground(false);
        barChart.setDescription("");
        barChart.getLegend().setEnabled(false);
        barChart.setDrawBorders(false);
        barChart.setDragEnabled(false);
        barChart.setNoDataText("");
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
        leftAxis.setValueFormatter(new CustomValueFormatter());
        rightAxis.setValueFormatter(new CustomValueFormatter());

    }

    private void setupGraph(int mode,ArrayList<String> dateArray,ArrayList<Integer> valueArray,BarChart barChart)
    {
        progressLayout.setVisibility(View.GONE);
        Typeface customFont;
        customFont = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Medium.ttf");
        if(mode == MODE_WEEK)
        {
            ArrayList<BarEntry> entryList = new ArrayList<BarEntry>();
            int l = valueArray.size();
            if(l>7)
                l = 7;

            for(int i=0; i<dateArray.size(); i++) {
                entryList.add(new BarEntry(valueArray.get(i), i));
                //BoostLog.d("ILUD Array:", String.valueOf(valueArray.get(i) + " " + dateArray.get(i)));
            }

            BarDataSet barDataSet = new BarDataSet(entryList,"");
            barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            barDataSet.setColors(colorArray);
            barDataSet.setValueTextSize(12.0f);
            barDataSet.setValueTypeface(customFont);
            //barDataSet.setValueTextColor(Color.argb(0,0,0,0));

            ArrayList<BarDataSet> barDataSetArray = new ArrayList<BarDataSet>();
            barDataSetArray.add(barDataSet);

            String[] newDayArray = new String[entryList.size()];
            for(int i= 0; i<dateArray.size();i++){
                newDayArray[i] = dayNameArray[i];
            }

            BarData barData = new BarData(newDayArray,barDataSetArray);
            barData.setValueFormatter(new CustomValueFormatter());

            barChart.setData(barData);
            barChart.animateY(1200);

            CustomMarkerClass mv = new CustomMarkerClass (context, R.layout.graph_custom_marker_view,dateArray,valueArray,MODE_WEEK);
            // set the marker to the chart
            //barChart[MODE_WEEK].setMarkerView(mv);
            for(int i=0 ; i<dateArray.size(); i++){
                BoostLog.d("ILUD DATE Array:", dateArray.get(i));
            }
            for(int i =0 ; i<valueArray.size(); i++){
                BoostLog.d("ILUD DATE Array:", valueArray.get(i) + "");
            }


        }
        else if(mode == MODE_MONTH)
        {
            ArrayList<BarEntry> entryList = new ArrayList<BarEntry>();
            int[] weekWiseNumbers = { -1,-1,-1,-1 };
            for(int i=0; i<valueArray.size(); i++)
            {
                int k = i/7;
                if(k!=4)
                    weekWiseNumbers[k] += valueArray.get(i);
                else
                    weekWiseNumbers[3] += valueArray.get(i);
            }
            for(int i=0; i<4; i++) {
                if(weekWiseNumbers[i]!=-1)
                        entryList.add(new BarEntry(weekWiseNumbers[i], i));
            }

            String[] newWeekArray = new String[entryList.size()];
            for(int i=0; i<entryList.size(); i++)
                newWeekArray[i] = weekNameArray[i];

            BarDataSet barDataSet = new BarDataSet(entryList,"");
            barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            barDataSet.setColors(colorArray);
            barDataSet.setValueTextSize(12.0f);
            barDataSet.setValueTypeface(customFont);
            //barDataSet.setValueTextColor(Color.argb(0,0,0,0));


            ArrayList<BarDataSet> barDataSetArray = new ArrayList<BarDataSet>();
            barDataSetArray.add(barDataSet);

            BarData barData = new BarData(newWeekArray,barDataSetArray);
            barData.setValueFormatter(new CustomValueFormatter());

            barChart.setData(barData);
            barChart.animateY(1200);

            CustomMarkerClass mv = new CustomMarkerClass (context, R.layout.graph_custom_marker_view,dateArray,valueArray,MODE_MONTH);
            // set the marker to the chart
            //barChart[MODE_MONTH].setMarkerView(mv);


        }
        else if(mode == MODE_YEAR)
        {
            boolean sizeBoolean = false;
            ArrayList<BarEntry> entryList = new ArrayList<BarEntry>();
            //int[] monthWiseNumbers = { 0,0,0,0,0,0,0,0,0,0,0,0 };
            int[] monthWiseNumbers = { -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1 };
            //List<Integer> monthData = new ArrayList<>();
            //BoostLog.d("ILUD Year Mode", y.size()+"");
            for(int i=0; i<valueArray.size(); i++)
            {
                int k = Integer.parseInt(dateArray.get(i).substring(5,7)) - 1;
                monthWiseNumbers[k] += valueArray.get(i);
            }
            for(int i=0; i<12; i++){
                int lengthchk = (monthWiseNumbers[i]+"").length();
                if(lengthchk>=6) {sizeBoolean = true;}
                if(monthWiseNumbers[i]!=-1) {
                    entryList.add(new BarEntry(monthWiseNumbers[i], i));
                }
            }


            BarDataSet barDataSet = new BarDataSet(entryList,"");
            barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            barDataSet.setColors(colorArray);
            barDataSet.setValueTypeface(customFont);
            BoostLog.d("Boo---",""+sizeBoolean);
            if (sizeBoolean) {
                barDataSet.setValueTextSize(7.0f);
            }else {
                barDataSet.setValueTextSize(10.0f);
            }
           // barDataSet.setValueTextColor(Color.argb(0,0,0,0));
            String[] newMonthArray = new String[entryList.size()];
            for(int i=0; i<entryList.size(); i++){
                newMonthArray[i] = monthNameArray[i];
            }

            ArrayList<BarDataSet> barDataSetArray = new ArrayList<BarDataSet>();
            barDataSetArray.add(barDataSet);
            BarData barData = new BarData(newMonthArray,barDataSetArray);
            barData.setValueFormatter(new CustomValueFormatter());

            barChart.setData(barData);
            barChart.animateY(1200);

            CustomMarkerClass mv = new CustomMarkerClass (context, R.layout.graph_custom_marker_view,dateArray,valueArray,MODE_YEAR);
            // set the marker to the chart
            //barChart[MODE_YEAR].setMarkerView(mv);

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

    private class GetWeekData extends AsyncTask<String, Void, String>{

        String data = null;

        @Override
        protected String doInBackground(String... params) {

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat currDf = new SimpleDateFormat("yyyy-MM-dd");
            Date currentdate = calendar.getTime();
            String currDate = currDf.format(currentdate);

            int curDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            int diff = (curDayOfWeek - calendar.getFirstDayOfWeek());
            Date date = calendar.getTime();
            date.setDate(calendar.getTime().getDate()-diff);
            String firstDateOfWeek = currDf.format(date);
            String[] firstdateOfWeekArray = firstDateOfWeek.split("-");
            String[] currdateOfWeek = currDate.split("-");
            dateArray = getDateArray(Integer.parseInt(firstdateOfWeekArray[2]), Integer.parseInt(firstdateOfWeekArray[1]),Integer.parseInt(firstdateOfWeekArray[0]),
                    Integer.parseInt(currdateOfWeek[2]),Integer.parseInt(currdateOfWeek[1]),Integer.parseInt(currdateOfWeek[0]) );
            BoostLog.d("ILUD Dates:", currDate + "   " + firstDateOfWeek);
            try {
                String startDate = URLEncoder.encode(firstDateOfWeek, "UTF-8");
                String endDate = URLEncoder.encode(String.format(currDate), "UTF-8");
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
            }catch (UnsupportedEncodingException e){
                e.printStackTrace();
            }


            return data;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Creating ParserTask
            //tv.setText("Result: "+result);
            try{
                ParserTask pt = new ParserTask();
                pt.execute(result);
            }catch(Exception e){e.printStackTrace();}



        }
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
            try{
                ParserTask pt = new ParserTask();
                pt.execute(result);
            }catch(Exception e){e.printStackTrace();}



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
            try{
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
                    if(PageFragment.currentNumbers[currentMode] != null )
                        PageFragment.currentNumbers[currentMode].setText(total+"");
                    if (currentMode == MODE_WEEK) {
                        dottedLine.setText("............................\n"+getResources().getString(R.string.visit_this_week));
                    } else if (currentMode == MODE_MONTH) {
                        dottedLine.setText("............................\n"+getResources().getString(R.string.visit_this_month));
                    } else if (currentMode == MODE_YEAR) {
                        dottedLine.setText("............................\n"+getResources().getString(R.string.visit_this_year));
                    }

                    setupGraph(currentMode, dateArray, valueArray,barChart[currentMode]);
                    // tv.setText(s);
                }
            }catch(Exception e){e.printStackTrace();}
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


    private class GetTotalTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... mode) {
            // For storing data from web service

            String data = "";

            try {

                String clientID = URLEncoder.encode("DB96EA35A6E44C0F8FB4A6BAA94DB017C0DFBE6F9944B14AA6C3C48641B3D70", "UTF-8");
                String starting = Constants.NOW_FLOATS_API_URL+"/Dashboard/v1/TECHNEWS/details?";
                String detailsType = URLEncoder.encode("0", "UTF-8");
                String scope = URLEncoder.encode("0", "UTF-8");

                // Building the url to the web service
                //String url = starting + "clientId=" + clientID + "&" + "startDate=" + startDate + "&" + "endDate=" + endDate
                  //      + "&detailstype=" + detailsType + "&scope=" + scope;
            String url = "https://api.withfloats.com/Dashboard/v1/TECHNEWS/summary?clientId=DB96EA35A6E44C0F8FB4A6BAA94DB017C0DFBE6F9944B14AA6C3C48641B3D70&fpId=TECHNEWS&scope=0";


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
            TotalParserTask pt = new TotalParserTask();
            pt.execute(result);


        }
    }


    private class TotalParserTask extends AsyncTask<String, Integer, Integer>{

        JSONObject jObject;
        int totalCount;

        @Override
        protected Integer doInBackground(String... jsonData) {


           TotalCountParser totalCountParser = new TotalCountParser();

            try{
                jObject = new JSONObject(jsonData[0]);

                // Getting the parsed data as a List construct
                totalCount = totalCountParser.parse(jObject);

            }catch(Exception e){
                BoostLog.d("Exception",e.toString());
            }
            return totalCount;
        }

        @Override
        protected void onPostExecute(Integer  result) {

            totalCount = result;
            totalCountTv.setText(result + " "+getResources().getString(R.string.total_visits));


        }
    }


}
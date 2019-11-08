package com.nowfloats.Analytics_Screen.Graph.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.nowfloats.util.BoostLog;
import com.thinksity.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


/**
 * Created by Abhi on 11/8/2016.
 */

public class MonthFragment extends Fragment {
    BarChart graph;
    int[] data;
    String[] days, months, weeks, shortArray;
    Context mContext;
    public final static String PARAMETER1 = "count", PARAMETER2 = "visit", PARAMETER3 = "frag", PARAMETER4 = "title", MONTH_PARAMETER = "month";
    String visitsThisWhat, title;
    public TextView visitsTitle, visitsCount;
    public int visitValue, frag;
    private OnYearDataClickListener onYearDataClickListener;
    private String titleMain;
    private boolean flag;

    public static MonthFragment newInstance(Bundle b) {
        MonthFragment monthFragment = new MonthFragment();
        monthFragment.setArguments(b);
        return monthFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null) {
            //Log.v("ggg","argument is null");
            return;
        }
        data = this.getArguments().getIntArray(PARAMETER1);
        frag = this.getArguments().getInt(PARAMETER3);
        visitValue = this.getArguments().getInt(PARAMETER2);
        titleMain = this.getArguments().getString(PARAMETER4);
        if (frag == 0) {
            title = getString(R.string.day);
            visitsThisWhat = titleMain;
            days = getResources().getStringArray(R.array.days);
            shortArray = Arrays.copyOfRange(days, 0, data.length);
        } else if (frag == 1) {
            title = getString(R.string.week);
            visitsThisWhat = titleMain;
            weeks = getResources().getStringArray(R.array.weeks);
            shortArray = Arrays.copyOfRange(weeks, 0, data.length);
            getWeeksAccordingToMonth(getArguments().getInt(MONTH_PARAMETER));
        } else {
            title = getString(R.string.Month);
            visitsThisWhat = titleMain;
            months = getResources().getStringArray(R.array.months);
            shortArray = Arrays.copyOfRange(months, 0, data.length);
        }

        //message("oncreate"+title);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analytics_main, container, false);
        visitsCount = (TextView) view.findViewById(R.id.tv_visit_count);
        visitsTitle = (TextView) view.findViewById(R.id.tv_visits_title);
        visitsTitle.setText(visitsThisWhat);
        visitsCount.setText(String.valueOf(visitValue));
        graph = (BarChart) view.findViewById(R.id.graph);
        //message("oncreateview"+title);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        //message("onAttach "+title);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (data == null) return;
        BarData data = new BarData(getDataSet());
        data.setValueFormatter(new MyYAxisValueFormatter());
        data.setValueTextSize(10);
        graph.setData(data);
        graph.setDrawGridBackground(false);
//        graph.getLegend().setEnabled(false);
//        graph.setDrawBorders(false);
//        graph.setDragEnabled(true);
        graph.setScaleXEnabled(true);
        graph.setScaleYEnabled(false);
        graph.setDoubleTapToZoomEnabled(false);
        graph.getAxisLeft().setAxisMinValue(0);
        graph.getAxisLeft().setSpaceBottom(0);
        XAxis xaxis = graph.getXAxis();
        YAxis leftAxis = graph.getAxisLeft();
        YAxis rightAxis = graph.getAxisRight();
        //leftAxis.setValueFormatter(new MyYAxisValueFormatter());
//
//        xaxis.setTextSize(100);
//        xaxis.setYOffset(-50);
//        xaxis.setXOffset(-1*(xaxis.mLabelWidth/2));
        xaxis.setDrawGridLines(false);
        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xaxis.setLabelsToSkip(0);
//
        leftAxis.setDrawGridLines(false);
        rightAxis.setDrawGridLines(false);
        leftAxis.setEnabled(false);
        rightAxis.setEnabled(false);
        leftAxis.setTextColor(Color.argb(0, 0, 0, 0));
        rightAxis.setTextColor(Color.argb(0, 0, 0, 0));
//        graph.setDescription("");
        graph.animateXY(1000, 1000);
        if (frag == 2) {
            graph.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, Highlight h) {
                    BoostLog.d("Clicked index:", " " + e.getX());
                    if (onYearDataClickListener != null)
                        onYearDataClickListener.onYearDataClicked((int) e.getX());
                }

                @Override
                public void onNothingSelected() {

                }
            });
        }

        XAxis xAxis = graph.getXAxis();
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return shortArray[(int) value];
            }
        });
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setGranularityEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        Description description = new Description();
        description.setText("");
        graph.setDescription(description);

        graph.invalidate();

    }

    private List<IBarDataSet> getDataSet() {
        List<IBarDataSet> dataSets = null;

        List<BarEntry> valueSet1 = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            valueSet1.add(new BarEntry(i, data[i], ""));
        }
        BarDataSet barDataSet1 = new BarDataSet(valueSet1, title);
        barDataSet1.setColor(Color.GRAY);

        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);
        return dataSets;
    }

    public class MyYAxisValueFormatter implements IValueFormatter {
        private DecimalFormat mFormat;

        public MyYAxisValueFormatter() {
            mFormat = new DecimalFormat("#########");
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return mFormat.format(value);
        }
    }

    public void setYearDataListener(OnYearDataClickListener listener) {
        this.onYearDataClickListener = listener;
    }

    public interface OnYearDataClickListener {
        public void onYearDataClicked(int month);
    }

    public void getWeeksAccordingToMonth(int month) {
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setFirstDayOfWeek(Calendar.MONDAY);
        int end = -1;
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DATE, 1);
        int currMonth = calendar.get(Calendar.MONTH);
        int start = calendar.get(Calendar.DAY_OF_MONTH);
        flag = false;
        int lastDay = calendar.get(Calendar.DAY_OF_MONTH);
        while (currMonth == calendar.get(Calendar.MONTH)) {

            int day = calendar.get(Calendar.WEEK_OF_MONTH);
            lastDay = calendar.get(Calendar.DAY_OF_MONTH);
            if (day > data.length || day == 0) break;
            if (start == -1) {

                start = calendar.get(Calendar.DAY_OF_MONTH);
                end = -1;

            } else if (end == -1) {

                if (currentCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) && currentCalendar.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)) {
                    lastDay = calendar.get(Calendar.DAY_OF_MONTH);
                    break;

                } else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                    end = calendar.get(Calendar.DAY_OF_MONTH);
                    shortArray[day - 1] = getResources().getStringArray(R.array.months)[month - 1] + "(" + start + "-" + end + ")";
                    start = -1;
                }
            }
            calendar.add(Calendar.DATE, 1);
        }
        if (end == -1 && data.length > 0) {
            shortArray[data.length - 1] = getResources().getStringArray(R.array.months)[month - 1] + "(" + start + "-" + lastDay + ")";
            //Log.v("ggg",start+" end "+end);
        }

    }
}

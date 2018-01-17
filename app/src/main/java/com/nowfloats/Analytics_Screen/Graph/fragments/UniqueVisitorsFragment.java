package com.nowfloats.Analytics_Screen.Graph.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.nowfloats.Analytics_Screen.Graph.model.VisitsModel;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Admin on 17-01-2018.
 */

public class UniqueVisitorsFragment extends Fragment {
    private Context mContext;
    private int pos = -1;
    private  String dataType;
    private  BarChart graph;
    private String tabType;
    private  ProgressBar progressBar;
    private TextView visitsTitle,visitsCount;
    private String[] labels;
    public static String pattern = "yyyy/MM/dd";
    private VisitsModel currVisitsModel;
    private BatchType batchType;
    public enum BatchType
    {
        dy(0),
        ww(1),
        mm(2),
        yy(3);

        int val;
        BatchType(int v){
            val = v;
        }

    }
    public static Fragment getInstance(Bundle bundle){
        Fragment frag = new UniqueVisitorsFragment();
        frag.setArguments(bundle);
        return frag;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() ==null) return;
        pos = getArguments().getInt("pos");
        switch (pos){
            case 0:
                dataType = getString(R.string.day);
                tabType = getString(R.string.week);
                labels = getResources().getStringArray(R.array.days);
                batchType = BatchType.dy;
                break;
            case 1:
                dataType = getString(R.string.week);
                tabType = getString(R.string.Month);
                batchType = BatchType.ww;
                labels = getResources().getStringArray(R.array.weeks);
                break;
            case 2:
                dataType = getString(R.string.Month);
                tabType =  getString(R.string.Year);
                batchType = BatchType.mm;
                labels = getResources().getStringArray(R.array.months);
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_visits,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (!isAdded()) return;
        visitsCount= (TextView) view.findViewById(R.id.tv_visit_count);
        visitsTitle= (TextView) view.findViewById(R.id.tv_visits_title);
        progressBar = view.findViewById(R.id.progress_bar);
        visitsTitle.setText(String.format("Visits this %s",tabType));
        graph = (BarChart) view.findViewById(R.id.graph);
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
        leftAxis.setTextColor(Color.argb(0,0,0,0));
        rightAxis.setTextColor(Color.argb(0,0,0,0));
        graph.setDescription("");
        graph.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                BoostLog.d("Clicked index:", " "+ e.getXIndex());
                HashMap<String,String> map = new HashMap<String, String>();
                map.put("batchType",batchType.name());
                map.put("startDate", Methods.getFormattedDate(currVisitsModel.getUniqueVisitsList().get(e.getXIndex()).getStartDate(),pattern));
                map.put("endDate",Methods.getFormattedDate(currVisitsModel.getUniqueVisitsList().get(e.getXIndex()).getEndDate(),pattern));
                ((ViewCallback)mContext).onPressChartBar(map);
            }

            @Override
            public void onNothingSelected() {

            }
        });
        graph.invalidate();
        // customize for all kind of tab
        if(getArguments().containsKey("hashmap")){
            HashMap<String,String> map = (HashMap<String, String>) getArguments().getSerializable("hashmap");
            if (map == null) return;
            map.put("batchType",batchType.name());
            progressBar.setVisibility(View.VISIBLE);
            ((ViewCallback)mContext).callDataApi(map,pos);
        }
    }

    private void addDataToGraph(){
        switch (batchType){
            case dy:
                break;
            case yy:
                break;
            case ww:
                break;
            case mm:
                break;
        }
        BarData data = new BarData(labels, getDataSet());
        data.setValueFormatter(new MyYAxisValueFormatter());
        data.setValueTextSize(10);
        graph.setData(data);
        graph.notifyDataSetChanged();
        graph.invalidate();
    }
    private List<IBarDataSet> getDataSet() {
        List<IBarDataSet> dataSets = null;

        List<BarEntry> valueSet1 = new ArrayList<>();
        List<VisitsModel.UniqueVisitsList> data = currVisitsModel.getUniqueVisitsList();
        for(int i=0;i<data.size();i++)
        {

            valueSet1.add(new BarEntry(data.get(i).getDataCount(),i));
        }
        BarDataSet barDataSet1 = new BarDataSet(valueSet1, dataType);
        barDataSet1.setColor(Color.GRAY);

        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);
        return dataSets;
    }

    public class MyYAxisValueFormatter implements ValueFormatter
    {
        private DecimalFormat mFormat;
        public MyYAxisValueFormatter () {
            mFormat = new DecimalFormat("#########");
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return mFormat.format(value);
        }
    }
    public void updateData(VisitsModel visitsModel) {
        progressBar.setVisibility(View.GONE);
        if (visitsModel == null){
            Toast.makeText(mContext, getString(R.string.something_went_wrong_try_again), Toast.LENGTH_SHORT).show();
        }
        currVisitsModel = visitsModel;
        addDataToGraph();
        // update data here
    }

    public interface ViewCallback{
        void callDataApi(HashMap<String,String> map, int pos);
        void onPressChartBar(HashMap<String,String> map);
    }
}

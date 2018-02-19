package com.nowfloats.Analytics_Screen.Graph.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
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
import com.nowfloats.Analytics_Screen.Graph.api.AnalyticsFetch;
import com.nowfloats.Analytics_Screen.Graph.model.VisitsModel;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Admin on 17-01-2018.
 */

public class UniqueVisitorsFragment extends Fragment implements View.OnClickListener {
    private Context mContext;
    private  String dataType;
    private  BarChart graph;
    private String tabType;
    private UserSessionManager manager;
    private  ProgressBar progressBar;
    private TextView visitsTitle,visitsCount;
    private ArrayList<String> labels = new ArrayList<>(12);
    public static String pattern = "yyyy/MM/dd";
    private VisitsModel currVisitsModel;
    int totalVisits = -1;
    public BatchType batchType;

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_info:
                showInfoDialog();
                break;
        }
    }

    private void showInfoDialog() {
        String title="", content="";
        switch (batchType){
            case dy:
                title = "Unique Visitors in a week";
                content = "The graph depicts unique visitors in the particular week. Note that a " +
                        "visitor might visit on multiple days in the week and still, will be counted as a unique visitor only once for the entire week";
                break;
            case mm:
                content = "The graph depicts unique visitors in the particular year. Note that a " +
                        "visitor might visit on multiple days in the year and still, will be counted as a unique visitor only once for the entire month";
                title = "Unique Visitors in a year";
                break;
            case ww:
                content = "The graph depicts unique visitors in the particular month. Note that a " +
                        "visitor might visit on multiple days in the month and still, will be counted as a unique visitor only once for the entire month";
                title = "Unique Visitors in a month";
                break;
        }

        new MaterialDialog.Builder(mContext)
                .title(title)
                .content(content)
                .iconRes(R.drawable.icon_info)
                .maxIconSize(Methods.dpToPx(15,mContext))
                .show();
    }

    public enum BatchType
    {
        dy(0),
        ww(1),
        mm(2),
        yy(3);

        public int val;
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
        int pos = getArguments().getInt("pos");
        totalVisits = getArguments().getInt("totalViews");
        switch (pos){
            case 0:
                dataType = getString(R.string.day);
                tabType = getString(R.string.week);
                batchType = BatchType.dy;
                break;
            case 1:
                dataType = getString(R.string.week);
                tabType = getString(R.string.Month);
                batchType = BatchType.ww;
                break;
            case 2:
                dataType = getString(R.string.Month);
                tabType =  getString(R.string.Year);
                batchType = BatchType.mm;
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
        if (!isAdded() || isDetached()) return;
        manager = new UserSessionManager(mContext,getActivity());
        visitsCount= (TextView) view.findViewById(R.id.tv_visit_count);
        visitsTitle= (TextView) view.findViewById(R.id.tv_visits_title);
        progressBar = view.findViewById(R.id.progress_bar);
        view.findViewById(R.id.img_info).setOnClickListener(this);
        visitsTitle.setText(String.format("unique visits this %s",tabType));
        if (totalVisits != 0){
            visitsCount.setText(String.valueOf(totalVisits));
        }else if(getArguments().containsKey("hashmap")){
                HashMap<String,String> map = (HashMap<String, String>) getArguments().getSerializable("hashmap");
                fetchTotalViews((HashMap<String, String>) map.clone());
        }
        graph = (BarChart) view.findViewById(R.id.graph);
        graph.setDrawGridBackground(false);
//        graph.getLegend().setEnabled(false);
//        graph.setDrawBorders(false);
//        graph.setDragEnabled(true);
        graph.setScaleXEnabled(true);
        graph.setScaleYEnabled(false);
        graph.setDoubleTapToZoomEnabled(true);
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
                if (e.getVal()<=0){
                    // do not show for 0 data
                    return;
                }
                HashMap<String,String> map = new HashMap<String, String>();
                switch (batchType){
                    case mm:
                        map.put("batchType",BatchType.ww.name());
                        break;
                    case ww:
                        map.put("batchType",BatchType.dy.name());
                        break;
                    case dy:
                    case yy:
                    default:
                        return;
                }
                map.put("startDate", getFormattedDate(currVisitsModel.getUniqueVisitsList().get(e.getXIndex()).getStartDate(),pattern));
                map.put("endDate",getFormattedDate(currVisitsModel.getUniqueVisitsList().get(e.getXIndex()).getEndDate(),pattern));
                ((ViewCallback)mContext).onChartBarClicked(map,(int)e.getVal());
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
            getFragmentData(map);
        }
    }

    private void fetchTotalViews(HashMap<String, String> map) {
        switch (batchType){
            case mm:
                map.put("batchType",BatchType.yy.name());
                break;
            case ww:
                map.put("batchType",BatchType.mm.name());
                break;
            case dy:
                map.put("batchType",BatchType.ww.name());
                break;
            case yy:
            default:
                return;
        }
        map.put("clientId", Constants.clientId);
        map.put("scope",manager.getISEnterprise().equals("true") ? "Enterprise" : "Store");
        Constants.testRestAdapter.create(AnalyticsFetch.FetchDetails.class)
                .getUniqueVisits(manager.getFpTag(), map, new Callback<VisitsModel>() {
                    @Override
                    public void success(VisitsModel visitsModel, Response response) {
                        int totalCount = 0;
                        if (visitsModel != null){
                            for (VisitsModel.UniqueVisitsList data:visitsModel.getUniqueVisitsList()){
                                totalCount+=data.getDataCount();
                            }
                        }
                        visitsCount.setText(String.valueOf(totalCount));
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        visitsCount.setText("0");
                        Toast.makeText(mContext, "unable to fetch total unique visits", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getFormattedDate(String date, String pattern) {
        if (TextUtils.isEmpty(date)) {
            return "";
        }
        return Methods.getFormattedDate(getMilliseconds(date),pattern);
    }
    private long getMilliseconds(String date){
        if (date.contains("/Date")) {
            date = date.replace("/Date(", "").replace("+0000)/", "");
        }
        return Long.valueOf(date);
    }
    private void addDataToGraph( List<IBarDataSet> dataSet){
        BarData data = new BarData(labels, dataSet);
        data.setValueFormatter(new MyYAxisValueFormatter());
        data.setValueTextSize(10);
        graph.setData(data);
        graph.setVisibleXRangeMaximum(5.2f);
        graph.notifyDataSetChanged();
        graph.animateXY(1000, 1000);
        graph.invalidate();
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
        if(!isAdded() || isDetached())
            return;
        progressBar.setVisibility(View.GONE);
        currVisitsModel = visitsModel;
        if (visitsModel == null){
            Toast.makeText(mContext, getString(R.string.something_went_wrong_try_again), Toast.LENGTH_SHORT).show();
            return;
        }
        addDataToGraph(getGraphDataSet());
        // update data here
    }

    private List<IBarDataSet> getGraphDataSet() {
        List<IBarDataSet> dataSets = null;

        List<BarEntry> valueSet1 = new ArrayList<>();
        List<VisitsModel.UniqueVisitsList> data = currVisitsModel.getUniqueVisitsList();
        labels.clear();
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        String[] months = getResources().getStringArray(R.array.months);
//        for (int i =0;i<12;i++){
//            valueSet1.add(new BarEntry(2000000000,i));
//            labels.add(months[i]);
//        }
        for(int i=0;i<data.size();i++)
        {
            valueSet1.add(new BarEntry(data.get(i).getDataCount(),i));
            c.setTimeInMillis(getMilliseconds(data.get(i).getStartDate()));
            switch (batchType){
                case ww:
                    int month = c.get(Calendar.MONTH);
                    int startDate = c.get(Calendar.DAY_OF_MONTH);
                    c.setTimeInMillis(getMilliseconds(data.get(i).getEndDate()));
                    labels.add(String.format(Locale.ENGLISH,"%d-%d %s'%d",startDate,c.get(Calendar.DAY_OF_MONTH),months[month],c.get(Calendar.YEAR)%100));
                    break;
                case mm:
                    labels.add(String.format(Locale.ENGLISH,"%s %d",months[c.get(Calendar.MONTH)],c.get(Calendar.YEAR)));
                    break;
                case dy:
                    labels.add(String.format(Locale.ENGLISH,"%d %s'%s",c.get(Calendar.DAY_OF_MONTH),months[c.get(Calendar.MONTH)],c.get(Calendar.YEAR)%100));
                    break;
            }
        }
        //visitsCount.setText(String.valueOf(totalCount));
        BarDataSet barDataSet1 = new BarDataSet(valueSet1, dataType);
        barDataSet1.setColor(Color.GRAY);

        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);
        return dataSets;
    }

    private void getFragmentData(final HashMap<String,String> map){

        map.put("clientId", Constants.clientId);
        map.put("scope",manager.getISEnterprise().equals("true") ? "Enterprise" : "Store");
        Constants.testRestAdapter.create(AnalyticsFetch.FetchDetails.class)
                .getUniqueVisits(manager.getFpTag(), map, new Callback<VisitsModel>() {
                    @Override
                    public void success(VisitsModel visitsModel, Response response) {
                       updateData(visitsModel);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        updateData(null);
                    }
                });
    }
    public interface ViewCallback{
        void onChartBarClicked(HashMap<String,String> map,int val);
    }
}

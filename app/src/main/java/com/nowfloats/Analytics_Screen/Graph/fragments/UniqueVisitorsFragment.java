package com.nowfloats.Analytics_Screen.Graph.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
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
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.nowfloats.Analytics_Screen.Graph.SiteViewsAnalytics;
import com.nowfloats.Analytics_Screen.Graph.api.AnalyticsFetch;
import com.nowfloats.Analytics_Screen.Graph.model.VisitsModel;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.VisitorsApiInterface;
import com.nowfloats.NavigationDrawer.model.VisitAnalytics;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.nowfloats.Analytics_Screen.Graph.SiteViewsAnalytics.VISITS_TYPE;

/**
 * Created by Admin on 17-01-2018.
 */

public class UniqueVisitorsFragment extends Fragment implements View.OnClickListener {
    private Context mContext;
    private String dataType;
    private BarChart graph;
    private String tabType;
    private UserSessionManager manager;
    private ProgressBar progressBar;
    private TextView visitsTitle, visitsCount;
    private ArrayList<String> labels = new ArrayList<>(12);
    public static String pattern = "yyyy/MM/dd";
    private VisitsModel currVisitsModel;
    int totalVisits = -1;
    int year;
    public BatchType batchType;
    public SiteViewsAnalytics.VisitsType mVisitType;
    private MaterialDialog.Builder materialDialog;


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_info:
                showInfoDialog();
                break;
        }
    }

    private void showInfoDialog() {
        String visitsName = "";
        String title;

        switch (mVisitType) {
            case TOTAL:
                visitsName = getString(R.string.overall_visits);
                break;
            case MAP_VISITS:
                visitsName = getString(R.string.map_visits);
                break;
            case UNIQUE:
                visitsName = getString(R.string.unique_visitors);
                break;
        }

        if(tabType.equals(getString(R.string.Year).toLowerCase()))
        {
            title = String.format("%s in %s %s", visitsName, tabType.toLowerCase(), year);
        }

        else
        {
            title = String.format("%s in a %s", visitsName, tabType.toLowerCase());
        }

        //String title = String.format("%s in a %s", visitsName, tabType.toLowerCase());

        String content = String.format(getString(mVisitType == SiteViewsAnalytics.VisitsType.UNIQUE ?
                R.string.unique_visitors_message : R.string.total_visits_message), tabType.toLowerCase());

        if (materialDialog == null) {
            materialDialog = new MaterialDialog.Builder(mContext)
                    .iconRes(R.drawable.icon_info)
                    .maxIconSize(Methods.dpToPx(15, mContext));
        }

        materialDialog.title(title);
        materialDialog.content(content);
        materialDialog.show();
    }

    public enum BatchType {
        dy(0),
        ww(1),
        mm(2),
        yy(3);

        public int val;

        BatchType(int v) {
            val = v;
        }

    }

    public static Fragment getInstance(Bundle bundle) {
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
        if (getArguments() == null) return;
        int pos = getArguments().getInt("pos");
        year = getArguments().getInt("year");
        totalVisits = getArguments().getInt("totalViews");
        mVisitType = (SiteViewsAnalytics.VisitsType) getArguments().getSerializable(VISITS_TYPE);
        switch (pos) {
            case 0:
                dataType = getString(R.string.day);
                tabType = getString(R.string.week).toLowerCase();
                batchType = BatchType.dy;
                break;
            case 1:
                dataType = getString(R.string.week);
                tabType = getString(R.string.Month).toLowerCase();
                batchType = BatchType.ww;
                break;
            case 2:
                dataType = getString(R.string.Month);
                tabType = getString(R.string.Year).toLowerCase();
                batchType = BatchType.mm;
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_visits, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (!isAdded() || isDetached()) return;
        manager = new UserSessionManager(mContext, getActivity());
        visitsCount = (TextView) view.findViewById(R.id.tv_visit_count);
        visitsTitle = (TextView) view.findViewById(R.id.tv_visits_title);
        progressBar = view.findViewById(R.id.progress_bar);
        view.findViewById(R.id.img_info).setOnClickListener(this);
        String visitsName = "";
        switch (mVisitType) {
            case TOTAL:
                visitsName = getString(R.string.overall_visits);
                break;
            case MAP_VISITS:
                visitsName = getString(R.string.map_visits);
                break;
            case UNIQUE:
                visitsName = getString(R.string.unique_visitors);
                break;
        }

        if(tabType.equals(getString(R.string.Year).toLowerCase()))
        {
            visitsTitle.setText(String.format("%s in %s %s", visitsName, tabType.toLowerCase(), year));
        }

        else
        {
            visitsTitle.setText(String.format("%s in a %s", visitsName, tabType.toLowerCase()));
        }

        if (totalVisits != 0) {
            visitsCount.setText(String.valueOf(totalVisits));
        } else if (getArguments().containsKey("hashmap")) {
            HashMap<String, String> map = (HashMap<String, String>) getArguments().getSerializable("hashmap");
            fetchTotalViews((HashMap<String, String>) map.clone());
        }
        graph = (BarChart) view.findViewById(R.id.graph);
        Paint p = graph.getPaint(Chart.PAINT_INFO);
        p.setColor(ContextCompat.getColor(mContext, R.color.primaryColor));
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
        leftAxis.setTextColor(Color.argb(0, 0, 0, 0));
        rightAxis.setTextColor(Color.argb(0, 0, 0, 0));
//        graph.setDescription("");

        graph.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                BoostLog.d("Clicked index:", " " + e.getX());
                if (e.getX() <= 0) {
                    // do not show for 0 data
                    return;
                }
                HashMap<String, String> map = new HashMap<String, String>();
                switch (batchType) {
                    case mm:
                        map.put("batchType", BatchType.ww.name());
                        break;
                    case ww:
                        map.put("batchType", BatchType.dy.name());
                        break;
                    case dy:
                    case yy:
                    default:
                        return;
                }
                map.put("startDate", Methods.getFormattedDate(currVisitsModel.getUniqueVisitsList().get((int) e.getX()).getStartDate(), pattern));
                map.put("endDate", Methods.getFormattedDate(currVisitsModel.getUniqueVisitsList().get((int) e.getX()).getEndDate(), pattern));
                ((ViewCallback) mContext).onChartBarClicked(map, (int) e.getX());
            }

            @Override
            public void onNothingSelected() {

            }
        });

        graph.invalidate();
        // customize for all kind of tab
        if (getArguments().containsKey("hashmap")) {
            HashMap<String, String> map = (HashMap<String, String>) getArguments().getSerializable("hashmap");
            if (map == null) return;
            map.put("batchType", batchType.name());
            progressBar.setVisibility(View.VISIBLE);
            getVisitsData(map, visitsModelCallback);
        }
    }

    private void fetchTotalViews(HashMap<String, String> map) {
        switch (batchType) {
            case mm:
                map.put("batchType", BatchType.yy.name());
                break;
            case ww:
                map.put("batchType", BatchType.mm.name());
                break;
            case dy:
                map.put("batchType", BatchType.ww.name());
                break;
            case yy:
            default:
                return;
        }
        getVisitsData(map, totalVisitsCallback);
    }

    private void addDataToGraph(BarDataSet dataSet) {
        BarData data = new BarData(dataSet);
        data.setValueFormatter(new MyYAxisValueFormatter());
        data.setValueTextSize(10);


        XAxis xAxis = graph.getXAxis();

        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setGranularityEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return labels.get((int) value);
            }
        });


        graph.setData(data);

        Description description = new Description();
        description.setText("");
        graph.setDescription(description);

        graph.getBarData().setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return Math.round(value) + "";
            }
        });

        graph.setVisibleXRangeMaximum(5.2f);
        graph.notifyDataSetChanged();
        graph.animateXY(1000, 1000);
        graph.invalidate();
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

    public void updateData(VisitsModel visitsModel) {
        if (!isAdded() || isDetached())
            return;
        progressBar.setVisibility(View.GONE);
        currVisitsModel = visitsModel;
        if (visitsModel == null) {
            Toast.makeText(mContext, getString(R.string.something_went_wrong_try_again), Toast.LENGTH_SHORT).show();
            return;
        }
        addDataToGraph(getGraphDataSet());
        // update data here
    }

    private BarDataSet getGraphDataSet() {
//        List<IBarDataSet> dataSets = null;

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
        for (int i = 0; i < data.size(); i++) {

            Log.d("DataSize", "" + data.size());

            valueSet1.add(new BarEntry(i, data.get(i).getDataCount(), ""));
            c.setTimeInMillis(Methods.getDateMillSecond(data.get(i).getStartDate()));
            switch (batchType) {
                case ww:
                    int month = c.get(Calendar.MONTH);
                    int startDate = c.get(Calendar.DAY_OF_MONTH);
                    c.setTimeInMillis(Methods.getDateMillSecond(data.get(i).getEndDate()));
                    labels.add(String.format(Locale.ENGLISH, "%d-%d %s'%d", startDate, c.get(Calendar.DAY_OF_MONTH), months[month], c.get(Calendar.YEAR) % 100));
                    break;
                case mm:
                    labels.add(String.format(Locale.ENGLISH, "%s %d", months[c.get(Calendar.MONTH)], c.get(Calendar.YEAR)));
                    break;
                case dy:
                    labels.add(String.format(Locale.ENGLISH, "%d %s'%s", c.get(Calendar.DAY_OF_MONTH), months[c.get(Calendar.MONTH)], c.get(Calendar.YEAR) % 100));
                    break;
            }
        }
        //visitsCount.setText(String.valueOf(totalCount));
        BarDataSet barDataSet1 = new BarDataSet(valueSet1, dataType);
        barDataSet1.setColor(Color.GRAY);

//        dataSets = new ArrayList<>();
//        dataSets.add(barDataSet1);
        return barDataSet1;
    }

    private void getVisitsData(HashMap<String, String> map, Callback<VisitsModel> callback) {

        Log.i("getVisitsData", "i am here");

        map.put("clientId", Constants.clientId);
        map.put("scope", manager.getISEnterprise().equals("true") ? "Enterprise" : "Store");
        AnalyticsFetch.FetchDetails visitsApi = Constants.restAdapter.create(AnalyticsFetch.FetchDetails.class);
        switch (mVisitType) {

            case UNIQUE:
                visitsApi.getUniqueVisits(manager.getFpTag(), map, callback);
                //getVisitsData();
                break;

            case TOTAL:
                visitsApi.getTotalVisits(manager.getFpTag(), map, callback);
                break;
            case MAP_VISITS:
                visitsApi.getMapVisits(manager.getFpTag(), map, callback);
                break;
        }
    }

    private Callback<VisitsModel> visitsModelCallback = new Callback<VisitsModel>() {
        @Override
        public void success(VisitsModel visitsModel, Response response) {

            int totalCount = 0;
            if (visitsModel != null) {
                for (VisitsModel.UniqueVisitsList data : visitsModel.getUniqueVisitsList()) {
                    totalCount += data.getDataCount();
                }
            }
            visitsCount.setText(String.valueOf(totalCount));
            Log.v("visitsModelCallback", "Success: " + totalCount);
            updateData(visitsModel);
        }

        @Override
        public void failure(RetrofitError error) {
            updateData(null);

            Log.v("visitsModelCallback", "Failure 0");
        }
    };

    private Callback<VisitsModel> totalVisitsCallback = new Callback<VisitsModel>() {
        @Override
        public void success(VisitsModel visitsModel, Response response) {
            /*int totalCount = 0;
            if (visitsModel != null) {
                for (VisitsModel.UniqueVisitsList data : visitsModel.getUniqueVisitsList()) {
                    totalCount += data.getDataCount();
                }
            }
            visitsCount.setText(String.valueOf(totalCount));*/
        }

        @Override
        public void failure(RetrofitError error) {
            visitsCount.setText("0");

            Log.v("visitsModelCallback", "Failure 1");
        }
    };

    public interface ViewCallback {
        void onChartBarClicked(HashMap<String, String> map, int val);
    }




    /**
     * CHIRANJIT
     */
    private void getVisitsData()
    {

        /*switch (batchType)
        {
            case dy:

                break;

            case mm:

                break;

            case ww:

                break;
        }*/


        Log.d("BATCH_TYPE", batchType.name());

        /**
         * Create calendar instance
         */
        Calendar calendar = Calendar.getInstance();

        /**
         * Get current date object from calendar and call method to convert to UTC
         */
        Date currentDate = localToGMT(calendar.getTime());

        /**
         * Subtract 2 months from current date
         */
        calendar.add(Calendar.MONTH, -2);

        /**
         * Get date object 2 months before
         */
        Date previousDate = localToGMT(calendar.getTime());

        /**
         * Format current date and previous date
         */
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        String startDate = df.format(previousDate);
        String endDate = df.format(currentDate);

        Log.d("VisitorsApiInterface", "Start Date : " + startDate);
        Log.d("VisitorsApiInterface", "End Date : " + endDate);

        List<String> array = new ArrayList<>();
        array.add(manager.getFPID());

        Log.d("VisitorsApiInterface", "FP ID : " + array);


        /**
         * Create HashMap for query string parameter
         */
        HashMap<String,String> map = new HashMap<>();

        map.put("clientId",Constants.clientId);
        map.put("startDate", startDate /*"2018-12-05"*/);
        map.put("endDate", endDate /*"2019-02-05"*/);
        map.put("batchType", "DAILY");
        map.put("scope", manager.getISEnterprise().equals("true") ? "1" : "0");

        //map.put("clientId", Constants.clientId);
        //map.put("scope", manager.getISEnterprise().equals("true") ? "Enterprise" : "Store");

        /**
         * Create object for retrofit API interface
         */
        VisitorsApiInterface visitors_interface = Constants.restAdapter.create(VisitorsApiInterface.class);

        visitors_interface.getVisitors(array, map, new Callback<List<VisitAnalytics>>() {

            @Override
            public void success(List<VisitAnalytics> visitAnalyticsList, retrofit.client.Response response)
            {
                /**
                 * Store visit count on session
                 */
                //sessionManager.setVisitsCount(String.valueOf(getTotalVisits(visitAnalyticsList)));
                /**
                 * Store visitors count on session
                 */
                //sessionManager.setVisitorsCount(String.valueOf(getTotalVisitors(visitAnalyticsList)));

                Log.d("VisitorsApiInterface", getString(R.string.total_analytics_data) + (visitAnalyticsList == null ? "NULL" : "" + visitAnalyticsList.size()));
                //Log.d("VisitorsApiInterface", "Total Visits - " + sessionManager.getVisitsCount());
                //Log.d("VisitorsApiInterface", "Total Visitors - " + sessionManager.getVisitorsCount());
            }

            @Override
            public void failure(RetrofitError error)
            {
                Log.d("VisitorsApiInterface", getString(R.string.fail_) + error.getMessage());
            }
        });


        //AnalyticsFetch.FetchDetails visitsApi = Constants.restAdapter.create(AnalyticsFetch.FetchDetails.class);

        /*switch (mVisitType)
        {
            case UNIQUE:
                visitsApi.getUniqueVisits(manager.getFpTag(), map, callback);
                break;
            case TOTAL:
                visitsApi.getTotalVisits(manager.getFpTag(), map, callback);
                break;
            case MAP_VISITS:
                visitsApi.getMapVisits(manager.getFpTag(), map, callback);
                break;
        }*/
    }


    /**
     * Convert local date to UTC date
     * @param date local date object
     * @return UTC date
     */
    private Date localToGMT(Date date)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        return new Date(sdf.format(date));
    }


    public void updateData1(List<VisitAnalytics> visitAnalyticsList) {
        if (!isAdded() || isDetached())
            return;
        progressBar.setVisibility(View.GONE);
        //currVisitsModel = visitsModel;
        if (visitAnalyticsList == null) {
            Toast.makeText(mContext, getString(R.string.something_went_wrong_try_again), Toast.LENGTH_SHORT).show();
            return;
        }
        addDataToGraph(getGraphDataSet1(visitAnalyticsList));
        // update data here
    }

    private BarDataSet getGraphDataSet1(List<VisitAnalytics> visitAnalyticsList) {
//        List<IBarDataSet> dataSets = null;

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
        for (int i = 0; i < data.size(); i++) {

            Log.d("DataSize", "" + data.size());

            valueSet1.add(new BarEntry(i, data.get(i).getDataCount(), ""));
            c.setTimeInMillis(Methods.getDateMillSecond(data.get(i).getStartDate()));
            switch (batchType) {
                case ww:
                    int month = c.get(Calendar.MONTH);
                    int startDate = c.get(Calendar.DAY_OF_MONTH);
                    c.setTimeInMillis(Methods.getDateMillSecond(data.get(i).getEndDate()));
                    labels.add(String.format(Locale.ENGLISH, "%d-%d %s'%d", startDate, c.get(Calendar.DAY_OF_MONTH), months[month], c.get(Calendar.YEAR) % 100));
                    break;
                case mm:
                    labels.add(String.format(Locale.ENGLISH, "%s %d", months[c.get(Calendar.MONTH)], c.get(Calendar.YEAR)));
                    break;
                case dy:
                    labels.add(String.format(Locale.ENGLISH, "%d %s'%s", c.get(Calendar.DAY_OF_MONTH), months[c.get(Calendar.MONTH)], c.get(Calendar.YEAR) % 100));
                    break;
            }
        }
        //visitsCount.setText(String.valueOf(totalCount));
        BarDataSet barDataSet1 = new BarDataSet(valueSet1, dataType);
        barDataSet1.setColor(Color.GRAY);

//        dataSets = new ArrayList<>();
//        dataSets.add(barDataSet1);
        return barDataSet1;
    }
}
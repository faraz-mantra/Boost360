package com.nowfloats.Analytics_Screen.Graph.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.nowfloats.Analytics_Screen.Graph.SiteViewsAnalytics;
import com.nowfloats.Analytics_Screen.Graph.api.AnalyticsFetch;
import com.nowfloats.Analytics_Screen.Graph.model.BotVisitResponse;
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

import static com.nowfloats.Analytics_Screen.Graph.SiteViewsAnalytics.VISITS_TYPE;
import static com.nowfloats.Analytics_Screen.Graph.SiteViewsAnalytics.VisitsType.OTHER_INSIGHTS;

/**
 * Created by Admin on 17-01-2018.
 */

public class UniqueVisitorsFragment extends Fragment implements View.OnClickListener {
    private Context mContext;
    private String dataType;
    private BarChart graph;
    private PieChart totalPieChart, botPieChart;
    private HorizontalScrollView hsvPieChart;
    private String tabType;
    private UserSessionManager manager;
    private ProgressBar progressBar;
    private TextView visitsTitle, visitsCount;
    private ArrayList<String> labels = new ArrayList<>(12);
    public static String pattern = "yyyy/MM/dd";
    private VisitsModel currVisitsModel;
    private BotVisitResponse botVisitResponse;
    int totalVisits = -1;
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
        String title = String.format("%s in a %s", visitsName, tabType.toLowerCase());

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

    int viewUpdateStatus = 0;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (!isAdded() || isDetached()) return;
        viewUpdateStatus = 0;
        manager = new UserSessionManager(mContext, getActivity());
        visitsCount = (TextView) view.findViewById(R.id.tv_visit_count);
        visitsTitle = (TextView) view.findViewById(R.id.tv_visits_title);
        progressBar = view.findViewById(R.id.progress_bar);
        view.findViewById(R.id.img_info).setOnClickListener(this);
        String visitsName = "";
        graph = (BarChart) view.findViewById(R.id.graph);
        hsvPieChart = (HorizontalScrollView) view.findViewById(R.id.hsvPieChart);
        totalPieChart = (PieChart) view.findViewById(R.id.totalPieChart);
        botPieChart = (PieChart) view.findViewById(R.id.botPieChart);

        totalPieChart.setLayoutParams(new LinearLayout.LayoutParams( getResources().getDisplayMetrics().widthPixels, LinearLayout.LayoutParams.MATCH_PARENT));
        botPieChart.setLayoutParams(new LinearLayout.LayoutParams( getResources().getDisplayMetrics().widthPixels, LinearLayout.LayoutParams.MATCH_PARENT));

        graph.setVisibility(View.VISIBLE);
        hsvPieChart.setVisibility(View.GONE);

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
            case OTHER_INSIGHTS:
                visitsName = getString(R.string.other_insights);
                graph.setVisibility(View.GONE);
                hsvPieChart.setVisibility(View.VISIBLE);
                break;
        }
        visitsTitle.setText(String.format("%s in a %s", visitsName, tabType.toLowerCase()));
        if (totalVisits != 0) {
            visitsCount.setText(String.valueOf(totalVisits));
        } else if (getArguments().containsKey("hashmap")) {
            HashMap<String, String> map = (HashMap<String, String>) getArguments().getSerializable("hashmap");
            fetchTotalViews((HashMap<String, String>) map.clone());
        }

        if (hsvPieChart.getVisibility() == View.VISIBLE) {
            // customize for all kind of tab
            if (getArguments().containsKey("hashmap")) {
                HashMap<String, String> map = (HashMap<String, String>) getArguments().getSerializable("hashmap");
                if (map == null) return;
                map.put("batchType", "YEARLY");
                progressBar.setVisibility(View.VISIBLE);
                getOtherInsightsData(map, botVisitResponseCallback);
            }
        } else {
            initGraph();
            // customize for all kind of tab
            if (getArguments().containsKey("hashmap")) {
                HashMap<String, String> map = (HashMap<String, String>) getArguments().getSerializable("hashmap");
                if (map == null) return;
                map.put("batchType", batchType.name());
                progressBar.setVisibility(View.VISIBLE);
                getVisitsData(map, visitsModelCallback);
            }
        }

    }

    private void initGraph() {
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
        graph.setDescription("");

        graph.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                BoostLog.d("Clicked index:", " " + e.getXIndex());
                if (e.getVal() <= 0) {
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
                map.put("startDate", Methods.getFormattedDate(currVisitsModel.getUniqueVisitsList().get(e.getXIndex()).getStartDate(), pattern));
                map.put("endDate", Methods.getFormattedDate(currVisitsModel.getUniqueVisitsList().get(e.getXIndex()).getEndDate(), pattern));
                ((ViewCallback) mContext).onChartBarClicked(map, (int) e.getVal());
            }

            @Override
            public void onNothingSelected() {

            }
        });
        graph.invalidate();
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

    private void addDataToGraph(List<IBarDataSet> dataSet) {
        BarData data = new BarData(labels, dataSet);
        data.setValueFormatter(new MyYAxisValueFormatter());
        data.setValueTextSize(10);
        graph.setData(data);
        graph.setVisibleXRangeMaximum(5.2f);
        graph.notifyDataSetChanged();
        graph.animateXY(1000, 1000);
        graph.invalidate();
    }

    public class MyYAxisValueFormatter implements ValueFormatter {
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
    }

    public void updateBotVisitsData(List<BotVisitResponse> botVisitResponse) {
        if (!isAdded() || isDetached())
            return;
        progressBar.setVisibility(View.GONE);
        if (botVisitResponse == null || botVisitResponse.size() == 0) {
            Toast.makeText(mContext, getString(R.string.something_went_wrong_try_again), Toast.LENGTH_SHORT).show();
            return;
        } else {
            this.botVisitResponse = botVisitResponse.get(0);
            updatePieView();
        }
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
        for (int i = 0; i < data.size(); i++) {
            valueSet1.add(new BarEntry(data.get(i).getDataCount(), i));
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

        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);
        return dataSets;
    }

    private void getVisitsData(HashMap<String, String> map, Callback<VisitsModel> callback) {
        map.put("clientId", Constants.clientId);
        map.put("scope", manager.getISEnterprise().equals("true") ? "Enterprise" : "Store");
        AnalyticsFetch.FetchDetails visitsApi = Constants.restAdapter.create(AnalyticsFetch.FetchDetails.class);
        switch (mVisitType) {
            case UNIQUE:
                visitsApi.getUniqueVisits(manager.getFpTag(), map, callback);
                break;
            case TOTAL:
                visitsApi.getTotalVisits(manager.getFpTag(), map, callback);
                break;
            case MAP_VISITS:
                visitsApi.getMapVisits(manager.getFpTag(), map, callback);
                break;
            case OTHER_INSIGHTS:
                visitsApi.getTotalVisits(manager.getFpTag(), map, callback);
                break;
        }
    }

    private void getOtherInsightsData(HashMap<String, String> map, Callback<List<BotVisitResponse>> callback) {
        map.put("clientId", Constants.clientId);
        map.put("scope", manager.getISEnterprise().equals("true") ? "Enterprise" : "Store");
        AnalyticsFetch.FetchDetails visitsApi = Constants.restAdapterApiV2.create(AnalyticsFetch.FetchDetails.class);
        switch (mVisitType) {
            case OTHER_INSIGHTS:
                map.put("startDate", "2018-01-01");
                map.put("endDate", "2018-03-01");
                visitsApi.getBotVisitAnalytics(manager.getFpTag(), map, callback);
                break;


        }
    }

    private Callback<VisitsModel> visitsModelCallback = new Callback<VisitsModel>() {
        @Override
        public void success(VisitsModel visitsModel, Response response) {
            updateData(visitsModel);
        }

        @Override
        public void failure(RetrofitError error) {
            updateData(null);
        }
    };


    private Callback<List<BotVisitResponse>> botVisitResponseCallback = new Callback<List<BotVisitResponse>>() {
        @Override
        public void success(List<BotVisitResponse> botVisitResponse, Response response) {
            updateBotVisitsData(botVisitResponse);
        }

        @Override
        public void failure(RetrofitError error) {
            updateBotVisitsData(null);
        }
    };

    private Callback<VisitsModel> totalVisitsCallback = new Callback<VisitsModel>() {
        @Override
        public void success(VisitsModel visitsModel, Response response) {
            int totalCount = 0;
            if (visitsModel != null) {
                for (VisitsModel.UniqueVisitsList data : visitsModel.getUniqueVisitsList()) {
                    totalCount += data.getDataCount();
                }
            }
            visitsCount.setText(String.valueOf(totalCount));
            updatePieView();
        }

        @Override
        public void failure(RetrofitError error) {
            visitsCount.setText("0");
            updatePieView();
        }
    };

    private void updatePieView() {
        viewUpdateStatus++;
        if (mVisitType == OTHER_INSIGHTS && viewUpdateStatus == 2) {

            float actualVisits = Long.parseLong(visitsCount.getText().toString())*1000;

            float botVisits = botVisitResponse.getBotVisits().getBingBot() +
                    botVisitResponse.getBotVisits().getBaiduSpider() +
                    botVisitResponse.getBotVisits().getDuckDuckBot() +
                    botVisitResponse.getBotVisits().getFacebook() +
                    botVisitResponse.getBotVisits().getGoogleBot() +
                    botVisitResponse.getBotVisits().getOthers() +
                    botVisitResponse.getBotVisits().getSlurpBot() +
                    botVisitResponse.getBotVisits().getYahooBot() +
                    botVisitResponse.getBotVisits().getYandexBot();

            float totalVisits = actualVisits + botVisits;

            float acutalEntry = (actualVisits / totalVisits) * 100;
            float botEntry = (botVisits / totalVisits) * 100;
            initTotalPieChart(acutalEntry, botEntry);
            initBotPieChart(acutalEntry, botEntry);

        }
    }

    private void initTotalPieChart(float acutalEntry, float botEntry) {
        totalPieChart.setUsePercentValues(true);

        ArrayList<Entry> yvalues = new ArrayList<Entry>();
        yvalues.add(new Entry(acutalEntry, 0));
        yvalues.add(new Entry(botEntry, 1));


        PieDataSet dataSet = new PieDataSet(yvalues, "");

        ArrayList<String> xVals = new ArrayList<String>();

        xVals.add("Actual Visits");
        xVals.add("Bot Visits");

        PieData data = new PieData(xVals, dataSet);
        // In Percentage
        data.setValueFormatter(new PercentFormatter());
        // Default value
        //data.setValueFormatter(new DefaultValueFormatter(0));
        totalPieChart.setData(data);
        totalPieChart.setDrawHoleEnabled(true);
        totalPieChart.setTransparentCircleRadius(58f);
        totalPieChart.setDescription("");

        totalPieChart.setHoleRadius(58f);
        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);

        data.setValueTextSize(13f);
        data.setValueTextColor(Color.DKGRAY);

        totalPieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

            }

            @Override
            public void onNothingSelected() {

            }
        });
        totalPieChart.invalidate();
    }

    private void initBotPieChart(float acutalEntry, float botEntry) {
        botPieChart.setUsePercentValues(true);

        ArrayList<Entry> yvalues = new ArrayList<Entry>();
        yvalues.add(new Entry(acutalEntry, 0));
        yvalues.add(new Entry(botEntry, 1));


        PieDataSet dataSet = new PieDataSet(yvalues, "");

        ArrayList<String> xVals = new ArrayList<String>();

        xVals.add("Actual Visits");
        xVals.add("Bot Visits");

        PieData data = new PieData(xVals, dataSet);
        // In Percentage
        data.setValueFormatter(new PercentFormatter());
        // Default value
        //data.setValueFormatter(new DefaultValueFormatter(0));
        botPieChart.setData(data);
        botPieChart.setDrawHoleEnabled(true);
        botPieChart.setTransparentCircleRadius(58f);
        botPieChart.setDescription("");

        botPieChart.setHoleRadius(58f);
        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);

        data.setValueTextSize(13f);
        data.setValueTextColor(Color.DKGRAY);

        botPieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

            }

            @Override
            public void onNothingSelected() {

            }
        });
        botPieChart.invalidate();
    }

    public interface ViewCallback {
        void onChartBarClicked(HashMap<String, String> map, int val);
    }
}

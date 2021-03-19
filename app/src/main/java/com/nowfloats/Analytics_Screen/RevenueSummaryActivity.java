package com.nowfloats.Analytics_Screen;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

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
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.nowfloats.Analytics_Screen.model.RevenueSummary;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.manageinventory.interfaces.WebActionCallInterface;
import com.nowfloats.manageinventory.models.SellerSummary;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit.RetrofitError;

public class RevenueSummaryActivity extends AppCompatActivity {

    private BarChart mChart;
    private MaterialDialog materialProgress;
    private UserSessionManager mSession;
    private TextView tvAmount;
    private TextView tvYear;
    private PopupWindow popup;
    private int count = 0;
    private RevenueSummary revenueSummary;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revenue_analytics);
        MixPanelController.track(MixPanelController.REVENUE_ANALYTICS, null);
        initView();
        initBarChart();
        syncRevenueSummary();
        getRevenueSummary(LAST_30_DAYS);
    }

    private void initView() {
        count = 0;
        mSession = new UserSessionManager(this, this);
        mChart = findViewById(R.id.barChart);
        tvAmount = findViewById(R.id.tvAmount);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView title = (TextView) findViewById(R.id.title);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            title.setText(getString(R.string.revenue_summary));
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tvYear = findViewById(R.id.tvYear);
        tvYear.setCompoundDrawablesWithIntrinsicBounds(null, null,
                AppCompatResources.getDrawable(this, R.drawable.ic_drop_down_white), null);


        tvYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiatePopupWindow(tvYear);
            }
        });

        tvYear.setText("Last 30 Days");

    }

    private void initiatePopupWindow(final View image) {

        if (popup == null) {
            try {
                UserSessionManager manager = new UserSessionManager(this, this);
                String createdDate = manager.getFPDetails(Key_Preferences.GET_FP_DETAILS_CREATED_ON);
                if (createdDate.contains("/Date")) {
                    createdDate = createdDate.replace("/Date(", "").replace(")/", "");
                }
                Calendar c = Calendar.getInstance();
                int currentYear = c.get(Calendar.YEAR);
                c.setTimeInMillis(Long.valueOf(createdDate));
                int createdYear = c.get(Calendar.YEAR);
//                final List<String> yearsList = new ArrayList<>(currentYear - createdYear + 1);
//                for (int i = currentYear; i >= createdYear; i--) {
//                    yearsList.add(String.valueOf(i));
//                }
                final List<String> yearsList = new ArrayList<>();
                yearsList.add("Today");
                yearsList.add("Yesterday");
                yearsList.add("Last 7 Days");
                yearsList.add("Last 30 Days");
                yearsList.add("This Month");
//                yearsList.add("Last Month");
//                yearsList.add("Last 6 Months");
//                yearsList.add("Last Year");
//                yearsList.add("Custom Range");

                popup = new PopupWindow(this);
                View layout = LayoutInflater.from(this).inflate(R.layout.layout_drop_down_list, null);
                popup.setContentView(layout);

                popup.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.white_round_corner));
                popup.setOutsideTouchable(true);
                ListView mListView = layout.findViewById(R.id.list_view);
                mListView.setAdapter(new ArrayAdapter<String>(this, R.layout.simple_text_center_item1, yearsList));
                popup.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
                popup.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        MixPanelController.track(MixPanelController.FILTER_ORDER_ANALYTICS, null);
                        count = 1;
                        initiatePopupWindow(image);
                        tvYear.setText(yearsList.get(i));
                        getRevenueSummary(yearsList.get(i));
//                        onYearSelected(Integer.valueOf(yearsList.get(i)));
                    }
                });
                popup.setFocusable(true);
                popup.showAsDropDown(image, 0, 5);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (popup.isShowing()) {
            popup.dismiss();
        } else {
            popup.showAsDropDown(image, 0, 5);
        }
    }


    private void initBarChart() {

        Paint p = mChart.getPaint(Chart.PAINT_INFO);
        p.setColor(ContextCompat.getColor(this, R.color.black));
        mChart.setDrawGridBackground(false);
        mChart.setScaleXEnabled(true);
        mChart.setScaleYEnabled(false);
        mChart.setDoubleTapToZoomEnabled(true);
        mChart.getAxisLeft().setAxisMinValue(0);
        mChart.getAxisLeft().setSpaceBottom(0);


        XAxis xaxis = mChart.getXAxis();
        YAxis leftAxis = mChart.getAxisLeft();
        YAxis rightAxis = mChart.getAxisRight();
        xaxis.setDrawGridLines(false);
        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//
        leftAxis.setDrawGridLines(false);
        rightAxis.setDrawGridLines(false);
        leftAxis.setEnabled(false);
        rightAxis.setEnabled(false);
        leftAxis.setTextSize(12.0f);
        rightAxis.setTextSize(12.0f);
        leftAxis.setTextColor(Color.argb(0, 0, 0, 0));
        rightAxis.setTextColor(Color.argb(0, 0, 0, 0));

        Description description = new Description();
        description.setText("");
        mChart.setExtraBottomOffset(5);
        mChart.setDescription(description);
        mChart.setXAxisRenderer(new CustomXAxisRenderer(mChart.getViewPortHandler(), mChart.getXAxis(),
                mChart.getTransformer(YAxis.AxisDependency.LEFT)));
        mChart.invalidate();

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            BoostLog.d("Back", "Back Pressed");
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void syncRevenueSummary() {
        showDialog();
        WebActionCallInterface callInterface = Constants.apAdapter.create(WebActionCallInterface.class);
        callInterface.getRevenueSummary(mSession.getFpTag(), new retrofit.Callback<SellerSummary>() {

            @Override
            public void success(SellerSummary sellerSummary, retrofit.client.Response response) {
                if (sellerSummary != null && sellerSummary.getData() != null) {
                    tvAmount.setText(sellerSummary.getData().getCurrencyCode() + " " + sellerSummary.getData().getTotalRevenue());
                } else {
                    tvAmount.setText(getString(R.string.inr) + " 0.0");
                }
                hideDialog();
            }

            @Override
            public void failure(RetrofitError error) {
                hideDialog();
            }
        });
    }

    public class CustomXAxisRenderer extends XAxisRenderer {
        public CustomXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans) {
            super(viewPortHandler, xAxis, trans);
        }

        @Override
        protected void drawLabel(Canvas c, String formattedLabel, float x, float y, MPPointF anchor, float angleDegrees) {

            if (formattedLabel.contains("-")) {
                String line[] = formattedLabel.split("-");
                Utils.drawXAxisValue(c, line[0], x, y, mAxisLabelPaint, anchor, angleDegrees);
                Utils.drawXAxisValue(c, line[1], x + mAxisLabelPaint.getTextSize(), y + mAxisLabelPaint.getTextSize(), mAxisLabelPaint, anchor, angleDegrees);
            } else {
                Utils.drawXAxisValue(c, formattedLabel, x, y, mAxisLabelPaint, anchor, angleDegrees);
            }
        }
    }

    private void syncData() {

        showDialog();
        WebActionCallInterface callInterface = Constants.apAdapter.create(WebActionCallInterface.class);

        callInterface.dailySellerRevenue(mSession.getFpTag(), startDate, endDate, new retrofit.Callback<RevenueSummary>() {

            @Override
            public void success(RevenueSummary revenue, retrofit.client.Response response) {
                revenueSummary = revenue;
                hashRevenue = new HashMap<>();
                if (revenueSummary != null &&
                        revenueSummary.getRevenueData().size() > 0) {
                    for (RevenueSummary.RevenueData revenueData : revenueSummary.getRevenueData()) {
                        hashRevenue.put(revenueData.getDeliveryDate(), revenueData.getAmount());
                    }
                }
                updateBarChart();
            }

            @Override
            public void failure(RetrofitError error) {
                hashRevenue = new HashMap<>();
                updateBarChart();
            }
        });


    }

    private HashMap<String, Float> hashRevenue;


    private void updateBarChart() {
        final ArrayList<String> labels = new ArrayList<>();

        List<IBarDataSet> dataSets = null;

        List<BarEntry> valueEntryList = new ArrayList<>();
        labels.clear();


        BarData barData = mChart.getData();
        if (barData != null) {
            barData.clearValues();
            mChart.notifyDataSetChanged();
        }

        mChart.clear();

        if (hashRevenue != null && hashRevenue.size() > 0) {
            try {
                final String[] months = getResources().getStringArray(R.array.months_short);

                Date initialDate = new SimpleDateFormat(Methods.YYYY_MM_DD).
                        parse(startDate);

                Date finalDate = new SimpleDateFormat(Methods.YYYY_MM_DD).
                        parse(endDate);

                //milliseconds
                long different = finalDate.getTime() - initialDate.getTime();

                System.out.println("startDate : " + initialDate);
                System.out.println("endDate : " + finalDate);
                System.out.println("different : " + different);

                long secondsInMilli = 1000;
                long minutesInMilli = secondsInMilli * 60;
                long hoursInMilli = minutesInMilli * 60;
                long daysInMilli = hoursInMilli * 24;
                long weekInMilli = daysInMilli * 7;

                long elapsedWeek = different / weekInMilli;
                long elapsedDay = different / daysInMilli;

                Calendar calendar = Calendar.getInstance();
                calendar.setFirstDayOfWeek(Calendar.MONDAY);
                calendar.setTimeInMillis(initialDate.getTime());

                labels.clear();

                int valueCounter = 0;
                if (elapsedWeek > 1) {
                    while (Methods.getDateDifference(initialDate, finalDate) > 0) {

                        int startDate = calendar.get(Calendar.DAY_OF_MONTH);
                        int prevMonth = calendar.get(Calendar.MONTH);


                        int totalValue = 0, counter = 0;

                        while (counter <= 6) {
                            String key = Methods.getDate(calendar.getTimeInMillis(), Methods.YYYY_MM_DD) + "T00:00:00";
                            if (hashRevenue.containsKey(key)) {
                                totalValue += hashRevenue.get(key);
                            }
                            calendar.add(Calendar.DATE, 1);
                            counter = counter + 1;
                        }

                        calendar.add(Calendar.DATE, -1);
                        int currentMonth = calendar.get(Calendar.MONTH);
                        labels.add(String.format(Locale.ENGLISH, "%s %d-%s %d", months[prevMonth], startDate, months[currentMonth],
                                calendar.get(Calendar.DAY_OF_MONTH)));
                        calendar.add(Calendar.DATE, 1);

                        valueEntryList.add(new BarEntry(valueCounter, totalValue,
                                totalValue));
                        initialDate = new SimpleDateFormat(Methods.YYYY_MM_DD).
                                parse(Methods.getDate(calendar.getTimeInMillis(), Methods.YYYY_MM_DD));
                        valueCounter++;
                    }

                } else if (elapsedDay > 0) {

                    while (Methods.getDateDifference(initialDate, finalDate) > 0) {

                        int startDate = calendar.get(Calendar.DAY_OF_MONTH);
                        int prevMonth = calendar.get(Calendar.MONTH);

                        String key = Methods.getDate(calendar.getTimeInMillis(), Methods.YYYY_MM_DD) + "T00:00:00";
                        if (hashRevenue.containsKey(key)) {
                            valueEntryList.add(new BarEntry(valueCounter, hashRevenue.get(key),
                                    hashRevenue.get(key)));
                        } else {
                            valueEntryList.add(new BarEntry(valueCounter, 0,
                                    0));
                        }

                        labels.add(String.format(Locale.ENGLISH, "%s %d", months[prevMonth], startDate));
                        calendar.add(Calendar.DATE, 1);


                        initialDate = new SimpleDateFormat(Methods.YYYY_MM_DD).
                                parse(Methods.getDate(calendar.getTimeInMillis(), Methods.YYYY_MM_DD));
                        valueCounter++;
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            final String[] months = new String[labels.size()];
            for (int count = 0; count < labels.size(); count++) {
                months[count] = labels.get(count);
            }

            //visitsCount.setText(String.valueOf(totalCount));
            BarDataSet barDataSet = new BarDataSet(valueEntryList, "Total Count");
            barDataSet.setColor(Color.GRAY);
            barDataSet.setValueTextSize(14.0f);
            barDataSet.setValueTextColor(getResources().getColor(R.color.black));

            dataSets = new ArrayList<>();
            dataSets.add(barDataSet);

            BarData data = new BarData(dataSets);
            data.setValueFormatter(new MyYAxisValueFormatter());
            data.setValueTextSize(10);
            mChart.setData(data);
            mChart.setVisibleXRangeMaximum(10.2f);
            mChart.notifyDataSetChanged();
            mChart.animateXY(1000, 1000);

            mChart.getBarData().setValueFormatter(new IValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                    return entry.getData().toString();
                }
            });

            XAxis xAxis = mChart.getXAxis();

            xAxis.setDrawGridLines(false);
            xAxis.setGranularity(1f); // only intervals of 1 day
            xAxis.setLabelCount(10);
            xAxis.setGranularityEnabled(true);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

            xAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {

                    if (months != null && months.length > 0 &&
                            months.length > (int) value) {
                        return months[(int) value];
                    } else return "";

                }
            });

        }

        mChart.invalidate();
        hideDialog();
    }

    private void showDialog() {
        if (materialProgress == null) {
            materialProgress = new MaterialDialog.Builder(this)
                    .widgetColorRes(R.color.accentColor)
                    .content(getString(R.string.please_wait_))
                    .progress(true, 0)
                    .cancelable(false)
                    .build();

        }
        if (!materialProgress.isShowing()) {
            materialProgress.show();
        }
    }

    private void hideDialog() {
        count++;
        if (materialProgress.isShowing() && count >= 2) {
            materialProgress.dismiss();
        }
    }


    public static final String TODAY = "Today";
    public static final String YESTERDAY = "Yesterday";
    public static final String LAST_SEVEN_DAYS = "Last 7 Days";
    public static final String LAST_30_DAYS = "Last 30 Days";
    public static final String THIS_MONTH = "This Month";
    public static final String LAST_MONTH = "Last Month";
    public static final String LAST_SIX_MONTH = "Last 6 Month";
    public static final String LAST_YEAR = "Last Year";
    public static final String CUSTOM_RANGE = "Custom Range";

    private String startDate = "", endDate = "";

    private void getRevenueSummary(String choice) {

        startDate = "";
        endDate = "";
        revenueSummary = null;
        Calendar calendar = Calendar.getInstance();

        switch (choice) {
            case LAST_SEVEN_DAYS:
                calendar.add(Calendar.DATE, 1);
                endDate = Methods.getDate(calendar.getTimeInMillis(), Methods.YYYY_MM_DD);
                calendar.add(Calendar.DATE, -7);
                startDate = Methods.getDate(calendar.getTimeInMillis(), Methods.YYYY_MM_DD);
                break;
            case LAST_30_DAYS:
                calendar.add(Calendar.DATE, 1);
                endDate = Methods.getDate(calendar.getTimeInMillis(), Methods.YYYY_MM_DD);
                calendar.add(Calendar.DATE, -30);
                startDate = Methods.getDate(calendar.getTimeInMillis(), Methods.YYYY_MM_DD);

                break;
            case TODAY:
                calendar.add(Calendar.DATE, 1);
                endDate = Methods.getDate(calendar.getTimeInMillis(), Methods.YYYY_MM_DD);
                calendar.add(Calendar.DATE, -1);
                startDate = Methods.getDate(calendar.getTimeInMillis(), Methods.YYYY_MM_DD);

                break;

            case YESTERDAY:
                endDate = Methods.getDate(calendar.getTimeInMillis(), Methods.YYYY_MM_DD);
                calendar.add(Calendar.DATE, -1);
                startDate = Methods.getDate(calendar.getTimeInMillis(), Methods.YYYY_MM_DD);

                break;
            case THIS_MONTH:
                calendar.add(Calendar.DATE, 1);
                endDate = Methods.getDate(calendar.getTimeInMillis(), Methods.YYYY_MM_DD);

                calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                startDate = Methods.getDate(calendar.getTimeInMillis(), Methods.YYYY_MM_DD);

                break;
        }

        syncData();
    }

}

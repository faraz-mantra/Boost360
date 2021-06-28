package com.nowfloats.Analytics_Screen;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
import com.nowfloats.Analytics_Screen.Fragments.OrderAnalyticsFragment;
import com.nowfloats.Analytics_Screen.model.OrderStatusSummary;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.manageinventory.interfaces.WebActionCallInterface;
import com.nowfloats.manageinventory.models.SellerSummary;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.WebEngageController;
import com.thinksity.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit.RetrofitError;

import static com.framework.webengageconstant.EventLabelKt.ORDER_ANALYTICS;
import static com.framework.webengageconstant.EventNameKt.CLICKED_ON_ORDER_ANALYTICS;
import static com.framework.webengageconstant.EventValueKt.NULL;

public class OrderSummaryActivity extends AppCompatActivity {

    private ImageView spinner;
    private LinearLayout layout;
    private BarChart mChart;
    private ViewPager vwCharts;
    private MaterialDialog materialProgress;
    private UserSessionManager mSession;
    private TextView tvYear;
    private PopupWindow popup;
    private ImageView ivLeftNav, ivRightNav;

    private int count = 0;
    private OrderStatusSummary previousOrderSummary, currentOrderSummary;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);
        WebEngageController.trackEvent(CLICKED_ON_ORDER_ANALYTICS, ORDER_ANALYTICS, NULL);
        MixPanelController.track(MixPanelController.ORDER_ANALYTICS, null);
        initView();
        initBarChart();
        syncSellerSummary();
        getOrderSummary(LAST_30_DAYS);
    }

    private void initView() {
        count = 0;
        mSession = new UserSessionManager(this, this);
        layout = (LinearLayout) findViewById(R.id.linearlayout);
        spinner = (ImageView) findViewById(R.id.toolbar_spinner);
        mChart = findViewById(R.id.barChart);
        vwCharts = (ViewPager) findViewById(R.id.vwCharts);
        ivLeftNav = (ImageView) findViewById(R.id.ivLeftNav);
        ivRightNav = (ImageView) findViewById(R.id.ivRightNav);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView title = (TextView) findViewById(R.id.title);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
//            title.setText(com.nowfloats.util.Utils.getOrderAnalyticsTaxonomyFromServiceCode(mSession.getFP_AppExperienceCode()));
            title.setText(com.nowfloats.util.Utils.getCustomerAppointmentTaxonomyFromServiceCode(mSession.getFP_AppExperienceCode()));
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

        ivLeftNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vwCharts.arrowScroll(ViewPager.FOCUS_LEFT);

            }
        });

        ivRightNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vwCharts.arrowScroll(ViewPager.FOCUS_RIGHT);

            }
        });
        ivLeftNav.setVisibility(View.GONE);
        vwCharts.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    ivLeftNav.setVisibility(View.GONE);
                    ivRightNav.setVisibility(View.VISIBLE);
                } else if (position == 2) {
                    ivLeftNav.setVisibility(View.VISIBLE);
                    ivRightNav.setVisibility(View.GONE);
                } else {
                    ivLeftNav.setVisibility(View.VISIBLE);
                    ivRightNav.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
                        getOrderSummary(yearsList.get(i));
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

    public class CustomXAxisRenderer extends XAxisRenderer {
        public CustomXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans) {
            super(viewPortHandler, xAxis, trans);
        }

        @Override
        protected void drawLabel(Canvas c, String formattedLabel, float x, float y, MPPointF anchor, float angleDegrees) {
            String line[] = formattedLabel.split("\n");
            Utils.drawXAxisValue(c, line[0], x, y, mAxisLabelPaint, anchor, angleDegrees);
            Utils.drawXAxisValue(c, line[1], x + mAxisLabelPaint.getTextSize(), y + mAxisLabelPaint.getTextSize(), mAxisLabelPaint, anchor, angleDegrees);
        }
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


    private void updatePager() {

        ivRightNav.setVisibility(View.VISIBLE);
        ivLeftNav.setVisibility(View.GONE);

        if (currentOrderSummary == null || currentOrderSummary.getOrderStatus() == null) {
            currentOrderSummary = new OrderStatusSummary();
            currentOrderSummary.setOrderStatus(new ArrayList<OrderStatusSummary.OrderStatus>());

        }
        if (previousOrderSummary == null || previousOrderSummary.getOrderStatus() == null) {
            previousOrderSummary = new OrderStatusSummary();
            previousOrderSummary.setOrderStatus(new ArrayList<OrderStatusSummary.OrderStatus>());
        }

        /**
         *  calculate change
         */
        vwCharts.setAdapter(new PagerAdapter(getSupportFragmentManager(),
                (ArrayList<OrderStatusSummary.OrderStatus>) currentOrderSummary.getOrderStatus(),
                (ArrayList<OrderStatusSummary.OrderStatus>) previousOrderSummary.getOrderStatus()));
    }

    private void syncSellerSummary() {
        showDialog();
        WebActionCallInterface callInterface = Constants.apAdapter.create(WebActionCallInterface.class);
        callInterface.getRevenueSummary(mSession.getFpTag(), new retrofit.Callback<SellerSummary>() {

            @Override
            public void success(SellerSummary sellerSummary, retrofit.client.Response response) {
                updateBarChart(sellerSummary);
            }

            @Override
            public void failure(RetrofitError error) {
                hideDialog();
            }
        });
    }

    private void syncData() {

        showDialog();
        WebActionCallInterface callInterface = Constants.apAdapter.create(WebActionCallInterface.class);

        callInterface.getOrderStatusSummary(mSession.getFpTag(), previousStartDate, previousEndDate, new retrofit.Callback<OrderStatusSummary>() {

            @Override
            public void success(OrderStatusSummary orderStatusSummary, retrofit.client.Response response) {
                previousOrderSummary = orderStatusSummary;
                hideDialog();
            }

            @Override
            public void failure(RetrofitError error) {
                hideDialog();
            }
        });

        callInterface.getOrderStatusSummary(mSession.getFpTag(), startDate, endDate, new retrofit.Callback<OrderStatusSummary>() {

            @Override
            public void success(OrderStatusSummary orderStatusSummary, retrofit.client.Response response) {
                currentOrderSummary = orderStatusSummary;
                hideDialog();
            }

            @Override
            public void failure(RetrofitError error) {
                hideDialog();
            }
        });

    }

    private void updateBarChart(SellerSummary sellerSummary) {
        final ArrayList<String> labels = new ArrayList<>(12);

        List<IBarDataSet> dataSets = null;

        List<BarEntry> valueEntryList = new ArrayList<>();
        labels.clear();
        final String[] months = com.nowfloats.util.Utils.getCustomerAppointmentBarChartCode(this, mSession.getFP_AppExperienceCode());

        for (int i = 0; i < months.length; i++) {
            if (i == 0) {
                valueEntryList.add(new BarEntry(i, sellerSummary.getData().getTotalOrdersCompleted(),
                        sellerSummary.getData().getTotalOrdersCompleted()));
            } else if (i == 1) {
                valueEntryList.add(new BarEntry(i, sellerSummary.getData().getTotalOrdersInProgress(),
                        sellerSummary.getData().getTotalOrdersInProgress()));
            } else if (i == 2) {
                valueEntryList.add(new BarEntry(i, sellerSummary.getData().getTotalOrdersCancelled(),
                        sellerSummary.getData().getTotalOrdersCancelled()));
            } else if (i == 3) {
                valueEntryList.add(new BarEntry(i, sellerSummary.getData().getTotalOrdersAbandoned(),
                        sellerSummary.getData().getTotalOrdersAbandoned()));
            }
            labels.add(months[i]);
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
        xAxis.setLabelCount(7);
        xAxis.setGranularityEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return months[(int) value];
            }
        });

        mChart.invalidate();

        hideDialog();
    }

    class PagerAdapter extends FragmentStatePagerAdapter {

        private ArrayList<OrderStatusSummary.OrderStatus> currentOrderStatus;
        private ArrayList<OrderStatusSummary.OrderStatus> previousOrderStatus;

        public PagerAdapter(FragmentManager fm,
                            ArrayList<OrderStatusSummary.OrderStatus> currentOrderStatus,
                            ArrayList<OrderStatusSummary.OrderStatus> previousOrderStatus) {
            super(fm);
            this.currentOrderStatus = currentOrderStatus;
            this.previousOrderStatus = previousOrderStatus;
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putInt("pos", position);
            bundle.putSerializable("curOrderStatus", currentOrderStatus);
            bundle.putSerializable("prevOrderStatus", previousOrderStatus);
            return OrderAnalyticsFragment.getInstance(bundle);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Page-" + position;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    private void showDialog() {
        if (materialProgress == null) {
            materialProgress = new MaterialDialog.Builder(this)
                    .widgetColorRes(R.color.accentColor)
                    .content(R.string.please_wait_)
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
        if (materialProgress.isShowing() && count >= 3) {
            materialProgress.dismiss();
            updatePager();
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

    private String startDate = "", endDate = "",
            previousStartDate = "", previousEndDate = "";

    private void getOrderSummary(String choice) {

        startDate = "";
        previousStartDate = "";
        endDate = "";
        previousEndDate = "";
        previousOrderSummary = null;
        currentOrderSummary = null;
        Calendar calendar = Calendar.getInstance();

        switch (choice) {
            case LAST_SEVEN_DAYS:
                calendar.add(Calendar.DATE, 1);
                endDate = Methods.getDate(calendar.getTimeInMillis(), Methods.YYYY_MM_DD);
                calendar.add(Calendar.DATE, -7);
                startDate = Methods.getDate(calendar.getTimeInMillis(), Methods.YYYY_MM_DD);

                previousEndDate = Methods.getDate(calendar.getTimeInMillis(), Methods.YYYY_MM_DD);
                calendar.add(Calendar.DATE, -7);
                previousStartDate = Methods.getDate(calendar.getTimeInMillis(), Methods.YYYY_MM_DD);

                break;
            case LAST_30_DAYS:
                calendar.add(Calendar.DATE, 1);
                endDate = Methods.getDate(calendar.getTimeInMillis(), Methods.YYYY_MM_DD);
                calendar.add(Calendar.DATE, -30);
                startDate = Methods.getDate(calendar.getTimeInMillis(), Methods.YYYY_MM_DD);

                previousEndDate = Methods.getDate(calendar.getTimeInMillis(), Methods.YYYY_MM_DD);
                calendar.add(Calendar.DATE, -30);
                previousStartDate = Methods.getDate(calendar.getTimeInMillis(), Methods.YYYY_MM_DD);

                break;
            case TODAY:
                calendar.add(Calendar.DATE, 1);
                endDate = Methods.getDate(calendar.getTimeInMillis(), Methods.YYYY_MM_DD);
                calendar.add(Calendar.DATE, -1);
                startDate = Methods.getDate(calendar.getTimeInMillis(), Methods.YYYY_MM_DD);

                previousEndDate = Methods.getDate(calendar.getTimeInMillis(), Methods.YYYY_MM_DD);
                calendar.add(Calendar.DATE, -1);
                previousStartDate = Methods.getDate(calendar.getTimeInMillis(), Methods.YYYY_MM_DD);
                break;

            case YESTERDAY:
                endDate = Methods.getDate(calendar.getTimeInMillis(), Methods.YYYY_MM_DD);
                calendar.add(Calendar.DATE, -1);
                startDate = Methods.getDate(calendar.getTimeInMillis(), Methods.YYYY_MM_DD);

                previousEndDate = Methods.getDate(calendar.getTimeInMillis(), Methods.YYYY_MM_DD);
                calendar.add(Calendar.DATE, -1);
                previousStartDate = Methods.getDate(calendar.getTimeInMillis(), Methods.YYYY_MM_DD);
                break;
            case THIS_MONTH:
                calendar.add(Calendar.DATE, 1);
                endDate = Methods.getDate(calendar.getTimeInMillis(), Methods.YYYY_MM_DD);

                calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                startDate = Methods.getDate(calendar.getTimeInMillis(), Methods.YYYY_MM_DD);

                previousEndDate = Methods.getDate(calendar.getTimeInMillis(), Methods.YYYY_MM_DD);
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                calendar.set(Calendar.DATE, 1);
                previousStartDate = Methods.getDate(calendar.getTimeInMillis(), Methods.YYYY_MM_DD);
                break;
        }

        syncData();
    }

}

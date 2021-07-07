package com.nowfloats.Analytics_Screen.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.nowfloats.Analytics_Screen.model.OrderStatusSummary;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Utils;
import com.thinksity.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class OrderAnalyticsFragment extends Fragment {


    private PieChart pieChart;
    private TextView tvTitle, tvYear;
    private int position;
    private List<OrderStatusSummary.OrderStatus> currentOrderStatus, previousOrderStatus;
    private ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();
    private ArrayList<String> xValues = new ArrayList<String>();
    private ArrayList<Integer> colors = new ArrayList<>();
    private RecyclerView rvLegend;
    private UserSessionManager mSession;
    private DecimalFormat mFormat;

    public static Fragment getInstance(Bundle b) {
        Fragment frag = new OrderAnalyticsFragment();
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle b = getArguments();
            position = b.getInt("pos");
            currentOrderStatus = (List<OrderStatusSummary.OrderStatus>) b.getSerializable("curOrderStatus");
            previousOrderStatus = (List<OrderStatusSummary.OrderStatus>) b.getSerializable("prevOrderStatus");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_order_analytics, container, false);
        pieChart = root.findViewById(R.id.pieChart);
        pieChart.setCenterTextColor(ContextCompat.getColor(getActivity(),R.color.colorAccent));
        pieChart.setNoDataTextColor(ContextCompat.getColor(getActivity(),R.color.colorAccent));
        tvTitle = root.findViewById(R.id.tvTitle);
        rvLegend = root.findViewById(R.id.rvLegend);
        mFormat = new DecimalFormat("#########");
        mSession = new UserSessionManager(getContext(), requireActivity());

        return root;
    }

    private void initPieChart() {
        PieDataSet dataSet = new PieDataSet(yValues, "");
        dataSet.setColors(colors);
        dataSet.setHighlightEnabled(false);

        dataSet.setValueLinePart1OffsetPercentage(20.f);
        dataSet.setValueLinePart1Length(0.2f);
        dataSet.setValueLinePart2Length(1.6f);
        dataSet.setValueLineColor(Color.BLACK);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setHighlightPerTapEnabled(false);

        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new MyYAxisValueFormatter());
        data.setValueTextSize(10f); // <- here
        pieChart.setData(data);
        pieChart.getLegend().setWordWrapEnabled(true);
        pieChart.setExtraOffsets(20.f, 0.f, 20.f, 0.f);
        pieChart.setExtraRightOffset(20.0f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(10.0f);
        pieChart.getDescription().setEnabled(false);
        buildLegend();
//        pieChart.animateY(1400, Easing.EasingOption.EaseInCubic);
        pieChart.invalidate();
    }

    private void buildLegend() {

        Legend legend = pieChart.getLegend();
        int colorcodes[] = legend.getColors();

        rvLegend.setHasFixedSize(true);
        rvLegend.setAdapter(new LegendAdapter(colorcodes, legend.getLabels()));
        if (colorcodes.length > 3) {
            rvLegend.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        } else {
            rvLegend.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        }
        pieChart.getLegend().setEnabled(false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getValues();
    }

    private void getValues() {

        if (currentOrderStatus != null && currentOrderStatus.size() > 0) {

            float totalOrders = 0;
            switch (position) {
                case 0:
                    float onlineOrders = 0, codOrders = 0;
                    int codPrevOrders = 0, onlinePrevOrders = 0;
                    tvTitle.setText("TOTAL " + Utils.getCustomerTypeFromServiceCode(mSession.getFP_AppExperienceCode()).toUpperCase());
                    for (OrderStatusSummary.OrderStatus orderStatus : currentOrderStatus) {

                        if (orderStatus.getPaymentMethod().equalsIgnoreCase("COD")) {
                            codOrders += orderStatus.getOrdersCount();
                        } else if (orderStatus.getPaymentMethod().equalsIgnoreCase("ONLINEPAYMENT")) {
                            onlineOrders += orderStatus.getOrdersCount();
                        }
                    }

                    if (previousOrderStatus != null && previousOrderStatus.size() > 0) {

                        for (OrderStatusSummary.OrderStatus orderStatus : previousOrderStatus) {

                            if (orderStatus.getPaymentMethod().equalsIgnoreCase("COD")) {
                                codPrevOrders += orderStatus.getOrdersCount();
                            } else if (orderStatus.getPaymentMethod().equalsIgnoreCase("ONLINEPAYMENT")) {
                                onlinePrevOrders += orderStatus.getOrdersCount();
                            }
                        }

                    }


                    if (codPrevOrders == 0) {
                        codPrevOrders = (int) (codOrders * 100);
                    } else {
                        codPrevOrders = (int) (((codOrders / codPrevOrders) - 1) * 100);
                    }

                    if (onlinePrevOrders == 0) {
                        onlinePrevOrders = (int) (onlineOrders * 100);
                    } else {
                        onlinePrevOrders = (int) (((onlineOrders / onlinePrevOrders) - 1) * 100);
                    }

                    xValues.add("Online Payment " + Utils.getCustomerTypeFromServiceCode(mSession.getFP_AppExperienceCode()) + ": " + (int) onlineOrders);
                    xValues.add("COD " + Utils.getCustomerTypeFromServiceCode(mSession.getFP_AppExperienceCode()) + ": " + (int) codOrders);

                    totalOrders = onlineOrders + codOrders;
                    onlineOrders = (onlineOrders / totalOrders) * 100;
                    codOrders = (codOrders / totalOrders) * 100;

                    if (onlinePrevOrders < 0) {
                        yValues.add(new PieEntry(onlineOrders, "", onlinePrevOrders + "% ↓"));
                    } else {
                        yValues.add(new PieEntry(onlineOrders, "", onlinePrevOrders + "% ↑"));
                    }

                    if (codPrevOrders < 0) {
                        yValues.add(new PieEntry(codOrders, "", codPrevOrders + "% ↓"));
                    } else {
                        yValues.add(new PieEntry(codOrders, "", codPrevOrders + "% ↑"));
                    }

                    colors.add(Color.parseColor("#00aff0"));
                    colors.add(Color.parseColor("#96c800"));
                    break;
                case 1:
                    tvTitle.setText("COD " + Utils.getCustomerTypeFromServiceCode(mSession.getFP_AppExperienceCode()).toUpperCase());
                    calculateOrders("COD");
                    break;
                case 2:
                    tvTitle.setText("ONLINE PAYMENT " + Utils.getCustomerTypeFromServiceCode(mSession.getFP_AppExperienceCode()).toUpperCase());
                    calculateOrders("ONLINEPAYMENT");
                    break;
            }

            initPieChart();
        }

    }

    private void calculateOrders(String orderPaymentMode) {

        float totalOrders = 0, confirmedOrders = 0, cancelledOrders = 0,
                successfulOrders = 0, placedOrders = 0, escalatedOrders = 0;

        int prevConfirmedOrders = 0, prevCancelledOrders = 0,
                prevSuccessfulOrders = 0, prevPlacedOrders = 0, prevEscalatedOrders = 0;

        for (OrderStatusSummary.OrderStatus orderStatus : currentOrderStatus) {

            if (orderStatus.getPaymentMethod().equalsIgnoreCase(orderPaymentMode)) {

                switch (orderStatus.getOrderStatus()) {
                    case "CONFIRMED":
                        confirmedOrders = confirmedOrders + orderStatus.getOrdersCount();
                        break;
                    case "COMPLETED":
                        successfulOrders = successfulOrders + orderStatus.getOrdersCount();
                        break;
                    case "CANCELLED":
                        cancelledOrders = cancelledOrders + orderStatus.getOrdersCount();
                        break;
                    case "PLACED":
                        placedOrders = placedOrders + orderStatus.getOrdersCount();
                        break;
                    case "ESCALATED":
                        escalatedOrders = escalatedOrders + orderStatus.getOrdersCount();
                        break;
                }
            }
        }


        if (previousOrderStatus != null && previousOrderStatus.size() > 0) {
            for (OrderStatusSummary.OrderStatus orderStatus : previousOrderStatus) {

                if (orderStatus.getPaymentMethod().equalsIgnoreCase(orderPaymentMode)) {

                    switch (orderStatus.getOrderStatus()) {
                        case "CONFIRMED":
                            prevConfirmedOrders += orderStatus.getOrdersCount();
                            break;
                        case "COMPLETED":
                            prevSuccessfulOrders += orderStatus.getOrdersCount();
                            break;
                        case "CANCELLED":
                            prevCancelledOrders += orderStatus.getOrdersCount();
                            break;
                        case "PLACED":
                            prevPlacedOrders += orderStatus.getOrdersCount();
                            break;
                        case "ESCALATED":
                            prevEscalatedOrders += orderStatus.getOrdersCount();
                            break;
                    }
                }
            }

        }

        totalOrders = confirmedOrders + successfulOrders + cancelledOrders + placedOrders + escalatedOrders;

        if (confirmedOrders > 0.0f) {
            colors.add(Color.parseColor("#fdd400"));
            xValues.add("Confirmed " + Utils.getCustomerTypeFromServiceCode(mSession.getFP_AppExperienceCode()) + ": " + (int) confirmedOrders);

            if (prevConfirmedOrders == 0) {
                prevConfirmedOrders = (int) (confirmedOrders * 100);
            } else {
                prevConfirmedOrders = (int) (((confirmedOrders / prevConfirmedOrders) - 1) * 100);
            }

            confirmedOrders = (confirmedOrders / totalOrders) * 100;


            if (prevConfirmedOrders < 0) {
                yValues.add(new PieEntry(confirmedOrders, "", prevConfirmedOrders + "% ↓"));
            } else {
                yValues.add(new PieEntry(confirmedOrders, "", prevConfirmedOrders + "% ↑"));
            }
        }


        if (successfulOrders > 0.0f) {
            colors.add(Color.parseColor("#158b44"));
            xValues.add("Successful " + Utils.getCustomerTypeFromServiceCode(mSession.getFP_AppExperienceCode()) + ": " + (int) successfulOrders);


            if (prevSuccessfulOrders == 0) {
                prevSuccessfulOrders = (int) (successfulOrders * 100);
            } else {
                prevSuccessfulOrders = (int) (((successfulOrders / prevSuccessfulOrders) - 1) * 100);
            }
            successfulOrders = (successfulOrders / totalOrders) * 100;


            if (prevSuccessfulOrders < 0) {
                yValues.add(new PieEntry(successfulOrders, "", prevSuccessfulOrders + "% ↓"));
            } else {
                yValues.add(new PieEntry(successfulOrders, "", prevSuccessfulOrders + "% ↑"));
            }

        }


        if (cancelledOrders > 0.0f) {
            colors.add(Color.parseColor("#f58020"));
            xValues.add("Cancelled " + Utils.getCustomerTypeFromServiceCode(mSession.getFP_AppExperienceCode()) + ": " + (int) cancelledOrders);

            if (prevCancelledOrders == 0) {
                prevCancelledOrders = (int) (cancelledOrders * 100);
            } else {
                prevCancelledOrders = (int) (((cancelledOrders / prevCancelledOrders) - 1) * 100);
            }
            cancelledOrders = (cancelledOrders / totalOrders) * 100;

            if (prevCancelledOrders < 0) {
                yValues.add(new PieEntry(cancelledOrders, "", prevCancelledOrders + "% ↓"));
            } else {
                yValues.add(new PieEntry(cancelledOrders, "", prevCancelledOrders + "% ↑"));
            }
        }


        if (escalatedOrders > 0.0f) {
            colors.add(Color.parseColor("#ffb900"));
            xValues.add("Escalated " + Utils.getCustomerTypeFromServiceCode(mSession.getFP_AppExperienceCode()) + ": " + (int) escalatedOrders);


            if (prevEscalatedOrders == 0) {
                prevEscalatedOrders = (int) (escalatedOrders * 100);
            } else {
                prevEscalatedOrders = (int) (((escalatedOrders / prevEscalatedOrders) - 1) * 100);
            }

            escalatedOrders = (escalatedOrders / totalOrders) * 100;

            if (prevEscalatedOrders < 0) {
                yValues.add(new PieEntry(escalatedOrders, "", prevEscalatedOrders + "% ↓"));
            } else {
                yValues.add(new PieEntry(escalatedOrders, "", prevEscalatedOrders + "% ↑"));
            }
        }


        if (placedOrders > 0.0f) {
            colors.add(Color.parseColor("#49ce75"));
            xValues.add("Placed " + Utils.getCustomerTypeFromServiceCode(mSession.getFP_AppExperienceCode()) + ": " + (int) placedOrders);

            if (prevPlacedOrders == 0) {
                prevPlacedOrders = (int) (placedOrders * 100);
            } else {
                prevPlacedOrders = (int) (((placedOrders / prevPlacedOrders) - 1) * 100);
            }

            placedOrders = (placedOrders / totalOrders) * 100;

            if (prevPlacedOrders < 0) {
                yValues.add(new PieEntry(placedOrders, "", prevPlacedOrders + "% ↓"));
            } else {
                yValues.add(new PieEntry(placedOrders, "", prevPlacedOrders + "% ↑"));
            }
        }

    }

    public class LegendAdapter extends RecyclerView.Adapter<LegendViewHolder> {
        private int[] colorcodes;
        private String[] labels;

        public LegendAdapter(int[] colorcodes,
                             String[] labels) {
            this.colorcodes = colorcodes;
            this.labels = labels;
        }

        @Override
        public LegendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.legend_item, parent, false);
            return new LegendViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final LegendViewHolder holder, final int position) {
            final String label = labels[position];
            holder.tvLabel.setText(xValues.get(position));
            holder.tvColor.setBackgroundColor(colorcodes[position]);
        }

        @Override
        public int getItemCount() {
            return xValues.size();
        }
    }

    public class MyYAxisValueFormatter implements IValueFormatter {

        public MyYAxisValueFormatter() {
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex,
                                        ViewPortHandler viewPortHandler) {

            String label = ((PieEntry) entry).getLabel();
            if (((PieEntry) entry).getData() != null) {
                label = ((PieEntry) entry).getData() + "";
            }
            return label;
        }
    }

}

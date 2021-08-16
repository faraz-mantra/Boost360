package com.nowfloats.manageinventory;

import android.content.Intent;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.manageinventory.interfaces.WebActionCallInterface;
import com.nowfloats.manageinventory.models.CommonStatus;
import com.nowfloats.manageinventory.models.SellerSummary;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.MixPanelController;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.thinksity.R;

import retrofit.RetrofitError;


public class SellerAnalyticsActivity extends AppCompatActivity {


    private TextView tvTotalRevenue, tvTotalOrders, tvCurrencyCode, tvSuccessfullOrders, tvReceivedOrders, tvCancelledOrders, tvDisputedOrders,
            tvReturnedPrders, tvAbandonedOrders, tvRevenueText;
    private ProgressBar pgTotalRevenue, pgTotalOrders, pgSuccessfullOrders, pgReceivedOrders, pgCancelledOrders, pgRefundedOrders, pgDisputedOrders,
            pgAbandonedOrders;
    private Toolbar toolBar;
    private LinearLayout revenueContainer;

    private UserSessionManager mSession;

    private Bus mBusEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_analytics);
        MixPanelController.track(MixPanelController.MY_ORDERS, null);
        mBusEvent = BusProvider.getInstance().getBus();
        mBusEvent.register(this);
        tvTotalOrders = (TextView) findViewById(R.id.tv_total_orders);
        tvTotalRevenue = (TextView) findViewById(R.id.tv_total_revenue);
        tvSuccessfullOrders = (TextView) findViewById(R.id.tv_successful_orders);
        tvReceivedOrders = (TextView) findViewById(R.id.tv_received_orders);
        tvCancelledOrders = (TextView) findViewById(R.id.tv_cancelled_orders);
        tvDisputedOrders = (TextView) findViewById(R.id.tv_disputed_orders);
        tvReturnedPrders = (TextView) findViewById(R.id.tv_returned_orders);
        tvAbandonedOrders = (TextView) findViewById(R.id.tv_abandoned_orders);
        tvCurrencyCode = (TextView) findViewById(R.id.tv_currency_code);

        pgTotalRevenue = (ProgressBar) findViewById(R.id.pb_total_revenue);
        pgTotalOrders = (ProgressBar) findViewById(R.id.pg_total_orders);
        pgSuccessfullOrders = (ProgressBar) findViewById(R.id.pg_successful_orders);
        pgReceivedOrders = (ProgressBar) findViewById(R.id.pg_received_orders);
        pgCancelledOrders = (ProgressBar) findViewById(R.id.pg_cancelled_orders);
        pgRefundedOrders = (ProgressBar) findViewById(R.id.pg_refunded_orders);
        pgAbandonedOrders = (ProgressBar) findViewById(R.id.pg_abandoned_orders);
        pgDisputedOrders = (ProgressBar) findViewById(R.id.pg_disputed_orders);
        revenueContainer = findViewById(R.id.ll_revenue_container);
        tvRevenueText = findViewById(R.id.tv_revenue_text);

        toolBar = (Toolbar) findViewById(R.id.toolbar);
        toolBar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));

        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setTitle("Orders");

        mSession = new UserSessionManager(this, this);


        init();


        if (!Constants.PACKAGE_NAME.equals("com.biz2.nowfloats")) {
            revenueContainer.setVisibility(View.INVISIBLE);
            tvRevenueText.setVisibility(View.INVISIBLE);
        } else {
            revenueContainer.setVisibility(View.VISIBLE);
            tvRevenueText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBusEvent.unregister(this);
    }

    public void onOrderTypeClicked(View v) {
        Intent i = new Intent(this, OrderListActivity.class);
        switch (v.getId()) {
            case R.id.cv_total_orders:
                i.putExtra(Constants.ORDER_TYPE, OrderListActivity.OrderType.TOTAL.ordinal());
                break;
            case R.id.cv_received_orders:
                i.putExtra(Constants.ORDER_TYPE, OrderListActivity.OrderType.RECEIVED.ordinal());
                break;
            case R.id.cv_successful_orders:
                i.putExtra(Constants.ORDER_TYPE, OrderListActivity.OrderType.SUCCESSFUL.ordinal());
                break;
            case R.id.cv_cancelled_orders:
                i.putExtra(Constants.ORDER_TYPE, OrderListActivity.OrderType.CANCELLED.ordinal());
                break;
            case R.id.cv_returned_orders:
                i.putExtra(Constants.ORDER_TYPE, OrderListActivity.OrderType.RETURNED.ordinal());
                break;
            case R.id.cv_disputed_orders:
                i.putExtra(Constants.ORDER_TYPE, OrderListActivity.OrderType.ESCALATED.ordinal());
                break;
            case R.id.cv_abandoned_orders:
                i.putExtra(Constants.ORDER_TYPE, OrderListActivity.OrderType.ABANDONED.ordinal());
                break;

        }
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    private void init() {


        showLoader();
        WebActionCallInterface callInterface = Constants.apAdapter.create(WebActionCallInterface.class);
        callInterface.getRevenueSummary(mSession.getFpTag(), new retrofit.Callback<SellerSummary>() {

            @Override
            public void success(SellerSummary revenueSummary, retrofit.client.Response response) {
                hideLoader(revenueSummary);
            }

            @Override
            public void failure(RetrofitError error) {
                hideLoader(null);
            }
        });

    }

    private void showLoader() {

        pgTotalOrders.setVisibility(View.VISIBLE);
        pgAbandonedOrders.setVisibility(View.VISIBLE);
        pgRefundedOrders.setVisibility(View.VISIBLE);
        pgCancelledOrders.setVisibility(View.VISIBLE);
        pgReceivedOrders.setVisibility(View.VISIBLE);
        pgSuccessfullOrders.setVisibility(View.VISIBLE);
        pgTotalOrders.setVisibility(View.VISIBLE);
        pgTotalRevenue.setVisibility(View.VISIBLE);
        pgDisputedOrders.setVisibility(View.VISIBLE);

        tvTotalOrders.setVisibility(View.INVISIBLE);
        tvTotalRevenue.setVisibility(View.INVISIBLE);
        tvAbandonedOrders.setVisibility(View.INVISIBLE);
        tvReturnedPrders.setVisibility(View.INVISIBLE);
        tvReceivedOrders.setVisibility(View.INVISIBLE);
        tvCancelledOrders.setVisibility(View.INVISIBLE);
        tvSuccessfullOrders.setVisibility(View.INVISIBLE);
        tvDisputedOrders.setVisibility(View.INVISIBLE);
        tvCurrencyCode.setVisibility(View.INVISIBLE);
    }


    private void hideLoader(SellerSummary revenueSummary) {
        pgTotalOrders.setVisibility(View.INVISIBLE);
        pgAbandonedOrders.setVisibility(View.INVISIBLE);
        pgRefundedOrders.setVisibility(View.INVISIBLE);
        pgCancelledOrders.setVisibility(View.INVISIBLE);
        pgReceivedOrders.setVisibility(View.INVISIBLE);
        pgSuccessfullOrders.setVisibility(View.INVISIBLE);
        pgTotalOrders.setVisibility(View.INVISIBLE);
        pgTotalRevenue.setVisibility(View.INVISIBLE);
        pgDisputedOrders.setVisibility(View.INVISIBLE);

        tvTotalOrders.setVisibility(View.VISIBLE);
        tvTotalRevenue.setVisibility(View.VISIBLE);
        tvAbandonedOrders.setVisibility(View.VISIBLE);
        tvReturnedPrders.setVisibility(View.VISIBLE);
        tvReceivedOrders.setVisibility(View.VISIBLE);
        tvCancelledOrders.setVisibility(View.VISIBLE);
        tvDisputedOrders.setVisibility(View.VISIBLE);
        tvCurrencyCode.setVisibility(View.VISIBLE);
        tvSuccessfullOrders.setVisibility(View.VISIBLE);

        if (revenueSummary != null && revenueSummary.getData() != null) {

            String revenue = revenueSummary.getData().getTotalRevenue().toString();
            boolean atleastOneAlpha = revenue.matches(".*[a-zA-Z]+.*");
            String[] amount = revenue.split("(?<=\\D)(?=\\d)");

            if (atleastOneAlpha) {
                tvTotalRevenue.setText(amount[1] + "");
                tvCurrencyCode.setText(amount[0]);
            } else {
                tvTotalRevenue.setText(revenue);
            }

            tvTotalOrders.setText(revenueSummary.getData().getTotalOrders() + "");
            tvSuccessfullOrders.setText(revenueSummary.getData().getTotalOrdersCompleted() + "");
            tvCancelledOrders.setText(revenueSummary.getData().getTotalOrdersCancelled() + "");
            tvDisputedOrders.setText(revenueSummary.getData().getTotalOrdersEscalated() + "");
            tvReceivedOrders.setText(revenueSummary.getData().getTotalOrdersInProgress() + "");
            tvAbandonedOrders.setText(revenueSummary.getData().getTotalOrdersAbandoned() + "");

//            tvTotalOrders.setText(mAllOrdersCount + "");
//            tvAbandonedOrders.setText(mAbandonedCarts + "");
//            tvReturnedPrders.setText(mReturnedOrders + "");
//            tvReceivedOrders.setText(mReceivedOrders + "");
//            tvCancelledOrders.setText(mCancelledOrders + "");
//            tvSuccessfullOrders.setText(mSuccessfulOrders + "");
        } else {
            tvTotalOrders.setText("-");
            tvTotalRevenue.setText("-");
            tvAbandonedOrders.setText("-");
            tvReturnedPrders.setText("-");
            tvReceivedOrders.setText("-");
            tvCancelledOrders.setText("-");
            tvDisputedOrders.setText("-");
            tvSuccessfullOrders.setText("-");
        }

    }


    @Subscribe
    public void refreshState(CommonStatus commonStatus) {
        init();
    }

}

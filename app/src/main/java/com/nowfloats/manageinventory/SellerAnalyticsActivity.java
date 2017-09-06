package com.nowfloats.manageinventory;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.manageinventory.interfaces.WebActionCallInterface;
import com.nowfloats.manageinventory.models.OrdersCountModel;
import com.nowfloats.manageinventory.models.TransactionModel;
import com.nowfloats.manageinventory.models.WebActionModel;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit.RetrofitError;


public class SellerAnalyticsActivity extends AppCompatActivity {

    private static String TOTAL_ORDERS_COUNT_URL = Constants.WA_BASE_URL +
            "orders3/aggregate-data" +
            "?match={$and:[{merchant_id:'%s'}, " +
            "{IsArchived:false}]}&group={_id:null, count:{$sum:1}}";
    private static String TOTAL_REVENUE_URL = Constants.WA_BASE_URL +
            "nf_transactions3/get-data" +
            "?query={merchant_id: '%s'}" +
            "&skip=0&limit=1&sort={CreatedOn: -1}";

    TextView tvTotalRevenue, tvTotalOrders, tvCurrencyCode, tvSuccessfullOrders, tvReceivedOrders, tvCancelledOrders, tvReturnedPrders, tvAbandonedOrders;
    ProgressBar pgTotalRevenue, pgTotalOrders, pgSuccessfullOrders, pgReceivedOrders, pgCancelledOrders, pgRefundedOrders, pgAbandonedOrders;
    Toolbar toolBar;

    private long mAllOrdersCount = 0, mSuccessfulOrders = 0, mCancelledOrders = 0,
            mReturnedOrders = 0, mReceivedOrders = 0, mAbandonedCarts = 0;

    private UserSessionManager mSession;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_analytics);

        tvTotalOrders = (TextView) findViewById(R.id.tv_total_orders);
        tvTotalRevenue = (TextView) findViewById(R.id.tv_total_revenue);
        tvSuccessfullOrders = (TextView) findViewById(R.id.tv_successful_orders);
        tvReceivedOrders = (TextView) findViewById(R.id.tv_received_orders);
        tvCancelledOrders = (TextView) findViewById(R.id.tv_cancelled_orders);
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

        toolBar = (Toolbar) findViewById(R.id.toolbar);
        toolBar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));

        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setTitle("Seller Analytics");

        mSession = new UserSessionManager(this, this);

        TOTAL_ORDERS_COUNT_URL = String.format(TOTAL_ORDERS_COUNT_URL, mSession.getFPID());
        TOTAL_REVENUE_URL = String.format(TOTAL_REVENUE_URL, mSession.getFPID());


        init();


    }

    public void onOrderTypeClicked(View v){
        Intent i = new Intent(this, OrderListActivity.class);
        switch (v.getId()){
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
            case R.id.cv_abandoned_orders:
                i.putExtra(Constants.ORDER_TYPE, OrderListActivity.OrderType.ABANDONED.ordinal());
                break;

        }
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    private void init(){

        OkHttpClient client = new OkHttpClient();
        final Gson gson  = new Gson();

        Request totalRevenueReq = new Request.Builder()
                .url(TOTAL_REVENUE_URL)
                .header("Authorization", Constants.WA_KEY)
                .build();

        client.newCall(totalRevenueReq).enqueue(new Callback() {
            Handler handler = new Handler(Looper.getMainLooper());
            @Override
            public void onFailure(Call call, IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        pgTotalRevenue.setVisibility(View.INVISIBLE);
                        tvTotalRevenue.setVisibility(View.VISIBLE);
                        tvTotalRevenue.setText("-");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String res = response.body().string();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        pgTotalRevenue.setVisibility(View.INVISIBLE);
                        tvTotalRevenue.setVisibility(View.VISIBLE);
                        tvCurrencyCode.setVisibility(View.VISIBLE);
                        try {

                            WebActionModel<TransactionModel> transaction = gson.fromJson(res,
                                    new TypeToken<WebActionModel<TransactionModel>>() {
                                    }.getType());

                            if (transaction != null && transaction.getData().size()>0) {
                                tvCurrencyCode.setText("INR");
                                tvTotalRevenue.setText(transaction.getData().get(0).getTotalRevenue()+"");
                            }else {
                                throw new NullPointerException("Orders Count is Null");
                            }
                        }catch (Exception e)
                        {
                            tvTotalRevenue.setText("-");
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        /*https://api.kitsune.tools/WebAction/v1/orders3/aggregate-data?pipeline=[{ $match: { IsArchived: false, merchant_id:'578f320f9ec66839b8f10947' } },
        {$group:{_id:{orderStatus:'$order_status', deliveryStatus:'$order_status_delivery'}, count:{$sum:1}}}]*/

        String countPipeline = String.format("[{ $match: { IsArchived: false, merchant_id:'%s'}}," +
                "{$group:{_id:{orderStatus:'$order_status', deliveryStatus:'$order_status_delivery'}, count:{$sum:1}}}]", mSession.getFPID());
        Constants.webActionAdapter.create(WebActionCallInterface.class).getAllOrdersCount(countPipeline, new retrofit.Callback<WebActionModel<OrdersCountModel>>() {
            @Override
            public void success(WebActionModel<OrdersCountModel> ordersCountModelWebActionModel, retrofit.client.Response response) {
                if(ordersCountModelWebActionModel!=null && ordersCountModelWebActionModel.getData().size()>0){
                    processOrderCounts(ordersCountModelWebActionModel);
                }else {
                    Methods.showSnackBarNegative(SellerAnalyticsActivity.this, "Error while retrieving number of orders");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                    Methods.showSnackBarNegative(SellerAnalyticsActivity.this, "Error while retrieving number of orders");
            }
        });


    }

    private void processOrderCounts(WebActionModel<OrdersCountModel> counts){
        for(OrdersCountModel countModel: counts.getData()){
            if(countModel.getId().getDeliveryStatus() == OrderListActivity.DeliveryStatus.ORDER_RECEIVED.ordinal()){
                mReceivedOrders+=countModel.getCount();
            }else if(countModel.getId().getOrderStatus() == OrderListActivity.OrderStatus.DELIVERED.ordinal()){
                mSuccessfulOrders+=countModel.getCount();
            }else if(countModel.getId().getOrderStatus() == OrderListActivity.OrderStatus.CANCELLED.ordinal()){
                mCancelledOrders+=countModel.getCount();
            }else if(countModel.getId().getOrderStatus() == OrderListActivity.OrderStatus.RETURNED.ordinal()){
                mReturnedOrders+=countModel.getCount();
            }else if(countModel.getId().getOrderStatus() == OrderListActivity.OrderStatus.NOT_INITIATED.ordinal()){
                mAbandonedCarts+=countModel.getCount();
            }
            mAllOrdersCount+=countModel.getCount();
        }

        pgTotalOrders.setVisibility(View.INVISIBLE);
        pgAbandonedOrders.setVisibility(View.INVISIBLE);
        pgRefundedOrders.setVisibility(View.INVISIBLE);
        pgCancelledOrders.setVisibility(View.INVISIBLE);
        pgReceivedOrders.setVisibility(View.INVISIBLE);
        pgSuccessfullOrders.setVisibility(View.INVISIBLE);

        tvTotalOrders.setText(mAllOrdersCount+"");
        tvAbandonedOrders.setText(mAbandonedCarts+"");
        tvReturnedPrders.setText(mReturnedOrders+"");
        tvReceivedOrders.setText(mReceivedOrders+"");
        tvCancelledOrders.setText(mCancelledOrders+"");
        tvSuccessfullOrders.setText(mSuccessfulOrders+"");

        tvTotalOrders.setVisibility(View.VISIBLE);
        tvAbandonedOrders.setVisibility(View.VISIBLE);
        tvReturnedPrders.setVisibility(View.VISIBLE);
        tvReceivedOrders.setVisibility(View.VISIBLE);
        tvCancelledOrders.setVisibility(View.VISIBLE);
        tvSuccessfullOrders.setVisibility(View.VISIBLE);
    }
}

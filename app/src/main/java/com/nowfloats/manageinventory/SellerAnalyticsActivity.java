package com.nowfloats.manageinventory;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.edmodo.cropper.cropwindow.handle.Handle;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.manageinventory.models.CountModel;
import com.nowfloats.manageinventory.models.TransactionModel;
import com.nowfloats.manageinventory.models.WebActionModel;
import com.nowfloats.util.Constants;
import com.thinksity.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class SellerAnalyticsActivity extends AppCompatActivity {

    private static String TOTAL_ORDERS_COUNT_URL = Constants.WA_BASE_URL +
            "orders3/aggregate-data" +
            "?match={$and:[{merchant_id:'%s'}, " +
            "{IsArchived:false}]}&group={_id:null, count:{$sum:1}}";
    private static String TOTAL_REVENUE_URL = Constants.WA_BASE_URL +
            "nf_transactions3/get-data" +
            "?query={merchant_id: '%s'}" +
            "&skip=0&limit=1&sort={CreatedOn: -1}";

    TextView tvTotalOrders, tvTotalRevenue;
    ProgressBar pgOrdersCount, pgTotalRevenue;
    Toolbar toolBar;

    private UserSessionManager mSession;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_analytics);

        tvTotalOrders = (TextView) findViewById(R.id.tv_total_orders_count);
        tvTotalRevenue = (TextView) findViewById(R.id.tv_total_revenue_count);

        pgOrdersCount = (ProgressBar) findViewById(R.id.pb_total_orders_count);
        pgTotalRevenue = (ProgressBar) findViewById(R.id.pb_total_revenue_count);

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
        Request ordersCountRequest = new Request.Builder()
                .url(TOTAL_ORDERS_COUNT_URL)
                .header("Authorization", Constants.WA_KEY)
                .build();

        final Gson gson  = new Gson();

        client.newCall(ordersCountRequest).enqueue(new Callback() {

            Handler handler = new Handler(Looper.getMainLooper());

            @Override
            public void onFailure(Call call, IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        pgOrdersCount.setVisibility(View.INVISIBLE);
                        tvTotalOrders.setVisibility(View.VISIBLE);
                        tvTotalOrders.setText("-");
                        //Toast.makeText(SellerAnalyticsActivity.this, "Unable to retrieve number of orders", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String res = response.body().string();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("SellerAnalyticsActivity", res);
                        pgOrdersCount.setVisibility(View.INVISIBLE);
                        tvTotalOrders.setVisibility(View.VISIBLE);

                        try {

                            WebActionModel<CountModel> ordersCountData = gson.fromJson(res,
                                    new TypeToken<WebActionModel<CountModel>>() {
                                    }.getType());

                            if (ordersCountData != null && ordersCountData.getData().size()>0) {
                                tvTotalOrders.setText(ordersCountData.getData().get(0).getCount()+"");
                                Log.d("TAG", ordersCountData.getData().get(0).getCount() + "");
                            }else {
                                throw new NullPointerException("Orders Count is Null");
                            }
                        }catch (Exception e)
                        {
                            tvTotalOrders.setText("-");
                            //Toast.makeText(SellerAnalyticsActivity.this, "Unable to retrieve number of orders", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

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
                        //Toast.makeText(SellerAnalyticsActivity.this, "Unable to retrieve total Revenue", Toast.LENGTH_SHORT).show();
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
                        try {

                            WebActionModel<TransactionModel> transaction = gson.fromJson(res,
                                    new TypeToken<WebActionModel<TransactionModel>>() {
                                    }.getType());

                            if (transaction != null && transaction.getData().size()>0) {
                                tvTotalRevenue.setText(transaction.getData().get(0).getTotalRevenue()+"");
                            }else {
                                throw new NullPointerException("Orders Count is Null");
                            }
                        }catch (Exception e)
                        {
                            tvTotalRevenue.setText("-");
                            //Toast.makeText(SellerAnalyticsActivity.this, "Unable to retrieve total revenue", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
}

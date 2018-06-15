package com.nowfloats.manageinventory;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nowfloats.manageinventory.adapters.OrderDetailsRvAdapter;
import com.nowfloats.manageinventory.interfaces.WebActionCallInterface;
import com.nowfloats.manageinventory.models.CommonStatus;
import com.nowfloats.manageinventory.models.MarkOrderAsShipped;
import com.nowfloats.manageinventory.models.OrderDataModel.Order;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.squareup.otto.Bus;
import com.thinksity.R;

import retrofit.RetrofitError;

public class OrderDetailsActivity extends AppCompatActivity {

    private Order mOrder;
    private String mTag;
    private RecyclerView rvOrderDetails;
    private ProgressBar pbOrderDetails;
    private Toolbar toolbar;
    private TextView tvPositive, tvNegative;
    private Bus mBusEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        mBusEvent = BusProvider.getInstance().getBus();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey("order")) {
                mOrder = (Order) bundle.get("order");
            }

            if (bundle.containsKey("tag")) {
                mTag = bundle.getString("tag");
            }
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        rvOrderDetails = (RecyclerView) findViewById(R.id.rv_order_details);
        pbOrderDetails = (ProgressBar) findViewById(R.id.pb_order_details);
        tvPositive = (TextView) findViewById(R.id.tvPositive);
        tvNegative = (TextView) findViewById(R.id.tvNegative);

        if (mOrder != null) {
            setTitle("ORDER ID: " + mOrder.getOrderId());
            showOrderDetails();

            if (mOrder.getStatus().equalsIgnoreCase(OrderListActivity.OrderStatus.PLACED)
                    || mOrder.getStatus().equalsIgnoreCase(OrderListActivity.OrderStatus.CONFIRMED)) {

                if (mOrder.getPaymentDetails() != null) {
                    if (mOrder.getPaymentDetails().
                            getMethod().equalsIgnoreCase("onlinepayment")
                            && mOrder.getPaymentDetails().getStatus().equalsIgnoreCase("Success")) {
                        performActions();
                    } else if (mOrder.getPaymentDetails().
                            getMethod().equalsIgnoreCase("COD")
                            && mOrder.getPaymentDetails().getStatus().equalsIgnoreCase("Pending")) {
                        performActions();
                    }
                }
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mBusEvent.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBusEvent.unregister(this);
    }

    private void performActions() {
        if (mOrder.getStatus().equalsIgnoreCase(OrderListActivity.OrderStatus.PLACED)) {
            confirmOrder();
        } else if (mOrder.getStatus().equalsIgnoreCase(OrderListActivity.OrderStatus.CONFIRMED)) {

            if (mOrder.getLogisticsDetails() != null && mOrder.getLogisticsDetails().getDeliveryConfirmationDetails() != null &&
                    !TextUtils.isEmpty(mOrder.getLogisticsDetails().getDeliveryConfirmationDetails().getNotificationSentOn())) {

            } else if (mOrder.getLogisticsDetails() != null &&
                    mOrder.getLogisticsDetails().getStatus().equalsIgnoreCase("Shipped")) {
                if (!mOrder.getMode().equalsIgnoreCase("PICKUP")) {
                    deliverOrder();
                }
            } else {
                shipOrder();
            }
        }
    }

    private void confirmOrder() {
        tvPositive.setVisibility(View.VISIBLE);
        tvNegative.setVisibility(View.VISIBLE);

        tvPositive.setText("Confirm");
        tvNegative.setText("Cancel");

        tvPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbOrderDetails.setVisibility(View.VISIBLE);
                WebActionCallInterface callInterface = Constants.apAdapter.create(WebActionCallInterface.class);
                callInterface.confirmOrder(mOrder.getOrderId(), new retrofit.Callback<CommonStatus>() {

                    @Override
                    public void success(CommonStatus commonStatus, retrofit.client.Response response) {
                        pbOrderDetails.setVisibility(View.GONE);
                        mBusEvent.post(commonStatus);
                        finish();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        pbOrderDetails.setVisibility(View.GONE);
                        Toast.makeText(OrderDetailsActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        tvNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbOrderDetails.setVisibility(View.VISIBLE);
                WebActionCallInterface callInterface = Constants.apAdapter.create(WebActionCallInterface.class);
                callInterface.cancelOrder(mOrder.getOrderId(), new retrofit.Callback<CommonStatus>() {

                    @Override
                    public void success(CommonStatus commonStatus, retrofit.client.Response response) {
                        pbOrderDetails.setVisibility(View.GONE);
                        mBusEvent.post(commonStatus);
                        finish();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        pbOrderDetails.setVisibility(View.GONE);
                        Toast.makeText(OrderDetailsActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private ShipOrderFragment shipOrderFragment;

    private void shipOrder() {
        tvPositive.setVisibility(View.VISIBLE);
        tvNegative.setVisibility(View.VISIBLE);

        if (!mOrder.getMode().equalsIgnoreCase("PICKUP")) {
            tvPositive.setText("Mark Order As Shipped");
            tvPositive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shipOrderFragment = ShipOrderFragment.newInstance();

                    shipOrderFragment.setResultListener(new ShipOrderFragment.OnResultReceive() {
                        @Override
                        public void OnResult(String shippedOn, String deliveryProvider, String trackingNumber,
                                             String trackingURL, double deliveryCharges) {

                            pbOrderDetails.setVisibility(View.VISIBLE);
                            MarkOrderAsShipped markOrderAsShipped = new MarkOrderAsShipped();
                            markOrderAsShipped.setDeliveryCharges(deliveryCharges);
                            markOrderAsShipped.setDeliveryProvider(deliveryProvider);
                            markOrderAsShipped.setShippedOn(shippedOn);
                            markOrderAsShipped.setTrackingNumber(trackingNumber);
                            markOrderAsShipped.setTrackingURL(trackingURL);


                            shipOrderFragment.setResultListener(null);
                            shipOrderFragment.dismiss();
                            shipOrderFragment = null;

                            WebActionCallInterface callInterface = Constants.apAdapter.create(WebActionCallInterface.class);
                            callInterface.markOrderAsShipped(markOrderAsShipped, new retrofit.Callback<CommonStatus>() {

                                @Override
                                public void success(CommonStatus commonStatus, retrofit.client.Response response) {
                                    pbOrderDetails.setVisibility(View.GONE);
                                    mBusEvent.post(commonStatus);
                                    finish();
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    pbOrderDetails.setVisibility(View.GONE);
                                    Toast.makeText(OrderDetailsActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });

                    shipOrderFragment.show(getFragmentManager(), "Test");
                }
            });
        } else {
            tvPositive.setText("Send Order Delivery Confirmation");

            tvPositive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pbOrderDetails.setVisibility(View.VISIBLE);
                    WebActionCallInterface callInterface = Constants.apAdapter.create(WebActionCallInterface.class);
                    callInterface.triggerOrderDeliveryConfirmation(mOrder.getOrderId(), "SELLER", new retrofit.Callback<CommonStatus>() {

                        @Override
                        public void success(CommonStatus commonStatus, retrofit.client.Response response) {
                            pbOrderDetails.setVisibility(View.GONE);
                            mBusEvent.post(commonStatus);
                            finish();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            pbOrderDetails.setVisibility(View.GONE);
                            Toast.makeText(OrderDetailsActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

        }

        tvNegative.setText("Cancel Order");

        tvNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbOrderDetails.setVisibility(View.VISIBLE);
                WebActionCallInterface callInterface = Constants.apAdapter.create(WebActionCallInterface.class);
                callInterface.cancelOrder(mOrder.getOrderId(), new retrofit.Callback<CommonStatus>() {

                    @Override
                    public void success(CommonStatus commonStatus, retrofit.client.Response response) {
                        pbOrderDetails.setVisibility(View.GONE);
                        mBusEvent.post(commonStatus);
                        finish();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        pbOrderDetails.setVisibility(View.GONE);
                        Toast.makeText(OrderDetailsActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void deliverOrder() {
        tvPositive.setVisibility(View.VISIBLE);
        tvNegative.setVisibility(View.VISIBLE);

        tvPositive.setText("Send Order Delivery Confirmation");
        tvNegative.setText("Escalate Order");

        tvPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbOrderDetails.setVisibility(View.VISIBLE);
                WebActionCallInterface callInterface = Constants.apAdapter.create(WebActionCallInterface.class);
                callInterface.triggerOrderDeliveryConfirmation(mOrder.getOrderId(), "SELLER", new retrofit.Callback<CommonStatus>() {

                    @Override
                    public void success(CommonStatus commonStatus, retrofit.client.Response response) {
                        pbOrderDetails.setVisibility(View.GONE);
                        mBusEvent.post(commonStatus);
                        finish();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        pbOrderDetails.setVisibility(View.GONE);
                        Toast.makeText(OrderDetailsActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        tvNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbOrderDetails.setVisibility(View.VISIBLE);
                WebActionCallInterface callInterface = Constants.apAdapter.create(WebActionCallInterface.class);
                callInterface.raiseOrderDispute(mOrder.getOrderId(), new retrofit.Callback<CommonStatus>() {

                    @Override
                    public void success(CommonStatus commonStatus, retrofit.client.Response response) {
                        pbOrderDetails.setVisibility(View.GONE);
                        mBusEvent.post(commonStatus);
                        finish();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        pbOrderDetails.setVisibility(View.GONE);
                        Toast.makeText(OrderDetailsActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);

    }

    private void showOrderDetails() {
        pbOrderDetails.setVisibility(View.GONE);
        rvOrderDetails.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvOrderDetails.setAdapter(new OrderDetailsRvAdapter(this, mTag, mOrder));
    }
}

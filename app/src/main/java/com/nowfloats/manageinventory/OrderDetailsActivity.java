package com.nowfloats.manageinventory;

import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
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
import com.nowfloats.manageinventory.models.OrderDetailDataModel;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.MixPanelController;
import com.squareup.otto.Bus;
import com.thinksity.R;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

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
        MixPanelController.track(MixPanelController.ORDER_DETAILS, null);

        mBusEvent = BusProvider.getInstance().getBus();
        Bundle bundle = getIntent().getExtras();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        rvOrderDetails = (RecyclerView) findViewById(R.id.rv_order_details);
        pbOrderDetails = (ProgressBar) findViewById(R.id.pb_order_details);
        tvPositive = (TextView) findViewById(R.id.tvPositive);
        tvNegative = (TextView) findViewById(R.id.tvNegative);

        if (bundle != null) {

            if (bundle.containsKey("tag")) {
                mTag = bundle.getString("tag");
            }

            if (bundle.containsKey("order")) {
                mOrder = (Order) bundle.get("order");
                showOrderDetails();
            } else if (bundle.containsKey("orderId")) {
                getOrderDetail(bundle.getString("orderId"));
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

            if (mOrder.getLogisticsDetails() != null &&
                    mOrder.getLogisticsDetails().getStatus().equalsIgnoreCase("Shipped")) {
                deliverOrder();
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
                MixPanelController.track(MixPanelController.CONFIRM_ORDER, null);
                pbOrderDetails.setVisibility(View.VISIBLE);
                WebActionCallInterface callInterface = Constants.apAdapter.create(WebActionCallInterface.class);
                callInterface.confirmOrder(mOrder.getOrderId(), new retrofit.Callback<CommonStatus>() {

                    @Override
                    public void success(CommonStatus commonStatus, retrofit.client.Response response) {

                        finishActions(commonStatus);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        pbOrderDetails.setVisibility(View.GONE);
                        Toast.makeText(OrderDetailsActivity.this, getString(R.string.something_went_wrong_), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        tvNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbOrderDetails.setVisibility(View.VISIBLE);
                MixPanelController.track(MixPanelController.CANCEL_ORDER, null);
                WebActionCallInterface callInterface = Constants.apAdapter.create(WebActionCallInterface.class);
                callInterface.cancelOrder(mOrder.getOrderId(), new retrofit.Callback<CommonStatus>() {

                    @Override
                    public void success(CommonStatus commonStatus, retrofit.client.Response response) {
                        finishActions(commonStatus);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        pbOrderDetails.setVisibility(View.GONE);
                        Toast.makeText(OrderDetailsActivity.this, getString(R.string.something_wen_wrong), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private ShipOrderFragment shipOrderFragment;

    private void shipOrder() {
        tvPositive.setVisibility(View.VISIBLE);
        tvNegative.setVisibility(View.VISIBLE);

        if (mOrder.getMode().equalsIgnoreCase("PICKUP")) {

            if (mOrder.getLogisticsDetails().getDeliveryConfirmationDetails() != null
                    && !TextUtils.isEmpty(mOrder.getLogisticsDetails().
                    getDeliveryConfirmationDetails().getNotificationSentOn())) {

                tvPositive.setVisibility(View.GONE);
                tvNegative.setText("Escalate Order");
                tvNegative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MixPanelController.track(MixPanelController.ESCALATE_ORDER, null);
                        pbOrderDetails.setVisibility(View.VISIBLE);
                        WebActionCallInterface callInterface = Constants.apAdapter.create(WebActionCallInterface.class);
                        callInterface.raiseOrderDispute(mOrder.getOrderId(), new retrofit.Callback<CommonStatus>() {

                            @Override
                            public void success(CommonStatus commonStatus, retrofit.client.Response response) {
                                finishActions(commonStatus);
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                pbOrderDetails.setVisibility(View.GONE);
                                Toast.makeText(OrderDetailsActivity.this, getString(R.string.something_wen_wrong), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });


            } else {

                tvPositive.setText("Mark Order as Picked Up");

                tvPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MixPanelController.track(MixPanelController.DELIVER_ORDER, null);
                        pbOrderDetails.setVisibility(View.VISIBLE);
                        WebActionCallInterface callInterface = Constants.apAdapter.create(WebActionCallInterface.class);
                        callInterface.triggerOrderDeliveryConfirmation(mOrder.getOrderId(), "SELLER", new retrofit.Callback<CommonStatus>() {

                            @Override
                            public void success(CommonStatus commonStatus, retrofit.client.Response response) {
                                finishActions(commonStatus);
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                pbOrderDetails.setVisibility(View.GONE);
                                Toast.makeText(OrderDetailsActivity.this, getString(R.string.something_wen_wrong), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                tvNegative.setText(R.string.cancel_order);

                tvNegative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MixPanelController.track(MixPanelController.CANCEL_ORDER, null);
                        pbOrderDetails.setVisibility(View.VISIBLE);
                        WebActionCallInterface callInterface = Constants.apAdapter.create(WebActionCallInterface.class);
                        callInterface.cancelOrder(mOrder.getOrderId(), new retrofit.Callback<CommonStatus>() {

                            @Override
                            public void success(CommonStatus commonStatus, retrofit.client.Response response) {
                                finishActions(commonStatus);
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                pbOrderDetails.setVisibility(View.GONE);
                                Toast.makeText(OrderDetailsActivity.this, getString(R.string.something_wen_wrong), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }

        } else {

            tvPositive.setText(R.string.mark_order_as_shipped);
            tvPositive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MixPanelController.track(MixPanelController.SHIP_ORDER, null);
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
                            markOrderAsShipped.setShippedBy("SELLER");
                            markOrderAsShipped.setTrackingNumber(trackingNumber);
                            markOrderAsShipped.setTrackingURL(trackingURL);
                            markOrderAsShipped.setOrderId(mOrder.getOrderId());


                            shipOrderFragment.setResultListener(null);
                            shipOrderFragment.dismiss();
                            shipOrderFragment = null;

                            WebActionCallInterface callInterface = Constants.apAdapter.create(WebActionCallInterface.class);
                            callInterface.markOrderAsShipped(markOrderAsShipped, new retrofit.Callback<CommonStatus>() {

                                @Override
                                public void success(CommonStatus commonStatus, retrofit.client.Response response) {
                                    finishActions(commonStatus);
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    pbOrderDetails.setVisibility(View.GONE);
                                    Toast.makeText(OrderDetailsActivity.this, getString(R.string.something_wen_wrong), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });

                    shipOrderFragment.show(getFragmentManager(), "Test");
                }
            });

            tvNegative.setText("Cancel Order");

            tvNegative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MixPanelController.track(MixPanelController.CANCEL_ORDER, null);
                    pbOrderDetails.setVisibility(View.VISIBLE);
                    WebActionCallInterface callInterface = Constants.apAdapter.create(WebActionCallInterface.class);
                    callInterface.cancelOrder(mOrder.getOrderId(), new retrofit.Callback<CommonStatus>() {

                        @Override
                        public void success(CommonStatus commonStatus, retrofit.client.Response response) {
                            finishActions(commonStatus);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            pbOrderDetails.setVisibility(View.GONE);
                            Toast.makeText(OrderDetailsActivity.this, getString(R.string.something_wen_wrong), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

        }

    }

    private void deliverOrder() {
        tvPositive.setVisibility(View.VISIBLE);
        tvNegative.setVisibility(View.VISIBLE);

        tvPositive.setText("Send Order Delivery Confirmation");
        tvNegative.setText("Escalate Order");

        if (mOrder.getLogisticsDetails().getDeliveryConfirmationDetails() != null
                && !TextUtils.isEmpty(mOrder.getLogisticsDetails().
                getDeliveryConfirmationDetails().getNotificationSentOn())) {
            tvPositive.setVisibility(View.GONE);
        } else {

            tvPositive.setVisibility(View.VISIBLE);
            tvPositive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MixPanelController.track(MixPanelController.DELIVER_ORDER, null);
                    pbOrderDetails.setVisibility(View.VISIBLE);
                    WebActionCallInterface callInterface = Constants.apAdapter.create(WebActionCallInterface.class);
                    callInterface.triggerOrderDeliveryConfirmation(mOrder.getOrderId(), "SELLER", new retrofit.Callback<CommonStatus>() {

                        @Override
                        public void success(CommonStatus commonStatus, retrofit.client.Response response) {
                            finishActions(commonStatus);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            pbOrderDetails.setVisibility(View.GONE);
                            Toast.makeText(OrderDetailsActivity.this, getString(R.string.something_wen_wrong), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }

        tvNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MixPanelController.track(MixPanelController.ESCALATE_ORDER, null);
                pbOrderDetails.setVisibility(View.VISIBLE);
                WebActionCallInterface callInterface = Constants.apAdapter.create(WebActionCallInterface.class);
                callInterface.raiseOrderDispute(mOrder.getOrderId(), new retrofit.Callback<CommonStatus>() {

                    @Override
                    public void success(CommonStatus commonStatus, retrofit.client.Response response) {
                        finishActions(commonStatus);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        pbOrderDetails.setVisibility(View.GONE);
                        Toast.makeText(OrderDetailsActivity.this, getString(R.string.something_wen_wrong), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void finishActions(CommonStatus commonStatus) {
        mBusEvent.post(commonStatus);
        getOrderDetail(mOrder.getOrderId());
    }

    private void getOrderDetail(String orderId) {
        WebActionCallInterface callInterface = Constants.apAdapter.create(WebActionCallInterface.class);

        callInterface.getOrdersDetails(orderId, new Callback<OrderDetailDataModel>() {
            @Override
            public void success(OrderDetailDataModel orderModelWebActionModel, Response response) {
                mOrder = orderModelWebActionModel.getOrder();
                showOrderDetails();
            }

            @Override
            public void failure(RetrofitError error) {
                pbOrderDetails.setVisibility(View.GONE);
                Toast.makeText(OrderDetailsActivity.this, getString(R.string.something_wen_wrong), Toast.LENGTH_SHORT).show();
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
        tvPositive.setVisibility(View.GONE);
        tvNegative.setVisibility(View.GONE);
        setTitle("ORDER ID: " + mOrder.getReferenceNumber());
        if (mOrder.getStatus().equalsIgnoreCase(OrderListActivity.OrderStatus.INITIATED)) {
            mTag = "Initiated";
        } else if (mOrder.getStatus().equalsIgnoreCase(OrderListActivity.OrderStatus.PLACED)) {
            mTag = "PLACED";
        } else if (mOrder.getStatus().equalsIgnoreCase(OrderListActivity.OrderStatus.CONFIRMED)) {
            mTag = "Confirmed";
        } else if (mOrder.getStatus().equalsIgnoreCase(OrderListActivity.OrderStatus.COMPLETED)) {
            mTag = "Completed";
        } else if (mOrder.getStatus().equalsIgnoreCase(OrderListActivity.OrderStatus.CANCELLED)) {
            mTag = "Cancelled";
        } else if (mOrder.getStatus().equalsIgnoreCase(OrderListActivity.OrderStatus.ESCALATED)) {
            mTag = "Escalated";
        }
        rvOrderDetails.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvOrderDetails.setAdapter(new OrderDetailsRvAdapter(this, mTag, mOrder));
        processOrder();
    }

    private void processOrder() {

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

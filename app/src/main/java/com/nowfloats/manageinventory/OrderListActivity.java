package com.nowfloats.manageinventory;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.manageinventory.adapters.OrdersRvAdapter;
import com.nowfloats.manageinventory.interfaces.WebActionCallInterface;
import com.nowfloats.manageinventory.models.CommonStatus;
import com.nowfloats.manageinventory.models.OrderDataModel;
import com.nowfloats.manageinventory.models.OrderDataModel.Order;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.thinksity.R;


import java.util.HashMap;

import io.codetail.animation.ViewAnimationUtils;
import io.codetail.widget.RevealFrameLayout;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class OrderListActivity extends AppCompatActivity implements OrdersRvAdapter.PaginationAdapterCallback,
        OrdersRvAdapter.OnRecyclerViewItemClickListener, View.OnClickListener {

    private Bus mBusEvent;

    @Override
    public void retryPageLoad() {
        loadNextPage();
    }

    @Override
    public void onItemClick(View v, int position) {
        Order orderModel = mAdapter.getItem(position);
//        ArrayList<ProductModel> productList = mAdapter.getProductsForOrder(orderModel.getId());
        openOrderDetails(orderModel, ((TextView) v.findViewById(R.id.tv_order_status_tag)).getText().toString());
    }

    private void openOrderDetails(Order orderModel, String tag) {
        Intent i = new Intent(this, OrderDetailsActivity.class);
        i.putExtra("order", orderModel);
        i.putExtra("tag", tag);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onClick(View v) {
        //hideRevealView();
        switch (v.getId()) {
            case R.id.rfl_overlay:
                if (!mHidden) {
                    showRevealView();
                }
                break;
            case R.id.rl_received_orders:
                showRevealView();
                showOrders(OrderType.RECEIVED.ordinal());
                break;
            case R.id.rl_abandoned_orders:
                showRevealView();
                showOrders(OrderType.ABANDONED.ordinal());
                break;
            case R.id.rl_cancelled_orders:
                showRevealView();
                showOrders(OrderType.CANCELLED.ordinal());
                break;
            case R.id.rl_disputed_orders:
                showRevealView();
                showOrders(OrderType.RETURNED.ordinal());
                break;
            case R.id.rl_successful_orders:
                showRevealView();
                showOrders(OrderType.SUCCESSFUL.ordinal());
                break;
            case R.id.rl_total_orders:
                showRevealView();
                showOrders(OrderType.TOTAL.ordinal());
                break;
        }
    }

    @Subscribe
    public void refreshState(CommonStatus commonStatus) {
        mSkip = 0;
        getOrders(mQuery, mSkip, LIMIT);
    }

    public enum OrderType {
        TOTAL, RECEIVED, SUCCESSFUL, CANCELLED, RETURNED, ABANDONED, ESCALATED
    }

//    public enum OrderStatus {
//        NOT_INITIATED,
//        PROCESSING,
//        ACCEPTED,
//        DISPATCHED,
//        TRANSIT,
//        DELIVERED,
//        RETURNED,
//        CANCELLED
//    }


    public enum PaymentStatus {
        NOT_INITIATED,
        FAILED,
        PENDING,
        SUCCESS,
        REFUNDED
    }

    public enum DeliveryStatus {
        NOT_INITIATED,
        ORDER_RECEIVED,
        DISPATCHED,
        TRANSIT,
        DELIVERED,
        RETURNED,
        CANCELLED
    }

    public static class OrderStatus {
        public static final String INITIATED = "INITIATED";
        public static final String PLACED = "PLACED";
        public static final String CONFIRMED = "CONFIRMED";
        public static final String COMPLETED = "COMPLETED";
        public static final String CANCELLED = "CANCELLED";
        public static final String ESCALATED = "ESCALATED";
    }

    private String mQuery, orderStatus = "", emptyMsg = "";
    private long mSkip = 0;
    private static final int LIMIT = 20;

    private static final int PAGE_START = 1;

    private boolean isLoading = false;
    private boolean isLastPage = false;
    private boolean mHidden = true;

    private int currentPage = PAGE_START;

    private UserSessionManager mSession;
    //private List<OrderModel> mOrderList = new ArrayList<>();
    //private Map<String, List<ProductModel>> mProductMap = new HashMap<>();
    private OrdersRvAdapter mAdapter;

    RecyclerView rvOrderList;
    Toolbar toolbar;
    LinearLayout llRevealLayout;
    RevealFrameLayout rflOverLay;
    View mCurrSelectedView;
    ProgressBar pbLoading;
    LinearLayout llEmptyView;
    TextView tvEmptyText;
    MaterialSearchView searchView;
    //View dropDownView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        mBusEvent = BusProvider.getInstance().getBus();
        mBusEvent.register(this);
        mSession = new UserSessionManager(this, this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Orders");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        llRevealLayout = (LinearLayout) findViewById(R.id.ll_reveal_layout);
        rflOverLay = (RevealFrameLayout) findViewById(R.id.rfl_overlay);
        llRevealLayout.setVisibility(View.INVISIBLE);

        findViewById(R.id.rl_abandoned_orders).setOnClickListener(this);
        findViewById(R.id.rl_cancelled_orders).setOnClickListener(this);
        findViewById(R.id.rl_received_orders).setOnClickListener(this);
        findViewById(R.id.rl_disputed_orders).setOnClickListener(this);
        findViewById(R.id.rl_successful_orders).setOnClickListener(this);
        findViewById(R.id.rl_total_orders).setOnClickListener(this);

        llEmptyView = (LinearLayout) findViewById(R.id.ll_empty_view);
        tvEmptyText = (TextView) findViewById(R.id.tv_empty_text);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);


        rvOrderList = (RecyclerView) findViewById(R.id.rv_orders);
        mAdapter = new OrdersRvAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvOrderList.setAdapter(mAdapter);
        rvOrderList.setLayoutManager(layoutManager);
        rvOrderList.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                loadNextPage();
            }

            @Override
            public int getTotalPageCount() {
                return 5;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        if (getIntent().hasExtra(Constants.ORDER_TYPE)) {
            showOrders(getIntent().getIntExtra(Constants.ORDER_TYPE, -1));
        } else {
            showEmptyLayout("You don't have any Order");
        }

        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchByOrderId(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                if (!mHidden) {
                    showRevealView();
                }
            }

            @Override
            public void onSearchViewClosed() {
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBusEvent.unregister(this);
    }

    private void searchByOrderId(String query) {
//        mQuery = String.format(Locale.getDefault(), "{merchant_id:'%s', order_id:'%s'}", mSession.getFPID(), query.toUpperCase());
        setTitle("Search");
        mCurrSelectedView.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        pbLoading.setVisibility(View.VISIBLE);
        getOrders(mQuery, mSkip, LIMIT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.orders_menu, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        //dropDownView = MenuItemCompat.getActionView(menu.findItem(R.id.action_orders_filter));

        return super.onCreateOptionsMenu(menu);
    }

    private void showRevealView() {
        int cx = (llRevealLayout.getLeft() + llRevealLayout.getRight() - Methods.dpToPx(80, this));
        int cy = llRevealLayout.getTop();
        int radius = Math.max(llRevealLayout.getWidth(), llRevealLayout.getHeight());

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Animator animator =
                    ViewAnimationUtils.createCircularReveal(llRevealLayout, cx, cy, 0, radius);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(700);

            if (mHidden) {
                llRevealLayout.setVisibility(View.VISIBLE);
                rflOverLay.setBackgroundColor(Color.parseColor("#66000000"));
                rflOverLay.setClickable(true);
                rflOverLay.setOnClickListener(this);
                animator.start();
                mHidden = false;
            } else {
                Animator anim = ViewAnimationUtils.
                        createCircularReveal(llRevealLayout, cx, cy, radius, 0);
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        llRevealLayout.setVisibility(View.INVISIBLE);
                        rflOverLay.setBackgroundColor(ContextCompat.getColor(OrderListActivity.this, R.color.transparent));
                        rflOverLay.setOnClickListener(null);
                        rflOverLay.setClickable(false);
                        mHidden = true;
                    }
                });
                anim.start();
            }
        } else {
            if (mHidden) {
                Animator anim = android.view.ViewAnimationUtils.
                        createCircularReveal(llRevealLayout, cx, cy, 0, radius);
                llRevealLayout.setVisibility(View.VISIBLE);
                rflOverLay.setBackgroundColor(Color.parseColor("#66000000"));
                rflOverLay.setClickable(true);
                rflOverLay.setOnClickListener(this);
                anim.start();
                mHidden = false;
            } else {
                Animator anim = android.view.ViewAnimationUtils.
                        createCircularReveal(llRevealLayout, cx, cy, radius, 0);
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        llRevealLayout.setVisibility(View.INVISIBLE);
                        rflOverLay.setBackgroundColor(ContextCompat.getColor(OrderListActivity.this, R.color.transparent));
                        rflOverLay.setOnClickListener(null);
                        rflOverLay.setClickable(false);
                        mHidden = true;
                    }
                });
                anim.start();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.action_orders_filter) {
            if (searchView.isSearchOpen()) {
                searchView.closeSearch();
            }
            showRevealView();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else if (!mHidden) {
            showRevealView();
        } else {
            super.onBackPressed();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
    }

    private void showEmptyLayout(String msg) {
        tvEmptyText.setText(msg);
        llEmptyView.setVisibility(View.VISIBLE);
    }

    private void showOrders(final int orderType) {
        pbLoading.setVisibility(View.VISIBLE);
        if (mCurrSelectedView != null) {
            mCurrSelectedView.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        }
        switch (orderType) {
            case 0:
//                mQuery = String.format(Locale.getDefault(), "{merchant_id:'%s'}", mSession.getFPID());
                setTitle("Total Orders");
                orderStatus = "";
                emptyMsg = "You don't have Any Order";
                mCurrSelectedView = findViewById(R.id.rl_total_orders);
                getOrders(mQuery, mSkip, LIMIT);
                break;
            case 1:
//                mQuery = String.format(Locale.getDefault(), "{merchant_id:'%s', order_status_delivery:%d}", mSession.getFPID(), DeliveryStatus.ORDER_RECEIVED.ordinal());
                setTitle("Received Orders");
                orderStatus = OrderStatus.PLACED;
                emptyMsg = "You don't have any Received Order";
                mCurrSelectedView = findViewById(R.id.rl_received_orders);
                getOrders(mQuery, mSkip, LIMIT);
                break;
            case 2:
//                mQuery = String.format(Locale.getDefault(), "{merchant_id:'%s', order_status:%d}", mSession.getFPID(), OrderStatus.DELIVERED.ordinal());
                setTitle("Delivered Orders");
                orderStatus = OrderStatus.COMPLETED;
                emptyMsg = "You don't have any Successful Order";
                mCurrSelectedView = findViewById(R.id.rl_successful_orders);
                getOrders(mQuery, mSkip, LIMIT);
                break;
            case 3:
//                mQuery = String.format(Locale.getDefault(), "{merchant_id:'%s', order_status:%d}", mSession.getFPID(), OrderStatus.CANCELLED.ordinal());
                setTitle("Cancelled Orders");
                orderStatus = OrderStatus.CANCELLED;
                emptyMsg = "You don't have any Cancelled Order";
                mCurrSelectedView = findViewById(R.id.rl_cancelled_orders);
                getOrders(mQuery, mSkip, LIMIT);
                break;
            case 4:
//                mQuery = String.format(Locale.getDefault(), "{merchant_id:'%s', order_status:%d}", mSession.getFPID(), OrderStatus.RETURNED.ordinal());
                setTitle("Disputed Orders");
                orderStatus = OrderStatus.ESCALATED;
                emptyMsg = "You don't have any Disputed Order";
                mCurrSelectedView = findViewById(R.id.rl_disputed_orders);
                getOrders(mQuery, mSkip, LIMIT);
                break;
            case 5:
//                mQuery = String.format(Locale.getDefault(), "{merchant_id:'%s', order_status:%d}", mSession.getFPID(), OrderStatus.NOT_INITIATED.ordinal());
                setTitle("Abandoned Orders");
                orderStatus = "";
                emptyMsg = "You don't have any Abandoned Order";
                mCurrSelectedView = findViewById(R.id.rl_abandoned_orders);
                getOrders(mQuery, mSkip, LIMIT);
                break;
            case 6:
//                mQuery = String.format(Locale.getDefault(), "{merchant_id:'%s', order_status:%d}", mSession.getFPID(), OrderStatus.RETURNED.ordinal());
                setTitle("Disputed Orders");
                orderStatus = OrderStatus.ESCALATED;
                mCurrSelectedView = findViewById(R.id.rl_disputed_orders);
                emptyMsg = "You don't have any Disputed Order";
                getOrders(mQuery, mSkip, LIMIT);
                break;
        }
        mCurrSelectedView.setBackgroundColor(Color.parseColor("#E8E8E8"));
    }


    private void getOrders(final String orderQuery, final long skip, final int limit) {
        hideEmptyLayout();

        WebActionCallInterface callInterface = Constants.apAdapter.create(WebActionCallInterface.class);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("sellerId", mSession.getFpTag());
        if (!TextUtils.isEmpty(orderStatus))
            hashMap.put("orderStatus", orderStatus);
        callInterface.getOrdersList(hashMap, skip, limit, orderStatusCallback);
    }

    public Callback<OrderDataModel> orderStatusCallback = new Callback<OrderDataModel>() {

        @Override
        public void success(OrderDataModel orderDataModel, Response response) {

            pbLoading.setVisibility(View.GONE);
            if (orderStatus.equalsIgnoreCase(OrderStatus.CONFIRMED) && !mAdapter.isEmpty()) {
                refreshOrders(orderDataModel);
            } else {
                mAdapter.clearAdapter();
                if (orderDataModel == null || orderDataModel.getData() == null) {
                    showEmptyLayout(emptyMsg);
                } else {
                    refreshOrders(orderDataModel);
                }
            }

        }

        @Override
        public void failure(RetrofitError error) {
            Toast.makeText(OrderListActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
            pbLoading.setVisibility(View.GONE);
            if (orderStatus.equalsIgnoreCase(OrderStatus.CONFIRMED) && !mAdapter.isEmpty()) {

            } else {
                showEmptyLayout(emptyMsg);
                if (orderStatus.equalsIgnoreCase(OrderStatus.PLACED)) {
                    orderStatus = OrderStatus.CONFIRMED;
                    getOrders(mQuery, mSkip, LIMIT);
                }
            }
        }
    };

    private void hideEmptyLayout() {
        llEmptyView.setVisibility(View.GONE);
    }

    private void refreshOrders(final OrderDataModel ordersModelWebAction) {
        mAdapter.addAll(ordersModelWebAction.getData().getOrders());
    }

    private void loadNextPage() {
        WebActionCallInterface callInterface = Constants.apAdapter.create(WebActionCallInterface.class);
        mSkip += LIMIT;
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("sellerId", mSession.getFpTag());
        if (!TextUtils.isEmpty(orderStatus))
            hashMap.put("orderStatus", orderStatus);

        callInterface.getOrdersList(hashMap, mSkip, LIMIT, new Callback<OrderDataModel>() {
            @Override
            public void success(OrderDataModel orderModelWebActionModel, Response response) {
                mAdapter.removeLoadingFooter();
                isLoading = false;
                refreshOrders(orderModelWebActionModel);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(OrderListActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                if (orderStatus.equalsIgnoreCase(OrderStatus.PLACED)) {
                    mSkip = 0;
                    orderStatus = OrderStatus.CONFIRMED;
                    getOrders(mQuery, mSkip, LIMIT);
                }
            }
        });
    }


}

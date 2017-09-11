package com.nowfloats.manageinventory;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
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
import com.nowfloats.manageinventory.models.OrderModel;
import com.nowfloats.manageinventory.models.ProductModel;
import com.nowfloats.manageinventory.models.WebActionModel;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.codetail.animation.ViewAnimationUtils;
import io.codetail.widget.RevealFrameLayout;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class OrderListActivity extends AppCompatActivity implements OrdersRvAdapter.PaginationAdapterCallback,
        OrdersRvAdapter.OnRecyclerViewItemClickListener, View.OnClickListener{

    @Override
    public void retryPageLoad() {
        loadNextPage();
    }

    @Override
    public void onItemClick(View v, int position) {
        OrderModel orderModel = mAdapter.getItem(position);
        ArrayList<ProductModel> productList = mAdapter.getProductsForOrder(orderModel.getId());
        openOrderDetails(orderModel, productList, ((TextView)v.findViewById(R.id.tv_order_status_tag)).getText().toString());
    }

    private void openOrderDetails(OrderModel orderModel, ArrayList<ProductModel> productModels, String tag) {
        Intent i = new Intent(this, OrderDetailsActivity.class);
        i.putExtra("order", orderModel);
        i.putExtra("products", productModels);
        i.putExtra("tag", tag);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onClick(View v) {
        //hideRevealView();
        switch (v.getId()){
            case R.id.rfl_overlay:
                if(!mHidden){
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
            case R.id.rl_returned_orders:
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

    public enum OrderType{
        TOTAL, RECEIVED, SUCCESSFUL, CANCELLED, RETURNED, ABANDONED
    }

    public enum OrderStatus
    {
        NOT_INITIATED,
        PROCESSING,
        ACCEPTED,
        DISPATCHED,
        TRANSIT,
        DELIVERED,
        RETURNED,
        CANCELLED
    }

    public enum PaymentStatus
    {
        NOT_INITIATED,
        FAILED,
        PENDING,
        SUCCESS,
        REFUNDED
    }

    public enum DeliveryStatus
    {
        NOT_INITIATED,
        ORDER_RECEIVED,
        DISPATCHED,
        TRANSIT,
        DELIVERED,
        RETURNED,
        CANCELLED
    }

    private String mQuery;
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
        findViewById(R.id.rl_returned_orders).setOnClickListener(this);
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

        if(getIntent().hasExtra(Constants.ORDER_TYPE)){
            showOrders(getIntent().getIntExtra(Constants.ORDER_TYPE, -1));
        }else {
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
                if(!mHidden){
                    showRevealView();
                }
            }

            @Override
            public void onSearchViewClosed() {
            }
        });
    }

    private void searchByOrderId(String query) {
        mQuery = String.format(Locale.getDefault(), "{merchant_id:'%s', order_id:'%s'}", mSession.getFPID(), query.toUpperCase());
        setTitle("Search");
        mCurrSelectedView.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        pbLoading.setVisibility(View.VISIBLE);
        getOrders(mQuery, mSkip, LIMIT, "OrderId not found");
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

    private void showRevealView(){
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
        }
        else {
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

        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }else if(item.getItemId() == R.id.action_orders_filter){
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
        }else if(!mHidden){
            showRevealView();
        }else {
            super.onBackPressed();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
    }

    private void showEmptyLayout(String msg) {
        tvEmptyText.setText(msg);
        llEmptyView.setVisibility(View.VISIBLE);
    }

    private void showOrders(final int orderType){
        pbLoading.setVisibility(View.VISIBLE);
        if(mCurrSelectedView!=null){
            mCurrSelectedView.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        }
        switch (orderType){
            case 0:
                mQuery = String.format(Locale.getDefault(), "{merchant_id:'%s'}", mSession.getFPID());
                setTitle("Total Orders");
                mCurrSelectedView = findViewById(R.id.rl_total_orders);
                getOrders(mQuery, mSkip, LIMIT, "You don't have Any Order");
                break;
            case 1:
                mQuery = String.format(Locale.getDefault(), "{merchant_id:'%s', order_status_delivery:%d}", mSession.getFPID(), DeliveryStatus.ORDER_RECEIVED.ordinal());
                setTitle("Received Orders");
                mCurrSelectedView = findViewById(R.id.rl_received_orders);
                getOrders(mQuery, mSkip, LIMIT, "You don't have any Received Order");
                break;
            case 2:
                mQuery = String.format(Locale.getDefault(), "{merchant_id:'%s', order_status:%d}", mSession.getFPID(), OrderStatus.DELIVERED.ordinal());
                setTitle("Delivered Orders");
                mCurrSelectedView = findViewById(R.id.rl_successful_orders);
                getOrders(mQuery, mSkip, LIMIT, "You don't have any Successful Order");
                break;
            case 3:
                mQuery = String.format(Locale.getDefault(), "{merchant_id:'%s', order_status:%d}", mSession.getFPID(), OrderStatus.CANCELLED.ordinal());
                setTitle("Cancelled Orders");
                mCurrSelectedView = findViewById(R.id.rl_cancelled_orders);
                getOrders(mQuery, mSkip, LIMIT, "You don't have any Cancelled Order");
                break;
            case 4:
                mQuery = String.format(Locale.getDefault(), "{merchant_id:'%s', order_status:%d}", mSession.getFPID(), OrderStatus.RETURNED.ordinal());
                setTitle("Returned Orders");
                mCurrSelectedView = findViewById(R.id.rl_returned_orders);
                getOrders(mQuery, mSkip, LIMIT, "You don't have any Returned Order");
                break;
            case 5:
                mQuery = String.format(Locale.getDefault(), "{merchant_id:'%s', order_status:%d}", mSession.getFPID(), OrderStatus.NOT_INITIATED.ordinal());
                setTitle("Abandoned Orders");
                mCurrSelectedView = findViewById(R.id.rl_abandoned_orders);
                getOrders(mQuery, mSkip, LIMIT, "You don't have any Abandoned Order");

        }
        mCurrSelectedView.setBackgroundColor(Color.parseColor("#E8E8E8"));
    }



    private void getOrders(final String orderQuery, final long skip, final int limit, final String emptyMsg){
        hideEmptyLayout();
        WebActionCallInterface callInterface = Constants.webActionAdapter.create(WebActionCallInterface.class);
        callInterface.getOrders(orderQuery, skip, limit, "{CreatedOn:-1}", new Callback<WebActionModel<OrderModel>>() {
            @Override
            public void success(WebActionModel<OrderModel> orderModelWebActionModel, Response response) {
                pbLoading.setVisibility(View.GONE);
                mAdapter.clearAdapter();
                if(orderModelWebActionModel==null || orderModelWebActionModel.getData().size()==0){
                    showEmptyLayout(emptyMsg);
                }else {
                    getProductsForOrders(orderModelWebActionModel);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(OrderListActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                pbLoading.setVisibility(View.GONE);
                showEmptyLayout(emptyMsg);
            }
        });

    }

    private void hideEmptyLayout() {
        llEmptyView.setVisibility(View.GONE);
    }

    private void getProductsForOrders(final WebActionModel<OrderModel> ordersModelWebAction) {
        if(ordersModelWebAction == null || ordersModelWebAction.getData().size()==0)
            return;
        final List<OrderModel> data = ordersModelWebAction.getData();
        StringBuilder builder = new StringBuilder("");
        for (OrderModel order : data){
            builder.append(String.format("'%s',", order.getId()));
        }
        if(builder.length()>0) {
            builder.deleteCharAt(builder.length() - 1);
        }

        String query = String.format("{Order_id:{$in:[%s]}}", builder.toString());

        WebActionCallInterface callInterface = Constants.webActionAdapter.create(WebActionCallInterface.class);
        callInterface.getProducts(query, 200, new Callback<WebActionModel<ProductModel>>() {
            @Override
            public void success(WebActionModel<ProductModel> productDetailsWA, Response response) {
                if(productDetailsWA!=null && productDetailsWA.getData().size()>0){
                    for(ProductModel product : productDetailsWA.getData()){
                        mAdapter.putToProductMap(product.getOrderId(), product);
                    }
                }
                mAdapter.addAll(data);
                if(ordersModelWebAction.getExtra().getTotalCount()
                        <= ordersModelWebAction.getExtra().getCurrentIndex() +
                        ordersModelWebAction.getExtra().getPageSize()){
                    isLastPage = true;
                }else {
                    mAdapter.addLoadingFooter();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                mAdapter.addAll(ordersModelWebAction.getData());
                if(ordersModelWebAction.getExtra().getTotalCount()
                        <= ordersModelWebAction.getExtra().getCurrentIndex() +
                        ordersModelWebAction.getExtra().getPageSize()){
                    isLastPage = true;
                }else {
                    mAdapter.addLoadingFooter();
                }
            }
        });

    }

    private void loadNextPage(){
        WebActionCallInterface callInterface = Constants.webActionAdapter.create(WebActionCallInterface.class);
        mSkip+=LIMIT;
        callInterface.getOrders(mQuery, mSkip, LIMIT, "{CreatedOn:-1}", new Callback<WebActionModel<OrderModel>>() {
            @Override
            public void success(WebActionModel<OrderModel> orderModelWebActionModel, Response response) {
                mAdapter.removeLoadingFooter();
                isLoading = false;
                getProductsForOrders(orderModelWebActionModel);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(OrderListActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }


}

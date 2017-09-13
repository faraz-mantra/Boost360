package com.nowfloats.manageinventory;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nowfloats.manageinventory.adapters.OrderDetailsRvAdapter;
import com.nowfloats.manageinventory.interfaces.WebActionCallInterface;
import com.nowfloats.manageinventory.models.OrderModel;
import com.nowfloats.manageinventory.models.ProductModel;
import com.nowfloats.manageinventory.models.UserModel;
import com.nowfloats.manageinventory.models.WebActionModel;
import com.nowfloats.util.Constants;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class OrderDetailsActivity extends AppCompatActivity {

    private OrderModel mOrder;
    private ArrayList<ProductModel> mProducts;
    private String mTag;


    RecyclerView rvOrderDetails;
    ProgressBar pbOrderDetails;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            if(bundle.containsKey("order")){
                mOrder = bundle.getParcelable("order");
            }
            if(bundle.containsKey("products")){
                mProducts = bundle.getParcelableArrayList("products");
            }

            if(bundle.containsKey("tag")){
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

        if(mOrder!=null) {
            setTitle("ORDER ID: " + mOrder.getOrderId());
            getUserDetails();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);

    }

    private void getUserDetails() {
        String query = String.format("{_id:'%s'}", mOrder.getOrderUserId());
        Constants.webActionAdapter.create(WebActionCallInterface.class).getUser(query, 1, new Callback<WebActionModel<UserModel>>() {
            @Override
            public void success(WebActionModel<UserModel> userModelWebActionModel, Response response) {
                if(userModelWebActionModel!=null && userModelWebActionModel.getData().size()>0) {
                    showOrderDetails(userModelWebActionModel.getData().get(0));
                }else {
                    Toast.makeText(OrderDetailsActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(OrderDetailsActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void showOrderDetails(UserModel userModel) {
        pbOrderDetails.setVisibility(View.GONE);
        rvOrderDetails.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvOrderDetails.setAdapter(new OrderDetailsRvAdapter(this, mTag, mProducts, mOrder, userModel));
    }
}

package com.nowfloats.Product_Gallery;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.Product_Gallery.Adapter.ProductCatagoryRecyclerAdapter;
import com.nowfloats.Product_Gallery.Model.ProductListModel;
import com.nowfloats.Product_Gallery.Service.ProductAPIService;
import com.nowfloats.Product_Gallery.Service.ProductGalleryInterface;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.thinksity.R;
import com.thinksity.databinding.ActivityProductCatalogBinding;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ProductCatalogActivity extends AppCompatActivity {

    //private Toolbar toolbar;
    //private TextView headerText;
    //private ImageView ivDelete;

    private ActivityProductCatalogBinding binding;
    private ProductCatagoryRecyclerAdapter adapter;
    private LinearLayoutManager layoutManager;

    public static Bus bus;

    private UserSessionManager session;
    private ProductAPIService apiService;

    private boolean stop = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_catalog);

        //toolbar = findViewById(R.id.app_bar_site_appearance);
        //ivDelete = findViewById(R.id.ivDelete);
        //setSupportActionBar(toolbar);
        //headerText = toolbar.findViewById(R.id.titleTextView);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            getSupportActionBar().setTitle("Product Catalog");
        }

        this.initProductRecyclerView(binding.productList);

        bus = BusProvider.getInstance().getBus();
        session = new UserSessionManager(getApplicationContext(), this);
        apiService = new ProductAPIService();

        getProducts();
    }

    private void getProducts()
    {
        final String skip = String.valueOf(adapter.getItemCount());

        HashMap<String, String> values = new HashMap<>();
        values.put("clientId", Constants.clientId);
        values.put("skipBy", skip);
        values.put("fpTag", session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG));

        ProductGalleryInterface galleryInterface = Constants.restAdapter.create(ProductGalleryInterface.class);

        galleryInterface.getProducts(values, new Callback<ArrayList<ProductListModel>>() {

            @Override
            public void success(ArrayList<ProductListModel> data, Response response) {

                if(data != null && response.getStatus() == 200)
                {
                    if(data.size() > 0)
                    {
                        adapter.setData(data);
                        return;
                    }
                }

                stop = true;

                /*try {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (Product_Gallery_Fragment.progressLayout != null)
                                Product_Gallery_Fragment.progressLayout.setVisibility(View.GONE);
                        }
                    });
                    if (map.get("skipBy").equals("0") && Product_Detail_Activity_V45.replaceImage) {
                        Product_Gallery_Fragment.productItemModelList = data;
                    }
                    if (bus != null) {
                        if (map.get("skipBy").equals("0")) {
                            bus.post(data);
                        } else {
                            bus.post(new LoadMoreProductEvent(data));
                        }
                    }
                }catch(Exception e){e.printStackTrace();}*/
            }

            @Override
            public void failure(RetrofitError error) {

                Log.d("PRODUCT_SIZE", "FAIL");
                /*activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (Product_Gallery_Fragment.empty_layout!=null)
                            Product_Gallery_Fragment.empty_layout.setVisibility(View.VISIBLE);
                        if (Product_Gallery_Fragment.progressLayout!=null)
                            Product_Gallery_Fragment.progressLayout.setVisibility(View.GONE);
                        Methods.showSnackBarNegative(activity, activity.getString(R.string.something_went_wrong_try_again));
                    }
                });*/
            }
        });
    }

    /**
     * Initialize service adapter
     * @param recyclerView
     */
    private void initProductRecyclerView(RecyclerView recyclerView)
    {
        layoutManager = new LinearLayoutManager(this);

        adapter = new ProductCatagoryRecyclerAdapter(this);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.SetOnItemClickListener(productListModel -> {

                }
        );

        addScrollListener(recyclerView);
    }


    private void addScrollListener(RecyclerView recyclerView)
    {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                if(lastVisibleItem>=totalItemCount-1 && !stop)
                {
                    getProducts();
                }
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:

                finish();
                break;

            case R.id.menu_add:

                startActivity(new Intent(ProductCatalogActivity.this, ManageProductActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    /*@Subscribe
    public void getProductList(ArrayList<ProductListModel> data) {

        Log.d("PRODUCT_SIZE", "null");

        if (data != null)
        {
            Log.d("PRODUCT_SIZE", "" + data.size());*/

            //Log.i("","PRoduct List Size--"+data.size());
            //Log.d("Product Id", data.get(0)._id);
            //checkIfAPEnabled();

            //productItemModelList = data;
            //productGalleryAdapter = new ProductGalleryAdapter(activity, currencyValue, from);
            //gridView.setAdapter(productGalleryAdapter);
            //gridView.invalidateViews();
            //productGalleryAdapter.refreshDetails(productItemModelList);

            /*if (productItemModelList.size() == 0)
            {
                empty_layout.setVisibility(View.VISIBLE);
                session.setBubbleShareProducts(false);

                if (from == FROM.BUBBLE)
                {
                    getActivity().finish();
                    startActivity(new Intent(getActivity(), BubbleInAppDialog.class));
                }
            }

            else
            {
                session.setBubbleShareProducts(true);
                empty_layout.setVisibility(View.GONE);
            }*/
        //}

        /*else
        {
            progressLayout.setVisibility(View.GONE);

            if (productItemModelList == null || productItemModelList.size() == 0)
            {
                Product_Gallery_Fragment.empty_layout.setVisibility(View.VISIBLE);
            }

            else
            {
                Product_Gallery_Fragment.empty_layout.setVisibility(View.GONE);
            }

            Methods.showSnackBarNegative(activity, getString(R.string.something_went_wrong_try_again));
        }

        if (productItemModelList != null && !session.getOnBoardingStatus() && productItemModelList.size() != session.getProductsCount())
        {
            session.setProductsCount(productItemModelList.size());
            OnBoardingApiCalls.updateData(session.getFpTag(),String.format("add_product:%s",productItemModelList.size()>0?"true":"false"));
        }*/
    //}
}
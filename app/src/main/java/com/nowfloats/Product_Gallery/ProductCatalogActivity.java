package com.nowfloats.Product_Gallery;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.Product_Gallery.Adapter.ProductCatagoryRecyclerAdapter;
import com.nowfloats.Product_Gallery.Model.Product;
import com.nowfloats.Product_Gallery.Model.ProductListModel;
import com.nowfloats.Product_Gallery.Service.ProductAPIService;
import com.nowfloats.Product_Gallery.Service.ProductGalleryInterface;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.squareup.otto.Bus;
import com.thinksity.R;
import com.thinksity.databinding.ActivityProductCatalogBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ProductCatalogActivity extends AppCompatActivity {

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

        setSupportActionBar(binding.layoutToolbar.toolbar);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            getSupportActionBar().setTitle("");

            binding.layoutToolbar.toolbarTitle.setText("Product Catalog");
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

        binding.pbLoading.setVisibility(View.VISIBLE);

        galleryInterface.getAllProducts(values, new Callback<List<Product>>() {

            @Override
            public void success(List<Product> data, Response response) {

                binding.pbLoading.setVisibility(View.GONE);

                if(data != null && response.getStatus() == 200)
                {
                    if(data.size() > 0)
                    {
                        binding.layoutEmpty.layoutEmptyView.setVisibility(View.GONE);
                        adapter.setData(data);
                        return;
                    }

                    if(adapter.getItemCount() == 0)
                    {
                        binding.layoutEmpty.layoutEmptyView.setVisibility(View.VISIBLE);
                    }
                }

                stop = true;
            }

            @Override
            public void failure(RetrofitError error) {

                binding.pbLoading.setVisibility(View.GONE);
                Methods.showSnackBarNegative(ProductCatalogActivity.this, getString(R.string.something_went_wrong_try_again));
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
        adapter.SetOnItemClickListener(product -> {

                    Intent intent = new Intent(ProductCatalogActivity.this, ManageProductActivity.class);
                    intent.putExtra("PRODUCT", product);
                    startActivity(intent);
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
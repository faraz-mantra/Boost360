package com.nowfloats.ProductGallery;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.ProductGallery.Adapter.ProductCategoryRecyclerAdapter;
import com.nowfloats.ProductGallery.Model.Product;
import com.nowfloats.ProductGallery.Service.ProductGalleryInterface;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.widget.WidgetKey;
import com.thinksity.R;
import com.thinksity.databinding.ActivityProductCatalogBinding;

import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ProductCatalogActivity extends AppCompatActivity implements WidgetKey.OnWidgetListener{

    private ActivityProductCatalogBinding binding;
    private ProductCategoryRecyclerAdapter adapter;

    private UserSessionManager session;

    private boolean stop = false;
    private boolean isLoading = false;
    private int limit = WidgetKey.WidgetLimit.FEATURE_NOT_AVAILABLE.getValue();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_catalog);

        setSupportActionBar(binding.layoutToolbar.toolbar);

        session = new UserSessionManager(getApplicationContext(), this);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            getSupportActionBar().setTitle("");

            String category = session.getFPDetails(Key_Preferences.PRODUCT_CATEGORY);
            binding.layoutToolbar.toolbarTitle.setText("Catalogue");
            binding.tvMessage.setText(String.format(getString(R.string.product_empty_view_message), category.toLowerCase()));
        }

        this.initProductRecyclerView();
        getProducts(false);
        getWidgetLimit();
    }

    private void getWidgetLimit()
    {
        WidgetKey widget = new WidgetKey();
        widget.setWidgetListener(this);
        widget.getWidgetLimit(session, WidgetKey.WIDGET_PRODUCT_CATALOG);
    }

    /**
     * API call to get the list of products
     * @param flag
     */
    private void getProducts(boolean flag)
    {
        if(!Methods.isOnline(ProductCatalogActivity.this))
        {
            return;
        }

        isLoading = true;

        final String skip = flag ? "0" : String.valueOf(adapter.getItemCount());

        HashMap<String, String> values = new HashMap<>();
        values.put("clientId", Constants.clientId);
        values.put("skipBy", skip);
        values.put("fpTag", session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG));

        ProductGalleryInterface galleryInterface = Constants.restAdapterDev.create(ProductGalleryInterface.class);

        binding.pbLoading.setVisibility(View.VISIBLE);

        galleryInterface.getAllProducts(values, new Callback<List<Product>>() {

            @Override
            public void success(List<Product> data, Response response) {

                isLoading = false;

                binding.pbLoading.setVisibility(View.GONE);

                if(data != null && response.getStatus() == 200)
                {
                    if(data.size() > 0)
                    {
                        binding.layoutEmptyView.setVisibility(View.GONE);
                        adapter.setData(data, flag);
                        return;
                    }

                    if(adapter.getItemCount() == 0)
                    {
                        binding.layoutEmptyView.setVisibility(View.VISIBLE);
                    }
                }

                stop = true;
            }

            @Override
            public void failure(RetrofitError error) {

                isLoading = false;

                binding.pbLoading.setVisibility(View.GONE);
                Methods.showSnackBarNegative(ProductCatalogActivity.this, getString(R.string.something_went_wrong_try_again));
            }
        });
    }

    /**
     * Initialize service/product adapter
     */
    private void initProductRecyclerView()
    {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        adapter = new ProductCategoryRecyclerAdapter(this);
        binding.productList.setLayoutManager(layoutManager);
        binding.productList.setAdapter(adapter);

        adapter.SetOnItemClickListener(product -> {

                    Intent intent = new Intent(ProductCatalogActivity.this, ManageProductActivity.class);
                    intent.putExtra("PRODUCT", product);
                    startActivityForResult(intent, 300);
                }
        );

        binding.productList.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                if(lastVisibleItem>=totalItemCount-1 && !stop && !isLoading)
                {
                    getProducts(false);
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

                addProduct();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 300 && resultCode == RESULT_OK)
        {
            boolean flag = data.getBooleanExtra("LOAD", true);
            getProducts(flag);
        }
    }

    private void openAddProductActivity()
    {
        Intent intent = new Intent(ProductCatalogActivity.this, ManageProductActivity.class);
        intent.putExtra("PRODUCT", new Product());
        startActivityForResult(intent, 300);
    }

    private void addProduct()
    {
        /**
         * If not new pricing plan
         */
        if(!WidgetKey.isNewPricingPlan)
        {
            if(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE).equals("-1"))
            {
                Methods.showFeatureNotAvailDialog(this);
            }

            else
            {
                openAddProductActivity();
            }

            Log.d("WIDGET_LIMIT_RESPONSE", "EXISTING PRICING PLAN");
        }

        else
        {
            Log.d("WIDGET_LIMIT_RESPONSE", "NEW PRICING PLAN");

            String value = WidgetKey.getPropertyValue(WidgetKey.WIDGET_PRODUCT_CATALOG, WidgetKey.WIDGET_PROPERTY_MAX);

            if(value.equals(WidgetKey.WidgetValue.FEATURE_NOT_AVAILABLE.getValue()))
            {
                Toast.makeText(getApplicationContext(), String.valueOf(getString(R.string.message_feature_not_available)), Toast.LENGTH_LONG).show();
            }

            else if(!value.equals(WidgetKey.WidgetValue.UNLIMITED.getValue()) && adapter.getItemCount() >= Integer.parseInt(value))
            {
                Toast.makeText(getApplicationContext(), String.valueOf(getString(R.string.message_add_product_limit)), Toast.LENGTH_LONG).show();
            }

            else
            {
               openAddProductActivity();
            }

            /*if(limit == WidgetKey.WidgetLimit.FEATURE_NOT_AVAILABLE.getValue())
            {
                Toast.makeText(getApplicationContext(), String.valueOf(getString(R.string.message_feature_not_available)), Toast.LENGTH_LONG).show();
            }

            else if(limit == WidgetKey.WidgetLimit.LIMIT_EXCEEDED.getValue())
            {
                Toast.makeText(getApplicationContext(), String.valueOf(getString(R.string.message_add_product_limit)), Toast.LENGTH_LONG).show();
            }

            else
            {
                openAddProductActivity();
            }*/
        }
    }

    @Override
    public void onWidgetLimit(int limit) {

        this.limit = limit;
        Log.d("WIDGET_LIMIT_RESPONSE", "SUCCESS " + limit);
    }

    public void onAddNewProduct(View view)
    {
        addProduct();
    }
}
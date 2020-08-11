package com.nowfloats.ProductGallery;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appservice.constant.FragmentType;
import com.appservice.constant.IntentConstant;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.ProductGallery.Adapter.ProductCategoryRecyclerAdapter;
import com.nowfloats.ProductGallery.Model.ImageListModel;
import com.nowfloats.ProductGallery.Model.Product;
import com.nowfloats.ProductGallery.Service.ProductGalleryInterface;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.Utils;
import com.nowfloats.widget.WidgetKey;
import com.thinksity.R;
import com.thinksity.databinding.ActivityProductCatalogBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.appservice.ui.catlogService.CatlogServiceContainerActivityKt.startFragmentActivityNew;

public class ProductCatalogActivity extends AppCompatActivity implements WidgetKey.OnWidgetListener {

    private ActivityProductCatalogBinding binding;
    private ProductCategoryRecyclerAdapter adapter;

    private UserSessionManager session;

    private boolean stop = false;
    private boolean isLoading = false;
    private int limit = WidgetKey.WidgetLimit.FEATURE_NOT_AVAILABLE.getValue();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_catalog);

        setSupportActionBar(binding.layoutToolbar.toolbar);

        session = new UserSessionManager(getApplicationContext(), this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            getSupportActionBar().setTitle("");

            binding.layoutToolbar.toolbarTitle.setText(Utils.getProductCatalogTaxonomyFromServiceCode(session.getFP_AppExperienceCode()));
            binding.tvMessage.setText(String.format(getString(R.string.product_empty_view_message),
                    Utils.getSingleProductTaxonomyFromServiceCode(session.getFP_AppExperienceCode()).toLowerCase()));
        }

        this.initProductRecyclerView();
        getProducts(false);
        getWidgetLimit();
    }

    private void getWidgetLimit() {
        WidgetKey widget = new WidgetKey();
        widget.setWidgetListener(this);
        widget.getWidgetLimit(session, WidgetKey.WIDGET_PRODUCT_CATALOG);
    }

    /**
     * API call to get the list of products
     *
     * @param flag
     */
    private void getProducts(boolean flag) {
        if (!Methods.isOnline(ProductCatalogActivity.this)) {
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

                if (data != null && response.getStatus() == 200) {
                    if (data.size() > 0) {
                        binding.layoutEmptyView.setVisibility(View.GONE);
                        adapter.setData(data, flag);
                        return;
                    }

                    if (adapter.getItemCount() == 0) {
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
    private void initProductRecyclerView() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        adapter = new ProductCategoryRecyclerAdapter(this);
        binding.productList.setLayoutManager(layoutManager);
        binding.productList.setAdapter(adapter);

        adapter.SetOnItemClickListener(product -> {
            openAddProductActivity(product);
//            Intent intent = new Intent(ProductCatalogActivity.this, ManageProductActivity.class);
//            intent.putExtra("PRODUCT", selected_product);
//            startActivityForResult(intent, 300);
        });
        binding.productList.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                if (lastVisibleItem >= totalItemCount - 1 && !stop && !isLoading) {
                    getProducts(false);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 300 && resultCode == RESULT_OK) {
            boolean flag = data.getBooleanExtra("LOAD", true);
            getProducts(flag);
        }
    }

    private void openAddProductActivity(Product p) {
        String type = Utils.getProductType(session.getFP_AppExperienceCode());
        switch (type) {
            case "SERVICES":
                p.setProductType(type);
                Bundle bundle = getBundleData(p);
                startFragmentActivityNew(this, FragmentType.SERVICE_DETAIL_VIEW, bundle, false, true);
                break;
            default:
                if (p.productType == null) p.setProductType(type);
                Intent intent = new Intent(ProductCatalogActivity.this, ManageProductActivity.class);
                intent.putExtra("PRODUCT", p);
                startActivityForResult(intent, 300);
                break;
        }
    }

    private Bundle getBundleData(Product p) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(IntentConstant.PRODUCT_DATA.name(), getProductData(p));
        bundle.putBoolean(IntentConstant.NON_PHYSICAL_EXP_CODE.name(), session.isNonPhysicalProductExperienceCode());
        String currencyType = "";
        if (TextUtils.isEmpty(p.CurrencyCode)) {
            try {
                currencyType = Constants.Currency_Country_Map.get(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY).toLowerCase());
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else currencyType = p.CurrencyCode;
        if (TextUtils.isEmpty(currencyType)) currencyType = getString(R.string.currency_text);
        bundle.putString(IntentConstant.CURRENCY_TYPE.name(), currencyType);
        bundle.putString(IntentConstant.FP_ID.name(), session.getFPID());
        bundle.putString(IntentConstant.FP_TAG.name(), session.getFpTag());
        bundle.putString(IntentConstant.CLIENT_ID.name(), Constants.clientId);
        bundle.putString(IntentConstant.EXTERNAL_SOURCE_ID.name(), session.getFPDetails(Key_Preferences.EXTERNAL_SOURCE_ID));
        bundle.putString(IntentConstant.APPLICATION_ID.name(), session.getFPDetails(Key_Preferences.GET_FP_DETAILS_APPLICATION_ID));
        return bundle;
    }

    private void addProduct() {
        /**
         * If not new pricing plan
         */
        if (!WidgetKey.isNewPricingPlan) {
            if (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE).equals("-1")) {
                Methods.showFeatureNotAvailDialog(this);
            } else {
                openAddProductActivity(new Product());
            }

            Log.d("WIDGET_LIMIT_RESPONSE", "EXISTING PRICING PLAN");
        } else {
            Log.d("WIDGET_LIMIT_RESPONSE", "NEW PRICING PLAN");

            String value = WidgetKey.getPropertyValue(WidgetKey.WIDGET_PRODUCT_CATALOG, WidgetKey.WIDGET_PROPERTY_MAX);

            if (value.equals(WidgetKey.WidgetValue.FEATURE_NOT_AVAILABLE.getValue())) {
                Toast.makeText(getApplicationContext(), getString(R.string.message_feature_not_available), Toast.LENGTH_LONG).show();
            } else if (!value.equals(WidgetKey.WidgetValue.UNLIMITED.getValue()) && adapter.getItemCount() >= Integer.parseInt(value)) {
                Toast.makeText(getApplicationContext(), getString(R.string.message_add_product_limit), Toast.LENGTH_LONG).show();
            } else {
                openAddProductActivity(new Product());
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

    public void onAddNewProduct(View view) {
        addProduct();
    }


    private com.appservice.model.serviceProduct.Product getProductData(Product p) {
        ArrayList<com.appservice.model.serviceProduct.ImageListModel> listImages = new ArrayList<>();
        if (p.Images != null) {
            for (ImageListModel data : p.Images) {
                listImages.add(new com.appservice.model.serviceProduct.ImageListModel(data.ImageUri, data.TileImageUri));
            }
        }
        ArrayList<com.appservice.model.KeySpecification> otherSpec = new ArrayList<>();
        if (p.otherSpecification != null) {
            for (Product.Specification spec : p.otherSpecification) {
                otherSpec.add(new com.appservice.model.KeySpecification(spec.key, spec.value));
            }
        }
        com.appservice.model.serviceProduct.Product newProduct = new com.appservice.model.serviceProduct.Product();
        newProduct.setCurrencyCode(p.CurrencyCode);
        newProduct.setDescription(p.Description);
        newProduct.setDiscountAmount(p.DiscountAmount);
        newProduct.setExternalSourceId(p.ExternalSourceId);
        newProduct.setIsArchived(p.IsArchived);
        newProduct.setIsAvailable(p.IsAvailable);
        newProduct.setIsFreeShipmentAvailable(p.IsFreeShipmentAvailable);
        newProduct.setName(p.Name);
        newProduct.setPrice(p.Price);
        newProduct.setPriority(p.Priority);
        newProduct.setShipmentDuration(p.ShipmentDuration);
        newProduct.setAvailableUnits(p.availableUnits);
        newProduct.set_keywords(p._keywords);
        newProduct.setTags((ArrayList<String>) p.tags);
        newProduct.setApplicationId(p.ApplicationId);
        newProduct.setFPTag(p.FPTag);
        newProduct.setImageUri(p.ImageUri);
        newProduct.setProductUrl(p.ProductUrl);
        newProduct.setImages(listImages);
        newProduct.setMerchantName(p.MerchantName);
        newProduct.setTileImageUri(p.TileImageUri);
        newProduct.setProductId(p.productId);
        newProduct.setGPId(p.GPId);
        newProduct.setTotalQueries(p.TotalQueries);
        newProduct.setCreatedOn(p.CreatedOn);
        newProduct.setProductIndex(p.ProductIndex);
        newProduct.setPicimageURI(p.picimageURI);
        newProduct.setUpdatedOn(p.UpdatedOn);
        newProduct.setProductSelected(p.isProductSelected);
        newProduct.setProductType(p.productType);
        newProduct.setPaymentType(p.paymentType);
        newProduct.setVariants(p.variants);
        newProduct.setBrandName(p.brandName);
        newProduct.setCategory(p.category);
        newProduct.setCodAvailable(p.codAvailable);
        newProduct.setMaxCodOrders(p.maxCodOrders);
        newProduct.setPrepaidOnlineAvailable(p.prepaidOnlineAvailable);
        newProduct.setMaxPrepaidOnlineAvailable(p.maxPrepaidOnlineAvailable);
        if (p.BuyOnlineLink != null) {
            newProduct.setBuyOnlineLink(new com.appservice.model.serviceProduct.BuyOnlineLink(p.BuyOnlineLink.url, p.BuyOnlineLink.description));
        }
        if (p.keySpecification != null) {
            newProduct.setKeySpecification(new com.appservice.model.KeySpecification(p.keySpecification.key, p.keySpecification.value));
        }
        newProduct.setOtherSpecification(otherSpec);
        newProduct.setPickupAddressReferenceId(p.pickupAddressReferenceId);
        return newProduct;
    }
}
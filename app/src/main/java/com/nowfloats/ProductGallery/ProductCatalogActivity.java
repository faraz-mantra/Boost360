package com.nowfloats.ProductGallery;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appservice.constant.FragmentType;
import com.appservice.constant.IntentConstant;
import com.framework.models.firestore.FirestoreManager;
import com.framework.utils.ContentSharing;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.ProductGallery.Adapter.ProductCategoryRecyclerAdapter;
import com.nowfloats.ProductGallery.Model.ImageListModel;
import com.nowfloats.ProductGallery.Model.Product;
import com.nowfloats.ProductGallery.Service.ProductGalleryInterface;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.Utils;
import com.nowfloats.util.WebEngageController;
import com.nowfloats.widget.WidgetKey;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.thinksity.R;
import com.thinksity.databinding.ActivityProductCatalogBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.appservice.ui.catalog.CatalogServiceContainerActivityKt.startFragmentActivityNew;
import static com.framework.webengageconstant.EventLabelKt.CLICK;
import static com.framework.webengageconstant.EventLabelKt.PAGE_VIEW;
import static com.framework.webengageconstant.EventNameKt.CLICKED_ON_PRODUCTS_CATALOGUE_ADD_NEW;
import static com.framework.webengageconstant.EventNameKt.CLICKED_ON_PRODUCTS_CATALOGUE_ITEM;
import static com.framework.webengageconstant.EventNameKt.EVENT_NAME_MANAGE_CONTENT;
import static com.framework.webengageconstant.EventNameKt.PRODUCT_CATALOGUE_LIST;
import static com.framework.webengageconstant.EventValueKt.EVENT_VALUE_MANAGE_CONTENT;
import static com.framework.webengageconstant.EventValueKt.NO_EVENT_VALUE;

public class ProductCatalogActivity extends AppCompatActivity implements WidgetKey.OnWidgetListener {

    // For sharing
    private static final int STORAGE_CODE = 120;
    static ProgressDialog pd;
    static boolean defaultShareGlobal = true;
    static int shareType = 2;
    static Product shareProduct;
    Target targetMap = null;
    private ActivityProductCatalogBinding binding;
    private ProductCategoryRecyclerAdapter adapter;
    private UserSessionManager session;
    private MenuItem itemToAdd;
    private boolean stop = false;
    private boolean isLoading = false;
    private int limit = WidgetKey.WidgetLimit.FEATURE_NOT_AVAILABLE.getValue();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_catalog);
        WebEngageController.trackEvent(PRODUCT_CATALOGUE_LIST, PAGE_VIEW, EVENT_VALUE_MANAGE_CONTENT);
        setSupportActionBar(binding.layoutToolbar.toolbar);

        session = new UserSessionManager(getApplicationContext(), this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            getSupportActionBar().setTitle("");
            binding.layoutToolbar.toolbarTitle.setText(Utils.getProductCatalogTaxonomyFromServiceCode(session.getFP_AppExperienceCode()));
            binding.layoutToolbar.toolbar.setNavigationIcon(R.drawable.ic_back_arrow_white);
            binding.tvMessage.setText(String.format(getString(R.string.product_empty_view_message),
                    Utils.getSingleProductTaxonomyFromServiceCode(session.getFP_AppExperienceCode()).toLowerCase()));
        }

        this.initProductRecyclerView();
        getProducts(false);
        getWidgetLimit();
        checkIsAdd();
        binding.btnAddCatalogue.setOnClickListener(view -> addProduct());
    }

    private void checkIsAdd() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            boolean isAdd = bundle.getBoolean("IS_ADD");
            if (isAdd) openAddProductActivity(new Product());
        }
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
                        if (itemToAdd != null) itemToAdd.setVisible(true);
                        binding.layoutEmptyView.setVisibility(View.GONE);
                        adapter.setData(data, flag);
                        onProductServiceAddedOrUpdated(data.size());
                        return;
                    }

                    if (adapter.getItemCount() == 0) {
                        if (itemToAdd != null) itemToAdd.setVisible(false);
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

    private void onProductServiceAddedOrUpdated(int count) {
        FirestoreManager instance = FirestoreManager.INSTANCE;
        String type = Utils.getProductType(session.getFP_AppExperienceCode());
        if (instance.getDrScoreData()==null || instance.getDrScoreData().getMetricdetail() == null) return;
        if (type.toUpperCase().equals("SERVICES")) {
            instance.getDrScoreData().getMetricdetail().setNumber_services_added(count);
        } else instance.getDrScoreData().getMetricdetail().setNumber_products_added(count);
        instance.updateDocument();
    }

    /**
     * Initialize service/product adapter
     */
    private void initProductRecyclerView() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        adapter = new ProductCategoryRecyclerAdapter(this);
        binding.productList.setLayoutManager(layoutManager);
        binding.productList.setAdapter(adapter);

        adapter.onShareClickListener((defaultShare, type, product) -> {
            defaultShareGlobal = defaultShare;
            shareType = type;
            shareProduct = product;
            if (checkStoragePermission()) {
                share(defaultShare, type, product);
            }
        });
        adapter.SetOnItemClickListener(product -> {
            WebEngageController.trackEvent(CLICKED_ON_PRODUCTS_CATALOGUE_ITEM, CLICK, NO_EVENT_VALUE);
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
        itemToAdd = menu.findItem(R.id.menu_add);
        itemToAdd.setVisible(true);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (!defaultShareGlobal && shareType != 2 && shareProduct != null) {
                share(defaultShareGlobal, shareType, shareProduct);
            }
        }
    }

    private void openAddProductActivity(Product p) {
        String type = "";
//        if (p.productType != null && !p.productType.isEmpty()) {
//            type = p.productType;
//        } else
        type = Utils.getProductType(session.getFP_AppExperienceCode());
        if ("SERVICES".equals(type.toUpperCase())) {
            p.setProductType(type);
            Bundle bundle = getBundleData(p);
            startFragmentActivityNew(this, FragmentType.SERVICE_DETAIL_VIEW, bundle, false, true);
        } else {
            p.setProductType(type);
            Bundle bundle1 = getBundleData(p);
            startFragmentActivityNew(this, FragmentType.PRODUCT_DETAIL_VIEW, bundle1, false, true);
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
        bundle.putString(IntentConstant.USER_PROFILE_ID.name(), session.getUserProfileId());
        bundle.putString(IntentConstant.CLIENT_ID.name(), Constants.clientId);
        bundle.putString(IntentConstant.EXTERNAL_SOURCE_ID.name(), session.getFPDetails(Key_Preferences.EXTERNAL_SOURCE_ID));
        bundle.putString(IntentConstant.APPLICATION_ID.name(), session.getFPDetails(Key_Preferences.GET_FP_DETAILS_APPLICATION_ID));
        return bundle;
    }

    private void addProduct() {
        /**
         * If not new pricing plan
         */
        // for testing:
        openAddProductActivity(new Product());
        if (true) {
            return;
        }
        if (!WidgetKey.isNewPricingPlan) {
            if (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE).equals("-1")) {
                Methods.showFeatureNotAvailDialog(this);
            } else {
                WebEngageController.trackEvent(CLICKED_ON_PRODUCTS_CATALOGUE_ADD_NEW, CLICK, NO_EVENT_VALUE);
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
                WebEngageController.trackEvent(CLICKED_ON_PRODUCTS_CATALOGUE_ADD_NEW, CLICK, NO_EVENT_VALUE);
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


    private com.appservice.model.serviceProduct.CatalogProduct getProductData(Product p) {
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
        com.appservice.model.serviceProduct.CatalogProduct newProduct = new com.appservice.model.serviceProduct.CatalogProduct();
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

    public boolean checkStoragePermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            Methods.showDialog(this, getString(R.string.storage_permission), "To share the image we need storage permission.",
                    (dialog, which) -> ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_CODE));
            return false;
        }

        return true;
    }

    public void share(boolean defaultShare, int type, Product product) {

        if (Methods.isOnline(this)) {
            if (!defaultShare) {
                if (type == 0) {
                    //fb
                    ContentSharing.Companion.shareProduct(product.Name, String.valueOf(product.Price), product.ProductUrl, session.getUserPrimaryMobile(), product.ImageUri, false, false, true, this);
                } else if (type == 1) {
                    ContentSharing.Companion.shareProduct(product.Name, String.valueOf(product.Price), product.ProductUrl, session.getUserPrimaryMobile(), product.ImageUri, true, false, false, this);
                }
            } else
                ContentSharing.Companion.shareProduct(product.Name, String.valueOf(product.Price), product.ProductUrl, session.getUserPrimaryMobile(), product.ImageUri, false, false, false, this);


        }
    }


}
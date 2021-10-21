package com.nowfloats.ProductGallery;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;

import androidx.fragment.app.DialogFragment;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.framework.analytics.SentryController;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.model.RiaNodeDataModel;
import com.nowfloats.ProductGallery.Adapter.InventoryListAdapter;
import com.nowfloats.ProductGallery.Model.ProductImageRequestModel;
import com.nowfloats.ProductGallery.Model.ProductImageResponseModel;
import com.nowfloats.ProductGallery.Model.ProductKeywordReqModel;
import com.nowfloats.ProductGallery.Model.ProductKeywordResponseModel;
import com.nowfloats.ProductGallery.Model.ProductListModel;
import com.nowfloats.ProductGallery.Model.Product_Gallery_Update_Model;
import com.nowfloats.ProductGallery.Model.ShippingMetricsModel;
import com.nowfloats.ProductGallery.Model.Tag;
import com.nowfloats.ProductGallery.Model.UpdateValue;
import com.nowfloats.ProductGallery.Service.ProductAPIService;
import com.nowfloats.ProductGallery.Service.ProductDelete;
import com.nowfloats.ProductGallery.Service.ProductGalleryInterface;
import com.nowfloats.ProductGallery.Service.ProductImageUploadV45;
import com.nowfloats.ProductGallery.fragments.ShippingCalculatorFragment;
import com.nowfloats.ProductGallery.widgets.NonScrollListView;
import com.nowfloats.ProductGallery.widgets.TagView;
import com.nowfloats.helper.ui.BaseActivity;
import com.nowfloats.manageinventory.models.WAAddDataModel;
import com.nowfloats.manageinventory.models.WaUpdateDataModel;
import com.nowfloats.manageinventory.models.WebActionModel;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.RiaEventLogger;
import com.nowfloats.util.Utils;
import com.nowfloats.webactions.WebAction;
import com.nowfloats.webactions.WebActionsFilter;
import com.nowfloats.webactions.models.ProductImage;
import com.nowfloats.webactions.models.WebActionError;
import com.nowfloats.webactions.models.WebActionVisibility;
import com.nowfloats.webactions.webactioninterfaces.IFilter;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.thinksity.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by guru on 09-06-2015.
 */
public class Product_Detail_Activity_V45 extends BaseActivity implements ShippingCalculatorFragment.ProductMetricCallBack {

    private static final double NF_ASSURANCE_CHARGE = 9.0;
    public static boolean replaceImage = false;
    private final int gallery_req_id = 6;
    private final int media_req_id = 5;
    public Toolbar toolbar;

    //private MaterialEditText etShipmentDuration, etPriority, etShippingCharge, etTransactionCharge, etAvailableUnits, etNetAmount;
    //TextView tvApEnabledText;
    public ImageView save;
    public ProductListModel product_data;

    //Switch svFreeShipment;
    //Switch switchHidePrice;
    //private Button btnShippingMatrix;
    public UserSessionManager session;
    public String path;
    public ProductAPIService apiService;
    public int retryImage = 0;
    ImageView productImage;
    Switch switchView;
    NonScrollListView lvInventoryData;
    ProductGalleryInterface productInterface;
    Activity activity;
    String tagName = "";
    List<Tag> createKeywordList = new ArrayList<>();
    private MaterialEditText productName, productDesc, productCurrency, productPrice, productDiscount, productLink;
    private LinearLayout layoutProductLink;
    private String currencyType = "";
    private Bitmap CameraBitmap;
    private Uri picUri;
    private MaterialDialog materialProgress;
    private HashMap<String, String> values;
    private String switchValue = "true";
    private boolean mIsFreeShipment = false;
    private boolean isHidePrice;
    private String[] mPriorityList;
    private int mPriorityVal = 1000000;
    private RiaNodeDataModel mRiaNodedata;
    private boolean mIsImagePicking = false, mIsApEnabled = false;
    private String deliveryMethod;
    private ShippingMetricsModel mShippingMetrix;
    private WebAction webAction;
    private InventoryListAdapter mInventoryAdapter;
    private List<com.nowfloats.webactions.models.WebAction> mInventoryWebActionList = new ArrayList<>();
    private int keyWordCount = 0, imageCount = 0;
    private String productCategory;
    private ArrayList<ProductImageResponseModel> lsProductImages;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail_v46);

        toolbar = (Toolbar) findViewById(R.id.tool_bar_product_detail);
        setSupportActionBar(toolbar);

        activity = Product_Detail_Activity_V45.this;
        session = new UserSessionManager(getApplicationContext(), activity);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        apiService = new ProductAPIService();
        save = (ImageView) toolbar.findViewById(R.id.home_view_delete_card);
        TextView title = (TextView) toolbar.findViewById(R.id.titleProduct);
        title.setVisibility(View.VISIBLE);
        title.setText(TextUtils.isEmpty(session.getFPDetails(Key_Preferences.PRODUCT_CATEGORY)) ? getString(R.string.add_product) : "Add " + session.getFPDetails(Key_Preferences.PRODUCT_CATEGORY));
        save.setImageResource(R.drawable.product_tick);
        productInterface = Constants.restAdapter.create(ProductGalleryInterface.class);
        tagName = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG);
        switchView = (Switch) findViewById(R.id.switchView);
        layoutProductLink = findViewById(R.id.layout_product_link);

        //switchHidePrice = findViewById(R.id.switch_hide_price);

        /*switchHidePrice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isHidePrice = isChecked;
                save.setVisibility(View.VISIBLE);
            }
        });*/

        //svFreeShipment = (Switch) findViewById(R.id.sv_free_shipping);
        //btnShippingMatrix = findViewById(R.id.btn_calculate_shipping_charges);

        this.productCategory = session.getFPDetails(Key_Preferences.PRODUCT_CATEGORY);

        switchView.setChecked(true);

        mInventoryAdapter = new InventoryListAdapter(this, R.layout.inventory_list_row_layout, mInventoryWebActionList);
        lvInventoryData = findViewById(R.id.lv_inventory_data);
        lvInventoryData.setAdapter(mInventoryAdapter);

        this.addWebActions();

        mPriorityList = getResources().getStringArray(R.array.priority_list);

        PorterDuffColorFilter color = new PorterDuffColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
        save.setColorFilter(color);

        productImage = (ImageView) findViewById(R.id.product_image);
        productName = (MaterialEditText) findViewById(R.id.product_name);
        productDesc = (MaterialEditText) findViewById(R.id.product_desc);
        productCurrency = (MaterialEditText) findViewById(R.id.product_currency);
        productPrice = (MaterialEditText) findViewById(R.id.product_retail_price);
        productDiscount = (MaterialEditText) findViewById(R.id.product_disc_price);
        productLink = (MaterialEditText) findViewById(R.id.product_link);

        //etShipmentDuration = (MaterialEditText) findViewById(R.id.et_shipping_days);
        //etPriority = (MaterialEditText) findViewById(R.id.product_priority);
        //etShippingCharge = (MaterialEditText) findViewById(R.id.et_shipping_charge);
        //etTransactionCharge = (MaterialEditText) findViewById(R.id.et_transaction_charge);
        //etAvailableUnits = (MaterialEditText) findViewById(R.id.et_available_unit);
        //etNetAmount = (MaterialEditText) findViewById(R.id.et_net_amount);
        //tvApEnabledText = (TextView) findViewById(R.id.tv_is_ap_enabled);

        Button deleteProduct = (Button) findViewById(R.id.delete_product);
        findViewById(R.id.btn_calculate_shipping_charges).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment fragment = new ShippingCalculatorFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("shippingMetric", mShippingMetrix);
                bundle.putString("deliveryMethod", deliveryMethod);
                fragment.setArguments(bundle);
                fragment.show(getSupportFragmentManager(), "ShippingCalculatorFragment");
            }
        });

        deleteProduct.setVisibility(View.GONE);

        //Currency
        final String[] array = Constants.currencyArray.toArray(new String[Constants.currencyArray.size()]);
        Arrays.sort(array);
        productCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCurrencyList(activity, array);
            }
        });

        /*etPriority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPriorityList();
            }
        });*/

        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
            }
        });

        /*if (getIntent() != null && getIntent().hasExtra("isApEnabled"))
        {
            mIsApEnabled = getIntent().getBooleanExtra("isApEnabled", false);
        }*/

        if (getIntent() != null && getIntent().hasExtra("deliveryMethod")) {
            deliveryMethod = getIntent().getStringExtra("deliveryMethod");
        }

        /*if (Constants.PACKAGE_NAME.equals("com.biz2.nowfloats"))
        {
            layoutProductLink.setVisibility(View.GONE);
        }

        else
        {
            layoutProductLink.setVisibility(View.VISIBLE);
        }*/

        /**
         * Check for delivery method
         */
        /*if(deliveryMethod.equalsIgnoreCase(Constants.DeliveryMethod.ASSURED_PURCHASE.getValue())
        || deliveryMethod.equalsIgnoreCase(Constants.DeliveryMethod.SELF.getValue()))
        {
            btnShippingMatrix.setVisibility(View.VISIBLE);
        }

        else
        {
            btnShippingMatrix.setVisibility(View.GONE);
        }*/

        /*if (mIsApEnabled)
        {
            etShipmentDuration.setEnabled(false);
            svFreeShipment.setEnabled(false);
            productLink.setEnabled(false);
            tvApEnabledText.setText("**Assured purchase is currently enabled");
            tvApEnabledText.setTextColor(Color.parseColor("#45b6bc"));
            productCurrency.setText("INR");
            productCurrency.setEnabled(false);
        }

        else
        {
            tvApEnabledText.setText("**Assured purchase is currently disabled");
            tvApEnabledText.setTextColor(Color.RED);
        }*/

        enableRealTimePriceUpdate();

        if (getIntent().hasExtra("product")) {
            final int position = Integer.parseInt(getIntent().getExtras().getString("product"));

            if (Product_Gallery_Fragment.productItemModelList != null && (Product_Gallery_Fragment.productItemModelList.size() - 1) >= position) {

                product_data = Product_Gallery_Fragment.productItemModelList.get(position);

                if (product_data != null) {
                    replaceImage = true;
                    save.setVisibility(View.GONE);
                    title.setText(TextUtils.isEmpty(session.getFPDetails(Key_Preferences.PRODUCT_CATEGORY)) ? getString(R.string.edit_product) : "Edit " + session.getFPDetails(Key_Preferences.PRODUCT_CATEGORY));
                    getShippingMetrix(product_data._id);

                    //load image

                    /*try
                    {
                        //currencyType = Constants.Currency_Country_Map.get(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY).toLowerCase());
                        productCurrency.setText(product_data.CurrencyCode);
                        //final String[] array = Constants.currencyArray.toArray(new String[Constants.currencyArray.size()]);
                        //Arrays.sort(array);
                        productCurrency.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v)
                            {
                                showCurrencyList(activity,array);
                            }
                        });
                    }

                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }*/

                    String image_url = product_data.TileImageUri;
                    if (image_url != null && image_url.length() > 0 && !image_url.equals("null")) {
                        if (!image_url.contains("http")) {
                            image_url = Constants.BASE_IMAGE_URL + product_data.TileImageUri;
                        }
                        Picasso.get().load(image_url).placeholder(R.drawable.default_product_image).into(productImage);
                    }
                    ViewCompat.setTransitionName(productImage, "imageKey");
                    //productName
                    String name = product_data.Name;
                    if (name != null && name.trim().length() > 0 && !name.equals("0"))
                        productName.setText(name);
                    textEditListener(productName);
                    //productDesc
                    String desc = product_data.Description;
                    if (desc != null && desc.trim().length() > 0 && !desc.equals("0"))
                        productDesc.setText(desc);
                    textEditListener(productDesc);
                    //price
                    String price = product_data.Price;
                    if (price != null && price.trim().length() > 0 && !price.equals("0") && !price.equals("0.0"))
                        productPrice.setText(price);
                    textEditListener(productPrice);
                    //discount
                    String dsPrice = product_data.DiscountAmount;
                    if (dsPrice != null && dsPrice.trim().length() > 0 && !dsPrice.equals("0") && !dsPrice.equals("0.0"))
                        productDiscount.setText(dsPrice);
                    textEditListener(productDiscount);
                    //availability
                    String avail = product_data.IsAvailable;
                    if (avail != null && avail.trim().length() > 0 && !avail.equals("0")) {
                        if (avail.equals("true")) {
                            switchView.setChecked(true);
                            switchValue = "true";
                        } else {
                            switchView.setChecked(false);
                            switchValue = "false";
                        }
                    }
                    //freeShipment
                    /*String freeShipment = product_data.IsFreeShipmentAvailable;
                    if (freeShipment != null && freeShipment.trim().length() > 0 && !freeShipment.equals("0")) {
                        if (mIsApEnabled) {
                            svFreeShipment.setChecked(true);
                        } else {
                            if (freeShipment.equals("true")) {
                                svFreeShipment.setChecked(true);
                                mIsFreeShipment = true;
                            } else {
                                svFreeShipment.setChecked(false);
                                mIsFreeShipment = false;
                            }
                        }
                    }*/

                    //link
                    String link = product_data.BuyOnlineLink;
                    if (link != null && link.trim().length() > 0 && !link.equals("0"))
                        productLink.setText(link);
                    textEditListener(productLink);
                    //shipment duration
                    /*String shipmentDuration = product_data.ShipmentDuration;
                    if (shipmentDuration != null && shipmentDuration.trim().length() > 0 && !shipmentDuration.equals("0"))
                        etShipmentDuration.setText(shipmentDuration);
                    textEditListener(etShipmentDuration);*/

                    /*int availableUnit = product_data.availableUnits;
                    if (availableUnit != -1)
                        etAvailableUnits.setText(String.valueOf(availableUnit));

                    textEditListener(etAvailableUnits);*/
                    //Currency Code
                    String currencyCode = product_data.CurrencyCode;
                    if (currencyCode != null && currencyCode.trim().length() > 0 && !currencyCode.equals("0"))
                        productCurrency.setText(currencyCode);
                    //textEditListener(productCurrency);

                    //priority
                    /*String priority = product_data.Priority;
                    mPriorityVal = Integer.parseInt(priority);
                    switch (priority) {
                        case "1":
                            etPriority.setText(mPriorityList[1]);
                            break;
                        case "1000000":
                            etPriority.setText(mPriorityList[0]);
                            break;
                        case "2":
                            etPriority.setText(mPriorityList[2]);
                            break;
                        case "3":
                            etPriority.setText(mPriorityList[3]);
                            break;
                    }*/


                    //update onclick listener
                    save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE).equals("-1")) {
                                Methods.showFeatureNotAvailDialog(Product_Detail_Activity_V45.this);
                            } else {
                                MixPanelController.track(EventKeysWL.PRODUCT_GALLERY_UPDATE, null);
                                try {

                                    /*if (mIsApEnabled && (TextUtils.isEmpty(etNetAmount.getText().toString().trim()) ||
                                            TextUtils.isEmpty(etShippingCharge.getText().toString().trim()) ||
                                            TextUtils.isEmpty(etTransactionCharge.getText().toString().trim()))) {
                                        Methods.showSnackBarNegative(Product_Detail_Activity_V45.this, "Please enter the shipping metrices");
                                        return;
                                    } else if (mIsApEnabled && Double.parseDouble(etNetAmount.getText().toString().trim())
                                            < Double.parseDouble(etShippingCharge.getText().toString().trim())) {
                                        Methods.showSnackBarNegative(Product_Detail_Activity_V45.this, "NetAmount can't be less than Shipping charge");
                                        return;
                                    }*/
                                    materialProgress = new MaterialDialog.Builder(activity)
                                            .widgetColorRes(R.color.accentColor)
                                            .content(getString(R.string.updating))
                                            .progress(true, 0).show();
                                    materialProgress.setCancelable(false);
                                    values = new HashMap<String, String>();
                                    boolean flag = ValidateFields(true);
                                    ArrayList<UpdateValue> updates = new ArrayList<UpdateValue>();
                                    for (Map.Entry<String, String> entry : values.entrySet()) {
                                        //String key = .toUpperCase();
                                        //BoostLog.d(Product_Detail_Activity.class.getName(), key);
                                        updates.add(new UpdateValue(entry.getKey(), entry.getValue()));
                                    }

                                    if (flag) {
                                        BoostLog.d("Product_Detail_Activity", updates.toString());
                                        Product_Gallery_Update_Model model = new Product_Gallery_Update_Model(Constants.clientId, product_data._id, updates);
                                        //BoostLog.d()
                                        productInterface.put_UpdateGalleryUpdate(model, new Callback<ArrayList<String>>() {
                                            @Override
                                            public void success(ArrayList<String> strings, Response response) {
                                                Log.d("UPdate success-Response", "" + response);
                                                Log.d("UPdate success-", "" + strings.size());
                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            Thread.sleep(3000);
                                                        } catch (Exception e) {
                                                            SentryController.INSTANCE.captureException(e);
                                                            e.printStackTrace();
                                                        }
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                materialProgress.dismiss();
                                                                invokeGetProductList(product_data._id);

                                                                //updateShippingMetric();
                                                                Methods.showSnackBarPositive(activity, getString(R.string.product_successfully_updated));
                                                            }
                                                        });
                                                    }
                                                }).start();
                                            }

                                            @Override
                                            public void failure(RetrofitError error) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        materialProgress.dismiss();
                                                        Methods.showSnackBarNegative(activity, getString(R.string.something_went_wrong_try_again));
                                                    }
                                                });
                                            }
                                        });
                                    } else {
                                        materialProgress.dismiss();
                                    }
                                } catch (Exception e) {
                                    SentryController.INSTANCE.captureException(e);
                                    e.printStackTrace();
                                    materialProgress.dismiss();
                                    Methods.showSnackBarNegative(activity, getString(R.string.something_went_wrong_try_again));
                                }
                            }
                        }
                    });


                    deleteProduct.setVisibility(View.VISIBLE);
                    deleteProduct.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                MixPanelController.track(EventKeysWL.PRODUCT_GALLERY_DELETE, null);
                                new MaterialDialog.Builder(activity)
                                        .title(getString(R.string.are_you_sure_want_to_delete))
                                        .positiveText(getString(R.string.delete_))
                                        .positiveColorRes(R.color.primaryColor)
                                        .negativeText(getString(R.string.cancel))
                                        .negativeColorRes(R.color.light_gray)
                                        .callback(new MaterialDialog.ButtonCallback() {
                                            @Override
                                            public void onPositive(MaterialDialog dialog) {
                                                super.onPositive(dialog);
                                                try {
                                                    JSONObject jsonObject = new JSONObject();
                                                    jsonObject.put("clientId", Constants.clientId);
                                                    jsonObject.put("productId", product_data._id);
                                                    String url = Constants.NOW_FLOATS_API_URL + "/Product/v1/Delete";
                                                    new ProductDelete(url, jsonObject.toString(), Product_Detail_Activity_V45.this, position).execute();
                                                } catch (JSONException e) {
                                                    SentryController.INSTANCE.captureException(e);
                                                    e.printStackTrace();
                                                }
                                                dialog.dismiss();
                                            }

                                            @Override
                                            public void onNegative(MaterialDialog dialog) {
                                                super.onNegative(dialog);
                                                dialog.dismiss();
                                            }
                                        }).show();

                            } catch (Exception e) {
                                SentryController.INSTANCE.captureException(e);
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        } else if (getIntent().hasExtra("new")) {
            try {
                mRiaNodedata = getIntent().getParcelableExtra(Constants.RIA_NODE_DATA);
                /*findViewById(R.id.productLayout).postDelayed(
                        new Runnable() {
                            public void run() {
                                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                inputMethodManager.toggleSoftInputFromWindow(productName.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                                productName.requestFocus();
                            }
                        }, 500);*/
            } catch (Exception e) {
                SentryController.INSTANCE.captureException(e);
                e.printStackTrace();
            }
            replaceImage = false;
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    /*if (mIsApEnabled && (TextUtils.isEmpty(etNetAmount.getText().toString().trim()) ||
                            TextUtils.isEmpty(etShippingCharge.getText().toString().trim()) ||
                            TextUtils.isEmpty(etTransactionCharge.getText().toString().trim()))) {
                        Methods.showSnackBarNegative(Product_Detail_Activity_V45.this, "Please enter the shipping metrices");
                        return;
                    } else if (mIsApEnabled && Double.parseDouble(etNetAmount.getText().toString().trim())
                            < Double.parseDouble(etShippingCharge.getText().toString().trim())
                            ) {
                        Methods.showSnackBarNegative(Product_Detail_Activity_V45.this, "NetAmount can't be less than Shipping charge");
                        return;
                    }*/
                    materialProgress = new MaterialDialog.Builder(activity)
                            .widgetColorRes(R.color.accentColor)
                            .content(getString(R.string.loading))
                            .progress(true, 0).show();
                    materialProgress.setCancelable(false);
                    try {
                        values = new HashMap<String, String>();
                        values.put("clientId", Constants.clientId);
                        values.put("fpTag", session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG).toUpperCase());

                        boolean flag = ValidateFields(false);

                        if (flag && (path == null || path.trim().length() == 0)) {
                            flag = false;
                            Methods.showSnackBarNegative(activity, String.format(String.valueOf(getString(R.string.upload_product_image)), productCategory.toLowerCase()));
                        }
                        if (flag) {
                            productInterface.addProduct(values, new Callback<String>() {
                                @Override
                                public void success(final String productId, Response response) {
                                    if (mRiaNodedata != null) {
                                        RiaEventLogger.getInstance().logPostEvent(session.getFpTag(),
                                                mRiaNodedata.getNodeId(), mRiaNodedata.getButtonId(),
                                                mRiaNodedata.getButtonLabel(),
                                                RiaEventLogger.EventStatus.COMPLETED.getValue());
                                        mRiaNodedata = null;
                                    }
                                    Log.i("PRODUCT ID__", "" + productId);

                                    /**
                                     * Add shipping matrix
                                     */
                                    onProductMetricCalculated(mShippingMetrix, ShippingCalculatorFragment.ShippingAddOrUpdate.ADD);
                                    mShippingMetrix.setProductId(productId);
                                    addShippingMetric(mShippingMetrix, true);


                                    if (createKeywordList != null && createKeywordList.size() > 0) {
                                        keyWordCount = 0;
                                        for (Tag tag : createKeywordList) {
                                            final ProductKeywordReqModel reqData = new ProductKeywordReqModel();
                                            reqData._pid = productId;
                                            reqData.keyword = tag.text;
                                            webAction.insert(session.getFpTag(), reqData, new WebAction.WebActionCallback<String>() {
                                                @Override
                                                public void onSuccess(String result) {
                                                    keyWordCount++;
                                                    if (keyWordCount == createKeywordList.size()) {
                                                        afterExecutingKeywords(productId);
                                                    }
                                                }

                                                @Override
                                                public void onFailure(WebActionError error) {
                                                    keyWordCount++;
                                                    if (keyWordCount == createKeywordList.size()) {
                                                        afterExecutingKeywords(productId);
                                                    }
                                                }
                                            });
                                        }
                                    } else {
                                        afterExecutingKeywords(productId);
                                    }

                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            materialProgress.dismiss();
                                            Methods.showSnackBarNegative(activity, getString(R.string.something_went_wrong_try_again));
                                        }
                                    });

                                }
                            });
                        } else {
                            materialProgress.dismiss();
                        }
                    } catch (Exception e) {
                        SentryController.INSTANCE.captureException(e);
                        e.printStackTrace();
                        materialProgress.dismiss();
                        Methods.showSnackBarNegative(activity, getString(R.string.something_went_wrong_try_again));
                    }
                }
            });
        }
        switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                save.setVisibility(View.VISIBLE);
                if (isChecked) switchValue = "true";
                else switchValue = "false";
            }
        });
        /*svFreeShipment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mIsFreeShipment = isChecked;
                save.setVisibility(View.VISIBLE);
            }
        });*/
        webAction = new WebAction.WebActionBuilder()
                .setAuthHeader("58ede4d4ee786c1604f6c535")
                .build();

//        if(product_data != null && !TextUtils.isEmpty(product_data._id)) {
        //displayAssociatedWebActions();
//        } else {
//            lvInventoryData.setVisibility(View.GONE);
//        }

        lvInventoryData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showInventoryDataDialog(mInventoryAdapter.getItem(i), i);
            }
        });
    }

    private void addWebActions() {
        com.nowfloats.webactions.models.WebAction action1 = new com.nowfloats.webactions.models.WebAction();
        action1.setDisplayName("Category");

        com.nowfloats.webactions.models.WebAction action2 = new com.nowfloats.webactions.models.WebAction();
        action2.setDisplayName("Image");

        List<com.nowfloats.webactions.models.WebAction> actionList = new ArrayList<>();
        actionList.add(action1);
        actionList.add(action2);

        //mInventoryWebActionList.add(action1);
        //mInventoryWebActionList.add(action2);

        mInventoryAdapter.addAll(actionList);
        ViewGroup.LayoutParams lp = lvInventoryData.getLayoutParams();
        lp.height = Utils.dpToPx(Product_Detail_Activity_V45.this, 60) * actionList.size();
        lvInventoryData.setLayoutParams(lp);
        lvInventoryData.requestLayout();
    }

    private void afterExecutingKeywords(String productId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                materialProgress.dismiss();
            }
        });
        createKeywordList.clear();
        if (mIsApEnabled) {
            if (mShippingMetrix != null) {
                mShippingMetrix.setProductId(productId);
            }
            addShippingMetric(mShippingMetrix, false);
        } else {
            uploadProductImage(productId);
        }
    }

    private void showInventoryDataDialog(com.nowfloats.webactions.models.WebAction item, int position) {
        switch (position) {
            //Position Keywords
            case 0:
                showProductKeywordDialog();
                break;
            case 1:
                showProductImages();
                break;
        }
    }

    private void showProductImages() {
//        if (product_data != null && !TextUtils.isEmpty(product_data._id)) {
        Intent i = new Intent(this, MultipleProductImageActivity.class);
        if (product_data != null && !TextUtils.isEmpty(product_data._id)) {
            i.putExtra("product_id", product_data._id);
        } else {
            i.putExtra("product_id", "");
        }
        if (lsProductImages == null) {
            lsProductImages = new ArrayList<>();
        }
        i.putExtra("cacheImages", lsProductImages);
        startActivityForResult(i, Constants.CHOSEN_PHOTO);
//        } else {
//            Toast.makeText(this, "Please save the Product first", Toast.LENGTH_SHORT).show();
//        }

    }

    private void showProductKeywordDialog() {
        webAction.setWebActionName("product_keywords");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.product_keywords_dialog_layout, null);
        final TagView tvProductKeyword = dialogView.findViewById(R.id.tv_product_keyword);
        final ProgressBar pbAddingKeyword = dialogView.findViewById(R.id.pb_adding_keyword);
        final Button btnAddKeyword = dialogView.findViewById(R.id.btn_add_keyword);
        final EditText etKeywordInput = dialogView.findViewById(R.id.et_keyword_input);
        final ProgressBar pbKeywordLoader = dialogView.findViewById(R.id.pb_keyword_loader);
        final TextView tvAddKeywords = dialogView.findViewById(R.id.tv_add_product_keywords);
        builder.setView(dialogView);
        Dialog dialog = builder.create();
        dialog.show();
        final List<Tag> keywordList = new ArrayList<>();
        if (product_data != null) {
            IFilter filter = new WebActionsFilter();
            filter = filter.eq("_pid", product_data._id);
            pbKeywordLoader.setVisibility(View.VISIBLE);
            webAction.findProductKeywords(filter, new WebAction.WebActionCallback<List<ProductKeywordResponseModel>>() {
                @Override
                public void onSuccess(List<ProductKeywordResponseModel> result) {
                    pbKeywordLoader.setVisibility(View.INVISIBLE);
                    if (result != null && result.size() > 0) {

                        for (ProductKeywordResponseModel productKeyword : result) {
                            Tag tag = new Tag(productKeyword.getKeyword(), productKeyword.getId());
                            keywordList.add(tag);
                        }

                        tvProductKeyword.addTags(keywordList);
                    } else {
                        tvAddKeywords.setVisibility(View.VISIBLE);

                    }
                }

                @Override
                public void onFailure(WebActionError error) {
                    BoostLog.d("INVENTORY", error.getErrorMessage());
                    pbKeywordLoader.setVisibility(View.INVISIBLE);
                }
            });
        } else {
            keywordList.addAll(this.createKeywordList);
            tvProductKeyword.addTags(keywordList);
            if (keywordList == null || keywordList.size() == 0) {
                tvAddKeywords.setVisibility(View.VISIBLE);
            }
        }

        btnAddKeyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(etKeywordInput.getText().toString()) &&
                        pbAddingKeyword.getVisibility() != View.VISIBLE) {
                    if (product_data != null && !TextUtils.isEmpty(product_data._id)) {
                        btnAddKeyword.setText("");
                        pbAddingKeyword.setVisibility(View.VISIBLE);
                        final ProductKeywordReqModel reqData = new ProductKeywordReqModel();
                        reqData._pid = product_data._id;
                        reqData.keyword = etKeywordInput.getText().toString();
                        webAction.insert(session.getFpTag(), reqData, new WebAction.WebActionCallback<String>() {
                            @Override
                            public void onSuccess(String result) {
                                pbAddingKeyword.setVisibility(View.INVISIBLE);
                                btnAddKeyword.setText("Add");

                                if (!TextUtils.isEmpty(result)) {
                                    tvProductKeyword.addTag(new Tag(reqData.keyword, result));
                                }
                                etKeywordInput.setText("");
                                tvAddKeywords.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onFailure(WebActionError error) {
                                pbAddingKeyword.setVisibility(View.INVISIBLE);
                                btnAddKeyword.setText("Add");
                                etKeywordInput.setText("");
                            }
                        });
                    } else {

                        btnAddKeyword.setText("");
                        final ProductKeywordReqModel reqData = new ProductKeywordReqModel();
                        reqData._pid = UUID.randomUUID().toString();
                        reqData.keyword = etKeywordInput.getText().toString();

                        Tag tag = new Tag(reqData.keyword, UUID.randomUUID().toString());
                        btnAddKeyword.setText("Add");
                        tvProductKeyword.addTag(tag);
                        etKeywordInput.setText("");
                        tvAddKeywords.setVisibility(View.INVISIBLE);
                        createKeywordList.add(tag);
//                        Toast.makeText(Product_Detail_Activity_V45.this, "Please save the Product First", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        tvProductKeyword.setOnTagDeleteListener(new TagView.OnTagDeleteListener() {
            @Override
            public void onTagDeleted(TagView view, Tag tag, final int position) {
                if (product_data != null && !TextUtils.isEmpty(product_data._id)) {
                    IFilter filter = new WebActionsFilter();
                    filter = filter.eq("_id", tag.tagId);
                    webAction.delete(filter, false, new WebAction.WebActionCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean result) {
                            if (result) {
                                tvProductKeyword.remove(position);
                                if (tvProductKeyword.getTagsCount() == 0) {
                                    tvAddKeywords.setVisibility(View.VISIBLE);
                                }
                            }

                        }

                        @Override
                        public void onFailure(WebActionError error) {

                        }
                    });
                } else {
                    tvProductKeyword.remove(position);
                    createKeywordList.remove(position);
                    if (tvProductKeyword.getTagsCount() == 0) {
                        tvAddKeywords.setVisibility(View.VISIBLE);
                    }
                }

            }
        });


    }

    private void displayAssociatedWebActions() {
        webAction.getAllWebActions("INVENTORY", WebActionVisibility.NONE, new WebAction.WebActionCallback<List<com.nowfloats.webactions.models.WebAction>>() {
            @Override
            public void onSuccess(List<com.nowfloats.webactions.models.WebAction> result) {
                if (result == null)
                    return;

                Log.d("WEB_ACTION", "" + result.size());
                mInventoryAdapter.addAll(result);
                ViewGroup.LayoutParams lp = lvInventoryData.getLayoutParams();
                lp.height = Utils.dpToPx(Product_Detail_Activity_V45.this, 60) * result.size();
                lvInventoryData.setLayoutParams(lp);
                lvInventoryData.requestLayout();
            }

            @Override
            public void onFailure(WebActionError error) {
                BoostLog.d("INVENTORY", error.getErrorMessage());

            }
        });

    }

    /*private void updateNetAmount() {

        if (mShippingMetrix != null) {
            String price = TextUtils.isEmpty(productPrice.getText().toString().trim()) ? "0" : productPrice.getText().toString().trim();
            String discount = TextUtils.isEmpty(productDiscount.getText().toString().trim()) ? "0" : productDiscount.getText().toString().trim();
            etShippingCharge.setText(mShippingMetrix.getShippingCharge() + "");
            double transactionCharge = ((Double.parseDouble(price) - Double.parseDouble(discount)) * NF_ASSURANCE_CHARGE) / 100.0;
            double netAmount = ((Double.parseDouble(price) - (Double.parseDouble(discount) + transactionCharge + mShippingMetrix.getShippingCharge())) * 100.0) / 100.0;
            etTransactionCharge.setText(transactionCharge + "");
            etNetAmount.setText(netAmount + "");
        }
    }*/

    private void enableRealTimePriceUpdate() {
        productPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //updateNetAmount();
            }
        });

        productDiscount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //updateNetAmount();
            }
        });
    }

    /*private void showPriorityList() {
        String priorityVal = etPriority.getText().toString().trim();
        int index = 0;
        if (!Util.isNullOrEmpty(priorityVal)) {
            index = Arrays.asList(mPriorityList).indexOf(priorityVal);
        }

        new MaterialDialog.Builder(activity)
                .title(getString(R.string.select_priority))
                .items(mPriorityList)
                .widgetColorRes(R.color.primaryColor)
                .itemsCallbackSingleChoice(index, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int position, CharSequence text) {
                        try {
                            etPriority.setText(mPriorityList[position]);
                            switch (position) {
                                case 0:
                                    mPriorityVal = 1000000;
                                    break;
                                case 1:
                                    mPriorityVal = 1;
                                    break;
                                case 2:
                                    mPriorityVal = 2;
                                    break;
                                case 3:
                                    mPriorityVal = 3;

                            }
                            save.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                        return true;
                    }
                }).show();
    }*/

    private void getShippingMetrix(String productId) {

        final ProgressDialog pd = ProgressDialog.show(this, "", getString(R.string.please_wait_));
        Constants.webActionAdapter.create(ProductGalleryInterface.class)
                .getShippingMetric(String.format("{product_id:'%s'}", productId), new Callback<WebActionModel<ShippingMetricsModel>>() {
                    @Override
                    public void success(WebActionModel<ShippingMetricsModel> shippingMetricsModelWebActionModel, Response response) {
                        pd.dismiss();

                        if (shippingMetricsModelWebActionModel.getData().size() > 0) {
                            mShippingMetrix = shippingMetricsModelWebActionModel.getData().get(0);
                            //String discount = TextUtils.isEmpty(product_data.DiscountAmount) ? "0" : product_data.DiscountAmount;
                            //etShippingCharge.setText(mShippingMetrix.getShippingCharge() + "");
                            //double transactionCharge = ((Double.parseDouble(product_data.Price) - Double.parseDouble(discount)) * NF_ASSURANCE_CHARGE) / 100.0;
                            //double netAmount = ((Double.parseDouble(product_data.Price) - (Double.parseDouble(discount) + transactionCharge + mShippingMetrix.getShippingCharge())) * 100.0) / 100.0;
                            //etTransactionCharge.setText(transactionCharge + "");
                            //etNetAmount.setText(netAmount + "");

                            /*if(mShippingMetrix != null && mShippingMetrix.getHidePrice() != null)
                            {
                                isHidePrice = mShippingMetrix.getHidePrice();
                                switchHidePrice.setChecked(isHidePrice);
                            }*/
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        pd.dismiss();

                    }
                });
    }

    private boolean ValidateFields(boolean keyCheck) {

        String desc = "description", disc = "discountAmount", link = "buyOnlineLink", name = "name",
                price = "price", currency = "currencyCode", avail = "isAvailable", ship = "shipmentDuration",
                freeShipment = "isFreeShipmentAvailable", priority = "priority", availableUnits = "availableUnits";

        if (keyCheck) {
            desc = desc.toUpperCase();
            disc = "DISCOUNTPRICE";
            link = link.toUpperCase();
            name = name.toUpperCase();
            price = price.toUpperCase();
            currency = currency.toUpperCase();
            avail = "ISAVAIALABLE";
            ship = ship.toUpperCase();
            freeShipment = "FREESHIPMENT";
        }

        double product_price = 0, product_discount = 0;

        values.put(avail, switchValue);

        values.put(freeShipment, String.valueOf(mIsFreeShipment));
        values.put(priority, String.valueOf(mPriorityVal));


        try {
            values.put(currency, productCurrency.getText().toString());
        } catch (Exception e) {
            SentryController.INSTANCE.captureException(e);
            e.printStackTrace();
        }

        if (productName != null && productName.getText().toString().trim().length() > 0) {
            values.put(name, productName.getText().toString().trim());
        } else {
            YoYo.with(Techniques.Shake).playOn(productName);
            Methods.showSnackBarNegative(activity, String.format(String.valueOf(getString(R.string.enter_product_name)), productCategory.toLowerCase()));
            return false;
        }

        if (productDesc != null && productDesc.getText().toString().trim().length() > 0) {
            values.put(desc, productDesc.getText().toString().trim());
        } else {
            YoYo.with(Techniques.Shake).playOn(productDesc);
            Methods.showSnackBarNegative(activity, String.format(String.valueOf(getString(R.string.enter_product_desc)), productCategory.toLowerCase()));
            return false;
        }

        /*if (etShipmentDuration != null && etShipmentDuration.getText().toString().trim().length() > 0)
        {
            values.put(ship, etShipmentDuration.getText().toString());
        }

        else
        {
            values.put(ship, null);
        }*/

        if (productPrice != null && productPrice.getText().toString().trim().length() > 0) {

            try {
                product_price = Double.valueOf(productPrice.getText().toString().trim());
            } catch (Exception e) {
                SentryController.INSTANCE.captureException(e);
                YoYo.with(Techniques.Shake).playOn(productPrice);
                Methods.showSnackBarNegative(activity, "Please enter valid price");
                return false;
            }
        }

        values.put(price, String.valueOf(product_price));

        if (productDiscount != null && productDiscount.getText().toString().trim().length() > 0) {
            try {
                product_discount = Double.valueOf(productDiscount.getText().toString().trim());
            } catch (Exception e) {
                SentryController.INSTANCE.captureException(e);
                YoYo.with(Techniques.Shake).playOn(productDiscount);
                Methods.showSnackBarNegative(activity, "Please enter valid discount");
                return false;
            }
        }

        values.put(disc, String.valueOf(product_discount));

        if (productLink != null && productLink.getText().toString().trim().length() > 0) {
            values.put(link, productLink.getText().toString().trim());
        } else {
            values.put(link, "");
        }

        if ((productPrice != null && productPrice.getText().toString().trim().length() > 0) &&
                (productDiscount != null && productDiscount.getText().toString().trim().length() > 0)) {

            if (!(Double.parseDouble(productPrice.getText().toString().trim()) >= Double.parseDouble(productDiscount.getText().toString().trim()))) {

                YoYo.with(Techniques.Shake).playOn(productDiscount);
                Methods.showSnackBarNegative(activity, getString(R.string.discount_amount_can_not_more_than_price));
                return false;
            }
        }

        if (mShippingMetrix == null) {
            Methods.showSnackBarNegative(activity, getString(R.string.shipping_details_required));
            return false;
        }

        if (mShippingMetrix.getWeight() == null || mShippingMetrix.getHeight() == null || mShippingMetrix.getLength() == null ||
                mShippingMetrix.getWidth() == null || mShippingMetrix.getShippingCharge() == null) {
            Methods.showSnackBarNegative(activity, getString(R.string.invalid_shipping_details));
            return false;
        }

        /*if (etAvailableUnits != null && etAvailableUnits.getText().toString().trim().length() > 0)
        {
            values.put(availableUnits, etAvailableUnits.getText().toString());
        }

        else
        {
            values.put(availableUnits, "-1");

            if (!keyCheck && flag)
            {
                YoYo.with(Techniques.Shake).playOn(etAvailableUnits);
                Methods.showSnackBarNegative(activity, "Please enter product available units");
                flag = false;
            }
        }*/

        /**
         * If delivery method ASSUREDPURCHASE OR SELF then add shipping metrix
         */
        /*if((deliveryMethod.equalsIgnoreCase(Constants.DeliveryMethod.ASSURED_PURCHASE.getValue())
                || deliveryMethod.equalsIgnoreCase(Constants.DeliveryMethod.SELF.getValue())))
        {
            if(mShippingMetrix == null)
            {
                Methods.showSnackBarNegative(activity, "Shipping Details Required");
                return false;
            }

            else if(deliveryMethod.equalsIgnoreCase(Constants.DeliveryMethod.ASSURED_PURCHASE.getValue())
                    && Constants.PACKAGE_NAME.equals("com.biz2.nowfloats"))
            {
                if(mShippingMetrix.getWeight() == null || mShippingMetrix.getHeight() == null || mShippingMetrix.getLength() == null ||
                        mShippingMetrix.getWidth() == null || mShippingMetrix.getShippingCharge() == null || mShippingMetrix.getGstCharge() == null)
                {
                    Methods.showSnackBarNegative(activity, "Invalid Shipping Details");
                    return false;
                }
            }

            else if(deliveryMethod.equalsIgnoreCase(Constants.DeliveryMethod.ASSURED_PURCHASE.getValue())
                    && !Constants.PACKAGE_NAME.equals("com.biz2.nowfloats"))
            {
                if(mShippingMetrix.getWeight() == null || mShippingMetrix.getHeight() == null || mShippingMetrix.getLength() == null ||
                        mShippingMetrix.getWidth() == null || mShippingMetrix.getShippingCharge() == null)
                {
                    Methods.showSnackBarNegative(activity, "Invalid Shipping Details");
                    return false;
                }
            }

            else if(deliveryMethod.equalsIgnoreCase(Constants.DeliveryMethod.SELF.getValue()))
            {
                if(mShippingMetrix.getShippingCharge() == null || mShippingMetrix.getGstCharge() == null)
                {
                    Methods.showSnackBarNegative(activity, "Invalid Shipping Details");
                    return false;
                }
            }
        }*/

        System.out.println(values);
        return true;
    }

    private void textEditListener(MaterialEditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                save.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void invokeGetProductList(final String productId) {
        if (lsProductImages != null && lsProductImages.size() > 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    materialProgress = new MaterialDialog.Builder(activity)
                            .widgetColorRes(R.color.accentColor)
                            .content(getString(R.string.uploading_other_image))
                            .progress(true, 0).show();
                    materialProgress.setCancelable(false);
                }
            });

            webAction.setWebActionName("product_images");
            imageCount = 0;
            for (ProductImageResponseModel productImageResponseModel : lsProductImages) {
                webAction.uploadFile(productImageResponseModel.getImage().url, new WebAction.WebActionCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        addImageData(productId, result);
                    }

                    @Override
                    public void onFailure(WebActionError error) {
                        imageCount++;
                        if (imageCount == lsProductImages.size()) {
                            navigateToProductDetail();
                        }
                    }
                }, new Handler(Looper.getMainLooper()));
            }
        } else {
            navigateToProductDetail();
        }

    }

    private void addImageData(String productId, String result) {
        final ProductImageRequestModel productImageRequestModel = new ProductImageRequestModel();
        productImageRequestModel._pid = productId;
        productImageRequestModel.image = new ProductImage(result, "Description");
        webAction.insert(session.getFpTag(), productImageRequestModel, new WebAction.WebActionCallback<String>() {
            @Override
            public void onSuccess(String id) {
                imageCount++;
                if (imageCount == lsProductImages.size()) {
                    navigateToProductDetail();
                }
            }

            @Override
            public void onFailure(WebActionError error) {
                imageCount++;
                if (imageCount == lsProductImages.size()) {
                    navigateToProductDetail();
                }
            }
        });
    }

    private void navigateToProductDetail() {
        if (materialProgress != null && materialProgress.isShowing())
            materialProgress.dismiss();
        if (lsProductImages != null && lsProductImages.size() > 0) {
            lsProductImages.clear();
        }
        values = new HashMap<>();
        values.put("clientId", Constants.clientId);
        values.put("skipBy", "0");
        values.put("fpTag", session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG));
        //invoke getProduct api
        apiService.getProductList(activity, values, Product_Gallery_Fragment.bus);
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    private void uploadProductImage(String productId) {
        try {
            String valuesStr = "clientId=" + Constants.clientId
                    + "&requestType=sequential&requestId=" + Constants.deviceId
                    + "&totalChunks=1&currentChunkNumber=1&productId=" + productId;
            String url = Constants.NOW_FLOATS_API_URL + "/Product/v1/AddImage?" + valuesStr;
            byte[] imageBytes = Methods.compressToByte(path, activity);
            new ProductImageUploadV45(url, imageBytes, Product_Detail_Activity_V45.this, productId).execute();
        } catch (Exception e) {
            SentryController.INSTANCE.captureException(e);
            e.printStackTrace();
            Methods.showSnackBarNegative(activity, getString(R.string.something_went_wrong_try_again));
        }
    }

    private void replaceProductImage(String productId) {
        try {
            MixPanelController.track(EventKeysWL.PRODUCT_GALLERY_UPDATEIMAGE, null);
            uploadProductImage(productId);
        } catch (Exception e) {
            SentryController.INSTANCE.captureException(e);
            e.printStackTrace();
            Methods.showSnackBarNegative(activity, getString(R.string.something_went_wrong_try_again));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_card_product_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public String showCurrencyList(Activity activity, final String[] currencyList) {
        String currencyVal = productCurrency.getText().toString().trim();
        int index = 0;
        if (!Util.isNullOrEmpty(currencyVal)) {
            index = Arrays.asList(currencyList).indexOf(currencyVal);
        }
        new MaterialDialog.Builder(activity)
                .title(getString(R.string.select_currency))
                .items(currencyList)
                .widgetColorRes(R.color.primaryColor)
                .itemsCallbackSingleChoice(index, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int position, CharSequence text) {
                        try {
                            currencyType = currencyList[position];
                            String s = currencyType.split("-")[1];
                            productCurrency.setText(s);
                            save.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            SentryController.INSTANCE.captureException(e);
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                        return true;
                    }
                }).show();
        return currencyType;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mIsImagePicking = false;
        if (resultCode == RESULT_OK && (Constants.CHOSEN_PHOTO == requestCode)) {
            if (data != null) {
                lsProductImages = (ArrayList<ProductImageResponseModel>) data.getExtras().get("cacheImages");
            }
        } else if (resultCode == RESULT_OK && (Constants.GALLERY_PHOTO == requestCode)) {
            if (data != null) {
                picUri = data.getData();
                if (picUri == null) {
                    CameraBitmap = (Bitmap) data.getExtras().get("data");
                    path = Util.saveBitmap(CameraBitmap, activity, tagName + System.currentTimeMillis());
                    picUri = Uri.parse(path);
                    productImage.setImageBitmap(CameraBitmap);
                    if (replaceImage) replaceProductImage(product_data._id);
                } else {
                    path = getRealPathFromURI(picUri);
                    CameraBitmap = Util.getBitmap(path, activity);
                    if (CameraBitmap != null) {
                        productImage.setImageBitmap(CameraBitmap);
                    }
                    if (replaceImage) replaceProductImage(product_data._id);
                }
            }
        } else if (resultCode == RESULT_OK && (Constants.CAMERA_PHOTO == requestCode)) {
            try {
                if (picUri == null) {
                    if (data != null) {
                        picUri = data.getData();
                        if (picUri == null) {
                            CameraBitmap = (Bitmap) data.getExtras().get("data");
                            path = Util.saveCameraBitmap(CameraBitmap, activity, tagName + System.currentTimeMillis());
                            picUri = Uri.parse(path);
                            productImage.setImageBitmap(CameraBitmap);
                            if (replaceImage) replaceProductImage(product_data._id);
                        } else {
                            path = getRealPathFromURI(picUri);
                            CameraBitmap = Util.getBitmap(path, activity);
                            productImage.setImageBitmap(CameraBitmap);
                            if (replaceImage) replaceProductImage(product_data._id);
                        }
                    } else {
                        Methods.showSnackBar(activity, getString(R.string.try_again));
                    }
                } else {
                    path = getRealPathFromURI(picUri);
                    CameraBitmap = Util.getBitmap(path, activity);
                    productImage.setImageBitmap(CameraBitmap);
                    if (replaceImage) replaceProductImage(product_data._id);
                }
            } catch (Exception e) {
                SentryController.INSTANCE.captureException(e);
                e.printStackTrace();
            } catch (OutOfMemoryError E) {
                E.printStackTrace();
                CameraBitmap.recycle();
                System.gc();
                Methods.showSnackBar(activity, getString(R.string.try_again));
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {

        String val = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            val = cursor.getString(column_index);
            cursor.close();
        }
        return val;
    }

    public void choosePicture() {
        final MaterialDialog dialog = new MaterialDialog.Builder(activity)
                .customView(R.layout.featuredimage_popup, true)
                .show();
        final PorterDuffColorFilter whiteLabelFilter_pop_ip = new PorterDuffColorFilter(getResources().getColor(R.color.primaryColor), PorterDuff.Mode.SRC_IN);

        View view = dialog.getCustomView();
        TextView header = (TextView) view.findViewById(R.id.textview_heading);
        if (replaceImage) header.setText(getString(R.string.replace_photo));
        else header.setText(getString(R.string.upload_photo));
        LinearLayout takeCamera = (LinearLayout) view.findViewById(R.id.cameraimage);
        LinearLayout takeGallery = (LinearLayout) view.findViewById(R.id.galleryimage);
        ImageView cameraImg = (ImageView) view.findViewById(R.id.pop_up_camera_imag);
        ImageView galleryImg = (ImageView) view.findViewById(R.id.pop_up_gallery_img);
        cameraImg.setColorFilter(whiteLabelFilter_pop_ip);
        galleryImg.setColorFilter(whiteLabelFilter_pop_ip);

        takeCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraIntent();
                dialog.dismiss();
            }
        });

        takeGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryIntent();
                dialog.dismiss();

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == media_req_id) {
            if (ActivityCompat.checkSelfPermission(Product_Detail_Activity_V45.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(Product_Detail_Activity_V45.this, Manifest.permission.CAMERA) !=
                    PackageManager.PERMISSION_GRANTED) {
                Methods.showApplicationPermissions(getString(R.string.camera_and_storage_permission), getString(R.string.we_need_this_permission_to_enable_capture_and_upload_images), Product_Detail_Activity_V45.this);
            }

        } else if (requestCode == gallery_req_id) {
            if (ActivityCompat.checkSelfPermission(Product_Detail_Activity_V45.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {
                Methods.showApplicationPermissions(getString(R.string.storage_permission), getString(R.string.we_need_this_to_enable_image_upload), Product_Detail_Activity_V45.this);
            }
        }
    }

    public void galleryIntent() {
        try {
            if (ActivityCompat.checkSelfPermission(Product_Detail_Activity_V45.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Methods.showApplicationPermissions(getString(R.string.storage_permission), getString(R.string.we_need_this_to_enable_image_upload), Product_Detail_Activity_V45.this);
                } else {
                    ActivityCompat.requestPermissions(Product_Detail_Activity_V45.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, gallery_req_id);
                }

            } else {
                mIsImagePicking = true;
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, Constants.GALLERY_PHOTO);
            }
        } catch (ActivityNotFoundException anfe) {
            SentryController.INSTANCE.captureException(anfe);
            // display an error message
            String errorMessage = getString(R.string.device_does_not_support_capturing_image);
            Methods.showSnackBarNegative(activity, errorMessage);
        }
    }

    public void cameraIntent() {
        try {
            // use standard intent to capture an image
            if (ActivityCompat.checkSelfPermission(Product_Detail_Activity_V45.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(Product_Detail_Activity_V45.this, Manifest.permission.CAMERA) !=
                    PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
                    Methods.showApplicationPermissions(getString(R.string.camera_and_storage_permission), getString(R.string.we_need_this_permission_to_enable_capture_and_upload_images), Product_Detail_Activity_V45.this);
                } else {
                    ActivityCompat.requestPermissions(Product_Detail_Activity_V45.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, media_req_id);
                }

            } else {
                mIsImagePicking = true;
                ContentValues Cvalues = new ContentValues();
                Intent captureIntent;
                Cvalues.put(MediaStore.Images.Media.TITLE, "New Picture");
                Cvalues.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                picUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Cvalues);
                captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
                startActivityForResult(captureIntent, Constants.CAMERA_PHOTO);
            }
        } catch (ActivityNotFoundException anfe) {
            SentryController.INSTANCE.captureException(anfe);
            // display an error message
            String errorMessage = getString(R.string.device_does_not_support_capturing_image);
            Methods.showSnackBarNegative(activity, errorMessage);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mRiaNodedata != null && !mIsImagePicking) {
            RiaEventLogger.getInstance().logPostEvent(session.getFpTag(),
                    mRiaNodedata.getNodeId(), mRiaNodedata.getButtonId(),
                    mRiaNodedata.getButtonLabel(),
                    RiaEventLogger.EventStatus.DROPPED.getValue());
            mRiaNodedata = null;
        }
    }

    public void onHintClicked(View v) {
        switch (v.getId()) {
            case R.id.iv_hint_product_name:
                showHintDialog(getString(R.string.product_name_hint));
                break;
            case R.id.iv_hint_product_description:
                showHintDialog(getString(R.string.product_desc_hint));
                break;
            case R.id.iv_hint_product_price:
                showHintDialog(getString(R.string.product_price_hint));
                break;
            case R.id.iv_hint_discount:
                showHintDialog(getString(R.string.product_discount_hint));
                break;
            case R.id.iv_hint_select_currency:
                showHintDialog(getString(R.string.product_currency_hint));
                break;
            case R.id.iv_hint_select_priority:
                showHintDialog(getString(R.string.product_priority_hint));
                break;
            case R.id.iv_hint_net_amount:
                showHintDialog(getString(R.string.product_net_amount_hint));
                break;
            case R.id.iv_hint_shipping_charge:
                showHintDialog(getString(R.string.product_shipping_charge_hint));
                break;
            case R.id.iv_hint_shipping_days:
                showHintDialog(getString(R.string.product_shipping_days_hint));
                break;
            case R.id.iv_hint_available_units:
                showHintDialog(getString(R.string.product_available_unit_hint));
                break;
            case R.id.iv_hint_transaction_charge:
                showHintDialog(getString(R.string.product_transaction_charge_hint));
                break;
            case R.id.iv_hint_product_link:
                showHintDialog(getString(R.string.product_buy_online_hint));
                break;
        }
    }

    private void showHintDialog(String hint) {
        new AlertDialog.Builder(this)
                .setMessage(hint)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void onProductMetricCalculated(ShippingMetricsModel shippingMetricsModel, ShippingCalculatorFragment.ShippingAddOrUpdate val) {

        //String price = TextUtils.isEmpty(productPrice.getText().toString().trim()) ? "0" : productPrice.getText().toString().trim();
        //String discount = TextUtils.isEmpty(productDiscount.getText().toString().trim()) ? "0" : productPrice.getText().toString().trim();
        //double transactionCharge = ((Double.parseDouble(price) - Double.parseDouble(price)) * NF_ASSURANCE_CHARGE) / 100.0;
        //double netAmount = ((Double.parseDouble(price) - (Double.parseDouble(discount) + transactionCharge + shippingMetricsModel.getShippingCharge())) * 100.0) / 100.0;

        /*if (netAmount < shippingMetricsModel.getShippingCharge())
        {
            Methods.showSnackBarNegative(this, "NetAmount can't be less than Shipping Charge");
            return;
        }*/

        //etShippingCharge.setText(shippingMetricsModel.getShippingCharge() + "");
        //etTransactionCharge.setText(transactionCharge + "");
        //etNetAmount.setText(netAmount + "");

        if (mShippingMetrix == null) {
            mShippingMetrix = new ShippingMetricsModel();
        }
        if (product_data != null) {
            mShippingMetrix.setProductId(product_data._id);
        }

        mShippingMetrix.setMerchantId(session.getFPID());

        if (shippingMetricsModel != null) {
            mShippingMetrix.setShippingCharge(shippingMetricsModel.getShippingCharge());
            mShippingMetrix.setGstCharge(shippingMetricsModel.getGstCharge());
            mShippingMetrix.setLength(shippingMetricsModel.getLength());
            mShippingMetrix.setHeight(shippingMetricsModel.getHeight());
            mShippingMetrix.setWeight(shippingMetricsModel.getWeight());
            mShippingMetrix.setWidth(shippingMetricsModel.getWidth());
        }

        //mShippingMetrix.setHidePrice(isHidePrice);

        /*if (val == ShippingCalculatorFragment.ShippingAddOrUpdate.ADD && product_data != null)
        {
            addShippingMetric(mShippingMetrix, true);
        }*/

        if (val == ShippingCalculatorFragment.ShippingAddOrUpdate.UPDATE && product_data != null) {
            Methods.showSnackBarPositive(activity, "Shipping Matrix Updated Successfully");
        }
    }


    private void updateShippingMetric() {
        try {
            this.onProductMetricCalculated(mShippingMetrix, ShippingCalculatorFragment.ShippingAddOrUpdate.UPDATE);

            WaUpdateDataModel update = new WaUpdateDataModel();

            update.setQuery(String.format("{product_id:'%s'}", mShippingMetrix.getProductId()));

            if (deliveryMethod.equalsIgnoreCase(Constants.DeliveryMethod.ASSURED_PURCHASE.getValue())) {
                if (Constants.PACKAGE_NAME.equals("com.biz2.nowfloats")) {
                    update.setUpdateValue(String.format("{$set:{length:'%s', width:'%s', weight:'%s', height:'%s', shipping_charge:%s, gst_slab:%s, hide_price:%s}}",
                            mShippingMetrix.getLength(),
                            mShippingMetrix.getWidth(),
                            mShippingMetrix.getWeight(),
                            mShippingMetrix.getHeight(),
                            mShippingMetrix.getShippingCharge().toString(),
                            mShippingMetrix.getGstCharge().toString(), mShippingMetrix.getHidePrice()));
                } else {
                    update.setUpdateValue(String.format("{$set:{length:'%s', width:'%s', weight:'%s', height:'%s', shipping_charge:%s, hide_price:%s}}",
                            mShippingMetrix.getLength(),
                            mShippingMetrix.getWidth(),
                            mShippingMetrix.getWeight(),
                            mShippingMetrix.getHeight(),
                            mShippingMetrix.getShippingCharge().toString(), mShippingMetrix.getHidePrice()));
                }
            } else if (deliveryMethod.equalsIgnoreCase(Constants.DeliveryMethod.SELF.getValue())) {
                update.setUpdateValue(String.format("{$set:{shipping_charge:%s, gst_slab:%s, hide_price:%s}}",
                        mShippingMetrix.getShippingCharge().toString(),
                        mShippingMetrix.getGstCharge().toString(), mShippingMetrix.getHidePrice()));
            } else {
                update.setUpdateValue(String.format("{$set:{hide_price:%s}}", mShippingMetrix.getHidePrice()));
            }

            update.setMulti(true);

            Constants.webActionAdapter.create(ProductGalleryInterface.class)
                    .updateProductMetrics(update, new Callback<String>() {
                        @Override
                        public void success(String s, Response response) {

                        }

                        @Override
                        public void failure(RetrofitError error) {

                            /*if(error.getResponse().getStatus() == 200)
                            {

                            }*/
                        }
                    });
        } catch (Exception e) {
            SentryController.INSTANCE.captureException(e);
            e.printStackTrace();
        }
    }


    private void addShippingMetric(final ShippingMetricsModel mShippingMetrix, final boolean isFromcallBack) {

        materialProgress = new MaterialDialog.Builder(activity)
                .widgetColorRes(R.color.accentColor)
                .content("Adding Shipping Matrix ...")
                .progress(true, 0).show();
        materialProgress.setCancelable(false);
        WAAddDataModel<ShippingMetricsModel> waModel = new WAAddDataModel<>();
        waModel.setWebsiteId(session.getFPID());
        waModel.setActionData(mShippingMetrix);

        Constants.webActionAdapter.create(ProductGalleryInterface.class)
                .addProductMetrics(waModel, new Callback<String>() {
                    @Override
                    public void success(String s, Response response) {

                        materialProgress.dismiss();
                        if (!isFromcallBack) {
                            uploadProductImage(mShippingMetrix.getProductId());
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {

                        materialProgress.dismiss();
                        if (!isFromcallBack) {
                            uploadProductImage(mShippingMetrix.getProductId());
                        }
                    }
                });
    }
}
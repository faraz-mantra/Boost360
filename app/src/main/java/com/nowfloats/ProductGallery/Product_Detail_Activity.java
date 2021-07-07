package com.nowfloats.ProductGallery;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.models.Image;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.ProductGallery.Adapter.ProductImageAdapter;
import com.nowfloats.ProductGallery.Model.ImageListModel;
import com.nowfloats.ProductGallery.Model.ProductListModel;
import com.nowfloats.ProductGallery.Model.Product_Gallery_Update_Model;
import com.nowfloats.ProductGallery.Model.UpdateValue;
import com.nowfloats.ProductGallery.Service.MultipleImageUploadService;
import com.nowfloats.ProductGallery.Service.ProductAPIService;
import com.nowfloats.ProductGallery.Service.ProductDelete;
import com.nowfloats.ProductGallery.Service.ProductGalleryInterface;
import com.nowfloats.ProductGallery.Service.ProductImageReplace;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.WebEngageController;
import com.nowfloats.webactions.WebAction;
import com.nowfloats.webactions.models.WebActionError;
import com.nowfloats.webactions.models.WebActionVisibility;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.thinksity.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.framework.webengageconstant.EventLabelKt.EVENT_LABEL_UPDATE;
import static com.framework.webengageconstant.EventNameKt.EVENT_NAME_UPDATE_CREATE;

/**
 * Created by guru on 09-06-2015.
 */
public class Product_Detail_Activity extends AppCompatActivity {
    public static final String ACTION = "com.nowFloats.Product_Gallery.ProductDetailsActivity";
    public static boolean replaceImage = false;
    private final int gallery_req_id = 6;
    private final int media_req_id = 5;
    public Toolbar toolbar;
    public ImageView save;
    public ProductListModel product_data;
    public UserSessionManager session;
    public String path;
    public ProductAPIService apiService;
    public int retryImage = 0;
    public boolean mIsReplacing = false;
    MaterialEditText productName, productDesc, productCurrency, productPrice, productDiscount, productLink, etShipmentDuration, etPriority;
    //ImageView productImage;
    Switch switchView, svFreeShipment;
    ProductGalleryInterface productInterface;
    Activity activity;
    String tagName = "";
    RecyclerView rvProductImg;
    private String currencyType = "";
    private Bitmap CameraBitmap;
    private Uri picUri;
    private MaterialDialog materialProgress;
    private HashMap<String, String> values;
    private String switchValue = "true";
    private boolean mIsFreeShipment = false;
    private String[] mPriorityList = getResources().getStringArray(R.array.priority_list);
    private int mPriorityVal = 1000000;
    private ProductImageAdapter adapter;
    private List<Image> mProductImageList;
    private IntentFilter mIntentFilter;
    private ProgressDialog pd;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            values = new HashMap<>();
            values.put("clientId", Constants.clientId);
            values.put("skipBy", "0");
            values.put("fpTag", session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG));
            //invoke getProduct api
            apiService.getProductList(activity, values, Product_Gallery_Fragment.bus);
            if (pd != null) {
                pd.dismiss();
            }
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
    };
    //Bus bus;
    private boolean mIsNew = true;
    private boolean mIsNewImageAdded = false;
    private int mSelectedPosition = 0;
    private boolean mIsImageChosen = false;
    private boolean mIsImageDeleted = false;
    private WebAction mWebAction;

    //private ImageView ivImageTest;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail);
        toolbar = (Toolbar) findViewById(R.id.tool_bar_product_detail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity = Product_Detail_Activity.this;
        apiService = new ProductAPIService();
        save = (ImageView) toolbar.findViewById(R.id.home_view_delete_card);
        TextView title = (TextView) toolbar.findViewById(R.id.titleProduct);
        title.setVisibility(View.VISIBLE);
        title.setText(getString(R.string.add_product));
        save.setImageResource(R.drawable.product_tick);
        session = new UserSessionManager(getApplicationContext(), activity);
        productInterface = Constants.restAdapter.create(ProductGalleryInterface.class);
        tagName = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG);
        switchView = (Switch) findViewById(R.id.switchView);
        svFreeShipment = (Switch) findViewById(R.id.sv_free_shipping);
        switchView.setChecked(true);

        //bus = new Bus();

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(ACTION);

        WebEngageController.trackEvent(EVENT_NAME_UPDATE_CREATE, EVENT_LABEL_UPDATE, session.getFpTag());
        PorterDuffColorFilter color = new PorterDuffColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
        save.setColorFilter(color);

        //recyclerView
        rvProductImg = (RecyclerView) findViewById(R.id.rv_product_img);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvProductImg.setLayoutManager(layoutManager);
        rvProductImg.setHasFixedSize(true);
        mProductImageList = new ArrayList<>();
        adapter = new ProductImageAdapter(mProductImageList);
        rvProductImg.setAdapter(adapter);
        adapter.setOnItemClickListener(new ProductImageAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showOptions(view, position);
            }
        });

        //ivImageTest = (ImageView) findViewById(R.id.ivTest);


        findViewById(R.id.btn_choose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsImageChosen && mProductImageList.size() >= 5) {
                    Methods.showSnackBarNegative(Product_Detail_Activity.this, getString(R.string.can_not_select_more_than_5images));
                } else {
                    choosePicture();
                }
            }
        });

        //productImage = (ImageView)findViewById(R.id.product_image);
        productName = (MaterialEditText) findViewById(R.id.product_name);
        productDesc = (MaterialEditText) findViewById(R.id.product_desc);
        productCurrency = (MaterialEditText) findViewById(R.id.product_currency);
        productPrice = (MaterialEditText) findViewById(R.id.product_retail_price);
        productDiscount = (MaterialEditText) findViewById(R.id.product_disc_price);
        productLink = (MaterialEditText) findViewById(R.id.product_link);
        etShipmentDuration = (MaterialEditText) findViewById(R.id.et_shipping_days);
        etPriority = (MaterialEditText) findViewById(R.id.product_priority);
        Button deleteProduct = (Button) findViewById(R.id.delete_product);
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
        etPriority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPriorityList();
            }
        });

        /*productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               choosePicture();
            }
        });*/

        if (getIntent().hasExtra("product")) {
//            product_data = getIntent().getExtras().getParcelable("product");
            mIsNew = false;
            final int position = Integer.parseInt(getIntent().getExtras().getString("product"));
            product_data = Product_Gallery_Fragment.productItemModelList.get(position);
            if (product_data != null) {
                replaceImage = true;
                save.setVisibility(View.GONE);
                title.setText(getString(R.string.edit_product));
                BoostLog.d("ProductId:", product_data._id);
                //load image

              /*  try{
                    //currencyType = Constants.Currency_Country_Map.get(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY).toLowerCase());
                    productCurrency.setText(product_data.CurrencyCode);
                    //final String[] array = Constants.currencyArray.toArray(new String[Constants.currencyArray.size()]);
                    //Arrays.sort(array);
                    productCurrency.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showCurrencyList(activity,array);
                        }
                    });
                }catch(Exception e){e.printStackTrace();}*/

                final List<ImageListModel> imageList = product_data.Images;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //final List<Image> newImageList = new ArrayList<>();
                        if (imageList != null) {
                            for (ImageListModel model : imageList) {
                                mProductImageList.add(new Image(1, null, model.TileImageUri, true));
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                }).start();
                //ViewCompat.setTransitionName(productImage, "imageKey");
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
                if (price != null && price.trim().length() > 0 && !price.equals("0"))
                    productPrice.setText(price);
                textEditListener(productPrice);
                //discount
                String dsPrice = product_data.DiscountAmount;
                if (dsPrice != null && dsPrice.trim().length() > 0 && !dsPrice.equals("0"))
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
                String freeShipment = product_data.IsFreeShipmentAvailable;
                if (freeShipment != null && freeShipment.trim().length() > 0 && !freeShipment.equals("0")) {
                    if (freeShipment.equals("true")) {
                        svFreeShipment.setChecked(true);
                        mIsFreeShipment = true;
                    } else {
                        svFreeShipment.setChecked(false);
                        mIsFreeShipment = false;
                    }
                }

                //link
                String link = product_data.BuyOnlineLink;
                if (link != null && link.trim().length() > 0 && !link.equals("0"))
                    productLink.setText(link);
                textEditListener(productLink);
                //shipment duration
                String shipmentDuration = product_data.ShipmentDuration;
                if (shipmentDuration != null && shipmentDuration.trim().length() > 0 && !shipmentDuration.equals("0"))
                    etShipmentDuration.setText(shipmentDuration);
                textEditListener(etShipmentDuration);
                //Currency Code
                String currencyCode = product_data.CurrencyCode;
                if (currencyCode != null && currencyCode.trim().length() > 0 && !currencyCode.equals("0"))
                    productCurrency.setText(currencyCode);
                //textEditListener(productCurrency);

                //priority
                String priority = product_data.Priority;
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
                }


                //update onclick listener
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MixPanelController.track(EventKeysWL.PRODUCT_GALLERY_UPDATE, null);
                        try {
                            materialProgress = new MaterialDialog.Builder(activity)
                                    .widgetColorRes(R.color.accentColor)
                                    .content(getString(R.string.updating))
                                    .progress(true, 0).show();
                            materialProgress.setCancelable(false);
                            values = new HashMap<String, String>();
                            boolean flag = ValidateFields(true);
                            ArrayList<UpdateValue> updates = new ArrayList<UpdateValue>();
                            for (Map.Entry<String, String> entry : values.entrySet()) {
                                updates.add(new UpdateValue(entry.getKey(), entry.getValue()));
                            }

                            if (flag) {
                                BoostLog.d("Product_Detail_Activity", updates.toString());
                                Product_Gallery_Update_Model model = new Product_Gallery_Update_Model(Constants.clientId, product_data._id, updates);

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
                                                    e.printStackTrace();
                                                }
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        materialProgress.dismiss();
                                                        if (mIsImageChosen && !mIsReplacing) {
                                                            uploadProductImage(product_data._id);
                                                        } else {
                                                            invokeGetProductList();
                                                        }
                                                        //invokeGetProductList();
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
                            e.printStackTrace();
                            materialProgress.dismiss();
                            Methods.showSnackBarNegative(activity, getString(R.string.something_went_wrong_try_again));
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
                                    .negativeText(getString(R.string.are_you_sure_want_to_delete))
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
                                                new ProductDelete(url, jsonObject.toString(), Product_Detail_Activity.this, position).execute();
                                            } catch (JSONException e) {
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
                            e.printStackTrace();
                        }
                    }
                });
            }
        } else if (getIntent().hasExtra("new")) {
            mIsNew = true;
            try {
                // make keyboard visible
                findViewById(R.id.productLayout).postDelayed(
                        new Runnable() {
                            public void run() {
                                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                inputMethodManager.toggleSoftInputFromWindow(productName.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                                productName.requestFocus();
                            }
                        }, 500);
            } catch (Exception e) {
                e.printStackTrace();
            }
            replaceImage = false;
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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

                        if (mProductImageList.isEmpty()) {
                            flag = false;
                            Methods.showSnackBarNegative(activity, getString(R.string.upload_product_image));
                        }
                        if (flag) {
                            productInterface.addProduct(values, new Callback<String>() {
                                @Override
                                public void success(String productId, Response response) {
                                    Log.i("PRODUCT ID__", "" + productId);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            materialProgress.dismiss();
                                        }
                                    });
                                    uploadProductImage(productId);
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
        svFreeShipment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mIsFreeShipment = isChecked;
                save.setVisibility(View.VISIBLE);
            }
        });
        mWebAction = new WebAction.WebActionBuilder()
                .setAuthHeader("58ede4d4ee786c1604f6c535")
                .build();
        displayAssociatedWebActions();
    }

    private void displayAssociatedWebActions() {
        mWebAction.getAllWebActions("INVENTORY", WebActionVisibility.NONE, new WebAction.WebActionCallback<List<com.nowfloats.webactions.models.WebAction>>() {
            @Override
            public void onSuccess(List<com.nowfloats.webactions.models.WebAction> result) {

            }

            @Override
            public void onFailure(WebActionError error) {

            }
        });

    }

    private void showOptions(View itemView, final int position) {
        final MaterialDialog dialog = new MaterialDialog.Builder(activity)
                .customView(R.layout.select_action, true)
                .show();
        final PorterDuffColorFilter whiteLabelFilter_pop_ip = new PorterDuffColorFilter(getResources().getColor(R.color.primaryColor), PorterDuff.Mode.SRC_IN);

        View view = dialog.getCustomView();
        TextView header = (TextView) view.findViewById(R.id.textview_heading);
        header.setText(getString(R.string.select_action));
        LinearLayout replace = (LinearLayout) view.findViewById(R.id.replace_image);
        final LinearLayout delete = (LinearLayout) view.findViewById(R.id.img_delete);
        ImageView replaceImg = (ImageView) view.findViewById(R.id.pop_up_replace_img);
        ImageView deleteImg = (ImageView) view.findViewById(R.id.pop_up_delete_img);
        replaceImg.setColorFilter(whiteLabelFilter_pop_ip);
        deleteImg.setColorFilter(whiteLabelFilter_pop_ip);

        replace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsReplacing = true;
                mSelectedPosition = position;
                choosePicture();
                dialog.dismiss();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*galleryIntent();
                dialog.dismiss();*/
                if (!mIsNew) {
                    deleteProductImage(position, product_data._id);
                } else {
                    mProductImageList.remove(position);
                    adapter.notifyDataSetChanged();
                }
                dialog.dismiss();

            }
        });
    }

    private void deleteProductImage(final int position, String productId) {
        final ProgressDialog pd = ProgressDialog.show(this, "", getString(R.string.wait_while_deleting_image));
        final String imageFileName = mProductImageList.get(position).path.replace("/tile/", "/actual/");
        BoostLog.d("ImageFileName:", imageFileName);
        ProductGalleryInterface productGalleryInterface = new RestAdapter.Builder()
                .setEndpoint(Constants.NOW_FLOATS_API_URL/*"http://api.withfloats.org"*/)
                .build()
                .create(ProductGalleryInterface.class);
        HashMap<String, String> map = new HashMap<>();
        map.put("clientId", Constants.clientId);
        map.put("imageFileName", imageFileName);
        map.put("productId", productId);
        productGalleryInterface.deleteProductImage(map, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                if (s.equals(imageFileName)) {
                    pd.dismiss();
                    mIsImageDeleted = true;
                    Methods.showSnackBarPositive(Product_Detail_Activity.this, getString(R.string.successfully_deleted));
                    mProductImageList.remove(position);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                BoostLog.d("RetrofitError:", error.getMessage());
                pd.dismiss();
                Methods.showSnackBarNegative(Product_Detail_Activity.this, getString(R.string.can_not_delete_image));
            }
        });
        //new DeleteProductImage(this).execute(imageFileName, productId);
    }

    private void showPriorityList() {
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
    }

    private boolean ValidateFields(boolean keyCheck) {
        boolean flag = true;
        String desc = "description", disc = "discountAmount", link = "buyOnlineLink", name = "name", price = "price", currency = "currencyCode", avail = "isAvailable", ship = "shipmentDuration", freeShipment = "isFreeShipmentAvailable", priority = "priority";
        if (keyCheck) {
            desc = desc.toUpperCase();
            disc = "DISCOUNTPRICE";
            link = link.toUpperCase();
            name = name.toUpperCase();
            price = price.toUpperCase();
            currency = currency.toUpperCase();
            avail = "ISAVAIALABLE";
            ship = ship.toUpperCase();
        }
        if (keyCheck) {
            freeShipment = "FREESHIPMENT";
        }


        values.put(avail, switchValue);

        values.put(freeShipment, String.valueOf(mIsFreeShipment));
        values.put(priority, String.valueOf(mPriorityVal));


        try {
            values.put(currency, productCurrency.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (productDesc != null && productDesc.getText().toString().trim().length() > 0) {
            values.put(desc, productDesc.getText().toString().trim());
        } else {
            values.put(desc, "");
        }

        if (etShipmentDuration != null && etShipmentDuration.getText().toString().trim().length() > 0) {
            values.put(ship, etShipmentDuration.getText().toString());
        } else {
            values.put(ship, null);
        }

        if (productDiscount != null && productDiscount.getText().toString().trim().length() > 0) {
            values.put(disc, productDiscount.getText().toString().trim());
        } else {
            values.put(disc, "0");
        }

        if (productLink != null && productLink.getText().toString().trim().length() > 0) {
            values.put(link, productLink.getText().toString().trim());
        } else {
            values.put(link, "");
        }


        if (productName != null && productName.getText().toString().trim().length() > 0) {
            values.put(name, productName.getText().toString().trim());
        } else {
            YoYo.with(Techniques.Shake).playOn(productName);
            Methods.showSnackBarNegative(activity, getString(R.string.enter_product_name));
            flag = false;
        }
        if (flag) {
            if (productPrice != null && productPrice.getText().toString().trim().length() > 0) {
                values.put(price, productPrice.getText().toString().trim());
            } else {
                YoYo.with(Techniques.Shake).playOn(productPrice);
                Methods.showSnackBarNegative(activity, getString(R.string.enter_product_price));
                flag = false;
            }
        }

        if ((productPrice != null && productPrice.getText().toString().trim().length() > 0) &&
                (productDiscount != null && productDiscount.getText().toString().trim().length() > 0) && flag) {
            if (!(Double.parseDouble(productPrice.getText().toString().trim()) > Double.parseDouble(productDiscount.getText().toString().trim()))) {
                YoYo.with(Techniques.Shake).playOn(productDiscount);
                Methods.showSnackBarNegative(activity, getString(R.string.discount_amount_can_not_more_than_price));
                flag = false;
            }
        }

        System.out.println(values);

        return flag;
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

    public void invokeGetProductList() {
        mIsImageDeleted = false;
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
            /*String valuesStr = "clientId="+Constants.clientId
                    +"&requestType=sequential&requestId="+Constants.deviceId
                    +"&totalChunks=1&currentChunkNumber=1&productId="+productId;
            String url = Constants.NOW_FLOATS_API_URL + "/Product/v1/AddImage?" +valuesStr;
            byte[] imageBytes = Methods.compressToByte(path,activity);
            new ProductImageUpload(url,imageBytes,Product_Detail_Activity.this).execute();*/
            pd = ProgressDialog.show(this, "", getString(R.string.wait_while_deleting_image));
            for (Image image : mProductImageList) {
                Intent i = new Intent(this, MultipleImageUploadService.class);
                i.putExtra(MultipleImageUploadService.REQUEST_PI, productId);
                i.putExtra(MultipleImageUploadService.REQUEST_FILE_NAME, image.path);
                startService(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Methods.showSnackBarNegative(activity, getString(R.string.something_went_wrong_try_again));
        }
    }

    private void replaceProductImage(String productId, String oldUrl, String path) {
        try {
            MixPanelController.track(EventKeysWL.PRODUCT_GALLERY_UPDATEIMAGE, null);
            if (oldUrl != null && oldUrl.trim().length() > 0) {
                String[] temp = oldUrl.split("\\/");
                String oldPic = temp[temp.length - 1];
                String valuesStr = "clientId=" + Constants.clientId
                        + "&requestType=sequential&requestId=" + Constants.deviceId
                        + "&totalChunks=1&currentChunkNumber=1&productId=" + productId + "&imageFileName=" + oldUrl.replace("/tile/", "/actual/");
                String url = Constants.NOW_FLOATS_API_URL/*"http://api.withfloats.org"*/ + "/Product/v2/ReplaceImage?" + valuesStr;
                byte[] imageBytes = Methods.compressToByte(path, activity);
                new ProductImageReplace(url, imageBytes, Product_Detail_Activity.this).execute();
            } else {
                uploadProductImage(productId);
            }
        } catch (Exception e) {
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
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
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
        if (!mIsImageChosen && !mIsReplacing) {
            mIsImageChosen = true;
            mProductImageList.clear();
        }
        if (requestCode == com.darsh.multipleimageselect.helpers.Constants.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(com.darsh.multipleimageselect.helpers.Constants.INTENT_EXTRA_IMAGES);
            if (mIsReplacing) {
                String oldUrl = mProductImageList.get(mSelectedPosition).path;
                mProductImageList.get(mSelectedPosition).path = images.get(0).path;
                adapter.notifyDataSetChanged();
                if (!mIsNew && !mIsImageChosen) {
                    replaceProductImage(product_data._id, oldUrl, images.get(0).path);
                }
            } else {
                //mProductImageList.clear();
                for (Image image : images) {
                    mProductImageList.add(image);
                }
                adapter.notifyDataSetChanged();
                if (!mIsNew) {
                    save.setVisibility(View.VISIBLE);
                    mIsNewImageAdded = true;
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
                            setImageFromCamera(path);
                            //productImage.setImageBitmap(CameraBitmap);
                            //if (replaceImage) replaceProductImage(product_data._id);
                        } else {
                            path = getRealPathFromURI(picUri);
                            CameraBitmap = Util.getBitmap(path, activity);
                            setImageFromCamera(path);
                            //productImage.setImageBitmap(CameraBitmap);
                            //if (replaceImage) replaceProductImage(product_data._id);
                        }
                    } else {
                        getString(R.string.try_again);
                    }
                } else {
                    path = getRealPathFromURI(picUri);
                    CameraBitmap = Util.getBitmap(path, activity);
                    setImageFromCamera(path);
                    //productImage.setImageBitmap(CameraBitmap);
                    //if (replaceImage) replaceProductImage(product_data._id);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } catch (OutOfMemoryError E) {
                E.printStackTrace();
                CameraBitmap.recycle();
                System.gc();
                Methods.showSnackBar(activity, getString(R.string.try_again));
            }
        }
    }

    private void setImageFromCamera(String path) {
        if (mIsReplacing) {
            String oldUrl = mProductImageList.get(mSelectedPosition).path;
            mProductImageList.get(mSelectedPosition).path = path;
            adapter.notifyDataSetChanged();
            if (!mIsNew && !mIsImageChosen) {
                replaceProductImage(product_data._id, oldUrl, path);
            }
        } else {
            //mProductImageList.clear();
            mProductImageList.add(new Image(1, "CamImage", path, true));
            adapter.notifyDataSetChanged();
            if (!mIsNew) {
                save.setVisibility(View.VISIBLE);
                mIsNewImageAdded = true;
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = managedQuery(contentUri, proj, null, null, null);
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
        }
        return null;
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
    protected void onResume() {
        super.onResume();
        //bus.register(this);
        registerReceiver(receiver, mIntentFilter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == media_req_id) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                cameraIntent();

            }

        } else if (requestCode == gallery_req_id) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                galleryIntent();

            }

        }
    }

    public void galleryIntent() {
        try {
            if (ActivityCompat.checkSelfPermission(Product_Detail_Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(Product_Detail_Activity.this, Manifest.permission.CAMERA) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Product_Detail_Activity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        gallery_req_id);
            } else {
                Intent intent = new Intent(this, AlbumSelectActivity.class);
                if (mIsReplacing) {
                    intent.putExtra(com.darsh.multipleimageselect.helpers.Constants.INTENT_EXTRA_LIMIT, 1);
                } else {
                    if (mIsImageChosen) {
                        intent.putExtra(com.darsh.multipleimageselect.helpers.Constants.INTENT_EXTRA_LIMIT, 5 - mProductImageList.size());
                    } else {
                        intent.putExtra(com.darsh.multipleimageselect.helpers.Constants.INTENT_EXTRA_LIMIT, 5);
                    }
                }
                startActivityForResult(intent, com.darsh.multipleimageselect.helpers.Constants.REQUEST_CODE);
            }
        } catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = getString(R.string.device_does_not_support_capturing_image);
            Methods.showSnackBarNegative(activity, errorMessage);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //bus.unregister(this);
        unregisterReceiver(receiver);
        if (mIsImageDeleted) {
            invokeGetProductList();
        }
    }

    public void cameraIntent() {
        try {
            // use standard intent to capture an image
            if (ActivityCompat.checkSelfPermission(Product_Detail_Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(Product_Detail_Activity.this, Manifest.permission.CAMERA) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Product_Detail_Activity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        media_req_id);
            } else {
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
            // display an error message
            String errorMessage = getString(R.string.device_does_not_support_capturing_image);
            Methods.showSnackBarNegative(activity, errorMessage);
        }
    }

}
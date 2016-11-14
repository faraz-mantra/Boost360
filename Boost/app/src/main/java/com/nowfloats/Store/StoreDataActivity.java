package com.nowfloats.Store;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.Store.Adapters.ExpandableListAdapter;
import com.nowfloats.Store.Adapters.ItemsRecyclerViewAdapter;
import com.nowfloats.Store.Adapters.PhotoAdapter;
import com.nowfloats.Store.Model.ERPRequestModel;
import com.nowfloats.Store.Model.EnablePackageResponse;
import com.nowfloats.Store.Model.MailModel;
import com.nowfloats.Store.Model.MainMailModel;
import com.nowfloats.Store.Model.MarkAsPaidModel;
import com.nowfloats.Store.Model.OPCModels.OPCDataMain;
import com.nowfloats.Store.Model.PhotoItem;
import com.nowfloats.Store.Model.StoreModel;
import com.nowfloats.Store.Model.WidgetPacks;
import com.nowfloats.Store.Service.IOPCValidation;
import com.nowfloats.Store.Service.StoreInterface;
import com.nowfloats.Store.iapUtils.IabHelper;
import com.nowfloats.Store.iapUtils.IabResult;
import com.nowfloats.Store.iapUtils.Inventory;
import com.nowfloats.Store.iapUtils.Purchase;
import com.nowfloats.signup.UI.Model.Get_FP_Details_Event;
import com.nowfloats.signup.UI.Service.Get_FP_Details_Service;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.TwoWayView;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.romeo.mylibrary.Models.OrderDataModel;
import com.romeo.mylibrary.ui.InstaMojoMainActivity;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.thinksity.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.locks.ReadWriteLock;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by guru on 28-04-2015.
 */

public class StoreDataActivity extends AppCompatActivity {
    ScrollView scrollView;
    public static ExpandableListView expandableList;
    ExpandableListAdapter expandableListAdapter;
    private TwoWayView mRecyclerImageView;
    protected PhotoAdapter mAdapter;
    protected ArrayList<PhotoItem> mPhotoListItem;
    public static StoreModel product;
    IabHelper mHelper;
    public static TextView ProductPrice;
    private String ProductPrice_Value;
    private int IAPRequestCode = 100101;
    String SKU_PACKAGE = "empty",purchased = "Already Purchased";
    private boolean EnablePackBoolean = true;
    private boolean purchaseCheck = false;
    private boolean domainPurchaseFlag = false;
    UserSessionManager sessionManager;
    private String fpID;
//    private String countryPhoneCode;
    private String InAppPrize;
    MaterialDialog materialProgress;
    private Bus bus;
    private TextView product_validity;
    private LinearLayout product_pay;
    private String soureClientId = "";
    private String ClickedValues;
    private String mErpExecutiveMailId;
    private String mErpId;
    private String mOPC;
    private OrderDataModel mOrderData;

    private final int DIRECT_REQUEST_CODE = 1;
    private final int OPC_REQUEST_CODE = 2;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_data);

        Toolbar toolbar = (Toolbar) findViewById(R.id.store_data_action_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        materialProgress = new MaterialDialog.Builder(this)
                .widgetColorRes(R.color.accentColor)
                .content("Please Wait...")
                .progress(true, 0)
                .show();
        bus = BusProvider.getInstance().getBus();
        sessionManager = new UserSessionManager(this,StoreDataActivity.this);
        soureClientId = sessionManager.getSourceClientId();

//        countryPhoneCode = sessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRYPHONECODE);
        fpID = sessionManager.getFPID();
        try{
            scrollView = (ScrollView)findViewById(R.id.scrollView);
            if (getIntent().hasExtra("key")){
                final int StorePosition = Integer.parseInt(getIntent().getExtras().getString("key"));
                product_pay = (LinearLayout) findViewById(R.id.product_payment);
                if(getIntent().getExtras().getString("type").equals("all")){
                    product = StoreFragmentTab.additionalWidgetModels.get(StorePosition);
                }else{
                    product = StoreFragmentTab.activeWidgetModels.get(StorePosition);
                    product_pay.setVisibility(View.GONE);
                }
                //Title
                TextView titleTextView = (TextView) toolbar.findViewById(R.id.store_title);
                titleTextView.setText(product.Name);
                MixPanelController.track("NFStore_"+product.Name, null);
                //Price
                ProductPrice = (TextView) findViewById(R.id.product_price);
                product_validity = (TextView) findViewById(R.id.product_validity);

//                if (("91").equals(countryPhoneCode)){
                if (/*product.ExternalApplicationDetails==null || product.ExternalApplicationDetails.equals("null")
                        || product.ExternalApplicationDetails.size()==0*/!sessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY).equalsIgnoreCase("India")){
                    materialProgress.dismiss();
                    ProductPrice.setText(getString(R.string.interest));
                    product_validity.setVisibility(View.GONE);
                    product_pay.setBackgroundColor(getResources().getColor(R.color.primaryColor));

                    ClickedValues = sessionManager.getLocalStorePurchase();
                    if (ClickedValues.contains(product.Name)) {
//                        if (sessionManager.getLightHousePurchase()){
                            ProductPrice.setText(getString(R.string.already_requested));
                            product_pay.setBackgroundColor(getResources().getColor(R.color.greenDark));
//                        }
                    }
//                    else if (product.Name.equals("NowFloats WildFire")) {
//                        if (sessionManager.getWildFirePurchase()){ProductPrice.setText("Already requested");
//                            product_pay.setBackgroundColor(getResources().getColor(R.color.greenDark));}
//                    }
                    try {
                        if (product.Name.equalsIgnoreCase("NowFloats Dictate") || product.Name.equalsIgnoreCase("NowFloats WildFire")) {
                            ProductPrice.setText(getString(R.string.contact_us));
                        }
                    }catch(Exception e){e.printStackTrace();}

                    product_pay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(ProductPrice.getText().toString().equalsIgnoreCase("Contact Us")){
                                try {
                                    StoreInterface anInterface = Constants.restAdapter.create(StoreInterface.class);
                                    ArrayList<String> emailList = new ArrayList<String>();
                                    emailList.add("leads@nowfloats.com");

                                    String tag = sessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG).toString().toUpperCase();
                                    String subj = "Request to activate "+product.Name +" "+ tag+ " site";
                                    String mailMsg = "Tag: "+tag+"<br>Package Name: "+product.Name+"<br>Package Id: "+product._id
                                            +"<br>Account Manager Id: "+ soureClientId;
                                    anInterface.mail(new MailModel(soureClientId, mailMsg,subj, emailList),
                                            new Callback<String>() {
                                                @Override
                                                public void success(String s, Response response) {
                                                    ProductPrice.setText(getString(R.string.already_requested));
                                                    product_pay.setBackgroundColor(getResources().getColor(R.color.greenDark));
                                                    new MaterialDialog.Builder(StoreDataActivity.this)
                                                            .title(getString(R.string.thank_you_for_your_interest))
                                                            .content(getString(R.string.our_team_contact_in_48hours))
                                                            .negativeText(getString(R.string.ok))
                                                            .negativeColorRes(R.color.light_gray)
                                                            .callback(new MaterialDialog.ButtonCallback() {
                                                                @Override
                                                                public void onNegative(MaterialDialog dialog) {
                                                                    super.onNegative(dialog);
                                                                    dialog.dismiss();
                                                                }
                                                            }).show();
                                                }

                                                @Override
                                                public void failure(RetrofitError error) {
                                                    Log.d("Mail", "" + error.getMessage());
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Methods.showSnackBarNegative(StoreDataActivity.this, getString(R.string.try_again));
                                                        }
                                                    });
                                                }
                                            });
                                }catch(Exception e){e.printStackTrace();}
                            }else {
//                                boolean storeClick = false;
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("clientId", soureClientId);
                                params.put("plantype", product.Name);
//                                if (product.Name.equals("NowFloats Lighthouse")) {params.put("plantype", "Lighthouse");
//                                    storeClick = sessionManager.getLightHousePurchase();
//                                    sessionManager.setLightHousePurchase(true);
//                                }
//                                else if (product.Name.equals("NowFloats WildFire")) {params.put("plantype", "WildFire");
//                                    storeClick = sessionManager.getWildFirePurchase();
//                                    sessionManager.setWildFirePurchase(true);
//                                }
//                            if (!storeClick){
                                if (!ClickedValues.contains(product.Name)) {
                                    ClickedValues += "#" + product.Name;
                                    sessionManager.setLocalStorePurchase(ClickedValues);

                                    StoreInterface storeInterface = Constants.restAdapter.create(StoreInterface.class);
                                    storeInterface.requestWidget(fpID, params, new Callback<String>() {
                                        @Override
                                        public void success(String s, Response response) {
                                            ProductPrice.setText(getString(R.string.already_requested));
                                            product_pay.setBackgroundColor(getResources().getColor(R.color.greenDark));

                                            new MaterialDialog.Builder(StoreDataActivity.this)
                                                    .title(getString(R.string.thank_you_for_your_interest))
                                                    .content(getString(R.string.our_team_contact_in_48hours))
                                                    .negativeText(getString(R.string.ok))
                                                    .negativeColorRes(R.color.light_gray)
                                                    .callback(new MaterialDialog.ButtonCallback() {
                                                        @Override
                                                        public void onNegative(MaterialDialog dialog) {
                                                            super.onNegative(dialog);
                                                            dialog.dismiss();
                                                        }
                                                    }).show();
                                        }

                                        @Override
                                        public void failure(RetrofitError error) {
                                            Methods.showSnackBarNegative(StoreDataActivity.this, getString(R.string.something_went_wrong_try_again));
                                        }
                                    });
                                } else {
                                    new MaterialDialog.Builder(StoreDataActivity.this)
                                            .title(getString(R.string.processing_request))
                                            .content(getString(R.string.processing_request_we_back_in_48hours))
                                            .negativeText(getString(R.string.ok))
                                            .negativeColorRes(R.color.light_gray)
                                            .callback(new MaterialDialog.ButtonCallback() {
                                                @Override
                                                public void onNegative(MaterialDialog dialog) {
                                                    super.onNegative(dialog);
                                                    dialog.dismiss();
                                                }
                                            }).show();
                                }
                            }
                        }
                    });
                } else {

                    materialProgress.dismiss();
                    product_validity.setVisibility(View.GONE);
                    ProductPrice.setVisibility(View.VISIBLE);
                    ProductPrice.setText(getString(R.string.buy_now));
                    product_pay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openPaymentTypeDialog();
                            MixPanelController.track(EventKeysWL.BUY_NOW_STORE_CLICKED, null);
                        }
                    });
                    /*if (product.ExternalApplicationDetails!=null && product.ExternalApplicationDetails.size()>0){
                        for (int i = 0; i < product.ExternalApplicationDetails.size(); i++) {
                            if (product.ExternalApplicationDetails.get(i).Type.equals("1")){
                                SKU_PACKAGE = product.ExternalApplicationDetails.get(i).ExternalSourceId;
                                Log.i("SKU---",""+SKU_PACKAGE);
                                break;
                            }
                        }
                    }

//                    ProductPrice_Value = "Pay " + product.CurrencyCode + " " + product.Price;
//TODO remove this line after testing
//                    SKU_PACKAGE = "android.test.purchased";
                    try {
                        if (product.Name.equalsIgnoreCase("NowFloats Dictate") || product.Name.equalsIgnoreCase("NowFloats WildFire")) {
                            ProductPrice.setText(getString(R.string.contact_us));
                        }
                    }catch(Exception e){e.printStackTrace();}
                    if(!(ProductPrice.getText().toString().equalsIgnoreCase("Contact Us"))){
                        product_validity.setVisibility(View.VISIBLE);
                        getInAppProductPrize();
                    }

                    product_pay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(ProductPrice.getText().toString().equalsIgnoreCase("Contact Us")){
                                try {
                                    StoreInterface anInterface = Constants.restAdapter.create(StoreInterface.class);
                                    ArrayList<String> emailList = new ArrayList<String>();
                                    emailList.add("leads@nowfloats.com");
                                    String tag = sessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG).toString().toUpperCase();
                                    String subj = "Request to activate "+product.Name +" "+ tag+ " site";
                                    String mailMsg = "Tag: "+tag+"<br>Package Name: "+product.Name+"<br>Package Id: "+product._id
                                            +"<br>Account Manager Id: "+ soureClientId;
                                    anInterface.mail(new MailModel(soureClientId, mailMsg,subj, emailList),
                                            new Callback<String>() {
                                                @Override
                                                public void success(String s, Response response) {
                                                    ProductPrice.setText(getString(R.string.already_requested));
                                                    product_pay.setBackgroundColor(getResources().getColor(R.color.greenDark));
                                                    new MaterialDialog.Builder(StoreDataActivity.this)
                                                            .title(getString(R.string.thank_you_for_your_interest))
                                                            .content(getString(R.string.our_team_contact_in_48hours))
                                                            .negativeText(getString(R.string.ok))
                                                            .negativeColorRes(R.color.light_gray)
                                                            .callback(new MaterialDialog.ButtonCallback() {
                                                                @Override
                                                                public void onNegative(MaterialDialog dialog) {
                                                                    super.onNegative(dialog);
                                                                    dialog.dismiss();
                                                                }
                                                            }).show();
                                                }

                                                @Override
                                                public void failure(RetrofitError error) {
                                                    Log.d("Mail", "" + error.getMessage());
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Methods.showSnackBarNegative(StoreDataActivity.this, getString(R.string.try_again));
                                                        }
                                                    });
                                                }
                                            });
                                }catch(Exception e){e.printStackTrace();}
                            }else {
                                if (!SKU_PACKAGE.equals("empty")) {
                                    if (Constants.StorePackageIds != null && Constants.StorePackageIds.size() > 0) {
                                    *//*for (int i = 0; i < Constants.StorePackageIds.size(); i++) {
                                        if (!product._id.equals(Constants.StorePackageIds.get(i))){
                                            purchaseCheck = true;
                                            break;
                                        }
                                    }*//*
                                        if (Constants.StorePackageIds.contains(product._id)) {
                                            purchaseCheck = false;
                                        } else {
                                            purchaseCheck = true;
                                        }
                                    } else {
                                        purchaseCheck = true;
                                    }
                                    //TODO remove this line after testing
//                                purchaseCheck = true; SKU_PACKAGE = "android.test.purchased";
                                    if (Methods.isOnline(StoreDataActivity.this) && purchaseCheck) {
                                        purchaseCheck = false;
                                        launchIAPurchaseFlow();
                                    }
                                } else {
                                    new MaterialDialog.Builder(StoreDataActivity.this)
                                            .title(getString(R.string.unable_to_process_purchase))
                                            .content(getString(R.string.reach_out_ria))
                                            .negativeText(getString(R.string.close))
                                            .positiveColorRes(R.color.primaryColor)
                                            .negativeColorRes(R.color.light_gray)
                                            .callback(new MaterialDialog.ButtonCallback() {
                                                @Override
                                                public void onNegative(MaterialDialog dialog) {
                                                    super.onNegative(dialog);
                                                    dialog.dismiss();
                                                }
                                            })
                                            .show();
                                }
                            }
                        }
                    });*/
                }

                //Product description
                TextView storeProductDesc = (TextView)findViewById(R.id.storeProductDesc);
                storeProductDesc.setText(product.Desc);

                // Set up screen shots picture Adapter
                mRecyclerImageView = (TwoWayView) findViewById(R.id.imageList);
                LinearLayout emptyScreen = (LinearLayout)findViewById(R.id.emptyscreenslayout);
                if (product.Screenshots==null || product.Screenshots.size()==0){
                    emptyScreen.setVisibility(View.VISIBLE);
                }else {
                    emptyScreen.setVisibility(View.GONE);
                }
                mPhotoListItem = new ArrayList<PhotoItem>() ;
                for (int i = 0; i < product.Screenshots.size(); i++) {
                    String urlStr = product.Screenshots.get(i).imageUri;
                    if(urlStr!=null && urlStr.length()>0 && !urlStr.equals("null")) {
                        if (!urlStr.contains("http")) {
                            urlStr = Constants.NOW_FLOATS_API_URL + product.Screenshots.get(i).imageUri;
                        }
                        Uri uri = Uri.parse(urlStr);
                        mPhotoListItem.add(new PhotoItem(uri));
                    }
                }

                mAdapter = new PhotoAdapter(StoreDataActivity.this, R.layout.photo_item, mPhotoListItem, false);
                if (mAdapter.getCount()>0){
                    mRecyclerImageView.setAdapter(mAdapter);
                    mRecyclerImageView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(StoreDataActivity.this,ScreenshotZoomActivity.class);
                            intent.putExtra("key",StorePosition+"");
                            intent.putExtra("imagePos",position+"");
                            Methods.launch(StoreDataActivity.this,view,intent);
                        }
                    });
                }

                //Product details list setup
                expandableList = (ExpandableListView) findViewById(R.id.expandableListView);
                LinearLayout emptyFeature = (LinearLayout)findViewById(R.id.emptyfeatureslayout);
                LinearLayout expLayout = (LinearLayout)findViewById(R.id.exp_layout);
                if (product.WidgetPacks==null || product.WidgetPacks.size()==0){
                    expLayout.setVisibility(View.GONE);
                    emptyFeature.setVisibility(View.VISIBLE);
                }else {
                    expLayout.setVisibility(View.VISIBLE);
                    emptyFeature.setVisibility(View.GONE);
                }
                prepareListData(product.WidgetPacks);

                expandableList.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
                    @Override
                    public void onGroupCollapse(int groupPosition) {
                    if (!(expandableListAdapter.getChildrenCount(groupPosition) == 0)) {
                        Methods.setListViewHeightBasedOnChildren(expandableList);
                    }
                    }
                });

                expandableList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                    @Override
                    public void onGroupExpand(int groupPosition) {
                        if (!(expandableListAdapter.getChildrenCount(groupPosition) == 0)) {
                            Methods.setListViewHeightBasedOnChildren(expandableList);
                        }
                    }
                });
            }
        }catch(Exception e){e.printStackTrace();}
    }

    private void openPaymentTypeDialog() {
        final String[] array = {"Pay Now", "Proceed with OPC"};
        MaterialDialog dialog = new MaterialDialog.Builder(StoreDataActivity.this)
                .title("Please select one to proceed")
                .items(array)
                .negativeText(getString(R.string.cancel))
                .cancelable(false)
                .positiveText(getString(R.string.ok))
                .negativeColorRes(R.color.light_gray)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        dialog.dismiss();
                    }

                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        int position = dialog.getSelectedIndex();
                        if (position == 0) {
                            mErpExecutiveMailId = null;
                            mOPC = null;
                            mErpId = null;
                            Intent i = new Intent(StoreDataActivity.this, InstaMojoMainActivity.class);
                            mOrderData = new OrderDataModel(sessionManager.getFpTag(), sessionManager.getFpTag(),
                                    sessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL),
                                    "10", product.Name,
                                    sessionManager.getFPDetails(Key_Preferences.MAIN_PRIMARY_CONTACT_NUM),
                                    "Light House", product.CurrencyCode);
                            i.putExtra(com.romeo.mylibrary.Constants.PARCEL_IDENTIFIER, mOrderData);
                            startActivityForResult(i, DIRECT_REQUEST_CODE);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }else if (position == 1) {
                            openOPCDialog();
                        }else{
                            Toast.makeText(StoreDataActivity.this, "Please select of the options", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                })
                .widgetColorRes(R.color.primaryColor)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int position, CharSequence text) {
                        return true;
                    }
                }).build();
        dialog.getWindow().getAttributes().windowAnimations =  R.style.DialogTheme;
        dialog.show();

    }

    private void openOPCDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.store_buy_now_dialog_layout);
        dialog.setCancelable(false);
        final Button btnOpcConfirmYes = (Button) dialog.findViewById(R.id.btn_opc_confirm__yes);
        final Button btnOpcConfirmNo = (Button) dialog.findViewById(R.id.btn_opc_confirm_no);
        final EditText etOpcCode = (EditText) dialog.findViewById(R.id.et_opc_code);
        final TextInputLayout opcInputlayout = (TextInputLayout) dialog.findViewById(R.id.opcInputLayout);
        opcInputlayout.setHintEnabled(true);
        opcInputlayout.setErrorEnabled(true);
        opcInputlayout.setHint(getString(R.string.opc_edit_text_hint));

        btnOpcConfirmNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                openPaymentTypeDialog();
            }
        });
        btnOpcConfirmYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyPaymentToken(etOpcCode.getText().toString().trim(), opcInputlayout, dialog);
            }
        });
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        dialog.show();

    }

    private void verifyPaymentToken(String OPCCode, final TextInputLayout layout, final Dialog mainDialog) {
        HashMap<String, String> data = new HashMap<>();
        data.put("clientId", Constants.clientId);
        data.put("token", OPCCode);
        data.put("fpId", sessionManager.getFPID());
        final ProgressDialog pd = ProgressDialog.show(this, "", getString(R.string.verify_opc));
        Constants.restAdapter.create(IOPCValidation.class)
                .verifyPaymentToken(data, new Callback<OPCDataMain>() {
                    @Override
                    public void success(OPCDataMain opcDataMain, Response response) {
                        if(pd!=null && pd.isShowing()){
                            pd.dismiss();
                        }
                        if(opcDataMain.success){
                            mainDialog.dismiss();
                            showInvoiceDialog(opcDataMain);
                        }else {
                            if(opcDataMain.reason!=null){
                                layout.setError(opcDataMain.reason);
                                //Methods.showSnackBarNegative(StoreDataActivity.this, opcDataMain.reason);
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if(pd!=null && pd.isShowing()){
                            pd.dismiss();
                        }
                        mainDialog.dismiss();
                        Methods.showSnackBarNegative(StoreDataActivity.this, getString(R.string.error_verifying_opc));
                    }
                });
    }

    private void showInvoiceDialog(final OPCDataMain opcDataMain) {
        if(opcDataMain==null){
            return;
        }
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.invoice_dialog_layout);

        TextView tvUserName = (TextView) dialog.findViewById(R.id.tv_username);
        TextView tvUserEmail = (TextView) dialog.findViewById(R.id.tv_user_email);
        TextView tvPhoneNumber = (TextView) dialog.findViewById(R.id.tv_user_phone_no);
        TextView tvNetTotal = (TextView) dialog.findViewById(R.id.tv_net_total);
        TextView tvTaxes = (TextView) dialog.findViewById(R.id.tv_taxes);
        TextView tvAmountToBePaid = (TextView) dialog.findViewById(R.id.tv_amount_to_be_paid);

        tvUserName.setText(sessionManager.getFpTag().toLowerCase());
        tvUserEmail.setText(sessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL));
        tvPhoneNumber.setText(sessionManager.getFPDetails(Key_Preferences.MAIN_PRIMARY_CONTACT_NUM));
        tvNetTotal.setText(product.CurrencyCode + " " + opcDataMain.response.Invoice.NetAmount + " /-");
        double tax = 0;
        StringBuilder details = new StringBuilder("");
        BoostLog.d("Taxes:", opcDataMain.response.Invoice.Taxes.toString());
        String taxes = opcDataMain.response.Invoice.Taxes.toString().substring(1, opcDataMain.response.Invoice.Taxes.toString().length()-1);
        String taxData[] = taxes.split(", ");
        for(int i=0; i<taxData.length; i++){
            details.append(taxData[i].split("=")[0] + " +\n");
            tax+=Double.parseDouble(taxData[i].split("=")[1]);
        }
        tvTaxes.setText(product.CurrencyCode + " " + (opcDataMain.response.Invoice.NetAmount*tax)/100 + " /-\n" + "( " + details.substring(0, details.length()-3)+ " )");
        tvAmountToBePaid.setText(product.CurrencyCode + " " + opcDataMain.response.Invoice.TotalAmount + " /-");
        String packages="";
        for(int i=0; i<opcDataMain.response.Invoice.Items.size(); i++){
            packages+=opcDataMain.response.Invoice.Items.get(i).packagename + " and ";
        }
        final String newPackage = packages;

        dialog.findViewById(R.id.btn_pay_now).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mErpExecutiveMailId = opcDataMain.response.CreatedBy;
                mErpId = opcDataMain.response.ERPId;
                mOPC = opcDataMain.response.Token;
                Intent i = new Intent(StoreDataActivity.this, InstaMojoMainActivity.class);
                mOrderData = new OrderDataModel(sessionManager.getFpTag(), sessionManager.getFpTag(),
                        sessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL),
                        "10", newPackage.substring(0, newPackage.length()-5),
                        sessionManager.getFPDetails(Key_Preferences.MAIN_PRIMARY_CONTACT_NUM),
                        "Light House", product.CurrencyCode);
                i.putExtra(com.romeo.mylibrary.Constants.PARCEL_IDENTIFIER, mOrderData);
                startActivityForResult(i, OPC_REQUEST_CODE);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });


        RecyclerView rvItems = (RecyclerView) dialog.findViewById(R.id.rv_store_items);
        rvItems.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        ItemsRecyclerViewAdapter adapter = new ItemsRecyclerViewAdapter(opcDataMain.response.Invoice.Items, product.CurrencyCode);
        rvItems.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        dialog.show();
    }

    private String getInAppProductPrize(){
        try{
            //play store public license key
            final String base64EncodedPublicKey = Constants.license_key;
            // compute your public key and store it in base64EncodedPublicKey
            mHelper = new IabHelper(this, base64EncodedPublicKey);
            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                public void onIabSetupFinished(IabResult result) {
                    if (!result.isSuccess()) {
                        Methods.showSnackBarNegative(StoreDataActivity.this, getString(R.string.looks_an_issue_with_app));
                        materialProgress.dismiss();
                    }else {
                        MixPanelController.track(EventKeysWL.STORE_IN_APP_PURCHASE_INITIATION, null);
                        //Check product
                        final ArrayList<String> additionalSkuList = new ArrayList<>();
                        additionalSkuList.add(SKU_PACKAGE);
                        mHelper.queryInventoryAsync(true, additionalSkuList, new IabHelper.QueryInventoryFinishedListener() {
                            @Override
                            public void onQueryInventoryFinished(IabResult result, final Inventory inv) {
                                try {
                                    if (inv!=null && inv.hasPurchase(SKU_PACKAGE)){
                                        mHelper.consumeAsync(inv.getPurchase(SKU_PACKAGE),new IabHelper.OnConsumeFinishedListener() {
                                            @Override
                                            public void onConsumeFinished(Purchase purchase, IabResult r) {
                                                if (r.isSuccess()) Log.i("STORE Consume Success", "");
                                            }
                                        });
                                        Methods.showSnackBarPositive(StoreDataActivity.this,getString(R.string.pack_is_already_purchased));
                                        ProductPrice.setText(purchased);
                                        InAppPrize = purchased;
                                        setPrice(InAppPrize);
                                        materialProgress.dismiss();
                                    }else{
                                        if (result.isFailure()) {
                                            // consume the product and update the UI
                                            if (Constants.lastPurchase!=null){
                                                mHelper.consumeAsync(Constants.lastPurchase,new IabHelper.OnConsumeFinishedListener() {
                                                    @Override
                                                    public void onConsumeFinished(Purchase purchase, IabResult result) {
                                                        if (result.isSuccess()) Log.i("STORE Consume Success", "");
                                                    }
                                                });
                                            }else{
                                                if (inv!=null){
                                                    if (inv.getPurchase(SKU_PACKAGE)!=null){
                                                        mHelper.consumeAsync(inv.getPurchase(SKU_PACKAGE),new IabHelper.OnConsumeFinishedListener() {
                                                            @Override
                                                            public void onConsumeFinished(Purchase purchase, IabResult result) {
                                                                if (result.isSuccess()) Log.i("STORE Consume Success", "");
                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                            materialProgress.dismiss();
                                            Methods.showSnackBarNegative(StoreDataActivity.this, getString(R.string.failed_to_retrive_detail_try) );
                                            return;
                                        }else{
                                            InAppPrize = inv.getSkuDetails(SKU_PACKAGE).getPrice();
                                            ProductPrice.setText(getString(R.string.pay) +InAppPrize);
                                            setPrice(InAppPrize);
                                            materialProgress.dismiss();
                                        }
                                    }
                                }catch (Exception e){e.printStackTrace();materialProgress.dismiss();}
                            }
                        });
                    }
                }
            });
        }catch (Exception e){e.printStackTrace();materialProgress.dismiss();}
        return InAppPrize;
    }

    private void setPrice(String ProductPrice_Value){
        if (ProductPrice_Value!=null){
           ProductPrice.setText(ProductPrice_Value);
           product_validity.setVisibility(View.VISIBLE);
           product_pay.setBackgroundColor(getResources().getColor(R.color.primaryColor));
        }
        /*if (Constants.StoreWidgets!=null &&  Constants.StoreWidgets.size()>0){
            for (int i = 0; i < product.WidgetPacks.size(); i++) {
                if (!Constants.StoreWidgets.contains(product.WidgetPacks.get(i).WidgetKey)){
                    purchaseCheck = true;
                    break;
                }
            }
        }else{
            purchaseCheck = true;
        }*/
        if (Constants.StorePackageIds !=null &&  Constants.StorePackageIds.size()>0){
            if(Constants.StorePackageIds.contains(product._id)){
                purchaseCheck = false;
            }else{
                purchaseCheck = true;
            }
        }else{
            purchaseCheck = true;
        }
        if (!purchaseCheck){
            ProductPrice.setText(purchased);
            product_validity.setVisibility(View.GONE);
            product_pay.setBackgroundColor(getResources().getColor(R.color.greenDark));
        }
    }

    private void launchIAPurchaseFlow() {
        try{
            String uniqueKey = "emptyTagkey",tagName = sessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG);
            if (tagName!=null && tagName.trim().length()>0){
                uniqueKey = tagName;
            }
            if(mHelper!=null){
                //Purchase
                mHelper.launchPurchaseFlow(StoreDataActivity.this, SKU_PACKAGE, IAPRequestCode,
                        new IabHelper.OnIabPurchaseFinishedListener() {
                            @Override
                            public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
                                if (result.isFailure()) {
                                    MixPanelController.track(EventKeysWL.STORE_IN_APP_PURCHASE_FAILURE, null);
                                    Methods.showSnackBarNegative(StoreDataActivity.this, getString(R.string.purchase_failed_try));
                                    return;
                                }else if(result.isSuccess()){
                                    Constants.lastPurchase = purchase;
                                    InAppPurchaseSuccessResponse();
                                }
                            }
                        }, uniqueKey);
            }
        }catch (Exception e){e.printStackTrace();}
    }

    private void InAppPurchaseSuccessResponse() {
        if (EnablePackBoolean) {
            MixPanelController.track(EventKeysWL.STORE_IN_APP_PURCHASE_SUCCESS, null);
            EnablePackBoolean = false;
            ProductPrice.setText(purchased);
            EnablePacks();

            mHelper.queryInventoryAsync(new IabHelper.QueryInventoryFinishedListener() {
                @Override
                public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                    // consume the product and update the UI
                    if (inv.hasPurchase(SKU_PACKAGE)) {
                        mHelper.consumeAsync(inv.getPurchase(SKU_PACKAGE), new IabHelper.OnConsumeFinishedListener() {
                            @Override
                            public void onConsumeFinished(Purchase purchase, IabResult result) {
                                if (result.isSuccess()) {
                                    Log.i("STORE Consume Success", "");
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    private void EnablePacks() {
        try {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("applicationPackageId", product._id);
            params.put("fpId", fpID);
            params.put("applicationId", soureClientId);
            params.put("currencyCode", product.CurrencyCode);
            params.put("packageValidityInMths", product.ExpiryInMths);
            params.put("amountPaid", product.Price);

            StoreInterface storeInterface = Constants.restAdapter.create(StoreInterface.class);
            storeInterface.enableWidgetPack(params, new Callback<EnablePackageResponse>() {
                @Override
                public void success(EnablePackageResponse result, Response response) {
                    EnablePackBoolean = true;
                    if(ProductPrice!=null)
                        ProductPrice.setText(purchased);
                    Methods.showSnackBarPositive(StoreDataActivity.this, getString(R.string.success_pack_is_active));
                    if(result.Result.equals("true") && result.StatusCode.equals("200")){
                        try{
                            MixPanelController.track(EventKeysWL.STORE_IN_APP_PURCHASE_PACKAGE_ACTIVATION, null);
                            new Get_FP_Details_Service(StoreDataActivity.this,fpID,Constants.clientId,bus);
                        }catch(Exception e){e.printStackTrace();}
                    }else if(result.StatusCode.equals("500")){
                        try{
                            new Get_FP_Details_Service(StoreDataActivity.this,fpID,Constants.clientId,bus);
                        }catch(Exception e){e.printStackTrace();}
                    }
                    domainPurchase();
                }

                @Override
                public void failure(RetrofitError error) {
                    EnablePackBoolean = true;
                    Methods.showSnackBarPositive(StoreDataActivity.this, getString(R.string.something_went_wrong_try_again));
                }
            });
        }catch(Exception e){e.printStackTrace(); EnablePackBoolean = true;}
    }

    private void domainPurchase() {
        if(domainPurchaseFlag){
            Intent intent = new Intent(StoreDataActivity.this,DomainLookup.class);
            intent.putExtra("key",purchased);
            startActivity(intent);
        }
    }

    private void prepareListData(ArrayList<WidgetPacks> widgetPacks) {
        try {
            List<String> listDataHeader = new ArrayList<String>();
            HashMap<String, ArrayList<String>> infoHashMap = new HashMap<String, ArrayList<String>>();
            // Adding data
            for (int i = 0; i < widgetPacks.size(); i++) {
                String head = getString(R.string.dot) + " " + widgetPacks.get(i).Name;
                listDataHeader.add(head);
                String desc = widgetPacks.get(i).Desc;
                if (desc!=null && !(desc.trim().equals(""))){
                    ArrayList<String> list = new ArrayList<String>();
                    list.add(desc+"#"+widgetPacks.get(i).WidgetKey);
                    infoHashMap.put(head, list);
                }
            }
            expandableListAdapter = new ExpandableListAdapter(StoreDataActivity.this, listDataHeader, infoHashMap);
            expandableList.setAdapter(expandableListAdapter);
            for (int i = 0; i < widgetPacks.size(); i++) {
                if (widgetPacks.get(i).WidgetKey.equals("DOMAINPURCHASE")) {
                    expandableList.expandGroup(i);
                    domainPurchaseFlag = true;
                }
            }
            Methods.setListViewHeightBasedOnChildren(expandableList);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_store, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id==android.R.id.home ){
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        /*Log.d("Start", "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d("ELSE", "onActivityResult handled by IABUtil.");
            if (resultCode==RESULT_OK){
                if (requestCode==IAPRequestCode){
                    InAppPurchaseSuccessResponse();
                }
            }
        }*/
        if(requestCode==DIRECT_REQUEST_CODE || requestCode==OPC_REQUEST_CODE && resultCode==RESULT_OK){
            if(data==null){
                return;
            }
            final boolean success = data.getBooleanExtra(com.romeo.mylibrary.Constants.RESULT_SUCCESS_KEY, false);
            final String status = data.getStringExtra(com.romeo.mylibrary.Constants.RESULT_STATUS);
            final String message = data.getStringExtra(com.romeo.mylibrary.Constants.ERROR_MESSAGE);
            final String paymentId = data.getStringExtra(com.romeo.mylibrary.Constants.PAYMENT_ID);
            final String transactionId = data.getStringExtra(com.romeo.mylibrary.Constants.TRANSACTION_ID);
            final String amount = data.getStringExtra(com.romeo.mylibrary.Constants.FINAL_AMOUNT);
            sendEmail(success, status, message, paymentId, transactionId, amount);
            if(success) {
                if(status.equals("Success")) {

                    MixPanelController.track(EventKeysWL.PAYMENT_SUCCESSFULL, null);

                    String msg = "Thank you for choosing a NowFloats Plan. You will be getting invoice details on your registered email id.\nThis is the Payment " +
                            "ID for your transaction: <b>" +
                            paymentId + "</b>.  Please share this Payment ID with the sales executive or the customer support team to activate your package. " +
                            "Please save this Payment ID for future reference. Once your package is activated, key features like Business Enquiries, " +
                            "Product Catalogue and Custom Pages will be unlocked.\nYou can reach customer support at <u>ria@nowfloats.com</u> or 1860-123-1233.";
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
                    builder.setTitle(status).setMessage(Html.fromHtml(msg)).setCancelable(false).setPositiveButton("Activate", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if(requestCode==DIRECT_REQUEST_CODE) {
                                markAsPaid(amount);
                            }
                        }
                    });
                    if(requestCode==OPC_REQUEST_CODE){
                        redeemOPC(paymentId, mOPC);
                    }
                    builder.create().show();
                }
            }else {
                if(status.equals("Pending")){
                    String msg = "Alert! Your payment is pending. Don't worry, it did not fail. It will soon go through and you will be getting invoice details on " +
                            "your registered email id.\nOnce that happens, please share this Payment ID: <b>" + paymentId + "</b> with the sales executive or the " +
                            "customer support team to activate your package. Please save this Payment ID for future reference. Once your package is activated, key " +
                            "features like Business Enquiries, Product Catalogue and Custom Pages will be unlocked.\nYou can reach customer support at <u>ria@nowfloats.com</u> " +
                            "or 1860-123-1233.";
                    showDialog(status, msg);
                }else if (status.equals("Failure")){
                    String msg = "Sorry! This transaction failed. Your payment did not go through. You will be getting transaction details on your registered email id.\nTo " +
                            "retry, please go to Business Profile and pay again.\nIn case of any query, you can reach customer support at <u>ria@nowfloats.com</u> or 1860-123-1233.";
                    showDialog(status, msg);
                }
            }
        }
    }

    private void redeemOPC(String paymentId, String opc) {
        HashMap<String, String> map = new HashMap<>();
        map.put("clientId", Constants.clientId);
        map.put("token", opc);
        map.put("paymentId", paymentId);
        IOPCValidation redeemTokenInterface = Constants.restAdapter.create(IOPCValidation.class);
        redeemTokenInterface.redeemToken(map, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                BoostLog.d("StoreDataActivity:", "OPC REEDEEM RESPONSE:" + s);

                if(s.equalsIgnoreCase("True")){
                    BoostLog.d("StoreDataActivity", "OPC REEDEMED");
                }else {
                    BoostLog.d("StoreDataActivity", "OPC REEDEMING ERROR");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                BoostLog.d("StoreDataActivity", "OPC REEDEMING ERROR");
            }
        });
    }

    private void sendEmail(boolean success, String status, String message, String paymentId, String transactionId, String amount) {
        DateFormat df = DateFormat.getTimeInstance();
        df.setTimeZone(TimeZone.getTimeZone("gmt"));
        String gmtTime = df.format(new Date());
        ArrayList<String> to = new ArrayList<>();
        to.add("opsdesk@nowfloats.com");
        to.add("findesk@nowfloats.com");
        String packageName = "N/A";
        if(mErpId==null){
            mErpId = "N/A";
        }
        if(mOPC==null){
            mOPC = "N/A";
        }
        if(mErpExecutiveMailId==null){
            mErpExecutiveMailId = "N/A";
        }else {
            to.add(mErpExecutiveMailId);
        }
        if(mOrderData!=null){
            packageName = mOrderData.getExpires();
        }

        String mailBody = String.format("<html><body><div id=\"mail\" style=\"display: none;\">\n" +
                "   <table style=\"font-family: monospace\" class=\"table message-table message\">\n" +
                "       <h3>Information regrading payment</h3>\n" +
                "       <tbody>\n" +
                "           <tr>\n" +
                "               <td>Name</td>\n" +
                "               <td>%s</td>\n" +
                "           </tr>\n" +
                "           <tr>\n" +
                "               <td>FP Tag</td>\n" +
                "               <td><a href=\"%s\">%s</a></td>\n" +
                "           </tr>\n" +
                "           <tr>\n" +
                "               <td>Status</td>\n" +
                "               <td>%s</td>\n" +
                "           </tr>\n" +
                "           <tr>\n" +
                "               <td>Payment Id</td>\n" +
                "               <td>%s</td>\n" +
                "           </tr>\n" +
                "           <tr>\n" +
                "               <td>Request Id</td>\n" +
                "               <td>%s</td>\n" +
                "           </tr>\n" +
                "           <tr>\n" +
                "               <td>Payment Date</td>\n" +
                "               <td>%s</td>\n" +
                "           </tr>\n" +
                "           <tr>\n" +
                "               <td>ERP ID</td>\n" +
                "               <td>%s</td>\n" +
                "           </tr>\n" +
                "           <tr>\n" +
                "               <td>Token</td>\n" +
                "               <td>%s</td>\n" +
                "           </tr>\n" +
                "           <tr>\n" +
                "               <td><span title=\"token\">OPC </span>Created By</td>\n" +
                "               <td>%s</td>\n" +
                "           </tr>\n" +
                "           <tr>\n" +
                "               <td>Amounts</td>\n" +
                "               <td>%s</td>\n" +
                "           </tr>\n" +
                "           <tr>\n" +
                "               <td>Type of Plan</td>\n" +
                "               <td>%s</td>\n" +
                "           </tr>\n" +
                "       </tbody>\n" +
                "   </table>\n" +
                "</div></body></html>", sessionManager.getFpTag(), "http://" + sessionManager.getFpTag().toLowerCase() + ".nowfloats.com", sessionManager.getFpTag(),
                status, paymentId, transactionId, gmtTime, mErpId, mOPC, mErpExecutiveMailId, product.CurrencyCode+ " " +amount + " /-", packageName);
        MainMailModel mailData =new MainMailModel();
        mailData._id = "57a6908294aa3c0ee4464a54";
        mailData.To = to;
        mailData.From = "ria@nowfloats.com";
        mailData.EmailBody = mailBody;
        mailData.Subject = "Hey! Ria here";
        mailData.ProcessingState = 0;
        mailData.Type = 0;
        IOPCValidation sendMailInterface = Constants.restAdapter.create(IOPCValidation.class);
        sendMailInterface.sendMail(Constants.clientId, mailData, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                BoostLog.d("StoreDataActivity", "Send Mail REsponse:" + s);
                if(s.equals("SUCCESSFULLY ADDED TO QUEUE")){
                    BoostLog.d("StoreDataActivity", "Mail Sent Succesfully");
                }else {
                    BoostLog.d("StoreDataActivity", "Error while sending mail");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                BoostLog.d("StoreDataActivity", "Error while sending mail");
            }
        });

    }

    private void markAsPaid(String amount) {
        final ProgressDialog pd = ProgressDialog.show(this, "", "Please wait while activating the package...");
        ERPRequestModel erpRequstModel = new ERPRequestModel();
        erpRequstModel._nfInternalERPId = "";
        erpRequstModel.customerEmailId = sessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL);
        erpRequstModel.purchasedUnits = 1;
        erpRequstModel.sendEmail = true;
        final MarkAsPaidModel markAsPaid = new MarkAsPaidModel();
        markAsPaid.ClientId  = "A91B82DE3E93446A8141A52F288F69EFA1B09B1D13BB4E55BE743AB547B3489E";
        try {
            markAsPaid.ExpectedAmount = Double.parseDouble(amount);
        }catch (Exception e){
            if(pd!=null && pd.isShowing()){
                pd.dismiss();
            }
            Toast.makeText(this, "Error while marking the FP paid", Toast.LENGTH_LONG).show();
            return;
        }
        markAsPaid.type = 1;
        markAsPaid.validityInMths = product.ValidityInMths;
        markAsPaid.FpId = sessionManager.getFPID();
        markAsPaid.FpTag  =sessionManager.getFpTag();
        markAsPaid.IsPaid = true;
        markAsPaid.currencyCode = product.CurrencyCode;
        markAsPaid.packageId = product._id;
        markAsPaid.customerSalesOrderRequest = erpRequstModel;
        IOPCValidation markAsPaidInterface = Constants.restAdapter.create(IOPCValidation.class);
        markAsPaidInterface.markAsPaid(markAsPaid, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                BoostLog.d("StoreDataActivity", "Mark As paid response:" + s);
                if(pd!=null && pd.isShowing()){
                    pd.dismiss();
                }
                if(s!=null && s.contains("OK")) {
                    showDialog("Activated", "Your package is successfully activated");
                }else if(s!=null && s.contains("NFINV")){
                    showDialog("Activated", "Your package is successfully activated with Invoice Number: " + s);
                }else{
                    showDialog("Error", "Error while activating the package. Please Contact Customer Support");
                    BoostLog.d("StoreDataActivity", " Response not ok");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if(pd!=null && pd.isShowing()){
                    pd.dismiss();
                }
                showDialog("Error", "Error while activating the package");
            }
        });
    }

    private void showDialog(String title, String msg){
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(msg).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    @Subscribe
    public void post_getFPDetails(Get_FP_Details_Event response)
    {/**Do refresh for widgets**/}

    @Override
    protected void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
    }
}
package com.nowfloats.Store;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.Store.Adapters.ExpandableListAdapter;
import com.nowfloats.Store.Adapters.PhotoAdapter;
import com.nowfloats.Store.Model.EnablePackageResponse;
import com.nowfloats.Store.Model.MailModel;
import com.nowfloats.Store.Model.PhotoItem;
import com.nowfloats.Store.Model.StoreModel;
import com.nowfloats.Store.Model.WidgetPacks;
import com.nowfloats.Store.Service.StoreInterface;
import com.nowfloats.Store.iapUtils.IabHelper;
import com.nowfloats.Store.iapUtils.IabResult;
import com.nowfloats.Store.iapUtils.Inventory;
import com.nowfloats.Store.iapUtils.Purchase;
import com.nowfloats.signup.UI.Model.Get_FP_Details_Event;
import com.nowfloats.signup.UI.Service.Get_FP_Details_Service;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.TwoWayView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.sephiroth.android.library.easing.Linear;
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
                if (product.ExternalApplicationDetails==null || product.ExternalApplicationDetails.equals("null")
                        || product.ExternalApplicationDetails.size()==0){
                    materialProgress.dismiss();
                    ProductPrice.setText(getString(R.string.interest));
                    product_validity.setVisibility(View.GONE);
                    product_pay.setBackgroundColor(getResources().getColor(R.color.primaryColor));

                    ClickedValues = sessionManager.getLocalStorePurchase();
                    if (ClickedValues.contains(product.Name)) {
//                        if (sessionManager.getLightHousePurchase()){
                            ProductPrice.setText("Already requested");
                            product_pay.setBackgroundColor(getResources().getColor(R.color.greenDark));
//                        }
                    }
//                    else if (product.Name.equals("NowFloats WildFire")) {
//                        if (sessionManager.getWildFirePurchase()){ProductPrice.setText("Already requested");
//                            product_pay.setBackgroundColor(getResources().getColor(R.color.greenDark));}
//                    }
                    try {
                        if (product.Name.equalsIgnoreCase("NowFloats Dictate") || product.Name.equalsIgnoreCase("NowFloats WildFire")) {
                            ProductPrice.setText("Contact Us");
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
                                                    ProductPrice.setText("Already requested");
                                                    product_pay.setBackgroundColor(getResources().getColor(R.color.greenDark));
                                                    new MaterialDialog.Builder(StoreDataActivity.this)
                                                            .title("Thank you for your interest!")
                                                            .content("Our team will get in touch with you within 48 hours to tell you more about our pricing plans.")
                                                            .negativeText("Ok")
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
                                                            Methods.showSnackBarNegative(StoreDataActivity.this, "Please try again...");
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
                                            ProductPrice.setText("Already requested");
                                            product_pay.setBackgroundColor(getResources().getColor(R.color.greenDark));

                                            new MaterialDialog.Builder(StoreDataActivity.this)
                                                    .title("Thank you for your interest!")
                                                    .content("Our team will get in touch with you within 48 hours to tell you more about our pricing plans.")
                                                    .negativeText("Ok")
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
                                            Methods.showSnackBarNegative(StoreDataActivity.this, "Uh oh! Something went wrong. Please try again.");
                                        }
                                    });
                                } else {
                                    new MaterialDialog.Builder(StoreDataActivity.this)
                                            .title("Processing your request")
                                            .content("We are processing your request. We will get back to you on this as soon as we can. Thank you for your patience")
                                            .negativeText("Ok")
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
                    if (product.ExternalApplicationDetails!=null && product.ExternalApplicationDetails.size()>0){
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
                            ProductPrice.setText("Contact Us");
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
                                                    ProductPrice.setText("Already requested");
                                                    product_pay.setBackgroundColor(getResources().getColor(R.color.greenDark));
                                                    new MaterialDialog.Builder(StoreDataActivity.this)
                                                            .title("Thank you for your interest!")
                                                            .content("Our team will get in touch with you within 48 hours to tell you more about our pricing plans.")
                                                            .negativeText("Ok")
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
                                                            Methods.showSnackBarNegative(StoreDataActivity.this, "Please try again...");
                                                        }
                                                    });
                                                }
                                            });
                                }catch(Exception e){e.printStackTrace();}
                            }else {
                                if (!SKU_PACKAGE.equals("empty")) {
                                    if (Constants.StorePackageIds != null && Constants.StorePackageIds.size() > 0) {
                                    /*for (int i = 0; i < Constants.StorePackageIds.size(); i++) {
                                        if (!product._id.equals(Constants.StorePackageIds.get(i))){
                                            purchaseCheck = true;
                                            break;
                                        }
                                    }*/
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
                                            .title("Unable to process your purchase")
                                            .content("Please reach out to us at ria@nowfloats.com and will be get back to immediately.")
                                            .negativeText("Close")
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
                    });
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
                            urlStr = Constants.BASE_IMAGE_URL + product.Screenshots.get(i).imageUri;
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
    private String getInAppProductPrize(){
        try{
            //play store public license key
            final String base64EncodedPublicKey = Constants.license_key;
            // compute your public key and store it in base64EncodedPublicKey
            mHelper = new IabHelper(this, base64EncodedPublicKey);
            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                public void onIabSetupFinished(IabResult result) {
                    if (!result.isSuccess()) {
                        Methods.showSnackBarNegative(StoreDataActivity.this, "Looks like there is an issue with your in-app billing setup. ");
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
                                        Methods.showSnackBarPositive(StoreDataActivity.this,"This pack is already purchased.");
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
                                            Methods.showSnackBarNegative(StoreDataActivity.this, "Failed to retrieve details. Please go back and try again" );
                                            return;
                                        }else{
                                            InAppPrize = inv.getSkuDetails(SKU_PACKAGE).getPrice();
                                            ProductPrice.setText("Pay " +InAppPrize);
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
                                    Methods.showSnackBarNegative(StoreDataActivity.this, "Purchase failed. Please try again ");
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
                    Methods.showSnackBarPositive(StoreDataActivity.this, "Success! Your pack is now activated.");
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
                    Methods.showSnackBarPositive(StoreDataActivity.this, "Something went wrong. Please try again");
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("Start", "onActivityResult(" + requestCode + "," + resultCode + "," + data);
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
        }
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
package com.nowfloats.Store;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.Store.Model.ActivePackage;
import com.nowfloats.Store.Model.AllPackage;
import com.nowfloats.Store.Model.PackageDetails;
import com.nowfloats.Store.Model.PricingPlansModel;
import com.nowfloats.Store.Model.WidgetPacks;
import com.nowfloats.Store.Service.StoreInterface;
import com.nowfloats.Volley.AppController;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class NewPricingPlansActivity extends AppCompatActivity implements ActivePlansFragment.ActivePlansCallback {

    TextView tvCategory, tvToolBarTitle;
    UserSessionManager mSession;
    private ActivePlansFragment mActivePlansFragment;
    private AllPlansFragment mAllPlansFragment;
    Toolbar toolbar;
    ImageView ivHistory;
    private boolean isHistoryShowing;

    List<PackageDetails> mBasePackages;
    List<PackageDetails>  mTopUps;
    private boolean shouldHistoryVisible = false;

    private static final int NUM_OF_FEATURES = 5;
    private final int DIRECT_REQUEST_CODE = 2013;

    MaterialDialog materialProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_pricing_plans);

        toolbar = (Toolbar) findViewById(R.id.pricing_plans_toolbar);
        tvToolBarTitle = (TextView) toolbar.findViewById(R.id.tv_toolbar_title);
        ivHistory = (ImageView) findViewById(R.id.iv_history);
        ivHistory.setVisibility(View.INVISIBLE);
        setSupportActionBar(toolbar);
        tvToolBarTitle.setText("Pricing Plans");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mSession = new UserSessionManager(this, this);

        tvCategory = (TextView) findViewById(R.id.tv_category);
        tvCategory.setText(mSession.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY));

        mActivePlansFragment = new ActivePlansFragment();
        mAllPlansFragment = new AllPlansFragment();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fl_pricing_plans, mActivePlansFragment)
                .commit();

        ivHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isHistoryShowing){
                    isHistoryShowing = false;
                    mActivePlansFragment.showActivePlans();
                    ivHistory.setImageResource(R.drawable.ic_history_black_24dp);
                } else {
                    isHistoryShowing = true;
                    mActivePlansFragment.showExpiredPlans();
                    ivHistory.setImageResource(R.drawable.ic_av_timer_black_24dp);
                }
            }
        });

        materialProgress = new MaterialDialog.Builder(this)
                .widgetColorRes(R.color.accentColor)
                .content("Please Wait...")
                .progress(true, 0)
                .cancelable(false)
                .build();

        getPricingPlanDetails();
    }

    private void getPricingPlanDetails() {

        String accId = mSession.getFPDetails(Key_Preferences.GET_FP_DETAILS_ACCOUNTMANAGERID);
        String appId = mSession.getFPDetails(Key_Preferences.GET_FP_DETAILS_APPLICATION_ID);
        String country = mSession.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY);
        Map<String, String> params = new HashMap<>();
        if (accId.length()>0){
            params.put("identifier", accId);
        }else{
            params.put("identifier", appId);
        }
        params.put("clientId", Constants.clientId);
        params.put("fpId", mSession.getFPID());
        params.put("country",country.toLowerCase());
        params.put("fpCategory", mSession.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY).toUpperCase());

        Constants.restAdapter.create(StoreInterface.class).getStoreList(params, new Callback<PricingPlansModel>() {
            @Override
            public void success(PricingPlansModel storeMainModel, Response response) {
                if(storeMainModel != null){
                    preProcessAndDispatchPlans(storeMainModel);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Test", error.getMessage());
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(shouldHistoryVisible) {
            ivHistory.setVisibility(View.VISIBLE);
        }
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==DIRECT_REQUEST_CODE && resultCode==RESULT_OK){
            if(data==null){
                return;
            }
            final boolean success = data.getBooleanExtra(com.romeo.mylibrary.Constants.RESULT_SUCCESS_KEY, false);
            final String status = data.getStringExtra(com.romeo.mylibrary.Constants.RESULT_STATUS);
            final String message = data.getStringExtra(com.romeo.mylibrary.Constants.ERROR_MESSAGE);
            final String paymentId = data.getStringExtra(com.romeo.mylibrary.Constants.PAYMENT_ID);
            final String transactionId = data.getStringExtra(com.romeo.mylibrary.Constants.TRANSACTION_ID);
            final String amount = data.getStringExtra(com.romeo.mylibrary.Constants.FINAL_AMOUNT);
            final boolean showTobeActivatedOn = data.getBooleanExtra("showToBeActivatedOn", false);
            final String tobeActivatedOn = data.getStringExtra("toBeActivatedOn");
            //sendEmail(success, status, message, paymentId, transactionId, amount);
            BoostLog.d("TransaCtionId", transactionId);
            if(success) {
                if(status.equals("Success")) {

                    MixPanelController.track(EventKeysWL.PAYMENT_SUCCESSFULL, null);

                    /*String msg = "Thank you! \n" +
                            "The Payment ID for your transaction is " + paymentId +". Your package will be activated within 24 hours. \n" +
                            "You can reach customer support at ria@nowfloats.com or 1860-123-1233 for any queries.";
                    showDialog(status, msg);*/
                    pollServerforStatus(transactionId, paymentId, status, showTobeActivatedOn, tobeActivatedOn);
                }
            }else {
                if(status.equals("Pending")){
                    String msg = "Alert! \n" +
                            "Your payment is pending. Once your payment is successful, your package will be activated within 24 hours. The Payment ID for your transaction is " + paymentId +" . \n" +
                            "You can reach customer support at ria@nowfloats.com or 1860-123-1233 for any queries.";
                    showDialog(status, msg);
                }else if (status.equals("Failure")){
                    String msg = "Sorry! \n" +
                            "This transaction failed. To retry, please go to the Store and pay again. \n" +
                            "You can reach customer support at ria@nowfloats.com or 1860-123-1233 for any queries.";
                    showDialog(status, msg);
                }
            }
        }
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

    private void pollServerforStatus(final String transactionId, final String paymentid, final String status, final boolean showTobeActivatedOn, final String tobeActivatedOn) {
        if(!materialProgress.isShowing()) {
            materialProgress.show();
        }
        String url = Constants.NOW_FLOATS_API_URL + "/payment/v1/floatingpoint/getPaymentStatus?clientId=" + Constants.clientId +"&paymentRequestId=" + transactionId;
        JsonObjectRequest request  =new JsonObjectRequest(Request.Method.POST, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("Result").equals("SUCCESS")) {
                        if(materialProgress!=null) {
                            materialProgress.dismiss();
                        }
                        if(!showTobeActivatedOn) {
                            String msg = "Thank you! \n" +
                                    "The Payment ID for your transaction is " + paymentid + ". Your package will be activated within 24 hours. \n" +
                                    "You can reach customer support at ria@nowfloats.com or 1860-123-1233 for any queries.";
                            showDialog(status, msg);
                        }
                        getPricingPlanDetails();

                    }else {
                        pollServerforStatus(transactionId, paymentid, status, showTobeActivatedOn, tobeActivatedOn);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    materialProgress.dismiss();
                }


            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String msg = "Your PaymentId is: " + paymentid + ". Please Contact Customer Support.";
                materialProgress.dismiss();
                showDialog(status, msg);
            }
        });
        AppController.getInstance().addToRequstQueue(request);
    }

    private void preProcessAndDispatchPlans(final PricingPlansModel storeMainModel) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<ActivePackage> activePlans = new ArrayList<>();
                final List<ActivePackage> expiredPlans = new ArrayList<>();
                for(ActivePackage activePackage : storeMainModel.activePackages){
                    int featuresCount = 0;
                    StringBuilder featuresBuilder = new StringBuilder("");
                    if(activePackage.getWidgetPacks()!=null) {
                        for (WidgetPacks widget : activePackage.getWidgetPacks()) {
                            featuresBuilder.append("• " + (widget.Name == null?"NA":widget.Name )+ "\n");
                            featuresCount++;
                            if (featuresCount >= NUM_OF_FEATURES) {
                                break;
                            }
                        }
                    }
                    activePackage.setFeatures(featuresBuilder.toString());
                    if(!isPackageExpired(activePackage)){
                        activePlans.add(activePackage);
                    }else {
                        expiredPlans.add(activePackage);
                    }
                }
                for(AllPackage allPackage : storeMainModel.allPackages){
                    if(allPackage.getKey().equals("TopUp")){
                        mTopUps = allPackage.getValue();
                        for(PackageDetails topUp : mTopUps) {
                            List<String> featuresList = new ArrayList<>();
                            int count = 0;
                            for(WidgetPacks widget : topUp.getWidgetPacks()){
                                if(widget.Name!=null) {
                                    featuresList.add(widget.Name);
                                    count++;
                                    if (count >= 8) {
                                        break;
                                    }
                                }
                            }
                            topUp.setFeatureList(featuresList);
                        }
                    } else if(allPackage.getKey().equals("Base")){
                        mBasePackages = allPackage.getValue();
                        for(PackageDetails basePackage : mBasePackages) {
                            List<String> featuresList = new ArrayList<>();
                            int count = 0;
                            for(WidgetPacks widget : basePackage.getWidgetPacks()){
                                if(widget.Name!=null) {
                                    featuresList.add(widget.Name);
                                    count++;
                                    if (count >= 8) {
                                        break;
                                    }
                                }
                            }
                            basePackage.setFeatureList(featuresList);
                        }
                    }
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mActivePlansFragment.setActivePlans(activePlans);
                        mActivePlansFragment.showActivePlans();
                        if(expiredPlans!=null && expiredPlans.size()>0) {
                            mActivePlansFragment.setExpiredPlans(expiredPlans);
                            ivHistory.setVisibility(View.VISIBLE);
                            shouldHistoryVisible = true;
                        }else {
                            ivHistory.setVisibility(View.INVISIBLE);
                            shouldHistoryVisible = false;
                        }
                        if(mBasePackages == null && mTopUps == null){
                            return;
                        }
                        if(mBasePackages == null){
                            mBasePackages = new ArrayList<PackageDetails>();
                        }
                        if(mTopUps == null) {
                            mTopUps = new ArrayList<PackageDetails>();
                        }
                        Collections.sort(mBasePackages);
                        Collections.sort(mTopUps);
                        mAllPlansFragment.setBasePlans(mBasePackages);
                        mAllPlansFragment.setTopUps(mTopUps);
                        mActivePlansFragment.setTopUps(mTopUps);
                    }
                });
            }
        }).start();
    }

    private boolean isPackageExpired(ActivePackage activePackage){
        long time = Long.parseLong(activePackage.getToBeActivatedOn().replaceAll("[^\\d]", ""));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        double totalMonthsValidity = activePackage.getTotalMonthsValidity();
        calendar.add(Calendar.MONTH, (int)Math.floor(totalMonthsValidity));
        calendar.add(Calendar.DATE, (int) ((totalMonthsValidity-Math.floor(totalMonthsValidity))*30));
        return calendar.getTime().before(new Date());
    }


    @Override
    public void onRenewOrUpdate() {
        if(mBasePackages==null || mBasePackages.size() == 0){
            Methods.showSnackBarNegative(this, "Renew/Update is not available");
            return;
        }
        ivHistory.setVisibility(View.INVISIBLE);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fl_pricing_plans, mAllPlansFragment)
                .addToBackStack(null)
                .commit();
    }
}

package com.nowfloats.Store;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
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
import com.nowfloats.NavigationDrawer.DictateFragment;
import com.nowfloats.NavigationDrawer.WildFireFragment;
import com.nowfloats.NavigationDrawer.businessApps.BusinessAppsFragment;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class NewPricingPlansActivity extends AppCompatActivity {

    TextView tvCategory, tvToolBarTitle;
    UserSessionManager mSession;
    Toolbar toolbar;
    ImageView ivHistory;
    List<PackageDetails> mBasePackages;
    List<PackageDetails> mTopUps;
    private static final int NUM_OF_FEATURES = 5;
    public static final int DIRECT_REQUEST_CODE = 2013;

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

        getPricingPlanDetails();
    }

    private void getPricingPlanDetails() {
        showDialog();
        String accId = mSession.getFPDetails(Key_Preferences.GET_FP_DETAILS_ACCOUNTMANAGERID);
        String appId = mSession.getFPDetails(Key_Preferences.GET_FP_DETAILS_APPLICATION_ID);
        String country;
        if (Constants.PACKAGE_NAME.equals("com.redtim")) {
            country = "UNITED STATES";
        } else {
            country = mSession.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY);
        }
        Map<String, String> params = new HashMap<>();
        if (accId.length() > 0) {
            params.put("identifier", accId);
        } else {
            params.put("identifier", appId);
        }
        params.put("clientId", Constants.clientId);
        params.put("fpId", mSession.getFPID());
        params.put("country", country.toLowerCase());
        params.put("fpCategory", mSession.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY).toUpperCase());

        Constants.restAdapter.create(StoreInterface.class).getStoreList(params, new Callback<PricingPlansModel>() {
            @Override
            public void success(PricingPlansModel storeMainModel, Response response) {
                if (storeMainModel != null) {
                    preProcessAndDispatchPlans(storeMainModel);
                } else {
                    hideDialog();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                hideDialog();
                Log.d("Test", error.getMessage());
            }
        });

    }

    private void showPlanFragments() {
        hideDialog();
        Fragment frag;
        String fragmentName = getIntent().getStringExtra("fragmentName");
        if (fragmentName == null) fragmentName = "";
        switch (fragmentName) {
            case "Dictate":
                frag = new DictateFragment();
                break;
            case "Wildfire":
                frag = new WildFireFragment();
                break;
            case "Biz APP":
                frag = new BusinessAppsFragment();
                break;
            case "BasePlans":
            default:
                AllPlansFragment allFrag = new AllPlansFragment();
                allFrag.setBasePlans(mBasePackages);
                allFrag.setTopUps(mTopUps);
                frag = allFrag;
                break;
            //empty screen
        }
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fl_pricing_plans, frag)
                .commit();
    }

    private void hideDialog() {
        if (materialProgress.isShowing())
            materialProgress.dismiss();
    }

    private void showDialog() {
        if (materialProgress == null) {
            materialProgress = new MaterialDialog.Builder(this)
                    .widgetColorRes(R.color.accentColor)
                    .content("Please Wait...")
                    .progress(true, 0)
                    .cancelable(false)
                    .build();

        }
        if (!materialProgress.isShowing()) {
            materialProgress.show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DIRECT_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data == null) {
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
            if (success) {
                if (status.equals("Success")) {

                    MixPanelController.track(EventKeysWL.PAYMENT_SUCCESSFULL, null);

                    /*String msg = "Thank you! \n" +
                            "The Payment ID for your transaction is " + paymentId +". Your package will be activated within 24 hours. \n" +
                            "You can reach customer support at ria@nowfloats.com or 1860-123-1233 for any queries.";
                    showDialog(status, msg);*/
                    pollServerForStatus(transactionId, paymentId, status, showTobeActivatedOn, tobeActivatedOn, 0);
                }
            } else {
                if (status.equals("Pending")) {
                    String msg = "Alert! \n" +
                            "Your payment is pending. Once your payment is successful, your package will be activated within 24 hours. The Payment ID for your transaction is " + paymentId + " . \n" +
                            "You can reach customer support at ria@nowfloats.com or 1860-123-1233 for any queries.";
                    Methods.showDialog(NewPricingPlansActivity.this, status, msg);
                } else if (status.equals("Failure")) {
                    String msg = "Sorry! \n" +
                            "This transaction failed. To retry, please go to the Store and pay again. \n" +
                            "You can reach customer support at ria@nowfloats.com or 1860-123-1233 for any queries.";
                    Methods.showDialog(NewPricingPlansActivity.this, status, msg);
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void pollServerForStatus(final String transactionId, final String paymentid, final String status, final boolean showTobeActivatedOn, final String tobeActivatedOn, final int pollCount) {
        if (!materialProgress.isShowing()) {
            materialProgress.show();
        }
        String url = Constants.NOW_FLOATS_API_URL + "/payment/v1/floatingpoint/getPaymentStatus?clientId=" + Constants.clientId + "&paymentRequestId=" + transactionId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("Result").equals("SUCCESS")) {
                        if (materialProgress != null) {
                            materialProgress.dismiss();
                        }
                        if (!showTobeActivatedOn) {
                            String msg = "Thank you! \n" +
                                    "The Payment ID for your transaction is " + paymentid + ". Your package will be activated within 24 hours. \n" +
                                    "You can reach customer support at ria@nowfloats.com or 1860-123-1233 for any queries.";
                            Methods.showDialog(NewPricingPlansActivity.this, status, msg);
                        }
                        getPricingPlanDetails();

                    } else {
                        if (pollCount < 5) {
                            pollServerForStatus(transactionId, paymentid, status, showTobeActivatedOn, tobeActivatedOn, pollCount + 1);
                        } else {
                            String msg = "Alert! \n" +
                                    "Your payment is pending. Once your payment is successful, your package will be activated within 24 hours. The Payment Request ID for your transaction is " + transactionId + " . \n" +
                                    "You can reach customer support at ria@nowfloats.com or 1860-123-1233 for any queries.";
                            Methods.showDialog(NewPricingPlansActivity.this, status, msg);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    materialProgress.dismiss();
                }


            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String msg = "Your PaymentId is: " + paymentid + ". Please Contact Customer Support.";
                materialProgress.dismiss();
                Methods.showDialog(NewPricingPlansActivity.this, status, msg);
            }
        });
        AppController.getInstance().addToRequstQueue(request);
    }

    private void preProcessAndDispatchPlans(final PricingPlansModel storeMainModel) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                for (AllPackage allPackage : storeMainModel.allPackages) {
                    if (allPackage.getKey().equals("TopUp")) {
                        mTopUps = allPackage.getValue();
                        for (PackageDetails topUp : mTopUps) {
                            List<String> featuresList = new ArrayList<>();
                            int count = 0;
                            for (WidgetPacks widget : topUp.getWidgetPacks()) {
                                if (widget.Name != null) {
                                    featuresList.add(widget.Name);
                                    count++;
                                    if (count >= 8) {
                                        break;
                                    }
                                }
                            }
                            topUp.setFeatureList(featuresList);
                        }
                    } else if (allPackage.getKey().equals("Base")) {
                        mBasePackages = allPackage.getValue();
                        for (PackageDetails basePackage : mBasePackages) {
                            List<String> featuresList = new ArrayList<>();
                            int count = 0;
                            for (WidgetPacks widget : basePackage.getWidgetPacks()) {
                                if (widget.Name != null) {
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


                        if (mBasePackages == null) {
                            mBasePackages = new ArrayList<PackageDetails>();
                        }
                        if (mTopUps == null) {
                            mTopUps = new ArrayList<PackageDetails>();
                        }
                        Collections.sort(mBasePackages);
                        Collections.sort(mTopUps);
                        showPlanFragments();
                    }
                });
            }
        }).start();
    }
}

package com.nowfloats.Store;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.finsky.externalreferrer.IGetInstallReferrerService;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.Store.Adapters.TopUpDialogAdapter;
import com.nowfloats.Store.Model.PackageDetails;
import com.nowfloats.Store.Service.TopUpPlansService;
import com.nowfloats.Volley.AppController;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.thinksity.BuildConfig;
import com.thinksity.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Admin on 15-02-2018.
 */

public class TopUpDialog implements TopUpPlansService.ServiceCallbackListener, View.OnClickListener, TopUpDialogAdapter.onItemClickListener {

    private static final int DIRECT_REQUEST_CODE = 2013, RESULT_OK = -1;
    private List<PackageDetails> mTopUps;
    private  List<PackageDetails> visiblePackages;
    private MaterialDialog topUpDialog;
    private Context mContext;
    private Activity activity;
    private String planType;
    private String selectedPackage;
    private ProgressDialog progressDialog;

    public enum TopUpType{
        WildFire,Dictate,App;
    }
    public TopUpDialog(Activity activity){
        this.activity = activity;
        mContext = activity;
    }
    public void getTopUpPricing(String planType){
        this.planType = planType;
        if (mTopUps == null) {
            UserSessionManager mSession = new UserSessionManager(mContext, activity);
            String accId = mSession.getFPDetails(Key_Preferences.GET_FP_DETAILS_ACCOUNTMANAGERID);
            String appId = mSession.getFPDetails(Key_Preferences.GET_FP_DETAILS_APPLICATION_ID);
            String country = mSession.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY);
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
            TopUpPlansService services = TopUpPlansService.getTopUpService(this);
            services.getTopUpPackages(params);
        }else{
            showTopUpDialog();
        }
    }

    private void showTopUpDialog() {
        if (visiblePackages == null)
            visiblePackages = new ArrayList<>();
        else{
            visiblePackages.clear();
        }

        for (PackageDetails details:mTopUps){
            if(details.getName() != null && details.getName().toLowerCase().contains(planType.toLowerCase())){
                visiblePackages.add(details);
            }
        }
        if (visiblePackages.size() == 0){
            Toast.makeText(mContext,mContext.getString( R.string.your_account_can_t_activate_this_top_up_packages), Toast.LENGTH_SHORT).show();
            return;
        }
        if (topUpDialog == null) {
            topUpDialog = new MaterialDialog.Builder(mContext)
                    .customView(R.layout.dialog_top_up_plans, false)
                    .positiveText("Buy")
                    .cancelable(false)
                    .positiveColorRes(R.color.primaryColor)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                            startBuy(selectedPackage);
                        }
                    })
                    .build();
        }
        View view = topUpDialog.getCustomView();
        if (view == null) return;
        view.findViewById(R.id.img_cancel).setOnClickListener(this);
        TextView title = view.findViewById(R.id.tv_title);
        TextView description = view.findViewById(R.id.tv_description);
        title.setText(String.format("%s Pricing",planType));
        description.setText(String.format(mContext.getString(R.string.select_the_duration_s_package_),planType));
        RecyclerView mRecyclerView = view.findViewById(R.id.rv_plans);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        TopUpDialogAdapter adapter = new TopUpDialogAdapter(visiblePackages);
        selectedPackage = visiblePackages.get(0).getId();
        adapter.setItemClickCallback(this);
        mRecyclerView.setAdapter(adapter);
        topUpDialog.show();
    }

    private void startBuy(String id){
        Intent i = new Intent(mContext, BuildConfig.APPLICATION_ID.equals("com.biz2.nowfloats")?ProductCheckout_v2Activity.class:ProductCheckoutActivity.class);
        i.putExtra("package_ids", new String[]{id});
        activity.startActivityForResult(i, DIRECT_REQUEST_CODE);
    }
    @Override
    public void onDataReceived(List<PackageDetails> packages) {
        if (packages == null || packages.size() == 0){
            Toast.makeText(mContext, mContext.getString(R.string.your_account_cant_activate_any_topup_package), Toast.LENGTH_SHORT).show();
            return;
        }
        mTopUps = packages;
        showTopUpDialog();
    }
    @Override
    public void startApiCall(){
        showProgressDialog("Loading...");
    }

    @Override
    public void endApiCall() {
        hideProgressDialog();
    }
    private void hideProgressDialog(){
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    private void showProgressDialog(String msg){
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.setMessage(msg);
        if (!progressDialog.isShowing())
            progressDialog.show();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_cancel:
                topUpDialog.dismiss();
                break;
        }
    }

    public void onPaymentResultReceived(int requestCode, int resultCode, Intent data){
        if(requestCode==DIRECT_REQUEST_CODE && resultCode==RESULT_OK){
            if(data==null){
                return;
            }
//            final boolean success = data.getBooleanExtra(com.romeo.mylibrary.Constants.RESULT_SUCCESS_KEY, false);
//            final String status = data.getStringExtra(com.romeo.mylibrary.Constants.RESULT_STATUS);
//            final String message = data.getStringExtra(com.romeo.mylibrary.Constants.ERROR_MESSAGE);
//            final String paymentId = data.getStringExtra(com.romeo.mylibrary.Constants.PAYMENT_ID);
//            final String transactionId = data.getStringExtra(com.romeo.mylibrary.Constants.TRANSACTION_ID);
//            final String amount = data.getStringExtra(com.romeo.mylibrary.Constants.FINAL_AMOUNT);
//            final boolean showTobeActivatedOn = data.getBooleanExtra("showToBeActivatedOn", false);
//            final String tobeActivatedOn = data.getStringExtra("toBeActivatedOn");
//            //sendEmail(success, status, message, paymentId, transactionId, amount);
//            BoostLog.d("TransaCtionId", transactionId);
//            if(success) {
//                if(status.equals("Success")) {
//
//                    MixPanelController.track(EventKeysWL.PAYMENT_SUCCESSFULL, null);
//
//                    /*String msg = "Thank you! \n" +
//                            "The Payment ID for your transaction is " + paymentId +". Your package will be activated within 24 hours. \n" +
//                            "You can reach customer support at ria@nowfloats.com or 1860-123-1233 for any queries.";
//                    showProgressDialog(status, msg);*/
//                    pollServerForStatus(transactionId, paymentId, status, showTobeActivatedOn, tobeActivatedOn);
//                }
//            }else {
//                if(status.equals("Pending")){
//                    String msg = "Alert! \n" +
//                            "Your payment is pending. Once your payment is successful, your package will be activated within 24 hours. The Payment ID for your transaction is " + paymentId +" . \n" +
//                            "You can reach customer support at ria@nowfloats.com or 1860-123-1233 for any queries.";
//                    Methods.showDialog(mContext,status, msg);
//                }else if (status.equals("Failure")){
//                    String msg = "Sorry! \n" +
//                            "This transaction failed. To retry, please go to the Store and pay again. \n" +
//                            "You can reach customer support at ria@nowfloats.com or 1860-123-1233 for any queries.";
//                    Methods.showDialog(mContext,status, msg);
//                }
//            }
        }
    }
    private void pollServerForStatus(final String transactionId, final String paymentid, final String status, final boolean showTobeActivatedOn, final String tobeActivatedOn) {
        showProgressDialog(mContext.getString(R.string.please_wait));
        String url = Constants.NOW_FLOATS_API_URL + "/payment/v1/floatingpoint/getPaymentStatus?clientId=" + Constants.clientId +"&paymentRequestId=" + transactionId;
        JsonObjectRequest request  =new JsonObjectRequest(Request.Method.POST, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("Result").equals("SUCCESS")) {
                        hideProgressDialog();
                        if(!showTobeActivatedOn) {
                            String msg = "Thank you! \n" +
                                    mContext.getString(R.string.the_payment_id_for_your_transaction_is) + paymentid + mContext.getString(R.string.your_package_will_be_activated_within_24_hours) +
                                    mContext.getString(R.string.you_can_reach_customer_support_at_ria_nowfloats_com_or_1860_123_1233_for_any_queries);
                            Methods.showDialog(mContext,status, msg);
                        }

                    }else {
                        pollServerForStatus(transactionId, paymentid, status, showTobeActivatedOn, tobeActivatedOn);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    hideProgressDialog();
                }


            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String msg = mContext.getString(R.string.your_payment_id_is_) + paymentid + mContext.getString(R.string.please_contact_customer_support);
                hideProgressDialog();
                Methods.showDialog(mContext,status, msg);
            }
        });
        AppController.getInstance().addToRequstQueue(request);
    }
    @Override
    public void onItemClick(String id) {
        selectedPackage = id;
    }
}
package com.nowfloats.Store;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.nowfloats.Store.Adapters.PricingPlansPagerAdapter;
import com.nowfloats.Store.Model.PackageDetails;
import com.nowfloats.Volley.AppController;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by NowFloats on 16-10-2017.
 */

public class AllPlansFragment extends Fragment {

    ViewPager vpPricingPlans;
    PricingPlansPagerAdapter pricingPagerAdapter;
    private List<PackageDetails> mBasePlans;
    private List<PackageDetails> mTopUps;
    TextView tvPrice, tvCurrency;
    LinearLayout llPurchasePlan;

    MaterialDialog materialProgress;

    private final int DIRECT_REQUEST_CODE = 2013;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_all_plans, container, false);

        vpPricingPlans = view.findViewById(R.id.vp_pricing_plans);
        llPurchasePlan = view.findViewById(R.id.ll_purchase_plan);
        tvPrice = view.findViewById(R.id.tv_price);
        tvCurrency = view.findViewById(R.id.tv_currency);
        vpPricingPlans.setClipToPadding(false);
        vpPricingPlans.setPageMargin(24);
        vpPricingPlans.setPadding(68, 8, 68, 8);
        vpPricingPlans.setOffscreenPageLimit(3);
        pricingPagerAdapter = new PricingPlansPagerAdapter(getActivity().getSupportFragmentManager(), mBasePlans, mTopUps);
        vpPricingPlans.setAdapter(pricingPagerAdapter);
        TabLayout tabLayout = view.findViewById(R.id.tabDots);
        if (mBasePlans != null && mBasePlans.size()>1){
            tabLayout.setupWithViewPager(vpPricingPlans, true);
        }

        pricingPagerAdapter.notifyDataSetChanged();
        if(mBasePlans!=null && mBasePlans.size()>0) {
            tvPrice.setText(mBasePlans.get(vpPricingPlans.getCurrentItem()).getPrice() + "");
            tvCurrency.setText(mBasePlans.get(vpPricingPlans.getCurrentItem()).getCurrencyCode());
        }

        vpPricingPlans.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tvPrice.setText(mBasePlans.get(position).getPrice()+"");
                tvCurrency.setText(mBasePlans.get(position).getCurrencyCode());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if(mBasePlans == null || mBasePlans.size() == 0){
            llPurchasePlan.setVisibility(View.GONE);
        }
        llPurchasePlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MixPanelController.track(EventKeysWL.BUY_NOW_STORE_CLICKED, null);
                Intent i = new Intent(getActivity(), ProductCheckoutActivity.class);
                i.putExtra("package_ids", new String[]{mBasePlans.get(vpPricingPlans.getCurrentItem()).getId()});
                startActivityForResult(i, DIRECT_REQUEST_CODE);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        if (materialProgress == null){
            materialProgress = new MaterialDialog.Builder(getActivity())
                    .widgetColorRes(R.color.accentColor)
                    .content("Please Wait...")
                    .progress(true, 0)
                    .cancelable(false)
                    .build();

        }

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==DIRECT_REQUEST_CODE && resultCode== RESULT_OK){
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
                    pollServerForStatus(transactionId, paymentId, status, showTobeActivatedOn, tobeActivatedOn, 0);
                }
            }else {
                if(status.equals("Pending")){
                    String msg = "Alert! \n" +
                            "Your payment is pending. Once your payment is successful, your package will be activated within 24 hours";
                    Methods.showDialog(getActivity(),status, msg);
                }else if (status.equals("Failure")){
                    String msg = "Sorry! \n" +
                            "This transaction failed. To retry, please go to the Store and pay again.";
                    Methods.showDialog(getActivity(),status, msg);
                }
            }
        }
    }



    private void pollServerForStatus(final String transactionId, final String paymentid, final String status, final boolean showTobeActivatedOn, final String tobeActivatedOn, final int pollCount) {
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
                                    "The Transaction ID for your transaction is " + transactionId + ". Your package will be activated within 24 hours.";
                            Methods.showDialog(getActivity(),status, msg);
                        }

                    }else {
                        if(pollCount < 5) {
                            pollServerForStatus(transactionId, paymentid, status, showTobeActivatedOn, tobeActivatedOn, pollCount + 1);
                        } else {
                            String msg = "Alert! \n" +
                                    "Your payment is pending. Once your payment is successful, your package will be activated within 24 hours.";
                            Methods.showDialog(getActivity(),status, msg);
                        }

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
                Methods.showDialog(getActivity(),status, msg);
            }
        });
        AppController.getInstance().addToRequstQueue(request);
    }


    public void setBasePlans(List<PackageDetails> basePlans){
        this.mBasePlans = basePlans;
    }

    public void setTopUps(List<PackageDetails> topUps){
        this.mTopUps = topUps;
    }

    @Override
    public void onStop() {
        super.onStop();
        pricingPagerAdapter = null;
        Log.d("AllFrags", getActivity().getSupportFragmentManager().getFragments().size()+"");
    }
}

package com.nowfloats.manageinventory.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nowfloats.BusinessProfile.UI.UI.Business_Address_Activity;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.Product_Gallery.ProductGalleryActivity;
import com.nowfloats.SiteAppearance.SiteAppearanceActivity;
import com.nowfloats.Store.PricingPlansActivity;
import com.nowfloats.manageinventory.SellerAnalyticsActivity;
import com.nowfloats.manageinventory.models.APIResponseModel;
import com.nowfloats.manageinventory.models.CountModel;
import com.nowfloats.manageinventory.models.WebActionModel;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.thinksity.R;
import com.twitter.sdk.android.core.models.VideoInfo;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class APEligibilityCheckerFragment extends DialogFragment implements View.OnClickListener{


    ProgressBar pbAddressPincode, pbSubscriptionStatus, pbCustomDomain, pbSiteAppearance, pbShippingMetrics, pbBankDetails;
    ImageView ivAddressPinCode, ivSubscriptionStatus, ivCustomDomain, ivSiteAppearance, ivShippingMetrics, ivBankDetails, ivClose;
    LinearLayout llAddressPincode, llSubscriptionStatus, llCustomDomain, llSiteAppearance, llShippingMetrics, llBankDetails;
    private Handler mHandler;
    private UserSessionManager mSession;
    private boolean mShouldEnableAP = true;
    private EligibilityCheckCallBack mEligibilityCheckCallBack;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_apeligibility_checker_fragemnt, container, false);

        pbAddressPincode = (ProgressBar) view.findViewById(R.id.pb_addr_pin_code);
        pbSiteAppearance = (ProgressBar) view.findViewById(R.id.pb_site_appearance);
        pbSubscriptionStatus = (ProgressBar) view.findViewById(R.id.pb_subscription_status);
        pbCustomDomain = (ProgressBar) view.findViewById(R.id.pb_custom_domain);
        pbShippingMetrics = (ProgressBar) view.findViewById(R.id.pb_shipping_metrics);
        pbBankDetails = (ProgressBar) view.findViewById(R.id.pb_bank_details);

        ivAddressPinCode = (ImageView) view.findViewById(R.id.iv_addr_pincode);
        ivSubscriptionStatus = (ImageView) view.findViewById(R.id.iv_subscription_status);
        ivCustomDomain = (ImageView) view.findViewById(R.id.iv_custom_domain);
        ivSiteAppearance = (ImageView) view.findViewById(R.id.iv_site_appearance);
        ivShippingMetrics = (ImageView) view.findViewById(R.id.iv_shipping_metrics);
        ivBankDetails = (ImageView) view.findViewById(R.id.iv_bank_details);

        llAddressPincode = (LinearLayout) view.findViewById(R.id.ll_addr_pincode);
        llSubscriptionStatus = (LinearLayout) view.findViewById(R.id.ll_subscription_status);
        llCustomDomain = (LinearLayout) view.findViewById(R.id.ll_custom_domain);
        llSiteAppearance = (LinearLayout) view.findViewById(R.id.ll_site_appearance);
        llShippingMetrics = (LinearLayout) view.findViewById(R.id.ll_shipping_metrics);
        llBankDetails = (LinearLayout) view.findViewById(R.id.ll_bank_details);

        mHandler = new Handler();
        mSession = new UserSessionManager(getActivity(), getActivity());

        initChecking();

        setCancelable(false);

        ivClose = (ImageView) view.findViewById(R.id.iv_close);

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(activity instanceof EligibilityCheckCallBack){
            mEligibilityCheckCallBack = (EligibilityCheckCallBack) activity;
        }else {
            throw new RuntimeException("Implement EligibilityCheckCallBack");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow()
                .setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }



    public void initChecking(){
        int time = 500;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pbAddressPincode.setVisibility(View.GONE);
                mShouldEnableAP &= !TextUtils.isEmpty(mSession.getFPDetails(Key_Preferences.GET_FP_DETAILS_PINCODE));
                if(TextUtils.isEmpty(mSession.getFPDetails(Key_Preferences.GET_FP_DETAILS_PINCODE))){
                    setNegativeStatusColor(ivAddressPinCode);
                    llAddressPincode.setOnClickListener(APEligibilityCheckerFragment.this);
                }else {
                    setPositiveStatusColor(ivAddressPinCode);
                }
            }
        }, time);

        time+=500;

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pbSubscriptionStatus.setVisibility(View.GONE);
                mShouldEnableAP &= !TextUtils.isEmpty(mSession.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE)) &&
                        mSession.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE).equals("1");
                if(TextUtils.isEmpty(mSession.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE)) ||
                        !mSession.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE).equals("1")){
                    setNegativeStatusColor(ivSubscriptionStatus);
                    llSubscriptionStatus.setOnClickListener(APEligibilityCheckerFragment.this);
                }else {
                    setPositiveStatusColor(ivSubscriptionStatus);
                }
            }
        }, time);

        time+=500;

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pbCustomDomain.setVisibility(View.GONE);
                mShouldEnableAP &= !TextUtils.isEmpty(mSession.getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI));
                if(TextUtils.isEmpty(mSession.getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI))){
                    setNegativeStatusColor(ivCustomDomain);
                    llCustomDomain.setOnClickListener(APEligibilityCheckerFragment.this);
                }else {
                    setPositiveStatusColor(ivCustomDomain);
                }
            }
        }, time);

        time+=500;

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pbSiteAppearance.setVisibility(View.GONE);
                mShouldEnableAP &= !TextUtils.isEmpty(mSession.getFPDetails(Key_Preferences.GET_FP_WEBTEMPLATE_ID)) &&
                        mSession.getFPDetails(Key_Preferences.GET_FP_WEBTEMPLATE_ID).equals("57c3c1a65d64370d7cf4eb17");
                if(TextUtils.isEmpty(mSession.getFPDetails(Key_Preferences.GET_FP_WEBTEMPLATE_ID)) ||
                        !mSession.getFPDetails(Key_Preferences.GET_FP_WEBTEMPLATE_ID).equals("57c3c1a65d64370d7cf4eb17")){
                    setNegativeStatusColor(ivSiteAppearance);
                    llSiteAppearance.setOnClickListener(APEligibilityCheckerFragment.this);
                }else {
                    setPositiveStatusColor(ivSiteAppearance);
                }
            }
        }, time);

        time+=500;

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkFpEligibility();

            }
        }, time);


    }

    private void checkFpEligibility(){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(String.format(Constants.NOW_FLOATS_API_URL+"/ProductCatalogue/v1/floatingpoints/isFpNFPaymentEligible?fpId=%s&clientId=%s", mSession.getFPID(), Constants.clientId))
                .build();
        final Gson gson = new Gson();
        client.newCall(request).enqueue(new Callback() {

            Handler handler = new Handler(Looper.getMainLooper());

            @Override
            public void onFailure(Call call, IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        pbShippingMetrics.setVisibility(View.GONE);
                        setNegativeStatusColor(ivShippingMetrics);
                        llShippingMetrics.setOnClickListener(APEligibilityCheckerFragment.this);
                        mShouldEnableAP &= false;
                        checkBankDetails();
                    }
                });

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String res = response.body().string();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        pbShippingMetrics.setVisibility(View.GONE);
                        try {
                            APIResponseModel<Boolean> resp = gson.fromJson(res,
                                    new TypeToken<APIResponseModel<Boolean>>() {
                                    }.getType());

                            if (resp != null && resp.Result) {
                                setPositiveStatusColor(ivShippingMetrics);
                                mShouldEnableAP &= true;
                                checkBankDetails();
                            }else {

                                //Toast.makeText(getActivity(), resp.Error.ErrorCode, Toast.LENGTH_SHORT).show();
                                throw new NullPointerException("Response is Null");
                            }
                        }catch (Exception e)
                        {
                            setNegativeStatusColor(ivShippingMetrics);
                            mShouldEnableAP &= false;
                            llShippingMetrics.setOnClickListener(APEligibilityCheckerFragment.this);
                            checkBankDetails();
                            e.printStackTrace();

                        }
                    }
                });

            }
        });
    }

    private void checkBankDetails(){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(String.format(Constants.WA_BASE_URL+"merchant_profile3/aggregate-data?match={$and:[{merchant_id:'%s'}, {IsArchived:false}]}&group={_id:null, count:{$sum:1}}", mSession.getFPID()))
                .header("Authorization", Constants.WA_KEY)
                .build();
        final Gson gson = new Gson();
        client.newCall(request).enqueue(new Callback() {

            Handler handler = new Handler(Looper.getMainLooper());

            @Override
            public void onFailure(Call call, IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        pbBankDetails.setVisibility(View.GONE);
                        setNegativeStatusColor(ivBankDetails);
                        mShouldEnableAP &= false;
                        llBankDetails.setOnClickListener(APEligibilityCheckerFragment.this);
                        mEligibilityCheckCallBack.onEligibiltyChecked(mShouldEnableAP);
                        APEligibilityCheckerFragment.this.setCancelable(true);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String res = response.body().string();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        pbBankDetails.setVisibility(View.GONE);
                        try {

                            WebActionModel<CountModel> ordersCountData = gson.fromJson(res,
                                    new TypeToken<WebActionModel<CountModel>>() {
                                    }.getType());

                            if (ordersCountData != null && ordersCountData.getData().size()>0 && ordersCountData.getData().get(0).getCount()>0) {
                                setPositiveStatusColor(ivBankDetails);
                                mShouldEnableAP &= true;
                                mEligibilityCheckCallBack.onEligibiltyChecked(mShouldEnableAP);
                                APEligibilityCheckerFragment.this.setCancelable(true);
                                ivClose.setVisibility(View.VISIBLE);
                                if(mShouldEnableAP) {
                                    APEligibilityCheckerFragment.this.dismiss();
                                }
                            }else {
                                throw new NullPointerException("Orders Count is Null");
                            }
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                            setNegativeStatusColor(ivBankDetails);
                            mShouldEnableAP &= false;
                            llBankDetails.setOnClickListener(APEligibilityCheckerFragment.this);
                            mEligibilityCheckCallBack.onEligibiltyChecked(mShouldEnableAP);
                            APEligibilityCheckerFragment.this.setCancelable(true);
                            ivClose.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });
    }

    private void setNegativeStatusColor(ImageView imageView){
        imageView.setImageResource(R.drawable.cross_wrong);
        imageView.setVisibility(View.VISIBLE);
    }

    private void setPositiveStatusColor(ImageView imageView){
        imageView.setImageResource(R.drawable.tick_right);
        imageView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_addr_pincode:
                Intent baActivity = new Intent(getActivity(), Business_Address_Activity.class);
                startActivity(baActivity);
                break;
            case R.id.ll_subscription_status:
                Intent ppActivity = new Intent(getActivity(), PricingPlansActivity.class);
                startActivity(ppActivity);
                break;
            case R.id.ll_custom_domain:
                Intent siteMeterFragment = new Intent(getActivity(), HomeActivity.class);
                siteMeterFragment.putExtra("url", "sitemeter");
                startActivity(siteMeterFragment);
                break;
            case R.id.ll_site_appearance:
                Intent saActivity = new Intent(getActivity(), SiteAppearanceActivity.class);
                startActivity(saActivity);
                break;
            case R.id.ll_shipping_metrics:
                Intent productGallery = new Intent(getActivity(), ProductGalleryActivity.class);
                startActivity(productGallery);
                break;
            case R.id.ll_bank_details:
                dismiss();
                DialogFragment fragment = new PaymentInfoEntryFragment();
                fragment.show(getActivity().getFragmentManager(), "PaymentInfoEntryFragment");
        }
    }

    public interface EligibilityCheckCallBack{
         void onEligibiltyChecked(boolean isEligible);
    }
}

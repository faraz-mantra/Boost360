package com.nowfloats.manageinventory.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nowfloats.BusinessProfile.UI.UI.Business_Address_Activity;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.ProductGallery.ProductGalleryActivity;
import com.nowfloats.SiteAppearance.SiteAppearanceActivity;
import com.nowfloats.Store.NewPricingPlansActivity;
import com.nowfloats.manageinventory.models.APIResponseModel;
import com.nowfloats.manageinventory.models.CountModel;
import com.nowfloats.manageinventory.models.WebActionModel;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class APEligibilityCheckerFragment extends DialogFragment implements View.OnClickListener {


    ProgressBar pbAddressPincode, pbSubscriptionStatus, pbCustomDomain, pbSiteAppearance, pbShippingMetrics, pbBankDetails;
    ImageView ivAddressPinCode, ivSubscriptionStatus, ivCustomDomain, ivSiteAppearance, ivShippingMetrics, ivBankDetails, ivClose;
    LinearLayout llAddressPincode, llSubscriptionStatus, llCustomDomain, llSiteAppearance, llShippingMetrics, llBankDetails;
    private Handler mHandler;
    private UserSessionManager mSession;
    private boolean mShouldEnableAP = true;
    private EligibilityCheckCallBack mEligibilityCheckCallBack;
    private Context mContext;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_apeligibility_checker_fragemnt, container, false);

        pbAddressPincode = view.findViewById(R.id.pb_addr_pin_code);
        pbSiteAppearance = view.findViewById(R.id.pb_site_appearance);
        pbSubscriptionStatus = view.findViewById(R.id.pb_subscription_status);
        pbCustomDomain = view.findViewById(R.id.pb_custom_domain);
        pbShippingMetrics = view.findViewById(R.id.pb_shipping_metrics);
        pbBankDetails = view.findViewById(R.id.pb_bank_details);

        ivAddressPinCode = view.findViewById(R.id.iv_addr_pincode);
        ivSubscriptionStatus = view.findViewById(R.id.iv_subscription_status);
        ivCustomDomain = view.findViewById(R.id.iv_custom_domain);
        ivSiteAppearance = view.findViewById(R.id.iv_site_appearance);
        ivShippingMetrics = view.findViewById(R.id.iv_shipping_metrics);
        ivBankDetails = view.findViewById(R.id.iv_bank_details);

        llAddressPincode = view.findViewById(R.id.ll_addr_pincode);
        llSubscriptionStatus = view.findViewById(R.id.ll_subscription_status);
        llCustomDomain = view.findViewById(R.id.ll_custom_domain);
        llSiteAppearance = view.findViewById(R.id.ll_site_appearance);
        llShippingMetrics = view.findViewById(R.id.ll_shipping_metrics);
        llBankDetails = view.findViewById(R.id.ll_bank_details);

        mHandler = new Handler();
        mSession = new UserSessionManager(getActivity(), getActivity());

        initChecking();

        setCancelable(false);

        ivClose = view.findViewById(R.id.iv_close);

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

        if (activity instanceof EligibilityCheckCallBack) {
            mEligibilityCheckCallBack = (EligibilityCheckCallBack) activity;
        } else {
            throw new RuntimeException(getString(R.string.implement_eligibility_check));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow()
                .setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }


    public void initChecking() {
        int time = 500;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pbAddressPincode.setVisibility(View.GONE);
                mShouldEnableAP &= !TextUtils.isEmpty(mSession.getFPDetails(Key_Preferences.GET_FP_DETAILS_PINCODE));
                if (TextUtils.isEmpty(mSession.getFPDetails(Key_Preferences.GET_FP_DETAILS_PINCODE))) {
                    setNegativeStatusColor(ivAddressPinCode);
                    llAddressPincode.setOnClickListener(APEligibilityCheckerFragment.this);
                } else {
                    setPositiveStatusColor(ivAddressPinCode);
                }
            }
        }, time);

        time += 500;

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pbSubscriptionStatus.setVisibility(View.GONE);
                mShouldEnableAP &= !TextUtils.isEmpty(mSession.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE)) &&
                        mSession.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE).equals("1");
                if (TextUtils.isEmpty(mSession.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE)) ||
                        !mSession.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE).equals("1")) {
                    setNegativeStatusColor(ivSubscriptionStatus);
                    llSubscriptionStatus.setOnClickListener(APEligibilityCheckerFragment.this);
                } else {
                    setPositiveStatusColor(ivSubscriptionStatus);
                }
            }
        }, time);

        time += 500;

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pbCustomDomain.setVisibility(View.GONE);
                mShouldEnableAP &= !TextUtils.isEmpty(mSession.getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI));
                if (TextUtils.isEmpty(mSession.getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI))) {
                    setNegativeStatusColor(ivCustomDomain);
                    llCustomDomain.setOnClickListener(APEligibilityCheckerFragment.this);
                } else {
                    setPositiveStatusColor(ivCustomDomain);
                }
            }
        }, time);

        time += 500;

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pbSiteAppearance.setVisibility(View.GONE);
                mShouldEnableAP &= !TextUtils.isEmpty(mSession.getFPDetails(Key_Preferences.GET_FP_WEBTEMPLATE_ID)) &&
                        mSession.getFPDetails(Key_Preferences.GET_FP_WEBTEMPLATE_ID).equals("57c3c1a65d64370d7cf4eb17");
                if (TextUtils.isEmpty(mSession.getFPDetails(Key_Preferences.GET_FP_WEBTEMPLATE_ID)) ||
                        !mSession.getFPDetails(Key_Preferences.GET_FP_WEBTEMPLATE_ID).equals("57c3c1a65d64370d7cf4eb17")) {
                    setNegativeStatusColor(ivSiteAppearance);
                    llSiteAppearance.setOnClickListener(APEligibilityCheckerFragment.this);
                } else {
                    setPositiveStatusColor(ivSiteAppearance);
                }
            }
        }, time);

        time += 500;

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkFpEligibility();

            }
        }, time);


    }

    private void checkFpEligibility() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(String.format(Constants.NOW_FLOATS_API_URL + "/ProductCatalogue/v1/floatingpoints/isFpNFPaymentEligible?fpId=%s&clientId=%s", mSession.getFPID(), Constants.clientId))
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
                            } else {

                                //Toast.makeText(getActivity(), resp.Error.ErrorCode, Toast.LENGTH_SHORT).show();
                                throw new NullPointerException("Response is Null");
                            }
                        } catch (Exception e) {
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

    private void checkBankDetails() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(String.format(Constants.WA_BASE_URL + "merchant_profile3/aggregate-data?match={$and:[{merchant_id:'%s'}, {IsArchived:false}]}&group={_id:null, count:{$sum:1}}", mSession.getFPID()))
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

                            if (ordersCountData != null && ordersCountData.getData().size() > 0 && ordersCountData.getData().get(0).getCount() > 0) {
                                setPositiveStatusColor(ivBankDetails);
                                mShouldEnableAP &= true;
                                mEligibilityCheckCallBack.onEligibiltyChecked(mShouldEnableAP);
                                APEligibilityCheckerFragment.this.setCancelable(true);
                                ivClose.setVisibility(View.VISIBLE);
                                if (mShouldEnableAP) {
                                    APEligibilityCheckerFragment.this.dismiss();
                                }
                            } else {
                                throw new NullPointerException(getString(R.string.order_count_is_null));
                            }
                        } catch (Exception e) {
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

    private void setNegativeStatusColor(ImageView imageView) {
        imageView.setImageResource(R.drawable.cross_wrong);
        imageView.setVisibility(View.VISIBLE);
    }

    private void setPositiveStatusColor(ImageView imageView) {
        imageView.setImageResource(R.drawable.tick_right);
        imageView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_addr_pincode:
                Intent baActivity = new Intent(getActivity(), Business_Address_Activity.class);
                startActivity(baActivity);
                break;
            case R.id.ll_subscription_status:

                Intent ppActivity = new Intent(getActivity(), NewPricingPlansActivity.class);
                startActivity(ppActivity);
                break;
            case R.id.ll_custom_domain:
//                Intent siteMeterFragment = new Intent(getActivity(), HomeActivity.class);
//                siteMeterFragment.putExtra("url", getString(R.string.site__meter));
//                startActivity(siteMeterFragment);
                try {
                    Intent siteMeterFragment = new Intent(getActivity(), Class.forName("com.dashboard.controller.DashboardActivity"));
                    siteMeterFragment.putExtra("url", getString(R.string.site__meter));
                    startActivity(siteMeterFragment);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.ll_site_appearance:
                showDialog(getString(R.string.site_apperance), getString(R.string.your_website_must_be_changed_to_fresh_milk), getString(R.string.take_me_there_));
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

    private void showDialog(String headText, String message, final String actionButton) {
        AlertDialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(Methods.fromHtml(message));
        builder.setTitle(headText);
        builder.setPositiveButton(actionButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent saActivity = new Intent(getActivity(), SiteAppearanceActivity.class);
                startActivity(saActivity);
                dialog.dismiss();
            }
        });
        dialog = builder.show();
        TextView textView = dialog.findViewById(android.R.id.message);
        Typeface face = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        textView.setTypeface(face);
        textView.setTextColor(Color.parseColor("#808080"));

    }

    public interface EligibilityCheckCallBack {
        void onEligibiltyChecked(boolean isEligible);
    }
}

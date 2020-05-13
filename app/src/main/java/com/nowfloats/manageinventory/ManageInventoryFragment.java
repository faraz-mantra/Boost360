package com.nowfloats.manageinventory;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.inventoryorder.constant.FragmentType;
import com.inventoryorder.constant.IntentConstant;
import com.inventoryorder.model.PreferenceData;
import com.nowfloats.Analytics_Screen.VmnCallCardsActivity;
import com.nowfloats.Business_Enquiries.BusinessEnquiryActivity;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.ProductGallery.ProductCatalogActivity;
import com.nowfloats.manageinventory.models.MerchantProfileModel;
import com.nowfloats.manageinventory.models.WebActionModel;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.Utils;
import com.nowfloats.widget.WidgetKey;
import com.thinksity.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.inventoryorder.ui.FragmentContainerOrderActivityKt.startFragmentActivityNew;
import static com.nowfloats.NavigationDrawer.HomeActivity.headerText;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManageInventoryFragment extends Fragment {
    TextView tvPaymentSetting, tvProductGallery, tvTotalNoOfOrders, tvTotalRevenue, tvSellerAnalytics, tvBusinessInquiries, tvBusinessCalls;
    ImageView ivLockWidget, ivPaymentIcon;
    //Typeface robotoLight;
    private SharedPreferences pref = null;
    UserSessionManager session;
    SharedPreferences.Editor prefsEditor;
    private Activity activity;
    private boolean mIsAPEnabled = false;
    private String mTransactionCharge = "9%";
    private String category_code = "";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        UserSessionManager session = new UserSessionManager(activity.getApplicationContext(), activity);
        category_code = session.getFP_AppExperienceCode();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mainView = inflater.inflate(R.layout.fragment_manage_inventory, container, false);
        ivLockWidget = mainView.findViewById(R.id.lock_widget);
        ivPaymentIcon = mainView.findViewById(R.id.secondrow_ImageView_ProfileV2);

        session = new UserSessionManager(getContext(), activity);
        pref = activity.getSharedPreferences(Constants.PREF_NAME, Activity.MODE_PRIVATE);
        prefsEditor = pref.edit();
        getPaymentSettings();
        return mainView;
    }

    private void getPaymentSettings() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(String.format(Constants.WA_BASE_URL + "merchant_profile3/get-data?query={merchant_id:'%s'}&limit=1", session.getFPID()))
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
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String res = response.body().string();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            WebActionModel<MerchantProfileModel> profile = gson.fromJson(res,
                                    new TypeToken<WebActionModel<MerchantProfileModel>>() {
                                    }.getType());

                            if (profile != null && profile.getData().size() > 0) {
                                if (profile.getData().get(0).getPaymentType() == 0) {
                                    mIsAPEnabled = true;
                                    ivLockWidget.setVisibility(View.GONE);
                                }
                                mTransactionCharge = profile.getData().get(0).getApplicableTxnCharge() + "%";
                            } else {
                                throw new NullPointerException("Orders Count is Null");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onViewCreated(final View mainView, Bundle savedInstanceState) {
        super.onViewCreated(mainView, savedInstanceState);
        if (!isAdded()) return;
        final PorterDuffColorFilter whiteLabelFilter_pop_ip = new PorterDuffColorFilter(getResources()
                .getColor(R.color.white), PorterDuff.Mode.SRC_IN);


        try {
            ivLockWidget.setVisibility(View.GONE);
            //Typeface robotoMedium = Typeface.createFromAsset(activity.getAssets(), "Roboto-Medium.ttf");
            //robotoLight = Typeface.createFromAsset(activity.getAssets(), "Roboto-Light.ttf");

            tvPaymentSetting = mainView.findViewById(R.id.tvPaymentSetting);
            //tvPaymentSetting.setTypeface(robotoMedium);

            tvProductGallery = mainView.findViewById(R.id.tvProductGallery);
            tvProductGallery.setText(TextUtils.isEmpty(session.getFPDetails(Key_Preferences.PRODUCT_CATEGORY)) ? getString(R.string.product_gallery) : session.getFPDetails(Key_Preferences.PRODUCT_CATEGORY) + " Gallery");
            //tvProductGallery.setTypeface(robotoMedium);

            tvSellerAnalytics = mainView.findViewById(R.id.tvSellerAnalytics);
            //tvSellerAnalytics.setTypeface(robotoMedium);

            tvBusinessCalls = mainView.findViewById(R.id.tvBusinessCalls);
            //tvBusinessCalls.setTypeface(robotoMedium);

            tvBusinessInquiries = mainView.findViewById(R.id.tvBusinessInquiries);
            //tvBusinessInquiries.setTypeface(robotoMedium);

            tvBusinessInquiries.setOnClickListener(v -> {

                Intent i = new Intent(getActivity(), BusinessEnquiryActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });

            tvBusinessCalls.setOnClickListener(v -> {

                Intent i = new Intent(getActivity(), VmnCallCardsActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });

            tvPaymentSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MixPanelController.track(EventKeysWL.SIDE_PANEL_PAYMENT_SETTING, null);
                    Intent i = new Intent(getActivity(), PaymentSettingsActivity.class);
                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
            tvProductGallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MixPanelController.track(EventKeysWL.SIDE_PANEL_PRODUCT_GALLERY, null);
                    //Intent i = new Intent(getActivity(), ProductGalleryActivity.class);
                    Intent i = new Intent(getActivity(), ProductCatalogActivity.class);
                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });

            tvSellerAnalytics.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    startOrdersActivity();

//                    if(mIsAPEnabled) {
//                        MixPanelController.track(EventKeysWL.SIDE_PANEL_SELLER_ANALYTICS, null);
//                        Intent i = new Intent(getActivity(), SellerAnalyticsActivity.class);
//                        startActivity(i);
//                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                    }else {
//                        new AlertDialog.Builder(getActivity())
//                                .setMessage("Enable Assured Purchase to view Seller Analytics")
//                                .setPositiveButton("Payment Settings", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                        MixPanelController.track(EventKeysWL.SIDE_PANEL_PAYMENT_SETTING, null);
//                                        Intent i = new Intent(getActivity(), PaymentSettingsActivity.class);
//                                        startActivity(i);
//                                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                                    }
//                                })
//                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                    }
//                                })
//                                .show();
//
//                    }
                }
            });

            String category = session.getFPDetails(Key_Preferences.PRODUCT_CATEGORY);
            tvProductGallery.setText("Catalogue");

        } catch (Exception e) {
            e.printStackTrace();
        }

        /*if (Constants.PACKAGE_NAME.equals("com.biz2.nowfloats"))
        {
            tvPaymentSetting.setVisibility(View.VISIBLE);
            ivPaymentIcon.setVisibility(View.VISIBLE);
        }

        else
        {
            tvPaymentSetting.setVisibility(View.GONE);
            ivPaymentIcon.setVisibility(View.GONE);
        }*/
    }


    @Override
    public void onResume() {
        super.onResume();
        if (headerText != null)
            headerText.setText(Utils.getDefaultOrderROIType(category_code));
    }

    private void openSellerAnalyticsActivity() {
        Bundle bundle = new Bundle();
        PreferenceData data = new PreferenceData(Constants.clientId_ORDER, session.getUserProfileId(), Constants.WA_KEY, session.getFpTag());
        bundle.putSerializable(IntentConstant.PREFERENCE_DATA.name(), data);
        bundle.putString(IntentConstant.INVENTORY_TYPE.name(), session.getFP_AppExperienceCode());

        if (getAppointmentType(session.getFP_AppExperienceCode())) {
            startFragmentActivityNew(activity, FragmentType.ALL_BOOKING_VIEW, bundle, false);
        } else startFragmentActivityNew(activity, FragmentType.ALL_ORDER_VIEW, bundle, false);

//        MixPanelController.track(EventKeysWL.SIDE_PANEL_SELLER_ANALYTICS, null);
//        Intent i = new Intent(getActivity(), SellerAnalyticsActivity.class);
//        startActivity(i);
//        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private boolean getAppointmentType(String fp_appExperienceCode) {
        switch (fp_appExperienceCode) {
            case "SVC":
            case "DOC":
            case "HOS":
            case "SPA":
            case "SAL":
            case "EDU":
                return true;
            default:
                return false;
        }
    }

    private void startOrdersActivity() {
        if (!WidgetKey.isNewPricingPlan) openSellerAnalyticsActivity();
        else {
            String value = WidgetKey.getPropertyValue(WidgetKey.WIDGET_SHOPPING_CART, WidgetKey.WIDGET_PROPERTY_CART);
            if (value.equals(WidgetKey.WidgetValue.FEATURE_NOT_AVAILABLE.getValue())) {
                Toast.makeText(getContext(), getString(R.string.message_feature_not_available), Toast.LENGTH_LONG).show();
            } else openSellerAnalyticsActivity();
        }
    }
}
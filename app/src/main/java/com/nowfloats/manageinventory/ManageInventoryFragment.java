package com.nowfloats.manageinventory;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.inventoryorder.constant.FragmentType;
import com.inventoryorder.constant.IntentConstant;
import com.inventoryorder.model.PreferenceData;
import com.nowfloats.Analytics_Screen.OrderSummaryActivity;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.Restaurants.BookATable.BookATableActivity;
import com.nowfloats.manageinventory.models.MerchantProfileModel;
import com.nowfloats.manageinventory.models.WebActionModel;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Utils;
import com.nowfloats.util.WebEngageController;
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
    TextView tvTransactionType_1, tvTransactionType_2, orderAnalytics;
    ImageView ivLockWidget, ivPaymentIcon, lockIcon, bookTableIcon;

    LinearLayout bookTable;
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
        ivLockWidget.setVisibility(View.GONE);
        ivPaymentIcon = mainView.findViewById(R.id.secondrow_ImageView_ProfileV2);

        session = new UserSessionManager(getContext(), activity);
        pref = activity.getSharedPreferences(Constants.PREF_NAME, Activity.MODE_PRIVATE);
        prefsEditor = pref.edit();
        //getPaymentSettings();
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
                            Toast.makeText(getActivity(), getString(R.string.something_went_wrong_), Toast.LENGTH_SHORT).show();
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
                                throw new NullPointerException(getString(R.string.order_count_is_null));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public static int getExperienceType(String fp_appExperienceCode) {
        switch (fp_appExperienceCode) {
            case "SVC": /* TODO for Appointment (delivery mode offline)*/
            case "SPA":
            case "SAL":
            case "DOC": /* TODO for Appointment (delivery mode offline) && consultation (delivery mode online)*/
            case "HOS":
                return 1;
            case "HOT": /* TODO for booking */
                return 2;
            // coming order case "EDU":
            default: /* TODO for order */
                return 3;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (headerText != null)
            headerText.setText(Utils.getDefaultTrasactionsTaxonomyFromServiceCode(category_code));
    }

    private void openPrimaryTransactionTypeOrdes() {
        int experienceType = getExperienceType(session.getFP_AppExperienceCode());
        if (experienceType == 1) startFragmentActivityNew(activity, FragmentType.ALL_APPOINTMENT_VIEW, getBundleData(), false);
        else if (experienceType == 3) startFragmentActivityNew(activity, FragmentType.ALL_ORDER_VIEW, getBundleData(), false);
        else Toast.makeText(activity, "Coming soon..", Toast.LENGTH_SHORT).show();
//        MixPanelController.track(EventKeysWL.SIDE_PANEL_SELLER_ANALYTICS, null);
//        Intent i = new Intent(getActivity(), SellerAnalyticsActivity.class);
//        startActivity(i);
//        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private Bundle getBundleData() {
        Bundle bundle = new Bundle();
        String url = "";
        String rootAlisasURI = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI);
        String normalURI = "http://" + session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG).toLowerCase() + getString(R.string.tag_for_partners);
        if (rootAlisasURI != null && !rootAlisasURI.isEmpty()) url = rootAlisasURI;
        else url = normalURI;
        PreferenceData data = new PreferenceData(Constants.clientId_ORDER, session.getUserProfileId(),
                Constants.WA_KEY, session.getFpTag(), session.getUserPrimaryMobile(), url, session.getFPEmail(),
                session.getFPDetails(Key_Preferences.LATITUDE), session.getFPDetails(Key_Preferences.LONGITUDE),
                session.getFP_AppExperienceCode());
        bundle.putSerializable(IntentConstant.PREFERENCE_DATA.name(), data);
        bundle.putString(IntentConstant.EXPERIENCE_CODE.name(), session.getFP_AppExperienceCode());
        return bundle;
    }

    @Override
    public void onViewCreated(final View mainView, Bundle savedInstanceState) {
        super.onViewCreated(mainView, savedInstanceState);
        if (!isAdded()) return;
        final PorterDuffColorFilter whiteLabelFilter_pop_ip = new PorterDuffColorFilter(getResources()
                .getColor(R.color.white), PorterDuff.Mode.SRC_IN);


        try {
            ivLockWidget.setVisibility(View.GONE);

            String svc_code = session.getFP_AppExperienceCode();
            tvTransactionType_1 = mainView.findViewById(R.id.transactions_type_1);
            tvTransactionType_1.setText(Utils.getDefaultTrasactionsTaxonomyFromServiceCode(svc_code));
            if ("DOC".equalsIgnoreCase(svc_code) || "HOS".equalsIgnoreCase(svc_code))
                tvTransactionType_1.setText(R.string.appointments_at_clinic_camel_case);

            tvTransactionType_2 = mainView.findViewById(R.id.transactions_type_2);
            ImageView tranType2Image = mainView.findViewById(R.id.transactions_type_2_image);
            String secondTransactionType;
            if (Utils.isRoomBooking(svc_code))
                secondTransactionType = getString(R.string.orders);
            else
                secondTransactionType = Utils.getSecondTypeTrasactionsTaxonomyFromServiceCode(svc_code);
            tvTransactionType_2.setText(secondTransactionType);
            if (secondTransactionType.length() > 1) {
                tvTransactionType_2.setVisibility(View.VISIBLE);
                tranType2Image.setVisibility(View.VISIBLE);
                mainView.findViewById(R.id.line_view2).setVisibility(View.VISIBLE);
            } else {
                tvTransactionType_2.setVisibility(View.GONE);
                tranType2Image.setVisibility(View.GONE);
                mainView.findViewById(R.id.line_view2).setVisibility(View.GONE);
            }

            tvTransactionType_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startOrdersActivity();
                }
            });

            tvTransactionType_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Utils.isRoomBooking(svc_code)) {
                        startFragmentActivityNew(activity, FragmentType.ALL_ORDER_VIEW, getBundleData(), false);
                    } else {
                        startFragmentActivityNew(activity, FragmentType.ALL_VIDEO_CONSULT_VIEW, getBundleData(), false);
                    }
                }
            });

            orderAnalytics = mainView.findViewById(R.id.tvOrderSummary);
            orderAnalytics.setText(Utils.getOrderAnalyticsTaxonomyFromServiceCode(svc_code));
            orderAnalytics.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), OrderSummaryActivity.class);
                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });

            bookTable = mainView.findViewById(R.id.book_table);
            bookTableIcon = mainView.findViewById(R.id.book_a_table_icon);
            lockIcon = mainView.findViewById(R.id.feature_lock);
            View borderLine = mainView.findViewById(R.id.line_view3);
            if (svc_code.equals("CAF")) {
                bookTable.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(), BookATableActivity.class);
                        startActivity(i);
                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                });
                if (session.getStoreWidgets().contains("BOOKTABLE")) {
                    lockIcon.setVisibility(View.GONE);
                } else {
                    lockIcon.setVisibility(View.VISIBLE);
                }
            } else {
                bookTable.setVisibility(View.GONE);
                bookTableIcon.setVisibility(View.GONE);
                borderLine.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startOrdersActivity() {
        openPrimaryTransactionTypeOrdes();
//        if (!WidgetKey.isNewPricingPlan) openSellerAnalyticsActivity();
//        else {
//            String value = WidgetKey.getPropertyValue(WidgetKey.WIDGET_SHOPPING_CART, WidgetKey.WIDGET_PROPERTY_CART);
//            if (value.equals(WidgetKey.WidgetValue.FEATURE_NOT_AVAILABLE.getValue())) {
//                Toast.makeText(getContext(), getString(R.string.message_feature_not_available), Toast.LENGTH_LONG).show();
//            } else openSellerAnalyticsActivity();
//        }
    }
}
package com.nowfloats.manageinventory;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.Product_Gallery.ProductGalleryActivity;
import com.nowfloats.manageinventory.models.MerchantProfileModel;
import com.nowfloats.manageinventory.models.WebActionModel;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.nowfloats.NavigationDrawer.HomeActivity.headerText;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManageInventoryFragment extends Fragment {
    TextView tvPaymentSetting, tvProductGallery,tvTotalNoOfOrders,tvTotalRevenue, tvSellerAnalytics;
    ImageView ivLockWidget;
    Typeface robotoLight;
    private SharedPreferences pref = null;
    UserSessionManager session;
    SharedPreferences.Editor prefsEditor;
    private Activity activity;
    private boolean mIsAPEnabled = false;
    private String mTransactionCharge = "9%";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mainView = inflater.inflate(R.layout.fragment_manage_inventory, container, false);
        ivLockWidget = (ImageView) mainView.findViewById(R.id.lock_widget);

        session = new UserSessionManager(getContext(), activity);
        pref = activity.getSharedPreferences(Constants.PREF_NAME, Activity.MODE_PRIVATE);
        prefsEditor = pref.edit();
        getPaymentSettings();
        return mainView;
    }

    private void getPaymentSettings(){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(String.format(Constants.WA_BASE_URL+"merchant_profile3/get-data?query={merchant_id:'%s'}&limit=1", session.getFPID()))
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
                        if(getActivity() != null)
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

                            if (profile != null && profile.getData().size()>0) {
                                if(profile.getData().get(0).getPaymentType() == 0){
                                    mIsAPEnabled = true;
                                    ivLockWidget.setVisibility(View.GONE);
                                }
                                mTransactionCharge = profile.getData().get(0).getApplicableTxnCharge()+"%";
                            }else {
                                throw new NullPointerException("Orders Count is Null");
                            }
                        }catch (Exception e)
                        {
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

            Typeface robotoMedium = Typeface.createFromAsset(activity.getAssets(), "Roboto-Medium.ttf");
            robotoLight = Typeface.createFromAsset(activity.getAssets(), "Roboto-Light.ttf");

            tvPaymentSetting = (TextView) mainView.findViewById(R.id.tvPaymentSetting);
            tvPaymentSetting.setTypeface(robotoMedium);

            tvProductGallery = (TextView) mainView.findViewById(R.id.tvProductGallery);
            tvProductGallery.setTypeface(robotoMedium);

            tvSellerAnalytics = (TextView) mainView.findViewById(R.id.tvSellerAnalytics);
            tvSellerAnalytics.setTypeface(robotoMedium);

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
                    Intent i = new Intent(getActivity(), ProductGalleryActivity.class);
                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
            tvSellerAnalytics.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mIsAPEnabled) {
                        MixPanelController.track(EventKeysWL.SIDE_PANEL_SELLER_ANALYTICS, null);
                        Intent i = new Intent(getActivity(), SellerAnalyticsActivity.class);
                        startActivity(i);
                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }else {
                        new AlertDialog.Builder(getActivity())
                                .setMessage("Enable Assured Purchase to view Seller Analytics")
                                .setPositiveButton("Payment Settings", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        MixPanelController.track(EventKeysWL.SIDE_PANEL_PAYMENT_SETTING, null);
                                        Intent i = new Intent(getActivity(), PaymentSettingsActivity.class);
                                        startActivity(i);
                                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();

                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        if (headerText != null)
            headerText.setText(getResources().getString(R.string.manage_inventory));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (headerText != null)
        headerText.setText(Constants.StoreName);
    }

}

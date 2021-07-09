package com.nowfloats.Store;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.CustomWidget.MaterialProgressBar;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.NavigationDrawer.businessApps.BusinessAppsDetailsActivity;
import com.nowfloats.NavigationDrawer.businessApps.FragmentsFactoryActivity;
import com.nowfloats.Store.Service.StoreInterface;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.nowfloats.NavigationDrawer.businessApps.BusinessAppsFragment.BIZ_APP_DEMO;
import static com.nowfloats.NavigationDrawer.businessApps.BusinessAppsFragment.BIZ_APP_DEMO_REMOVE;
import static com.nowfloats.NavigationDrawer.businessApps.BusinessAppsFragment.BIZ_APP_PAID;

/**
 * Created by Admin on 29-01-2018.
 */

public class AddOnFragment extends Fragment {

    private Context mContext;
    private SharedPreferences pref;
    private TopUpPlansAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_top_up_plans, container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        pref = mContext.getSharedPreferences(Constants.PREF_NAME, Activity.MODE_PRIVATE);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isAdded() || isDetached()) return;

        RecyclerView mRecyclerView = view.findViewById(R.id.rv_upgrade);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TopUpPlansAdapter();
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mContext instanceof HomeActivity && HomeActivity.headerText != null) {
            HomeActivity.headerText.setText(getString(R.string.add_ons));
        }
    }

    private class TopUpPlansAdapter extends RecyclerView.Adapter<TopUpPlansAdapter.TopUpCardHolder> {

        private TopUpDialog topUpDialog;

        @Override
        public TopUpCardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.adapter_item_top_up, parent, false);
            return new TopUpPlansAdapter.TopUpCardHolder(view);
        }

        @Override
        public void onBindViewHolder(TopUpPlansAdapter.TopUpCardHolder holder, int position) {
            switch (position) {
                case 0:
                    holder.setCardHolderData(R.drawable.wild_fire_expire, getString(R.string.wildfire), getString(R.string.wildfire_definition));
                    break;
                case 1:
                    holder.setCardHolderData(R.drawable.ic_dictate_plan, getString(R.string.dictate), getString(R.string.dictate_definition));
                    break;
                case 2:
                    holder.setCardHolderData(R.drawable.ic_business_apps, getString(R.string.my_business_app), getString(R.string.business_app_definition));
                    break;
            }
        }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (topUpDialog != null)
                topUpDialog.onPaymentResultReceived(requestCode, resultCode, data);
        }

        @Override
        public int getItemCount() {
            return 3;
        }

        private void startDetails(int pos) {
            Intent intent = null;
            switch (pos) {
                case 0:
                    intent = new Intent(getContext(), FragmentsFactoryActivity.class);
                    intent.putExtra("fragmentName", "WildFireFragment");
                    break;
                case 1:
                    intent = new Intent(getContext(), FragmentsFactoryActivity.class);
                    intent.putExtra("fragmentName", "DictateFragment");
                    break;
                case 2:
                    startBusinessApp();
                    return;
            }
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

        private void startBusinessApp() {
            Intent i;
            SharedPreferences pref = getActivity().getSharedPreferences(Constants.PREF_NAME, Activity.MODE_PRIVATE);
            int businessAppStatus = pref.getInt(Key_Preferences.ABOUT_BUSINESS_APP, BIZ_APP_DEMO);
            if (businessAppStatus == BIZ_APP_DEMO) {
                i = new Intent(getContext(), FragmentsFactoryActivity.class);
                i.putExtra("fragmentName", "BusinessAppsFragment");
            } else {
                if (businessAppStatus == BIZ_APP_PAID) {
                    pref.edit().putInt(Key_Preferences.ABOUT_BUSINESS_APP, BIZ_APP_DEMO_REMOVE).apply();
                }
                i = new Intent(getContext(), BusinessAppsDetailsActivity.class);
            }
            startActivity(i);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

        class TopUpCardHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private TextView descriptionTv, typeTv;
            private ImageView topUpImage;

            TopUpCardHolder(View itemView) {
                super(itemView);
                topUpImage = itemView.findViewById(R.id.img_top_up);
                descriptionTv = itemView.findViewById(R.id.tv_top_up_description);
                itemView.findViewById(R.id.tv_top_up_detail).setOnClickListener(this);
                itemView.findViewById(R.id.tv_top_up_pricing).setOnClickListener(this);
                typeTv = itemView.findViewById(R.id.tv_top_up_type);
            }

            private void setCardHolderData(int resId, String title, String description) {
                descriptionTv.setText(Methods.fromHtml(description));
                typeTv.setText(title);
                topUpImage.setImageResource(resId);
            }

            private void sendEmailRequestForBizApp() {
                final SharedPreferences pref = getActivity().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
                UserSessionManager session = new UserSessionManager(getContext(), requireActivity());
                if (!pref.getBoolean(Key_Preferences.BUSINESS_APP_REQUESTED, false)) {
                    MaterialProgressBar.startProgressBar(getActivity(), getString(R.string.submiting_request), false);

                    Map<String, String> params = new HashMap<>();
                    params.put("clientId", session.getSourceClientId());
                    params.put("planType", "BizApps");
                    params.put("toEmail", getString(R.string.email_id_to_request_plans));
                    StoreInterface storeInterface = Constants.restAdapter.create(StoreInterface.class);
                    storeInterface.requestWidget(session.getFPID(), params, new Callback<String>() {
                        @Override
                        public void success(String s, Response response) {
                            MaterialProgressBar.dismissProgressBar();
                            if (response.getStatus() != 200) {
                                Methods.showSnackBarNegative(getActivity(), getString(R.string.something_went_wrong_try_again));
                                return;
                            }
                            pref.edit().putBoolean(Key_Preferences.BUSINESS_APP_REQUESTED, true).apply();
                            MixPanelController.track(MixPanelController.BUSINESS_APP_INTRESTED, null);

                            showBizAppDialog();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            MaterialProgressBar.dismissProgressBar();
                            Methods.showSnackBarNegative(getActivity(), getString(R.string.something_went_wrong_try_again));
                        }
                    });

                } else {
                    showBizAppDialog();
                }
            }

            private void showBizAppDialog() {
                new MaterialDialog.Builder(getContext())
                        .title(getString(R.string.thank_you_for_your_interest))
                        .content(getString(R.string.business_app_requested_success))
                        .negativeText(getString(R.string.ok))
                        .negativeColorRes(R.color.primary_color)
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        }).show();
            }

            @Override
            public void onClick(View view) {
                int id = view.getId();
                if (id == R.id.tv_top_up_detail) {
                    startDetails(getAdapterPosition());
                } else if (id == R.id.tv_top_up_pricing) {
                    if (topUpDialog == null)
                        topUpDialog = new TopUpDialog(requireActivity());

                    switch (getAdapterPosition()) {
                        case 0:
                            topUpDialog.getTopUpPricing(TopUpDialog.TopUpType.WildFire.name());
                            break;
                        case 1:
                            topUpDialog.getTopUpPricing(TopUpDialog.TopUpType.Dictate.name());
                            break;
                        case 2:
                            sendEmailRequestForBizApp();
                            break;
                    }
                }
            }
        }
    }

}

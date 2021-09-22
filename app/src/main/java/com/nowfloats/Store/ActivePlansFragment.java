package com.nowfloats.Store;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nowfloats.Store.Adapters.ActivePlansRvAdapter;
import com.nowfloats.Store.Adapters.TopUpDialogRvAdapter;
import com.nowfloats.Store.Model.ActivePackage;
import com.nowfloats.Store.Model.PackageDetails;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.thinksity.BuildConfig;
import com.thinksity.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by NowFloats on 16-10-2017.
 */

public class ActivePlansFragment extends Fragment implements ActivePlansRvAdapter.OnItemClickInterface {

    private final int DIRECT_REQUEST_CODE = 2013;
    CardView cvUpgradePlan;
    CardView cvActivePlans;
    ProgressBar pbActivePlans;
    TextView tvUpgradePlanText, plansHeaderText;
    RecyclerView rvActivePlans;
    private ActivePlansCallback mActivePlansCallback;
    private List<ActivePackage> mActivePlans = new ArrayList<>();
    private List<ActivePackage> mExpiredPlans = new ArrayList<>();
    private List<PackageDetails> mTopUps = new ArrayList<>();
    private List<String> mTopUpPlanNames = new ArrayList<>();
    private List<String> mPackageIds;
    private double mTotalAmount = 0;
    private ActivePlansRvAdapter mPlansRvAdapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ActivePlansCallback) {
            mActivePlansCallback = (ActivePlansCallback) activity;
        } else {
            throw new RuntimeException("Implement ActivePlansCallback iin the Activity");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_active_plans, container, false);

        cvUpgradePlan = (CardView) view.findViewById(R.id.cv_upgrade_plan);
        cvActivePlans = (CardView) view.findViewById(R.id.cv_curr_active_plans);
        pbActivePlans = (ProgressBar) view.findViewById(R.id.pb_active_plans);
        tvUpgradePlanText = (TextView) view.findViewById(R.id.tv_upgrade_plan_text);
        plansHeaderText = (TextView) view.findViewById(R.id.plans_header_text);
        rvActivePlans = (RecyclerView) view.findViewById(R.id.rv_active_plan_details);

        pbActivePlans.setVisibility(View.VISIBLE);

        mPlansRvAdapter = new ActivePlansRvAdapter(requireActivity());
        rvActivePlans.setAdapter(mPlansRvAdapter);
        rvActivePlans.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        rvActivePlans.addItemDecoration(decoration);
        mPlansRvAdapter.setActivePackages(mActivePlans);
        mPlansRvAdapter.setOnItemClickInterface(this);

        view.findViewById(R.id.rl_upgrade).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mActivePlansCallback != null) {
                    mActivePlansCallback.onRenewOrUpdate();
                }
            }
        });

        return view;
    }

    public void setActivePlans(List<ActivePackage> activePlans) {
        this.mActivePlans = activePlans;
    }

    public void showActivePlans() {

        if (!isAdded() || isDetached()) {
            return;
        }
        pbActivePlans.setVisibility(View.INVISIBLE);
        if (mActivePlans != null && mActivePlans.size() > 0) {
            if (mActivePlans.size() == 1 && mActivePlans.get(0).getName().toLowerCase().contains("demo")) {
                cvActivePlans.setVisibility(View.GONE);
                DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                String date = df.format(getExpiryDate(mActivePlans.get(0).getToBeActivatedOn(),
                        mActivePlans.get(0).getTotalMonthsValidity()));
                tvUpgradePlanText.setText(getString(R.string.your) + mActivePlans.get(0).getTotalMonthsValidity()
                        + getString(R.string.month_boost_demo_package_will_expire_on) + date
                        + getString(R.string.to_continue_with_the_service));
                cvUpgradePlan.setVisibility(View.VISIBLE);
            } else {
                cvUpgradePlan.setVisibility(View.GONE);
                cvActivePlans.setVisibility(View.VISIBLE);
                plansHeaderText.setText(R.string.plan_in_use);
                mPlansRvAdapter.setActivePackages(mActivePlans);
            }
        } else {
            cvActivePlans.setVisibility(View.GONE);
            tvUpgradePlanText.setText(R.string.currently_you_dont_have_any_active_packages);
            cvUpgradePlan.setVisibility(View.VISIBLE);
        }
    }

    private Date getExpiryDate(String toBeActivatedOn, double totalMonthsValidity) {
        long time = Long.parseLong(toBeActivatedOn.replaceAll("[^\\d]", ""));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.add(Calendar.MONTH, (int) Math.floor(totalMonthsValidity));
        calendar.add(Calendar.DATE, (int) ((totalMonthsValidity - Math.floor(totalMonthsValidity)) * 30));
        return calendar.getTime();
    }

    public void setExpiredPlans(List<ActivePackage> expiredPlans) {
        this.mExpiredPlans = expiredPlans;
    }

    public void showExpiredPlans() {

        if (!isAdded() || isDetached()) {
            return;
        }
        pbActivePlans.setVisibility(View.INVISIBLE);
        if (mExpiredPlans != null && mExpiredPlans.size() > 0) {
            cvUpgradePlan.setVisibility(View.GONE);
            cvActivePlans.setVisibility(View.VISIBLE);
            plansHeaderText.setText("PLANS ARCHIVE");
            mPlansRvAdapter.setExpiredPlans(mExpiredPlans);
        } else {
            cvActivePlans.setVisibility(View.GONE);
            tvUpgradePlanText.setText(R.string.currently_you_dont_have_any_archived_packages);
            cvUpgradePlan.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRenewOrUpdate() {
        if (mActivePlansCallback != null) {
            mActivePlansCallback.onRenewOrUpdate();
        }
    }

    @Override
    public void onAddFeatures() {
        showTopUpPlans();
    }

    private void showTopUpPlans() {
        if (mTopUps == null || mTopUps.size() == 0) {
            Methods.showSnackBarNegative(getActivity(), getString(R.string.top_up_plan_is_not_available));
            return;
        }
        final View topUpDialogView = LayoutInflater.from(requireActivity()).inflate(R.layout.top_up_dialog_layout, null);
        final AlertDialog dialog = new AlertDialog.Builder(requireActivity())
                .setView(topUpDialogView)
                .show();
        RecyclerView rvTopUpPlans = (RecyclerView) topUpDialogView.findViewById(R.id.rv_top_plans);
        final TextView tvAmount = (TextView) topUpDialogView.findViewById(R.id.tv_price);
        final TextView tvCurrency = (TextView) topUpDialogView.findViewById(R.id.tv_currency);
        tvCurrency.setText(mTopUps.get(0).getCurrencyCode());
        rvTopUpPlans.setAdapter(new TopUpDialogRvAdapter(mTopUps, new TopUpDialogRvAdapter.UpdateAmountListener() {
            @Override
            public void onAmountUpdated(double amount, List<String> packageIds) {
                mTotalAmount = amount;
                mPackageIds = packageIds;
                tvAmount.setText(amount + "");
            }
        }));
        rvTopUpPlans.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        topUpDialogView.findViewById(R.id.ll_purchase_plan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPackageIds == null || mPackageIds.size() == 0) {
                    return;
                }
                dialog.dismiss();
                MixPanelController.track(EventKeysWL.BUY_NOW_STORE_CLICKED, null);
                Intent i = new Intent(getActivity(), BuildConfig.APPLICATION_ID.equals("com.biz2.nowfloats") ? ProductCheckout_v2Activity.class : ProductCheckoutActivity.class);
                i.putExtra("package_ids", mPackageIds.toArray(new String[mPackageIds.size()]));
                startActivityForResult(i, DIRECT_REQUEST_CODE);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                mPackageIds.clear();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mPackageIds != null && mPackageIds.size() > 0)
                    mPackageIds.clear();
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (mPackageIds != null && mPackageIds.size() > 0)
                    mPackageIds.clear();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //Log.d("CurrentFragmentsNum", getActivity().getSupportFragmentManager().getFragments().size()+"");
    }

    public void setTopUps(List<PackageDetails> topUps) {
        this.mTopUps = topUps;
        mTopUpPlanNames.clear();
        for (PackageDetails topUp : topUps) {
            mTopUpPlanNames.add(topUp.getName());
        }
    }

    public enum TopUpTypes {
        WILDFIRE_GOOGLE,
        WILDFIRE_FACEBOOK,
        BIZAPP_ANDROID,
        BIZAPP_IPHONE,
        DICTATE
    }

    public interface ActivePlansCallback {
        public void onRenewOrUpdate();
    }
}

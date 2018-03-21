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

import com.nowfloats.Store.Adapters.PricingPlansPagerAdapter;
import com.nowfloats.Store.Model.PackageDetails;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

import java.util.List;

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
        tabLayout.setupWithViewPager(vpPricingPlans, true);
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

        return view;
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

package com.nowfloats.Store;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.nowfloats.Store.Adapters.PricingPlansPagerAdapter;
import com.thinksity.R;

public class NewPricingPlansActivity extends AppCompatActivity {

    ViewPager vpPricingPlans;
    PricingPlansPagerAdapter pricingPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_pricing_plans);

        vpPricingPlans = (ViewPager) findViewById(R.id.vp_pricing_plans);
        pricingPagerAdapter = new PricingPlansPagerAdapter(getSupportFragmentManager());
        vpPricingPlans.setAdapter(pricingPagerAdapter);
    }
}

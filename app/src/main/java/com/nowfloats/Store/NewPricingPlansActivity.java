package com.nowfloats.Store;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.Store.Adapters.PricingPlansPagerAdapter;
import com.nowfloats.util.Key_Preferences;
import com.thinksity.R;

public class NewPricingPlansActivity extends AppCompatActivity {

    ViewPager vpPricingPlans;
    PricingPlansPagerAdapter pricingPagerAdapter;
    TextView tvCategory;
    UserSessionManager mSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_pricing_plans);

        mSession = new UserSessionManager(this, this);

        vpPricingPlans = (ViewPager) findViewById(R.id.vp_pricing_plans);
        vpPricingPlans.setClipToPadding(false);
        vpPricingPlans.setPageMargin(24);
        vpPricingPlans.setPadding(68, 8, 68, 8);
        vpPricingPlans.setOffscreenPageLimit(3);
        pricingPagerAdapter = new PricingPlansPagerAdapter(getSupportFragmentManager());
        vpPricingPlans.setAdapter(pricingPagerAdapter);

        tvCategory = (TextView) findViewById(R.id.tv_category);
        tvCategory.setText(mSession.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY));

        /*findViewById(R.id.ll_select_category).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
    }
}

package com.nowfloats.Store.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.nowfloats.Store.PricingDetailsFragment;

/**
 * Created by NowFloats on 20-09-2017.
 */

public class PricingPlansPagerAdapter extends FragmentPagerAdapter {

    public PricingPlansPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return PricingDetailsFragment.newInstance();
    }

    @Override
    public int getCount() {
        return 4;
    }

}

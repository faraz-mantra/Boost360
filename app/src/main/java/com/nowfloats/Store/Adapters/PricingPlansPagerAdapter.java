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
        switch (position){
            case 0:
                return PricingDetailsFragment.newInstance(PricingDetailsFragment.PricingType.BOOST_LITE);
            case 1:
                return PricingDetailsFragment.newInstance(PricingDetailsFragment.PricingType.BOOST_PRO);
            case 2:
                return PricingDetailsFragment.newInstance(PricingDetailsFragment.PricingType.BOOST_CUSTOM);
            default:
                return PricingDetailsFragment.newInstance(PricingDetailsFragment.PricingType.BOOST_PRO);

        }

    }

    @Override
    public int getCount() {
        return 3;
    }

}

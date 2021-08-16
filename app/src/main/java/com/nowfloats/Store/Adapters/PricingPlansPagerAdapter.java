package com.nowfloats.Store.Adapters;

import android.os.Parcelable;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.nowfloats.Store.Model.PackageDetails;
import com.nowfloats.Store.PricingDetailsFragment;

import java.util.List;

/**
 * Created by NowFloats on 20-09-2017.
 */

public class PricingPlansPagerAdapter extends FragmentStatePagerAdapter {

    List<PackageDetails> mBasePackages, mTopUps;

    public PricingPlansPagerAdapter(FragmentManager fm, List<PackageDetails> basePackages, List<PackageDetails> topUps) {
        super(fm);

        this.mBasePackages = basePackages;
        this.mTopUps = topUps;
    }

    @Override
    public Fragment getItem(int position) {
        return PricingDetailsFragment.newInstance(mBasePackages.get(position), mTopUps);
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public int getCount() {
        return mBasePackages == null ? 0 : mBasePackages.size();
    }


}

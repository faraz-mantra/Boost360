package com.nowfloats.Analytics_Screen.Graph;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.thinksity.R;

/**
 * Created by tushar on 18-05-2015.
 */
public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private Context context;
    private ViewPager viewPager;
    private String[] tabTitles;

    public SampleFragmentPagerAdapter(FragmentManager fm,Context context,ViewPager viewPager) {
        super(fm);
        this.context = context;
        this.viewPager = viewPager;
        tabTitles = context.getResources().getStringArray(R.array.calendar);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        return PageFragment.newInstance(position + 1,context,viewPager);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
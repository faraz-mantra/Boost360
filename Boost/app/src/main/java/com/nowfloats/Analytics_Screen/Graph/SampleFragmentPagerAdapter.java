package com.nowfloats.Analytics_Screen.Graph;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

/**
 * Created by tushar on 18-05-2015.
 */
public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { "Week", "Month", "Year" };
    private Context context;
    private ViewPager viewPager;


    public SampleFragmentPagerAdapter(FragmentManager fm,Context context,ViewPager viewPager) {
        super(fm);
        this.context = context;
        this.viewPager = viewPager;
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
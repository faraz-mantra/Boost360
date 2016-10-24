package com.nowfloats.NavigationDrawer;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.nowfloats.NotificationCenter.NotificationFragment;
import com.thinksity.R;

public class TabPagerAdapter extends FragmentPagerAdapter  {
    Home_Main_Fragment homeFragment;
    NotificationFragment alertFragment;
    Analytics_Fragment analyticsFragment ;
    Context appContext;
    int currentItem ;
    private FragmentManager mFragmentManager;

    CharSequence Titles[]=appContext.getResources().getStringArray(R.array.dashboard_tabs);
    public TabPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        Log.d("Tap Pager Adapter"," Tab Pager Adapter ");
        homeFragment = new Home_Main_Fragment();
        alertFragment = new NotificationFragment();
        analyticsFragment = new Analytics_Fragment();
        appContext = context ;
        mFragmentManager = fm;
    }

    private static String makeFragmentName(int viewId, int index) {
        return "android:switcher:" + viewId + ":" + index;
    }

    @Override
    public Fragment getItem(int index) {
        currentItem = index;
        // String name = makeFragmentName(R.id.pager, index);
        // Fragment selectedFragment = mFragmentManager.findFragmentByTag(name);
        Log.d("Index","Index : "+index);
        Fragment selectedFragment = null;
        // if(selectedFragment == null) {
        switch (index) {
            case 0:
                selectedFragment = homeFragment.newInstance();
                currentItem = 0;
                break;
            case 1:
                selectedFragment = analyticsFragment;
                currentItem = 1;
                break;
            case 2:
                selectedFragment = alertFragment;
                currentItem = 2;
                break;
        }
        //}
        //Log.d("Selected Fragment ","Selected Fragment : "+selectedFragment.getTag());
        return selectedFragment;
    }


    @Override
    public int getItemPosition(Object object) {
        //Log.d("TabPagerAdapter","getItemPosition : ");
        return super.getItemPosition(object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }



    @Override
    public int getCount() {
        // Log.d("TabPagerAdapter","getCount ");
        return 3;
    }

}
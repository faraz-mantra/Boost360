package com.nowfloats.Store.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.nowfloats.NavigationDrawer.Analytics_Fragment;
import com.nowfloats.NavigationDrawer.Home_Main_Fragment;
import com.nowfloats.NotificationCenter.NotificationFragment;
import com.nowfloats.Store.AccountDetailsFragment;
import com.nowfloats.Store.Store_Fragment;

public class StorePagerAdapter extends FragmentPagerAdapter  {
    Context appContext;
    int currentItem ;
    private FragmentManager mFragmentManager;

    CharSequence Titles[]={"ADDITIONAL PLANS", "ACTIVE PLANS"};
    public StorePagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        Log.d("STORE Pager Adapter"," STORE Pager Adapter ");
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
            case 1:
                selectedFragment = new AccountDetailsFragment();
                currentItem = 0;
                break;
            case 0:
                selectedFragment = Store_Fragment.newInstance("inactive");
                currentItem = 1;
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
        return 2;
    }
}
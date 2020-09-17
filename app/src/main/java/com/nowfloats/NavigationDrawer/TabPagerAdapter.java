package com.nowfloats.NavigationDrawer;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.nowfloats.NotificationCenter.NotificationFragment;
import com.nowfloats.util.WebEngageController;
import com.thinksity.R;

public class TabPagerAdapter extends FragmentPagerAdapter {
    Home_Main_Fragment homeFragment;
    Analytics_Fragment analyticsFragment ;
    Context appContext;
    private int currentItem ;
    private FragmentManager mFragmentManager;

    private CharSequence mTitles[];
    public TabPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        //Log.d("Tap Pager Adapter"," Tab Pager Adapter ");

        appContext = context ;
        mTitles = appContext.getResources().getStringArray(R.array.dashboard_tabs);
    }

    private static String makeFragmentName(int viewId, int index) {
        return "android:switcher:" + viewId + ":" + index;
    }

    @Override
    public Fragment getItem(int index) {
        currentItem = index;
        // String name = makeFragmentName(R.id.pager, index);
        // Fragment selectedFragment = mFragmentManager.findFragmentByTag(name);
        //Log.d("Index","Index : "+index);
        Fragment selectedFragment = null;
        // if(selectedFragment == null) {
        switch (index) {
            case 0:
                selectedFragment  = new Home_Main_Fragment();
                currentItem = 0;
                break;
            case 1:
                selectedFragment =  new Analytics_Fragment();
                /*selectedFragment = OffersFragment.newInstance();*/
                currentItem = 1;
                break;
            case 2:
                selectedFragment = new NotificationFragment();
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
        return mTitles[position];
    }



    @Override
    public int getCount() {
        // Log.d("TabPagerAdapter","getCount ");
        return 3;
    }


    public int getCurrentItem()
    {
        return this.currentItem;
    }




}
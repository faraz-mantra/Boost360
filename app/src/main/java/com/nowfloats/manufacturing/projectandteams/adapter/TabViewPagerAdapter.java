package com.nowfloats.manufacturing.projectandteams.adapter;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.nowfloats.manufacturing.projectandteams.ui.projectandteams.ProjectCategoryFragment;
import com.nowfloats.manufacturing.projectandteams.ui.projectandteams.TeamCategoryFragment;
import com.thinksity.R;

public class TabViewPagerAdapter extends FragmentPagerAdapter {

    Context appContext;
    private int currentItem ;

    private CharSequence mTitles[];
    public TabViewPagerAdapter(FragmentManager fm, Context context) {
        super(fm);

        appContext = context ;
        mTitles = appContext.getResources().getStringArray(R.array.projectandterms_tabs);
    }

    private static String makeFragmentName(int viewId, int index) {
        return "android:switcher:" + viewId + ":" + index;
    }

    @Override
    public Fragment getItem(int index) {
        currentItem = index;
        Fragment selectedFragment = null;
        switch (index) {
            case 0:
                selectedFragment  = new ProjectCategoryFragment();
                currentItem = 0;
                break;
            case 1:
                selectedFragment =  new TeamCategoryFragment();
                currentItem = 1;
                break;
            }
        return selectedFragment;
    }


    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }



    @Override
    public int getCount() {
        return 2;
    }


    public int getCurrentItem()
    {
        return this.currentItem;
    }

}
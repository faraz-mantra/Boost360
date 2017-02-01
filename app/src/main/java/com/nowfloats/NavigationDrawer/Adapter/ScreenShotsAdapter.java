package com.nowfloats.NavigationDrawer.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.nowfloats.NavigationDrawer.businessApps.ImageViewFragment;

import java.util.ArrayList;

/**
 * Created by Admin on 01-02-2017.
 */

public class ScreenShotsAdapter extends FragmentStatePagerAdapter {
    public ArrayList<String> list;

    public ScreenShotsAdapter(FragmentManager fm, ArrayList<String> list) {
        super(fm);
        this.list = list;
    }
    @Override
    public Fragment getItem(int position) {
        return ImageViewFragment.getInstance(list.get(position));
    }

    @Override
    public int getCount() {
        return list.size();
    }

}


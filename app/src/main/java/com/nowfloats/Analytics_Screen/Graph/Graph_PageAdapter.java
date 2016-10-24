package com.nowfloats.Analytics_Screen.Graph;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import java.util.ArrayList;

public class Graph_PageAdapter extends FragmentStatePagerAdapter {
    Context context;
    ArrayList<String> pages;




    public Graph_PageAdapter(android.support.v4.app.FragmentManager fm, Context context, ArrayList<String> pages) {
        super(fm);
        // TODO Auto-generated constructor stub
        this.context=context;
        this.pages=pages;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // TODO Auto-generated method stub
        return pages.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        // TODO Auto-generated method stub
        return ArrayListFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return pages.size();
    }



    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // TODO Auto-generated method stub

        android.support.v4.app.FragmentManager manager = ((Fragment) object).getFragmentManager();
        FragmentTransaction trans = manager.beginTransaction();
        trans.remove((Fragment) object);
        trans.commit();

        super.destroyItem(container, position, object);
    }
}
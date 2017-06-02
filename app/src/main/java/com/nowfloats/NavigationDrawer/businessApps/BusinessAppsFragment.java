package com.nowfloats.NavigationDrawer.businessApps;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.thinksity.R;
import com.viewpagerindicator.CirclePageIndicator;


/**
 * Created by Abhi on 12/12/2016.
 */

public class BusinessAppsFragment extends Fragment {
    ViewPager mPager;
    CirclePageIndicator mIndicator;
    viewPagerAdapter mAdapter;
    Button mButton;
    Context context;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(savedInstanceState!=null)
        {
            Log.d("ggg","view pager not null");
        }
        return inflater.inflate(R.layout.fragment_business_apps,container,false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setHasOptionsMenu(true);
        this.context=context;
    }

    @Override
    public void onResume() {
        super.onResume();
        if( HomeActivity.headerText!=null)
        HomeActivity.headerText.setText(getString(R.string.business_apps));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mButton= (Button) view.findViewById(R.id.customer_apps_get_store_link_button);
        mAdapter = new viewPagerAdapter(getChildFragmentManager());
        mPager = (ViewPager) view.findViewById(R.id.ps_pager);
       // mPager.setClipToPadding(false);
        // set padding manually, the more you set the padding the more you see of prev & next page
        mPager.setPadding(30, 30, 30, 0);
        // sets a margin b/w individual pages to ensure that there is a gap b/w them
        mPager.setPageMargin(30);

        mPager.setAdapter(mAdapter);
        mIndicator = (CirclePageIndicator) view.findViewById(R.id.ps_indicator);
        mIndicator.setViewPager(mPager);
        mIndicator.setStrokeColor(R.color.black_translucent);
        mIndicator.setFillColor(R.color.dark_black_color);
//        mIndicator.setRadius(5);
        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch(position) {
                    case 3:
                        mButton.setText("GO AHEAD");
                        break;
                    default:
                        mButton.setText("NEXT");
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mPager.getCurrentItem()<3){
                    mPager.setCurrentItem(mPager.getCurrentItem()+1,true);
                }else if(Methods.isOnline(getActivity()))
                {
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
                    int siteMeter = Integer.parseInt(sharedPreferences.getString("sitescore", "0"));

                    Intent i=new Intent(context,BusinessAppsActivity.class);
                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });
    }

    private class viewPagerAdapter extends FragmentStatePagerAdapter {

        viewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return BusinessAppScreenOneFragment.getInstance(position);
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.business_app_main_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.skip:
                if(Methods.isOnline(getActivity())) {
                    Intent i = new Intent(context, BusinessAppsActivity.class);
                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

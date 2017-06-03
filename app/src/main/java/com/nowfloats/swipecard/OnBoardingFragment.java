package com.nowfloats.swipecard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thinksity.R;
import com.viewpagerindicator.CirclePageIndicator;


/**
 * Created by admin on 5/31/2017.
 */

public class OnBoardingFragment extends Fragment {


    private ViewPager ps_pager;

    private CirclePageIndicator ps_indicator;

    private LayoutInflater mLayoutInflater;

    private TextView tvGetStarted;

    public OnBoardingFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.csp_fragment_on_boarding, null);
        this.mLayoutInflater = inflater;
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ps_pager = (ViewPager) view.findViewById(R.id.ps_pager);
        ps_indicator = (CirclePageIndicator) view.findViewById(R.id.ps_indicator);
        tvGetStarted = (TextView) view.findViewById(R.id.tvGetStarted);
        ps_pager.setAdapter(new BoardingPagerAdapter());
        ps_indicator.setViewPager(ps_pager);

        ((LinearLayout) view.findViewById(R.id.llGetStarted))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (ps_pager.getCurrentItem() == 2) {
                            ((SuggestionsActivity) getActivity()).switchView(SuggestionsActivity.SwitchView.ACTION_ITEMS);
                        } else {
                            ps_pager.setCurrentItem(ps_pager.getCurrentItem() + 1);
                        }
                    }
                });

        ps_pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 2) {
                    tvGetStarted.setText("GET STARTED");
                } else {
                    tvGetStarted.setText("NEXT");
                }
                ps_indicator.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private class BoardingPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            View view = null;
            switch (position) {
                case 0:
                    view = mLayoutInflater.inflate(R.layout.csp_on_board_1, container, false);
                    break;
                case 1:
                    view = mLayoutInflater.inflate(R.layout.csp_on_board_2, container, false);
                    break;
                case 2:
                    view = mLayoutInflater.inflate(R.layout.csp_on_board_3, container, false);
                    break;
            }
            container.addView(view);

            return view;
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }
}

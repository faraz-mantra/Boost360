package com.nowfloats.manufacturing.projectandteams.ui.projectandteams;

import androidx.core.content.ContextCompat;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.SlidingTabLayout;
import com.nowfloats.manufacturing.projectandteams.adapter.TabViewPagerAdapter;
import com.nowfloats.util.Key_Preferences;
import com.thinksity.R;

public class ProjectAndTeamsFragment extends Fragment {

    ViewPager viewPager;
    SlidingTabLayout tabs;
    TabViewPagerAdapter tabPagerAdapter;
    UserSessionManager session;

    public static ProjectAndTeamsFragment newInstance() {
        return new ProjectAndTeamsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.project_and_teams_fragment, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        session = new UserSessionManager(requireActivity().getApplicationContext(), requireActivity());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabs = view.findViewById(R.id.tabs);
        viewPager = view.findViewById(R.id.TabViewpager);
        tabPagerAdapter = new TabViewPagerAdapter(getChildFragmentManager(), requireActivity());


        viewPager.setAdapter(tabPagerAdapter);
        try {
            requireActivity().setTitle(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));
        } catch (Exception e) {
            e.printStackTrace();
        }
        tabs.setDistributeEvenly(true);
        tabs.setCustomTabView(R.layout.tab_text, R.id.tab_textview);
        //((ViewGroup)tabs.getChildAt(0)).getChildAt(1).setVisibility(View.VISIBLE);
//                        tabs.setSelectedIndicatorColors(getResources().getColor(R.color.white));
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return ContextCompat.getColor(getContext(), R.color.white);
            }
        });
        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(viewPager, ContextCompat.getColorStateList(getActivity(), R.color.selector));

    }
}
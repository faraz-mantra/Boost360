package com.nowfloats.manufacturing.projectandteams.ui.projectandteams;

import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.SlidingTabLayout;
import com.nowfloats.manufacturing.projectandteams.ProjectAndTermsActivity;
import com.nowfloats.manufacturing.projectandteams.adapter.TabViewPagerAdapter;
import com.nowfloats.manufacturing.projectandteams.ui.projectandteamsdetails.ProjectDetailsFragment;
import com.nowfloats.manufacturing.projectandteams.ui.projectandteamsdetails.TeamsDetailsFragment;
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

        //set title and menu option
        setHeader(view);


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

    public void setHeader(View view){
        LinearLayout rightButton,backButton;
        ImageView rightIcon;
        TextView title;

        title = view.findViewById(R.id.title);
        backButton = view.findViewById(R.id.back_button);
        rightButton = view.findViewById(R.id.right_icon_layout);
        rightIcon = view.findViewById(R.id.right_icon);
        title.setText("Projects & Teams");
        rightIcon.setImageResource(R.drawable.ic_dot_menu_white);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });
    }

    // custom method
    private void showPopup(final View view) {
        Context wrapper = new ContextThemeWrapper(requireContext(), R.style.MyPopupMenuStyleWhite);
        PopupMenu popupMenu = new PopupMenu(wrapper, view);
        popupMenu.getMenu().add(0, 0, Menu.NONE, "Add Project");
        popupMenu.getMenu().add(0, 1, Menu.NONE, "Add Team");
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getTitle().equals("Add Project")){
                    ((ProjectAndTermsActivity)requireActivity()).addFragment(new ProjectDetailsFragment(),"PROJECT_DETAILS_FRAGMENT");
                }else if(item.getTitle().equals("Add Team")){
                    ((ProjectAndTermsActivity)requireActivity()).addFragment(new TeamsDetailsFragment(),"TEAM_DETAILS_FRAGMENT");
                }
                return true;
            }
        });
        popupMenu.show();
    }
}
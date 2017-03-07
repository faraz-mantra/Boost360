package com.nowfloats.NavigationDrawer;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.model.AlertCountEvent;
import com.nowfloats.NavigationDrawer.model.RiaCardModel;
import com.nowfloats.NotificationCenter.NotificationFragment;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.thinksity.R;

import java.util.ArrayList;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class Home_Fragment_Tab extends Fragment {
    public static ViewPager viewPager = null;
    TabPagerAdapter tabPagerAdapter;
    SlidingTabLayout tabs;
    UserSessionManager session;
    TextView alertCountTv;
    public static String alertCountVal = "0";
    private Bus bus;
    public Activity activity;
    LinearLayout progressLayout;
    private MaterialDialog materialDialog;

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
        if (viewPager!=null){
            if(Constants.createMsg){
                viewPager.setCurrentItem(0);
                if(Home_Main_Fragment.progressBar!=null)
                    Home_Main_Fragment.progressBar.setVisibility(View.VISIBLE);
                Constants.createMsg = false;
            }else{
                if(Home_Main_Fragment.progressBar!=null)
                    Home_Main_Fragment.progressBar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        session = new UserSessionManager(activity.getApplicationContext(),activity);
        bus = BusProvider.getInstance().getBus();
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                if(activity==null){activity = getActivity();}

            }
        }).start();*/
    }

    @Subscribe
    public void getalertCountEvent(AlertCountEvent ev){
        if (alertCountVal!=null && alertCountVal.trim().length()>0 && !alertCountVal.equals("0") && alertCountTv!=null){
            alertCountTv.setText(alertCountVal);
            alertCountTv.setVisibility(View.VISIBLE);
        }else if(alertCountTv!=null){
            alertCountTv.setText("0");
            alertCountTv.setVisibility(View.GONE);
            alertCountVal = "0";
        }else{
            alertCountVal = "0";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_home__fragment__tab, container, false);
        tabPagerAdapter = new TabPagerAdapter(getChildFragmentManager(), activity);
        return mainView ;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NotificationFragment.getAlertCount(session, Constants.alertInterface, bus);
        progressLayout = (LinearLayout)view.findViewById(R.id.progress_layout);
        progressLayout.setVisibility(View.VISIBLE);
        tabs = (SlidingTabLayout) view.findViewById(R.id.tabs);
        viewPager = (ViewPager) view.findViewById(R.id.homeTabViewpager);
        alertCountTv = (TextView)view.findViewById(R.id.alert_count_textview);
        alertCountTv.setVisibility(View.GONE);
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(Constants.createMsg){
                    v.getParent().requestDisallowInterceptTouchEvent(true);

                    try{
                        if (materialDialog!=null && materialDialog.isShowing()){
                            materialDialog.dismiss();
                        }
                        materialDialog = new MaterialDialog.Builder(activity)
                                .title(getString(R.string.update_not_done))
                                .content(getString(R.string.do_you_want_to_cancel))
                                .positiveText(getString(R.string.yes))
                                .negativeText(getString(R.string.no))
                                .positiveColorRes(R.color.primaryColor)
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        super.onPositive(dialog);
                                        try {
                                            Home_Main_Fragment.facebookPostCount = 0;
                                            Home_Main_Fragment.recentPostEvent = null;
                                            Home_Main_Fragment.progressBar.setVisibility(View.GONE);
                                            Home_Main_Fragment.retryLayout.setVisibility(View.GONE);
                                            Home_Main_Fragment.progressCrd.setVisibility(View.GONE);
                                            Constants.createMsg = false;
                                        }catch(Exception e){e.printStackTrace();}
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onNegative(MaterialDialog dialog) {
                                        super.onNegative(dialog);
                                        dialog.dismiss();
                                    }
                                }).show();
                        materialDialog.setCancelable(false);

                    }catch(Exception e){e.printStackTrace();}
                    return true;
                }else {
                    return false;
                }
            }
        });

        viewPager.setAdapter(tabPagerAdapter);
        try{
            activity.setTitle(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));
        }catch(Exception e){e.printStackTrace();}
        tabs.setDistributeEvenly(true);
        tabs.setCustomTabView(R.layout.tab_text,R.id.tab_textview);
        //((ViewGroup)tabs.getChildAt(0)).getChildAt(1).setVisibility(View.VISIBLE);
//                        tabs.setSelectedIndicatorColors(getResources().getColor(R.color.white));
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.white);
            }
        });
        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(viewPager);

        if (alertCountVal!=null && alertCountVal.trim().length()>0 && !alertCountVal.equals("0")){
            alertCountTv.setText(alertCountVal);
            alertCountTv.setVisibility(View.VISIBLE);
        }
        progressLayout.setVisibility(View.GONE);
        if (viewPager!=null){
            if(Constants.createMsg){
                viewPager.setCurrentItem(0);
                if(Home_Main_Fragment.progressBar!=null)
                    Home_Main_Fragment.progressBar.setVisibility(View.VISIBLE);
                Constants.createMsg = false;
            }else if(Constants.deepLinkAnalytics)
            {
                viewPager.setCurrentItem(1);
                Constants.deepLinkAnalytics = false ;
            }
        }

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


                    }
                });
            }
        }, 500);*/
    }
    public void setFragmentTab(int i){
        if(!isAdded()) return;
        if(Constants.deepLinkAnalytics)
        {
            viewPager.setCurrentItem(i);
            Constants.deepLinkAnalytics = false ;
        }
    }

    @Subscribe
    public void getRiaCardModels(ArrayList<RiaCardModel> model){
        if(tabs.getTabView(1)!=null){
            if(model.size()>0) {
                tabs.getTabView(1).findViewById(R.id.ll_ria_alert).setVisibility(View.VISIBLE);
            }else {
                tabs.getTabView(1).findViewById(R.id.ll_ria_alert).setVisibility(View.GONE);
            }
        }
    }
}
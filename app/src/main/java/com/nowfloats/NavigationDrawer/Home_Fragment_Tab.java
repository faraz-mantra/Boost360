package com.nowfloats.NavigationDrawer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.model.AlertCountEvent;
import com.nowfloats.NavigationDrawer.model.RiaCardModel;
import com.nowfloats.NotificationCenter.NotificationFragment;
import com.nowfloats.bubble.CustomerAssistantService;
import com.nowfloats.on_boarding.OnBoardingManager;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.WebEngageController;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.thinksity.R;

import java.util.ArrayList;

import static com.framework.webengageconstant.EventLabelKt.HOME_SCREEN;
import static com.framework.webengageconstant.EventNameKt.CLICKED_ON_REPORTS;
import static com.framework.webengageconstant.EventValueKt.NULL;
import static com.nowfloats.NavigationDrawer.HomeActivity.headerText;
import static com.nowfloats.bubble.BubblesService.ACTION_KILL_DIALOG;

/**
 * A simple {@link Fragment} subclass.
 */
public class Home_Fragment_Tab extends Fragment {
    private static final int PERM_REQUEST_CODE_DRAW_OVERLAYS = 122;
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
    LinearLayout bubbleOverlay;
    SharedPreferences pref;
    OnBoardingManager onBoardingManager;
    private IntentFilter clickIntentFilters = new IntentFilter(ACTION_KILL_DIALOG);
    private MaterialDialog overLayDialog;
    private boolean dialogAlreadyShown=false;

    public static enum DrawOverLay {FromHome, FromTab};

    @Override
    public void onResume() {
        super.onResume();

        bus.register(this);
        if (headerText != null) {
            headerText.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));
        }
        if (viewPager != null) {
            if (Constants.createMsg) {
                viewPager.setCurrentItem(0);
                //if (Home_Main_Fragment.progressBar != null)
                    //Home_Main_Fragment.progressBar.setVisibility(View.VISIBLE);
                Constants.createMsg = false;
            } else {
                //if (Home_Main_Fragment.progressBar != null){}
                    //Home_Main_Fragment.progressBar.setVisibility(View.GONE);
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
        bus = BusProvider.getInstance().getBus();
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                if(activity==null){activity = getActivity();}

            }
        }).start();*/
        onBoardingManager = new OnBoardingManager(getContext());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
        session = new UserSessionManager(activity.getApplicationContext(), activity);
    }

    @Subscribe
    public void getalertCountEvent(AlertCountEvent ev) {
        if (alertCountVal != null && alertCountVal.trim().length() > 0 && !alertCountVal.equals("0") && alertCountTv != null) {
            if (Integer.parseInt(alertCountVal) > 99) {
                alertCountTv.setText("99+");
            } else {
                alertCountTv.setText(alertCountVal);
            }

            alertCountTv.setVisibility(View.VISIBLE);
        } else if (alertCountTv != null) {
            alertCountTv.setText("0");
            alertCountTv.setVisibility(View.GONE);
            alertCountVal = "0";
        } else {
            alertCountVal = "0";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_home__fragment__tab, container, false);
        tabPagerAdapter = new TabPagerAdapter(getChildFragmentManager(), activity);
        return mainView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isAdded()) return;
        pref = getActivity().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        NotificationFragment.getAlertCount(session, Constants.alertInterface, bus);
        progressLayout = (LinearLayout) view.findViewById(R.id.progress_layout);
        progressLayout.setVisibility(View.VISIBLE);
        tabs = (SlidingTabLayout) view.findViewById(R.id.tabs);
        viewPager = (ViewPager) view.findViewById(R.id.homeTabViewpager);
        alertCountTv = (TextView) view.findViewById(R.id.alert_count_textview);
        bubbleOverlay = (LinearLayout) view.findViewById(R.id.floating_bubble_overlay);
        alertCountTv.setVisibility(View.GONE);
        String paymentState = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE);

//        if (Constants.PACKAGE_NAME.equals("com.biz2.nowfloats") /*&& "1".equals(paymentState)*/) {
//
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    checkOverlay(DrawOverLay.FromTab);
//                }
//            }, 8000);
//
//        }

        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (Constants.createMsg) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);

                    try {
                        if (materialDialog != null && materialDialog.isShowing()) {
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
                                            //Home_Main_Fragment.progressBar.setVisibility(View.GONE);
                                            Home_Main_Fragment.retryLayout.setVisibility(View.GONE);
                                            Home_Main_Fragment.progressCrd.setVisibility(View.GONE);
                                            Constants.createMsg = false;
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onNegative(MaterialDialog dialog) {
                                        super.onNegative(dialog);
                                        dialog.dismiss();
                                    }
                                }).show();
                        materialDialog.setCancelable(false);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });



        viewPager.setAdapter(tabPagerAdapter);
        try {
            activity.setTitle(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));
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

        if (alertCountVal != null && alertCountVal.trim().length() > 0 && !alertCountVal.equals("0")) {
            if (Integer.parseInt(alertCountVal) > 99) {
                alertCountTv.setText("99+");
            } else {
                alertCountTv.setText(alertCountVal);
            }
            alertCountTv.setVisibility(View.VISIBLE);
        }
        progressLayout.setVisibility(View.GONE);
        if (viewPager != null) {
            if (Constants.createMsg) {
                viewPager.setCurrentItem(0);
                //if (Home_Main_Fragment.progressBar != null)
                   // Home_Main_Fragment.progressBar.setVisibility(View.VISIBLE);
                Constants.createMsg = false;
            } else if (Constants.deepLinkAnalytics) {
                viewPager.setCurrentItem(1);
                Constants.deepLinkAnalytics = false;
            }
        }

        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d("Position", String.valueOf(position));
                if(position == 1){
                    WebEngageController.trackEvent( CLICKED_ON_REPORTS, HOME_SCREEN, NULL);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



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

    public void setFragmentTab(int i) {
        if (!isAdded()) return;
        viewPager.setCurrentItem(i);
    }

    @Subscribe
    public void getRiaCardModels(ArrayList<RiaCardModel> model) {
//        if (tabs.getTabView(1) != null) {
//            if (model != null && model.size() > 0) {
//                tabs.getTabView(1).findViewById(R.id.ll_ria_alert).setVisibility(View.VISIBLE);
//            } else {
//                tabs.getTabView(1).findViewById(R.id.ll_ria_alert).setVisibility(View.GONE);
//            }
//        }
    }

    @Override
    public void onStop() {
        super.onStop();

//        if (getActivity() == null) return;
//        if (Constants.PACKAGE_NAME.equals("com.biz2.nowfloats")) {
//            getActivity().stopService(new Intent(getActivity(), BubblesService.class));
//            getActivity().unregisterReceiver(clickReceiver);
//        }

    }

//    private void showBubble() {
//
////        if (!pref.getBoolean(Key_Preferences.SHOW_BUBBLE_COACH_MARK, false)) {
//        addOverlay();
//        pref.edit().putBoolean(Key_Preferences.SHOW_BUBBLE_COACH_MARK, true).apply();
////        }
//
//        int px = Methods.dpToPx(80, getActivity());
//        Intent intent = new Intent(getActivity(), BubblesService.class);
//        intent.putExtra(Key_Preferences.BUBBLE_POS_Y, px);
//        intent.putExtra(Key_Preferences.DIALOG_FROM, BubblesService.FROM.HOME_ACTIVITY);
//        activity.startService(intent);
//
//    }

    public void checkOverlay(DrawOverLay from) {
//        if (!isAdded() || getActivity() == null) {
//            return;
//        }
//
//
//        Calendar calendar = Calendar.getInstance();
//        long oldTime = pref.getLong(Key_Preferences.SHOW_BUBBLE_TIME, -1);
//        long newTime = calendar.getTimeInMillis();
//        long diff = 3 * 24 * 60 * 60 * 1000;
//        if (oldTime != -1 && ((newTime - oldTime) < diff)) {
//            return;
//        } else {

        if (getActivity() != null) {
            if (!Methods.hasOverlayPerm(getActivity())) {
                dialogForOverlayPath(from);
            }
        }
//        }
//        pref.edit().putLong(Key_Preferences.SHOW_BUBBLE_TIME, Calendar.getInstance().getTimeInMillis()).apply();
    }

    private void dialogForOverlayPath(DrawOverLay from) {
        if (getActivity() == null || !isAdded()) return;
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_bubble_overlay_permission, null);
        ImageView image = (ImageView) view.findViewById(R.id.gif_image);
        try {
//            GlideDrawableImageViewTarget target = new GlideDrawableImageViewTarget(image);
            Glide.with(getContext()).load(R.drawable.overlay_gif).into(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (overLayDialog == null) {
            overLayDialog = new MaterialDialog.Builder(getContext())
                    .customView(view, false)
                    .positiveColorRes(R.color.primary)
                    .positiveText(getString(R.string.open_setting))
                    .callback(new MaterialDialog.ButtonCallback() {

                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);
                            try {
                                requestOverlayPermission();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            dialog.dismiss();
                        }
                    }).show();
        } else if (from == DrawOverLay.FromHome) {
            overLayDialog.show();
        }
    }

    private void requestOverlayPermission() {

        if (getActivity() == null) {
            return;
        }

        MixPanelController.track(MixPanelController.BUBBLE_OVERLAY_PERM, null);
        if (android.os.Build.VERSION.SDK_INT >= 23) {

            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getActivity().getPackageName()));
            startActivityForResult(intent, PERM_REQUEST_CODE_DRAW_OVERLAYS);
        } else {
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERM_REQUEST_CODE_DRAW_OVERLAYS) {

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (android.os.Build.VERSION.SDK_INT >= 23) {

                        if (getActivity() != null && Settings.canDrawOverlays(getActivity())) {

                            if (pref.getBoolean(Key_Preferences.HAS_SUGGESTIONS, false)) {
                                checkCustomerAssistantService();
                            }

                        }
                    }
                }
            }, 1000);
        }
    }

    private void checkCustomerAssistantService() {
//        pref.edit().putBoolean(Key_Preferences.HAS_SUGGESTIONS, true).commit();
        if (pref.getBoolean(Key_Preferences.HAS_SUGGESTIONS, false)) {

            if (Methods.hasOverlayPerm(getActivity())) {

                if (!Methods.isMyServiceRunning(getActivity(), CustomerAssistantService.class)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        getActivity().startForegroundService(new Intent(getActivity(), CustomerAssistantService.class));
                    } else {
                        Intent bubbleIntent = new Intent(getActivity(), CustomerAssistantService.class);
                        getActivity().startService(bubbleIntent);
                    }
                }
            }
        }
        getActivity().sendBroadcast(new Intent(CustomerAssistantService.ACTION_REMOVE_BUBBLE));
    }


//    private void addOverlay() {
//        bubbleOverlay.setVisibility(View.VISIBLE);
//        View view = LayoutInflater.from(getActivity()).inflate(R.layout.bubble_pointing_sign, bubbleOverlay);
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                bubbleOverlay.removeAllViews();
//                bubbleOverlay.setVisibility(View.GONE);
//                //layout.setOnClickListener(null);
//            }
//        });
//    }

    @Override
    public void onStart() {
        super.onStart();

        if ((!session.isAllAuthSet() || !pref.getBoolean(Key_Preferences.ON_BOARDING_STATUS, false && (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE).equals("1") || session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE).equals("0"))))&& !dialogAlreadyShown) {
            onBoardingManager.getMerchantProfileCOnnection(session.getFpTag(),null);
            dialogAlreadyShown=true;
        }
        if (alertCountVal != null && alertCountVal.trim().length() > 0 && !alertCountVal.equals("0") && alertCountTv != null) {
            if (Integer.parseInt(alertCountVal) > 99) {
                alertCountTv.setText("99+");
            } else {
                alertCountTv.setText(alertCountVal);
            }

            alertCountTv.setVisibility(View.VISIBLE);
        } else if (alertCountTv != null) {
            alertCountTv.setText("0");
            alertCountTv.setVisibility(View.GONE);
            alertCountVal = "0";
        } else {
            alertCountVal = "0";
        }
//        if (Constants.PACKAGE_NAME.equals("com.biz2.nowfloats")) {
//            getActivity().registerReceiver(clickReceiver, clickIntentFilters);
//        }
    }

//    BroadcastReceiver clickReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Log.v("ggg", "clicked");
//            bubbleOverlay.removeAllViews();
//            bubbleOverlay.setVisibility(View.GONE);
//            //bubbleOverlay.setOnClickListener(null);
//        }
//
//    };


    public int getCurrentItem()
    {
        return viewPager.getCurrentItem();
    }
}
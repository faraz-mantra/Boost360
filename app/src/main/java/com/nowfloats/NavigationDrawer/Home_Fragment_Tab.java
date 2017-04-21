package com.nowfloats.NavigationDrawer;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.model.AlertCountEvent;
import com.nowfloats.NavigationDrawer.model.RiaCardModel;
import com.nowfloats.NotificationCenter.NotificationFragment;
import com.nowfloats.bubble.BubblesService;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.thinksity.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;

import static com.nowfloats.accessbility.BubbleDialog.ACTION_KILL_DIALOG;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
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
    private IntentFilter clickIntentFilters = new IntentFilter(ACTION_KILL_DIALOG);
    private MaterialDialog overLayDialog;

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
        if(!isAdded()) return;
        pref = getActivity().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        NotificationFragment.getAlertCount(session, Constants.alertInterface, bus);
        progressLayout = (LinearLayout)view.findViewById(R.id.progress_layout);
        progressLayout.setVisibility(View.VISIBLE);
        tabs = (SlidingTabLayout) view.findViewById(R.id.tabs);
        viewPager = (ViewPager) view.findViewById(R.id.homeTabViewpager);
        alertCountTv = (TextView)view.findViewById(R.id.alert_count_textview);
        bubbleOverlay = (LinearLayout) view.findViewById(R.id.floating_bubble_overlay);
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
            if(model!=null && model.size()>0) {
                tabs.getTabView(1).findViewById(R.id.ll_ria_alert).setVisibility(View.VISIBLE);
            }else {
                tabs.getTabView(1).findViewById(R.id.ll_ria_alert).setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if(getActivity()==null) return;

        getActivity().stopService(new Intent(getActivity(),BubblesService.class));
        getActivity().unregisterReceiver(clickReceiver);
    }
    private  void showBubble(){

        if(!pref.getBoolean(Key_Preferences.SHOW_BUBBLE_COACH_MARK,false)) {
            addOverlay();
            pref.edit().putBoolean(Key_Preferences.SHOW_BUBBLE_COACH_MARK,true).apply();
        }

        int px = Methods.dpToPx(80,getActivity());
        Intent intent = new Intent(getActivity(), BubblesService.class);
        intent.putExtra(Key_Preferences.BUBBLE_POS,px);
        intent.putExtra(Key_Preferences.DIALOG_FROM, BubblesService.FROM.HOME_ACTIVITY);
        activity.startService(intent);

    }
    private void checkOverlay() {
        Calendar calendar = Calendar.getInstance();
        long oldTime = pref.getLong(Key_Preferences.SHOW_BUBBLE_TIME, -1);
        long newTime = calendar.getTimeInMillis();
        long diff = 3 * 24 * 60 * 60 * 1000;
        Log.v("ggg", oldTime + "");
//Log.v("ggg",String.valueOf(diff)+" "+String.valueOf(newTime-oldTime));
        if (oldTime != -1 && ((newTime - oldTime) < diff)) {
            return;
        }

        boolean checkAccessibility = true;

        if (android.os.Build.VERSION.SDK_INT >= 23 && getActivity() != null && !Settings.canDrawOverlays(getActivity())) {
            checkAccessibility = false;
            dialogForOverlayPath();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkAccessibility = canDrawOverlaysUsingReflection(getActivity());
            if (!checkAccessibility) {
                dialogForOverlayPath();
            }
        }

        if (checkAccessibility)
            checkForAccessibility();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean canDrawOverlaysUsingReflection(Context context) {

        try {

            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            Class clazz = AppOpsManager.class;
            Method dispatchMethod = clazz.getMethod("checkOp", new Class[]{int.class, int.class, String.class});
//AppOpsManager.OP_SYSTEM_ALERT_WINDOW = 24
            int mode = (Integer) dispatchMethod.invoke(manager, new Object[]{24, Binder.getCallingUid(), context.getApplicationContext().getPackageName()});

            return AppOpsManager.MODE_ALLOWED == mode;

        } catch (Exception e) {
            return false;
        }

    }
    /*private void checkOverlay() {
        Calendar calendar = Calendar.getInstance();
        long oldTime = pref.getLong(Key_Preferences.SHOW_BUBBLE_TIME,-1);
        long newTime = calendar.getTimeInMillis();
        long diff = 3*24*60*60*1000;
        Log.v("ggg",oldTime+"");
        //Log.v("ggg",String.valueOf(diff)+" "+String.valueOf(newTime-oldTime));
        if(oldTime != -1 && ((newTime-oldTime) < diff)){
            return;
        }

        if (android.os.Build.VERSION.SDK_INT >= 23 && getActivity() != null && !Settings.canDrawOverlays(getActivity())) {
            dialogForOverlayPath();
        } else {
            checkForAccessibility();
        }
    }*/
    private void dialogForOverlayPath(){

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_bubble_overlay_permission,null);
        ImageView image = (ImageView) view.findViewById(R.id.gif_image);
        try {
            GlideDrawableImageViewTarget target = new GlideDrawableImageViewTarget(image);
            Glide.with(getActivity()).load(R.drawable.overlay_gif).into(target);
        }catch(Exception e){
            e.printStackTrace();
        }
        if(overLayDialog == null){
        overLayDialog = new MaterialDialog.Builder(getActivity())
                .title(getString(R.string.boost_bubble))
                .customView(view,false)
                .positiveColorRes(R.color.primary)
                .positiveText(getString(R.string.open_setting))
                .callback(new MaterialDialog.ButtonCallback() {

                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);

                        requestOverlayPermission();
                        dialog.dismiss();
                    }
                }).show();
        }
    }

    private void requestOverlayPermission(){
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getActivity().getPackageName()));
        startActivityForResult(intent, PERM_REQUEST_CODE_DRAW_OVERLAYS);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERM_REQUEST_CODE_DRAW_OVERLAYS) {
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                if (getActivity()!= null && Settings.canDrawOverlays(getActivity())) {
                    checkForAccessibility();
                }
            }
        }
    }
    private void checkForAccessibility(){
        if(getActivity() ==null) return;
        if(!Methods.isAccessibilitySettingsOn(getActivity())) {
            showBubble();
        }else if(pref!=null){
            pref.edit().putLong(Key_Preferences.SHOW_BUBBLE_TIME,Calendar.getInstance().getTimeInMillis()).apply();
        }
    }
    private void addOverlay(){
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.bubble_pointing_sign, bubbleOverlay);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bubbleOverlay.removeAllViews();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        checkOverlay();
        getActivity().registerReceiver(clickReceiver,clickIntentFilters);
    }

    BroadcastReceiver clickReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v("ggg","clicked");
            bubbleOverlay.removeAllViews();
        }

    };

}
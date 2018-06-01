package com.nowfloats.managenotification;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nowfloats.Analytics_Screen.model.VmnCallModel;
import com.nowfloats.Business_Enquiries.Model.Business_Enquiry_Model;
import com.nowfloats.Business_Enquiries.Model.Entity_model;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.accessbility.BubbleDialog;
import com.nowfloats.bubble.BubblesService;
import com.nowfloats.bubble.CustomerAssistantService;
import com.nowfloats.customerassistant.CustomerAssistantActivity;
import com.nowfloats.customerassistant.FirebaseLogger;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.nowfloats.util.Constants.PREF_NOTI_CALL_LOGS;
import static com.nowfloats.util.Constants.PREF_NOTI_ENQUIRIES;


/**
 * Created by NowFloats on 4/12/2017.
 */

public class CallerInfoDialog extends AppCompatActivity implements ExpandableCardView.OnExpandedListener {


    private CallerInfoAdapter callsAdapter, enquiryAdapter;

    private ArrayList<VmnCallModel> callList = new ArrayList<>();

    private ArrayList<Business_Enquiry_Model> businessEnquiryList = new ArrayList<>();

    private ExpandableCardView ecvCalls, ecvEnquiries;

    private TextView tvDismissCalls, tvDismissEnquiries;

    private RecyclerView rvCalls, rvEnquiries;

    private UserSessionManager session;

    private String appVersion = "";

    private SharedPreferences pref = null;

    private DisplayMetrics metrics;

    private KillListener killListener;

    private class KillListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            initStaticData();
            bindValues();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sendBroadcast(new Intent(CustomerAssistantService.ACTION_REMOVE_BUBBLE));
        overridePendingTransition(R.anim.bubble_scale_up, R.anim.bubble_scale_down);
        super.onCreate(savedInstanceState);
        killListener = new KillListener();
        setContentView(R.layout.dialog_caller_info);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(CallerInfoDialog.this, R.color.primary));
        }

        initialize();

        MixPanelController.track(MixPanelController.SAM_BUBBLE_CLICKED, null);
        FirebaseLogger.getInstance().logSAMEvent("", FirebaseLogger.SAMSTATUS.BUBBLE_CLICKED, session.getFPID(), appVersion);


        initStaticData();
        bindValues();
//        setListAdapter();
//        getCalls();
    }

    private void initialize() {

        try {
            appVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        pref = getSharedPreferences(Constants.PREF_NAME, Activity.MODE_PRIVATE);
        metrics = getResources().getDisplayMetrics();
        int screenHeight = (int) (metrics.heightPixels * 0.90);
        int screenWidth = (int) (metrics.widthPixels * 1);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setLayout(screenWidth, screenHeight);

        ecvCalls = findViewById(R.id.ecvCalls);
        ecvEnquiries = findViewById(R.id.ecvEnquiries);
        tvDismissCalls = findViewById(R.id.tvDismissCalls);
        tvDismissEnquiries = findViewById(R.id.tvDismissEnquiries);


        rvCalls = ecvCalls.findViewById(R.id.rvList);
        rvEnquiries = ecvEnquiries.findViewById(R.id.rvList);
//        tvDismissCalls = ecvCalls.findViewById(R.id.tvDismiss);
//        tvDismissEnquiries = ecvEnquiries.findViewById(R.id.tvDismiss);
        session = new UserSessionManager(getApplicationContext(), CallerInfoDialog.this);

        ecvCalls.setOnExpandedListener(this);
        ecvEnquiries.setOnExpandedListener(this);


        tvDismissCalls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MixPanelController.track(MixPanelController.BUBBLE_DISMISS_CALL_TRACKER, null);
                ecvCalls.setVisibility(View.GONE);
                tvDismissCalls.setVisibility(View.GONE);
                pref.edit().putString(PREF_NOTI_CALL_LOGS, "").commit();
                if (ecvEnquiries.getVisibility() == View.GONE) {
                    stopService();
                }
            }
        });

        tvDismissEnquiries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MixPanelController.track(MixPanelController.BUBBLE_DISMISS_ENQUIRY, null);
                tvDismissEnquiries.setVisibility(View.GONE);
                ecvEnquiries.setVisibility(View.GONE);
                pref.edit().putString(PREF_NOTI_ENQUIRIES, "").commit();
                if (ecvCalls.getVisibility() == View.GONE) {
                    stopService();
                }
            }
        });

    }

    private void initStaticData() {
        String callLogsData = pref.getString(PREF_NOTI_CALL_LOGS, "");

        final Gson gson = new Gson();
        if (!TextUtils.isEmpty(callLogsData)) {
            Type type = new TypeToken<List<VmnCallModel>>() {
            }.getType();
            callList = gson.fromJson(callLogsData, type);
        }

        String businessLogsData = pref.getString(PREF_NOTI_ENQUIRIES, "");

        if (!TextUtils.isEmpty(businessLogsData)) {
            Type type = new TypeToken<List<Business_Enquiry_Model>>() {
            }.getType();
            businessEnquiryList = gson.fromJson(businessLogsData, type);
        }

    }

    public void checkValues() {

        sendBroadcast(new Intent(CustomerAssistantService.ACTION_REMOVE_BUBBLE));

        String callLogsData = pref.getString(PREF_NOTI_CALL_LOGS, "");

        final Gson gson = new Gson();
        if (!TextUtils.isEmpty(callLogsData)) {
            Type type = new TypeToken<List<VmnCallModel>>() {
            }.getType();
            callList = gson.fromJson(callLogsData, type);
        }

        String businessLogsData = pref.getString(PREF_NOTI_ENQUIRIES, "");

        if (!TextUtils.isEmpty(businessLogsData)) {
            Type type = new TypeToken<List<Business_Enquiry_Model>>() {
            }.getType();
            businessEnquiryList = gson.fromJson(businessLogsData, type);
        }

        if (callList.size() == 0 && businessEnquiryList.size() == 0) {
            stopService();
        }
    }

    private void bindValues() {


        if (callList.size() > 0) {
            ecvCalls.setVisibility(View.VISIBLE);
            tvDismissCalls.setVisibility(View.VISIBLE);
            ecvCalls.setTitle("Calls (" + callList.size() + ")");

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            callsAdapter = new CallerInfoAdapter(CallerInfoDialog.this, CallerInfoAdapter.NOTI_TYPE.CALLS, callList, pref);
            rvCalls.setLayoutManager(linearLayoutManager);
            rvCalls.setAdapter(callsAdapter);

            ecvCalls.expand();
        } else {
            ecvCalls.setVisibility(View.GONE);
            tvDismissCalls.setVisibility(View.GONE);
        }

        if (businessEnquiryList.size() > 0) {

            if (callList.size() > 0)
                ecvEnquiries.setInitCollapse(true);
            else {
                ecvEnquiries.expand();
            }
            tvDismissEnquiries.setVisibility(View.VISIBLE);
            ecvEnquiries.setVisibility(View.VISIBLE);
            ecvEnquiries.setTitle("Enquiries (" + businessEnquiryList.size() + ")");
//            ecvEnquiries.collapse();

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            enquiryAdapter = new CallerInfoAdapter(CallerInfoDialog.this, CallerInfoAdapter.NOTI_TYPE.ENQUIRIES, businessEnquiryList, pref);
            rvEnquiries.setLayoutManager(linearLayoutManager);
            rvEnquiries.setAdapter(enquiryAdapter);
        } else {
            ecvEnquiries.setVisibility(View.GONE);
            tvDismissEnquiries.setVisibility(View.GONE);
        }

        if (callList.size() > 0 && businessEnquiryList.size() > 0) {
            ecvCalls.setMaxHeight((int) (metrics.heightPixels * 0.90) / 2);
            ecvEnquiries.setMaxHeight((int) (metrics.heightPixels * 0.90) / 2);
        } else {
            ecvCalls.setMaxHeight((int) (metrics.heightPixels * 0.80) - Methods.dpToPx(40, CallerInfoDialog.this));
            ecvEnquiries.setMaxHeight((int) (metrics.heightPixels * 0.80) - Methods.dpToPx(40, CallerInfoDialog.this));
        }


        if (callList.size() == 0 && businessEnquiryList.size() == 0) {
            stopService();
        }

    }

    private void stopService() {
        pref.edit().putBoolean(Key_Preferences.HAS_SUGGESTIONS, false).commit();
        stopService(new Intent(this, CustomerAssistantService.class));
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CustomerAssistantService.ACTION_REFRESH_DIALOG);
        registerReceiver(killListener, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        sendBroadcast(new Intent(CustomerAssistantService.ACTION_ADD_BUBBLE));
        unregisterReceiver(killListener);
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onExpandChanged(int id, View v, boolean isExpanded) {
        switch (id) {
            case R.id.ecvCalls:
                if (isExpanded) {
                    ecvEnquiries.collapse();
                }
                break;
            case R.id.ecvEnquiries:
                if (isExpanded) {
                    ecvCalls.collapse();
                }
                break;
        }
    }
}

package com.nowfloats.managenotification;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nowfloats.Analytics_Screen.model.VmnCallModel;
import com.nowfloats.Business_Enquiries.Model.Business_Enquiry_Model;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.bubble.CustomerAssistantService;
import com.nowfloats.customerassistant.FirebaseLogger;
import com.nowfloats.manageinventory.SellerAnalyticsActivity;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.view.Window.FEATURE_NO_TITLE;
import static com.nowfloats.util.Constants.PREF_NOTI_CALL_LOGS;
import static com.nowfloats.util.Constants.PREF_NOTI_ENQUIRIES;
import static com.nowfloats.util.Constants.PREF_NOTI_ORDERS;


/**
 * Created by NowFloats on 4/12/2017.
 */

public class CallerInfoDialog extends AppCompatActivity implements ExpandableCardView.OnExpandedListener {


    private CallerInfoAdapter callsAdapter, enquiryAdapter, orderAdapter;

    private ArrayList<VmnCallModel> callList = new ArrayList<>();

    private ArrayList<Business_Enquiry_Model> businessEnquiryList = new ArrayList<>();

    private ArrayList<OrderModel> ordersList = new ArrayList<>();

    private ExpandableCardView ecvCalls, ecvEnquiries, ecvOrders;

    private TextView tvDismissCalls, tvDismissEnquiries, tvDismissOrders, tvViewOrders;

    private LinearLayout llOrders;

    private RecyclerView rvCalls, rvEnquiries, rvOrders;

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
        supportRequestWindowFeature(FEATURE_NO_TITLE);
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
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ecvCalls = findViewById(R.id.ecvCalls);
        ecvEnquiries = findViewById(R.id.ecvEnquiries);
        ecvOrders = findViewById(R.id.ecvOrders);
        tvDismissCalls = findViewById(R.id.tvDismissCalls);
        tvDismissEnquiries = findViewById(R.id.tvDismissEnquiries);
        tvDismissOrders = findViewById(R.id.tvDismissOrders);
        tvViewOrders = findViewById(R.id.tvViewOrders);
        llOrders = findViewById(R.id.llOrders);

        rvCalls = ecvCalls.findViewById(R.id.rvList);
        rvEnquiries = ecvEnquiries.findViewById(R.id.rvList);
        rvOrders = ecvOrders.findViewById(R.id.rvList);
//        tvDismissCalls = ecvCalls.findViewById(R.id.tvDismiss);
//        tvDismissEnquiries = ecvEnquiries.findViewById(R.id.tvDismiss);
        session = new UserSessionManager(getApplicationContext(), CallerInfoDialog.this);

        ecvCalls.setOnExpandedListener(this);
        ecvEnquiries.setOnExpandedListener(this);
        ecvOrders.setOnExpandedListener(this);

        tvDismissCalls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MixPanelController.track(MixPanelController.BUBBLE_DISMISS_CALL_TRACKER, null);
                ecvCalls.setVisibility(View.GONE);
                tvDismissCalls.setVisibility(View.GONE);
                pref.edit().putString(PREF_NOTI_CALL_LOGS, "").commit();
                if (ecvEnquiries.getVisibility() == View.GONE && ecvOrders.getVisibility() == View.GONE) {
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
                if (ecvCalls.getVisibility() == View.GONE && ecvOrders.getVisibility() == View.GONE) {
                    stopService();
                }
            }
        });

        tvDismissOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MixPanelController.track(MixPanelController.BUBBLE_DISMISS_ORDER, null);
                llOrders.setVisibility(View.GONE);
                ecvOrders.setVisibility(View.GONE);
                pref.edit().putString(PREF_NOTI_ORDERS, "").commit();
                if (ecvCalls.getVisibility() == View.GONE && ecvEnquiries.getVisibility() == View.GONE) {
                    stopService();
                }
            }
        });

        tvViewOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MixPanelController.track(MixPanelController.BUBBLE_VIEW_ORDERS, null);

                llOrders.setVisibility(View.GONE);
                ecvOrders.setVisibility(View.GONE);
                pref.edit().putString(PREF_NOTI_ORDERS, "").commit();
                if (ecvCalls.getVisibility() == View.GONE && ecvEnquiries.getVisibility() == View.GONE) {
                    stopService();
                }
                Intent intent = new Intent(getApplicationContext(), SellerAnalyticsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("from", "notification");
                intent.putExtra("url", getApplicationContext().getString(R.string.deep_link_call_tracker));
                getApplicationContext().startActivity(intent);
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

        String orderLogsData = pref.getString(PREF_NOTI_ORDERS, "");

        if (!TextUtils.isEmpty(orderLogsData)) {
            Type type = new TypeToken<List<OrderModel>>() {
            }.getType();
            ordersList = gson.fromJson(orderLogsData, type);
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

        String orderLogsData = pref.getString(PREF_NOTI_ORDERS, "");

        if (!TextUtils.isEmpty(orderLogsData)) {
            Type type = new TypeToken<List<OrderModel>>() {
            }.getType();
            ordersList = gson.fromJson(orderLogsData, type);
        }

        if (callList.size() == 0 && businessEnquiryList.size() == 0 && ordersList.size() == 0) {
            stopService();
        }
    }

    private void bindValues() {


        if (ordersList.size() > 0) {
            llOrders.setVisibility(View.VISIBLE);
            ecvOrders.setVisibility(View.VISIBLE);
            ecvOrders.setTitle("Orders (" + ordersList.size() + ")");

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            orderAdapter = new CallerInfoAdapter(CallerInfoDialog.this, CallerInfoAdapter.NOTI_TYPE.ORDERS, ordersList, pref);
            rvOrders.setLayoutManager(linearLayoutManager);
            rvOrders.setAdapter(orderAdapter);

            ecvOrders.expand();

        } else {
            ecvOrders.setVisibility(View.GONE);
            llOrders.setVisibility(View.GONE);
        }

        if (callList.size() > 0) {

            if (ordersList.size() > 0)
                ecvCalls.setInitCollapse(true);
            else {
                ecvCalls.expand();
            }
            ecvCalls.setVisibility(View.VISIBLE);
            tvDismissCalls.setVisibility(View.VISIBLE);
            ecvCalls.setTitle("Calls (" + callList.size() + ")");

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            callsAdapter = new CallerInfoAdapter(CallerInfoDialog.this, CallerInfoAdapter.NOTI_TYPE.CALLS, callList, pref);
            rvCalls.setLayoutManager(linearLayoutManager);
            rvCalls.setAdapter(callsAdapter);
        } else {
            ecvCalls.setVisibility(View.GONE);
            tvDismissCalls.setVisibility(View.GONE);
        }

        if (businessEnquiryList.size() > 0) {

            if (callList.size() > 0 || ordersList.size() > 0)
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

        if (callList.size() > 0 && businessEnquiryList.size() > 0 && ordersList.size() > 0) {
            ecvCalls.setMaxHeight((int) (metrics.heightPixels * 0.90) / 3);
            ecvEnquiries.setMaxHeight((int) (metrics.heightPixels * 0.90) / 3);
            ecvOrders.setMaxHeight((int) (metrics.heightPixels * 0.90) / 3);
        } else if (callList.size() > 0 && businessEnquiryList.size() > 0) {
            ecvCalls.setMaxHeight((int) (metrics.heightPixels * 0.90) / 2);
            ecvEnquiries.setMaxHeight((int) (metrics.heightPixels * 0.90) / 2);
        } else if (ordersList.size() > 0 && businessEnquiryList.size() > 0) {
            ecvOrders.setMaxHeight((int) (metrics.heightPixels * 0.90) / 2);
            ecvEnquiries.setMaxHeight((int) (metrics.heightPixels * 0.90) / 2);
        } else if (ordersList.size() > 0 && callList.size() > 0) {
            ecvOrders.setMaxHeight((int) (metrics.heightPixels * 0.90) / 2);
            ecvCalls.setMaxHeight((int) (metrics.heightPixels * 0.90) / 2);
        } else {
            ecvCalls.setMaxHeight((int) (metrics.heightPixels * 0.80) - Methods.dpToPx(40, CallerInfoDialog.this));
            ecvEnquiries.setMaxHeight((int) (metrics.heightPixels * 0.80) - Methods.dpToPx(40, CallerInfoDialog.this));
            ecvOrders.setMaxHeight((int) (metrics.heightPixels * 0.80) - Methods.dpToPx(40, CallerInfoDialog.this));
        }


        if (callList.size() == 0 && businessEnquiryList.size() == 0 && ordersList.size() == 0) {
            stopService();
        }

    }

    private void stopService() {
        pref.edit().putBoolean(Key_Preferences.HAS_SUGGESTIONS, false).apply();
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
        sendBroadcast(new Intent(CustomerAssistantService.ACTION_REMOVE_BUBBLE));
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
                    ecvOrders.collapse();
                }
                break;
            case R.id.ecvEnquiries:
                if (isExpanded) {
                    ecvCalls.collapse();
                    ecvOrders.collapse();
                }
                break;
            case R.id.ecvOrders:
                if (isExpanded) {
                    ecvEnquiries.collapse();
                    ecvCalls.collapse();
                }
                break;
        }
    }
}

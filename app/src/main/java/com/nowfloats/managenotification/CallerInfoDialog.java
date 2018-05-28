package com.nowfloats.managenotification;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.nowfloats.Analytics_Screen.model.VmnCallModel;
import com.nowfloats.Business_Enquiries.Model.Entity_model;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.bubble.CustomerAssistantService;
import com.nowfloats.customerassistant.CustomerAssistantActivity;
import com.nowfloats.customerassistant.FirebaseLogger;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

import java.util.ArrayList;


/**
 * Created by NowFloats on 4/12/2017.
 */

public class CallerInfoDialog extends AppCompatActivity implements ExpandableCardView.OnExpandedListener {

    private UserSessionManager sessionManager;

    private CallerInfoAdapter callsAdapter, enquiryAdapter;

    private ArrayList<VmnCallModel> callList = new ArrayList<>();

    private ArrayList<Entity_model> businessEnquiryList = new ArrayList<>();

    private ExpandableCardView ecvCalls, ecvEnquiries;

    private TextView tvDismissCalls, tvDismissEnquiries;

    private RecyclerView rvCalls, rvEnquiries;

    private UserSessionManager session;

    private String appVersion = "";

    private SharedPreferences pref = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sendBroadcast(new Intent(CustomerAssistantService.ACTION_REMOVE_BUBBLE));
        overridePendingTransition(R.anim.bubble_scale_up, R.anim.bubble_scale_down);
        super.onCreate(savedInstanceState);
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
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenHeight = (int) (metrics.heightPixels * 1);
        int screenWidth = (int) (metrics.widthPixels * 1);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setLayout(screenWidth, screenHeight);

        sessionManager = new UserSessionManager(this, this);

        ecvCalls = findViewById(R.id.ecvCalls);
        ecvEnquiries = findViewById(R.id.ecvEnquiries);
        tvDismissCalls = findViewById(R.id.tvDismissCalls);
        tvDismissEnquiries = findViewById(R.id.tvDismissEnquiries);


        rvCalls = ecvCalls.findViewById(R.id.rvList);
        rvEnquiries = ecvEnquiries.findViewById(R.id.rvList);
        session = new UserSessionManager(getApplicationContext(), CallerInfoDialog.this);

    }

    private void initStaticData() {
        VmnCallModel vmnCallModel = new VmnCallModel();
        vmnCallModel.setCallerNumber("9177513207");
        vmnCallModel.setCallDateTime("24jan, 01:17am");
        callList.add(vmnCallModel);

        VmnCallModel vmnCallModel1 = new VmnCallModel();
        vmnCallModel1.setCallerNumber("9189513207");
        vmnCallModel1.setCallDateTime("24feb, 01:17am");
        callList.add(vmnCallModel1);


        VmnCallModel vmnCallModel2 = new VmnCallModel();
        vmnCallModel2.setCallerNumber("9477515607");
        vmnCallModel2.setCallDateTime("24mar, 01:17am");
        callList.add(vmnCallModel2);

        Entity_model entity_model = new Entity_model();
        entity_model.setPhone("9876564810");
        entity_model.setMessage("I need info on this kid's wallpaper");
        entity_model.setCreatedDate("24jan, 01:17am");
        businessEnquiryList.add(entity_model);

        Entity_model entity_model1 = new Entity_model();
        entity_model1.setPhone("9876564810");
        entity_model1.setMessage("HI, I would like to know the cost per square feet and material used");
        entity_model1.setCreatedDate("24jan, 01:17am");
        businessEnquiryList.add(entity_model1);
    }

    private void bindValues() {

        ecvCalls.setOnExpandedListener(this);
        ecvEnquiries.setOnExpandedListener(this);

        tvDismissCalls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ecvCalls.setVisibility(View.GONE);
                tvDismissCalls.setVisibility(View.GONE);
            }
        });

        tvDismissEnquiries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ecvEnquiries.setVisibility(View.GONE);
                tvDismissEnquiries.setVisibility(View.GONE);
            }
        });

        tvDismissCalls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ecvCalls.setVisibility(View.GONE);
                tvDismissCalls.setVisibility(View.GONE);
                if (ecvEnquiries.getVisibility() == View.GONE) {
                    finish();
                }
            }
        });

        tvDismissEnquiries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvDismissEnquiries.setVisibility(View.GONE);
                ecvEnquiries.setVisibility(View.GONE);
                if (ecvCalls.getVisibility() == View.GONE) {
                    finish();
                }
            }
        });

        if (callList.size() > 0) {
            ecvCalls.setVisibility(View.VISIBLE);
            tvDismissCalls.setVisibility(View.VISIBLE);
            ecvCalls.setTitle("Calls (" + callList.size() + ")");

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            callsAdapter = new CallerInfoAdapter(CallerInfoDialog.this, CallerInfoAdapter.NOTI_TYPE.CALLS, callList);
            rvCalls.setLayoutManager(linearLayoutManager);
            rvCalls.setAdapter(callsAdapter);

            ecvCalls.expand();
        } else {
            ecvCalls.setVisibility(View.GONE);
            tvDismissCalls.setVisibility(View.GONE);
        }

        if (businessEnquiryList.size() > 0) {
            ecvEnquiries.collapse();
            tvDismissEnquiries.setVisibility(View.VISIBLE);
            ecvEnquiries.setVisibility(View.VISIBLE);
            ecvEnquiries.setTitle("Enquiries (" + businessEnquiryList.size() + ")");

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            enquiryAdapter = new CallerInfoAdapter(CallerInfoDialog.this, CallerInfoAdapter.NOTI_TYPE.ENQUIRIES, businessEnquiryList);
            rvEnquiries.setLayoutManager(linearLayoutManager);
            rvEnquiries.setAdapter(enquiryAdapter);
        } else {
            ecvEnquiries.setVisibility(View.GONE);
            tvDismissEnquiries.setVisibility(View.GONE);
        }

        if (callList.size() == 0 && businessEnquiryList.size() == 0) {
            pref.edit().putBoolean(Key_Preferences.HAS_SUGGESTIONS, true).commit();
            stopService(new Intent(this, CustomerAssistantService.class));
            finish();
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        sendBroadcast(new Intent(CustomerAssistantService.ACTION_ADD_BUBBLE));
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

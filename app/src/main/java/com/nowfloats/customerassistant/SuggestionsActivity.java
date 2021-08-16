package com.nowfloats.customerassistant;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.bubble.BubblesService;
import com.nowfloats.customerassistant.models.MessageDO;
import com.nowfloats.customerassistant.models.SMSSuggestions;
import com.nowfloats.customerassistant.models.SuggestionsDO;
import com.nowfloats.customerassistant.service.CustomerAssistantApi;
import com.nowfloats.sync.DbController;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.Utils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.HashMap;


public class SuggestionsActivity extends AppCompatActivity {


    public SharedPreferences pref;
    public SMSSuggestions smsSuggestions;
    private CustomerAssistantApi suggestionsApi;
    private Bus mBus;
    private FragmentManager fragmentManager;
    private UserSessionManager session;
    private CallToActionFragment callToActionFragment;
    private ProgressBar pbView;

    private String appVersion = "";

    private DbController mDbController;
    private KillListener killListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestions);
        this.setFinishOnTouchOutside(false);

        pbView = (ProgressBar) findViewById(R.id.pbView);
        mBus = BusProvider.getInstance().getBus();
        session = new UserSessionManager(getApplicationContext(), SuggestionsActivity.this);
        suggestionsApi = new CustomerAssistantApi(mBus);
        mDbController = DbController.getDbController(SuggestionsActivity.this);

        initializeControls();

    }

    private void initializeControls() {

        try {
            appVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        setDisplayMetrics(1.0f, 0.80f, false);

        pref = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        killListener = new KillListener();

        fragmentManager = getSupportFragmentManager();

        MixPanelController.track(MixPanelController.SAM_BUBBLE_CLICKED, null);
        FirebaseLogger.getInstance().logSAMEvent("", FirebaseLogger.SAMSTATUS.BUBBLE_CLICKED, session.getFPID(), appVersion);

        if (!pref.getBoolean(Key_Preferences.HAS_SHOWN_SAM_COACH_MARK, false)) {
//            fragmentManager.beginTransaction().
//                    replace(R.id.flTopView, new OnBoardingFragment())
//                    .addToBackStack(null)
//                    .commit();
        } else {
            switchView(SuggestionsActivity.SwitchView.CALL_TO_ACTION);
        }

    }

    private void setDisplayMetrics(float width, float height, boolean isCenter) {

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenHeight = (int) (metrics.heightPixels * height);
        int screenWidth = (int) (metrics.widthPixels * width);

        if (isCenter)
            getWindow().setGravity(Gravity.CENTER);
        else
            getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, screenHeight);
//        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    @Override
    public void onStart() {
        super.onStart();
        mBus.register(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BubblesService.ACTION_KILL_DIALOG);
        registerReceiver(killListener, intentFilter);
    }

    @Override
    public void onStop() {
        mBus.unregister(this);
        unregisterReceiver(killListener);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        sendBroadcast(new Intent(BubblesService.ACTION_RESET_BUBBLE));
        super.onDestroy();
    }

    public void switchView(SwitchView switchView) {

        switch (switchView) {

            case ACTION_ITEMS:
                break;
            case CALL_TO_ACTION:
                loadCallToActionItems();

                break;
        }
    }

    private void loadCallToActionItems() {

        removeFragment();

        pbView.setVisibility(View.VISIBLE);

        if (Utils.isNetworkConnected(SuggestionsActivity.this)) {
            HashMap<String, String> offersParam = new HashMap<>();
            offersParam.put("fpId", session.getFPID());
//            suggestionsApi.getMessages(offersParam);
        } else {
            loadDataFromDb();
        }
    }

    private void removeFragment() {

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        }
    }

    @Subscribe
    public void processSMSDetails(SMSSuggestions suggestions) {

        pbView.setVisibility(View.GONE);

        smsSuggestions = suggestions;

        if (smsSuggestions != null && smsSuggestions.getSuggestionList() != null) {

            if (smsSuggestions.getSuggestionList().size() > 0) {
                viewMessages();

                Gson gson = new GsonBuilder().create();
                String payloadStr = gson.toJson(suggestions);
                mDbController.postSamData(payloadStr);

            } else {
                MixPanelController.track(MixPanelController.SAM_BUBBLE_CLICKED_NO_DATA, null);
                FirebaseLogger.getInstance().logSAMEvent("", FirebaseLogger.SAMSTATUS.HAS_NO_DATA, session.getFPID(), appVersion);
                pref.edit().putBoolean(Key_Preferences.HAS_SUGGESTIONS, false).apply();
                finish();
            }

        } else {

            MixPanelController.track(MixPanelController.SAM_BUBBLE_CLICKED_SERVER_ERROR, null);
            FirebaseLogger.getInstance().logSAMEvent("", FirebaseLogger.SAMSTATUS.SERVER_ERROR, session.getFPID(), appVersion);
            // problem with server need to show some dialog
        }

    }

    private void loadDataFromDb() {

        String payloadStr = mDbController.getSamData();

        if (payloadStr.isEmpty()) {
            finish();
        } else {
            Gson gson = new GsonBuilder().create();
            smsSuggestions = gson.fromJson(payloadStr, SMSSuggestions.class);
            viewMessages();
        }

    }

    private void viewMessages() {
        MixPanelController.track(MixPanelController.SAM_BUBBLE_CLICKED_DATA, null);
        FirebaseLogger.getInstance().logSAMEvent("", FirebaseLogger.SAMSTATUS.HAS_DATA, session.getFPID(), appVersion);

        fragmentManager.beginTransaction()
                .replace(R.id.flTopView,
                        callToActionFragment = new CallToActionFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            if (callToActionFragment != null && callToActionFragment.isProductsVisible()) {
                callToActionFragment.displayCTA();
            }

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (callToActionFragment != null && callToActionFragment.isProductsVisible()) {
            callToActionFragment.displayCTA();
        } else {
            super.onBackPressed();
            finish();
        }

    }

    public void updateActionsToServer(SuggestionsDO mSuggestionsDO) {

        ArrayList<MessageDO> arrMessageDO = new ArrayList<>();

        MessageDO messageDO = new MessageDO();
        messageDO.setMessageId(mSuggestionsDO.getMessageId());
        messageDO.setFpId(mSuggestionsDO.getFpId());
        messageDO.setStatus(mSuggestionsDO.getStatus());
        arrMessageDO.add(messageDO);

        suggestionsApi.updateMessage(arrMessageDO);
    }

    public void updateRating(int rating) {

        HashMap<String, String> offersParam = new HashMap<>();
        offersParam.put("fpId", session.getFPID());
        offersParam.put("rating", rating + "");
        suggestionsApi.updateRating(offersParam);
    }

    public enum SwitchView {
        ACTION_ITEMS,
        CALL_TO_ACTION
    }

    private class KillListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    }
}

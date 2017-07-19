package com.nowfloats.swipecard;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.bubble.BubblesService;
import com.nowfloats.swipecard.models.MessageDO;
import com.nowfloats.swipecard.models.SMSSuggestions;
import com.nowfloats.swipecard.models.SuggestionsDO;
import com.nowfloats.swipecard.service.SuggestionsApi;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.HashMap;


public class SuggestionsActivity extends AppCompatActivity {


    private SharedPreferences pref;

    private SuggestionsApi suggestionsApi;

    private Bus mBus;

    private FragmentManager fragmentManager;

    private UserSessionManager session;

    private CallToActionFragment callToActionFragment;

    public SMSSuggestions smsSuggestions;

    private ProgressBar pbView;

    private class KillListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    }

    private KillListener killListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestions);
        this.setFinishOnTouchOutside(false);

        pbView = (ProgressBar) findViewById(R.id.pbView);
        mBus = BusProvider.getInstance().getBus();
        session = new UserSessionManager(getApplicationContext(), SuggestionsActivity.this);
        suggestionsApi = new SuggestionsApi(mBus);

        initializeControls();

    }

    private void initializeControls() {

        setDisplayMetrics(0.80f, 0.60f, true);

        pref = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        killListener = new KillListener();

        fragmentManager = getSupportFragmentManager();

        FirebaseLogger.getInstance().logSAMEvent("", 0, session.getFPID());

        fragmentManager.beginTransaction().
                replace(R.id.flTopView, new OnBoardingFragment())
                .addToBackStack(null)
                .commit();
    }

    private void setDisplayMetrics(float width, float height, boolean isCenter) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenHeight = (int) (metrics.heightPixels * height);
        int screenWidth = (int) (metrics.widthPixels * width);
        if (isCenter)
            getWindow().setGravity(Gravity.CENTER);
        else
            getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setLayout(screenWidth, screenHeight);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
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

    public enum SwitchView {
        ACTION_ITEMS,
        CALL_TO_ACTION
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

        FirebaseLogger.getInstance().logSAMEvent("", 1, session.getFPID());

        HashMap<String, String> offersParam = new HashMap<>();
        offersParam.put("fpId", session.getFPID());
        suggestionsApi.getMessages(offersParam);
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

        if (smsSuggestions != null && smsSuggestions.getSuggestionList().size() > 0) {

            fragmentManager.beginTransaction()
                    .replace(R.id.flTopView,
                            callToActionFragment = new CallToActionFragment())
                    .addToBackStack(null)
                    .commit();
        } else {
            pref.edit().putBoolean(Key_Preferences.HAS_SUGGESTIONS, false).apply();
            finish();
        }

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
}

package com.nowfloats.swipecard;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MenuItem;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.bubble.BubblesService;
import com.nowfloats.swipecard.models.SMSSuggestions;
import com.nowfloats.swipecard.service.SuggestionsApi;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.thinksity.R;

import java.util.HashMap;


public class SuggestionsActivity extends AppCompatActivity {


    private SharedPreferences pref;

    private SuggestionsApi suggestionsApi;

    private Bus mBus;

    private FragmentManager fragmentManager;

    private UserSessionManager session;

    private ActionItemsFragment actionItemsFragment;

    private CallToActionFragment callToActionFragment;

    public SMSSuggestions smsSuggestions;

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
                loadSMSDetails();

                break;
            case CALL_TO_ACTION:
                loadCallToActionItems();

                break;
        }
    }

    private void loadSMSDetails() {

        removeFragment();

        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left)
                .replace(R.id.flTopView, actionItemsFragment =
                        new ActionItemsFragment())
                .addToBackStack(null)
                .commit();


        HashMap<String, String> offersParam = new HashMap<>();
        offersParam.put("fpId", "590462167ca16e051830201a");
        suggestionsApi.getMessages(offersParam);
    }

    private void loadCallToActionItems() {

        removeFragment();

        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left)
                .replace(R.id.flTopView,
                        callToActionFragment = new CallToActionFragment())
                .addToBackStack(null)
                .commit();
    }

    private void removeFragment() {

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        }
    }

    @Subscribe
    public void processSMSDetails(SMSSuggestions suggestions) {

        smsSuggestions = suggestions;

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.flTopView);
        if (fragment != null && fragment instanceof ActionItemsFragment) {
            actionItemsFragment.filterData();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            if (callToActionFragment != null && callToActionFragment.isProductsVisible()) {
                callToActionFragment.displayCTA();
            } else {
                switchView(SwitchView.ACTION_ITEMS);
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
}

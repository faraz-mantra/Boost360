package com.nowfloats.sam;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.bubble.SAMBubblesService;
import com.nowfloats.sam.models.MessageDO;
import com.nowfloats.sam.models.SMSSuggestions;
import com.nowfloats.sam.models.SuggestionsDO;
import com.nowfloats.sam.service.SuggestionsApi;
import com.nowfloats.sync.DbController;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.Utils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.thinksity.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by admin on 8/17/2017.
 */

public class CustomerAssistantActivity extends Activity {


    public ProgressBar pbView;

    private SuggestionsApi suggestionsApi;

    private Bus mBus;

    private DbController mDbController;

    private UserSessionManager session;

    public SMSSuggestions smsSuggestions;

    public SharedPreferences pref;

    private String appVersion = "";

    private OnBoardingFragment onBoardingFragment;

    private CustomerAssistantListFragment mCustomerAssitantListFragment;

    private CustomerAssistantDetailFragment mCustomerAssistantDetailFragment;

    public static final String KEY_DATA = "mData";

    public static final String TAG_FRAGMENT = "customer_assistant";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        sendBroadcast(new Intent(SAMBubblesService.ACTION_REMOVE_BUBBLE));
        overridePendingTransition(R.anim.bubble_scale_up, R.anim.bubble_scale_down);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_assistant);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(CustomerAssistantActivity.this, R.color.transparent_blk));
        }


        initializeControls();

        if (!pref.getBoolean(Key_Preferences.HAS_SHOWN_SAM_COACH_MARK, false)) {
            getFragmentManager().beginTransaction().
                    replace(R.id.fmCustomerAssistant, onBoardingFragment = new OnBoardingFragment(), "on_boarding").commit();
        } else {
            loadCallToActionItems();
        }


    }

    private void initializeControls() {

        mCustomerAssitantListFragment = new CustomerAssistantListFragment();
        mCustomerAssistantDetailFragment = new CustomerAssistantDetailFragment();

        pbView = (ProgressBar) findViewById(R.id.pbView);

        pref = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        mBus = BusProvider.getInstance().getBus();
        session = new UserSessionManager(getApplicationContext(), CustomerAssistantActivity.this);
        suggestionsApi = new SuggestionsApi(mBus);
        mDbController = DbController.getDbController(CustomerAssistantActivity.this);
    }

    public void loadCallToActionItems() {

        if (onBoardingFragment != null) {
            pref.edit().putBoolean(Key_Preferences.HAS_SHOWN_SAM_COACH_MARK, true).apply();
            getFragmentManager().beginTransaction().remove(onBoardingFragment).commit();
        }

        pbView.setVisibility(View.VISIBLE);

        if (Utils.isNetworkConnected(CustomerAssistantActivity.this)) {
            HashMap<String, String> offersParam = new HashMap<>();
//            offersParam.put("fpId", session.getFPID());
            offersParam.put("fpId", "5928106e13c54e0b50251f21");
            suggestionsApi.getMessages(offersParam);
        } else {
            loadDataFromDb();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        mBus.register(this);
    }

    @Override
    public void onStop() {
        sendBroadcast(new Intent(SAMBubblesService.ACTION_ADD_BUBBLE));
        mBus.unregister(this);
        super.onStop();
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

    private void viewMessages() {
        MixPanelController.track(MixPanelController.SAM_BUBBLE_CLICKED_DATA, null);
        FirebaseLogger.getInstance().logSAMEvent("", FirebaseLogger.SAMSTATUS.HAS_DATA, session.getFPID(), appVersion);
        showCustomerList();
    }

    private void showCustomerList() {
        pbView.setVisibility(View.GONE);

        Bundle mBundle = new Bundle();
        mBundle.putSerializable(KEY_DATA, (Serializable) smsSuggestions.getSuggestionList());

        mCustomerAssitantListFragment.setArguments(mBundle);

        getFragmentManager().beginTransaction().replace(R.id.fmCustomerAssistant, mCustomerAssitantListFragment).
                commit();
    }

    public void onCustomerSelection(SuggestionsDO mSuggestionsDO) {

        Bundle mBundle = new Bundle();
        mBundle.putSerializable(KEY_DATA, (Serializable) mSuggestionsDO);

        mCustomerAssistantDetailFragment.setArguments(mBundle);

        getFragmentManager().beginTransaction().replace(R.id.fmCustomerAssistant,
                mCustomerAssistantDetailFragment, TAG_FRAGMENT)
                .commit();
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

    @Override
    public void onBackPressed() {
        final CustomerAssistantDetailFragment fragment =
                (CustomerAssistantDetailFragment) getFragmentManager().findFragmentByTag(TAG_FRAGMENT);
        if (fragment != null) {
            showCustomerList();
        } else {
            super.onBackPressed();
        }
    }
}

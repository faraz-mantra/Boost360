package com.nowfloats.sam;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.sam.adapters.SAMCustomersAdapter;
import com.nowfloats.sam.decorators.RecyclerSectionItemDecoration;
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

import java.util.ArrayList;
import java.util.HashMap;

import static com.nowfloats.sam.SAMCustomerDetailActivity.KEY_MSG;

/**
 * Created by admin on 8/17/2017.
 */

public class SAMCustomerListActivity extends Activity
        implements RecyclerSectionItemDecoration.SectionCallback {


    public ProgressBar pbView;

    private RecyclerView rvSAMCustomerList;

    private SuggestionsApi suggestionsApi;

    private Bus mBus;

    private DbController mDbController;

    private UserSessionManager session;

    public SMSSuggestions smsSuggestions;

    public SharedPreferences pref;

    private SAMCustomersAdapter samCustomersAdapter;

    private String appVersion = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        overridePendingTransition(R.anim.bubble_scale_up, R.anim.bubble_scale_down);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sam_customer_list);

        initializeControls();

        loadCallToActionItems();
    }

    private void initializeControls() {

        pbView = (ProgressBar) findViewById(R.id.pbView);
        rvSAMCustomerList = (RecyclerView) findViewById(R.id.rvSAMCustomerList);

        rvSAMCustomerList.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,
                false));


        RecyclerSectionItemDecoration sectionItemDecoration =
                new RecyclerSectionItemDecoration(120,
                        true,
                        SAMCustomerListActivity.this);
        rvSAMCustomerList.addItemDecoration(sectionItemDecoration);

        rvSAMCustomerList.setAdapter(samCustomersAdapter = new SAMCustomersAdapter(SAMCustomerListActivity.this, null));

        pref = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        mBus = BusProvider.getInstance().getBus();
        session = new UserSessionManager(getApplicationContext(), SAMCustomerListActivity.this);
        suggestionsApi = new SuggestionsApi(mBus);
        mDbController = DbController.getDbController(SAMCustomerListActivity.this);

        final Typeface hebrewFont  = Typeface.createFromAsset(getAssets(),"OpenSansHebrew-Regular.ttf");
        ((TextView)findViewById(R.id.tvTitle)).setTypeface(hebrewFont);
    }

    private void loadCallToActionItems() {

        pbView.setVisibility(View.VISIBLE);

        if (Utils.isNetworkConnected(SAMCustomerListActivity.this)) {
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
        samCustomersAdapter.refreshDetails((ArrayList<SuggestionsDO>) smsSuggestions.getSuggestionList());
    }

    public void onCustomerSelection(SuggestionsDO mSuggestionsDO) {

        Intent mIntent = new Intent(SAMCustomerListActivity.this, SAMCustomerDetailActivity.class);
        mIntent.putExtra(KEY_MSG, mSuggestionsDO);
        startActivity(mIntent);
    }

    @Override
    public boolean isSection(int position) {
        if (position % 2 == 0)
            return false;
        return true;
    }

    @Override
    public CharSequence getSectionHeader(int position) {
//        return smsSuggestions.getSuggestionList().get(position).getExpiryTimeOfMessage();
        return "27-jan-2017";
    }
}

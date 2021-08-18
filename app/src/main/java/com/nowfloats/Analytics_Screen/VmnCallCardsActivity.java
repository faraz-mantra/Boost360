package com.nowfloats.Analytics_Screen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.boost.upgrades.UpgradeActivity;
import com.framework.views.customViews.CustomToolbar;
import com.framework.views.zero.old.AppFragmentZeroCase;
import com.framework.views.zero.old.AppOnZeroCaseClicked;
import com.framework.views.zero.old.AppRequestZeroCaseBuilder;
import com.framework.views.zero.old.AppZeroCases;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonObject;
import com.nowfloats.Analytics_Screen.API.CallTrackerApis;
import com.nowfloats.Analytics_Screen.model.VmnCallModel;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.WebEngageController;
import com.thinksity.R;
import com.thinksity.databinding.ActivityVmnCallCardsBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;

import static com.framework.webengageconstant.EventLabelKt.EVENT_LABEL_BUSINESS_CALLS;
import static com.framework.webengageconstant.EventNameKt.BUSINESS_CALLS;
import static com.framework.webengageconstant.EventValueKt.NULL;
import static com.nowfloats.util.Key_Preferences.GET_FP_DETAILS_CATEGORY;

/**
 * Created by Admin on 27-04-2017.
 */

public class VmnCallCardsActivity extends AppCompatActivity implements View.OnClickListener, AppOnZeroCaseClicked {

    private static final String TAG = "VmnCallCardsActivity";
    UserSessionManager sessionManager;
    CardView viewCallLogCard;
    TextView missedCountText, receivedCountText, allCountText, potentialCallsText, webCallCount, phoneCallCount;
    Toolbar toolbar;
    ProgressDialog vmnProgressBar;
    ImageView seeMoreLessImage;
    ConstraintLayout parentLayout, helpWebPhoneLayout, primaryLayout;
    View backgroundLayout;
    boolean seeMoreLessStatus = false;
    int totalCallCount = 0;
    int totalPotentialCallCount = 0;
    boolean stopApiCall;
    boolean allowCallPlayFlag; // This flag allows only one audio to play at a time. True means an audio can be played.
    ArrayList<VmnCallModel> headerList = new ArrayList<>();
    VmnCall_Adapter vmnCallAdapter;
    RecyclerView mRecyclerView;
    String selectedViewType = "ALL";
    LinearLayout secondLayout;
    LinearLayout firstLayout;
    private LinearLayout  mainLayout, secondaryLayout, websiteHelper, phoneHelper, websiteHelperInfo, phoneHelperInfo;
    private UserSessionManager session;
    private int offset = 0;
    private RelativeLayout seeMoreLess;
    private ActivityVmnCallCardsBinding binding;
    private AppFragmentZeroCase appFragmentZeroCase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new UserSessionManager(getApplicationContext(), this);

        binding = DataBindingUtil.setContentView(this,R.layout.activity_vmn_call_cards);
        appFragmentZeroCase =new AppRequestZeroCaseBuilder(AppZeroCases.BUSINESS_CALLS,this,this,isPremium()).getRequest().build();
        getSupportFragmentManager().beginTransaction().add(binding.childContainer.getId(),appFragmentZeroCase).commit();
        CustomToolbar toolbar = (CustomToolbar) findViewById(R.id.toolbar);
        MixPanelController.track(MixPanelController.VMN_CALL_TRACKER, null);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            setTitle("Business Calls");
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        WebEngageController.trackEvent(BUSINESS_CALLS, EVENT_LABEL_BUSINESS_CALLS, NULL);
        vmnProgressBar = new ProgressDialog(this);
        vmnProgressBar.setIndeterminate(true);
        vmnProgressBar.setMessage(getString(R.string.please_wait));
        vmnProgressBar.setCancelable(false);
        sessionManager = new UserSessionManager(this, this);
        missedCountText = (TextView) findViewById(R.id.missed_count);
        receivedCountText = (TextView) findViewById(R.id.received_count);
        allCountText = (TextView) findViewById(R.id.total_count);
        viewCallLogCard = (CardView) findViewById(R.id.card_view_view_calllog);
        binding.tvTrackedCall.setText(getString(R.string.tracked_calls)+" "+sessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER));
        seeMoreLess = findViewById(R.id.see_more_less);
        seeMoreLessImage = findViewById(R.id.see_more_less_image);
        helpWebPhoneLayout = findViewById(R.id.help_web_phone_layout);
        websiteHelper = findViewById(R.id.website_helper);
        phoneHelper = findViewById(R.id.phone_helper);
        websiteHelperInfo = findViewById(R.id.help_website_info);
        phoneHelperInfo = findViewById(R.id.help_phone_info);
        parentLayout = findViewById(R.id.parent_layout);
        backgroundLayout = findViewById(R.id.background_layout);
        potentialCallsText = findViewById(R.id.total_number_of_calls);
        webCallCount = findViewById(R.id.web_call_count);
        phoneCallCount = findViewById(R.id.phone_call_count);
        allowCallPlayFlag = true;

        //tracking calls
        showTrackedCalls();

        mRecyclerView = (RecyclerView) findViewById(R.id.call_recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        vmnCallAdapter = new VmnCall_Adapter(this, headerList);
        mRecyclerView.setAdapter(vmnCallAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int totalItemCount = linearLayoutManager.getItemCount();
                int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (lastVisibleItem >= totalItemCount - 2 && !stopApiCall) {
                    getCalls();
                }
            }
        });



        seeMoreLess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!seeMoreLessStatus) {
                    seeMoreLessStatus = true;
                    seeMoreLessImage.setImageResource(R.drawable.up_arrow);
                    helpWebPhoneLayout.setVisibility(View.VISIBLE);
                } else {
                    seeMoreLessStatus = false;
                    seeMoreLessImage.setImageResource(R.drawable.down_arrow);
                    helpWebPhoneLayout.setVisibility(View.GONE);

                    //hide info
                    websiteHelperInfo.setVisibility(View.GONE);
                    phoneHelperInfo.setVisibility(View.GONE);
                }
            }
        });

        websiteHelper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneHelperInfo.setVisibility(View.GONE);
                if (websiteHelperInfo.getVisibility() == View.VISIBLE) {
                    websiteHelperInfo.setVisibility(View.GONE);
                } else {
                    websiteHelperInfo.setVisibility(View.VISIBLE);
                }
            }
        });

        phoneHelper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                websiteHelperInfo.setVisibility(View.GONE);
                if (phoneHelperInfo.getVisibility() == View.VISIBLE) {
                    phoneHelperInfo.setVisibility(View.GONE);
                } else {
                    phoneHelperInfo.setVisibility(View.VISIBLE);
                }
            }
        });

        parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (seeMoreLessStatus) {
                    seeMoreLessStatus = false;
                    seeMoreLessImage.setImageResource(R.drawable.down_arrow);
                    helpWebPhoneLayout.setVisibility(View.GONE);

                    //hide info
                    websiteHelperInfo.setVisibility(View.GONE);
                    phoneHelperInfo.setVisibility(View.GONE);
                }
            }
        });

        //show or hide if feature is available to user
        mainLayout = (LinearLayout) findViewById(R.id.main_layout);
        primaryLayout = findViewById(R.id.primary_layout);
        secondLayout = findViewById(R.id.second_layout);
        firstLayout = findViewById(R.id.first_layout);

        secondaryLayout = (LinearLayout) findViewById(R.id.secondary_layout);
        getWebsiteCallCount();

        if (isPremium()) {
            nonEmptyView();
            getCalls();
        } else {
            emptyView();
        }



        vmnCallAdapter.setAllowAudioPlay(new AllowAudioPlay() {
            @Override
            public boolean allowAudioPlay() {
                return allowCallPlayFlag;
            }

            @Override
            public void toggleAllowAudioPlayFlag(boolean setValue) {
                allowCallPlayFlag = setValue;
            }
        });

    }

    private boolean isPremium(){
        List<String> keys = session.getStoreWidgets();

        if (keys != null && keys.contains("CALLTRACKER")){
            return true;
        }

        return false;
    }

    private void showTrackedCalls() {

        binding.tableLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition()==0){
                    if (!selectedViewType.equals("ALL")) {
                        selectedViewType = "ALL";
                        updateRecyclerData(null);
                    }
                }else if (tab.getPosition()==1){
                    if (!selectedViewType.equals("MISSED")) {
                        selectedViewType = "MISSED";
                        updateRecyclerData(null);
                    }
                }else if (tab.getPosition()==2){
                    if (!selectedViewType.equals("CONNECTED")) {
                        selectedViewType = "CONNECTED";
                        updateRecyclerData(null);
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }


    private void getCalls() {
        Log.i(TAG, "getCalls: function called");
        stopApiCall = true;
        showProgress();
        final String startOffset = String.valueOf(offset);
        CallTrackerApis trackerApis = Constants.restAdapter.create(CallTrackerApis.class);
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("clientId", Constants.clientId);
        hashMap.put("fpid", sessionManager.getISEnterprise().equals("true") ? sessionManager.getFPParentId() : sessionManager.getFPID());
        hashMap.put("offset", startOffset);
        hashMap.put("limit", String.valueOf(100));
        hashMap.put("identifierType", sessionManager.getISEnterprise().equals("true") ? "MULTI" : "SINGLE");
        trackerApis.trackerCalls(hashMap, new Callback<ArrayList<VmnCallModel>>() {
            @Override
            public void success(ArrayList<VmnCallModel> vmnCallModels, Response response) {
                Log.i(TAG, "getCalls success: ");
                hideProgress();
                if (vmnCallModels == null || response.getStatus() != 200) {
                    Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    return;
                }
                int size = vmnCallModels.size();
                Log.v("getCalls", " " + size);
                stopApiCall = size < 100;
                updateRecyclerData(vmnCallModels);

                if (size != 0) {
                    offset += 100;
                }
                if (size < 1) {
                    emptyView();
                } else {
                    nonEmptyView();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgress();
                stopApiCall = false;
                Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateRecyclerData(ArrayList<VmnCallModel> newItems) {
        if (newItems != null) {
            int sizeOfList = headerList.size();
            int listSize = newItems.size();

            for (int i = 0; i < listSize; i++) {
                VmnCallModel model = newItems.get(i);
                headerList.add(model);
//                vmnCallAdapter.notifyItemInserted(sizeOfList + i);
            }
        }
        Log.i(TAG, "updateRecyclerData: header list size "+getSelectedTypeList(headerList).size());
        vmnCallAdapter.updateList(getSelectedTypeList(headerList));

    }

    private ArrayList<VmnCallModel> getSelectedTypeList(ArrayList<VmnCallModel> list) {
        ArrayList<VmnCallModel> selectedItems = new ArrayList<>();
        switch (selectedViewType) {
            case "ALL": {
                selectedItems = list;
                break;
            }
            case "MISSED": {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getCallStatus().equals("MISSED")) {
                        selectedItems.add(list.get(i));
                    }
                }
                break;
            }
            case "CONNECTED": {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getCallStatus().equals("CONNECTED")) {
                        selectedItems.add(list.get(i));
                    }
                }
                break;
            }
        }
        return selectedItems;
    }

    private void showEmptyScreen() {
        if (totalCallCount == 0) {
            findViewById(R.id.empty_layout).setVisibility(View.VISIBLE);
            ImageView imageView = (ImageView) findViewById(R.id.image_item);
            TextView mMainText = (TextView) findViewById(R.id.main_text);
            TextView mDescriptionText = (TextView) findViewById(R.id.description_text);

            imageView.setImageResource(R.drawable.icon_no_calls);
            mMainText.setText("You have no call records yet.");
            mDescriptionText.setText("Your customers haven't contacted\nyou on your call tracking number yet.");
        } else {
            findViewById(R.id.calls_details_layout).setVisibility(View.VISIBLE);
            viewCallLogCard.setVisibility(View.VISIBLE);
            viewCallLogCard.setOnClickListener(this);
        }
    }

    private void showProgress() {
        if (!vmnProgressBar.isShowing() && !isFinishing()) {
            vmnProgressBar.show();
        }
    }

    private void hideProgress() {
        if (vmnProgressBar.isShowing() && !isFinishing()) {
            vmnProgressBar.dismiss();
        }
    }

//    private void setVmnTotalCallCount() {
//        showProgress();
//        CallTrackerApis trackerApis = Constants.restAdapter.create(CallTrackerApis.class);
//        String type = sessionManager.getISEnterprise().equals("true") ? "MULTI" : "SINGLE";
//
//        trackerApis.getVmnSummary(Constants.clientId, sessionManager.getFPID(), type, new Callback<JsonObject>() {
//            @Override
//            public void success(JsonObject jsonObject, Response response) {
//                hideProgress();
//
//                if (jsonObject == null || response.getStatus() != 200) {
//                    Methods.showSnackBarNegative(VmnCallCardsActivity.this, getString(R.string.something_went_wrong));
//
//                } else {
//                    if (jsonObject.has("TotalCalls")) {
//                        String vmnTotalCalls = jsonObject.get("TotalCalls").getAsString();
//                        // oldcode
////                        totalCount.setText(vmnTotalCalls != null && !"null".equalsIgnoreCase(vmnTotalCalls) ? vmnTotalCalls : "0");
//                        if(vmnTotalCalls != null && !"null".equalsIgnoreCase(vmnTotalCalls)){
//                            totalCallCount = Integer.parseInt(vmnTotalCalls);
//                            allCountText.setText(vmnTotalCalls);
//                            potentialCallsText.setText("View potential calls ("+totalCallCount+")");
//                        }else{
//                            allCountText.setText("0");
//                        }
//                    }
//                    if (jsonObject.has("MissedCalls")) {
//                        String vmnMissedCalls = jsonObject.get("MissedCalls").getAsString();
//                        missedCountText.setText(vmnMissedCalls != null && !"null".equalsIgnoreCase(vmnMissedCalls) ? vmnMissedCalls : "0");
//                    }
//                    if (jsonObject.has("ReceivedCalls")) {
//                        String vmnReceivedCalls = jsonObject.get("ReceivedCalls").getAsString();
//                        receivedCountText.setText(vmnReceivedCalls != null && !"null".equalsIgnoreCase(vmnReceivedCalls) ? vmnReceivedCalls : "0");
//                    }
//                    getWebsiteCallCount();
//                }
//                showEmptyScreen();
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                hideProgress();
//                showEmptyScreen();
//                Methods.showSnackBarNegative(VmnCallCardsActivity.this, getString(R.string.something_went_wrong));
//            }
//        });
//    }

    private void getWebsiteCallCount() {
        showProgress();

        //oldcode
//        CallTrackerApis trackerApis = Constants.restAdapter.create(CallTrackerApis.class);
        CallTrackerApis trackerApis = new RestAdapter.Builder()
                .setEndpoint("https://riamemory.withfloats.com")
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setLog(new AndroidLog("ggg"))
                .build()
                .create(CallTrackerApis.class);

        trackerApis.getCallCountByType(session.getFpTag(), "POTENTIAL_CALLS", "WEB", new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                hideProgress();

                if (jsonObject == null || response.getStatus() != 200) {
                    Methods.showSnackBarNegative(VmnCallCardsActivity.this, getString(R.string.something_went_wrong));
                } else {
                    String callCount = jsonObject.get("POTENTIAL_CALLS").getAsString();
                    webCallCount.setText(callCount);
                    totalPotentialCallCount += Integer.parseInt(callCount);
                    potentialCallsText.setText("View potential calls (" + totalPotentialCallCount + ")");
                    getPhoneCallCount();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgress();
                showEmptyScreen();
                Methods.showSnackBarNegative(VmnCallCardsActivity.this, getString(R.string.something_went_wrong));
            }
        });
    }

    private void getPhoneCallCount() {
        showProgress();

        //old code
//        CallTrackerApis trackerApis = Constants.restAdapter.create(CallTrackerApis.class);
        CallTrackerApis trackerApis = new RestAdapter.Builder()
                .setEndpoint("https://riamemory.withfloats.com")
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setLog(new AndroidLog("ggg"))
                .build()
                .create(CallTrackerApis.class);

        trackerApis.getCallCountByType(session.getFpTag(), "POTENTIAL_CALLS", "MOBILE", new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                hideProgress();

                if (jsonObject == null || response.getStatus() != 200) {
                    Methods.showSnackBarNegative(VmnCallCardsActivity.this, getString(R.string.something_went_wrong));
                } else {
                    String callCount = jsonObject.get("POTENTIAL_CALLS").getAsString();
//                    webCallCount.setText(callCount);
                    phoneCallCount.setText(callCount);
                    totalPotentialCallCount += Integer.parseInt(callCount);
                    potentialCallsText.setText("View potential calls (" + totalPotentialCallCount + ")");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgress();
                showEmptyScreen();
                Methods.showSnackBarNegative(VmnCallCardsActivity.this, getString(R.string.something_went_wrong));
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(VmnCallCardsActivity.this, ShowVmnCallActivity.class);

        switch (v.getId()) {
            case R.id.card_view_view_calllog:
                if (totalCallCount == 0) {
                    Methods.showSnackBarNegative(VmnCallCardsActivity.this, "You do not have call logs.");
                    return;
                }
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
        }
    }


    private void initiateBuyFromMarketplace() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        String status = "Loading. Please wait...";
        progressDialog.setMessage(status);
        progressDialog.setCancelable(false);
        progressDialog.show();
        Intent intent = new Intent(this, UpgradeActivity.class);
        intent.putExtra("expCode", session.getFP_AppExperienceCode());
        intent.putExtra("fpName", session.getFPName());
        intent.putExtra("fpid", session.getFPID());
        intent.putExtra("fpTag", session.getFpTag());
        intent.putExtra("accountType", session.getFPDetails(GET_FP_DETAILS_CATEGORY));
        intent.putStringArrayListExtra("userPurchsedWidgets", Constants.StoreWidgets);
        if (session.getUserProfileEmail() != null) {
            intent.putExtra("email", session.getUserProfileEmail());
        } else {
            intent.putExtra("email", "ria@nowfloats.com");
        }
        if (session.getUserPrimaryMobile() != null) {
            intent.putExtra("mobileNo", session.getUserPrimaryMobile());
        } else {
            intent.putExtra("mobileNo", "9160004303");
        }
        intent.putExtra("profileUrl", session.getFPLogo());
        intent.putExtra("buyItemKey", "CALLTRACKER");
        startActivity(intent);
        new Handler().postDelayed(() -> {
            progressDialog.dismiss();
        }, 1000);
    }

    private void nonEmptyView() {
        binding.primaryLayout.setVisibility(View.VISIBLE);
        binding.childContainer.setVisibility(View.GONE);
    }


    private void emptyView() {
        binding.primaryLayout.setVisibility(View.GONE);
        binding.childContainer.setVisibility(View.VISIBLE);

    }

    @Override
    public void primaryButtonClicked() {
        initiateBuyFromMarketplace();
    }

    @Override
    public void secondaryButtonClicked() {

    }

    @Override
    public void ternaryButtonClicked() {

    }

    @Override
    public void appOnBackPressed() {

    }
}

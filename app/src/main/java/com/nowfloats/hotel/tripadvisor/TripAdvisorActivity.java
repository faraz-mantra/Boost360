package com.nowfloats.hotel.tripadvisor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boost.upgrades.UpgradeActivity;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.hotel.API.HotelAPIInterfaces;
import com.nowfloats.hotel.API.model.GetPlacesAround.GetPlacesAroundModel;
import com.nowfloats.hotel.API.model.GetTripAdvisorData.GetTripAdvisorData;
import com.nowfloats.hotel.placesnearby.PlacesNearByActivity;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import org.json.JSONObject;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;

public class TripAdvisorActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    public UserSessionManager session;
    RelativeLayout emptyLayout;
    TextView buyItemButton;
    LinearLayout primaryLayout;
    GetTripAdvisorData tripAdvisorData;
    SwitchCompat switchSetting;
    EditText widgetSnippet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_advisor);

        emptyLayout = findViewById(R.id.empty_layout);
        buyItemButton = findViewById(R.id.buy_item);
        primaryLayout = findViewById(R.id.primary_layout);
        switchSetting = findViewById(R.id.setting_switch);
        widgetSnippet = findViewById(R.id.widget_snippet);

        buyItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateBuyFromMarketplace();
            }
        });

        //setHeader
        setHeader();

        if (!Constants.StoreWidgets.contains("TRIPADVISOR-REVIEWS")){
            emptyLayout.setVisibility(View.GONE);
            primaryLayout.setVisibility(View.VISIBLE);
            loadData();
        }else{
            emptyLayout.setVisibility(View.VISIBLE);
            primaryLayout.setVisibility(View.GONE);
        }

    }

    private void loadData() {
        try {
            JSONObject query = new JSONObject();
            query.put("WebsiteId", "HOTELS");
            HotelAPIInterfaces APICalls = new RestAdapter.Builder()
                    .setEndpoint("https://webaction.api.boostkit.dev")
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setLog(new AndroidLog("ggg"))
                    .build()
                    .create(HotelAPIInterfaces.class);

            APICalls.getTripAdvisorData(query, 0, 1000, new Callback<GetTripAdvisorData>() {
                @Override
                public void success(GetTripAdvisorData getTripAdvisorData, Response response) {
                    if (getTripAdvisorData == null || response.getStatus() != 200) {
                        Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    tripAdvisorData = getTripAdvisorData;
                    updateUIforTripAdvisor();
                }

                @Override
                public void failure(RetrofitError error) {
                    Methods.showSnackBarNegative(TripAdvisorActivity.this, getString(R.string.something_went_wrong));
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void updateUIforTripAdvisor(){
        widgetSnippet.setText(tripAdvisorData.getData().get(0).getWidgetSnippet());
        if(tripAdvisorData.getData().get(0).getShowWidget()){
            switchSetting.setChecked(true);
        }else{
            switchSetting.setChecked(false);
        }
    }

    public void setHeader(){
        LinearLayout backButton;
        TextView title;

        title = findViewById(R.id.title);
        backButton = findViewById(R.id.back_button);
        title.setText("Tripadvisor Ratings");

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void showLoader(final String message) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(getApplicationContext());
                    progressDialog.setCanceledOnTouchOutside(false);
                }
                progressDialog.setMessage(message);
                progressDialog.show();
            }
        });
    }

    private void hideLoader() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void initiateBuyFromMarketplace() {
        ProgressDialog progressDialog = new ProgressDialog(TripAdvisorActivity.this);
        String status = "Loading. Please wait...";
        progressDialog.setMessage(status);
        progressDialog.setCancelable(false);
        progressDialog.show();
        Intent intent = new Intent(TripAdvisorActivity.this, UpgradeActivity.class);
        intent.putExtra("expCode", session.getFP_AppExperienceCode());
        intent.putExtra("fpName", session.getFPName());
        intent.putExtra("fpid", session.getFPID());
        intent.putExtra("loginid", session.getUserProfileId());
        if (session.getFPEmail() != null) {
            intent.putExtra("email", session.getFPEmail());
        } else {
            intent.putExtra("email", "ria@nowfloats.com");
        }
        if (session.getFPPrimaryContactNumber() != null) {
            intent.putExtra("mobileNo", session.getFPPrimaryContactNumber());
        } else {
            intent.putExtra("mobileNo", "9160004303");
        }
        intent.putExtra("profileUrl", session.getFPLogo());
        intent.putExtra("buyItemKey", "TRIPADVISOR-REVIEWS");
        startActivity(intent);
        new Handler().postDelayed(() -> {
            progressDialog.dismiss();
            finish();
        },1000);
    }


}
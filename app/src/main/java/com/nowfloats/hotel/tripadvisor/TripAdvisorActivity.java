package com.nowfloats.hotel.tripadvisor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.boost.upgrades.UpgradeActivity;
import com.google.gson.Gson;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.hotel.API.HotelAPIInterfaces;
import com.nowfloats.hotel.API.model.AddTripAdvisorData.ActionData;
import com.nowfloats.hotel.API.model.AddTripAdvisorData.AddTripAdvisorDataRequest;
import com.nowfloats.hotel.API.model.GetTripAdvisorData.Data;
import com.nowfloats.hotel.API.model.GetTripAdvisorData.GetTripAdvisorData;
import com.nowfloats.hotel.API.model.UpdateTripAdvisorData.UpdateTripAdvisorDataRequest;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import org.json.JSONObject;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;

import static com.nowfloats.util.Key_Preferences.GET_FP_DETAILS_CATEGORY;
import static com.thinksity.Specific.CONTACT_EMAIL_ID;
import static com.thinksity.Specific.CONTACT_PHONE_ID;

public class TripAdvisorActivity extends AppCompatActivity {

    public UserSessionManager session;
    RelativeLayout emptyLayout;
    TextView buyItemButton, saveButton;
    LinearLayout primaryLayout;
    Data tripAdvisorData = null;
    SwitchCompat switchSetting;
    EditText widgetSnippet;
    boolean editState = false;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_advisor);

        emptyLayout = findViewById(R.id.empty_layout);
        buyItemButton = findViewById(R.id.buy_item);
        primaryLayout = findViewById(R.id.primary_layout);
        switchSetting = findViewById(R.id.setting_switch);
        widgetSnippet = findViewById(R.id.widget_snippet);
        saveButton = findViewById(R.id.save_button);
        session = new UserSessionManager(this, this);

        buyItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateBuyFromMarketplace();
            }
        });

        switchSetting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    widgetSnippet.setTextColor(getResources().getColor(R.color.common_text_color));
                } else {
                    widgetSnippet.setTextColor(getResources().getColor(R.color.d9d9d9));
                }
                editState = true;
                saveButton.setVisibility(View.VISIBLE);
                widgetSnippet.clearFocus();
                Methods.hideKeyboard(TripAdvisorActivity.this);
            }
        });

        widgetSnippet.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    editState = true;
                    saveButton.setVisibility(View.VISIBLE);
                    widgetSnippet.setTextColor(getResources().getColor(R.color.common_text_color));
                } else {
                    widgetSnippet.setTextColor(getResources().getColor(R.color.d9d9d9));
                }
            }
        });

        widgetSnippet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editState = true;
                saveButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                widgetSnippet.clearFocus();
                Methods.hideKeyboard(TripAdvisorActivity.this);
                if (validateData()) {
                    if (tripAdvisorData == null) {
                        //call addtripadvisor api
                        addData();
                    } else {
                        if (editState) {
                            //call updateTripAdvisor api
                            updateData();
                        }

                    }
                }

            }
        });


        //setHeader
        setHeader();

        if (session.getStoreWidgets().contains("TRIPADVISOR-REVIEWS")){
            emptyLayout.setVisibility(View.GONE);
            primaryLayout.setVisibility(View.VISIBLE);
            loadData();
        } else {
            emptyLayout.setVisibility(View.VISIBLE);
            primaryLayout.setVisibility(View.GONE);
        }

    }

    private boolean validateData() {
        if (widgetSnippet.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.fields_are_empty), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void loadData() {
        try {
            JSONObject query = new JSONObject();
            query.put("WebsiteId", session.getFpTag());
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
                    if (getTripAdvisorData.getData().size() > 0) {
                        tripAdvisorData = getTripAdvisorData.getData().get(0);
                        updateUIforTripAdvisor();
                        saveButton.setVisibility(View.GONE);
                    } else {
                        saveButton.setVisibility(View.VISIBLE);
                    }
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

    private void addData() {
        try {
            ActionData actionData = new ActionData();
            actionData.setWidgetSnippet(widgetSnippet.getText().toString());
            actionData.setShowWidget(switchSetting.isChecked());

            AddTripAdvisorDataRequest requestBody = new AddTripAdvisorDataRequest();
            requestBody.setWebsiteId(session.getFpTag());
            requestBody.setActionData(actionData);

            HotelAPIInterfaces APICalls = new RestAdapter.Builder()
                    .setEndpoint("https://webaction.api.boostkit.dev")
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setLog(new AndroidLog("ggg"))
                    .build()
                    .create(HotelAPIInterfaces.class);


            APICalls.addTripAdvisorData(requestBody, new Callback<String>() {
                @Override
                public void success(String s, Response response) {
                    if (response.getStatus() != 200) {
                        Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Methods.showSnackBarPositive(TripAdvisorActivity.this, getString(R.string.successfully_added_trip_advisor));
                    finish();
                }

                @Override
                public void failure(RetrofitError error) {
                    if (error.getResponse().getStatus() == 200) {
                        Methods.showSnackBarPositive(TripAdvisorActivity.this, getString(R.string.successfully_added_trip_advisor));
                        finish();
                    } else {
                        Methods.showSnackBarNegative(TripAdvisorActivity.this, getString(R.string.something_went_wrong));
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateData() {
        try {
            ActionData actionData = new ActionData();
            actionData.setWidgetSnippet(widgetSnippet.getText().toString());
            actionData.setShowWidget(switchSetting.isChecked());

            UpdateTripAdvisorDataRequest requestBody = new UpdateTripAdvisorDataRequest();
            requestBody.setQuery("{_id:'" + tripAdvisorData.getId() + "'}");
            requestBody.setUpdateValue("{$set :" + new Gson().toJson(actionData) + "}");

            HotelAPIInterfaces APICalls = new RestAdapter.Builder()
                    .setEndpoint("https://webaction.api.boostkit.dev")
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setLog(new AndroidLog("ggg"))
                    .build()
                    .create(HotelAPIInterfaces.class);

            APICalls.updateTripAdvisorData(requestBody, new Callback<String>() {
                @Override
                public void success(String s, Response response) {
                    if (response.getStatus() != 200) {
                        Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Methods.showSnackBarPositive(TripAdvisorActivity.this, getString(R.string.successfully_updated_trip_advisor_details));
                    finish();
                }

                @Override
                public void failure(RetrofitError error) {
                    if (error.getResponse().getStatus() == 200) {
                        Methods.showSnackBarPositive(TripAdvisorActivity.this, getString(R.string.successfully_updated_trip_advisor_details));
                        finish();
                    } else {
                        Methods.showSnackBarNegative(TripAdvisorActivity.this, getString(R.string.something_went_wrong));
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void updateUIforTripAdvisor() {
        if (tripAdvisorData != null) {
            widgetSnippet.setText(tripAdvisorData.getWidgetSnippet());
            widgetSnippet.setTextColor(getResources().getColor(R.color.d9d9d9));
            if (tripAdvisorData.getShowWidget()) {
                switchSetting.setChecked(true);
            } else {
                switchSetting.setChecked(false);
            }
        }
    }

    public void setHeader() {
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
        intent.putExtra("fpTag", session.getFpTag());
        intent.putExtra("accountType", session.getFPDetails(GET_FP_DETAILS_CATEGORY));
        intent.putStringArrayListExtra("userPurchsedWidgets", Constants.StoreWidgets);
        if (session.getFPEmail() != null) {
            intent.putExtra("email", session.getFPEmail());
        } else {
            intent.putExtra("email", CONTACT_EMAIL_ID);
        }
        if (session.getUserPrimaryMobile() != null) {
            intent.putExtra("mobileNo", session.getUserPrimaryMobile());
        } else {
            intent.putExtra("mobileNo", CONTACT_PHONE_ID);
        }
        intent.putExtra("profileUrl", session.getFPLogo());
        intent.putExtra("buyItemKey", "TRIPADVISOR-REVIEWS");
        startActivity(intent);
        new Handler().postDelayed(() -> {
            progressDialog.dismiss();
            finish();
        }, 1000);
    }


}
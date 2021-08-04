package com.nowfloats.manufacturing.projectandteams.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boost.upgrades.UpgradeActivity;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.manufacturing.API.ManufacturingAPIInterfaces;
import com.nowfloats.manufacturing.API.model.GetProjects.GetProjectsData;
import com.nowfloats.manufacturing.API.model.GetTeams.GetTeamsData;
import com.nowfloats.manufacturing.projectandteams.ui.project.ProjectActivity;
import com.nowfloats.manufacturing.projectandteams.ui.teams.TeamsActivity;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.nowfloats.util.Utils;
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

public class ProjectAndTermsActivity extends AppCompatActivity {

    public UserSessionManager session;
    LinearLayout projectLayout, teamLayout;
    TextView projectTitle, teamTitle, buyItemButton;
    LinearLayout primaryLayout, secoundaryLayout;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_and_teams);

        initView();

        //setheader
        setHeader();
    }

    private void initView() {

        session = new UserSessionManager(this, this);
        primaryLayout = findViewById(R.id.primary_layout);
        secoundaryLayout = findViewById(R.id.secondary_layout);
        projectLayout = findViewById(R.id.project_layout);
        teamLayout = findViewById(R.id.team_layout);
        projectTitle = findViewById(R.id.project_title);
        teamTitle = findViewById(R.id.team_title);
        buyItemButton = findViewById(R.id.buy_item);

        buyItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateBuyFromMarketplace();
            }
        });

        projectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isNetworkConnected(ProjectAndTermsActivity.this)) {
                    Intent projectIntent = new Intent(ProjectAndTermsActivity.this, ProjectActivity.class);
                    startActivity(projectIntent);
                } else {
                    Methods.showSnackBarNegative(ProjectAndTermsActivity.this, getString(R.string.no_internet_connection));
                }
            }
        });

        teamLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isNetworkConnected(ProjectAndTermsActivity.this)) {
                    Intent teamsIntent = new Intent(ProjectAndTermsActivity.this, TeamsActivity.class);
                    startActivity(teamsIntent);
                } else {
                    Methods.showSnackBarNegative(ProjectAndTermsActivity.this, getString(R.string.no_internet_connection));
                }
            }
        });

        if (session.getStoreWidgets().contains("PROJECTTEAM")) {
            primaryLayout.setVisibility(View.VISIBLE);
            secoundaryLayout.setVisibility(View.GONE);
        } else {
            primaryLayout.setVisibility(View.GONE);
            secoundaryLayout.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (session.getStoreWidgets().contains("PROJECTTEAM")) {
            if (Utils.isNetworkConnected(ProjectAndTermsActivity.this)) {
                loadProjectsData();
                loadTeamData();
            } else {
                Methods.showSnackBarNegative(ProjectAndTermsActivity.this, getString(R.string.no_internet_connection));
            }
        }
    }

    public void setHeader() {
        LinearLayout backButton;
        TextView title;

        title = findViewById(R.id.title);
        backButton = findViewById(R.id.back_button);
        title.setText("Projects & Teams");

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    void loadProjectsData() {
        try {
            JSONObject query = new JSONObject();
            query.put("WebsiteId", session.getFpTag());
            ManufacturingAPIInterfaces APICalls = new RestAdapter.Builder()
                    .setEndpoint("https://webaction.api.boostkit.dev")
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setLog(new AndroidLog("ggg"))
                    .build()
                    .create(ManufacturingAPIInterfaces.class);

            APICalls.getProjectsList(query, 0, 1000, new Callback<GetProjectsData>() {
                @Override
                public void success(GetProjectsData getProjectsData, Response response) {
                    if (getProjectsData == null || response.getStatus() != 200) {
                        Toast.makeText(ProjectAndTermsActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    projectTitle.setText("Projects Listing (" + getProjectsData.getData().size() + ")");
                }

                @Override
                public void failure(RetrofitError error) {
                    Methods.showSnackBarNegative(ProjectAndTermsActivity.this, getString(R.string.something_went_wrong));
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void loadTeamData() {
        try {
            JSONObject query = new JSONObject();
            query.put("WebsiteId", session.getFpTag());
            ManufacturingAPIInterfaces APICalls = new RestAdapter.Builder()
                    .setEndpoint("https://webaction.api.boostkit.dev")
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setLog(new AndroidLog("ggg"))
                    .build()
                    .create(ManufacturingAPIInterfaces.class);

            APICalls.getTeamsList(query, 0, 1000, new Callback<GetTeamsData>() {
                @Override
                public void success(GetTeamsData getTeamsData, Response response) {
                    if (getTeamsData == null || response.getStatus() != 200) {
                        Toast.makeText(ProjectAndTermsActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    teamTitle.setText("Team Listing (" + getTeamsData.getData().size() + ")");
                }

                @Override
                public void failure(RetrofitError error) {
                    Methods.showSnackBarNegative(ProjectAndTermsActivity.this, getString(R.string.something_went_wrong));
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initiateBuyFromMarketplace() {
        ProgressDialog progressDialog = new ProgressDialog(ProjectAndTermsActivity.this);
        String status = "Loading. Please wait...";
        progressDialog.setMessage(status);
        progressDialog.setCancelable(false);
        progressDialog.show();
        Intent intent = new Intent(ProjectAndTermsActivity.this, UpgradeActivity.class);
        intent.putExtra("expCode", session.getFP_AppExperienceCode());
        intent.putExtra("fpName", session.getFPName());
        intent.putExtra("fpid", session.getFPID());
        intent.putExtra("fpTag", session.getFpTag());
        intent.putExtra("accountType", session.getFPDetails(GET_FP_DETAILS_CATEGORY));
        intent.putStringArrayListExtra("userPurchsedWidgets", Constants.StoreWidgets);
        if (session.getUserProfileEmail() != null) {
            intent.putExtra("email", session.getUserProfileEmail());
        } else {
            intent.putExtra("email", CONTACT_EMAIL_ID);
        }
        if (session.getUserPrimaryMobile() != null) {
            intent.putExtra("mobileNo", session.getUserPrimaryMobile());
        } else {
            intent.putExtra("mobileNo", CONTACT_PHONE_ID);
        }
        intent.putExtra("profileUrl", session.getFPLogo());
        intent.putExtra("buyItemKey", "PROJECTTEAM");
        startActivity(intent);
        new Handler().postDelayed(() -> {
            progressDialog.dismiss();
            finish();
        }, 1000);
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

}
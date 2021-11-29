package com.nowfloats.manufacturing.projectandteams.ui.project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boost.upgrades.UpgradeActivity;
import com.framework.views.fabButton.FloatingActionButton;
import com.framework.views.zero.old.AppFragmentZeroCase;
import com.framework.views.zero.old.AppOnZeroCaseClicked;
import com.framework.views.zero.old.AppRequestZeroCaseBuilder;
import com.framework.views.zero.old.AppZeroCases;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.manufacturing.API.ManufacturingAPIInterfaces;
import com.nowfloats.manufacturing.API.model.DeleteProject.DeleteProjectData;
import com.nowfloats.manufacturing.API.model.GetProjects.Data;
import com.nowfloats.manufacturing.API.model.GetProjects.GetProjectsData;
import com.nowfloats.manufacturing.projectandteams.Interfaces.ProjectActivityListener;
import com.nowfloats.manufacturing.projectandteams.adapter.ProjectAdapter;
import com.nowfloats.util.Methods;
import com.nowfloats.util.Utils;
import com.thinksity.R;
import com.thinksity.Specific;
import com.thinksity.databinding.ActivityProjectCategoryBinding;
import com.thinksity.databinding.ActivityTeamCategoryBinding;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

public class ProjectActivity extends AppCompatActivity implements ProjectActivityListener, AppOnZeroCaseClicked {

  UserSessionManager session;
  List<Data> dataList;
  private RecyclerView recyclerView;
  private ProjectAdapter adapter;
  private AppFragmentZeroCase appFragmentZeroCase;
  private ActivityProjectCategoryBinding binding;
  private UserSessionManager userSessionManager;
  private String WIDGET_KEY = "PROJECTTEAM";
  private static final String TAG = "TeamsActivity";

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    userSessionManager = new UserSessionManager(this, this);
    ;

    binding = DataBindingUtil.setContentView(this, R.layout.activity_project_category);
    appFragmentZeroCase = new AppRequestZeroCaseBuilder(AppZeroCases.PROJECTS, this, this, isPremium()).getRequest().build();
    getSupportFragmentManager().beginTransaction().replace(binding.childContainer.getId(), appFragmentZeroCase).commit();

    initView();
  }

  private Boolean isPremium() {
    return userSessionManager.getStoreWidgets().contains(WIDGET_KEY);
  }


  public void initView() {

    session = new UserSessionManager(this, this);
    recyclerView = findViewById(R.id.recycler);
    adapter = new ProjectAdapter(new ArrayList(), this, this);

    recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
    recyclerView.setAdapter(adapter);

    //setHeader
    setHeader();

  }

  void loadData() {
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
            Toast.makeText(ProjectActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            return;
          }


          dataList = getProjectsData.getData();
          dataList.clear();
          if (dataList.size() > 0) {
            updateRecyclerView();
            nonEmptyView();
          } else {
            emptyView();
          }
        }

        @Override
        public void failure(RetrofitError error) {
          Methods.showSnackBarNegative(ProjectActivity.this, getString(R.string.something_went_wrong));
        }
      });

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Override
  protected void onResume() {
    super.onResume();
    if (Utils.isNetworkConnected(this)) {
      if (isPremium()) {
        nonEmptyView();
        loadData();
      } else {
        emptyView();
      }
    } else {
      Methods.showSnackBarNegative(ProjectActivity.this, getString(R.string.no_internet_connection));
    }
  }

  private void updateRecyclerView() {
    adapter.menuOption(-1, false);
    adapter.updateList(dataList);
    adapter.notifyDataSetChanged();
  }

  @Override
  public void itemMenuOptionStatus(int pos, boolean status) {
    adapter.menuOption(pos, status);
    adapter.notifyDataSetChanged();
  }

  @Override
  public void editOptionClicked(Data data) {
    adapter.menuOption(-1, false);
    adapter.notifyDataSetChanged();
    Intent projectIntent = new Intent(ProjectActivity.this, ProjectDetailsActivity.class);
    projectIntent.putExtra("ScreenState", "edit");
    projectIntent.putExtra("data", new Gson().toJson(data));
    startActivity(projectIntent);
  }

  @Override
  public void deleteOptionClicked(Data data) {
    adapter.menuOption(-1, false);
    adapter.notifyDataSetChanged();
    try {
      DeleteProjectData requestBody = new DeleteProjectData();
      requestBody.setQuery("{_id:'" + data.getId() + "'}");
      requestBody.setUpdateValue("{$set : {IsArchived: true }}");
      requestBody.setMulti(true);

      ManufacturingAPIInterfaces APICalls = new RestAdapter.Builder()
          .setEndpoint("https://webaction.api.boostkit.dev")
          .setLogLevel(RestAdapter.LogLevel.FULL)
          .setLog(new AndroidLog("ggg"))
          .setConverter(new GsonConverter(new GsonBuilder().setLenient().create()))
          .build()
          .create(ManufacturingAPIInterfaces.class);

      APICalls.deleteProjectData(requestBody, new Callback<String>() {
        @Override
        public void success(String data, Response response) {
          if (response != null && response.getStatus() == 200) {
            Log.d("deletePlacesAround ->", response.getBody().toString());
            Methods.showSnackBarPositive(ProjectActivity.this, getString(R.string.successfully_deleted_));
            loadData();
          } else {
            Methods.showSnackBarNegative(ProjectActivity.this, getString(R.string.something_went_wrong));
          }
        }

        @Override
        public void failure(RetrofitError error) {
          if (error.getResponse().getStatus() == 200) {
            Methods.showSnackBarPositive(ProjectActivity.this, getString(R.string.successfully_deleted_));
            loadData();
          } else {
            Methods.showSnackBarNegative(ProjectActivity.this, getString(R.string.something_went_wrong));
          }
        }
      });

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void setHeader() {
    LinearLayout backButton;
    ImageView rightIcon;
    TextView title;
    FloatingActionButton btnAdd;

    title = findViewById(R.id.title);
    backButton = findViewById(R.id.back_button);
    btnAdd = findViewById(R.id.btn_add);
    rightIcon = findViewById(R.id.right_icon);
    title.setText("Projects");
    rightIcon.setVisibility(View.INVISIBLE);
    btnAdd.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        addProject();
      }
    });

    backButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });
  }

  private void addProject() {
    Intent projectIntent = new Intent(ProjectActivity.this, ProjectDetailsActivity.class);
    projectIntent.putExtra("ScreenState", "new");
    startActivity(projectIntent);
  }

  @Override
  public void primaryButtonClicked() {
    if (isPremium()) {
      addProject();
    } else {
      initiateBuyFromMarketplace();
    }
  }

  @Override
  public void secondaryButtonClicked() {
    Toast.makeText(this, getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();
  }

  @Override
  public void ternaryButtonClicked() {

  }

  @Override
  public void appOnBackPressed() {

  }

  private void nonEmptyView() {
    binding.mainlayout.setVisibility(View.VISIBLE);
    binding.childContainer.setVisibility(View.GONE);
  }


  private void emptyView() {
    binding.mainlayout.setVisibility(View.GONE);
    binding.childContainer.setVisibility(View.VISIBLE);

  }

  private void initiateBuyFromMarketplace() {

    ProgressDialog progressDialog = new android.app.ProgressDialog((this));
    String status = "Loading. Please wait...";
    progressDialog.setMessage(status);
    progressDialog.setCancelable(false);
    progressDialog.show();
    Intent intent = new Intent(this, UpgradeActivity.class);
    intent.putExtra("expCode", userSessionManager.getFP_AppExperienceCode());
    intent.putExtra("fpName", userSessionManager.getFPName());
    intent.putExtra("fpid", userSessionManager.getFPID());
    intent.putExtra("loginid", userSessionManager.getUserProfileId());
    intent.putStringArrayListExtra("userPurchsedWidgets", new ArrayList(session.getStoreWidgets()));
    intent.putExtra("fpTag", userSessionManager.getFpTag());
    if (userSessionManager.getUserProfileEmail() != null) {
      intent.putExtra("email", userSessionManager.getUserProfileEmail());
    } else {
      intent.putExtra("email", Specific.CONTACT_EMAIL_ID);
    }
    if (userSessionManager.getUserPrimaryMobile() != null) {
      intent.putExtra("mobileNo", userSessionManager.getUserPrimaryMobile());
    } else {
      intent.putExtra("mobileNo", Specific.CONTACT_PHONE_ID);
    }
    intent.putExtra("profileUrl", userSessionManager.getFPLogo());
    intent.putExtra("buyItemKey", WIDGET_KEY);
    startActivity(intent);
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        progressDialog.dismiss();
        finish();
      }
    }, 1000);

  }
}
package com.nowfloats.manufacturing.digitalbrochures;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boost.upgrades.UpgradeActivity;
import com.framework.views.fabButton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.manufacturing.API.ManufacturingAPIInterfaces;
import com.nowfloats.manufacturing.API.model.DeleteBrochures.DeleteBrochuresData;
import com.nowfloats.manufacturing.API.model.DeleteProject.DeleteProjectData;
import com.nowfloats.manufacturing.API.model.GetBrochures.Data;
import com.nowfloats.manufacturing.API.model.GetBrochures.GetBrochuresData;
import com.nowfloats.manufacturing.digitalbrochures.Interfaces.DigitalBrochuresListener;
import com.nowfloats.manufacturing.digitalbrochures.adapter.DigitalBrochuresAdapter;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.nowfloats.util.Utils;
import com.thinksity.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

import static com.nowfloats.util.Key_Preferences.GET_FP_DETAILS_CATEGORY;


public class DigitalBrochuresActivity extends AppCompatActivity implements DigitalBrochuresListener {

  public static TextView headerText;
  public UserSessionManager session;
  RelativeLayout emptyLayout;
  RecyclerView recyclerView;
  List<Data> dataList;
  DigitalBrochuresAdapter adapter;
  TextView buyButton;
  private ProgressDialog progressDialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_digital_brochures);
    initView();
  }

  private void initView() {
    session = new UserSessionManager(this, this);
    buyButton = findViewById(R.id.buy_item);
    emptyLayout = findViewById(R.id.empty_layout);
    recyclerView = findViewById(R.id.digital_brochure_recycler);
    adapter = new DigitalBrochuresAdapter(new ArrayList(), this);

    recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
    recyclerView.setAdapter(adapter);

    buyButton.setOnClickListener(v -> initiateBuyFromMarketplace());

    //setheader
    setHeader();

    if (session.getStoreWidgets().contains("BROCHURE")) {
      recyclerView.setVisibility(View.VISIBLE);
      emptyLayout.setVisibility(View.GONE);
    } else {
      recyclerView.setVisibility(View.GONE);
      emptyLayout.setVisibility(View.VISIBLE);
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (session.getStoreWidgets().contains("BROCHURE")) {
      if (Utils.isNetworkConnected(DigitalBrochuresActivity.this)) {
        loadData();
      } else {
        Methods.showSnackBarNegative(DigitalBrochuresActivity.this, getString(R.string.no_internet_connection));
      }
    }
  }

  private void loadData() {
    try {
      JSONObject query = new JSONObject();
      query.put("WebsiteId", session.getFpTag());
      ManufacturingAPIInterfaces APICalls = new RestAdapter.Builder()
          .setEndpoint("https://webaction.api.boostkit.dev")
          .setLogLevel(RestAdapter.LogLevel.FULL)
          .setLog(new AndroidLog("ggg"))
          .build()
          .create(ManufacturingAPIInterfaces.class);

      APICalls.getBrochuresList(query, 0, 1000, new Callback<GetBrochuresData>() {
        @Override
        public void success(GetBrochuresData getBrochuresData, Response response) {
          if (getBrochuresData == null || response.getStatus() != 200) {
            Toast.makeText(DigitalBrochuresActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            return;
          }

          dataList = getBrochuresData.getData();
          updateRecyclerView();
        }

        @Override
        public void failure(RetrofitError error) {
          Methods.showSnackBarNegative(DigitalBrochuresActivity.this, getString(R.string.something_went_wrong));
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
    title.setText(getResources().getString(R.string.digital_brochures));
    rightIcon.setVisibility(View.INVISIBLE);
    if (session.getStoreWidgets().contains("BROCHURE")) {
      btnAdd.setVisibility(View.VISIBLE);
      btnAdd.setOnClickListener(v -> {
        Intent brochuresIntent = new Intent(DigitalBrochuresActivity.this, DigitalBrochuresDetailsActivity.class);
        brochuresIntent.putExtra("ScreenState", "new");
        startActivity(brochuresIntent);
      });
    } else btnAdd.setVisibility(View.GONE);

    backButton.setOnClickListener(v -> onBackPressed());
  }

  private void showLoader(final String message) {

    runOnUiThread(() -> {
      if (progressDialog == null) {
        progressDialog = new ProgressDialog(getApplicationContext());
        progressDialog.setCanceledOnTouchOutside(false);
      }
      progressDialog.setMessage(message);
      progressDialog.show();
    });
  }

  private void hideLoader() {

    runOnUiThread(() -> {
      if (progressDialog != null && progressDialog.isShowing()) {
        progressDialog.dismiss();
      }
    });
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
    Intent brochuresIntent = new Intent(DigitalBrochuresActivity.this, DigitalBrochuresDetailsActivity.class);
    brochuresIntent.putExtra("ScreenState", "edit");
    brochuresIntent.putExtra("data", new Gson().toJson(data));
    startActivity(brochuresIntent);
  }

  @Override
  public void deleteOptionClicked(Data data) {
    adapter.menuOption(-1, false);
    adapter.notifyDataSetChanged();
    try {
      DeleteBrochuresData requestBody = new DeleteBrochuresData();
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

      APICalls.deleteBrochuresData(requestBody, new Callback<String>() {
        @Override
        public void success(String data, Response response) {
          if (response != null && response.getStatus() == 200) {
            Log.d("deletePlacesAround ->", response.getBody().toString());
            Methods.showSnackBarPositive(DigitalBrochuresActivity.this, getString(R.string.successfully_deleted_));
            loadData();
          } else {
            Methods.showSnackBarNegative(DigitalBrochuresActivity.this, getString(R.string.something_went_wrong));
          }
        }

        @Override
        public void failure(RetrofitError error) {
          if (error.getResponse().getStatus() == 200) {
            Methods.showSnackBarPositive(DigitalBrochuresActivity.this, getString(R.string.successfully_deleted_));
            loadData();
          } else {
            Methods.showSnackBarNegative(DigitalBrochuresActivity.this, getString(R.string.something_went_wrong));
          }
        }
      });

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

    private void initiateBuyFromMarketplace() {
        ProgressDialog progressDialog = new ProgressDialog(DigitalBrochuresActivity.this);
        String status = "Loading. Please wait...";
        progressDialog.setMessage(status);
        progressDialog.setCancelable(false);
        progressDialog.show();
        Intent intent = new Intent(DigitalBrochuresActivity.this, UpgradeActivity.class);
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
        intent.putExtra("buyItemKey", "BROCHURE");
        startActivity(intent);
        new Handler().postDelayed(() -> {
            progressDialog.dismiss();
            finish();
        }, 1000);
    }
}
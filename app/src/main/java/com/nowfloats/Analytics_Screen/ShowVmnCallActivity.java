package com.nowfloats.Analytics_Screen;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.framework.views.customViews.CustomToolbar;
import com.nowfloats.Analytics_Screen.API.CallTrackerApis;
import com.nowfloats.Analytics_Screen.model.VmnCallModel;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Admin on 27-04-2017.
 */

public class ShowVmnCallActivity extends AppCompatActivity implements VmnCall_v2Adapter.RequestPermission, View.OnClickListener {

  final static int REQUEST_PERMISSION = 202;
  CustomToolbar toolbar;
  RecyclerView mRecyclerView;
  UserSessionManager sessionManager;
  LinearLayoutManager linearLayoutManager;
  VmnCall_v2Adapter vmnCallAdapter;
  ArrayList<VmnCallModel> headerList = new ArrayList<>();
  boolean stopApiCall;
  ProgressBar progressBar;
  Button loadButton;
  private int offset = 0;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_vmn_calls);
    MixPanelController.track(MixPanelController.VMN_CALL_TRACKER_LOGS, null);

    toolbar = (CustomToolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    mRecyclerView = (RecyclerView) findViewById(R.id.rv_list);
    progressBar = (ProgressBar) findViewById(R.id.progressbar);

    loadButton = (Button) findViewById(R.id.btn_load);
    loadButton.setOnClickListener(this);

    sessionManager = new UserSessionManager(this, this);
    linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    mRecyclerView.setLayoutManager(linearLayoutManager);
    mRecyclerView.setHasFixedSize(true);
    vmnCallAdapter = new VmnCall_v2Adapter(this, headerList);
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
    if (getSupportActionBar() != null) {
      setTitle("Call Logs");
      getSupportActionBar().setDisplayShowHomeEnabled(true);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    getCalls();
  }

  private void getCalls() {
    stopApiCall = true;
    showProgress();
    final String startOffset = String.valueOf(offset);
    CallTrackerApis trackerApis = Constants.restAdapter.create(CallTrackerApis.class);
    Map<String, String> hashMap = new HashMap<>();
    hashMap.put("clientId", Constants.clientId);
    hashMap.put("fpid", sessionManager.getFPID());
    hashMap.put("offset", startOffset);
    hashMap.put("identifierType", sessionManager.getISEnterprise().equals("true") ? "MULTI" : "SINGLE");
    trackerApis.trackerCalls(hashMap, new Callback<ArrayList<VmnCallModel>>() {
      @Override
      public void success(ArrayList<VmnCallModel> vmnCallModels, Response response) {
        hideProgress();
        if (vmnCallModels == null || response.getStatus() != 200) {
          Toast.makeText(ShowVmnCallActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
          return;
        }
        int size = vmnCallModels.size();
        stopApiCall = size < 10;
        saveWithViewType(vmnCallModels);

        if (size != 0) {
          offset += 10;
        }
      }

      @Override
      public void failure(RetrofitError error) {
        hideProgress();
        stopApiCall = false;
        Toast.makeText(ShowVmnCallActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
      }
    });
  }

  // making key for each item from DATE and store that for headers only
  private void saveWithViewType(ArrayList<VmnCallModel> list) {
    String oldKey = "";
    int headerSize = 0;
    Calendar c = Calendar.getInstance();
    Calendar temp = Calendar.getInstance();
    int day = c.get(Calendar.DAY_OF_YEAR);

    int sizeOfList = headerList.size();
    if (sizeOfList > 0) {
      String Sdate = headerList.get(sizeOfList - 1).getCallDateTime();
      String date = Methods.getFormattedDate(Sdate);
      if (Sdate.contains("/Date")) {
        Sdate = Sdate.replace("/Date(", "").replace(")/", "");
      }
      Long epochTime = Long.parseLong(Sdate);
      temp.setTimeInMillis(epochTime);
      oldKey = date.substring(0, date.indexOf(" at"));
      if (c.get(Calendar.YEAR) != temp.get(Calendar.YEAR)) {

      } else if (temp.get(Calendar.DAY_OF_YEAR) == day) {
        oldKey = "Today, " + oldKey;
      } else if (day == (temp.get(Calendar.DAY_OF_YEAR) + 1)) {
        oldKey = "Yesterday, " + oldKey;
      }
    }
    int listSize = list.size();

    for (int i = 0; i < listSize; i++) {
      VmnCallModel model = list.get(i);
      String Sdate = model.getCallDateTime();
      String date = Methods.getFormattedDate(Sdate);
      String key = date.substring(0, date.indexOf(" at"));

      if (Sdate.contains("/Date")) {
        Sdate = Sdate.replace("/Date(", "").replace(")/", "");
      }

      Long epochTime = Long.parseLong(Sdate);
      temp.setTimeInMillis(epochTime);
      if (c.get(Calendar.YEAR) != temp.get(Calendar.YEAR)) {

      } else if (temp.get(Calendar.DAY_OF_YEAR) == day) {
        key = "Today, " + key;
      } else if ((temp.get(Calendar.DAY_OF_YEAR) + 1) == day) {
        key = "Yesterday, " + key;
      }
      if (!key.equals(oldKey)) {
        oldKey = key;
        VmnCallModel tempModel = new VmnCallModel();
        tempModel.setViewType(0);
        tempModel.setCallDateTime(key);
        headerList.add(tempModel);
        vmnCallAdapter.notifyItemInserted(sizeOfList + headerSize + i);
        headerSize++;
      }
      model.setViewType(1);
      headerList.add(model);
      vmnCallAdapter.notifyItemInserted(sizeOfList + headerSize + i);
    }
    //vmnCallAdapter.notifyDataSetChanged();
        /*vmnCallAdapter = new VmnCall_v2Adapter(this,headerList);
        mRecyclerView.setAdapter(vmnCallAdapter);*/
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

    if (vmnCallAdapter != null && vmnCallAdapter.connectToVmn != null) {
      vmnCallAdapter.connectToVmn.releaseResources();
    }
    super.onBackPressed();
    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
  }

  public void gotoSetting() {
    Intent intent = new Intent();
    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
    Uri uri = Uri.fromParts("package", getPackageName(), null);
    intent.setData(uri);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
  }

  private void showProgress() {
    progressBar.setVisibility(View.VISIBLE);
  }

  private void hideProgress() {
    progressBar.setVisibility(View.GONE);
  }

  @Override
  public void requestStoragePermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
    } else {
      new MaterialDialog.Builder(this)
          .title("Recording Download")
          .content(R.string.we_need_external_storage_permission_to_download_this_file)
          .negativeColorRes(R.color.gray_transparent)
          .negativeText(getString(R.string.cancel))
          .positiveColorRes(R.color.primary_color)
          .positiveText(getString(R.string.open_setting))
          .callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
              super.onPositive(dialog);
              dialog.dismiss();
              gotoSetting();
              dialog.dismiss();
            }

            @Override
            public void onNegative(MaterialDialog dialog) {
              super.onNegative(dialog);
              dialog.dismiss();
            }
          })
          .show();
    }
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_load:
        getCalls();
        break;
    }
  }

}

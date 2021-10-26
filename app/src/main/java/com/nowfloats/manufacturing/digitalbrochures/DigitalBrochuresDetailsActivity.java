package com.nowfloats.manufacturing.digitalbrochures;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.manufacturing.API.ManufacturingAPIInterfaces;
import com.nowfloats.manufacturing.API.UploadPdfFile;
import com.nowfloats.manufacturing.API.model.AddBrochures.ActionData;
import com.nowfloats.manufacturing.API.model.AddBrochures.AddBrochuresData;
import com.nowfloats.manufacturing.API.model.AddBrochures.Uploadpdf;
import com.nowfloats.manufacturing.API.model.DeleteBrochures.DeleteBrochuresData;
import com.nowfloats.manufacturing.API.model.GetBrochures.Data;
import com.nowfloats.manufacturing.API.model.UpdateBrochures.UpdateBrochuresData;
import com.nowfloats.manufacturing.digitalbrochures.Interfaces.DigitalBrochuresDetailsListener;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.util.concurrent.ExecutionException;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;


public class DigitalBrochuresDetailsActivity extends AppCompatActivity implements DigitalBrochuresDetailsListener {

  private final int gallery_req_id = 0;
  String ScreenType = "", itemId = "";
  EditText nameText, descriptionText, attachedURLText;
  TextView saveButton;
  LinearLayout uploadImageButton, attachBrochureEmptyLayout, fileSelectedLayout, removePdfButton;
  String documentPdfUrl = "";
  UserSessionManager session;
  Data existingItemData;
  int SELECT_PDF = 123;
  View dummyView1;
  String path = null;
  private ProgressDialog progressDialog;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_digital_brochures_details);
    initView();
  }


  public void initView() {

    session = new UserSessionManager(this, this);
    nameText = findViewById(R.id.name);
    descriptionText = findViewById(R.id.description);
    attachedURLText = findViewById(R.id.attached_url);
    saveButton = findViewById(R.id.save_review);
    uploadImageButton = findViewById(R.id.upload_image_button);
    attachBrochureEmptyLayout = findViewById(R.id.attach_brochure_empty_layout);
    fileSelectedLayout = findViewById(R.id.file_selected_layout);
    dummyView1 = findViewById(R.id.dummy_view1);
    dummyView1.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    removePdfButton = findViewById(R.id.remove_pdf);

    progressDialog = new ProgressDialog(this);
    progressDialog.setCancelable(false);

    removePdfButton.setOnClickListener(v -> {
      attachBrochureEmptyLayout.setVisibility(View.VISIBLE);
      fileSelectedLayout.setVisibility(View.GONE);
      documentPdfUrl = "";
      attachedURLText.setText("");
    });

    saveButton.setOnClickListener(v -> {
      if (!TextUtils.isEmpty(path)) {
        showLoader("Uploading document.Please Wait...");
        new Handler().postDelayed(this::uploadPdfFileToServer, 200);
      } else {
        uploadDataToServer();
      }
    });

    uploadImageButton.setOnClickListener(v -> {
      if (ActivityCompat.checkSelfPermission(DigitalBrochuresDetailsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
      ActivityCompat.checkSelfPermission(DigitalBrochuresDetailsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(DigitalBrochuresDetailsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, gallery_req_id);
        return;
      }
      Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
      intent.setType("application/pdf");
      intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"application/pdf"});
      intent.addCategory(Intent.CATEGORY_OPENABLE);
      startActivityForResult(Intent.createChooser(intent, getString(R.string.select_a_pdf)), SELECT_PDF);
    });


    attachedURLText.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        documentPdfUrl = s.toString();
      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });

    //setheader
    setHeader();

    Bundle extra = getIntent().getExtras();
    ScreenType = extra.getString("ScreenState");
    if (ScreenType != null && ScreenType.equals("edit")) {
      displayData();
    }

    attachBrochureEmptyLayout.setVisibility(View.VISIBLE);
    fileSelectedLayout.setVisibility(View.GONE);
  }

  private void displayData() {
    existingItemData = new Gson().fromJson(getIntent().getStringExtra("data"), Data.class);

    itemId = existingItemData.getId();
    documentPdfUrl = existingItemData.getUploadpdf().getUrl();
    nameText.setText(existingItemData.getTitle());
    descriptionText.setText(existingItemData.getUploadpdf().getDescription());
    attachedURLText.setText(documentPdfUrl);


//        if(!documentPdfUrl.isEmpty()){
//            attachBrochureEmptyLayout.setVisibility(View.GONE);
//            fileSelectedLayout.setVisibility(View.VISIBLE);
//        }else{
//            attachBrochureEmptyLayout.setVisibility(View.VISIBLE);
//            fileSelectedLayout.setVisibility(View.GONE);
//        }
  }

  void createNewTeamsAPI() {
    try {

      if (validateInput()) {
        showLoader(getString(R.string.creating_record_please_wait));
        ActionData actionData = new ActionData();
        actionData.setTitle(nameText.getText().toString());

        Uploadpdf uploadpdf = new Uploadpdf();
        uploadpdf.setUrl(documentPdfUrl);
        uploadpdf.setDescription(descriptionText.getText().toString());

        actionData.setUploadpdf(uploadpdf);

        AddBrochuresData request = new AddBrochuresData();
        request.setWebsiteId(session.getFpTag());
        request.setActionData(actionData);

        ManufacturingAPIInterfaces APICalls = new RestAdapter.Builder()
            .setEndpoint("https://webaction.api.boostkit.dev")
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setLog(new AndroidLog("ggg"))
            .build()
            .create(ManufacturingAPIInterfaces.class);

        APICalls.addBrochuresData(request, new Callback<String>() {
          @Override
          public void success(String s, Response response) {
            hideLoader();
            if (response.getStatus() != 200) {
              Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
              return;
            }
            Methods.showSnackBarPositive(DigitalBrochuresDetailsActivity.this, getString(R.string.successfully_added_team_details));
            onBackPressed();
          }

          @Override
          public void failure(RetrofitError error) {
            hideLoader();
            Methods.showSnackBarNegative(DigitalBrochuresDetailsActivity.this, getString(R.string.something_went_wrong));
          }
        });

      }
    } catch (Exception e) {
      hideLoader();
      e.printStackTrace();
    }

  }

  private boolean validateInput() {
    if (nameText.getText().toString().isEmpty() || descriptionText.getText().toString().isEmpty()) {
      Toast.makeText(getApplicationContext(), getString(R.string.fields_are_empty), Toast.LENGTH_LONG).show();
      return false;
    }

    if (!Patterns.WEB_URL.matcher(documentPdfUrl).matches()) {
      Toast.makeText(getApplicationContext(), getString(R.string.invalid_link_pdf_document), Toast.LENGTH_LONG).show();
      return false;
    }

//        if(documentPdfUrl.length() < 7 || !documentPdfUrl.substring(0,7).equals("http://")){
//            Toast.makeText(getApplicationContext(), "Invalid PDF Link document", Toast.LENGTH_LONG).show();
//            return false;
//        }

    return true;
  }


  void updateExistingTeamsAPI() {
    try {

      if (validateInput()) {
        showLoader(getString(R.string.updating_record_please_wait));
        ActionData actionData = new ActionData();
        actionData.setTitle(nameText.getText().toString());

        Uploadpdf uploadpdf = new Uploadpdf();
        uploadpdf.setUrl(documentPdfUrl);
        uploadpdf.setDescription(descriptionText.getText().toString());

        actionData.setUploadpdf(uploadpdf);

        AddBrochuresData request = new AddBrochuresData();
        request.setWebsiteId(session.getFpTag());
        request.setActionData(actionData);

        UpdateBrochuresData requestBody = new UpdateBrochuresData();
        requestBody.setQuery("{_id:'" + existingItemData.getId() + "'}");
        requestBody.setUpdateValue("{$set :" + new Gson().toJson(actionData) + "}");
        requestBody.setMulti(true);

        ManufacturingAPIInterfaces APICalls = new RestAdapter.Builder()
            .setEndpoint("https://webaction.api.boostkit.dev")
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setLog(new AndroidLog("ggg"))
            .build()
            .create(ManufacturingAPIInterfaces.class);

        APICalls.updateBrochuresData(requestBody, new Callback<String>() {
          @Override
          public void success(String s, Response response) {
            hideLoader();
            if (response.getStatus() != 200) {
              Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
              return;
            }
            Methods.showSnackBarPositive(DigitalBrochuresDetailsActivity.this, getString(R.string.successfully_updated_brochure_details));
            finish();
          }

          @Override
          public void failure(RetrofitError error) {
            hideLoader();
            if (error.getResponse().getStatus() == 200) {
              Methods.showSnackBarPositive(DigitalBrochuresDetailsActivity.this, getString(R.string.successfully_updated_brochure_details));
              finish();
            } else {
              Methods.showSnackBarNegative(DigitalBrochuresDetailsActivity.this, getString(R.string.something_went_wrong));
            }
          }
        });

      }

    } catch (Exception e) {
      hideLoader();
      e.printStackTrace();
    }

  }

  void deleteRecord(String itemId) {
    try {
      DeleteBrochuresData requestBody = new DeleteBrochuresData();
      requestBody.setQuery("{_id:'" + itemId + "'}");
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
          hideLoader();
          if (response != null && response.getStatus() == 200) {
            Log.d("deleteTeams ->", response.getBody().toString());
            Methods.showSnackBarPositive(DigitalBrochuresDetailsActivity.this, getString(R.string.successfully_deleted_));
            finish();
          } else {
            Methods.showSnackBarNegative(DigitalBrochuresDetailsActivity.this, getString(R.string.something_went_wrong));
          }
        }

        @Override
        public void failure(RetrofitError error) {
          hideLoader();
          if (error.getResponse().getStatus() == 200) {
            Methods.showSnackBarPositive(DigitalBrochuresDetailsActivity.this, getString(R.string.successfully_deleted_));
            finish();
          } else {
            Methods.showSnackBarNegative(DigitalBrochuresDetailsActivity.this, getString(R.string.something_went_wrong));
          }
        }
      });

    } catch (Exception e) {
      hideLoader();
      e.printStackTrace();
    }
  }

  public void setHeader() {
    LinearLayout rightButton, backButton;
    ImageView rightIcon;
    TextView title;

    title = findViewById(R.id.title);
    backButton = findViewById(R.id.back_button);
    rightButton = findViewById(R.id.right_icon_layout);
    rightIcon = findViewById(R.id.right_icon);
    title.setText("Brochure Details");
    rightIcon.setImageResource(R.drawable.ic_delete_white_outerline);
    rightButton.setOnClickListener(v -> {
      if (ScreenType != null && ScreenType.equals("edit")) {
        deleteRecord(itemId);
        return;
      }
      finish();
    });

    backButton.setOnClickListener(v -> onBackPressed());
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    //PDF
    if (requestCode == SELECT_PDF && resultCode == RESULT_OK) {
      Uri selectedUri_PDF = data.getData();
//      path = FileUtils.getPath(this, selectedUri_PDF);
      path = new com.appservice.utils.FileUtils(this).getPath(selectedUri_PDF);
      Log.d("onActivityResult", "Path: " + path + "\n uri" + selectedUri_PDF.getPath());
      if (!TextUtils.isEmpty(path)) {
        attachBrochureEmptyLayout.setVisibility(View.GONE);
        fileSelectedLayout.setVisibility(View.VISIBLE);
      }
      Toast.makeText(this, "File path getting error!", Toast.LENGTH_SHORT).show();
    }
  }


  private void uploadDataToServer() {
    if (ScreenType.equals("edit")) {
      updateExistingTeamsAPI();
    } else createNewTeamsAPI();
    Methods.hideKeyboard(DigitalBrochuresDetailsActivity.this);
  }

  private void uploadPdfFileToServer() {
    String fname = "Brochures" + System.currentTimeMillis();
    if (path != null) {
      try {
        documentPdfUrl = new UploadPdfFile(DigitalBrochuresDetailsActivity.this, this, path, fname).execute().get();
      } catch (ExecutionException e) {
        e.printStackTrace();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void UploadedPdfURL(String url) {
    Log.i("UploadedPdfURL", ">>>>> " + url);
    documentPdfUrl = url;
    uploadDataToServer();
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
}
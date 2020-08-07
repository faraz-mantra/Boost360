package com.nowfloats.manufacturing.digitalbrochures;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.nowfloats.util.FileUtils;
import com.nowfloats.util.Methods;
import com.nowfloats.util.Utils;
import com.thinksity.R;

import org.intellij.lang.annotations.RegExp;

import java.util.concurrent.ExecutionException;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;


public class DigitalBrochuresDetailsActivity extends AppCompatActivity implements DigitalBrochuresDetailsListener {

    String ScreenType = "", itemId = "";
    EditText nameText, descriptionText, attachedURLText;
    TextView saveButton;
    LinearLayout uploadImageButton, attachBrochureEmptyLayout, fileSelectedLayout, removePdfButton;
    String documentPdfUrl = "";
    UserSessionManager session;
    Data existingItemData;
    int SELECT_PDF = 123;
    private final int gallery_req_id = 0;
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

        removePdfButton = findViewById(R.id.remove_pdf);

        removePdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachBrochureEmptyLayout.setVisibility(View.VISIBLE);
                fileSelectedLayout.setVisibility(View.GONE);
                documentPdfUrl = "";
                attachedURLText.setText("");
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ScreenType.equals("edit")) {
                    updateExistingTeamsAPI();
                    Methods.hideKeyboard(DigitalBrochuresDetailsActivity.this);
                } else {
                    createNewTeamsAPI();
                    Methods.hideKeyboard(DigitalBrochuresDetailsActivity.this);
                }
            }
        });

        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(DigitalBrochuresDetailsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DigitalBrochuresDetailsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            gallery_req_id);
                    return;
                }
                Intent intent = new Intent();
                intent.setType("application/pdf");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select a PDF "), SELECT_PDF);
            }
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
                        if (response.getStatus() != 200) {
                            Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Methods.showSnackBarPositive(DigitalBrochuresDetailsActivity.this, "Successfully Added Team Details");
                        onBackPressed();
                    }

                    @Override
                    public void failure(RetrofitError error) {

                        Methods.showSnackBarNegative(DigitalBrochuresDetailsActivity.this, getString(R.string.something_went_wrong));
                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean validateInput() {
        if (nameText.getText().toString().isEmpty() || descriptionText.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Fields are Empty!!..", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!Patterns.WEB_URL.matcher(documentPdfUrl).matches()) {
            Toast.makeText(getApplicationContext(), "Invalid PDF Link document", Toast.LENGTH_LONG).show();
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
                        if (response.getStatus() != 200) {
                            Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Methods.showSnackBarPositive(DigitalBrochuresDetailsActivity.this, "Successfully Updated Brochure Details");
                        finish();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (error.getResponse().getStatus() == 200) {
                            Methods.showSnackBarPositive(DigitalBrochuresDetailsActivity.this, "Successfully Updated Brochure Details");
                            finish();
                        } else {
                            Methods.showSnackBarNegative(DigitalBrochuresDetailsActivity.this, getString(R.string.something_went_wrong));
                        }
                    }
                });

            }

        } catch (Exception e) {
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
                    if (response != null && response.getStatus() == 200) {
                        Log.d("deleteTeams ->", response.getBody().toString());
                        Methods.showSnackBarPositive(DigitalBrochuresDetailsActivity.this, "Successfully Deleted.");
                        finish();
                    } else {
                        Methods.showSnackBarNegative(DigitalBrochuresDetailsActivity.this, getString(R.string.something_went_wrong));
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    if (error.getResponse().getStatus() == 200) {
                        Methods.showSnackBarPositive(DigitalBrochuresDetailsActivity.this, "Successfully Deleted.");
                        finish();
                    } else {
                        Methods.showSnackBarNegative(DigitalBrochuresDetailsActivity.this, getString(R.string.something_went_wrong));
                    }
                }
            });

        } catch (Exception e) {
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
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ScreenType != null && ScreenType.equals("edit")) {
                    deleteRecord(itemId);
                    return;
                }
                finish();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //PDF
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PDF) {
                Uri selectedUri_PDF = data.getData();
//                String path = Utils.getAbsoluteFilePath(this, selectedUri_PDF);
                String path = FileUtils.getPath(this, selectedUri_PDF);
//                SelectedPDF = getPDFPath(selectedUri_PDF);
//                showLoader("Uploading Image. Please Wait...");
                String fname = "Brochures" + System.currentTimeMillis();
                try {
                    documentPdfUrl = new UploadPdfFile(DigitalBrochuresDetailsActivity.this, this, path, fname).execute().get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void UploadedPdfURL(String url) {
//        hideLoader();
        Log.i("UploadedPdfURL", ">>>>> " + url);
        documentPdfUrl = url;
        attachBrochureEmptyLayout.setVisibility(View.GONE);
        fileSelectedLayout.setVisibility(View.VISIBLE);
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
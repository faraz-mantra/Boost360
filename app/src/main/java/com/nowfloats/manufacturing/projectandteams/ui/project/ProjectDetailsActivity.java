package com.nowfloats.manufacturing.projectandteams.ui.project;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.floating_view.ImagePickerBottomSheetDialog;
import com.nowfloats.manufacturing.API.ManufacturingAPIInterfaces;
import com.nowfloats.manufacturing.API.UploadProjectImage;
import com.nowfloats.manufacturing.API.model.AddProject.ActionData;
import com.nowfloats.manufacturing.API.model.AddProject.AddProjectData;
import com.nowfloats.manufacturing.API.model.AddProject.FeaturedImage;
import com.nowfloats.manufacturing.API.model.AddProject.ProjectImage2;
import com.nowfloats.manufacturing.API.model.AddProject.ProjectImage3;
import com.nowfloats.manufacturing.API.model.DeleteProject.DeleteProjectData;
import com.nowfloats.manufacturing.API.model.GetProjects.Data;
import com.nowfloats.manufacturing.API.model.UpdateProject.UpdateProjectData;
import com.nowfloats.manufacturing.projectandteams.Interfaces.ProjectDetailsListener;
import com.nowfloats.manufacturing.projectandteams.adapter.ProjectDetailsImageAdapter;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

public class ProjectDetailsActivity extends AppCompatActivity implements ProjectDetailsListener {

    private static final int GALLERY_PHOTO = 2;
    private static final int CAMERA_PHOTO = 1;
    final Calendar myCalendar = Calendar.getInstance();
    private final int gallery_req_id = 0;
    private final int media_req_id = 1;
    RecyclerView recyclerView;
    String ScreenType = "", itemId = "";
    ProjectDetailsImageAdapter adapter;
    UserSessionManager session;
    Data existingItemData;
    EditText companyTitle, aboutCompany, clientName, clientCategory, projectClientRequirement, ourApproach, budget, dateOfCompletion;
    List<String> imageURLs = new ArrayList();
    TextView saveButton, inProgressText, completedText, withdrawText;
    RelativeLayout addImageButton;
    View dummyView1, dummyView2, dummyView3;
    Uri imageUri;
    List<String> path = new ArrayList();
    String projectResultStatus = "IN PROGRESS";
    LinearLayout inProgressButton, completedButton, withdrawButton;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_project_details);
        initView();
    }

    public void initView() {

        recyclerView = findViewById(R.id.image_recycler);
        session = new UserSessionManager(this, this);

        saveButton = findViewById(R.id.save_review);
        companyTitle = findViewById(R.id.company_title);
        aboutCompany = findViewById(R.id.company_description);
        clientName = findViewById(R.id.client_name);
        clientCategory = findViewById(R.id.client_category);
        projectClientRequirement = findViewById(R.id.project_client_requirement);
        ourApproach = findViewById(R.id.our_approach);
        budget = findViewById(R.id.budget_value);
        dateOfCompletion = findViewById(R.id.date_of_completion);

        inProgressButton = findViewById(R.id.inprogress_button);
        completedButton = findViewById(R.id.completed_button);
        withdrawButton = findViewById(R.id.withdraw_button);
        inProgressText = findViewById(R.id.inprogress_text);
        completedText = findViewById(R.id.completed_text);
        withdrawText = findViewById(R.id.withdraw_text);

        addImageButton = findViewById(R.id.add_image_button);

        dummyView1 = findViewById(R.id.dummy_view1);
        dummyView2 = findViewById(R.id.dummy_view2);
        dummyView3 = findViewById(R.id.dummy_view3);

        dummyView1.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        dummyView2.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        dummyView3.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);


        adapter = new ProjectDetailsImageAdapter(new ArrayList(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        addImageButton.setOnClickListener(v -> {
            if (imageURLs.size() < 3) {
                showDialogToGetImage();
            } else {
                Toast.makeText(ProjectDetailsActivity.this, getString(R.string.max_image_upload_is_three), Toast.LENGTH_LONG).show();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (path.size() > 0) {
                    showLoader(getString(R.string.uploading_image_please_wait));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            uploadImageToServer();
                        }
                    }, 200);
                } else {
                    uploadDataToServer();
                }
            }
        });


        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        dateOfCompletion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ProjectDetailsActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        //setheader
        setHeader();

        Bundle extra = getIntent().getExtras();
        ScreenType = extra.getString("ScreenState");
        if (ScreenType != null && ScreenType.equals("edit")) {
            displayData();
            updateProjectStatusButton(existingItemData.getProjectResult());
        } else {
            //set default styling
            updateProjectStatusButton("IN PROGRESS");
        }

        projectButtonStateController();
    }

    private void projectButtonStateController() {

        inProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProjectStatusButton("IN PROGRESS");
            }
        });

        completedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProjectStatusButton("COMPLETED");
            }
        });

        withdrawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProjectStatusButton("WITHDRAW");
            }
        });
    }

    private void updateProjectStatusButton(String value) {
        if (value.equals("COMPLETED")) {

            inProgressButton.setBackgroundResource(R.drawable.all_side_small_curve_bg);
            completedButton.setBackgroundResource(R.drawable.all_side_small_curve_bg_blue);
            withdrawButton.setBackgroundResource(R.drawable.all_side_small_curve_bg);

            inProgressText.setTextColor(getResources().getColor(R.color.customeNavyBlue));
            completedText.setTextColor(getResources().getColor(R.color.white));
            withdrawText.setTextColor(getResources().getColor(R.color.customeNavyBlue));
            projectResultStatus = "COMPLETED";

        } else if (value.equals("WITHDRAW")) {

            inProgressButton.setBackgroundResource(R.drawable.all_side_small_curve_bg);
            completedButton.setBackgroundResource(R.drawable.all_side_small_curve_bg);
            withdrawButton.setBackgroundResource(R.drawable.all_side_small_curve_bg_blue);

            inProgressText.setTextColor(getResources().getColor(R.color.customeNavyBlue));
            completedText.setTextColor(getResources().getColor(R.color.customeNavyBlue));
            withdrawText.setTextColor(getResources().getColor(R.color.white));

            projectResultStatus = "WITHDRAW";

        } else {

            inProgressButton.setBackgroundResource(R.drawable.all_side_small_curve_bg_blue);
            completedButton.setBackgroundResource(R.drawable.all_side_small_curve_bg);
            withdrawButton.setBackgroundResource(R.drawable.all_side_small_curve_bg);

            inProgressText.setTextColor(getResources().getColor(R.color.white));
            completedText.setTextColor(getResources().getColor(R.color.customeNavyBlue));
            withdrawText.setTextColor(getResources().getColor(R.color.customeNavyBlue));
            projectResultStatus = "IN PROGRESS";
        }
    }

    private void updateLabel() {
        String myFormat = "dd-MM-yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dateOfCompletion.setText(sdf.format(myCalendar.getTime()));
    }

    public void displayData() {
        existingItemData = new Gson().fromJson(getIntent().getStringExtra("data"), Data.class);

        itemId = existingItemData.getId();

        companyTitle.setText(existingItemData.getProjectTitle());
        aboutCompany.setText(existingItemData.getProjectDescription());
        clientName.setText(existingItemData.getProjectClientName());
        clientCategory.setText(existingItemData.getProjectClientCategory());
        projectClientRequirement.setText(existingItemData.getProjectClientRequirement());
        ourApproach.setText(existingItemData.getProjectWhatWeDid());
        budget.setText(existingItemData.getProjectBudget());

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date date = inputFormat.parse(existingItemData.getProjectCompletedOn());
            dateOfCompletion.setText(outputFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (existingItemData.getFeaturedImage() != null && !existingItemData.getFeaturedImage().getUrl().isEmpty()) {
            path.add(existingItemData.getFeaturedImage().getUrl());
        }

        if (existingItemData.getProjectImage2() != null && !existingItemData.getProjectImage2().getUrl().isEmpty()) {
            path.add(existingItemData.getProjectImage2().getUrl());
        }

        if (existingItemData.getProjectImage3() != null && !existingItemData.getProjectImage3().getUrl().isEmpty()) {
            path.add(existingItemData.getProjectImage3().getUrl());
        }

        updateRecyclerView();

    }

    private void updateRecyclerView() {
        adapter.updateList(path);
        adapter.notifyDataSetChanged();
    }

    private void uploadDataToServer() {
        if (ScreenType.equals("edit")) {
            updateExistingTeamsAPI();
            Methods.hideKeyboard(ProjectDetailsActivity.this);
        } else {
            createNewTeamsAPI();
            Methods.hideKeyboard(ProjectDetailsActivity.this);
        }
    }

    void createNewTeamsAPI() {
        try {
            if (validateInput()) {
                showLoader(getString(R.string.creating_record_please_wait));
                ActionData actionData = new ActionData();
                actionData.setProjectTitle(companyTitle.getText().toString());
                actionData.setProjectDescription(aboutCompany.getText().toString());
                actionData.setProjectClientName(clientName.getText().toString());
                actionData.setProjectClientCategory(clientCategory.getText().toString());
                actionData.setProjectClientRequirement(projectClientRequirement.getText().toString());
                actionData.setProjectWhatWeDid(ourApproach.getText().toString());
                actionData.setProjectBudget(budget.getText().toString());
                actionData.setProjectResult(projectResultStatus);

                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                Date date = myCalendar.getTime();
                actionData.setProjectCompletedOn(inputFormat.format(date));

                //set featured Image
                FeaturedImage featuredImage = new FeaturedImage();
                featuredImage.setDescription("");

                if (imageURLs.size() > 0) {
                    featuredImage.setUrl(imageURLs.get(0));
                } else {
                    featuredImage.setUrl("");
                }

                actionData.setFeaturedImage(featuredImage);

                //set ProjectImage2
                ProjectImage2 projectImage2 = new ProjectImage2();
                projectImage2.setDescription("");

                if (imageURLs.size() > 1) {
                    projectImage2.setUrl(imageURLs.get(1));
                } else {
                    projectImage2.setUrl("");
                }

                actionData.setProjectImage2(projectImage2);

                //set ProjectImage3
                ProjectImage3 projectImage3 = new ProjectImage3();
                projectImage3.setDescription("");

                if (imageURLs.size() > 2) {
                    projectImage3.setUrl(imageURLs.get(2));
                } else {
                    projectImage3.setUrl("");
                }

                actionData.setProjectImage3(projectImage3);

                AddProjectData request = new AddProjectData();
                request.setWebsiteId(session.getFpTag());
                request.setActionData(actionData);

                ManufacturingAPIInterfaces APICalls = new RestAdapter.Builder()
                        .setEndpoint("https://webaction.api.boostkit.dev")
                        .setLogLevel(RestAdapter.LogLevel.FULL)
                        .setLog(new AndroidLog("ggg"))
                        .build()
                        .create(ManufacturingAPIInterfaces.class);

                APICalls.addProjectData(request, new Callback<String>() {
                    @Override
                    public void success(String s, Response response) {
                        hideLoader();
                        if (response.getStatus() != 200) {
                            Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Methods.showSnackBarPositive(ProjectDetailsActivity.this, getString(R.string.successfully_added_project_details));
                        onBackPressed();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        hideLoader();
                        error.printStackTrace();
                        Methods.showSnackBarNegative(ProjectDetailsActivity.this, getString(R.string.something_went_wrong));
                    }
                });

            }
        } catch (Exception e) {
            hideLoader();
            e.printStackTrace();
        }

    }

    private boolean validateInput() {

        if (companyTitle.getText().toString().isEmpty() || aboutCompany.getText().toString().isEmpty() || clientName.getText().toString().isEmpty()
                || clientCategory.getText().toString().isEmpty() || projectClientRequirement.getText().toString().isEmpty() || ourApproach.getText().toString().isEmpty()
                || budget.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.fields_are_empty), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (dateOfCompletion.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.select_a_valid_date_of_completion), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    void updateExistingTeamsAPI() {
        try {
            if (validateInput()) {
                showLoader(getString(R.string.updating_record_please_wait));
                ActionData actionData = new ActionData();
                actionData.setProjectTitle(companyTitle.getText().toString());
                actionData.setProjectDescription(aboutCompany.getText().toString());
                actionData.setProjectClientName(clientName.getText().toString());
                actionData.setProjectClientCategory(clientCategory.getText().toString());
                actionData.setProjectClientRequirement(projectClientRequirement.getText().toString());
                actionData.setProjectWhatWeDid(ourApproach.getText().toString());
                actionData.setProjectBudget(budget.getText().toString());
                actionData.setProjectResult(projectResultStatus);

                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                Date date = myCalendar.getTime();
                actionData.setProjectCompletedOn(inputFormat.format(date));

                //set featured Image
                FeaturedImage featuredImage = new FeaturedImage();
                featuredImage.setDescription("");

                if (imageURLs.size() > 0) {
                    featuredImage.setUrl(imageURLs.get(0));
                } else {
                    featuredImage.setUrl("");
                }

                actionData.setFeaturedImage(featuredImage);

                //set ProjectImage2
                ProjectImage2 projectImage2 = new ProjectImage2();
                projectImage2.setDescription("");

                if (imageURLs.size() > 1) {
                    projectImage2.setUrl(imageURLs.get(1));
                } else {
                    projectImage2.setUrl("");
                }

                actionData.setProjectImage2(projectImage2);

                //set ProjectImage3
                ProjectImage3 projectImage3 = new ProjectImage3();
                projectImage3.setDescription("");

                if (imageURLs.size() > 2) {
                    projectImage3.setUrl(imageURLs.get(2));
                } else {
                    projectImage3.setUrl("");
                }

                actionData.setProjectImage3(projectImage3);

                UpdateProjectData requestBody = new UpdateProjectData();
                requestBody.setQuery("{_id:'" + existingItemData.getId() + "'}");
                requestBody.setUpdateValue("{$set :" + new Gson().toJson(actionData) + "}");
                requestBody.setMulti(true);

                ManufacturingAPIInterfaces APICalls = new RestAdapter.Builder()
                        .setEndpoint("https://webaction.api.boostkit.dev")
                        .setLogLevel(RestAdapter.LogLevel.FULL)
                        .setLog(new AndroidLog("ggg"))
                        .build()
                        .create(ManufacturingAPIInterfaces.class);

                APICalls.updateProjectData(requestBody, new Callback<String>() {
                    @Override
                    public void success(String s, Response response) {
                        hideLoader();
                        if (response.getStatus() != 200) {
                            Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Methods.showSnackBarPositive(ProjectDetailsActivity.this, getString(R.string.successfully_added_project_details));
                        finish();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        hideLoader();
                        if (error.getResponse().getStatus() == 200) {
                            Methods.showSnackBarPositive(ProjectDetailsActivity.this, getString(R.string.successfully_added_project_details));
                            finish();
                        } else {
                            Methods.showSnackBarNegative(ProjectDetailsActivity.this, getString(R.string.something_went_wrong));
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
            DeleteProjectData requestBody = new DeleteProjectData();
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

            APICalls.deleteProjectData(requestBody, new Callback<String>() {
                @Override
                public void success(String data, Response response) {
                    hideLoader();
                    if (response != null && response.getStatus() == 200) {
                        Log.d("deleteTeams ->", response.getBody().toString());
                        Methods.showSnackBarPositive(ProjectDetailsActivity.this, getString(R.string.successfully_deleted_));
                        finish();
                    } else {
                        Methods.showSnackBarNegative(ProjectDetailsActivity.this, getString(R.string.something_went_wrong));
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    hideLoader();
                    if (error.getResponse().getStatus() == 200) {
                        Methods.showSnackBarPositive(ProjectDetailsActivity.this, getString(R.string.successfully_deleted_));
                        finish();
                    } else {
                        Methods.showSnackBarNegative(ProjectDetailsActivity.this, getString(R.string.something_went_wrong));
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
        title.setText("Project Details");
        rightIcon.setImageResource(R.drawable.ic_delete_white_outerline);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ScreenType != null && ScreenType.equals("edit")) {
                    showLoader(getString(R.string.deleting_record_please_wait));
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
    public void removeURLfromList(int pos) {
//        imageURLs.remove(pos);
        path.remove(pos);
        updateRecyclerView();
    }

    public void showDialogToGetImage() {
        final ImagePickerBottomSheetDialog imagePickerBottomSheetDialog = new ImagePickerBottomSheetDialog(this::onClickImagePicker);
        imagePickerBottomSheetDialog.show(getSupportFragmentManager(), ImagePickerBottomSheetDialog.class.getName());
    }

    private void onClickImagePicker(ImagePickerBottomSheetDialog.IMAGE_CLICK_TYPE image_click_type) {
        if (image_click_type.name().equals(ImagePickerBottomSheetDialog.IMAGE_CLICK_TYPE.CAMERA.name())) {
            cameraIntent();
        } else if (image_click_type.name().equals(ImagePickerBottomSheetDialog.IMAGE_CLICK_TYPE.GALLERY.name())) {
            galleryIntent();
        }
    }

//    public void showDialogToGetImage() {
//        final MaterialDialog dialog = new MaterialDialog.Builder(ProjectDetailsActivity.this)
//                .customView(R.layout.featuredimage_popup, true)
//                .show();
//
//        PorterDuffColorFilter whiteLabelFilter = new PorterDuffColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
//        View view = dialog.getCustomView();
//        TextView title = (TextView) view.findViewById(R.id.textview_heading);
//        title.setText("Upload Image");
//        LinearLayout takeCamera = (LinearLayout) view.findViewById(R.id.cameraimage);
//        LinearLayout takeGallery = (LinearLayout) view.findViewById(R.id.galleryimage);
//        ImageView cameraImg = (ImageView) view.findViewById(R.id.pop_up_camera_imag);
//        ImageView galleryImg = (ImageView) view.findViewById(R.id.pop_up_gallery_img);
//        cameraImg.setColorFilter(whiteLabelFilter);
//        galleryImg.setColorFilter(whiteLabelFilter);
//
//        takeCamera.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cameraIntent();
//                dialog.dismiss();
//            }
//        });
//
//        takeGallery.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                galleryIntent();
//                dialog.dismiss();
//
//            }
//        });
//    }

    public void cameraIntent() {
        try {
            // use standard intent to capture an image
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        media_req_id);
                return;
            }
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            Constants.PACKAGE_NAME + ".provider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, CAMERA_PHOTO);
                }
            }

        } catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = getResources().getString(R.string.device_does_not_support_capturing_image);
            Methods.showSnackBarNegative(ProjectDetailsActivity.this, errorMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        path.add(image.getAbsolutePath());
        return image;
    }

    public void galleryIntent() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        gallery_req_id);
                return;
            }
            Intent i = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(i, GALLERY_PHOTO);
        } catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = getResources().getString(R.string.device_does_not_support_capturing_image);
            Methods.showSnackBarNegative(ProjectDetailsActivity.this, errorMessage);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK && (CAMERA_PHOTO == requestCode)) {
                updateRecyclerView();
            } else if (resultCode == RESULT_OK && (GALLERY_PHOTO == requestCode)) {
                {
                    Uri picUri = data.getData();
                    if (picUri != null) {
                        path.add(Methods.getPath(this, picUri));
                        updateRecyclerView();
                    }
                }
            }
        } catch (Exception e) {
            Log.e("onActivityResult ->", "Failed ->" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void uploadImageURL(String url) {
        imageURLs.add(url);
        if (path.size() != imageURLs.size()) {
            uploadImageToServer();
        } else {
            uploadDataToServer();
        }
    }

    public void uploadImageToServer() {
        try {
            if (validateInput()) {
                String fname = "Project" + System.currentTimeMillis();
                if (path.size() > 0) {
                    if (path.get(imageURLs.size()).startsWith("https")) {
                        imageURLs.add(path.get(imageURLs.size()));
                        if (path.size() != imageURLs.size()) {
                            uploadImageToServer();
                        } else {
                            uploadDataToServer();
                        }
                    } else {
                        new UploadProjectImage(ProjectDetailsActivity.this, this, path.get(imageURLs.size()), fname).execute().get();
                    }
                } else {
                    Methods.showSnackBarNegative(ProjectDetailsActivity.this, getResources().getString(R.string.select_image_upload));
                }
            }
        } catch (Exception e) {
            Log.e("uploadImageToServer ->", "Failed ->" + e.getMessage());
            e.printStackTrace();
        } catch (OutOfMemoryError E) {
            E.printStackTrace();
            System.gc();
        }
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
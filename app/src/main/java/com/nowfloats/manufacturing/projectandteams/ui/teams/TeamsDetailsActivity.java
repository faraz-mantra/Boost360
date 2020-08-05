package com.nowfloats.manufacturing.projectandteams.ui.teams;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.manufacturing.API.ManufacturingAPIInterfaces;
import com.nowfloats.manufacturing.API.UploadTeamsImage;
import com.nowfloats.manufacturing.API.model.AddTeams.ActionData;
import com.nowfloats.manufacturing.API.model.AddTeams.AddTeamsData;
import com.nowfloats.manufacturing.API.model.AddTeams.FbURL;
import com.nowfloats.manufacturing.API.model.AddTeams.ProfileImage;
import com.nowfloats.manufacturing.API.model.AddTeams.SkypeHandle;
import com.nowfloats.manufacturing.API.model.AddTeams.TwitterURL;
import com.nowfloats.manufacturing.API.model.DeleteTeams.DeleteTeamsData;
import com.nowfloats.manufacturing.API.model.GetTeams.Data;
import com.nowfloats.manufacturing.API.model.UpdateTeams.UpdateTeamsData;
import com.nowfloats.manufacturing.projectandteams.Interfaces.TeamsDetailsListener;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.net.URL;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

public class TeamsDetailsActivity extends AppCompatActivity implements TeamsDetailsListener {

    UserSessionManager session;
    Data existingItemData;
    String uploadedImageURL = "";
    String ScreenType = "", itemId = "";
    TextView saveButton;
    EditText nameValue, designationValue, fbValue, twitterValue, skypeValue;
    ImageView placeImage;
    ImageButton removePlaceImage;
    CardView placeImageLayout;
    private final int gallery_req_id = 0;
    private final int media_req_id = 1;
    private static final int GALLERY_PHOTO = 2;
    private static final int CAMERA_PHOTO = 1;
    Uri imageUri;
    String path = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teams_details);

        initView();
    }

    public void initView() {

        session = new UserSessionManager(this, this);
        saveButton = findViewById(R.id.save_review);
        nameValue = findViewById(R.id.name_value);
        designationValue = findViewById(R.id.designation_value);
        fbValue = findViewById(R.id.facebook_url_value);
        twitterValue = findViewById(R.id.twitter_url_value);
        skypeValue = findViewById(R.id.skype_url_value);
        placeImage = findViewById(R.id.iv_primary_image);
        removePlaceImage = findViewById(R.id.ib_remove_product_image);
        placeImageLayout = findViewById(R.id.card_primary_image);

        placeImageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogToGetImage();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ScreenType.equals("edit")) {
                    updateExistingTeamsAPI();
                    Methods.hideKeyboard(TeamsDetailsActivity.this);
                } else {
                    createNewTeamsAPI();
                    Methods.hideKeyboard(TeamsDetailsActivity.this);
                }
            }
        });

        removePlaceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeImage.setImageDrawable(null);
                removePlaceImage.setVisibility(View.GONE);
                uploadedImageURL = "";
            }
        });

        //setheader
        setHeader();

        Bundle extra = getIntent().getExtras();
        ScreenType = extra.getString("ScreenState");
        if (ScreenType != null && ScreenType.equals("edit")) {
            displayData();
        }
    }

    public void setHeader(){
        LinearLayout rightButton,backButton;
        ImageView rightIcon;
        TextView title;

        title = findViewById(R.id.title);
        backButton = findViewById(R.id.back_button);
        rightButton = findViewById(R.id.right_icon_layout);
        rightIcon = findViewById(R.id.right_icon);
        title.setText("Team Details");
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


    public void displayData() {
        existingItemData = new Gson().fromJson(getIntent().getStringExtra("data"), Data.class);

        itemId = existingItemData.getId();
        uploadedImageURL = existingItemData.getProfileImage().getUrl();
        nameValue.setText(existingItemData.getName());
        designationValue.setText(existingItemData.getDesignation());
        fbValue.setText(existingItemData.getFbURL().getUrl());
        twitterValue.setText(existingItemData.getTwitterURL().getUrl());
        skypeValue.setText(existingItemData.getSkypeHandle().getUrl());

        updatePlaceProfileImage();
    }

    public void updatePlaceProfileImage() {
        if(!uploadedImageURL.isEmpty()) {
            Glide.with(TeamsDetailsActivity.this).load(uploadedImageURL).into(placeImage);
            removePlaceImage.setVisibility(View.VISIBLE);
        }
    }

    void createNewTeamsAPI() {
        try {

            if (validateInput()) {

                ActionData actionData = new ActionData();
                actionData.setName(nameValue.getText().toString());
                actionData.setDesignation(designationValue.getText().toString());

                //set fbURL
                FbURL fbURL = new FbURL();
                fbURL.setUrl(fbValue.getText().toString());
                fbURL.setDescription("");

                actionData.setFbURL(fbURL);

                //set twitterURL
                TwitterURL twitterURL = new TwitterURL();
                twitterURL.setUrl(twitterValue.getText().toString());
                twitterURL.setDescription("");

                actionData.setTwitterURL(twitterURL);

                //set SkypeURL
                SkypeHandle skypeHandle = new SkypeHandle();
                skypeHandle.setUrl(skypeValue.getText().toString());
                skypeHandle.setDescription("");

                actionData.setSkypeHandle(skypeHandle);

                ProfileImage placeImage = new ProfileImage();
                placeImage.setUrl(uploadedImageURL);
                placeImage.setDescription("");

                actionData.setProfileImage(placeImage);


                AddTeamsData request = new AddTeamsData();
                request.setWebsiteId(session.getFpTag());
                request.setActionData(actionData);

                ManufacturingAPIInterfaces APICalls = new RestAdapter.Builder()
                        .setEndpoint("https://webaction.api.boostkit.dev")
                        .setLogLevel(RestAdapter.LogLevel.FULL)
                        .setLog(new AndroidLog("ggg"))
                        .build()
                        .create(ManufacturingAPIInterfaces.class);

                APICalls.addTeamsData(request, new Callback<String>() {
                    @Override
                    public void success(String s, Response response) {
                        if (response.getStatus() != 200) {
                            Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Methods.showSnackBarPositive(TeamsDetailsActivity.this, "Successfully Added Team Details");
                        onBackPressed();
                    }

                    @Override
                    public void failure(RetrofitError error) {

                        Methods.showSnackBarNegative(TeamsDetailsActivity.this, getString(R.string.something_went_wrong));
                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    void updateExistingTeamsAPI() {
        try {

            if (validateInput()) {

                ActionData actionData = new ActionData();
                actionData.setName(nameValue.getText().toString());
                actionData.setDesignation(designationValue.getText().toString());

                //set fbURL
                FbURL fbURL = new FbURL();
                fbURL.setUrl(fbValue.getText().toString());
                fbURL.setDescription("");

                actionData.setFbURL(fbURL);

                //set twitterURL
                TwitterURL twitterURL = new TwitterURL();
                twitterURL.setUrl(twitterValue.getText().toString());
                twitterURL.setDescription("");

                actionData.setTwitterURL(twitterURL);

                //set SkypeURL
                SkypeHandle skypeHandle = new SkypeHandle();
                skypeHandle.setUrl(skypeValue.getText().toString());
                skypeHandle.setDescription("");

                actionData.setSkypeHandle(skypeHandle);

                ProfileImage placeImage = new ProfileImage();
                placeImage.setUrl(uploadedImageURL);
                placeImage.setDescription("");

                actionData.setProfileImage(placeImage);

                UpdateTeamsData requestBody = new UpdateTeamsData();
                requestBody.setQuery("{_id:'" + existingItemData.getId() + "'}");
                requestBody.setUpdateValue("{$set :" + new Gson().toJson(actionData) + "}");
                requestBody.setMulti(true);

                ManufacturingAPIInterfaces APICalls = new RestAdapter.Builder()
                        .setEndpoint("https://webaction.api.boostkit.dev")
                        .setLogLevel(RestAdapter.LogLevel.FULL)
                        .setLog(new AndroidLog("ggg"))
                        .build()
                        .create(ManufacturingAPIInterfaces.class);

                APICalls.updateTeamsData(requestBody, new Callback<String>() {
                    @Override
                    public void success(String s, Response response) {
                        if (response.getStatus() != 200) {
                            Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Methods.showSnackBarPositive(TeamsDetailsActivity.this, "Successfully Updated Team Details");
                        finish();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (error.getResponse().getStatus() == 200) {
                            Methods.showSnackBarPositive(TeamsDetailsActivity.this, "Successfully Updated Team Details");
                            finish();
                        } else {
                            Methods.showSnackBarNegative(TeamsDetailsActivity.this, getString(R.string.something_went_wrong));
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
            DeleteTeamsData requestBody = new DeleteTeamsData();
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

            APICalls.deleteTeamsData(requestBody, new Callback<String>() {
                @Override
                public void success(String data, Response response) {
                    if (response != null && response.getStatus() == 200) {
                        Log.d("deleteTeams ->", response.getBody().toString());
                        Methods.showSnackBarPositive(TeamsDetailsActivity.this, "Successfully Deleted.");
                        finish();
                    } else {
                        Methods.showSnackBarNegative(TeamsDetailsActivity.this, getString(R.string.something_went_wrong));
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    if (error.getResponse().getStatus() == 200) {
                        Methods.showSnackBarPositive(TeamsDetailsActivity.this, "Successfully Deleted.");
                        finish();
                    } else {
                        Methods.showSnackBarNegative(TeamsDetailsActivity.this, getString(R.string.something_went_wrong));
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showDialogToGetImage() {
        final MaterialDialog dialog = new MaterialDialog.Builder(TeamsDetailsActivity.this)
                .customView(R.layout.featuredimage_popup, true)
                .show();

        PorterDuffColorFilter whiteLabelFilter = new PorterDuffColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
        View view = dialog.getCustomView();
        TextView title = (TextView) view.findViewById(R.id.textview_heading);
        title.setText("Upload Image");
        LinearLayout takeCamera = (LinearLayout) view.findViewById(R.id.cameraimage);
        LinearLayout takeGallery = (LinearLayout) view.findViewById(R.id.galleryimage);
        ImageView cameraImg = (ImageView) view.findViewById(R.id.pop_up_camera_imag);
        ImageView galleryImg = (ImageView) view.findViewById(R.id.pop_up_gallery_img);
        cameraImg.setColorFilter(whiteLabelFilter);
        galleryImg.setColorFilter(whiteLabelFilter);

        takeCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraIntent();
                dialog.dismiss();
            }
        });

        takeGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryIntent();
                dialog.dismiss();

            }
        });
    }

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
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
            imageUri = getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent captureIntent = new Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            // we will handle the returned data in onActivityResult
            startActivityForResult(captureIntent, CAMERA_PHOTO);

        } catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = getResources().getString(R.string.device_does_not_support_capturing_image);
            Methods.showSnackBarNegative(TeamsDetailsActivity.this, errorMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            Methods.showSnackBarNegative(TeamsDetailsActivity.this, errorMessage);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            path = null;
            if (resultCode == RESULT_OK && (CAMERA_PHOTO == requestCode)) {
                try {
                    Log.e("ImageURI ->",imageUri.getPath());
                    path = Methods.getRealPathFromURI(this, imageUri);
                    String fname = "PlaceNearBy" + System.currentTimeMillis();
//                    path = Util.saveBitmap(path, TeamsDetailsActivity.this, fname);
                    if (!Util.isNullOrEmpty(path)) {
                        if (!TextUtils.isEmpty(path)) {
//                            uploadFileToServer(path, fname);
                            new UploadTeamsImage(TeamsDetailsActivity.this, this, path, fname).execute().get();
                        }
                    } else
                        Methods.showSnackBarNegative(TeamsDetailsActivity.this, getResources().getString(R.string.select_image_upload));
                } catch (Exception e) {
                    e.printStackTrace();
                    //  Util.toast("Uh oh. Something went wrong. Please try again", this);
                } catch (OutOfMemoryError E) {
                    //Log.d("ANDRO_ASYNC",String.format("catch Out Of Memory error"));
                    E.printStackTrace();
                    System.gc();
                    // Util.toast("Uh oh. Something went wrong. Please try again", this);
                }
            } else if (resultCode == RESULT_OK && (GALLERY_PHOTO == requestCode)) {
                {
                    Uri picUri = data.getData();
                    if (picUri != null) {
                        path = Methods.getPath(this, picUri);
                        String fname = "PlaceNearBy" + System.currentTimeMillis();
//                        path = Util.saveBitmap(path, TeamsDetailsActivity.this, fname);
                        if (!Util.isNullOrEmpty(path)) {
                            if (!TextUtils.isEmpty(path)) {
//                                uploadFileToServer(path, fname);
                                uploadedImageURL = new UploadTeamsImage(TeamsDetailsActivity.this, this, path, fname).execute().get();
                            }
                        } else
                            Methods.showSnackBarNegative(TeamsDetailsActivity.this, getResources().getString(R.string.select_image_upload));
                    }
                }
            }
        } catch (Exception e) {
            Log.e("onActivityResult ->", "Failed ->"+e.getMessage());
            e.printStackTrace();
        }
    }


    private boolean validateInput() {
        if(nameValue.getText().toString().isEmpty() || designationValue.getText().toString().isEmpty() || fbValue.getText().toString().isEmpty()
        || twitterValue.getText().toString().isEmpty() || skypeValue.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Fields are Empty!!..", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }


    @Override
    public void uploadImageURL(String url) {
        uploadedImageURL = url;
        updatePlaceProfileImage();
    }
}
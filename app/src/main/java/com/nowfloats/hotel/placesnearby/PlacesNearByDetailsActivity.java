package com.nowfloats.hotel.placesnearby;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.os.Environment;
import android.os.Handler;
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
import com.nowfloats.AccrossVerticals.Testimonials.TestimonialsFeedbackActivity;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.hotel.API.HotelAPIInterfaces;
import com.nowfloats.hotel.API.UploadPlaceNearByImage;
import com.nowfloats.hotel.API.model.AddPlacesAround.ActionData;
import com.nowfloats.hotel.API.model.AddPlacesAround.AddPlacesAroundRequest;
import com.nowfloats.hotel.API.model.AddPlacesAround.PlaceImage;
import com.nowfloats.hotel.API.model.DeletePlacesAround.DeletePlacesAroundRequest;
import com.nowfloats.hotel.API.model.GetPlacesAround.Data;
import com.nowfloats.hotel.API.model.UpdatePlacesAround.UpdatePlacesAroundRequest;
import com.nowfloats.hotel.Interfaces.PlaceNearByDetailsListener;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.nowfloats.util.Utils;
import com.thinksity.R;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

public class PlacesNearByDetailsActivity extends AppCompatActivity implements PlaceNearByDetailsListener {

    TextView saveReview;
    EditText placeName, placeAddress, placeDescription, placeDistance;
    CardView placeImageLayout;
    ImageView placeImage;
    ImageButton removePlaceImage;
    private final int gallery_req_id = 0;
    private final int media_req_id = 1;
    private static final int GALLERY_PHOTO = 2;
    private static final int CAMERA_PHOTO = 1;
    private static final int ACTION_REQUEST_IMAGE_EDIT = 3;
    Uri imageUri;
    String path = null;
    String uploadedImageURL = "";
    String ScreenType = "", itemId = "";
    Data existingItemData = null;
    UserSessionManager session;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_near_by_details);
        initView();
    }


    public void initView() {

        saveReview = findViewById(R.id.save_review);
        placeName = findViewById(R.id.place_name);
        placeAddress = findViewById(R.id.place_address);
        placeDescription = findViewById(R.id.place_description);
        placeDistance = findViewById(R.id.place_distance);
        placeImageLayout = findViewById(R.id.card_primary_image);
        placeImage = findViewById(R.id.iv_primary_image);
        removePlaceImage = findViewById(R.id.ib_remove_product_image);
        session = new UserSessionManager(this, this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        placeImageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogToGetImage();
            }
        });

        saveReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (path != null) {
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

        removePlaceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeImage.setImageDrawable(null);
                removePlaceImage.setVisibility(View.GONE);
                uploadedImageURL = "";
                path = null;
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

    public void displayData() {
        existingItemData = new Gson().fromJson(getIntent().getStringExtra("data"), Data.class);

        itemId = existingItemData.getId();
        uploadedImageURL = existingItemData.getPlaceImage().getUrl();

        if (!uploadedImageURL.isEmpty()) {
            Glide.with(PlacesNearByDetailsActivity.this).load(uploadedImageURL).into(placeImage);
            removePlaceImage.setVisibility(View.VISIBLE);
        }

        placeName.setText(existingItemData.getPlaceName());
        placeAddress.setText(existingItemData.getPlaceAddress());
        placeDescription.setText(existingItemData.getPlaceImage().getDescription());
        placeDistance.setText(existingItemData.getDistance());

        updatePlaceProfileImage();
    }

    public void showDialogToGetImage() {
        final MaterialDialog dialog = new MaterialDialog.Builder(PlacesNearByDetailsActivity.this)
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

    public void setHeader() {
        LinearLayout rightButton, backButton;
        ImageView rightIcon;
        TextView title;

        title = findViewById(R.id.title);
        backButton = findViewById(R.id.back_button);
        rightButton = findViewById(R.id.right_icon_layout);
        rightIcon = findViewById(R.id.right_icon);
        title.setText("Place Details");
        rightIcon.setImageResource(R.drawable.ic_delete_white_outerline);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ScreenType != null && ScreenType.equals("edit")) {
                    showLoader(getString(R.string.deleting_record));
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

    void createNewPlaceNearByAPI() {
        try {

            if (validateInput()) {

                ActionData actionData = new ActionData();
                actionData.setPlaceName(placeName.getText().toString());
                actionData.setPlaceAddress(placeAddress.getText().toString());
                actionData.setDistance(placeDistance.getText().toString());


                PlaceImage placeImage = new PlaceImage();
                placeImage.setUrl(uploadedImageURL);
                placeImage.setDescription(placeDescription.getText().toString());

                actionData.setPlaceImage(placeImage);


                AddPlacesAroundRequest request = new AddPlacesAroundRequest();
                request.setWebsiteId(session.getFpTag());
                request.setActionData(actionData);

                HotelAPIInterfaces APICalls = new RestAdapter.Builder()
                        .setEndpoint("https://webaction.api.boostkit.dev")
                        .setLogLevel(RestAdapter.LogLevel.FULL)
                        .setLog(new AndroidLog("ggg"))
                        .build()
                        .create(HotelAPIInterfaces.class);

                APICalls.addPlacesAround(request, new Callback<String>() {
                    @Override
                    public void success(String s, Response response) {
                        hideLoader();
                        if (response.getStatus() != 200) {
                            Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Methods.showSnackBarPositive(PlacesNearByDetailsActivity.this, getString(R.string.successfully_added_placedetails));
                        onBackPressed();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        hideLoader();
                        Methods.showSnackBarNegative(PlacesNearByDetailsActivity.this, getString(R.string.something_went_wrong));
                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void updateExistingPlaceNearByAPI() {
        try {

            if (validateInput()) {
                ActionData actionData = new ActionData();
                actionData.setPlaceName(placeName.getText().toString());
                actionData.setPlaceAddress(placeAddress.getText().toString());
                actionData.setDistance(placeDistance.getText().toString());


                PlaceImage placeImage = new PlaceImage();
                placeImage.setUrl(uploadedImageURL);
                placeImage.setDescription(placeDescription.getText().toString());

                actionData.setPlaceImage(placeImage);

                UpdatePlacesAroundRequest requestBody = new UpdatePlacesAroundRequest();
                requestBody.setQuery("{_id:'" + existingItemData.getId() + "'}");
                requestBody.setUpdateValue("{$set :" + new Gson().toJson(actionData) + "}");

                HotelAPIInterfaces APICalls = new RestAdapter.Builder()
                        .setEndpoint("https://webaction.api.boostkit.dev")
                        .setLogLevel(RestAdapter.LogLevel.FULL)
                        .setLog(new AndroidLog("ggg"))
                        .build()
                        .create(HotelAPIInterfaces.class);

                APICalls.updatePlacesAround(requestBody, new Callback<String>() {
                    @Override
                    public void success(String s, Response response) {
                        hideLoader();
                        if (response.getStatus() != 200) {
                            Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Methods.showSnackBarPositive(PlacesNearByDetailsActivity.this, getString(R.string.successfully_updated_place_details));
                        finish();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        hideLoader();
                        if (error.getResponse().getStatus() == 200) {
                            Methods.showSnackBarPositive(PlacesNearByDetailsActivity.this, getString(R.string.successfully_updated_place_details));
                            finish();
                        } else {
                            Methods.showSnackBarNegative(PlacesNearByDetailsActivity.this, getString(R.string.something_went_wrong));
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validateInput() {
        if (placeName.getText().toString().isEmpty() || placeDescription.getText().toString().isEmpty()
                || placeDistance.getText().toString().isEmpty() || placeAddress.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(),getString( R.string.fields_are_empty), Toast.LENGTH_SHORT).show();
            hideLoader();
            return false;
        }
        return true;
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
            Methods.showSnackBarNegative(PlacesNearByDetailsActivity.this, errorMessage);
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
        path = image.getAbsolutePath();
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
            Methods.showSnackBarNegative(PlacesNearByDetailsActivity.this, errorMessage);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK && (CAMERA_PHOTO == requestCode)) {
                updatePlaceProfileImage();
            } else if (resultCode == RESULT_OK && (GALLERY_PHOTO == requestCode)) {
                {
                    Uri picUri = data.getData();
                    if (picUri != null) {
                        path = Methods.getPath(this, picUri);
                        updatePlaceProfileImage();
                    } else {
                        Toast.makeText(this, getString(R.string.something_went_wrong_try_later), Toast.LENGTH_LONG).show();
                    }
                }
            }
        } catch (Exception e) {
            Log.e("onActivityResult ->", getString(R.string.failed_) + e.getMessage());
            e.printStackTrace();
        }
    }

    public void uploadImageToServer() {
        try {
            if (validateInput()) {
                String fname = "PlaceNearBy" + System.currentTimeMillis();
                if (!Util.isNullOrEmpty(path)) {
                    if (!TextUtils.isEmpty(path)) {
                        new UploadPlaceNearByImage(PlacesNearByDetailsActivity.this, this, path, fname).execute().get();
                    }
                } else {
                    Methods.showSnackBarNegative(PlacesNearByDetailsActivity.this, getResources().getString(R.string.select_image_upload));
                }
            }
        } catch (Exception e) {
            Log.e("uploadImageToServer ->", getString(R.string.failed_) + e.getMessage());
            e.printStackTrace();
        } catch (OutOfMemoryError E) {
            E.printStackTrace();
            System.gc();
        }
    }

    public void updatePlaceProfileImage() {
        if (path != null) {
            Glide.with(PlacesNearByDetailsActivity.this).load(path).into(placeImage);
            removePlaceImage.setVisibility(View.VISIBLE);
        }
    }

    void deleteRecord(String itemId) {
        try {
            DeletePlacesAroundRequest requestBody = new DeletePlacesAroundRequest();
            requestBody.setQuery("{_id:'" + itemId + "'}");
            requestBody.setUpdateValue("{$set : {IsArchived: true }}");
            requestBody.setMulti(true);

            HotelAPIInterfaces APICalls = new RestAdapter.Builder()
                    .setEndpoint("https://webaction.api.boostkit.dev")
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setLog(new AndroidLog("ggg"))
                    .setConverter(new GsonConverter(new GsonBuilder().setLenient().create()))
                    .build()
                    .create(HotelAPIInterfaces.class);

            APICalls.deletePlacesAround(requestBody, new Callback<String>() {
                @Override
                public void success(String data, Response response) {
                    hideLoader();
                    if (response != null && response.getStatus() == 200) {
                        Log.d("deletePlacesAround ->", response.getBody().toString());
                        Methods.showSnackBarPositive(PlacesNearByDetailsActivity.this, getString(R.string.successfully_deleted_));
                        finish();
                    } else {
                        Methods.showSnackBarNegative(PlacesNearByDetailsActivity.this, getString(R.string.something_went_wrong));
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    hideLoader();
                    if (error.getResponse().getStatus() == 200) {
                        Methods.showSnackBarPositive(PlacesNearByDetailsActivity.this,  getString(R.string.successfully_deleted_));
                        finish();
                    } else {
                        Methods.showSnackBarNegative(PlacesNearByDetailsActivity.this, getString(R.string.something_went_wrong));
                    }
                }
            });

        } catch (Exception e) {
            hideLoader();
            e.printStackTrace();
        }
    }

    @Override
    public void uploadImageURL(String url) {
        uploadedImageURL = url;
        uploadDataToServer();
    }

    private void uploadDataToServer() {
        if (ScreenType.equals("edit")) {
            showLoader(getString(R.string.updating_record_please_wait));
            updateExistingPlaceNearByAPI();
        } else {
            showLoader(getString(R.string.creating_record_please_wait));
            createNewPlaceNearByAPI();
            Methods.hideKeyboard(PlacesNearByDetailsActivity.this);
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


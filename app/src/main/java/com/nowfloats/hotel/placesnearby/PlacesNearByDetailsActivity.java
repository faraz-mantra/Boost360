package com.nowfloats.hotel.placesnearby;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.Manifest;
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

import android.os.Environment;
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
import com.nowfloats.util.Methods;
import com.nowfloats.util.Utils;
import com.thinksity.R;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
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

        placeImageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogToGetImage();
            }
        });

        saveReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ScreenType.equals("edit")) {
                    updateExistingPlaceNearByAPI();
                } else {
                    createNewPlaceNearByAPI();
                    Methods.hideKeyboard(PlacesNearByDetailsActivity.this);
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

    public void displayData() {
        existingItemData = new Gson().fromJson(getIntent().getStringExtra("data"), Data.class);

        itemId = existingItemData.getId();
        uploadedImageURL = existingItemData.getPlaceImage().getUrl();
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
                        if (response.getStatus() != 200) {
                            Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Methods.showSnackBarPositive(PlacesNearByDetailsActivity.this, "Successfully Added Place Details");
                        onBackPressed();
                    }

                    @Override
                    public void failure(RetrofitError error) {

                        Methods.showSnackBarNegative(PlacesNearByDetailsActivity.this, getString(R.string.something_went_wrong));
                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void updateExistingPlaceNearByAPI() {

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
                if (response.getStatus() != 200) {
                    Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    return;
                }
                Methods.showSnackBarPositive(PlacesNearByDetailsActivity.this, "Successfully Updated Place Details");
                finish();
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getResponse().getStatus() == 200) {
                    Methods.showSnackBarPositive(PlacesNearByDetailsActivity.this, "Successfully Updated Place Details");
                    finish();
                } else {
                    Methods.showSnackBarNegative(PlacesNearByDetailsActivity.this, getString(R.string.something_went_wrong));
                }
            }
        });

    }

    private boolean validateInput() {
        if (placeName.getText().toString().isEmpty() || placeDescription.getText().toString().isEmpty()
                || placeDistance.getText().toString().isEmpty() || placeAddress.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Fields are Empty...", Toast.LENGTH_SHORT).show();
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
            Methods.showSnackBarNegative(PlacesNearByDetailsActivity.this, errorMessage);
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
            Methods.showSnackBarNegative(PlacesNearByDetailsActivity.this, errorMessage);
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
//                    path = Util.saveBitmap(path, PlacesNearByDetailsActivity.this, fname);
                    if (!Util.isNullOrEmpty(path)) {
                        if (!TextUtils.isEmpty(path)) {
//                            uploadFileToServer(path, fname);
                            new UploadPlaceNearByImage(PlacesNearByDetailsActivity.this, this, path, fname).execute().get();
                        }
                    } else
                        Methods.showSnackBarNegative(PlacesNearByDetailsActivity.this, getResources().getString(R.string.select_image_upload));
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
//                        path = Util.saveBitmap(path, PlacesNearByDetailsActivity.this, fname);
                        if (!Util.isNullOrEmpty(path)) {
                            if (!TextUtils.isEmpty(path)) {
//                                uploadFileToServer(path, fname);
                                uploadedImageURL = new UploadPlaceNearByImage(PlacesNearByDetailsActivity.this, this, path, fname).execute().get();
                            }
                        } else
                            Methods.showSnackBarNegative(PlacesNearByDetailsActivity.this, getResources().getString(R.string.select_image_upload));
                    }
                }
            }
        } catch (Exception e) {
            Log.e("onActivityResult ->", "Failed ->"+e.getMessage());
            e.printStackTrace();
        }
    }

    public void updatePlaceProfileImage() {
        if(!uploadedImageURL.isEmpty()) {
            Glide.with(PlacesNearByDetailsActivity.this).load(uploadedImageURL).into(placeImage);
            removePlaceImage.setVisibility(View.VISIBLE);
        }
    }

    public void uploadFileToServer(String path, String fileName) {
        File file = new File(path);
        try {
            OkHttpClient client = new OkHttpClient();
            InputStream in = new FileInputStream(file);
            byte[] buf;
            buf = new byte[in.available()];
            while (in.read(buf) != -1) ;
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", fileName, RequestBody.create(MediaType.parse("image/*"), buf))
                    .build();

            //https://webaction.api.boostkit.dev/api/v1/placesaround/upload-file?assetFileName=screenshot-assuredpurchase.withfloats.com-2020.07.17-14_38_42.png
            Request request = new Request.Builder()
                    .url("https://webaction.api.boostkit.dev/api/v1/placesaround/upload-file?assetFileName=screenshot-assuredpurchase.withfloats.com-" + fileName + ".jpg")
                    .post(requestBody)
                    .addHeader("Authorization", "59c8add5dd304111404e7f04")
                    .build();

            okhttp3.Response response = client.newCall(request).execute();
            if (response != null && response.code() == 200) {
                uploadedImageURL = Objects.requireNonNull(response.body()).string();
                updatePlaceProfileImage();
            } else {
                Methods.showSnackBarNegative(PlacesNearByDetailsActivity.this, "Uploading Image Failed");
            }

            in.close();
            buf = null;

        } catch (Exception e) {
            e.printStackTrace();
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
                    if (response != null && response.getStatus() == 200) {
                        Log.d("deletePlacesAround ->", response.getBody().toString());
                        Methods.showSnackBarPositive(PlacesNearByDetailsActivity.this, "Successfully Deleted.");
                        finish();
                    } else {
                        Methods.showSnackBarNegative(PlacesNearByDetailsActivity.this, getString(R.string.something_went_wrong));
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    if (error.getResponse().getStatus() == 200) {
                        Methods.showSnackBarPositive(PlacesNearByDetailsActivity.this, "Successfully Deleted.");
                        finish();
                    } else {
                        Methods.showSnackBarNegative(PlacesNearByDetailsActivity.this, getString(R.string.something_went_wrong));
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void uploadImageURL(String url) {
        uploadedImageURL = url;
        updatePlaceProfileImage();
    }
}


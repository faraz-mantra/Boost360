package com.nowfloats.AccrossVerticals.Testimonials;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.nowfloats.AccrossVerticals.API.APIInterfaces;
import com.nowfloats.AccrossVerticals.API.UploadProfileImage;
import com.nowfloats.AccrossVerticals.API.model.AddTestimonials.ActionData;
import com.nowfloats.AccrossVerticals.API.model.AddTestimonials.AddTestimonialsData;
import com.nowfloats.AccrossVerticals.API.model.AddTestimonials.Profileimage;
import com.nowfloats.AccrossVerticals.API.model.DeleteTestimonials.DeleteTestimonialsData;
import com.nowfloats.AccrossVerticals.API.model.GetTestimonials.Data;
import com.nowfloats.AccrossVerticals.API.model.UpdateTestimonialsData.UpdateTestimonialsData;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.manufacturing.projectandteams.ui.home.ProjectAndTermsActivity;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

public class TestimonialsFeedbackActivity extends AppCompatActivity implements TestimonialsFeedbackListener {

    private TextView saveButton, descriptionCharCount;
    private String ScreenType = "", itemId = "";
    Uri imageUri;
    String path = null;
    String uploadedImageURL = "";
    UserSessionManager session;
    Data existingItemData = null;
    EditText userNameText, reviewTitleText, reviewDescriptionText;
    CardView imageLayout;
    ImageView profileImage;
    ImageButton removeProfileImage;

    private final int gallery_req_id = 0;
    private final int media_req_id = 1;
    private static final int GALLERY_PHOTO = 2;
    private static final int CAMERA_PHOTO = 1;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testimonials_feedback);

        //setheader
        setHeader();
        initialization();
    }


    void initialization() {

        saveButton = (TextView) findViewById(R.id.save_review);
        session = new UserSessionManager(this, this);
        userNameText = findViewById(R.id.user_name);
        reviewTitleText = findViewById(R.id.review_title);
        reviewDescriptionText = findViewById(R.id.review_description);
        imageLayout = findViewById(R.id.card_primary_image);
        profileImage = findViewById(R.id.iv_primary_image);
        removeProfileImage = findViewById(R.id.ib_remove_product_image);
        descriptionCharCount = findViewById(R.id.description_char_count);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        reviewDescriptionText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int tempCount = 200 - s.length();
                descriptionCharCount.setText("(" + tempCount + " Characters)");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (path != null) {
                    showLoader("Uploading Image.Please Wait...");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            uploadImageToServer();
                        }
                    },200);
                } else {
                    uploadDataToServer();
                }
            }
        });

        imageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogToGetImage();
            }
        });

        removeProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileImage.setImageDrawable(null);
                removeProfileImage.setVisibility(View.GONE);
                uploadedImageURL = "";
                path = null;
            }
        });

        Bundle extra = getIntent().getExtras();
        ScreenType = extra.getString("ScreenState");
        if (ScreenType != null && ScreenType.equals("edit")) {
            displayData();
        }

    }

    public void displayData() {
        existingItemData = new Gson().fromJson(getIntent().getStringExtra("data"), Data.class);

        itemId = existingItemData.getId();
        uploadedImageURL = existingItemData.getProfileimage().getUrl();
        if (!uploadedImageURL.isEmpty()) {
            Glide.with(TestimonialsFeedbackActivity.this).load(uploadedImageURL).into(profileImage);
            removeProfileImage.setVisibility(View.VISIBLE);
        }

        userNameText.setText(existingItemData.getUsername());
        reviewTitleText.setText(existingItemData.getTitle());
        reviewDescriptionText.setText(existingItemData.getDescription());

    }

    public void setHeader() {
        LinearLayout rightButton, backButton;
        ImageView rightIcon;
        TextView title;

        title = findViewById(R.id.title);
        backButton = findViewById(R.id.back_button);
        rightButton = findViewById(R.id.right_icon_layout);
        rightIcon = findViewById(R.id.right_icon);
        title.setText("Testimonials");
        rightIcon.setImageResource(R.drawable.ic_delete_white_outerline);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ScreenType != null && ScreenType.equals("edit")) {
                    showLoader("Deleting Record.Please Wait...");
                    deleteRecord(itemId);
                    return;
                }
                onBackPressed();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void showDialogToGetImage() {
        final MaterialDialog dialog = new MaterialDialog.Builder(TestimonialsFeedbackActivity.this)
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

    void createNewTestimonialsAPI() {
        try {

            if (validateInput()) {

                ActionData actionData = new ActionData();
                actionData.setUsername(userNameText.getText().toString());
                actionData.setTitle(reviewTitleText.getText().toString());
                actionData.setDescription(reviewDescriptionText.getText().toString());


                Profileimage placeImage = new Profileimage();
                placeImage.setUrl(uploadedImageURL);
                placeImage.setDescription("");

                actionData.setProfileimage(placeImage);


                AddTestimonialsData request = new AddTestimonialsData();
                request.setWebsiteId(session.getFpTag());
                request.setActionData(actionData);

                APIInterfaces APICalls = new RestAdapter.Builder()
                        .setEndpoint("https://webaction.api.boostkit.dev")
                        .setLogLevel(RestAdapter.LogLevel.FULL)
                        .setLog(new AndroidLog("ggg"))
                        .build()
                        .create(APIInterfaces.class);

                APICalls.addTestimoinals(request, new Callback<String>() {
                    @Override
                    public void success(String s, Response response) {
                        hideLoader();
                        if (response.getStatus() != 200) {
                            Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(getApplicationContext(), "Successfully Added Testimonials", Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        hideLoader();
                        Methods.showSnackBarNegative(TestimonialsFeedbackActivity.this, getString(R.string.something_went_wrong));
                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void updateExistingTestimonialsAPI() {
        try {

            if (validateInput()) {

                ActionData actionData = new ActionData();
                actionData.setUsername(userNameText.getText().toString());
                actionData.setTitle(reviewTitleText.getText().toString());
                actionData.setDescription(reviewDescriptionText.getText().toString());


                Profileimage placeImage = new Profileimage();
                placeImage.setUrl(uploadedImageURL);
                placeImage.setDescription("");

                actionData.setProfileimage(placeImage);

                UpdateTestimonialsData requestBody = new UpdateTestimonialsData();
                requestBody.setQuery("{_id:'" + existingItemData.getId() + "'}");
                requestBody.setUpdateValue("{$set :" + new Gson().toJson(actionData) + "}");

                APIInterfaces APICalls = new RestAdapter.Builder()
                        .setEndpoint("https://webaction.api.boostkit.dev")
                        .setLogLevel(RestAdapter.LogLevel.FULL)
                        .setLog(new AndroidLog("ggg"))
                        .build()
                        .create(APIInterfaces.class);

                APICalls.updateTestimoinals(requestBody, new Callback<String>() {
                    @Override
                    public void success(String s, Response response) {
                        hideLoader();
                        if (response.getStatus() != 200) {
                            Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(getApplicationContext(), "Successfully Updated Testimonials", Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        hideLoader();
                        if (error.getResponse().getStatus() == 200) {
                            Toast.makeText(getApplicationContext(), "Successfully Updated Testimonials", Toast.LENGTH_LONG).show();
                            onBackPressed();
                        } else {
                            Methods.showSnackBarNegative(TestimonialsFeedbackActivity.this, getString(R.string.something_went_wrong));
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean validateInput() {
        if (userNameText.getText().toString().isEmpty() || reviewTitleText.getText().toString().isEmpty()
                || reviewDescriptionText.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Fields are Empty...", Toast.LENGTH_SHORT).show();
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
            Methods.showSnackBarNegative(TestimonialsFeedbackActivity.this, errorMessage);
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
            Methods.showSnackBarNegative(TestimonialsFeedbackActivity.this, errorMessage);
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
                        Toast.makeText(this, "Something went wrong. Try Later!!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        } catch (Exception e) {
            Log.e("onActivityResult ->", "Failed ->" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void uploadImageToServer() {
        try {
            if(validateInput()) {
                String fname = "Testimonials" + System.currentTimeMillis();
                if (!Util.isNullOrEmpty(path)) {
                    if (!TextUtils.isEmpty(path)) {
                        uploadedImageURL = new UploadProfileImage(TestimonialsFeedbackActivity.this, this, path, fname).execute().get();
                    }
                } else {
                    Methods.showSnackBarNegative(TestimonialsFeedbackActivity.this, getResources().getString(R.string.select_image_upload));
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

    public void updatePlaceProfileImage() {
        if (path != null) {
            Glide.with(TestimonialsFeedbackActivity.this).load(path).into(profileImage);
            removeProfileImage.setVisibility(View.VISIBLE);
        }
    }

    private void deleteRecord(String itemId) {
        try {
            DeleteTestimonialsData requestBody = new DeleteTestimonialsData();
            requestBody.setQuery("{_id:'" + itemId + "'}");
            requestBody.setUpdateValue("{$set : {IsArchived: true }}");
            requestBody.setMulti(true);

            APIInterfaces APICalls = new RestAdapter.Builder()
                    .setEndpoint("https://webaction.api.boostkit.dev")
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setLog(new AndroidLog("ggg"))
                    .setConverter(new GsonConverter(new GsonBuilder().setLenient().create()))
                    .build()
                    .create(APIInterfaces.class);

            APICalls.deleteTestimoinals(requestBody, new Callback<String>() {
                @Override
                public void success(String data, Response response) {
                    hideLoader();
                    if (response != null && response.getStatus() == 200) {
                        Log.d("deletePlacesAround ->", response.getBody().toString());
                        Toast.makeText(getApplicationContext(), "Successfully Deleted.", Toast.LENGTH_LONG).show();
                        onBackPressed();
                    } else {
                        Methods.showSnackBarNegative(TestimonialsFeedbackActivity.this, getString(R.string.something_went_wrong));
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    hideLoader();
                    if (error.getResponse().getStatus() == 200) {
                        Toast.makeText(getApplicationContext(), "Successfully Deleted.", Toast.LENGTH_LONG).show();
                        onBackPressed();
                    } else {
                        Methods.showSnackBarNegative(TestimonialsFeedbackActivity.this, getString(R.string.something_went_wrong));
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
            showLoader("Updating Record.Please Wait...");
            updateExistingTestimonialsAPI();
        } else {
            showLoader("Creating Record.Please Wait...");
            createNewTestimonialsAPI();
            Methods.hideKeyboard(TestimonialsFeedbackActivity.this);
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
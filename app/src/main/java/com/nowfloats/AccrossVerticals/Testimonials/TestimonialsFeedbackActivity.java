package com.nowfloats.AccrossVerticals.Testimonials;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Bundle;
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
import com.nowfloats.NavigationDrawer.floating_view.ImagePickerBottomSheetDialog;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Methods;
import com.nowfloats.util.WebEngageController;
import com.thinksity.R;

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
    private boolean isNewDataAdded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testimonials_feedback);

        Bundle extra = getIntent().getExtras();
        ScreenType = extra.getString("ScreenState");

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

                if (ScreenType.equals("edit")) {
                    updateExistingTestimonialsAPI();
                } else {
                    createNewTestimonialsAPI();
                    Methods.hideKeyboard(TestimonialsFeedbackActivity.this);
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
            }
        });
        if (ScreenType != null && ScreenType.equals("edit")) {
            displayData();
        }
    }

    public void displayData() {
        existingItemData = new Gson().fromJson(getIntent().getStringExtra("data"), Data.class);

        itemId = existingItemData.getId();
        uploadedImageURL = existingItemData.getProfileimage().getUrl();

        userNameText.setText(existingItemData.getUsername());
        reviewTitleText.setText(existingItemData.getTitle());
        reviewDescriptionText.setText(existingItemData.getDescription());

        updatePlaceProfileImage();
    }

    public void setHeader() {
        LinearLayout rightButton, backButton;
        ImageView rightIcon;
        TextView title;

        title = findViewById(R.id.title);
        backButton = findViewById(R.id.back_button);
        rightButton = findViewById(R.id.right_icon_layout);
        rightIcon = findViewById(R.id.right_icon);

        if (ScreenType != null && ScreenType.equals("edit")) {
            title.setText(R.string.editing_testimonial);
            rightIcon.setImageResource(R.drawable.ic_delete_white_outerline);
        } else {
            title.setText(R.string.add_a_testimonial);
        }
//        rightIcon.setImageResource(R.drawable.ic_delete_white_outerline);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ScreenType != null && ScreenType.equals("edit")) {
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

        final ImagePickerBottomSheetDialog imagePickerBottomSheetDialog = new ImagePickerBottomSheetDialog(this::onClickImagePicker);
        imagePickerBottomSheetDialog.show(getSupportFragmentManager(), ImagePickerBottomSheetDialog.class.getName());

//        final MaterialDialog dialog = new MaterialDialog.Builder(TestimonialsFeedbackActivity.this)
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
    }

    private void onClickImagePicker(ImagePickerBottomSheetDialog.IMAGE_CLICK_TYPE image_click_type) {
        if (image_click_type.name().equals(ImagePickerBottomSheetDialog.IMAGE_CLICK_TYPE.CAMERA.name()))
            cameraIntent();
        else if (image_click_type.name().equals(ImagePickerBottomSheetDialog.IMAGE_CLICK_TYPE.GALLERY.name()))
            galleryIntent();
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
                        if (response.getStatus() != 200) {
                            Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        WebEngageController.trackEvent("Testimonial added","MANAGE CONTENT",  session.getFpTag());
                        isNewDataAdded  = true;
                        Toast.makeText(getApplicationContext(), "Successfully Added Testimonials", Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Methods.showSnackBarNegative(TestimonialsFeedbackActivity.this, getString(R.string.something_went_wrong));
                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void updateExistingTestimonialsAPI() {

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
                if (response.getStatus() != 200) {
                    Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    return;
                }
//                Methods.showSnackBarPositive(TestimonialsFeedbackActivity.this, "Successfully Updated Testimonials");
                WebEngageController.trackEvent("MANAGE CONTENT", "Testimonial added", session.getFpTag());
                Toast.makeText(getApplicationContext(), "Successfully Updated Testimonials", Toast.LENGTH_LONG).show();
                onBackPressed();
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getResponse().getStatus() == 200) {
//                    Methods.showSnackBarPositive(TestimonialsFeedbackActivity.this, "Successfully Updated Testimonials");
                    Toast.makeText(getApplicationContext(), "Successfully Updated Testimonials", Toast.LENGTH_LONG).show();
                    onBackPressed();
                } else {
                    Methods.showSnackBarNegative(TestimonialsFeedbackActivity.this, getString(R.string.something_went_wrong));
                }
            }
        });

    }

    private boolean validateInput() {
        if (userNameText.getText().toString().isEmpty() || reviewTitleText.getText().toString().isEmpty()
                || reviewDescriptionText.getText().toString().isEmpty()) {
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
            Methods.showSnackBarNegative(TestimonialsFeedbackActivity.this, errorMessage);
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
            Methods.showSnackBarNegative(TestimonialsFeedbackActivity.this, errorMessage);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            path = null;
            if (resultCode == RESULT_OK && (CAMERA_PHOTO == requestCode)) {
                try {
                    Log.e("ImageURI ->", imageUri.getPath());
                    path = Methods.getRealPathFromURI(this, imageUri);
                    String fname = "Testimonials" + System.currentTimeMillis();
//                    path = Util.saveBitmap(path, TestimonialsFeedbackActivity.this, fname);
                    if (!Util.isNullOrEmpty(path)) {
                        if (!TextUtils.isEmpty(path)) {
//                            uploadFileToServer(path, fname);
                            new UploadProfileImage(TestimonialsFeedbackActivity.this, this, path, fname).execute().get();
                        }
                    } else
                        Methods.showSnackBarNegative(TestimonialsFeedbackActivity.this, getResources().getString(R.string.select_image_upload));
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
                        String fname = "Testimonials" + System.currentTimeMillis();
//                        path = Util.saveBitmap(path, TestimonialsFeedbackActivity.this, fname);
                        if (!Util.isNullOrEmpty(path)) {
                            if (!TextUtils.isEmpty(path)) {
//                                uploadFileToServer(path, fname);
                                uploadedImageURL = new UploadProfileImage(TestimonialsFeedbackActivity.this, this, path, fname).execute().get();
                            }
                        } else
                            Methods.showSnackBarNegative(TestimonialsFeedbackActivity.this, getResources().getString(R.string.select_image_upload));
                    }
                }
            }
        } catch (Exception e) {
            Log.e("onActivityResult ->", "Failed ->" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updatePlaceProfileImage() {
        if (!uploadedImageURL.isEmpty()) {
            Glide.with(TestimonialsFeedbackActivity.this).load(uploadedImageURL).into(profileImage);
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
                    if (response != null && response.getStatus() == 200) {
                        Log.d("deletePlacesAround ->", response.getBody().toString());
//                        Methods.showSnackBarPositive(TestimonialsFeedbackActivity.this, "Successfully Deleted.");
                        Toast.makeText(getApplicationContext(), "Successfully Deleted.", Toast.LENGTH_LONG).show();
                        onBackPressed();
                    } else {
                        Methods.showSnackBarNegative(TestimonialsFeedbackActivity.this, getString(R.string.something_went_wrong));
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    if (error.getResponse().getStatus() == 200) {
//                        Methods.showSnackBarPositive(TestimonialsFeedbackActivity.this, "Successfully Deleted.");
                        Toast.makeText(getApplicationContext(), "Successfully Deleted.", Toast.LENGTH_LONG).show();
                        onBackPressed();
                    } else {
                        Methods.showSnackBarNegative(TestimonialsFeedbackActivity.this, getString(R.string.something_went_wrong));
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

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        data.putExtra("IS_REFRESH", isNewDataAdded );
        setResult(RESULT_OK, data);
        super.onBackPressed();
    }
}
package com.nowfloats.hotel.seasonalOffers;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.ViewCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Build;
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
import com.nowfloats.AccrossVerticals.API.UploadProfileImage;
import com.nowfloats.AccrossVerticals.Testimonials.TestimonialsFeedbackActivity;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.floating_view.ImagePickerBottomSheetDialog;
import com.nowfloats.hotel.API.HotelAPIInterfaces;
import com.nowfloats.hotel.API.UploadOfferImage;
import com.nowfloats.hotel.API.UploadPlaceNearByImage;
import com.nowfloats.hotel.API.model.AddOffer.ActionData;
import com.nowfloats.hotel.API.model.AddOffer.AddOfferRequest;
import com.nowfloats.hotel.API.model.AddOffer.OfferImage;
import com.nowfloats.hotel.API.model.DeleteOffer.DeleteOfferRequest;
import com.nowfloats.hotel.API.model.GetOffers.Data;
import com.nowfloats.hotel.API.model.UpdateOffer.UpdateOfferRequest;
import com.nowfloats.hotel.Interfaces.SeasonalOffersDetailsListener;
import com.nowfloats.hotel.placesnearby.PlacesNearByDetailsActivity;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.WebEngageController;
import com.thinksity.R;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

import static com.framework.webengageconstant.EventLabelKt.UPDATED_BUINSESS_LOGO;
import static com.framework.webengageconstant.EventNameKt.UPLOAD_LOGO;

public class SeasonalOffersDetailsActivity extends AppCompatActivity implements SeasonalOffersDetailsListener {

    Data existingItemData = null;
    String ScreenType = "", itemId = "", uploadedImageURL = "";
    EditText offerTitleText, currentPriceText, discountText, offerDescriptionText;
    TextView checkButton, saveButton, offerPriceText;
    CardView placeImageLayout;
    ImageButton removePlaceImage;
    ImageView offerImage;
    Uri imageUri;
    String path = null;
    ImageButton removeOfferImage;
    double mrpPrice = 0, offerPrice = 0;
    UserSessionManager session;
    private final int gallery_req_id = 0;
    private final int media_req_id = 1;
    private static final int GALLERY_PHOTO = 2;
    private static final int CAMERA_PHOTO = 1;
    boolean checkButtonClickStatus = false;
    private ProgressDialog progressDialog;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seasonal_offers_details);

        initView();
    }

    private void initView() {

        offerTitleText = findViewById(R.id.offer_title);
        currentPriceText = findViewById(R.id.current_price);
        discountText = findViewById(R.id.discount);
        offerDescriptionText = findViewById(R.id.offer_description);
        checkButton = findViewById(R.id.check_button);
        saveButton = findViewById(R.id.save_button);
        offerImage = findViewById(R.id.iv_primary_image);
        removeOfferImage = findViewById(R.id.ib_remove_product_image);
        offerPriceText = findViewById(R.id.offer_price);
        session = new UserSessionManager(this, this);
        placeImageLayout = findViewById(R.id.card_primary_image);
        removePlaceImage = findViewById(R.id.ib_remove_product_image);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        placeImageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogToGetImage();
            }
        });

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!discountText.getText().toString().isEmpty() && !currentPriceText.getText().toString().isEmpty()) {
                    if (Double.parseDouble(discountText.getText().toString()) > 100) {
                        Methods.showSnackBarNegative(SeasonalOffersDetailsActivity.this, getString(R.string.invalid_discount_percent));
                        checkButtonClickStatus = false;
                    } else {
                        checkButtonClickStatus = true;
                    }
                    double discount = 100 - Double.parseDouble(discountText.getText().toString());
                    mrpPrice = Double.parseDouble(currentPriceText.getText().toString());
                    offerPrice = (discount * mrpPrice) / 100;
                    offerPriceText.setText("Rs." + String.valueOf(offerPrice));
                }else{
                    Toast.makeText(getApplicationContext(), getString(R.string.fields_are_empty), Toast.LENGTH_SHORT).show();
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
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
                offerImage.setImageDrawable(null);
                removePlaceImage.setVisibility(View.GONE);
                uploadedImageURL = "";
                path = null;
            }
        });


        //setheader
        setHeader();

        setEditTextListeners();

        Bundle extra = getIntent().getExtras();
        ScreenType = extra.getString("ScreenState");
        if (ScreenType != null && ScreenType.equals("edit")) {
            displayData();
        }
    }

    private void setEditTextListeners() {
//        offerTitleText.setOnFocusChangeListener(this);
//        offerDescriptionText.setOnFocusChangeListener(this);
//        offerPriceText.setOnFocusChangeListener(this);
//        discountText.setOnFocusChangeListener(this);
//        currentPriceText.setOnFocusChangeListener(this);
    }

    public void showDialogToGetImage() {
        final ImagePickerBottomSheetDialog imagePickerBottomSheetDialog = new ImagePickerBottomSheetDialog(this::onClickImagePicker);
        imagePickerBottomSheetDialog.show(getSupportFragmentManager(), ImagePickerBottomSheetDialog.class.getName());
//        final MaterialDialog dialog = new MaterialDialog.Builder(SeasonalOffersDetailsActivity.this)
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
        if (image_click_type.name().equals(ImagePickerBottomSheetDialog.IMAGE_CLICK_TYPE.CAMERA.name())) {
            cameraIntent();
        } else if (image_click_type.name().equals(ImagePickerBottomSheetDialog.IMAGE_CLICK_TYPE.GALLERY.name())) {
            galleryIntent();
        }
    }

    public void displayData() {
        checkButtonClickStatus = true;

        existingItemData = new Gson().fromJson(getIntent().getStringExtra("data"), Data.class);

        itemId = existingItemData.getId();
        uploadedImageURL = existingItemData.getOfferImage().getUrl();

        if (!uploadedImageURL.isEmpty()) {
            Glide.with(SeasonalOffersDetailsActivity.this).load(uploadedImageURL).into(offerImage);
            removeOfferImage.setVisibility(View.VISIBLE);
        }

        mrpPrice = existingItemData.getOrignalPrice();
        offerPrice = existingItemData.getDiscountedPrice();

        offerTitleText.setText(existingItemData.getOfferTitle());
        currentPriceText.setText(String.valueOf(mrpPrice));
        double discount = (mrpPrice - offerPrice) / (double) mrpPrice;
        discount *= 100.0;
        discountText.setText(new DecimalFormat("##.##").format(discount));
        offerPriceText.setText("Rs." + String.valueOf(offerPrice));
        offerDescriptionText.setText(existingItemData.getOfferDescription());

        currentPriceText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkButtonClickStatus = false;
                offerPriceText.setText("Rs.0");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        discountText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkButtonClickStatus = false;
                offerPriceText.setText("Rs.0");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        updateOffersImage();
    }

    public void setHeader() {
        LinearLayout rightButton, backButton;
        ImageView rightIcon;
        TextView title;

        title = findViewById(R.id.title);
        backButton = findViewById(R.id.back_button);
        rightButton = findViewById(R.id.right_icon_layout);
        rightIcon = findViewById(R.id.right_icon);
        title.setText("Offer Details");
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

    public void updateOffersImage() {
        if (path!=null) {
            Glide.with(SeasonalOffersDetailsActivity.this).load(path).into(offerImage);
            removeOfferImage.setVisibility(View.VISIBLE);
        }
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
            Methods.showSnackBarNegative(SeasonalOffersDetailsActivity.this, errorMessage);
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
            Methods.showSnackBarNegative(SeasonalOffersDetailsActivity.this, errorMessage);
        }
    }


    void createNewOfferAPI() {
        try {

            if (validateInput()) {

                ActionData actionData = new ActionData();
                actionData.setOrignalPrice(mrpPrice);
                actionData.setDiscountedPrice(offerPrice);
                actionData.setOfferTitle(offerTitleText.getText().toString());
                actionData.setOfferDescription(offerDescriptionText.getText().toString());


                OfferImage placeImage = new OfferImage();
                placeImage.setUrl(uploadedImageURL);
                placeImage.setDescription(offerDescriptionText.getText().toString());

                actionData.setOfferImage(placeImage);


                AddOfferRequest request = new AddOfferRequest();
                request.setWebsiteId(session.getFpTag());
                request.setActionData(actionData);

                HotelAPIInterfaces APICalls = new RestAdapter.Builder()
                        .setEndpoint("https://webaction.api.boostkit.dev")
                        .setLogLevel(RestAdapter.LogLevel.FULL)
                        .setLog(new AndroidLog("ggg"))
                        .build()
                        .create(HotelAPIInterfaces.class);

                APICalls.addOffer(request, new Callback<String>() {
                    @Override
                    public void success(String s, Response response) {
                        hideLoader();
                        if (response.getStatus() != 200) {
                            Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Methods.showSnackBarPositive(SeasonalOffersDetailsActivity.this, getString(R.string.successfully_added_offer_details));
                        onBackPressed();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        hideLoader();
                        Methods.showSnackBarNegative(SeasonalOffersDetailsActivity.this, getString(R.string.something_went_wrong));
                    }
                });

            }
        } catch (Exception e) {
            hideLoader();
            e.printStackTrace();
        }

    }

    private boolean validateInput() {
        if (offerTitleText.getText().toString().isEmpty() || currentPriceText.getText().toString().isEmpty()
                || discountText.getText().toString().isEmpty() || offerDescriptionText.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.fields_are_empty), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!checkButtonClickStatus) {
            Methods.showSnackBar(SeasonalOffersDetailsActivity.this, getString(R.string.click_check_button_to_continue));
            return false;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK && (CAMERA_PHOTO == requestCode)) {
                updateOffersImage();
            } else if (resultCode == RESULT_OK && (GALLERY_PHOTO == requestCode)) {
                {
                    Uri picUri = data.getData();
                    if (picUri != null) {
                        path = Methods.getPath(this, picUri);
                        updateOffersImage();
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
                String fname = "SeasonalOffers" + System.currentTimeMillis();
                if (!Util.isNullOrEmpty(path)) {
                    if (!TextUtils.isEmpty(path)) {
                        uploadedImageURL = new UploadOfferImage(SeasonalOffersDetailsActivity.this, this, path, fname).execute().get();
                    }
                } else {
                    Methods.showSnackBarNegative(SeasonalOffersDetailsActivity.this, getResources().getString(R.string.select_image_upload));
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

    void updateExistingOfferAPI() {

        try {
            if (validateInput()) {

                ActionData actionData = new ActionData();
                actionData.setOrignalPrice(mrpPrice);
                actionData.setDiscountedPrice(offerPrice);
                actionData.setOfferTitle(offerTitleText.getText().toString());
                actionData.setOfferDescription(offerDescriptionText.getText().toString());


                OfferImage placeImage = new OfferImage();
                placeImage.setUrl(uploadedImageURL);
                placeImage.setDescription(offerDescriptionText.getText().toString());

                actionData.setOfferImage(placeImage);

                UpdateOfferRequest requestBody = new UpdateOfferRequest();
                requestBody.setQuery("{_id:'" + existingItemData.getId() + "'}");
                requestBody.setUpdateValue("{$set :" + new Gson().toJson(actionData) + "}");

                HotelAPIInterfaces APICalls = new RestAdapter.Builder()
                        .setEndpoint("https://webaction.api.boostkit.dev")
                        .setLogLevel(RestAdapter.LogLevel.FULL)
                        .setLog(new AndroidLog("ggg"))
                        .build()
                        .create(HotelAPIInterfaces.class);

                APICalls.updateOffer(requestBody, new Callback<String>() {
                    @Override
                    public void success(String s, Response response) {
                        hideLoader();
                        if (response.getStatus() != 200) {
                            Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Methods.showSnackBarPositive(SeasonalOffersDetailsActivity.this, getString(R.string.successfully_updated_offer_details));
                        finish();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        hideLoader();
                        if (error.getResponse().getStatus() == 200) {
                            Methods.showSnackBarPositive(SeasonalOffersDetailsActivity.this, getString(R.string.successfully_updated_offer_details));
                            finish();
                        } else {
                            Methods.showSnackBarNegative(SeasonalOffersDetailsActivity.this, getString(R.string.something_went_wrong));
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
            DeleteOfferRequest requestBody = new DeleteOfferRequest();
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

            APICalls.deleteOffer(requestBody, new Callback<String>() {
                @Override
                public void success(String s, Response response) {
                    hideLoader();
                    if (response != null && response.getStatus() == 200) {
                        Log.d("deletePlacesAround ->", response.getBody().toString());
                        Methods.showSnackBarPositive(SeasonalOffersDetailsActivity.this, getString(R.string.successfully_deleted_));
                        finish();
                    } else {
                        Methods.showSnackBarNegative(SeasonalOffersDetailsActivity.this, getString(R.string.something_went_wrong));
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    hideLoader();
                    if (error.getResponse().getStatus() == 200) {
                        Methods.showSnackBarPositive(SeasonalOffersDetailsActivity.this, getString(R.string.successfully_deleted_));
                        finish();
                    } else {
                        Methods.showSnackBarNegative(SeasonalOffersDetailsActivity.this, getString(R.string.something_went_wrong));
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
            updateExistingOfferAPI();
        } else {
            showLoader(getString(R.string.creating_record_please_wait));
            createNewOfferAPI();
            Methods.hideKeyboard(SeasonalOffersDetailsActivity.this);
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

//    void elevationControllerEnable(View view){
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            view.setElevation(R.dimen._2sdp);
//        }
//    }
//
//    void elevationControllerDisable(View view){
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            view.setElevation(0);
//        }
//    }
//
//    @Override
//    public void onFocusChange(View v, boolean hasFocus) {
//        if(hasFocus){
//            elevationControllerEnable(v);
//        }else{
//            elevationControllerDisable(v);
//        }
//    }
}
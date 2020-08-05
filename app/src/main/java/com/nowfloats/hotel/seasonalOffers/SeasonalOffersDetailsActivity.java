package com.nowfloats.hotel.seasonalOffers;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Build;
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
import com.nowfloats.Login.UserSessionManager;
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
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.text.DecimalFormat;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

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
                        Methods.showSnackBarNegative(SeasonalOffersDetailsActivity.this, "Invalid Discount Percentage!!");
                        checkButtonClickStatus = false;
                    } else {
                        checkButtonClickStatus = true;
                    }
                    double discount = 100 - Double.parseDouble(discountText.getText().toString());
                    mrpPrice = Double.parseDouble(currentPriceText.getText().toString());
                    offerPrice = (discount * mrpPrice) / 100;
                    offerPriceText.setText("Rs." + String.valueOf(offerPrice));
                }else{
                    Toast.makeText(getApplicationContext(), "Fields are Empty...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ScreenType.equals("edit")) {
                    updateExistingOfferAPI();
                } else {
                    createNewOfferAPI();
                    Methods.hideKeyboard(SeasonalOffersDetailsActivity.this);
                }
            }
        });

        removePlaceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                offerImage.setImageDrawable(null);
                removePlaceImage.setVisibility(View.GONE);
                uploadedImageURL = "";
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
        final MaterialDialog dialog = new MaterialDialog.Builder(SeasonalOffersDetailsActivity.this)
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

    public void displayData() {
        checkButtonClickStatus = true;

        existingItemData = new Gson().fromJson(getIntent().getStringExtra("data"), Data.class);

        itemId = existingItemData.getId();
        uploadedImageURL = existingItemData.getOfferImage().getUrl();
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
        title.setText("Offer Details");
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

    public void updatePlaceProfileImage() {
        if (!uploadedImageURL.isEmpty()) {
            Glide.with(SeasonalOffersDetailsActivity.this).load(uploadedImageURL).into(offerImage);
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
            Methods.showSnackBarNegative(SeasonalOffersDetailsActivity.this, errorMessage);
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
                        if (response.getStatus() != 200) {
                            Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Methods.showSnackBarPositive(SeasonalOffersDetailsActivity.this, "Successfully Added Offer Details");
                        onBackPressed();
                    }

                    @Override
                    public void failure(RetrofitError error) {

                        Methods.showSnackBarNegative(SeasonalOffersDetailsActivity.this, getString(R.string.something_went_wrong));
                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean validateInput() {
        if (offerTitleText.getText().toString().isEmpty() || currentPriceText.getText().toString().isEmpty()
                || discountText.getText().toString().isEmpty() || offerDescriptionText.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Fields are Empty...", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!checkButtonClickStatus) {
            Methods.showSnackBar(SeasonalOffersDetailsActivity.this, "Click CHECK Button To Continue...");
            return false;
        }

        return true;
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
                    String fname = "PlaceNearBy" + System.currentTimeMillis();
//                    path = Util.saveBitmap(path, SeasonalOffersDetailsActivity.this, fname);
                    if (!Util.isNullOrEmpty(path)) {
                        if (!TextUtils.isEmpty(path)) {
//                            uploadFileToServer(path, fname);
                            new UploadOfferImage(SeasonalOffersDetailsActivity.this, this, path, fname).execute().get();
                        }
                    } else
                        Methods.showSnackBarNegative(SeasonalOffersDetailsActivity.this, getResources().getString(R.string.select_image_upload));
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
//                        path = Util.saveBitmap(path, SeasonalOffersDetailsActivity.this, fname);
                        if (!Util.isNullOrEmpty(path)) {
                            if (!TextUtils.isEmpty(path)) {
//                                uploadFileToServer(path, fname);
                                uploadedImageURL = new UploadOfferImage(SeasonalOffersDetailsActivity.this, this, path, fname).execute().get();
                            }
                        } else
                            Methods.showSnackBarNegative(SeasonalOffersDetailsActivity.this, getResources().getString(R.string.select_image_upload));
                    }
                }
            }
        } catch (Exception e) {
            Log.e("onActivityResult ->", "Failed ->" + e.getMessage());
            e.printStackTrace();
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
                        if (response.getStatus() != 200) {
                            Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Methods.showSnackBarPositive(SeasonalOffersDetailsActivity.this, "Successfully Updated Offer Details");
                        finish();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (error.getResponse().getStatus() == 200) {
                            Methods.showSnackBarPositive(SeasonalOffersDetailsActivity.this, "Successfully Updated Offer Details");
                            finish();
                        } else {
                            Methods.showSnackBarNegative(SeasonalOffersDetailsActivity.this, getString(R.string.something_went_wrong));
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
                    if (response != null && response.getStatus() == 200) {
                        Log.d("deletePlacesAround ->", response.getBody().toString());
                        Methods.showSnackBarPositive(SeasonalOffersDetailsActivity.this, "Successfully Deleted.");
                        finish();
                    } else {
                        Methods.showSnackBarNegative(SeasonalOffersDetailsActivity.this, getString(R.string.something_went_wrong));
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    if (error.getResponse().getStatus() == 200) {
                        Methods.showSnackBarPositive(SeasonalOffersDetailsActivity.this, "Successfully Deleted.");
                        finish();
                    } else {
                        Methods.showSnackBarNegative(SeasonalOffersDetailsActivity.this, getString(R.string.something_went_wrong));
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
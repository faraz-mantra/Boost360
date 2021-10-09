package com.nowfloats.AccrossVerticals.Testimonials;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.boost.upgrades.UpgradeActivity;
import com.bumptech.glide.Glide;
import com.framework.analytics.SentryController;
import com.framework.models.caplimit_feature.CapLimitFeatureResponseItem;
import com.framework.models.caplimit_feature.PropertiesItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nowfloats.AccrossVerticals.API.APIInterfaces;
import com.nowfloats.AccrossVerticals.API.UploadProfileImage;
import com.nowfloats.AccrossVerticals.API.model.AddTestimonials.ActionData;
import com.nowfloats.AccrossVerticals.API.model.AddTestimonials.AddTestimonialsData;
import com.nowfloats.AccrossVerticals.API.model.AddTestimonials.ProfileImagee;
import com.nowfloats.AccrossVerticals.API.model.AddTestimonials.Profileimage;
import com.nowfloats.AccrossVerticals.API.model.DeleteTestimonials.DeleteTestimonialsData;
import com.nowfloats.AccrossVerticals.API.model.GetTestimonials.TestimonialData;
import com.nowfloats.AccrossVerticals.API.model.GetToken.GetTokenData;
import com.nowfloats.AccrossVerticals.API.model.GetToken.WebActionsItem;
import com.nowfloats.AccrossVerticals.API.model.UpdateTestimonialsData.UpdateTestimonialsData;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.floating_view.ImagePickerBottomSheetDialog;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.WebEngageController;
import com.nowfloats.AccrossVerticals.API.model.GetTestimonials.GetTestimonialData;
import com.thinksity.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

import static com.framework.models.caplimit_feature.CapLimitFeatureResponseItemKt.filterFeature;
import static com.framework.models.caplimit_feature.CapLimitFeatureResponseItemKt.getCapData;
import static com.framework.pref.Key_Preferences.GET_FP_DETAILS_CATEGORY;
import static com.framework.utils.UtilKt.hideKeyBoard;
import static com.framework.webengageconstant.EventLabelKt.EVENT_LABEL_MANAGE_CONTENT;
import static com.framework.webengageconstant.EventLabelKt.EVENT_LABEL_TESTIMONIAL_ADDED;
import static com.framework.webengageconstant.EventNameKt.TESTIMONIAL_ADDED;

import static com.framework.webengageconstant.EventNameKt.TESTIMONIAL_UPDATED;
import static com.nowfloats.AccrossVerticals.Testimonials.TestimonialUtils.*;

import org.json.JSONException;
import org.json.JSONObject;

public class TestimonialsFeedbackActivity extends AppCompatActivity implements TestimonialsFeedbackListener {

  private List<String> allTestimonialType = Arrays.asList("testimonials", "testimonial", "guestreviews");
  private static final int GALLERY_PHOTO = 2;
  private static final int CAMERA_PHOTO = 1;
  private final int gallery_req_id = 0;
  private final int media_req_id = 1;
  Uri imageUri;
  String path = null;
  String uploadedImageURL = "";
  UserSessionManager session;
  TestimonialData existingItemData = null;
  EditText userNameText, reviewTitleText, reviewDescriptionText, profileDescEdt;
  LinearLayout profileDescView, reviewTitleView;
  TextView titleProfileDesc, isFillProfileDesc;
  CardView imageLayout;
  ImageView profileImage;
  ImageButton removeProfileImage;
  TextView reviewDescriptionLabel;
  TextView reviewTitleLabel;
  private TextView saveButton, descriptionCharCount;
  private String ScreenType = "", itemId = "";
  private ProgressDialog progressDialog;
  private boolean isNewDataAdded = false;
  private String headerToken = "59c89bbb5d64370a04c9aea1";
  private String testimonialType = "testimonials";
  private Activity activity;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_testimonials_feedback);
    activity = this;
    setHeader();
    initialization();
    getHeaderAuthToken();
    Log.v("experincecode", " " + session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY) + " " + session.getFPID() + " " + session.getFPDetails(Key_Preferences.GET_FP_WEBTEMPLATE_ID) + " " + session.getFpTag());
  }


  void initialization() {
    saveButton = (TextView) findViewById(R.id.save_review);
    session = new UserSessionManager(this, this);
    userNameText = findViewById(R.id.user_name);
    reviewTitleText = findViewById(R.id.review_title);
    reviewDescriptionText = findViewById(R.id.review_description);
    imageLayout = findViewById(R.id.card_primary_image);
    titleProfileDesc = findViewById(R.id.title_img_desc);
    isFillProfileDesc = findViewById(R.id.is_fill_img_desc);
    profileDescView = findViewById(R.id.img_desc_view);
    reviewTitleView = findViewById(R.id.review_title_view);
    profileDescEdt = findViewById(R.id.img_desc);
    profileImage = findViewById(R.id.iv_primary_image);
    removeProfileImage = findViewById(R.id.ib_remove_product_image);
    descriptionCharCount = findViewById(R.id.description_char_count);
    reviewDescriptionLabel = findViewById(R.id.reviewDescriptionLabel);
    reviewTitleLabel = findViewById(R.id.reviewTitleLabel);
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

    saveButton.setOnClickListener(v -> {
      if (path != null) {
        showLoader(getString(R.string.uploading_image_please_wait));
        new Handler().postDelayed(() -> uploadImageToServer(), 200);
      } else {
        uploadDataToServer();
      }
    });

    imageLayout.setOnClickListener(v -> showDialogToGetImage());
    removeProfileImage.setOnClickListener(v -> {
      profileImage.setImageDrawable(null);
      removeProfileImage.setVisibility(View.GONE);
      uploadedImageURL = "";
      path = null;
    });

    if (ScreenType != null && ScreenType.equals("edit")) {
      displayData();
    }

    reviewTitleLabel.setText(getReviewSecondTitle(session.getFP_AppExperienceCode()));
    reviewDescriptionLabel.setText(getDescTitle(session.getFP_AppExperienceCode()));

  }

  private boolean isValide() {
    return false;
  }

  public void displayData() {
    existingItemData = new Gson().fromJson(getIntent().getStringExtra("data"), TestimonialData.class);
    itemId = existingItemData.getId();
    if (existingItemData.getProfileimage() != null)
      uploadedImageURL = existingItemData.getProfileimage().getUrl();
    reviewTitleView.setVisibility(isReviewSecondValue(session.getFP_AppExperienceCode()));
    reviewTitleText.setText(getReviewSecondValue(existingItemData, session.getFP_AppExperienceCode()));
    userNameText.setText(existingItemData.getUsername());
    reviewDescriptionText.setText(existingItemData.getDescription());

    profileDescView.setVisibility(isProfileDescShow(session.getFP_AppExperienceCode()));
    profileDescEdt.setText(getProfileDescValue(existingItemData, session.getFP_AppExperienceCode()));
    titleProfileDesc.setText(getTitleProfileDesc(session.getFP_AppExperienceCode()));
    isFillProfileDesc.setVisibility(isProfileDescFill(session.getFP_AppExperienceCode()));

    if (!uploadedImageURL.isEmpty()) {
      Glide.with(TestimonialsFeedbackActivity.this).load(uploadedImageURL).into(profileImage);
      removeProfileImage.setVisibility(View.VISIBLE);
    }
  }

  public void setHeader() {
    LinearLayout rightButton, backButton;
    ImageView rightIcon;
    TextView title;

    Bundle extra = getIntent().getExtras();
    ScreenType = extra.getString("ScreenState");

    title = findViewById(R.id.title);
    backButton = findViewById(R.id.back_button);
    rightButton = findViewById(R.id.right_icon_layout);
    rightIcon = findViewById(R.id.right_icon);
    if (ScreenType.equals("edit")) {
      title.setText("Edit Testimonial");
      rightIcon.setImageResource(R.drawable.ic_delete_white_outerline);
      rightButton.setOnClickListener(v -> {
        if (ScreenType != null && ScreenType.equals("edit")) {
          showLoader(getString(R.string.deleting_record_please_wait));
          deleteRecord(itemId);
          return;
        }
        onBackPressed();
      });
    } else {
      title.setText("Add Testimonial");
    }

    backButton.setOnClickListener(v -> onBackPressed());
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

  void createNewTestimonialsAPI() {
    try {
      if (validateInput()) {
        ActionData actionData = getActionData(session.getFP_AppExperienceCode());
        AddTestimonialsData request = new AddTestimonialsData();
        request.setWebsiteId(session.getFpTag());
        request.setActionData(actionData);
        Log.v("addTest", " " + session.getFpTag());
        APIInterfaces APICalls = new RestAdapter.Builder()
            .setEndpoint("https://webaction.api.boostkit.dev")
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setLog(new AndroidLog("ggg"))
            .build()
            .create(APIInterfaces.class);

        APICalls.addTestimonials(headerToken, testimonialType, request, new Callback<String>() {
          @Override
          public void success(String s, Response response) {
            hideLoader();
            if (response.getStatus() != 200) {
              Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
              return;
            }
            WebEngageController.trackEvent(TESTIMONIAL_ADDED, EVENT_LABEL_MANAGE_CONTENT, session.getFpTag());
            Toast.makeText(getApplicationContext(), "Successfully Added Testimonials", Toast.LENGTH_LONG).show();
            isNewDataAdded = true;
            onBackPressed();
          }

          @Override
          public void failure(RetrofitError error) {
            Log.v("addTestimonial", " message: " + error.getMessage() + " error: " + error);
            hideLoader();
            Methods.showSnackBarNegative(TestimonialsFeedbackActivity.this, getString(R.string.something_went_wrong));
          }
        });

      }
    } catch (Exception e) {
      SentryController.INSTANCE.captureException(e);
      e.printStackTrace();
    }

  }

  private ActionData getActionData(String fp_appExperienceCode) {
    ActionData actionData = new ActionData();
    String name = userNameText.getText().toString();
    String desc = reviewDescriptionText.getText().toString();
    String review = reviewTitleText.getText().toString();
    String profileDesc = profileDescEdt.getText().toString();
    switch (fp_appExperienceCode) {
      case "HOT":
        actionData.setCustomerName(name);
        actionData.setTestimonial(desc);
        actionData.setCity(review);
        ProfileImagee placeImage = new ProfileImagee();
        placeImage.setUrl(uploadedImageURL);
        placeImage.setDescription(profileDesc);
        actionData.setProfileImage(placeImage);
        break;
      case "DOC":
      case "HOS":
      case "EDU":
      case "SVC":
      case "RTL":
        actionData.setUsername(name);
        actionData.setDescription(desc);
        Profileimage placeImageDH = new Profileimage();
        placeImageDH.setUrl(uploadedImageURL);
        placeImageDH.setDescription(profileDesc);
        actionData.setProfileimage(placeImageDH);
        break;
      case "CAF":
        actionData.setName(name);
        actionData.setText(desc);
        ProfileImagee placeImageC = new ProfileImagee();
        placeImageC.setUrl(uploadedImageURL);
        placeImageC.setDescription(profileDesc);
        actionData.setImg(placeImageC);
        break;
      case "SPA":
      case "SAL":
        actionData.setName(name);
        actionData.setTitle(review);
        actionData.setOurStory(desc);
        Profileimage placeImageSP = new Profileimage();
        placeImageSP.setUrl(uploadedImageURL);
        placeImageSP.setDescription(profileDesc);
        actionData.setProfileimage(placeImageSP);
        break;
      case "MFG":
      default:
        actionData.setUsername(name);
        actionData.setTitle(review);
        actionData.setDescription(desc);
        Profileimage placeImageD = new Profileimage();
        placeImageD.setUrl(uploadedImageURL);
        placeImageD.setDescription(profileDesc);
        actionData.setProfileimage(placeImageD);
        break;
    }
    return actionData;
  }

  void updateExistingTestimonialsAPI() {
    try {
      if (validateInput()) {
        ActionData actionData = getActionData(session.getFP_AppExperienceCode());
        UpdateTestimonialsData requestBody = new UpdateTestimonialsData();
        requestBody.setQuery("{_id:'" + existingItemData.getId() + "'}");
        requestBody.setUpdateValue("{$set :" + new Gson().toJson(actionData) + "}");
        APIInterfaces APICalls = new RestAdapter.Builder()
            .setEndpoint("https://webaction.api.boostkit.dev")
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setLog(new AndroidLog("ggg"))
            .build()
            .create(APIInterfaces.class);
        APICalls.updateTestimonials(headerToken, testimonialType, requestBody, new Callback<String>() {
          @Override
          public void success(String s, Response response) {
            hideLoader();
            if (response.getStatus() != 200) {
              Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
              return;
            }
            WebEngageController.trackEvent(TESTIMONIAL_UPDATED, EVENT_LABEL_TESTIMONIAL_ADDED, session.getFpTag());
            Toast.makeText(getApplicationContext(), "Successfully Updated Testimonials", Toast.LENGTH_LONG).show();
            onBackPressed();
          }

          @Override
          public void failure(RetrofitError error) {
            hideLoader();
            if (error != null && error.getResponse() != null && error.getResponse().getStatus() == 200) {
              Toast.makeText(getApplicationContext(), getString(R.string.successfully_updated_testimonials), Toast.LENGTH_LONG).show();
              onBackPressed();
            } else {
              Methods.showSnackBarNegative(TestimonialsFeedbackActivity.this, getString(R.string.something_went_wrong));
            }
          }
        });
      }
    } catch (Exception e) {
      SentryController.INSTANCE.captureException(e);
      e.printStackTrace();
    }

  }

  private boolean validateInput() {
    if (userNameText.getText().toString().isEmpty()) {
      return messageInpute();
    } else if (reviewTitleView.getVisibility() != View.GONE && reviewTitleText.getText().toString().isEmpty()) {
      return messageInpute();
    } else if (reviewDescriptionText.getText().toString().isEmpty() || (isProfileDescFill(session.getFP_AppExperienceCode()) == View.VISIBLE && profileDescEdt.getText().toString().isEmpty())) {
      return messageInpute();
    } else {
      return true;
    }
  }

  private boolean messageInpute() {
    Toast.makeText(getApplicationContext(), "Fields are empty...", Toast.LENGTH_SHORT).show();
    hideLoader();
    return false;
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
          SentryController.INSTANCE.captureException(ex);
          ex.printStackTrace();
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
          Uri photoURI = FileProvider.getUriForFile(this, Constants.PACKAGE_NAME + ".provider", photoFile);
          takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
          startActivityForResult(takePictureIntent, CAMERA_PHOTO);
        }
      }

    } catch (ActivityNotFoundException e) {
      SentryController.INSTANCE.captureException(e);
      String errorMessage = getResources().getString(R.string.device_does_not_support_capturing_image);
      Methods.showSnackBarNegative(TestimonialsFeedbackActivity.this, errorMessage);
    } catch (Exception e) {
      SentryController.INSTANCE.captureException(e);
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
      if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, gallery_req_id);
        return;
      }
      Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
      startActivityForResult(i, GALLERY_PHOTO);
    } catch (ActivityNotFoundException anfe) {
      SentryController.INSTANCE.captureException(anfe);
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
            Toast.makeText(this, getString(R.string.something_went_wrong_try_later), Toast.LENGTH_LONG).show();
          }
        }
      }
    } catch (Exception e) {
      SentryController.INSTANCE.captureException(e);
      Log.e("onActivityResult ->", "Failed ->" + e.getMessage());
      e.printStackTrace();
    }
  }

  public void uploadImageToServer() {
    try {
      if (validateInput()) {
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
      SentryController.INSTANCE.captureException(e);
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

      APICalls.deleteTestimonials(headerToken, testimonialType, requestBody, new Callback<String>() {
        @Override
        public void success(String data, Response response) {
          hideLoader();
          if (response != null && response.getStatus() == 200) {
            Log.d("deletePlacesAround ->", response.getBody().toString());
            Toast.makeText(getApplicationContext(), getString(R.string.successfully_deleted_), Toast.LENGTH_LONG).show();
            onBackPressed();
          } else {
            Methods.showSnackBarNegative(TestimonialsFeedbackActivity.this, getString(R.string.something_went_wrong));
          }
        }

        @Override
        public void failure(RetrofitError error) {
          hideLoader();
          if (error != null && error.getResponse() != null && error.getResponse().getStatus() == 200) {
            Toast.makeText(getApplicationContext(), getString(R.string.successfully_deleted_), Toast.LENGTH_LONG).show();
            onBackPressed();
          } else {
            Methods.showSnackBarNegative(TestimonialsFeedbackActivity.this, getString(R.string.something_went_wrong));
          }
        }
      });

    } catch (Exception e) {
      SentryController.INSTANCE.captureException(e);
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
      updateExistingTestimonialsAPI();
    } else {
      showLoader(getString(R.string.creating_record_please_wait));
      createNewTestimonialsAPI();
      Methods.hideKeyboard(TestimonialsFeedbackActivity.this);
    }
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

  @Override
  public void onBackPressed() {
    Intent data = new Intent();
    data.putExtra("IS_REFRESH", isNewDataAdded);
    setResult(RESULT_OK, data);
    super.onBackPressed();
  }

  private void getHeaderAuthToken() {
    testimonialType = session.getFPDetails(TestimonialType.TESTIMONIAL_TYPE.name());
    headerToken = session.getFPDetails(TestimonialType.TESTIMONIAL_HEADER.name());
    if (testimonialType.isEmpty() && headerToken.isEmpty()) {
      try {
        APIInterfaces APICalls = new RestAdapter.Builder().setEndpoint("https://developer.api.boostkit.dev")
            .setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog("ggg")).build().create(APIInterfaces.class);
        Log.v("newvlue", " " + session.getFPDetails(Key_Preferences.GET_FP_WEBTEMPLATE_ID) + " " + session.getFpTag());
        APICalls.getHeaderAuthorizationtoken(session.getFPDetails(Key_Preferences.GET_FP_WEBTEMPLATE_ID), session.getFpTag(), new Callback<GetTokenData>() {
          @Override
          public void success(GetTokenData s, Response response) {
            Log.v("experincecode1", " " + s.getToken() + " " + response.getReason() + " " + response.getBody());
            int status = response.getStatus();
            if ((status == 200 || status == 201 || status == 202) && s != null) {

              if (s.getWebActions() != null && !s.getWebActions().isEmpty()) {
                loopBreak:
                for (WebActionsItem action : s.getWebActions()) {
                  for (String type : allTestimonialType) {
                    if (action.getName().equalsIgnoreCase(type)) {
                      testimonialType = action.getName();
                      break loopBreak;
                    }
                  }
                }
              }

              headerToken = s.getToken();
              session.storeFPDetails(TestimonialType.TESTIMONIAL_TYPE.name(), testimonialType);
              session.storeFPDetails(TestimonialType.TESTIMONIAL_HEADER.name(), headerToken);
              capLimitCheck(testimonialType, headerToken);
            } else {
              Toast.makeText(getApplicationContext(), response.getStatus(), Toast.LENGTH_SHORT).show();
              headerToken = "";
            }
          }

          @Override
          public void failure(RetrofitError error) {
            Log.v("experincecode2", " " + error.getBody() + " " + error.getMessage());
            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            Methods.showSnackBarNegative(TestimonialsFeedbackActivity.this, getString(R.string.something_went_wrong));
          }
        });
      } catch (Exception e) {
        SentryController.INSTANCE.captureException(e);
        Log.v("experincecode3", " " + e.getMessage() + " " + e.getStackTrace());
        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        e.printStackTrace();
      }
    } else capLimitCheck(testimonialType, headerToken);
  }

  private void capLimitCheck(String testimonialType, String headerToken) {
    try {
      JSONObject query = new JSONObject();
      query.put("WebsiteId", session.getFpTag());
      APIInterfaces APICalls = new RestAdapter.Builder().setEndpoint("https://webaction.api.boostkit.dev")
          .setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog("ggg")).build()
          .create(APIInterfaces.class);
      APICalls.getTestimonialsList(headerToken, testimonialType, query, 0, 2, new Callback<GetTestimonialData>() {
        @Override
        public void success(GetTestimonialData testimonialModel, Response response) {
          CapLimitFeatureResponseItem data = filterFeature(getCapData(), CapLimitFeatureResponseItem.FeatureType.TESTIMONIALS);
          if (data != null && testimonialModel.getExtra() != null) {
            PropertiesItem capLimitTestimonial = data.filterProperty(PropertiesItem.KeyType.LIMIT);
            if (testimonialModel.getExtra().getTotalCount() != null && capLimitTestimonial.getValueN() != null && testimonialModel.getExtra().getTotalCount() >= capLimitTestimonial.getValueN()) {
              hideKeyBoard(activity);
              showAlertCapLimit("Can't add the testimonial, please activate your premium Add-ons plan.");
            }
          }
        }

        @Override
        public void failure(RetrofitError error) {
          Log.v("failure", " " + error.getMessage());
        }
      });
    } catch (JSONException e) {
      SentryController.INSTANCE.captureException(e);
      e.printStackTrace();
    }
  }

  void showAlertCapLimit(String msg) {
    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomAlertDialogTheme));
    builder.setCancelable(false);
    builder.setTitle("You have exceeded limit!").setMessage(msg);
    builder.setPositiveButton("Explore Add-ons", (dialog, which) -> {
      dialog.dismiss();
      initiateBuyFromMarketplace();
      activity.finish();
    });
    builder.setNegativeButton("Close", (dialog, which) -> {
      dialog.dismiss();
      activity.finish();
    });
    builder.create().show();
  }

  private void initiateBuyFromMarketplace() {
    ProgressDialog progressDialog = new ProgressDialog(this);
    String status = "Loading. Please wait...";
    progressDialog.setMessage(status);
    progressDialog.setCancelable(false);
    progressDialog.show();
    Intent intent = new Intent(activity, UpgradeActivity.class);
    intent.putExtra("expCode", session.getFP_AppExperienceCode());
    intent.putExtra("fpName", session.getFPName());
    intent.putExtra("fpid", session.getFPID());
    intent.putExtra("fpTag", session.getFpTag());
    intent.putExtra("accountType", session.getFPDetails(GET_FP_DETAILS_CATEGORY));
    intent.putStringArrayListExtra("userPurchsedWidgets", Constants.StoreWidgets);
    if (session.getUserProfileEmail() != null) {
      intent.putExtra("email", session.getUserProfileEmail());
    } else {
      intent.putExtra("email", "ria@nowfloats.com");
    }
    if (session.getUserPrimaryMobile() != null) {
      intent.putExtra("mobileNo", session.getUserPrimaryMobile());
    } else {
      intent.putExtra("mobileNo", "9160004303");
    }
    intent.putExtra("profileUrl", session.getFPLogo());
    intent.putExtra("buyItemKey", CapLimitFeatureResponseItem.FeatureType.TESTIMONIALS.name());
    startActivity(intent);
    new Handler().postDelayed(() -> progressDialog.dismiss(), 1000);
  }
}
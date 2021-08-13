package com.nowfloats.BusinessProfile.UI.UI;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.framework.models.firestore.FirestoreManager;
import com.nowfloats.BusinessProfile.UI.API.Upload_Logo;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.EditImageActivity;
import com.nowfloats.NavigationDrawer.floating_view.ImagePickerBottomSheetDialog;
import com.nowfloats.NotificationCenter.AlertArchive;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.WebEngageController;
import com.thinksity.R;
import com.thinksity.databinding.ActivityBusinessLogoBinding;

import java.io.File;
import java.io.InputStream;

import static com.framework.webengageconstant.EventLabelKt.BUSINESS_PROFILE;
import static com.framework.webengageconstant.EventLabelKt.UPDATED_BUINSESS_LOGO;
import static com.framework.webengageconstant.EventNameKt.BUSINESS_LOGO_ADDED;
import static com.framework.webengageconstant.EventNameKt.EVENT_NAME_BUSINESS_PROFILE;
import static com.framework.webengageconstant.EventNameKt.UPLOAD_LOGO;

public class Business_Logo_Activity extends AppCompatActivity {
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_GALLERY = 2;
    private static final int ACTION_REQUEST_IMAGE_EDIT = 3;
    private static final int GALLERY_PHOTO = 2;
    private static final int CAMERA_PHOTO = 1;
    public static ImageView logoimageView;
    private final int gallery_req_id = 0;
    private final int media_req_id = 1;
    Button uploadButton;
    ContentValues values;
    Uri imageUri;
    Bitmap CameraBitmap;
    String path = null;
    String imageUrl = "";
    PorterDuffColorFilter whiteLabelFilter;
    UserSessionManager session;
    ProgressDialog mProgressDialog;
    ActivityBusinessLogoBinding binding;
    private Toolbar toolbar;
    private TextView titleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_business_logo);
        //setContentView(R.layout.activity_business__logo);

        setSupportActionBar(binding.appBar.toolbar);
        Methods.isOnline(Business_Logo_Activity.this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            getSupportActionBar().setTitle("");
        }
        binding.appBar.toolbarTitle.setText(getResources().getString(R.string.business_logo));

        /*toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(getResources().getString(R.string.logo));

        titleTextView = (TextView) toolbar.findViewById(R.id.titleTextView);
        titleTextView.setText(getResources().getString(R.string.business_logo));*/
        session = new UserSessionManager(getApplicationContext(), Business_Logo_Activity.this);
        WebEngageController.trackEvent(EVENT_NAME_BUSINESS_PROFILE, BUSINESS_PROFILE, session.getFpTag());
        logoimageView = (ImageView) findViewById(R.id.logoimageView);
        uploadButton = (Button) findViewById(R.id.addLogoButton);

        try {
            uploadButton.setText(getResources().getString(R.string.change));
            String iconUrl = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_LogoUrl);
            if (iconUrl != null && iconUrl.length() > 0 && !iconUrl.contains("http")) {
                BoostLog.d("Logo Url:", iconUrl);
                Glide.with(this).asGif().load(iconUrl).apply(new RequestOptions().placeholder(R.drawable.logo_default_image)).into(logoimageView);
                onBusinessLogoAddedOrUpdated(true);
            } else {
                if (iconUrl != null && iconUrl.length() > 0) {
                    BoostLog.d("Logo Url:", iconUrl);
                    Glide.with(this).load(iconUrl).apply(new RequestOptions().placeholder(R.drawable.logo_default_image)).into(logoimageView);
                    onBusinessLogoAddedOrUpdated(true);
                } else {
                    Glide.with(this).asGif().load(R.drawable.logo_default_image).into(logoimageView);
                    onBusinessLogoAddedOrUpdated(false);
                    uploadButton.setText(getResources().getString(R.string.add_logo));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.gc();
        }
        logoimageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(Business_Logo_Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Business_Logo_Activity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, gallery_req_id);
                    return;
                }
                if (TextUtils.isEmpty(path)) {
                    new DownloadImage().execute();
                } else {
                    editImage();
                }
            }
        });
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//        if (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE).equals("-1")) {
//          Methods.showFeatureNotAvailDialog(Business_Logo_Activity.this);
//          return;
//        }
                final ImagePickerBottomSheetDialog imagePickerBottomSheetDialog = new ImagePickerBottomSheetDialog(this::onClickImagePicker);
                imagePickerBottomSheetDialog.show(getSupportFragmentManager(), ImagePickerBottomSheetDialog.class.getName());

//                final MaterialDialog dialog = new MaterialDialog.Builder(Business_Logo_Activity.this)
//                        .customView(R.layout.featuredimage_popup,true)
//                        .show();
//
//                View view = dialog.getCustomView();
//                TextView title = (TextView) view.findViewById(R.id.textview_heading);
//                title.setText(getResources().getString(R.string.upload_logo_image));
//                LinearLayout takeCamera = (LinearLayout) view.findViewById(R.id.cameraimage);
//                LinearLayout takeGallery = (LinearLayout) view.findViewById(R.id.galleryimage);
//                ImageView   cameraImg = (ImageView) view.findViewById(R.id.pop_up_camera_imag);
//                ImageView galleryImg = (ImageView) view.findViewById(R.id.pop_up_gallery_img);
//                cameraImg.setColorFilter(whiteLabelFilter);
//                galleryImg.setColorFilter(whiteLabelFilter);
//
//                takeCamera.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        MixPanelController.track(EventKeysWL.UPDATE_LOGO_CAMERA,null);
//                        WebEngageController.trackEvent("UPLOAD LOGO","Updated Buinsess Logo",session.getFpTag());
//                        cameraIntent();
//                        dialog.dismiss();
//                    }
//                });
//
//                takeGallery.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        MixPanelController.track(EventKeysWL.UPDATE_LOGO_GALLERY,null);
//                        galleryIntent();
//                        dialog.dismiss();
//
//                    }
//                });

            }

            private void onClickImagePicker(ImagePickerBottomSheetDialog.IMAGE_CLICK_TYPE image_click_type) {
                if (image_click_type.name().equals(ImagePickerBottomSheetDialog.IMAGE_CLICK_TYPE.CAMERA.name())) {
                    MixPanelController.track(EventKeysWL.UPDATE_LOGO_CAMERA, null);
                    WebEngageController.trackEvent(UPLOAD_LOGO, UPDATED_BUINSESS_LOGO, session.getFpTag());
                    cameraIntent();
                } else if (image_click_type.name().equals(ImagePickerBottomSheetDialog.IMAGE_CLICK_TYPE.GALLERY.name())) {
                    MixPanelController.track(EventKeysWL.UPDATE_LOGO_GALLERY, null);
                    galleryIntent();
                }
            }
        });


//        uploadButton.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                selectImage();
//
//                return false;
//            }
//        });
    }

    private void selectImage() {
        final CharSequence[] items = {"Take photo", "Choose from library", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder( new ContextThemeWrapper(this,R.style.CustomAlertDialogTheme));
        builder.setTitle("Add photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment
                            .getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, PICK_FROM_CAMERA);
                } else if (items[item].equals("Choose from library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    //intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select file"),
                            PICK_FROM_GALLERY);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == media_req_id) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                cameraIntent();

            }

        } else if (requestCode == gallery_req_id) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                galleryIntent();

            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_business__logo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        return super.onOptionsItemSelected(item);
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
            values = new ContentValues();
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
            Methods.showSnackBarNegative(Business_Logo_Activity.this, errorMessage);
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
            Methods.showSnackBarNegative(Business_Logo_Activity.this, errorMessage);
        }
    }

    private void onBusinessLogoAddedOrUpdated(Boolean isAdded) {
        FirestoreManager instance = FirestoreManager.INSTANCE;
        if (instance.getDrScoreData() != null && instance.getDrScoreData().getMetricdetail() != null) {
            instance.getDrScoreData().getMetricdetail().setBoolean_add_clinic_logo(isAdded);
            instance.updateDocument();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK && (CAMERA_PHOTO == requestCode)) {

                try {
                    CameraBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    imageUrl = Methods.getRealPathFromURI(this, imageUri);
                    path = imageUrl;
                    path = Util.saveBitmap(path, Business_Logo_Activity.this, "ImageFloat" + System.currentTimeMillis());
                } catch (Exception e) {
                    e.printStackTrace();
                    //  Util.toast("Uh oh. Something went wrong. Please try again", this);
                } catch (OutOfMemoryError E) {
                    //Log.d("ANDRO_ASYNC",String.format("catch Out Of Memory error"));
                    E.printStackTrace();
                    System.gc();
                    // Util.toast("Uh oh. Something went wrong. Please try again", this);
                }
                if (!Util.isNullOrEmpty(path)) {
                    editImage();
                } else
                    Methods.showSnackBarNegative(Business_Logo_Activity.this, getResources().getString(R.string.select_image_upload));
            } else if (resultCode == RESULT_OK && (GALLERY_PHOTO == requestCode)) {
                {
                    Uri picUri = data.getData();
                    if (picUri != null) {
                        path = Methods.getPath(this, picUri);
                        path = Util.saveBitmap(path, Business_Logo_Activity.this, "ImageFloat" + System.currentTimeMillis());
                        if (!Util.isNullOrEmpty(path)) {
                            editImage();
                        } else
                            Methods.showSnackBarNegative(Business_Logo_Activity.this, getResources().getString(R.string.select_image_upload));
                    }
                }
            } else if (resultCode == RESULT_OK && requestCode == ACTION_REQUEST_IMAGE_EDIT) {
                String path = data.getStringExtra("edit_image");
                if (!TextUtils.isEmpty(path)) {
                    this.path = path;
                    WebEngageController.trackEvent(BUSINESS_LOGO_ADDED, BUSINESS_PROFILE, session.getFpTag());
                    uploadPrimaryPicture(path);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void editImage() {
        Intent in = new Intent(Business_Logo_Activity.this, EditImageActivity.class);
        in.putExtra("image", path);
        in.putExtra("isFixedAspectRatio", true);
        startActivityForResult(in, ACTION_REQUEST_IMAGE_EDIT);
    }

    public void uploadPrimaryPicture(String path) {
        uploadButton.setText(getResources().getString(R.string.change));
        new AlertArchive(Constants.alertInterface, "LOGO", session.getFPID());
        Upload_Logo upload_logo = new Upload_Logo(Business_Logo_Activity.this, path, session.getFPID(), session, this::imageUpload);
        upload_logo.execute();
//        Constants.isImgUploaded = false;
//        UploadPictureAsyncTask upa = new UploadPictureAsyncTask(Business_Logo_Activity.this, path, false,true,session.getFPID());
//        upa.execute();
    }

    private void imageUpload(Boolean isSuccess) {
        if (isSuccess) {
            onBusinessLogoAddedOrUpdated(true);
        }
    }

    private class DownloadImage extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(Business_Logo_Activity.this);
            // Set progressdialog title
            // Set progressdialog message
            mProgressDialog.setMessage(getString(R.string.please_wait));
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... URL) {

            String imageURL = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_LogoUrl);

            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            path = Util.saveCameraBitmap(bitmap, Business_Logo_Activity.this, "ImageFloat" + System.currentTimeMillis());
            return path;
        }

        @Override
        protected void onPostExecute(String result) {
            // Set the bitmap into ImageView
            mProgressDialog.dismiss();
            editImage();

        }
    }
}
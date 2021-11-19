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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.framework.analytics.SentryController;
import com.framework.firebaseUtils.firestore.FirestoreManager;
import com.nowfloats.BusinessProfile.UI.API.uploadIMAGEURI;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.EditImageActivity;
import com.nowfloats.NavigationDrawer.floating_view.ImagePickerBottomSheetDialog;
import com.nowfloats.NotificationCenter.AlertArchive;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.WebEngageController;
import com.squareup.picasso.Picasso;
import com.thinksity.R;
import com.thinksity.databinding.ActivityFeaturedImageBinding;

import java.io.File;
import java.io.InputStream;

import static com.framework.webengageconstant.EventLabelKt.MANAGE_CONTENT;
import static com.framework.webengageconstant.EventLabelKt.UPDATED_FEATURED_IMAGE;
import static com.framework.webengageconstant.EventNameKt.FEATURED_IMAGE_ADDED;
import static com.framework.webengageconstant.EventNameKt.UPLOAD_FEATURED_IMAGE;

public class FeaturedImageActivity extends AppCompatActivity {
    private static final String TAG = "FeaturedImageActivity";
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
    ActivityFeaturedImageBinding binding;
    private Toolbar toolbar;
    private TextView titleTextView;

    private void onFeatureLogoAddedOrUpdated(Boolean isAdded) {
        FirestoreManager instance = FirestoreManager.INSTANCE;
        if (instance.getDrScoreData() != null && instance.getDrScoreData().getMetricdetail() != null) {
            instance.getDrScoreData().getMetricdetail().setBoolean_add_featured_image_video(isAdded);
            instance.updateDocument();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_featured_image);
        //setContentView(R.layout.activity_business__logo);

        setSupportActionBar(binding.appBar.toolbar);
        Methods.isOnline(FeaturedImageActivity.this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            getSupportActionBar().setTitle("");
        }

        binding.appBar.toolbarTitle.setText(getResources().getString(R.string.featured_image));

        /*toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(getResources().getString(R.string.logo));

        titleTextView = (TextView) toolbar.findViewById(R.id.titleTextView);
        titleTextView.setText(getResources().getString(R.string.business_logo));*/
        session = new UserSessionManager(getApplicationContext(), FeaturedImageActivity.this);
        logoimageView = (ImageView) findViewById(R.id.logoimageView);
        uploadButton = (Button) findViewById(R.id.addLogoButton);

        Log.d("FP_ID", session.getFPID());

        try {
            uploadButton.setText(getResources().getString(R.string.change));
            String iconUrl = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_IMAGE_URI);
            if (iconUrl.length() > 0 && iconUrl.contains("BizImages") && !iconUrl.contains("http")) {
                String baseNameProfileImage = Constants.BASE_IMAGE_URL + "" + iconUrl;
                Picasso.get().load(baseNameProfileImage).placeholder(R.drawable.featured_photo_default).into(logoimageView);
                onFeatureLogoAddedOrUpdated(true);
            } else {
                if (!TextUtils.isEmpty(iconUrl)) {
                    Picasso.get().load(iconUrl).placeholder(R.drawable.featured_photo_default).into(logoimageView);
                    onFeatureLogoAddedOrUpdated(true);
                } else {
                    Picasso.get().load(R.drawable.featured_photo_default).into(logoimageView);
                    onFeatureLogoAddedOrUpdated(false);
                    uploadButton.setText(getResources().getString(R.string.add_featured_image));
                }
            }

               /* String iconUrl = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_LogoUrl);

            if(iconUrl!=null && iconUrl.length()>0 && !iconUrl.contains("http")) {
                //String baseNameProfileImage = Constants.BASE_IMAGE_URL+"" + iconUrl;
                BoostLog.d("Logo Url:", iconUrl);
                Glide.with(this).asGif().load(iconUrl).apply(new RequestOptions().placeholder(R.drawable.logo_default_image)).into(logoimageView);
            }else{
                if(iconUrl!=null && iconUrl.length()>0) {
                    BoostLog.d("Logo Url:", iconUrl);
                    Glide.with(this).load(iconUrl).apply(new RequestOptions().placeholder(R.drawable.logo_default_image)).into(logoimageView);
                }else{
                    Glide.with(this).asGif().load(R.drawable.logo_default_image).into(logoimageView);
                }
            }*/
        } catch (Exception e) {
            e.printStackTrace();
            SentryController.INSTANCE.captureException(e);
            System.gc();
        }
        /*logoimageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(FeaturedImageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                        PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions(FeaturedImageActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            gallery_req_id);
                    return;
                }
               if (TextUtils.isEmpty(path)){
                   new DownloadImage().execute();
               }else{
                   editImage();
               }
            }
        });*/
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE).equals("-1")) {
//                    Methods.showFeatureNotAvailDialog(FeaturedImageActivity.this);
//                    return;
//                }
//                final MaterialDialog dialog = new MaterialDialog.Builder(FeaturedImageActivity.this)
//                        .customView(R.layout.featuredimage_popup,true)
//                        .show();
//
//                View view = dialog.getCustomView();
//                TextView title = (TextView) view.findViewById(R.id.textview_heading);
//                title.setText(getResources().getString(R.string.upload_features_image));
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
                final ImagePickerBottomSheetDialog imagePickerBottomSheetDialog = new ImagePickerBottomSheetDialog(this::onClickImagePicker);
                imagePickerBottomSheetDialog.show(getSupportFragmentManager(), ImagePickerBottomSheetDialog.class.getName());
            }

            private void onClickImagePicker(ImagePickerBottomSheetDialog.IMAGE_CLICK_TYPE image_click_type) {
                if (image_click_type.name().equals(ImagePickerBottomSheetDialog.IMAGE_CLICK_TYPE.CAMERA.name())) {
                    MixPanelController.track(EventKeysWL.UPDATE_LOGO_CAMERA, null);
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
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(FeaturedImageActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment
                            .getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, PICK_FROM_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    //intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
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
            SentryController.INSTANCE.captureException(anfe);
            // display an error message
            String errorMessage = getResources().getString(R.string.device_does_not_support_capturing_image);
            Methods.showSnackBarNegative(FeaturedImageActivity.this, errorMessage);
        } catch (Exception e) {
            e.printStackTrace();
            SentryController.INSTANCE.captureException(e);
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
            SentryController.INSTANCE.captureException(anfe);
            // display an error message
            String errorMessage = getResources().getString(R.string.device_does_not_support_capturing_image);
            Methods.showSnackBarNegative(FeaturedImageActivity.this, errorMessage);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK && (CAMERA_PHOTO == requestCode)) {
                Log.i(TAG, "onActivityResult: photo result recieved");
                try {
                    CameraBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    imageUrl = Methods.getRealPathFromURI(this, imageUri);
                    path = imageUrl;
                    path = Util.saveBitmap(path, FeaturedImageActivity.this, "ImageFloat" + System.currentTimeMillis());
                    WebEngageController.trackEvent(UPLOAD_FEATURED_IMAGE, UPDATED_FEATURED_IMAGE, session.getFpTag());
                } catch (Exception e) {
                    SentryController.INSTANCE.captureException(e);
                    e.printStackTrace();
                    //  Util.toast("Uh oh. Something went wrong. Please try again", this);
                } catch (OutOfMemoryError E) {
                    //Log.d("ANDRO_ASYNC",String.format("catch Out Of Memory error"));
                    E.printStackTrace();
                    System.gc();
                    // Util.toast("Uh oh. Something went wrong. Please try again", this);
                }

                if (!TextUtils.isEmpty(path)) {
                    uploadPrimaryPicture(path);
                }

                /*if (!Util.isNullOrEmpty(path)) {
                    editImage();
                }  else
                    Methods.showSnackBarNegative(FeaturedImageActivity.this,getResources().getString(R.string.select_image_upload));*/
            } else if (resultCode == RESULT_OK && (GALLERY_PHOTO == requestCode)) {
                {
                    Uri picUri = data.getData();
                    if (picUri != null) {
                        path = Methods.getPath(this, picUri);
                        path = Util.saveBitmap(path, FeaturedImageActivity.this, "ImageFloat" + System.currentTimeMillis());
                        WebEngageController.trackEvent(UPLOAD_FEATURED_IMAGE, UPDATED_FEATURED_IMAGE, session.getFpTag());
                        /*if (!Util.isNullOrEmpty(path)) {
                            editImage();
                        } else
                            Methods.showSnackBarNegative(FeaturedImageActivity.this,getResources().getString(R.string.select_image_upload));*/

                        if (!TextUtils.isEmpty(path)) {
                            uploadPrimaryPicture(path);
                        }
                    }
                }
            } else if (resultCode == RESULT_OK && requestCode == ACTION_REQUEST_IMAGE_EDIT) {
                String path = data.getStringExtra("edit_image");
                if (!TextUtils.isEmpty(path)) {
                    this.path = path;
                    WebEngageController.trackEvent(FEATURED_IMAGE_ADDED, MANAGE_CONTENT, session.getFpTag());
                    uploadPrimaryPicture(path);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            SentryController.INSTANCE.captureException(e);
        }
    }

    private void editImage() {
        Intent in = new Intent(FeaturedImageActivity.this, EditImageActivity.class);
        in.putExtra("image", path);
        in.putExtra("isFixedAspectRatio", true);
        startActivityForResult(in, ACTION_REQUEST_IMAGE_EDIT);
    }

    public void uploadPrimaryPicture(String path) {
        uploadButton.setText(getResources().getString(R.string.change));
        new AlertArchive(Constants.alertInterface, "LOGO", session.getFPID());
        // Upload_Logo upload_logo = new Upload_Logo(FeaturedImageActivity.this,path,session.getFPID(), session);
        // upload_logo.execute();
//        Constants.isImgUploaded = false;
//        UploadPictureAsyncTask upa = new UploadPictureAsyncTask(Business_Logo_Activity.this, path, false,true,session.getFPID());
//        upa.execute();
        new uploadIMAGEURI(FeaturedImageActivity.this, path, session.getFPID(), this::imageUploaded).execute();
    }

    private void imageUploaded(boolean isSuccess) {
        if (isSuccess) onFeatureLogoAddedOrUpdated(true);
    }

    private class DownloadImage extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(FeaturedImageActivity.this);
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
                SentryController.INSTANCE.captureException(e);
            }
            path = Util.saveCameraBitmap(bitmap, FeaturedImageActivity.this, "ImageFloat" + System.currentTimeMillis());
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
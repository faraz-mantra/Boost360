package com.nowfloats.BusinessProfile.UI.UI;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.BusinessProfile.UI.API.UploadFaviconImage;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.RoundCorners_image;
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
import com.thinksity.databinding.ActivityFaviconImageBinding;

import java.util.UUID;

public class FaviconImageActivity extends AppCompatActivity implements UploadFaviconImage.OnImageUpload
{
    Button uploadButton ;

    private static final int ACTION_REQUEST_IMAGE_EDIT = 3;

    ContentValues values;
    Uri imageUri ;

    private static final int GALLERY_PHOTO = 2;
    private static final int CAMERA_PHOTO = 1;

    Bitmap CameraBitmap;
    String path = null;
    String imageUrl ="";
    private ImageView logoimageView;
    PorterDuffColorFilter whiteLabelFilter;
    UserSessionManager session;

    private final int gallery_req_id = 0;
    private final int media_req_id = 1;
    private ProgressDialog dialog;

    ActivityFaviconImageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_favicon_image);

        setSupportActionBar(binding.appBar.toolbar);
        Methods.isOnline(FaviconImageActivity.this);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            getSupportActionBar().setTitle("");
        }

        binding.appBar.toolbarTitle.setText(getResources().getString(R.string.favicon_image));

        session = new UserSessionManager(getApplicationContext(), FaviconImageActivity.this);

        logoimageView = findViewById(R.id.logoimageView);
        uploadButton = findViewById(R.id.addLogoButton);

        try
        {
            String iconUrl = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_FAVICON_IMAGE_URI);

            if (!TextUtils.isEmpty(iconUrl))
            {
                Picasso.with(FaviconImageActivity.this).load(iconUrl).placeholder(R.drawable.logo_default_image).into(logoimageView);
            }

            else
            {
                Picasso.with(FaviconImageActivity.this).load(R.drawable.logo_default_image).into(logoimageView);
            }
        }

        catch(Exception e)
        {
            e.printStackTrace();
            System.gc();
        }

        uploadButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE).equals("-1")) {
                    Methods.showFeatureNotAvailDialog(FaviconImageActivity.this);
                    return;
                }
                final MaterialDialog dialog = new MaterialDialog.Builder(FaviconImageActivity.this)
                        .customView(R.layout.featuredimage_popup,true)
                        .show();

                View view = dialog.getCustomView();
                TextView title = (TextView) view.findViewById(R.id.textview_heading);
                title.setText(getResources().getString(R.string.upload_favicon_image));
                LinearLayout takeCamera = (LinearLayout) view.findViewById(R.id.cameraimage);
                LinearLayout takeGallery = (LinearLayout) view.findViewById(R.id.galleryimage);
                ImageView   cameraImg = (ImageView) view.findViewById(R.id.pop_up_camera_imag);
                ImageView galleryImg = (ImageView) view.findViewById(R.id.pop_up_gallery_img);
                cameraImg.setColorFilter(whiteLabelFilter);
                galleryImg.setColorFilter(whiteLabelFilter);

                takeCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        MixPanelController.track(EventKeysWL.UPDATE_LOGO_CAMERA,null);
                        WebEngageController.trackEvent("UPLOAD FAVICON IMAGE","UPLOAD FAVICON IMAGE",null);
                        cameraIntent();
                        dialog.dismiss();
                    }
                });

                takeGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MixPanelController.track(EventKeysWL.UPDATE_LOGO_GALLERY,null);
                        WebEngageController.trackEvent("UPLOAD FAVICON IMAGE","UPLOAD FAVICON IMAGE",null);
                        galleryIntent();
                        dialog.dismiss();

                    }
                });

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        if(requestCode == media_req_id)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
            {
                cameraIntent();
            }

        }

        else if(requestCode == gallery_req_id)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                galleryIntent();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_business__logo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        return super.onOptionsItemSelected(item);
    }

    public void cameraIntent()
    {
        try
        {
            // use standard intent to capture an image
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                    PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!=
                    PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, media_req_id);
                return;
            }

            values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
            imageUri =getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            // we will handle the returned data in onActivityResult
            startActivityForResult(captureIntent, CAMERA_PHOTO);
        }

        catch (ActivityNotFoundException anfe)
        {
            String errorMessage = getResources().getString(R.string.device_does_not_support_capturing_image);
            Methods.showSnackBarNegative(FaviconImageActivity.this,errorMessage);
        }

        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void galleryIntent()
    {
        try
        {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED ) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, gallery_req_id);
                return;
            }

            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(i, GALLERY_PHOTO);
        }

        catch (ActivityNotFoundException anfe)
        {
            String errorMessage = getResources().getString(R.string.device_does_not_support_capturing_image);
            Methods.showSnackBarNegative(FaviconImageActivity.this,errorMessage);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        try
        {
            if (resultCode == RESULT_OK && (CAMERA_PHOTO == requestCode))
            {
                try
                {
                    CameraBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    imageUrl = Methods.getRealPathFromURI(this, imageUri);
                    path = imageUrl;
                    path = Util.saveBitmap(path, FaviconImageActivity.this, "ImageFloat" + System.currentTimeMillis());
                }

                catch (Exception e)
                {
                    e.printStackTrace();
                }

                catch (OutOfMemoryError E)
                {
                    E.printStackTrace();
                    System.gc();
                }

                if (!TextUtils.isEmpty(path))
                {
                    uploadPrimaryPicture(path);
                }
            }

            else if (resultCode == RESULT_OK && (GALLERY_PHOTO == requestCode))
            {
                Uri picUri = data.getData();

                if (picUri != null)
                {
                    path = Methods.getPath(this, picUri);
                    path = Util.saveBitmap(path, FaviconImageActivity.this, "ImageFloat" + System.currentTimeMillis());

                    if (!TextUtils.isEmpty(path))
                    {
                        uploadPrimaryPicture(path);
                    }
                }
            }

            else if(resultCode == RESULT_OK && requestCode == ACTION_REQUEST_IMAGE_EDIT)
            {
                String path = data.getStringExtra("edit_image");

                if (!TextUtils.isEmpty(path))
                {
                    this.path = path;
                    uploadPrimaryPicture(path);
                }
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void uploadPrimaryPicture(String path)
    {
        new AlertArchive(Constants.alertInterface,"LOGO",session.getFPID());

        String s_uuid = UUID.randomUUID().toString();
        s_uuid = s_uuid.replace("-", "");
        String uri;
        String param = "createFaviconImage";
        uri = Constants.LoadStoreURI +
                param + "?clientId=" +
                Constants.clientId +
                "&fpId=" + session.getFPID() +
                "&reqType=sequential&reqtId=" +
                s_uuid + "&";

        String url = uri + "totalChunks=1&currentChunkNumber=1" ;

        UploadFaviconImage upload = new UploadFaviconImage(path, url);
        upload.setUploadListener(this);
        upload.execute();
    }

    @Override
    public void onPreUpload()
    {
        dialog = ProgressDialog.show(this, "", "Uploading image...");
        dialog.setCancelable(false);
    }

    @Override
    public void onPostUpload(boolean isSuccess, String response)
    {
        if(isSuccess)
        {
            Methods.showSnackBarPositive(this, "Image updated successfully");

            if(!TextUtils.isEmpty(response))
            {
                session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_FAVICON_IMAGE_URI, response.replace("\\", "").replace("\"", ""));
            }

            try
            {
                Bitmap bmp = Util.getBitmap(path, this);
                bmp = RoundCorners_image.getRoundedCornerBitmap(bmp, 15);

                if(logoimageView != null)
                {
                    logoimageView.setImageBitmap(bmp);
                }
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        else
        {
            Methods.showSnackBarNegative(this, "Failed to Upload Image");
        }

        if(dialog != null && dialog.isShowing())
        {
            dialog.dismiss();
        }
    }
}
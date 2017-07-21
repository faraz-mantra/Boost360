package com.nowfloats.BusinessProfile.UI.UI;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.nowfloats.BusinessProfile.UI.API.Upload_Logo;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NotificationCenter.AlertArchive;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

import java.io.File;

public class Business_Logo_Activity extends AppCompatActivity {
    Button uploadButton ;
    ImageView backgroundImageView ;
    private Toolbar toolbar;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_GALLERY = 2;
    private TextView titleTextView;
    ContentValues values;
    Uri imageUri ;
    private static final int GALLERY_PHOTO = 2;
    private static final int CAMERA_PHOTO = 1;
    Bitmap CameraBitmap;
    String path = null;
    String imageUrl ="";
    public static ImageView logoimageView;
    PorterDuffColorFilter whiteLabelFilter;
    UserSessionManager session ;

    private final int gallery_req_id = 0;
    private final int media_req_id = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business__logo);
        Methods.isOnline(Business_Logo_Activity.this);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(getResources().getString(R.string.logo));

        titleTextView = (TextView) toolbar.findViewById(R.id.titleTextView);
        titleTextView.setText(getResources().getString(R.string.logo));
        session = new UserSessionManager(getApplicationContext(),Business_Logo_Activity.this);
        logoimageView = (ImageView) findViewById(R.id.logoimageView);
        uploadButton = (Button) findViewById(R.id.addLogoButton);
        backgroundImageView = (ImageView) findViewById(R.id.imageView);
        try {
                String iconUrl = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_LogoUrl);
                if(iconUrl.length()>0 && !iconUrl.contains("http")) {
                    //String baseNameProfileImage = Constants.BASE_IMAGE_URL+"" + iconUrl;
                    BoostLog.d("Logo Url:", iconUrl);
                    Glide.with(this).load(iconUrl).asGif().placeholder(R.drawable.logo_default_image).into(logoimageView);
                }else{
                    if(iconUrl!=null && iconUrl.length()>0) {
                        BoostLog.d("Logo Url:", iconUrl);
                        Glide.with(this).load(iconUrl).placeholder(R.drawable.logo_default_image).into(logoimageView);
                    }else{
                        Glide.with(this).load(R.drawable.logo_default_image).asGif().into(logoimageView);
                    }
                }
        }catch(Exception e){e.printStackTrace();System.gc();}

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final MaterialDialog dialog = new MaterialDialog.Builder(Business_Logo_Activity.this)
                        .customView(R.layout.featuredimage_popup,true)
                        .show();

                View view = dialog.getCustomView();
                TextView title = (TextView) view.findViewById(R.id.textview_heading);
                title.setText(getResources().getString(R.string.upload_logo_image));
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
                        cameraIntent();
                        dialog.dismiss();
                    }
                });

                takeGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MixPanelController.track(EventKeysWL.UPDATE_LOGO_GALLERY,null);
                        galleryIntent();
                        dialog.dismiss();

                    }
                });

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
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(Business_Logo_Activity.this);
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

        this.setTitle(getResources().getString(R.string.setting));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        if(requestCode==media_req_id)
        {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                cameraIntent();

            }

        }
        else if(requestCode==gallery_req_id)
        {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                galleryIntent();

            }

        }
    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        // Toast.makeText(Image_Gallery_MainActivity.this,"Camera : "+requestCode+" Data : "+data.getData(),Toast.LENGTH_SHORT).show();
//
//        if (requestCode == PICK_FROM_CAMERA) {
//            Uri extras = data.getData();
//            if (extras != null) {
//                //Bitmap photo = extras.getParcelable("data");
//                try {
//                    Bitmap photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), extras);
//                    // testImageView.setImageBitmap(photo);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                Toast.makeText(Business_Logo_Activity.this, "Camera", Toast.LENGTH_SHORT).show();
//                //imgview.setImageBitmap(photo);
//
//            }
//
//
//
//        }
//
//        if (requestCode == PICK_FROM_GALLERY) {
//
//            String filepath = getGalleryImagePath(data);
//
//            Upload_Logo upload = new Upload_Logo(Business_Logo_Activity.this,filepath);
//            upload.execute();
//            // sent_check if the specified image exists.
//        }
//
//
//    }

    public String getGalleryImagePath(Intent data) {
        Uri imgUri = data.getData();
        String filePath = "";
        if (data.getType() == null) {
            // For getting images from default gallery app.
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(imgUri, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            cursor.close();
        } else if (data.getType().equals("image/jpeg") || data.getType().equals("image/png")) {
            // For getting images from dropbox or any other gallery apps.
            filePath = imgUri.getPath();
        }
        return filePath;
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




    public void cameraIntent(){
        try {
            // use standard intent to capture an image
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                    PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        media_req_id);
            }
            values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
            imageUri =getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent captureIntent = new Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            // we will handle the returned data in onActivityResult
            startActivityForResult(captureIntent, CAMERA_PHOTO);
        } catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = getResources().getString(R.string.device_does_not_support_capturing_image);
            Methods.showSnackBarNegative(Business_Logo_Activity.this,errorMessage);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void galleryIntent(){
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                    PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        gallery_req_id);
            }
            Intent i = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(i, GALLERY_PHOTO);
        } catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = getResources().getString(R.string.device_does_not_support_capturing_image);
            Methods.showSnackBarNegative(Business_Logo_Activity.this,errorMessage);
        }
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (resultCode == RESULT_OK && (CAMERA_PHOTO == requestCode)) {

                try {
                    CameraBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    imageUrl = getRealPathFromURI(imageUri);
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
                    uploadPrimaryPicture(path);
                }  else
                    Methods.showSnackBarNegative(Business_Logo_Activity.this,getResources().getString(R.string.select_image_upload));
            }
            else if (resultCode == RESULT_OK && (GALLERY_PHOTO == requestCode)) {
                {
                    Uri picUri = data.getData();
                    if (picUri != null) {
                        path = getPath(picUri);
                        path = Util.saveBitmap(path, Business_Logo_Activity.this, "ImageFloat" + System.currentTimeMillis());
                        if (!Util.isNullOrEmpty(path)) {
                            uploadPrimaryPicture(path);
                        } else
                            Methods.showSnackBarNegative(Business_Logo_Activity.this,getResources().getString(R.string.select_image_upload));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public String getPath(Uri uri) {
        try {
            String[] projection = { MediaStore.Images.Media.DATA };
            Cursor cursor = managedQuery(uri, projection, null, null, null);
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);

        } catch (Exception e) {

        }
        return null;
    }

    public void uploadPrimaryPicture(String path) {
        new AlertArchive(Constants.alertInterface,"LOGO",session.getFPID());
        Upload_Logo upload_logo = new Upload_Logo(Business_Logo_Activity.this,path,session.getFPID(), session);
        upload_logo.execute();
//        Constants.isImgUploaded = false;
//        UploadPictureAsyncTask upa = new UploadPictureAsyncTask(Business_Logo_Activity.this, path, false,true,session.getFPID());
//        upa.execute();
    }
}
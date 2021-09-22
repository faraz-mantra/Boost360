package com.nowfloats.NavigationDrawer;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.text.InputType;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.OfferImageUploadService;
import com.nowfloats.NavigationDrawer.API.OffersApiService;
import com.nowfloats.NavigationDrawer.model.PostOfferEvent;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.thinksity.R;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

public class CreateOffersActivity extends AppCompatActivity implements OfferImageUploadService.OfferImageUploadComplete {

    private final int gallery_req_id = 6;
    private final int media_req_id = 5;
    EditText etTitle, etDescription, etStartDate, etEndDate, etDiscount, etUri;
    ImageView ivOfferImage;
    Toolbar toolbar;
    private Bus mBus;
    private String mImageUri;
    private UserSessionManager mSession;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_offers);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        TextView tiTleText = (TextView) toolbar.findViewById(R.id.titleTextView);
        TextView postOffer = (TextView) toolbar.findViewById(R.id.saveTextView);

        tiTleText.setText(getString(R.string.createOffersTitle));
        postOffer.setText(getString(R.string.post_in_capital));

        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setDisplayShowHomeEnabled(false);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etTitle = (EditText) findViewById(R.id.et_offer_title);
        etDescription = (EditText) findViewById(R.id.et_offer_desc);
        etStartDate = (EditText) findViewById(R.id.et_offer_start_date);
        etEndDate = (EditText) findViewById(R.id.et_offer_end_date);
        etDiscount = (EditText) findViewById(R.id.et_offer_discount);
        etUri = (EditText) findViewById(R.id.et_offer_uri);

        etStartDate.setInputType(InputType.TYPE_NULL);
        etStartDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    pickDate(etStartDate);
                }
                return true;
            }
        });
        etEndDate.setInputType(InputType.TYPE_NULL);
        etEndDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    pickDate(etEndDate);
                }
                return true;
            }
        });

        ivOfferImage = (ImageView) findViewById(R.id.iv_offers);

        mSession = new UserSessionManager(getApplicationContext(), this);

        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.please_wait));
        pd.setCancelable(false);

        mBus = BusProvider.getInstance().getBus();

        ivOfferImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        postOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiatePostingOffer();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mBus.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBus.unregister(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == media_req_id) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] ==
                    PackageManager.PERMISSION_GRANTED) {
                cameraIntent();

            }

        } else if (requestCode == gallery_req_id) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] ==
                    PackageManager.PERMISSION_GRANTED) {
                galleryIntent();

            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && (Constants.GALLERY_PHOTO == requestCode)) {
            if (data != null) {
                Uri picUri = data.getData();
                if (picUri == null) {
                    Bitmap CameraBitmap = (Bitmap) data.getExtras().get("data");
                    mImageUri = Util.saveBitmap(CameraBitmap, this, mSession.getFpTag()
                            + System.currentTimeMillis());
                    ivOfferImage.setImageBitmap(CameraBitmap);
                } else {
                    mImageUri = getRealPathFromURI(picUri);
                    Bitmap CameraBitmap = Util.getBitmap(mImageUri, this);
                    ivOfferImage.setImageBitmap(CameraBitmap);
                }
            }
        } else if (resultCode == RESULT_OK && (Constants.CAMERA_PHOTO == requestCode)) {
            try {
                Uri picUri = null;
                if (picUri == null) {
                    if (data != null) {
                        picUri = data.getData();
                        if (picUri == null) {
                            Bitmap CameraBitmap = (Bitmap) data.getExtras().get("data");
                            mImageUri = Util.saveCameraBitmap(CameraBitmap, this, mSession.getFpTag()
                                    + System.currentTimeMillis());
                            ivOfferImage.setImageBitmap(CameraBitmap);
                        } else {
                            mImageUri = getRealPathFromURI(picUri);
                            Bitmap CameraBitmap = Util.getBitmap(mImageUri, this);
                            ivOfferImage.setImageBitmap(CameraBitmap);
                        }
                    } else {
                        Methods.showSnackBar(this, getString(R.string.try_again));
                    }
                } else {
                    mImageUri = getRealPathFromURI(picUri);
                    Bitmap CameraBitmap = Util.getBitmap(mImageUri, this);
                    ivOfferImage.setImageBitmap(CameraBitmap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } catch (OutOfMemoryError E) {
                E.printStackTrace();
                System.gc();
                Methods.showSnackBar(this, getString(R.string.try_again));
            }
        }
    }

    @Override
    public void onUploadCompletion(String response) {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
        if (response.equals("true")) {
            Methods.showSnackBarPositive(this, getString(R.string.suucessfull_offer_post));
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        } else {
            Methods.showSnackBarPositive(this, getString(R.string.error_offer_post));
        }
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    private void initiatePostingOffer() {
        String title = etTitle.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();
        String startDate = etStartDate.getText().toString().trim();
        String endDate = etEndDate.getText().toString().trim();
        String discount = etDiscount.getText().toString().trim();
        if (isOfferValidated(title, desc, startDate, endDate, discount, mImageUri)) {
            pd.show();
            HashMap<String, String> data = new HashMap<>();
            data.put("Description", desc);
            data.put("DiscountPercent", discount);
            data.put("EndDate", endDate);
            data.put("MerchantContact", mSession.getFPDetails(Key_Preferences.MAIN_PRIMARY_CONTACT_NUM));
            data.put("MerchantId", mSession.getFPID());
            data.put("MerchantName", mSession.getFPName());
            data.put("MerchantTag", mSession.getFpTag());
            data.put("StartDate", startDate);
            data.put("Title", title);
            data.put("Uri", etUri.getText().toString().trim());
            data.put("clientId", Constants.clientId);
            new OffersApiService(mBus).postOffers(data);

        }
    }

    @Subscribe
    public void postOfferSuccess(PostOfferEvent e) {
        if (e.success) {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            pd.setMessage(getString(R.string.offer_image_upload_dialog_text));
            pd.show();
            String imageUploadUrl = Constants.PictureFloatImgCreationURI + "?clientId=" +
                    Constants.clientId + "&bizMessageId=" + e.result.replace("\"", "") +
                    "&requestType=sequential&requestId=" + UUID.randomUUID().toString() +
                    "&sendToSubscribers=false&" + "totalChunks=1&currentChunkNumber=1";
            new OfferImageUploadService(this).execute(mImageUri, imageUploadUrl);
        } else {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            BoostLog.d("Offers Error Reason", e.result);
            Methods.showSnackBarNegative(this, getString(R.string.offer_upload_failure));
        }
    }

    /*
     * @Params title, desc, startDate, endDate, discount, imageUri
     * @return
     */
    private boolean isOfferValidated(String title, String desc, String startDate, String endDate,
                                     String discount, String imageUri) {
        if (Util.isNullOrEmpty(title)) {
            Methods.showSnackBarNegative(this, getString(R.string.offers_title_error));
            return false;
        } else if (Util.isNullOrEmpty(desc)) {
            Methods.showSnackBarNegative(this, getString(R.string.offers_desc_error));
            return false;
        } else if (Util.isNullOrEmpty(startDate)) {
            Methods.showSnackBarNegative(this, getString(R.string.offers_start_date_error));
            return false;
        } else if (Util.isNullOrEmpty(endDate)) {
            Methods.showSnackBarNegative(this, getString(R.string.offers_end_date_error));
            return false;
        } else if (Util.isNullOrEmpty(imageUri)) {
            Methods.showSnackBarNegative(this, getString(R.string.offers_image_error));
            return false;
        }

        File file = new File(imageUri);
        long length = file.length() / 1024;
        if (length > 4000) {
            Methods.showSnackBarNegative(this, getString(R.string.offers_image_size_error));
            return false;
        }

        return true;

    }

    private void chooseImage() {
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .customView(R.layout.featuredimage_popup, true)
                .show();
        final PorterDuffColorFilter whiteLabelFilter_pop_ip =
                new PorterDuffColorFilter(getResources().getColor(R.color.primaryColor),
                        PorterDuff.Mode.SRC_IN);

        View view = dialog.getCustomView();
        TextView header = (TextView) view.findViewById(R.id.textview_heading);
        header.setText(getString(R.string.upload_photo));
        LinearLayout takeCamera = (LinearLayout) view.findViewById(R.id.cameraimage);
        LinearLayout takeGallery = (LinearLayout) view.findViewById(R.id.galleryimage);
        ImageView cameraImg = (ImageView) view.findViewById(R.id.pop_up_camera_imag);
        ImageView galleryImg = (ImageView) view.findViewById(R.id.pop_up_gallery_img);
        cameraImg.setColorFilter(whiteLabelFilter_pop_ip);
        galleryImg.setColorFilter(whiteLabelFilter_pop_ip);

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

    public void galleryIntent() {
        try {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                            PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA},
                        gallery_req_id);
            } else {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, Constants.GALLERY_PHOTO);
            }
        } catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = getString(R.string.device_does_not_support_capturing_image);
            Methods.showSnackBarNegative(this, errorMessage);
        }
    }

    public void cameraIntent() {
        try {
            // use standard intent to capture an image
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                            PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA},
                        media_req_id);
            } else {
                ContentValues cValues = new ContentValues();
                Intent captureIntent;
                cValues.put(MediaStore.Images.Media.TITLE, getString(R.string.new_picture));
                cValues.put(MediaStore.Images.Media.DESCRIPTION, getString(R.string.from_your_camera));
                Uri picUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        cValues);
                captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
                startActivityForResult(captureIntent, Constants.CAMERA_PHOTO);
            }
        } catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = getString(R.string.device_does_not_support_capturing_image);
            Methods.showSnackBarNegative(this, errorMessage);
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = managedQuery(contentUri, proj, null, null, null);
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
        }
        return null;
    }

    public void pickDate(final EditText et) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                et.setText(monthOfYear + "-" + dayOfMonth + "-" + year);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

}

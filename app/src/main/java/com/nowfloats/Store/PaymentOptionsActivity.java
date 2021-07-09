package com.nowfloats.Store;

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
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.NavigationDrawer.EditImageActivity;
import com.nowfloats.Store.Model.SalesmanModel;
import com.nowfloats.Store.Service.OnPaymentOptionClick;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.thinksity.R;


/**
 * Created by Admin on 12-04-2018.
 */

public class PaymentOptionsActivity extends AppCompatActivity implements OnPaymentOptionClick {

    private final static int media_req_id = 111, gallery_req_id = 112, ACTION_REQUEST_IMAGE_EDIT = 113;
    private String path;
    private int requestCode;
    private Uri imageUri;
    private PaymentType requestedFragmentType = null;
    private ProgressDialog progressDialog;

    @Override
    public void onOptionClicked(PaymentType type) {
        showPaymentOptionScreen(type, new Bundle());
    }

    @Override
    public void onPickImage(PaymentType type, int requestCode) {
        requestedFragmentType = type;
        this.requestCode = requestCode;
        choosePicture();
    }

    @Override
    public void showProcess(String message) {
        showLoader(message);
    }

    @Override
    public void hideProcess() {
        hideLoader();
    }

    @Override
    public void setResult(Intent intent) {
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }


    public enum PaymentType {
        PAYMENT_OPTIONS, CHEQUE, OPC, BANK_TRANSFER;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_options);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        showPaymentOptionScreen(PaymentType.PAYMENT_OPTIONS, null);
    }

    public void choosePicture() {
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .customView(R.layout.featuredimage_popup, true)
                .show();
        final PorterDuffColorFilter whiteLabelFilter_pop_ip = new PorterDuffColorFilter(ContextCompat.getColor(this, R.color.primaryColor), PorterDuff.Mode.SRC_IN);

        View view = dialog.getCustomView();
        TextView header = (TextView) view.findViewById(R.id.textview_heading);
        LinearLayout takeCamera = (LinearLayout) view.findViewById(R.id.cameraimage);
        LinearLayout takeGallery = (LinearLayout) view.findViewById(R.id.galleryimage);
        ImageView cameraImg = (ImageView) view.findViewById(R.id.pop_up_camera_imag);
        ImageView galleryImg = (ImageView) view.findViewById(R.id.pop_up_gallery_img);
        header.setText("Upload Image");
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == media_req_id) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                cameraIntent();

            } else {
                Toast.makeText(this, getString(R.string.please_give_storage_and_camera_permission), Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == gallery_req_id) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                galleryIntent();

            } else {
                Toast.makeText(this,getString( R.string.please_give_read_storage_permission), Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (resultCode == RESULT_OK && (Constants.CAMERA_PHOTO == requestCode)) {

                try {
                    path = Methods.getRealPathFromURI(this, imageUri);
                    path = Util.saveBitmap(path, this, "ImageFloat" + System.currentTimeMillis());
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
                    Methods.showSnackBarNegative(this, getResources().getString(R.string.select_image_upload));
            } else if (resultCode == RESULT_OK && (Constants.GALLERY_PHOTO == requestCode)) {
                {
                    Uri picUri = data.getData();
                    if (picUri != null) {
                        path = Methods.getPath(this, picUri);
                        path = Util.saveBitmap(path, this, "ImageFloat" + System.currentTimeMillis());
                        if (!Util.isNullOrEmpty(path)) {
                            editImage();
                        } else
                            Methods.showSnackBarNegative(this, getResources().getString(R.string.select_image_upload));
                    }
                }
            } else if (resultCode == RESULT_OK && requestCode == ACTION_REQUEST_IMAGE_EDIT) {
                String path = data.getStringExtra("edit_image");
                if (!TextUtils.isEmpty(path)) {
                    this.path = path;
                    sendPathToFragment();
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showLoader(final String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    private void hideLoader() {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void sendPathToFragment() {
        OnImagePicked frag = (OnImagePicked) getSupportFragmentManager().findFragmentByTag(requestedFragmentType.name());
        if (frag == null) return;
        Bundle b = new Bundle();
        b.putString("path", path);
        b.putInt("requestCode", requestCode);
        frag.onShowPicked(b);
    }

    private void editImage() {
        Intent in = new Intent(this, EditImageActivity.class);
        in.putExtra("image", path);
        in.putExtra("isFixedAspectRatio", false);
        startActivityForResult(in, ACTION_REQUEST_IMAGE_EDIT);
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
            startActivityForResult(captureIntent, Constants.CAMERA_PHOTO);
        } catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = getResources().getString(R.string.device_does_not_support_capturing_image);
            Methods.showSnackBarNegative(this, errorMessage);
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

            startActivityForResult(i, Constants.GALLERY_PHOTO);
        } catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = getResources().getString(R.string.device_does_not_support_capturing_image);
            Methods.showSnackBarNegative(this, errorMessage);
        }
    }

    private void showPaymentOptionScreen(PaymentType type, Bundle b) {
        if (b != null) {
            b.putString("packageList", getIntent().getStringExtra("packageList"));
            b.putSerializable("discountCoupon", (DiscountCoupon) getIntent().getExtras().get("discountCoupon"));
            b.putInt("tdsPercentage", (int) getIntent().getExtras().get("tdsPercentage"));
            b.putSerializable("salesmanModel", (SalesmanModel) getIntent().getExtras().get("salesmanModel"));
//            b.putString(com.romeo.mylibrary.Constants.PARCEL_IDENTIFIER, getIntent().getStringExtra(com.romeo.mylibrary.Constants.PARCEL_IDENTIFIER));
        }
        FragmentManager manager = getSupportFragmentManager();
        Fragment frag = manager.findFragmentByTag(type.name());
        switch (type) {
            case OPC:

                if (frag == null) {

                    frag = OpcPaymentFragment.getInstance(b);
                }
                manager.beginTransaction().replace(R.id.fragment_layout, frag, type.name())
                        .addToBackStack(null)
                        .commit();
                break;
            case CHEQUE:

                if (frag == null) {
                    frag = ChequePaymentFragment.getInstance(b);
                }
                manager.beginTransaction().replace(R.id.fragment_layout, frag, type.name())
                        .addToBackStack(null)
                        .commit();
                break;
            case BANK_TRANSFER:

                if (frag == null) {
                    frag = BankTransferFragment.getInstance(b);
                }
                manager.beginTransaction().replace(R.id.fragment_layout, frag, type.name())
                        .addToBackStack(null)
                        .commit();
                break;
            case PAYMENT_OPTIONS:

                if (frag == null) {
                    frag = PaymentOptionsListFragment.getInstance(b);
                }
                manager.beginTransaction().replace(R.id.fragment_layout, frag, type.name())
                        .commit();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

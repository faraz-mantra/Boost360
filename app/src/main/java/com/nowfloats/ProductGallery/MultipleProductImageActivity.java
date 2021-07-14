package com.nowfloats.ProductGallery;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.ProductGallery.Adapter.ProductImageListAdapter;
import com.nowfloats.ProductGallery.Model.ProductImageRequestModel;
import com.nowfloats.ProductGallery.Model.ProductImageResponseModel;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.nowfloats.webactions.WebAction;
import com.nowfloats.webactions.WebActionsFilter;
import com.nowfloats.webactions.models.ProductImage;
import com.nowfloats.webactions.models.WebActionError;
import com.nowfloats.webactions.webactioninterfaces.IFilter;
import com.thinksity.R;

import java.util.List;
import java.util.UUID;

import static android.view.Window.FEATURE_NO_TITLE;

public class MultipleProductImageActivity extends AppCompatActivity {

    private final int galleryReqId = 6;
    private final int mediaReqId = 5;
    FloatingActionButton fabDeleteImage, fabAddImage;
    ViewPager vpMultipleImages;
    List<ProductImageResponseModel> lsProductImages;
    private String mProductId;
    private WebAction mWebAction;
    private ProductImageListAdapter mAdapter;
    private Uri picUri;
    private String path;
    private Bitmap CameraBitmap;
    private boolean mIsImagePicking = false;
    private UserSessionManager mSession;
    private ProgressDialog progressDialog;
    private TextView tvCurrentCountKeeper, tvAddImages;
    private ImageView ivNavLeft, ivNavRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(FEATURE_NO_TITLE);

        setContentView(R.layout.activity_multiple_product_image);


        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mProductId = getIntent().getExtras().getString("product_id");
            lsProductImages = (List<ProductImageResponseModel>) getIntent().getExtras().get("cacheImages");
        }

        fabDeleteImage = findViewById(R.id.fab_delete_image);
        fabAddImage = findViewById(R.id.fab_add_image);
        vpMultipleImages = findViewById(R.id.vp_product_images);
        tvAddImages = findViewById(R.id.tv_add_images);

        tvCurrentCountKeeper = findViewById(R.id.tv_current_view_count);
        ivNavLeft = findViewById(R.id.iv_nav_left);
        ivNavRight = findViewById(R.id.iv_nav_right);

        tvCurrentCountKeeper.setText("0 of 0");

        mSession = new UserSessionManager(this, this);

        mAdapter = new ProductImageListAdapter(this);
        vpMultipleImages.setAdapter(mAdapter);

        mWebAction = new WebAction.WebActionBuilder()
                .setAuthHeader("58ede4d4ee786c1604f6c535")
                .build();

        mWebAction.setWebActionName("product_images");

        fabAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickerDialog();
            }
        });

        fabDeleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (progressDialog != null)
                    progressDialog.show();
                final int position = vpMultipleImages.getCurrentItem();
                ProductImageResponseModel image = mAdapter.get(position);
                IFilter filter = new WebActionsFilter();
                filter = filter.eq("_id", image.getId());
                mWebAction.delete(filter, false, new WebAction.WebActionCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        if (result) {
                            mAdapter.removeImage(position);
                            vpMultipleImages.setAdapter(null);
                            vpMultipleImages.setAdapter(mAdapter);
                            tvCurrentCountKeeper.setText(String.format("%d of %d", vpMultipleImages.getCurrentItem() + 1, mAdapter.getCount()));
                        }
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        if (mAdapter.getCount() == 0) {
                            tvAddImages.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(WebActionError error) {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        });

        vpMultipleImages.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                tvCurrentCountKeeper.setText(String.format("%d of %d", position + 1, mAdapter.getCount()));
                if (position == 0) {
                    ivNavLeft.setVisibility(View.INVISIBLE);
                } else {
                    ivNavLeft.setVisibility(View.VISIBLE);
                }

                if (position == mAdapter.getCount() - 1) {
                    ivNavRight.setVisibility(View.INVISIBLE);
                } else {
                    ivNavRight.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        ivNavRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vpMultipleImages.getCurrentItem() + 1 < mAdapter.getCount()) {
                    vpMultipleImages.setCurrentItem(vpMultipleImages.getCurrentItem() + 1, true);
                }
            }
        });

        ivNavLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vpMultipleImages.getCurrentItem() > 0) {
                    vpMultipleImages.setCurrentItem(vpMultipleImages.getCurrentItem() - 1, true);
                }
            }
        });

        displayImagesForProduct();
    }

    private void showImagePickerDialog() {
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .customView(R.layout.featuredimage_popup, true)
                .show();
        final PorterDuffColorFilter whiteLabelFilter_pop_ip = new PorterDuffColorFilter(getResources().getColor(R.color.primaryColor), PorterDuff.Mode.SRC_IN);

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
            if (ActivityCompat.checkSelfPermission(MultipleProductImageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Methods.showApplicationPermissions(getString(R.string.storage_permission), getString(R.string.we_need_this_permission_to_enable_image_upload), MultipleProductImageActivity.this);
                } else {
                    ActivityCompat.requestPermissions(MultipleProductImageActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, galleryReqId);
                }

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
            if (ActivityCompat.checkSelfPermission(MultipleProductImageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MultipleProductImageActivity.this, Manifest.permission.CAMERA) !=
                    PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                    Methods.showApplicationPermissions(getString(R.string.camera_and_storage_permission), getString(R.string.we_need_this_permission_to_enable_capture_and_upload_images), MultipleProductImageActivity.this);
                } else {
                    ActivityCompat.requestPermissions(MultipleProductImageActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, mediaReqId);
                }

            } else {
                mIsImagePicking = true;
                ContentValues Cvalues = new ContentValues();
                Intent captureIntent;
                Cvalues.put(MediaStore.Images.Media.TITLE, "New Picture");
                Cvalues.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                picUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Cvalues);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mIsImagePicking = false;
        if (resultCode == RESULT_OK && (Constants.GALLERY_PHOTO == requestCode)) {
            if (data != null) {
                picUri = data.getData();
                if (picUri == null) {
                    CameraBitmap = (Bitmap) data.getExtras().get("data");
                    path = Util.saveBitmap(CameraBitmap, this, "PRODUCT_IMAGE" + System.currentTimeMillis());
                    picUri = Uri.parse(path);
                } else {
                    path = getRealPathFromURI(picUri);
                }
            }
        } else if (resultCode == RESULT_OK && (Constants.CAMERA_PHOTO == requestCode)) {
            try {
                if (picUri == null) {
                    if (data != null) {
                        picUri = data.getData();
                        if (picUri == null) {
                            CameraBitmap = (Bitmap) data.getExtras().get("data");
                            path = Util.saveCameraBitmap(CameraBitmap, this, "PRODUCT_IMAGE" + System.currentTimeMillis());
                            picUri = Uri.parse(path);
                        } else {
                            path = getRealPathFromURI(picUri);
                        }
                    } else {
                        Methods.showSnackBar(this, getString(R.string.try_again));
                    }
                } else {
                    path = getRealPathFromURI(picUri);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } catch (OutOfMemoryError E) {
                E.printStackTrace();
                CameraBitmap.recycle();
                System.gc();
                Methods.showSnackBar(this, getString(R.string.try_again));
            }
        }

        if (!TextUtils.isEmpty(path) && resultCode == RESULT_OK) {
            if (TextUtils.isEmpty(mProductId)) {
                ProductImageResponseModel imageData = new ProductImageResponseModel();
                imageData.setId(UUID.randomUUID().toString());
                imageData.setPid(UUID.randomUUID().toString());
                imageData.setImage(new ProductImage(path, "Description"));
                mAdapter.addImage(imageData);
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                vpMultipleImages.setCurrentItem(mAdapter.getCount() - 1, true);
                tvAddImages.setVisibility(View.INVISIBLE);
            } else {

                progressDialog.show();
                mWebAction.uploadFile(path, new WebAction.WebActionCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        path = null;
                        addImageData(result);
                    }

                    @Override
                    public void onFailure(WebActionError error) {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                }, new Handler(Looper.getMainLooper()));
            }
        }
    }

    private void addImageData(final String result) {
        if (!TextUtils.isEmpty(result)) {
            final ProductImageRequestModel productImageRequestModel = new ProductImageRequestModel();
            productImageRequestModel._pid = mProductId;
            productImageRequestModel.image = new ProductImage(result, "Description");
            mWebAction.insert(mSession.getFpTag(), productImageRequestModel, new WebAction.WebActionCallback<String>() {
                @Override
                public void onSuccess(String id) {
                    ProductImageResponseModel imageData = new ProductImageResponseModel();
                    imageData.setId(id);
                    imageData.setPid(mProductId);
                    imageData.setImage(productImageRequestModel.image);
                    mAdapter.addImage(imageData);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    vpMultipleImages.setCurrentItem(mAdapter.getCount() - 1, true);
                    tvAddImages.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onFailure(WebActionError error) {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            });
        }
    }

    public String getRealPathFromURI(Uri contentUri) {

        String val = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            val = cursor.getString(column_index);
            cursor.close();
        }
        return val;
    }

    private void displayImagesForProduct() {
        progressDialog = ProgressDialog.show(this, "", getString(R.string.please_wait_));
        if (TextUtils.isEmpty(mProductId)) {
            refreshList(lsProductImages);
        } else {
            IFilter filter = new WebActionsFilter();
            filter = filter.eq("_pid", mProductId);
            mWebAction.findProductImages(filter, new WebAction.WebActionCallback<List<ProductImageResponseModel>>() {
                @Override
                public void onSuccess(List<ProductImageResponseModel> result) {
                    refreshList(result);
                }

                @Override
                public void onFailure(WebActionError error) {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(MultipleProductImageActivity.this, getString(R.string.something_went_wrong_), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void refreshList(List<ProductImageResponseModel> result) {
        mAdapter.addImages(result);
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        tvCurrentCountKeeper.setText(String.format("%d of %d", vpMultipleImages.getCurrentItem() + 1, mAdapter.getCount()));
        ivNavLeft.setVisibility(View.INVISIBLE);
        if (mAdapter.getCount() == 0) {
            tvAddImages.setVisibility(View.VISIBLE);
            tvCurrentCountKeeper.setText("0 of 0");
        }
        if (mAdapter.getCount() < 2) {
            ivNavLeft.setVisibility(View.INVISIBLE);
            ivNavRight.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("cacheImages", mAdapter.getImages());
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }
}

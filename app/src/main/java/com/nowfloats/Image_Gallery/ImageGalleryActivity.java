package com.nowfloats.Image_Gallery;

import android.annotation.SuppressLint;

import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dashboard.utils.ActivityUtilsKt;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;
import com.thinksity.databinding.ActivityImageGalleryBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class ImageGalleryActivity extends AppCompatActivity {

    private Image_Gallery_Fragment image_gallery_fragment;

    public static final String TAG_IMAGE = "TAG_IMAGE";

    ActivityImageGalleryBinding binding;
    private int noOfImages = 0;
    private int imageLimit = 0;
    ArrayList<String> purchasedWidgetList = new ArrayList<String>();
    private com.framework.pref.UserSessionManager session;
    private AppCompatActivity mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_image_gallery);
        MixPanelController.track(MixPanelController.IMAGE_GALLERY, null);
        mContext = this;

        setSupportActionBar(binding.appBar.toolbar);
        Methods.isOnline(ImageGalleryActivity.this);
        session = new com.framework.pref.UserSessionManager(getApplicationContext());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            getSupportActionBar().setTitle("");
        }

        binding.appBar.toolbarTitle.setText(getResources().getString(R.string.image_gallery));

        image_gallery_fragment = new Image_Gallery_Fragment();
        findViewById(R.id.fm_site_appearance).setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction().replace(R.id.fm_site_appearance, image_gallery_fragment, TAG_IMAGE).
                commit();

        if (savedInstanceState != null && image_gallery_fragment != null) {
            Boolean startCreateMode = savedInstanceState.getBoolean("create_image", false);
            if (startCreateMode) {
                image_gallery_fragment.addImage();
            }
        }
//        noOfImages =  image_gallery_fragment.getImageCount();
    }

    public void onResume() {
        super.onResume();
//        noOfImages =  image_gallery_fragment.getImageCount();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                finish();
                break;

            case R.id.menu_add:
                if (imageLimit == 0) {


                    if (noOfImages < 3) {
                        Log.d("imagesReceived", " 1 : " + noOfImages);
                        image_gallery_fragment.addImage();
                    } else {
                        new AlertDialog.Builder(this)
                                .setTitle("Limit Reached")
                                .setMessage("You have reached the limit of content updates. Please upgrade to next plan to grow your business.")

                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                // The dialog is automatically dismissed when a dialog button is clicked.
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Continue with delete operation
                                        ActivityUtilsKt.initiateAddonMarketplace(mContext,session,false,"comparePackageSelection","",false);

                                    }
                                })

                                // A null listener allows the button to dismiss the dialog and take no further action.
                                .setNegativeButton(android.R.string.no, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
//                        ActivityUtilsKt.initiateAddonMarketplace(this,session,false,"comparePackageSelection","",false);
                    }
                } else {
                    if (noOfImages < imageLimit) {

                        image_gallery_fragment.addImage();
                    } else {
                        new AlertDialog.Builder(this)
                                .setTitle("Limit Reached")
                                .setMessage("You have reached the limit of content updates. Please upgrade to next plan to grow your business.")

                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                // The dialog is automatically dismissed when a dialog button is clicked.
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Continue with delete operation
                                        ActivityUtilsKt.initiateAddonMarketplace(mContext,session,false,"comparePackageSelection","",false);

                                    }
                                })

                                // A null listener allows the button to dismiss the dialog and take no further action.
                                .setNegativeButton(android.R.string.no, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
//                        ActivityUtilsKt.initiateAddonMarketplace(session, false, "comparePackageSelection", "");
//                        ActivityUtilsKt.initiateAddonMarketplace(this,session,false,"comparePackageSelection","",false);
                    }

                }

//                image_gallery_fragment.addImage();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void imageCount(int count) {
        noOfImages = count;
        Log.d("imagesReceived", " 5 : " + count);
    }

    @Override
    public void imageLimit(int limit) {
        imageLimit = limit;
    }
}

package com.nowfloats.Image_Gallery;

import android.annotation.SuppressLint;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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
    ArrayList<String> purchasedWidgetList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_image_gallery);
        MixPanelController.track(MixPanelController.IMAGE_GALLERY, null);

        setSupportActionBar(binding.appBar.toolbar);
        Methods.isOnline(ImageGalleryActivity.this);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            getSupportActionBar().setTitle("");
        }

        binding.appBar.toolbarTitle.setText(getResources().getString(R.string.image_gallery));

        image_gallery_fragment = new Image_Gallery_Fragment();
        findViewById(R.id.fm_site_appearance).setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction().replace(R.id.fm_site_appearance, image_gallery_fragment, TAG_IMAGE).
                commit();

        if(savedInstanceState != null && image_gallery_fragment != null){
            Boolean startCreateMode = savedInstanceState.getBoolean("create_image", false);
            if(startCreateMode){
                image_gallery_fragment.addImage();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:

                finish();
                break;

            case R.id.menu_add:
                image_gallery_fragment.addImage();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}

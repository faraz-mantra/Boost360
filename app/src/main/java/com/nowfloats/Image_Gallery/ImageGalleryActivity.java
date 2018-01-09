package com.nowfloats.Image_Gallery;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.thinksity.R;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class ImageGalleryActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private TextView headerText;

    private ImageView ivDelete;

    private Image_Gallery_Fragment image_gallery_fragment;

    public static final String TAG_IMAGE = "TAG_IMAGE";

    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_appearance);

        toolbar = (Toolbar) findViewById(R.id.app_bar_site_appearance);
        ivDelete = (ImageView) findViewById(R.id.ivDelete);
        setSupportActionBar(toolbar);
        headerText = (TextView) toolbar.findViewById(R.id.titleTextView);
        hideActionDelete();

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        image_gallery_fragment = new Image_Gallery_Fragment();

        findViewById(R.id.fm_site_appearance).setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction().replace(R.id.fm_site_appearance, image_gallery_fragment, TAG_IMAGE).
                commit();

        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                MaterialDialog.Builder builder = new MaterialDialog.Builder(ImageGalleryActivity.this)
                        .content(getString(R.string.delete) + " " + count + (count > 1 ? " files" : " file") + "?")
                        .positiveText(getString(R.string.ok))
                        .negativeText(getString(R.string.cancel))
                        .positiveColorRes(R.color.primaryColor)
                        .negativeColorRes(R.color.primaryColor)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);
                                Image_Gallery_Fragment image_gallery_fragment =
                                        (Image_Gallery_Fragment) getSupportFragmentManager().findFragmentByTag(TAG_IMAGE);

                                if (image_gallery_fragment != null) {
                                    image_gallery_fragment.deleteSelectedImages();
                                }
                                hideActionDelete();
                            }
                        });

               builder.show();

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            if (ivDelete.getVisibility() == View.VISIBLE) {
                hideActionDelete();
                Image_Gallery_Fragment image_gallery_fragment =
                        (Image_Gallery_Fragment) getSupportFragmentManager().findFragmentByTag(TAG_IMAGE);

                if (image_gallery_fragment != null) {
                    image_gallery_fragment.clearSelectedImages();
                }
            } else {

                onBackPressed();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void showActionDelete(int count) {
        this.count = count;
        headerText.setText(count + " " + getResources().getString(R.string.selected));
        ivDelete.setVisibility(View.VISIBLE);
    }

    public void hideActionDelete() {
        count = 0;
        headerText.setText(getResources().getString(R.string.photo_gallery));
        ivDelete.setVisibility(View.GONE);
    }
}

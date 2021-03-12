package com.nowfloats.ProductGallery;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Key_Preferences;
import com.thinksity.R;

/**
 * Created by guru on 08-06-2015.
 */
public class ProductGalleryActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private TextView headerText;

    private Product_Gallery_Fragment product_gallery_fragment;

    private ImageView ivDelete;

    public static final String TAG_PRODUCT = "TAG_PRODUCT";

    private int count = 0;
    UserSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_appearance);

        toolbar = (Toolbar) findViewById(R.id.app_bar_site_appearance);
        ivDelete = (ImageView) findViewById(R.id.ivDelete);
        setSupportActionBar(toolbar);
        headerText = (TextView) toolbar.findViewById(R.id.titleTextView);
        session = new UserSessionManager(this, this);
        hideActionDelete();

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        product_gallery_fragment = new Product_Gallery_Fragment();

        findViewById(R.id.fm_site_appearance).setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction().add(R.id.fm_site_appearance, product_gallery_fragment, TAG_PRODUCT).
                commit();

        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MaterialDialog.Builder builder = new MaterialDialog.Builder(ProductGalleryActivity.this)
                        .content(getString(R.string.delete_) + " " + count + (count > 1 ? " products" : " product") + "?")
                        .positiveText(getString(R.string.ok))
                        .negativeText(getString(R.string.cancel))
                        .positiveColorRes(R.color.primaryColor)
                        .negativeColorRes(R.color.primaryColor)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);
                                Product_Gallery_Fragment product_gallery_fragment =
                                        (Product_Gallery_Fragment) getSupportFragmentManager().findFragmentByTag(TAG_PRODUCT);

                                if (product_gallery_fragment != null) {
                                    product_gallery_fragment.deleteSelectedProducts();
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
                Product_Gallery_Fragment product_gallery_fragment =
                        (Product_Gallery_Fragment) getSupportFragmentManager().findFragmentByTag(TAG_PRODUCT);

                if (product_gallery_fragment != null) {
                    product_gallery_fragment.clearSelectedImages();
                }
            } else {

                onBackPressed();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    public void showActionDelete(int count) {
        this.count = count;
        headerText.setText(count + " " + getResources().getString(R.string.selected));
        ivDelete.setVisibility(View.VISIBLE);
    }

    public void hideActionDelete() {

        count = 0;
//        headerText.setText(TextUtils.isEmpty(session.getFPDetails(Key_Preferences.PRODUCT_CATEGORY))?
//                getString(R.string.product_gallery):session.getFPDetails(Key_Preferences.PRODUCT_CATEGORY)+" Gallery");

        headerText.setText(TextUtils.isEmpty(session.getFPDetails(Key_Preferences.PRODUCT_CATEGORY))?
                getString(R.string.product_gallery):session.getFPDetails(Key_Preferences.PRODUCT_CATEGORY));

        ivDelete.setVisibility(View.GONE);
    }
}
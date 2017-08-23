package com.nowfloats.Product_Gallery;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.thinksity.R;

/**
 * Created by guru on 08-06-2015.
 */
public class ProductGalleryActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView headerText;
    private Product_Gallery_Fragment product_gallery_fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_appearance);

        toolbar = (Toolbar) findViewById(R.id.app_bar_site_appearance);
        setSupportActionBar(toolbar);
        headerText = (TextView) toolbar.findViewById(R.id.titleTextView);
        headerText.setText(getResources().getString(R.string.product_gallery));

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        product_gallery_fragment = new Product_Gallery_Fragment();

        findViewById(R.id.fm_site_appearance).setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction().add(R.id.fm_site_appearance, product_gallery_fragment).
                commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}
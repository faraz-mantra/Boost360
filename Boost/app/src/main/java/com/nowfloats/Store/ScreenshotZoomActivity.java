package com.nowfloats.Store;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.nowfloats.Store.Adapters.ViewPagerAdapter;
import com.nowfloats.Store.Model.Screenshots;
import com.nowfloats.Store.Model.StoreModel;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.util.ArrayList;

public class ScreenshotZoomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_zoomimage_page);
        if (getIntent().hasExtra("key")){
            int storePos = Integer.parseInt(getIntent().getExtras().getString("key"));
            final ViewPager productViewPager = (ViewPager) findViewById(R.id.product_detail_viewPager);
            ImageView leftArrow = (ImageView) findViewById(R.id.product_leftArrow);
            ImageView rightArrow = (ImageView) findViewById(R.id.product_rightArrow);
            ImageView close = (ImageView) findViewById(R.id.product_close);
            ViewCompat.setTransitionName(productViewPager, "imageKey");
            StoreModel productDetailModel = StoreDataActivity.product;

            if (productDetailModel != null) {
                final ArrayList<Screenshots> imageModelArray = productDetailModel.Screenshots;
                int layout = R.layout.product_zoom_image;
                ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(ScreenshotZoomActivity.this, imageModelArray, layout);
                productViewPager.setAdapter(pagerAdapter);
                if (getIntent().hasExtra("imagePos")) {
                    int position = Integer.parseInt(getIntent().getExtras().getString("imagePos"));
                    productViewPager.setCurrentItem(position);
                }
                leftArrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (productViewPager.getCurrentItem() > 0) {
                            productViewPager.setCurrentItem(productViewPager.getCurrentItem() - 1);
                        }
                    }
                });
                rightArrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int totalCount = imageModelArray.size();
                        if (productViewPager.getCurrentItem() < totalCount) {
                            productViewPager.setCurrentItem(productViewPager.getCurrentItem() + 1);
                        }
                    }
                });
            } else {
                Methods.showSnackBarNegative(this,"No Screenshots...");
            }

            close.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    finish();
                    return false;
                }
        });
        }
    }
}
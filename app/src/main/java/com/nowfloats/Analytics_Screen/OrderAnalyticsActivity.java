package com.nowfloats.Analytics_Screen;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.nowfloats.Analytics_Screen.model.RevenueSummary;
import com.nowfloats.CustomPage.CustomPageActivity;
import com.nowfloats.Image_Gallery.ImageGalleryActivity;
import com.nowfloats.NavigationDrawer.SidePanelFragment;
import com.nowfloats.NavigationDrawer.businessApps.FragmentsFactoryActivity;
import com.nowfloats.Store.Model.OnItemClickCallback;
import com.nowfloats.Store.SimpleImageTextListAdapter;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

public class OrderAnalyticsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_analytics);
        MixPanelController.track(MixPanelController.ORDER_ANALYTICS, null);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView title = (TextView) findViewById(R.id.title);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            title.setText(getString(R.string.order_analytics));
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final String[] adapterTexts = getResources().getStringArray(R.array.order_analytics_tab_items);
        final TypedArray imagesArray = getResources().obtainTypedArray(R.array.order_analytics_img);
        int[] adapterImages = new int[adapterTexts.length];
        for (int i = 0; i < adapterTexts.length; i++) {
            adapterImages[i] = imagesArray.getResourceId(i, -1);
        }
        imagesArray.recycle();
        RecyclerView mRecyclerView = findViewById(R.id.rv_upgrade);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        SimpleImageTextListAdapter adapter = new SimpleImageTextListAdapter(this, new OnItemClickCallback() {
            @Override
            public void onItemClick(int pos) {
                Intent intent = null;
                switch (adapterTexts[pos]) {
                    case "Order Summary":
                        intent = new Intent(OrderAnalyticsActivity.this, OrderSummaryActivity.class);
                        break;
                    case "Revenue Summary":
                        intent = new Intent(OrderAnalyticsActivity.this, RevenueSummaryActivity.class);
                        break;
                    default:
                        return;
                }
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        adapter.setItems(adapterImages, adapterTexts);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);
    }

}

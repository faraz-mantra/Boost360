package com.nowfloats.manageinventory;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.framework.views.customViews.CustomToolbar;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.ProductGallery.ProductGalleryActivity;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.Utils;
import com.thinksity.R;

/**
 * Created by guru on 08-06-2015.
 */
public class ManageInventoryActivity extends AppCompatActivity {

    TextView tvPaymentSetting, tvProductGallery, tvTotalNoOfOrders, tvTotalRevenue;
    CustomToolbar toolbar;
    String category_code = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_inventory);
         toolbar = (CustomToolbar) findViewById(R.id.toolbar);
        MixPanelController.track(MixPanelController.MANAGE_INVENTORY, null);
        setSupportActionBar(toolbar);
        UserSessionManager session = new UserSessionManager(getApplicationContext(), this);
        category_code = session.getFP_AppExperienceCode();
        if (getSupportActionBar() != null) {
            setTitle(Utils.getDefaultTrasactionsTaxonomyFromServiceCode(category_code));
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Typeface robotoMedium = Typeface.createFromAsset(getAssets(), "Roboto-Medium.ttf");

        tvPaymentSetting = (TextView) findViewById(R.id.tvPaymentSetting);
        tvPaymentSetting.setTypeface(robotoMedium);

        tvProductGallery = (TextView) findViewById(R.id.tvProductGallery);
        tvProductGallery.setTypeface(robotoMedium);

        tvPaymentSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                    Intent i = new Intent(getActivity(), BusinessEnquiryActivity.class);
//                    startActivity(i);
//                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        tvProductGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ManageInventoryActivity.this, ProductGalleryActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

    }

    private String getFragmentTitle() {
        return "";
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
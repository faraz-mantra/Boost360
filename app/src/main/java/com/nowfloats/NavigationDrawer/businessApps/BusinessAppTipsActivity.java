package com.nowfloats.NavigationDrawer.businessApps;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.nowfloats.BusinessProfile.UI.UI.BusinessHoursActivity;
import com.nowfloats.BusinessProfile.UI.UI.ContactInformationActivity;
import com.nowfloats.Image_Gallery.ImageGalleryActivity;
import com.nowfloats.NavigationDrawer.Create_Message_Activity;
import com.nowfloats.ProductGallery.Product_Detail_Activity_V45;
import com.thinksity.R;

/**
 * Created by Admin on 02-06-2017.
 */

public class BusinessAppTipsActivity extends AppCompatActivity implements DeepLinkAdapter.DeepLinkOnclick {

    RecyclerView list;
    LinearLayout parentLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guidelines);
        parentLayout = (LinearLayout) findViewById(R.id.parent_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String[] mainText = getResources().getStringArray(R.array.business_app_tips);
        String[] descriptions = getResources().getStringArray(R.array.business_app_tips_description);
        if (getSupportActionBar() != null) {
            setTitle(getString(R.string.tips_for_business_app_completeness));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        list = (RecyclerView) findViewById(R.id.list);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        list.setAdapter(new DeepLinkAdapter(this, mainText, descriptions));
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
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            parentLayout.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
            setTitle(getString(R.string.tips_for_business_app_completeness));
        } else {
            super.onBackPressed();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }

    }

    @Override
    public void onDeepLinkClick(int pos) {
        Intent intent = null;
        switch (pos) {
            case 0:
                intent = new Intent(BusinessAppTipsActivity.this, Create_Message_Activity.class);
                break;
            case 1:
                intent = new Intent(BusinessAppTipsActivity.this, Product_Detail_Activity_V45.class);
                intent.putExtra("new", "");
                break;
            case 2:
                list.setVisibility(View.GONE);
                parentLayout.setVisibility(View.VISIBLE);
                intent = new Intent(BusinessAppTipsActivity.this, ImageGalleryActivity.class);
                break;
            case 3:
                intent = new Intent(BusinessAppTipsActivity.this, ContactInformationActivity.class);
                break;
            case 4:
                intent = new Intent(BusinessAppTipsActivity.this, BusinessHoursActivity.class);
                break;
            default:
                return;
        }
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

}

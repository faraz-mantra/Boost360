package com.nowfloats.Business_Enquiries;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.widget.TextView;

import com.thinksity.R;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

/**
 * Created by guru on 05-05-2015.
 */
public class BusinessEnqUpdateDetails extends AppCompatActivity {
    String text = "";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bz_update_detail);
        if (getIntent().hasExtra("key")) {
            text = getIntent().getStringExtra("key");
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.store_data_action_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
//        if (domainPurchase) getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//        else
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Title
        TextView titleTextView = (TextView) toolbar.findViewById(R.id.store_title);
        titleTextView.setText(getResources().getString(R.string.business_enquiries_title));
        TextView bzText = (TextView) findViewById(R.id.bz_text);
        bzText.setText(text);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}

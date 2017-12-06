package com.nowfloats.Store;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.thinksity.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PricingPlansActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView headerText;
    private StoreFragmentTab storeFragmentTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_appearance);

        toolbar = findViewById(R.id.app_bar_site_appearance);
        setSupportActionBar(toolbar);
        headerText = toolbar.findViewById(R.id.titleTextView);
        headerText.setText(getResources().getString(R.string.store));

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        storeFragmentTab = new StoreFragmentTab();

        findViewById(R.id.fm_site_appearance).setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction().add(R.id.fm_site_appearance, storeFragmentTab).
                commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
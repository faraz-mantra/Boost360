package com.nowfloats.SiteAppearance;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.thinksity.R;

public class SiteAppearanceActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView headerText;
    private SiteAppearanceFragment siteAppearanceFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_appearance);

        toolbar = (Toolbar)findViewById(R.id.app_bar_site_appearance);
        setSupportActionBar(toolbar);
        headerText = (TextView) toolbar.findViewById(R.id.titleTextView);
        headerText.setText(getResources().getString(R.string.side_panel_site_appearance));

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        siteAppearanceFragment = new SiteAppearanceFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fm_site_appearance,siteAppearanceFragment).
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

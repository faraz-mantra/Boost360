package com.nowfloats.NavigationDrawer;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.nowfloats.CustomPage.CustomPageDeleteInterface;
import com.nowfloats.Store.UpgradesFragment;
import com.thinksity.R;

/**
 * Created by guru on 08-06-2015.
 */
public class AccountInfoActivity extends AppCompatActivity implements CustomPageDeleteInterface {

    Toolbar toolbar;
    TextView headerText;
    private UpgradesFragment upgradesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_appearance);

        toolbar = (Toolbar) findViewById(R.id.app_bar_site_appearance);
        setSupportActionBar(toolbar);
        headerText = (TextView) toolbar.findViewById(R.id.titleTextView);
        headerText.setText("Account Info");

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        upgradesFragment = new UpgradesFragment();

        findViewById(R.id.fm_site_appearance).setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction().add(R.id.fm_site_appearance, upgradesFragment).
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

    @Override
    public void DeletePageTrigger(int position, boolean chk, View view) {

    }
}
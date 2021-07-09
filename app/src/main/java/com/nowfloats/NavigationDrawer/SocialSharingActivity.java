package com.nowfloats.NavigationDrawer;


import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.nowfloats.BusinessProfile.UI.UI.SocialSharingFragment;
import com.nowfloats.CustomPage.CustomPageDeleteInterface;
import com.nowfloats.util.BoostLog;
import com.thinksity.R;

/**
 * Created by guru on 08-06-2015.
 */
public class SocialSharingActivity extends AppCompatActivity implements CustomPageDeleteInterface {

    Toolbar toolbar;
    TextView headerText;
    private SocialSharingFragment customPageFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_appearance);

        toolbar = (Toolbar) findViewById(R.id.app_bar_site_appearance);
        setSupportActionBar(toolbar);
        headerText = (TextView) toolbar.findViewById(R.id.titleTextView);
        headerText.setText("Content Sharing");

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        customPageFragment = new SocialSharingFragment();

        findViewById(R.id.fm_site_appearance).setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction().replace(R.id.fm_site_appearance, customPageFragment, "socialSharingFragment").
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (CardAdapter_V3.pd != null)
            CardAdapter_V3.pd.dismiss();
        BoostLog.d("onActivityResult", "i am here");

        Fragment sociaSharingFragment = getSupportFragmentManager().findFragmentByTag("socialSharingFragment");
        if (sociaSharingFragment != null) {
            ((SocialSharingFragment) sociaSharingFragment).onSocialSharingResult(requestCode, resultCode, data);
        }
    }
}
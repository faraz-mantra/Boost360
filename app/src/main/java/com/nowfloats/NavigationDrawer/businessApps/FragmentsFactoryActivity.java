package com.nowfloats.NavigationDrawer.businessApps;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.nowfloats.BusinessProfile.UI.UI.Business_Profile_Fragment_V2;
import com.nowfloats.NavigationDrawer.DictateFragment;
import com.nowfloats.NavigationDrawer.WildFireFragment;
import com.thinksity.R;

/**
 * Created by Admin on 30-01-2018.
 */

public class FragmentsFactoryActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView headerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_appearance);

        toolbar = (Toolbar) findViewById(R.id.app_bar_site_appearance);
        setSupportActionBar(toolbar);
        headerText = (TextView) toolbar.findViewById(R.id.titleTextView);


        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.fm_site_appearance).setVisibility(View.VISIBLE);
        Bundle intent = getIntent().getExtras();

        if (intent != null && intent.containsKey("fragmentName")){
            String fragmentName = intent.getString("fragmentName");
            Fragment attachedFragment;
            switch (fragmentName){
                case "DictateFragment":
                    headerText.setText("Dictate");
                    attachedFragment = new DictateFragment();
                    break;
                case "WildFireFragment":
                    headerText.setText("WildFire");
                    attachedFragment = new WildFireFragment();
                    break;
                case "BusinessAppsFragment":
                    headerText.setText(getResources().getString(R.string.my_business_apps));
                    attachedFragment = new BusinessAppsFragment();
                    break;
                case "Business_Profile_Fragment_V2":
                    headerText.setText(getResources().getString(R.string.business_profile));
                    attachedFragment = new Business_Profile_Fragment_V2();
                    break;
                default:
                    finish();
                    return;
            }
            getSupportFragmentManager().beginTransaction().add(R.id.fm_site_appearance, attachedFragment).
                    commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

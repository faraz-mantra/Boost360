package com.nowfloats.SiteAppearance;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

       /* ViewPager mPager = (ViewPager) findViewById(R.id.ps_pager);
        PageIndicatorView mIndicator = (PageIndicatorView) findViewById(R.id.ps_indicator);
        mPager.setClipToPadding(false);
        mPager.setPadding(70, 50, 70,0);
        // sets a margin b/w individual pages to ensure that there is a gap b/w them
        mPager.setPageMargin(30);

        mPager.setAdapter(new PagerThemeAdapter(getSupportFragmentManager()));
        mIndicator.setViewPager(mPager);*/
    }

    /*public class PagerThemeAdapter extends FragmentStatePagerAdapter {

        FragmentManager manager;
        public PagerThemeAdapter(FragmentManager fm) {
            super(fm);
            manager = fm;
        }

        @Override
        public Fragment getItem(int position) {
           return ThemeSelectorFragment.getInstanse(position);
        }

        @Override
        public int getCount() {
            return 4;
        }
    }*/
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

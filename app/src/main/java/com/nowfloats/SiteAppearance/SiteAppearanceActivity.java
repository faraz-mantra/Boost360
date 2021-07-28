package com.nowfloats.SiteAppearance;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.nowfloats.Login.UserSessionManager;
import com.rd.PageIndicatorView;
import com.thinksity.R;

public class SiteAppearanceActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView headerText;
    private SiteAppearanceFragment siteAppearanceFragment;
    private PagerThemeAdapter adapter;
    private UserSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_appearance);

        toolbar = (Toolbar) findViewById(R.id.app_bar_site_appearance);
        setSupportActionBar(toolbar);
        headerText = (TextView) toolbar.findViewById(R.id.titleTextView);
        headerText.setText(getResources().getString(R.string.side_panel_site_appearance));

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        siteAppearanceFragment = new SiteAppearanceFragment();
        session = new UserSessionManager(this, this);

        if (session.getWebTemplateType().equals("4")) {
            findViewById(R.id.fm_site_appearance).setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction().add(R.id.fm_site_appearance, siteAppearanceFragment).
                    commit();
        } else {
            findViewById(R.id.theme_picker).setVisibility(View.VISIBLE);
            setThemePicker();
        }

    }

    private void setThemePicker() {
        ViewPager mPager = (ViewPager) findViewById(R.id.ps_pager);
        PageIndicatorView mIndicator = (PageIndicatorView) findViewById(R.id.ps_indicator);
        mPager.setClipToPadding(false);
        mPager.setPadding(70, 50, 70, 0);
        // sets a margin b/w individual pages to ensure that there is a gap b/w them
        mPager.setPageMargin(30);
        adapter = new PagerThemeAdapter(getSupportFragmentManager());
        mPager.setAdapter(adapter);
        mIndicator.setViewPager(mPager);
    }

    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
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

    private class PagerThemeAdapter extends FragmentStatePagerAdapter {

        FragmentManager manager;

        private PagerThemeAdapter(FragmentManager fm) {
            super(fm);
            manager = fm;
        }

        @Override
        public Fragment getItem(int position) {
            return ThemeSelectorFragment.getInstance(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return ThemeSelectorFragment.imageIds.length;
        }
    }

}

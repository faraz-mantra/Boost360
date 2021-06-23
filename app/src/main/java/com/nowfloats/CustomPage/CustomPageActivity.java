package com.nowfloats.CustomPage;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.nowfloats.util.WebEngageController;
import com.thinksity.R;

import static com.framework.webengageconstant.EventLabelKt.EVENT_LABEL_CUSTOMPAGE;
import static com.framework.webengageconstant.EventNameKt.CUSTOMPAGE_LOAD;
import static com.framework.webengageconstant.EventValueKt.NULL;

/**
 * Created by guru on 08-06-2015.
 */
public class CustomPageActivity extends AppCompatActivity implements CustomPageDeleteInterface {

  Toolbar toolbar;
  TextView headerText;
  boolean isAdd = false;
  private CustomPageFragment customPageFragment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_site_appearance);

    WebEngageController.trackEvent(CUSTOMPAGE_LOAD, EVENT_LABEL_CUSTOMPAGE, NULL);

    toolbar = (Toolbar) findViewById(R.id.app_bar_site_appearance);
    setSupportActionBar(toolbar);
    headerText = (TextView) toolbar.findViewById(R.id.titleTextView);
    headerText.setText(getResources().getString(R.string.custom_pages));

    getSupportActionBar().setDisplayShowHomeEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    customPageFragment = new CustomPageFragment();

    findViewById(R.id.fm_site_appearance).setVisibility(View.VISIBLE);
    getSupportFragmentManager().beginTransaction().add(R.id.fm_site_appearance, customPageFragment).
        commit();
    Bundle bundle = getIntent().getExtras();
    if (bundle != null) isAdd = bundle.getBoolean("IS_ADD");
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
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == RESULT_OK && requestCode == 202) {
      if (!(data != null && data.getBooleanExtra("IS_REFRESH", false))) onBackPressed();
      else if (customPageFragment != null) customPageFragment.isRefreshList();
    }
  }
}
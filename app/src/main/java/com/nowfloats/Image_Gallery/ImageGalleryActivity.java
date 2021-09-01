package com.nowfloats.Image_Gallery;

import static com.nowfloats.util.Key_Preferences.GET_FP_DETAILS_CATEGORY;

import android.annotation.SuppressLint;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.boost.upgrades.UpgradeActivity;
import com.framework.models.caplimit_feature.CapLimitFeatureResponseItem;
import com.framework.models.caplimit_feature.PropertiesItem;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;
import com.thinksity.databinding.ActivityImageGalleryBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class ImageGalleryActivity extends AppCompatActivity {

  public static final String TAG_IMAGE = "TAG_IMAGE";
  ActivityImageGalleryBinding binding;
  ArrayList<String> purchasedWidgetList = new ArrayList<String>();
  private Image_Gallery_Fragment image_gallery_fragment;
  private int noOfImages = 0;
  public UserSessionManager session;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_image_gallery);
    MixPanelController.track(MixPanelController.IMAGE_GALLERY, null);

    setSupportActionBar(binding.appBar.toolbar);
    Methods.isOnline(ImageGalleryActivity.this);
    session = new UserSessionManager(getApplicationContext(), this);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setDisplayShowHomeEnabled(true);

      getSupportActionBar().setTitle("");
    }

    binding.appBar.toolbarTitle.setText(getResources().getString(R.string.image_gallery));

    image_gallery_fragment = new Image_Gallery_Fragment();
    findViewById(R.id.fm_site_appearance).setVisibility(View.VISIBLE);
    getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.fm_site_appearance, image_gallery_fragment, TAG_IMAGE)
        .commit();

    if (savedInstanceState != null && image_gallery_fragment != null) {
      Boolean startCreateMode = savedInstanceState.getBoolean("create_image", false);
      if (startCreateMode) {
        image_gallery_fragment.addImage();
      }
    }
    binding.btnAdd.setOnClickListener(view -> {
      CapLimitFeatureResponseItem data = new CapLimitFeatureResponseItem().getCapData();
      if (data != null) {
        PropertiesItem capLimitImage = data.filterProperty(PropertiesItem.KeyType.IMAGE);
        if (capLimitImage.getValueN() != null && Constants.storeSecondaryImages.size() >= capLimitImage.getValueN()) {
          showAlertCapLimit("Can't add the image, please activate your premium Add-ons plan.");
          return;
        }
      }
      image_gallery_fragment.addImage();
    });
  }

  void showAlertCapLimit(String msg) {
    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomAlertDialogTheme));
    builder.setCancelable(false);
    builder.setTitle("You have exceeded limit!").setMessage(msg);
    builder.setPositiveButton("Explore Add-ons", (dialog, which) -> {
      dialog.dismiss();
      initiateBuyFromMarketplace();
    });
    builder.create().show();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    //        getMenuInflater().inflate(R.menu.menu_add, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
        break;
      case R.id.menu_add:
        image_gallery_fragment.addImage();
        break;
    }

    return super.onOptionsItemSelected(item);
  }

  private void initiateBuyFromMarketplace() {
    ProgressDialog progressDialog = new ProgressDialog(this);
    String status = "Loading. Please wait...";
    progressDialog.setMessage(status);
    progressDialog.setCancelable(false);
    progressDialog.show();
    Intent intent = new Intent(this, UpgradeActivity.class);
    intent.putExtra("expCode", session.getFP_AppExperienceCode());
    intent.putExtra("fpName", session.getFPName());
    intent.putExtra("fpid", session.getFPID());
    intent.putExtra("fpTag", session.getFpTag());
    intent.putExtra("accountType", session.getFPDetails(GET_FP_DETAILS_CATEGORY));
    intent.putStringArrayListExtra("userPurchsedWidgets", Constants.StoreWidgets);
    if (session.getUserProfileEmail() != null) {
      intent.putExtra("email", session.getUserProfileEmail());
    } else {
      intent.putExtra("email", "ria@nowfloats.com");
    }
    if (session.getUserPrimaryMobile() != null) {
      intent.putExtra("mobileNo", session.getUserPrimaryMobile());
    } else {
      intent.putExtra("mobileNo", "9160004303");
    }
    intent.putExtra("profileUrl", session.getFPLogo());
    intent.putExtra("buyItemKey", CapLimitFeatureResponseItem.FeatureType.UNLIMITED_CONTENT.name());
    startActivity(intent);
    new Handler().postDelayed(() -> progressDialog.dismiss(), 1000);
  }
}

package com.nowfloats.ProductGallery;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.ProductGallery.Model.Product;
import com.nowfloats.helper.ui.BaseActivity;
import com.thinksity.R;
import com.thinksity.databinding.ActivityManageProductBinding;


public class ManageProductActivity extends BaseActivity {
    private ActivityManageProductBinding binding;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manage_product);

        setSupportActionBar(binding.layoutToolbar.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            getSupportActionBar().setTitle("");
        }

        Product product = (Product) getIntent().getSerializableExtra("PRODUCT");
//        loadFragment(ProductCategoryFragment.newInstance(product), "PRODUCT_CATEGORY");
        loadFragment(ManageProductFragment.newInstance(product), "MANAGE_PRODUCT");
//        loadFragment(ManageProductFragment.newInstance("products", "", product), "MANAGE_PRODUCT");

        /*if(this.product == null)
        {
            loadFragment(ProductCategoryFragment.newInstance(), "PRODUCT_CATEGORY");
        }

        else
        {
            loadFragment(ManageProductFragment.newInstance("", "", this.product), "PRODUCT_CATEGORY");
        }*/
    }


    public void setTitle(String title) {
        binding.layoutToolbar.toolbarTitle.setText(String.valueOf(title));
    }

    public void loadFragment(Fragment fragment, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction
                //.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                .replace(R.id.fm_site_appearance, fragment, tag)
                .commit();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                onHomePressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onHomePressed() {
        if (isProductCategoryFragment()) {
            finish();
        } else {
            confirm();
        }
    }

    private void confirm() {
        new MaterialDialog.Builder(this)
                .title(R.string.information_not_saved)
                .content(R.string.you_have_unsaved_information_do_you_still_want_to_close)
                .positiveText(getString(R.string.no))
                .positiveColorRes(R.color.primaryColor)
                .negativeText(getString(R.string.yes))
                .negativeColorRes(R.color.light_gray)
                .callback(new MaterialDialog.ButtonCallback() {

                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        dialog.dismiss();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        dialog.dismiss();
                        finish();
                    }
                }).show();

    }


    @Override
    public void onBackPressed() {
        confirm();

//        if(isProductCategoryFragment())
//        {

//            super.onBackPressed();
//            return;
//        }
//
//        if (doubleBackToExitPressedOnce)
//        {
//            super.onBackPressed();
//            return;
//        }
//
//        this.doubleBackToExitPressedOnce = true;
//        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
//
//        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }


    private boolean isProductCategoryFragment() {
        /*ProductCategoryFragment fragment = (ProductCategoryFragment)getSupportFragmentManager().findFragmentByTag("PRODUCT_CATEGORY");

        if (fragment != null && fragment.isVisible())
        {
            return true;
        }*/

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fm_site_appearance);
        return fragment instanceof ProductCategoryFragment;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fm_site_appearance);
        fragment.onActivityResult(requestCode, resultCode, data);
    }
}
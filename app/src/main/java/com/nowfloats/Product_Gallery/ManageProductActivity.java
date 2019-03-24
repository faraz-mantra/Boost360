package com.nowfloats.Product_Gallery;

import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.helper.ui.BaseActivity;
import com.thinksity.R;
import com.thinksity.databinding.ActivityManageProductBinding;

import static com.nowfloats.Product_Gallery.ProductGalleryActivity.TAG_PRODUCT;

public class ManageProductActivity extends BaseActivity
{
    private ActivityManageProductBinding binding;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manage_product);

        setSupportActionBar(binding.layoutToolbar.toolbar);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            getSupportActionBar().setTitle("");
        }

        loadFragment(ProductCategoryFragment.newInstance());
    }


    public void setTitle(String title)
    {
        binding.layoutToolbar.toolbarTitle.setText(String.valueOf(title));
    }

    public void loadFragment(Fragment fragment)
    {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fm_site_appearance, fragment, TAG_PRODUCT)
                .commit();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:

                confirm();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void confirm()
    {
        new MaterialDialog.Builder(this)
                .title("Are you sure ?")
                .content("Information not saved. Are you sure want to close.")
                .positiveText(getString(R.string.yes))
                .positiveColorRes(R.color.primaryColor)
                .negativeText(getString(R.string.no))
                .negativeColorRes(R.color.light_gray)
                .callback(new MaterialDialog.ButtonCallback() {

                    @Override
                    public void onPositive(MaterialDialog dialog)
                    {
                        super.onPositive(dialog);
                        dialog.dismiss();
                        finish();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog)
                    {
                        super.onNegative(dialog);
                        dialog.dismiss();
                    }
                }).show();

    }


    @Override
    public void onBackPressed()
    {
        if (doubleBackToExitPressedOnce)
        {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, 2000);
    }
}
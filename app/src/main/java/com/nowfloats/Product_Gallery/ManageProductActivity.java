package com.nowfloats.Product_Gallery;

import android.databinding.DataBindingUtil;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.MenuItem;

import com.nowfloats.helper.ui.BaseActivity;
import com.thinksity.R;
import com.thinksity.databinding.ActivityManageProductBinding;

import static com.nowfloats.Product_Gallery.ProductGalleryActivity.TAG_PRODUCT;

public class ManageProductActivity extends BaseActivity
{
    private ActivityManageProductBinding binding;

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

                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
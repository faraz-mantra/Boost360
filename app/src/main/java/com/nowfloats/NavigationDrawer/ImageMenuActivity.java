package com.nowfloats.NavigationDrawer;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appservice.constant.FragmentType;
import com.appservice.ui.background_image.BackgroundImageContainerActivityKt;
import com.nowfloats.BusinessProfile.UI.UI.Business_Logo_Activity;
import com.nowfloats.BusinessProfile.UI.UI.FaviconImageActivity;
import com.nowfloats.BusinessProfile.UI.UI.FeaturedImageActivity;
import com.nowfloats.Image_Gallery.ImageGalleryActivity;
import com.nowfloats.Store.Model.OnItemClickCallback;
import com.nowfloats.Store.SimpleImageTextListAdapter;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;
import com.thinksity.databinding.ActivityImageMenuBinding;

public class ImageMenuActivity extends AppCompatActivity {
    private ActivityImageMenuBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_image_menu);

        setSupportActionBar(binding.layoutToolbar.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            getSupportActionBar().setTitle("");
        }

        binding.layoutToolbar.toolbarTitle.setText("All Images");
        initMenuRecyclerView(binding.menuList);
    }

    /**
     * Initialize pickup address list adapter
     *
     * @param mRecyclerView
     */
    private void initMenuRecyclerView(RecyclerView mRecyclerView) {
        final String[] adapterTexts = getResources().getStringArray(R.array.images_content_list_items);
        final TypedArray imagesArray = getResources().obtainTypedArray(R.array.image_list_icons);
        int[] adapterImages = new int[adapterTexts.length];

        for (int i = 0; i < adapterTexts.length; i++) {
            adapterImages[i] = imagesArray.getResourceId(i, -1);
        }

        imagesArray.recycle();

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        SimpleImageTextListAdapter adapter = new SimpleImageTextListAdapter(this, new OnItemClickCallback() {

            @Override
            public void onItemClick(int pos) {
                Intent intent = null;

                switch (adapterTexts[pos]) {
                    case "Business logo":

                        MixPanelController.track(EventKeysWL.LOGO, null);
                        intent = new Intent(ImageMenuActivity.this, Business_Logo_Activity.class);
                        break;

                    case "Featured image":

                        intent = new Intent(ImageMenuActivity.this, FeaturedImageActivity.class);
                        break;

                    case "Image gallery":

                        intent = new Intent(ImageMenuActivity.this, ImageGalleryActivity.class);
                        break;

                    case "Custom background images":
                        intent = new Intent(ImageMenuActivity.this, BackgroundImageContainerActivityKt.class);
                        break;

                    case "Favicon":

                        intent = new Intent(ImageMenuActivity.this, FaviconImageActivity.class);
                        break;
                }

                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        adapter.setItems(adapterImages, adapterTexts);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
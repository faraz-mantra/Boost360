package com.festive.poster.ui

import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import com.festive.poster.R
import com.festive.poster.base.AppBaseActivity
import com.festive.poster.databinding.ActivityFestivePoterContainerBinding
import com.framework.models.BaseViewModel

class FestivePosterContainerActivity:
    AppBaseActivity<ActivityFestivePoterContainerBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.activity_festive_poter_container
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        addFragmentReplace(binding?.container?.id,PosterPackListingFragment.newInstance(),true)
        setSupportActionBar(binding?.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(ContextCompat.getDrawable(this,R.drawable.ic_fposter_back_poster_pack_listing))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.festive_poster_listing_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_downloads->{
                addFragmentReplace(binding?.container?.id,PosterDownloadsFragment.newInstance(),true)

            }
        }
        return super.onOptionsItemSelected(item)
    }
}
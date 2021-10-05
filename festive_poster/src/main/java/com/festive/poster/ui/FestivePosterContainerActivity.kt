package com.festive.poster.ui

import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.festive.poster.R
import com.festive.poster.base.AppBaseActivity
import com.festive.poster.databinding.ActivityFestivePoterContainerBinding
import com.festive.poster.utils.SvgUtils
import com.framework.models.BaseViewModel

class FestivePosterContainerActivity:
    AppBaseActivity<ActivityFestivePoterContainerBinding, BaseViewModel>() {

     override var TAG = "FestivePosterContainerA"
    override fun getLayout(): Int {
        return R.layout.activity_festive_poter_container
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()

       /* setSupportActionBar(binding?.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)*/
        handleBackStack()
        menuClickListener()
/*
        supportActionBar?.setHomeAsUpIndicator(ContextCompat.getDrawable(this,R.drawable.ic_fposter_back_poster_pack_listing))
*/
        addFragmentReplace(binding?.container?.id,PosterPackListingFragment.newInstance(),true)

        lifecycleScope.launchWhenCreated {
           // SvgUtils.downloadSvg("https://www.learningcontainer.com/wp-content/uploads/2020/08/Sample-SVG-Image-File-Download.svg")
        }

    }

    private fun menuClickListener() {

        binding?.toolbar?.navigationIcon = ContextCompat.getDrawable(this,R.drawable.ic_fposter_back_poster_pack_listing)
        binding?.toolbar?.setNavigationOnClickListener {
            onBackPressed()
        }
        binding?.toolbar?.setOnMenuItemClickListener { item ->
            when (item?.itemId) {
                R.id.menu_downloads -> {
                    addFragmentReplace(
                        binding?.container?.id,
                        PosterPackPurchasedFragment.newInstance(),
                        true
                    )

                }
            }

            true
        }

    }

    private fun handleBackStack() {
        supportFragmentManager.addOnBackStackChangedListener {


            binding?.toolbar?.menu?.clear()
            binding?.toolbar?.inflateMenu(R.menu.festive_poster_help_menu)

            val topFragment = getTopFragment()
            Log.i(TAG, "handleBackStack: top ${topFragment?.tag}")

            if (topFragment!=null){
                when(topFragment){
                    is  PosterPackListingFragment->{
                        supportActionBar?.title = getString(R.string.festival_poster)
                        binding?.toolbar?.menu?.clear()
                        binding?.toolbar?.inflateMenu(R.menu.festive_poster_listing_menu)
                    }

                    is PosterPackPurchasedFragment->{
                        supportActionBar?.title = getString(R.string.purchased_posters)
                    }
                    is PosterListFragment->{
                        supportActionBar?.title =topFragment.title
                    }
                }



            }else{
                Log.i(TAG, "handleBackStack: top fragment is null")
            }
        }
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_downloads->{
                addFragmentReplace(binding?.container?.id,PosterPackPurchasedFragment.newInstance(),true)

            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        if (getTopFragment() ==null){
            finish()
        }
    }
}
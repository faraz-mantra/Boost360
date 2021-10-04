package com.festive.poster.ui

import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import com.festive.poster.R
import com.festive.poster.base.AppBaseActivity
import com.festive.poster.databinding.ActivityFestivePoterContainerBinding
import com.framework.models.BaseViewModel

class FestivePosterContainerActivity:
    AppBaseActivity<ActivityFestivePoterContainerBinding, BaseViewModel>() {

    var menu:Menu?=null
     override var TAG = "FestivePosterContainerA"
    override fun getLayout(): Int {
        return R.layout.activity_festive_poter_container
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        addFragmentReplace(binding?.container?.id,PosterPackListingFragment.newInstance(),false)
        setSupportActionBar(binding?.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(ContextCompat.getDrawable(this,R.drawable.ic_fposter_back_poster_pack_listing))
    }

    private fun handleBackStack() {
        supportFragmentManager.addOnBackStackChangedListener {

            binding?.toolbar?.menu!!.findItem(R.id.menu_downloads)!!.isVisible = false
            binding?.toolbar?.menu!!.findItem(R.id.menu_search)!!.isVisible =false
            val topFragment = getTopFragment()
            Log.i(TAG, "handleBackStack: top ${topFragment?.tag}")
            if (topFragment!=null){
                when(topFragment){
                    is  PosterPackListingFragment->{
                        supportActionBar?.title = getString(R.string.festival_poster)
                        binding?.toolbar?.menu?.findItem(R.id.menu_downloads)?.isVisible =true
                        binding?.toolbar?.menu?.findItem(R.id.menu_search)?.isVisible =true
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.festive_poster_listing_menu,menu)
        this.menu = menu
        handleBackStack()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_downloads->{
                addFragmentReplace(binding?.container?.id,PosterPackPurchasedFragment.newInstance(),true)

            }
        }
        return super.onOptionsItemSelected(item)
    }


}
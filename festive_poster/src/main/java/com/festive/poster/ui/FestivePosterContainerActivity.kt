package com.festive.poster.ui

import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.festive.poster.R
import com.festive.poster.base.AppBaseActivity
import com.festive.poster.databinding.ActivityFestivePoterContainerBinding
import com.festive.poster.utils.SvgUtils
import com.festive.poster.utils.WebEngageController
import com.festive.poster.viewmodels.FestivePosterSharedViewModel
import com.framework.models.BaseViewModel
import com.framework.webengageconstant.SHARE_FESTIVE_POSTER_BUTTON
import kotlinx.android.synthetic.main.mtemplate_progress_dialog.*

class FestivePosterContainerActivity:
    AppBaseActivity<ActivityFestivePoterContainerBinding, BaseViewModel>() {

     override var TAG = "FestivePosterContainerA"
    private var sharedViewModel: FestivePosterSharedViewModel?=null

    override fun getLayout(): Int {
        return R.layout.activity_festive_poter_container
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        WebEngageController.trackEvent(SHARE_FESTIVE_POSTER_BUTTON)
        sharedViewModel = ViewModelProvider(this).get(FestivePosterSharedViewModel::class.java)

        binding?.toolbar?.setTitleTextAppearance(this,R.style.BoldTextAppearance)
        binding?.toolbar?.inflateMenu(R.menu.festive_poster_help_menu)

        handleBackStack()
        menuClickListener()

        addFragmentReplace(binding?.container?.id,PosterPackListingFragment.newInstance(),true)

        lifecycleScope.launchWhenCreated {
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
                R.id.menu_help->{
                    PosterHelpSheet().show(supportFragmentManager,PosterHelpSheet::class.java.name)
                }
            }

            true
        }

    }

    private fun handleBackStack() {
        supportFragmentManager.addOnBackStackChangedListener {


            //binding?.toolbar?.menu?.clear()
       //     binding?.toolbar?.inflateMenu(R.menu.festive_poster_help_menu)

            val topFragment = getTopFragment()
            Log.i(TAG, "handleBackStack: top ${topFragment?.tag}")

            if (topFragment!=null){
                when(topFragment){
                    is  PosterPackListingFragment->{
                        binding?.toolbar?.title = getString(R.string.festival_poster)
                      //  binding?.toolbar?.menu?.clear()
                       // binding?.toolbar?.inflateMenu(R.menu.festive_poster_listing_menu)
                    }

                    is PosterPackPurchasedFragment->{
                        binding?.toolbar?.title = getString(R.string.purchased_posters)
                    }
                    is PosterListFragment->{
                        binding?.toolbar?.title =
                            sharedViewModel?.selectedPosterPack?.tagsModel?.Name+
                                    " (${sharedViewModel?.selectedPosterPack?.posterList?.size})"
                    }
                }



            }else{
                Log.i(TAG, "handleBackStack: top fragment is null")
            }
        }
    }




    override fun onBackPressed() {
        super.onBackPressed()
        if (getTopFragment() ==null){
            finish()
        }
    }
}
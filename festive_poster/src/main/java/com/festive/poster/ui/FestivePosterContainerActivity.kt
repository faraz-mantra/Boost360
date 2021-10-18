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
import com.framework.webengageconstant.FESTIVE_POSTER_PAGE_LOAD
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
        WebEngageController.trackEvent(FESTIVE_POSTER_PAGE_LOAD,event_value = HashMap())
        sharedViewModel = ViewModelProvider(this).get(FestivePosterSharedViewModel::class.java)

        handleBackStack()
        menuClickListener()

        SvgUtils.initReqBuilder(this)

        addFragmentReplace(binding?.container?.id,PosterPackListingFragment.newInstance(),true)

        lifecycleScope.launchWhenCreated {
        }

    }

    private fun menuClickListener() {

        binding?.ivBack?.setOnClickListener {
            onBackPressed()
        }
        binding?.ivHelp?.setOnClickListener {
            PosterHelpSheet().show(supportFragmentManager,PosterHelpSheet::class.java.name)

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
                        binding?.tvTitle?.text = getString(R.string.festival_poster)
                      //  binding?.toolbar?.menu?.clear()
                       // binding?.toolbar?.inflateMenu(R.menu.festive_poster_listing_menu)
                    }

                    is PosterPackPurchasedFragment->{
                        binding?.tvTitle?.text  = getString(R.string.purchased_posters)
                    }
                    is PosterListFragment->{
                        binding?.tvTitle?.text  =
                            sharedViewModel?.selectedPosterPack?.tagsModel?.name+
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
package com.festive.poster.ui

import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.festive.poster.R
import com.festive.poster.base.AppBaseActivity
import com.festive.poster.databinding.ActivityFestivePoterContainerBinding
import com.festive.poster.utils.WebEngageController
import com.festive.poster.viewmodels.FestivePosterSharedViewModel
import com.framework.models.BaseViewModel
import com.framework.webengageconstant.FESTIVE_POSTER_PAGE_LOAD

class FestivePosterContainerActivity : AppBaseActivity<ActivityFestivePoterContainerBinding, BaseViewModel>() {

  override var TAG = "FestivePosterContainerA"
  private var sharedViewModel: FestivePosterSharedViewModel? = null

  override fun getLayout(): Int {
    return R.layout.activity_festive_poter_container
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    WebEngageController.trackEvent(FESTIVE_POSTER_PAGE_LOAD, event_value = HashMap())
    sharedViewModel = ViewModelProvider(this).get(FestivePosterSharedViewModel::class.java)
    handleBackStack()
    menuClickListener()
    addFragmentReplace(binding?.container?.id, PosterPackListingFragment.newInstance(), true)
    lifecycleScope.launchWhenCreated {
    }
  }

  private fun menuClickListener() {
    binding?.ivBack?.setOnClickListener {
      onBackPressed()
    }
    binding?.ivHelp?.setOnClickListener {
      PosterHelpSheet().show(supportFragmentManager, PosterHelpSheet::class.java.name)
    }
  }

  private fun handleBackStack() {
    supportFragmentManager.addOnBackStackChangedListener {
      val topFragment = getTopFragment()
      Log.i(TAG, "handleBackStack: top ${topFragment?.tag}")
      if (topFragment != null) {
        when (topFragment) {
          is PosterPackListingFragment -> {
            binding?.tvTitle?.text = getString(R.string.festival_poster)
          }

          is PosterPackPurchasedFragment -> {
            binding?.tvTitle?.text = getString(R.string.purchased_posters)
          }
          is PosterListFragment -> {
            binding?.tvTitle?.text = sharedViewModel?.selectedPosterPack?.tagsModel?.name + " (${sharedViewModel?.selectedPosterPack?.posterList?.size})"
          }
        }
      } else {
        Log.i(TAG, "handleBackStack: top fragment is null")
      }
    }
  }


  override fun onBackPressed() {
    super.onBackPressed()
    if (getTopFragment() == null) finish()
  }
}
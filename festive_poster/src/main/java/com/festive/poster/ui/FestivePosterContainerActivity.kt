package com.festive.poster.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.festive.poster.R
import com.festive.poster.base.AppBaseActivity
import com.festive.poster.databinding.ActivityFestivePoterContainerBinding
import com.festive.poster.utils.SvgUtils
import com.festive.poster.utils.WebEngageController
import com.festive.poster.viewmodels.FestivePosterSharedViewModel
import com.framework.models.BaseViewModel
import com.framework.webengageconstant.FESTIVE_POSTER_PAGE_LOAD

class FestivePosterContainerActivity : AppBaseActivity<ActivityFestivePoterContainerBinding, BaseViewModel>() {

  override var TAG = "FestivePosterContainerA"
  private var sharedViewModel: FestivePosterSharedViewModel? = null
  /*private val RC_STORAGE_PERMISSION=201*/

  override fun getLayout(): Int {
    return R.layout.activity_festive_poter_container
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    WebEngageController.trackEvent(FESTIVE_POSTER_PAGE_LOAD, event_value = HashMap())
    SvgUtils.initReqBuilder(this)
    sharedViewModel = ViewModelProvider(this).get(FestivePosterSharedViewModel::class.java)
    handleBackStack()
    menuClickListener()
    showPosterPackListing()
    lifecycleScope.launchWhenCreated {
    }
  }

  /*private fun checkStoragePermission() {
   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),RC_STORAGE_PERMISSION)
        }else{
          showPosterPackListing()
        }
      } else {
        showPosterPackListing()
      }

  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    if (requestCode==RC_STORAGE_PERMISSION&&grantResults.isNotEmpty()){
      if (grantResults[0]==PackageManager.PERMISSION_GRANTED)
          showPosterPackListing()
      else
        finish()
    }
  }*/

  private fun showPosterPackListing() {
    addFragmentReplace(binding?.container?.id, PosterPackListingFragment.newInstance(), true)

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
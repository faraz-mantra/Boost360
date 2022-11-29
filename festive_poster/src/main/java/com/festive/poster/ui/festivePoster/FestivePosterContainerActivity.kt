package com.festive.poster.ui.festivePoster

import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.festive.poster.R
import com.festive.poster.base.AppBaseActivity
import com.festive.poster.databinding.ActivityFestivePoterContainerBinding
import com.festive.poster.utils.SvgUtils
import com.festive.poster.utils.WebEngageController
import com.festive.poster.viewmodels.FestivePosterSharedViewModel
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.framework.utils.toArrayList
import com.framework.webengageconstant.FESTIVE_POSTER_PAGE_LOAD

class FestivePosterContainerActivity : AppBaseActivity<ActivityFestivePoterContainerBinding, BaseViewModel>() {

  override var TAG = "FestivePosterContainerA"
  private var sharedViewModel: FestivePosterSharedViewModel? = null
  private var posterPackListFragment= PosterPackListingFragment.newInstance()
  private var isPosterPackLoaded=false
  /*private val RC_STORAGE_PERMISSION=201*/

  override fun getLayout(): Int {
    return R.layout.activity_festive_poter_container
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    binding?.ivDownload?.isEnabled = false

    WebEngageController.trackEvent(FESTIVE_POSTER_PAGE_LOAD, event_value = HashMap())
    SvgUtils.initReqBuilder(this)
    sharedViewModel = ViewModelProvider(this).get(FestivePosterSharedViewModel::class.java)
    handleBackStack()
    setOnClickListener(binding?.ivBack,binding?.ivHelp,binding?.ivDownload)
    showPosterPackListing()
    lifecycleScope.launchWhenCreated {
    }

    setObserevers()
  }

  private fun setObserevers() {
    sharedViewModel?.posterPackLoadListener?.observe(this,{
      isPosterPackLoaded = it
      if (it){
        binding?.ivDownload?.isEnabled = true
        binding?.ivDownload?.setImageDrawable(getDrawable(R.drawable.ic_fposter_download_menu_grey))
      }else{
        binding?.ivDownload?.isEnabled = false
        binding?.ivDownload?.setImageDrawable(getDrawable(R.drawable.ic_fposter_download_menu_grey_disabled))
      }


    })
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
    addFragment(binding?.container?.id, posterPackListFragment, true,true)

  }


  override fun onClick(v: View?) {
    super.onClick(v)
    when(v){
      binding?.ivBack->{
        onBackPressed()
      }
      binding?.ivHelp->{
        PosterHelpSheet().show(supportFragmentManager, PosterHelpSheet::class.java.name)
      }
      binding?.ivDownload->{
        if (isPosterPackLoaded){
         // posterPackListFragment.dataList?.forEach { it.isPurchased=true }
          addFragment(binding?.container?.id, PosterPackPurchasedListingFragment.newInstance(
              posterPackListFragment.dataList?.filter { it.isPurchased }?.toArrayList()
          ),true,true)
        }

      }
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
            binding?.ivDownload?.visible()
          }

          is PosterPackPurchasedListingFragment -> {
            binding?.tvTitle?.text = getString(R.string.purchased_posters)
            binding?.ivDownload?.gone()

          }
          is PosterListFragment -> {
            binding?.ivDownload?.gone()

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
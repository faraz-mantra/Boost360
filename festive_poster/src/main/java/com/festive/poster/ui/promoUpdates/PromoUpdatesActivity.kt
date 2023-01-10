package com.festive.poster.ui.promoUpdates

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.festive.poster.R
import com.festive.poster.base.AppBaseActivity
import com.festive.poster.databinding.ActivityPromoUpdatesBinding
import com.festive.poster.utils.WebEngageController
import com.festive.poster.viewmodels.PromoUpdatesViewModel
import com.framework.base.setFragmentType
import com.framework.pref.UserSessionManager
import com.framework.utils.setStatusBarColor
import com.framework.webengageconstant.*

class PromoUpdatesActivity : AppBaseActivity<ActivityPromoUpdatesBinding, PromoUpdatesViewModel>() {

  private var session: UserSessionManager? = null
  private var promoLandingPageBundle = Bundle()

  override fun getLayout(): Int {
    return R.layout.activity_promo_updates
  }

  override fun getViewModelClass(): Class<PromoUpdatesViewModel> {
    return PromoUpdatesViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    session = UserSessionManager(this)
    fetchDataFromServer()
    observeFragmentStack()
    setOnClickListener(binding?.ivToolbarBack, binding?.ivStore, binding?.ivLove)
    addFragmentReplace(binding?.container?.id, PromoLandingPageFragment.newInstance(promoLandingPageBundle), true)
  }

  private fun fetchDataFromServer() {

  }

  companion object {
    fun launchActivity(activity: Activity) {
      activity.startActivity(Intent(activity, PromoUpdatesActivity::class.java))
    }
  }


  override fun onResume() {
    super.onResume()
    setStatusBarColor(R.color.toolbar_bg)
  }

  override fun onClick(v: View?) {
    super.onClick(v)
    when (v) {
      binding?.ivToolbarBack -> {
        onBackPressed()
      }
      binding?.ivLove -> {
        WebEngageController.trackEvent(UPDATE_STUDIO_VIEW_FAVOURITES_PAGE_CLICK, CLICK, TO_BE_ADDED)
        addFragmentReplace(binding?.container?.id, FavouriteListFragment.newInstance(), true, showAnim = true)
      }
      binding?.ivStore -> {
//        WebEngageController.trackEvent(Promotional_Update_Posted_Updates_Click)
        WebEngageController.trackEvent(Update_studio_view_past_updates_click)
        val intent = Intent(this, Class.forName("com.appservice.ui.updatesBusiness.UpdateBusinessContainerActivity"))
        intent.setFragmentType("PAST_UPDATES")
        startActivity(intent)
      }
    }
  }


  override fun onBackPressed() {
    if (getTopFragment() == null)
      finish()
    else
      super.onBackPressed()
  }

  private fun observeFragmentStack() {
    supportFragmentManager.addOnBackStackChangedListener {
      binding?.ivLove?.isVisible = true
      binding?.ivStore?.isVisible = true
      when (getTopFragment()) {
        is PromoLandingPageFragment -> {
          binding?.tvToolbarTitle?.text = getString(R.string.update_studios)
        }
        is BrowseAllFragment -> {
          binding?.tvToolbarTitle?.text = getString(R.string.browse_all)
        }
        is FavouriteListFragment -> {
          binding?.tvToolbarTitle?.text = getString(R.string.favourites)
          binding?.ivLove?.isVisible = false
          binding?.ivStore?.isVisible = false
        }
      }
    }
  }
}
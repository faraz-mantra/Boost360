package com.festive.poster.ui.promoUpdates

import android.graphics.BlendMode
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.festive.poster.R
import com.festive.poster.base.AppBaseActivity
import com.festive.poster.constant.Constants
import com.festive.poster.constant.FragmentType
import com.festive.poster.databinding.ActivityPromoUpdatesBinding
import com.festive.poster.models.promoModele.SocialConnModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.ui.promoUpdates.pastUpdates.startFragmentPastUpdatesContainerActivity
import com.festive.poster.utils.WebEngageController
import com.festive.poster.viewmodels.FestivePosterSharedViewModel
import com.festive.poster.viewmodels.PromoUpdatesViewModel
import com.framework.models.BaseViewModel
import com.framework.pref.UserSessionManager
import com.framework.utils.setStatusBarColor
import com.framework.webengageconstant.Post_Promotional_Update_Click
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class PromoUpdatesActivity : AppBaseActivity<ActivityPromoUpdatesBinding, PromoUpdatesViewModel>() {

    private var session: UserSessionManager?=null

    override fun getLayout(): Int {
        return R.layout.activity_promo_updates
    }

    override fun getViewModelClass(): Class<PromoUpdatesViewModel> {
        return PromoUpdatesViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()

        WebEngageController.trackEvent(Post_Promotional_Update_Click)
        session = UserSessionManager(this)
        // sharedViewModel?.shouldRefresh=true
        fetchDataFromServer()

        observeFragmentStack()
        setOnClickListener(binding?.ivToolbarBack, binding?.ivStore, binding?.ivLove)
        addFragmentReplace(binding?.container?.id, PromoLandingPageFragment.newInstance(), true)
    }

    private fun fetchDataFromServer() {
        viewModel.getTodaysPickData(Constants.PROMO_FEATURE_CODE,session?.fPID, session?.fpTag)
        viewModel.getBrowseAllData(Constants.PROMO_FEATURE_CODE,session?.fPID, session?.fpTag)
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
                addFragmentReplace(binding?.container?.id, FavouriteListFragment.newInstance(), true, showAnim = true)
            }
            binding?.ivStore -> {
                startFragmentPastUpdatesContainerActivity(this, type = FragmentType.UPDATES_LISTING_FRAGMENT, bundle = Bundle())
            }
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        if (getTopFragment() == null) finish()
    }

    fun observeFragmentStack() {
        supportFragmentManager.addOnBackStackChangedListener {
            binding?.ivLove?.isVisible =true

            when (getTopFragment()) {
                is PromoLandingPageFragment -> {
                    binding?.tvToolbarTitle?.text = getString(R.string.update_studios)
                }
                is BrowseAllFragment -> {
                    binding?.tvToolbarTitle?.text = getString(R.string.browse_all)
                }
                is FavouriteListFragment->{
                    binding?.tvToolbarTitle?.text = getString(R.string.favourites)

                    binding?.ivLove?.isVisible =false
                }
            }
        }
    }
}
package com.festive.poster.ui.promoUpdates

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.festive.poster.R
import com.festive.poster.base.AppBaseActivity
import com.festive.poster.constant.FragmentType
import com.festive.poster.databinding.ActivityPromoUpdatesBinding
import com.festive.poster.ui.promoUpdates.pastUpdates.startFragmentPastUpdatesContainerActivity
import com.festive.poster.utils.WebEngageController
import com.festive.poster.viewmodels.FestivePosterSharedViewModel
import com.framework.models.BaseViewModel
import com.framework.utils.setStatusBarColor
import com.framework.webengageconstant.Post_Promotional_Update_Click

class PromoUpdatesActivity : AppBaseActivity<ActivityPromoUpdatesBinding, BaseViewModel>() {

    private var sharedViewModel: FestivePosterSharedViewModel?=null

    override fun getLayout(): Int {
        return R.layout.activity_promo_updates
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()

        WebEngageController.trackEvent(Post_Promotional_Update_Click)

        sharedViewModel = ViewModelProvider(this).get(FestivePosterSharedViewModel::class.java)
       // sharedViewModel?.shouldRefresh=true
        observeFragmentStack()
        setOnClickListener(binding?.ivToolbarBack, binding?.ivPastPromoUpdates)
        addFragmentReplace(binding?.container?.id, PromoLandingPageFragment.newInstance(), true)
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
            binding?.ivPastPromoUpdates -> {
                startFragmentPastUpdatesContainerActivity(this, type = FragmentType.UPDATES_LISTING_FRAGMENT, bundle = Bundle())
            }
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        if (getTopFragment()==null) finish()
    }

    fun observeFragmentStack(){
        supportFragmentManager.addOnBackStackChangedListener {
            when(getTopFragment()){
               is PromoLandingPageFragment->{
                   binding?.tvToolbarTitle?.text = getString(R.string.update_studios)
               }
                is BrowseAllFragment->{
                    binding?.tvToolbarTitle?.text = getString(R.string.browse_all)

                }
            }
        }
    }
}
package com.festive.poster.ui.promoUpdates

import android.graphics.BlendMode
import android.graphics.PorterDuff
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.festive.poster.R
import com.festive.poster.base.AppBaseActivity
import com.festive.poster.databinding.ActivityPromoUpdatesBinding
import com.festive.poster.models.promoModele.SocialConnModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.utils.WebEngageController
import com.festive.poster.viewmodels.FestivePosterSharedViewModel
import com.framework.models.BaseViewModel
import com.framework.utils.setStatusBarColor
import com.framework.webengageconstant.Post_Promotional_Update_Click
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

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
        setOnClickListener(binding?.ivToolbarBack)
        addFragmentReplace(binding?.container?.id,PromoLandingPageFragment.newInstance(),true)
    }


    override fun onResume() {
        super.onResume()
        setStatusBarColor(R.color.toolbar_bg)
    }
    override fun onClick(v: View?) {
        super.onClick(v)
        when(v){
            binding?.ivToolbarBack->{
                onBackPressed()
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
                   binding?.tvToolbarTitle?.text = getString(R.string.promo_updates)
               }
                is BrowseAllFragment->{
                    binding?.tvToolbarTitle?.text = getString(R.string.browse_all)

                }
            }
        }
    }
}
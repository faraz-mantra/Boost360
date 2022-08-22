package com.festive.poster.ui.promoUpdates.intro

import android.content.Intent
import android.graphics.BlendMode
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.festive.poster.R
import com.festive.poster.base.AppBaseActivity
import com.festive.poster.constant.Constants
import com.festive.poster.constant.FragmentType
import com.festive.poster.databinding.ActivityPromoUpdatesBinding
import com.festive.poster.databinding.ActivityUpdateStudioIntroBinding
import com.festive.poster.models.IntroUpdateStudioItem
import com.festive.poster.models.promoModele.SocialConnModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.utils.WebEngageController
import com.festive.poster.viewmodels.FestivePosterSharedViewModel
import com.festive.poster.viewmodels.PromoUpdatesViewModel
import com.framework.base.setFragmentType
import com.framework.models.BaseViewModel
import com.framework.pref.UserSessionManager
import com.framework.utils.setStatusBarColor
import com.framework.webengageconstant.Post_Promotional_Update_Click
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.math.abs

class UpdateStudioIntroActivity : AppBaseActivity<ActivityUpdateStudioIntroBinding, PromoUpdatesViewModel>() {

    private var session: UserSessionManager?=null

    override fun getLayout(): Int {
        return R.layout.activity_update_studio_intro
    }

    override fun getViewModelClass(): Class<PromoUpdatesViewModel> {
        return PromoUpdatesViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        setupSlider()

    }

    private fun setupSlider() {
        val data = arrayListOf(
            IntroUpdateStudioItem(
                getString(R.string.one_update_multiple_platforms),
            R.drawable.update_studio_intro1,
            getString(R.string.you_can_post_on_multiple_platforms_with_a_single_tap)),

            IntroUpdateStudioItem(
                getString(R.string.your_brand_templates_for_quick_posting),
                R.drawable.update_studio_intro3,
                getString(R.string.this_premium_feature_is_available_in_online_classic_online_advanced_packs)),

            IntroUpdateStudioItem(
                getString(R.string.post_free_updates_using_create_tab),
                R.drawable.update_studio_intro3,
                getString(R.string.you_can_continue_to_post_good_old_updates_using_the_create_tab_on_top)),
            )

        val adapter = AppBaseRecyclerViewAdapter(this,data,null)
        binding?.slider?.adapter=adapter
        binding?.slider?.setPageTransformer(SlideBottomTransformer())
    }


    override fun onResume() {
        super.onResume()
        setStatusBarColor(R.color.toolbar_bg)
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v) {

        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        if (getTopFragment() == null) finish()
    }


    class SlideBottomTransformer:ViewPager2.PageTransformer{
        override fun transformPage(page: View, position: Float) {
            page.apply {
                when{

                    position==0f->{
                        translationY=0f
                    }
                    else->{
                        translationY=300f* abs(position)
                    }


                }
            }
        }

    }

}
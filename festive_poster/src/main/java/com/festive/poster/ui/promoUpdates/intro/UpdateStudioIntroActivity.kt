package com.festive.poster.ui.promoUpdates.intro

import android.content.Intent
import android.graphics.BlendMode
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.festive.poster.ui.promoUpdates.PromoUpdatesActivity
import com.festive.poster.utils.WebEngageController
import com.festive.poster.viewmodels.FestivePosterSharedViewModel
import com.festive.poster.viewmodels.PromoUpdatesViewModel
import com.framework.base.setFragmentType
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.framework.pref.UserSessionManager
import com.framework.utils.setStatusBarColor
import com.framework.webengageconstant.*
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.math.abs

class UpdateStudioIntroActivity : AppBaseActivity<ActivityUpdateStudioIntroBinding, PromoUpdatesViewModel>() {

    private lateinit var sliderRunnable: Runnable
    private var session: UserSessionManager?=null

    override fun getLayout(): Int {
        return R.layout.activity_update_studio_intro
    }

    override fun getViewModelClass(): Class<PromoUpdatesViewModel> {
        return PromoUpdatesViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            binding!!.constraintLayout3.transitionToEnd()
        },1500)
        setupSlider()
        setOnClickListener(binding?.btnUpdateStudio,binding?.btnNext)

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
        TabLayoutMediator(binding!!.sliderIndicator,binding!!.slider)
        { tab, position ->
        }.attach()

        binding?.slider?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when(position){
                    0->{
                        WebEngageController.trackEvent(Update_studio_intro_1_loaded)
                        binding?.btnNext?.visible()
                        binding?.btnUpdateStudio?.gone()
                    }
                    1->{
                        WebEngageController.trackEvent(Update_studio_intro_2_loaded)
                        binding?.btnNext?.visible()
                        binding?.btnUpdateStudio?.gone()
                    }
                    2->{
                        WebEngageController.trackEvent(Update_studio_intro_3_loaded)
                        binding?.btnNext?.gone()
                        binding?.btnUpdateStudio?.visible()
                    }
                }
            }
        })

        autoSlide()
    }

    private fun autoSlide() {
        sliderRunnable = Runnable {
            if (binding!!.slider.currentItem>=2){
                binding!!.slider.currentItem=0
            }else{
                binding!!.slider.currentItem = binding!!.slider.currentItem+1
            }

            Handler(Looper.getMainLooper()).postDelayed(sliderRunnable,
                3000)
        }
        Handler(Looper.getMainLooper()).postDelayed(sliderRunnable,
            2500)
    }


    override fun onResume() {
        super.onResume()
        setStatusBarColor(R.color.color_4a4a4a_jio_ec008c)
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v) {
            binding?.btnNext->{
                val currentItem = binding!!.slider.currentItem
                if (currentItem==0){
                    WebEngageController.trackEvent(Update_studio_intro_1_next_click)
                }else if (currentItem==1){
                    WebEngageController.trackEvent(Update_studio_intro_2_next_click)
                }
                binding!!.slider.currentItem = currentItem+1
            }
            binding?.btnUpdateStudio->{
                WebEngageController.trackEvent(Update_studio_intro_3_next_click)
                PromoUpdatesActivity.launchActivity(this)
                finish()
            }
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
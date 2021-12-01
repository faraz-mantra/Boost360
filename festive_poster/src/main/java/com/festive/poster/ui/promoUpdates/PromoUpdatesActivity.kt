package com.festive.poster.ui.promoUpdates

import androidx.appcompat.content.res.AppCompatResources
import androidx.viewpager2.widget.ViewPager2
import com.festive.poster.R
import com.festive.poster.base.AppBaseActivity
import com.festive.poster.databinding.ActivityPromoUpdatesBinding
import com.festive.poster.models.promoModele.SocialConnModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.framework.models.BaseViewModel
import com.google.android.material.tabs.TabLayoutMediator

class PromoUpdatesActivity : AppBaseActivity<ActivityPromoUpdatesBinding, BaseViewModel>() {

    override fun getLayout(): Int {
        return R.layout.activity_promo_updates
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        setupView()
        setupViewPager()
    }

    private fun setupView() {

        val soicalConnList = arrayListOf(
            SocialConnModel(
            content = "1000+ people are on twitter"
        ), SocialConnModel(
            content = "1000+ people are on facebook"
        )
        )

        val socialconnadapter = AppBaseRecyclerViewAdapter(this,soicalConnList)
        binding?.vpSocialConn?.adapter = socialconnadapter
        binding?.ivLeftArrow?.setOnClickListener {
            binding?.vpSocialConn?.postDelayed(Runnable {
                binding?.vpSocialConn?.currentItem = binding?.vpSocialConn?.currentItem?:1 - 1;

            }, 100)
        }
        binding?.ivRightArrow?.setOnClickListener {
            binding?.vpSocialConn?.postDelayed(Runnable {
                binding?.vpSocialConn?.currentItem = binding?.vpSocialConn?.currentItem?:0 + 1;

            }, 100)
        }

    }

    private fun setupViewPager() {
        val fragmentList = arrayListOf(
            TodaysPickFragment.newInstance(),
            BrowseAllTemplateFragment.newInstance(),
            CreatePostFragment.newInstance()
        )
        val viewPagerAdapter = TabAdapter(fragmentList, this)
        binding?.viewPager?.apply {
            isUserInputEnabled = false
            adapter = viewPagerAdapter
            offscreenPageLimit = 3
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {

                    when(position){
                        0->{


                        }
                        1->{


                        }
                    }
                }
            })
        }


        // New way of interaction with TabLayout and page title setting
        TabLayoutMediator(binding!!.tabLayout, binding!!.viewPager) { currentTab, currentPosition ->
            currentTab.icon=when(currentPosition){
                0->AppCompatResources.getDrawable(this,R.drawable.ic_sundim)
                1->AppCompatResources.getDrawable(this,R.drawable.ic_circlesfour)
                2->AppCompatResources.getDrawable(this,R.drawable.ic_magnifyingglass)
                else -> null
            }
            currentTab.text = when (currentPosition) {
                0 -> getString(R.string.todays_pick)
                1 -> getString(R.string.browser_all)
                2 -> getString(R.string.create_post)
                else -> ""
            }
        }.attach()


    }
}
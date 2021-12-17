package com.festive.poster.ui.promoUpdates

import android.graphics.BlendMode
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.festive.poster.R
import com.festive.poster.base.AppBaseFragment
import com.festive.poster.databinding.FragmentPromoLandingPageBinding
import com.festive.poster.models.PosterPackModel
import com.festive.poster.models.promoModele.SocialConnModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.ui.promoUpdates.bottomSheet.SubscribePlanBottomSheet
import com.festive.poster.utils.WebEngageController
import com.framework.base.BaseActivity
import com.framework.models.BaseViewModel
import com.framework.utils.setColorFilter2
import com.framework.webengageconstant.Post_Promotional_Update_Click
import com.framework.webengageconstant.Promotional_Update_Browse_All_Click
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class PromoLandingPageFragment : AppBaseFragment<FragmentPromoLandingPageBinding, BaseViewModel>(),TodaysPickFragment.Callbacks {

    val browseTabFragment =BrowseTabFragment.newInstance()

    override fun getLayout(): Int {
        return R.layout.fragment_promo_landing_page
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    companion object {
        fun newInstance(bundle: Bundle = Bundle()): PromoLandingPageFragment {
            val fragment =PromoLandingPageFragment()
            fragment.arguments = bundle
            return fragment
        }


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

        val socialconnadapter = AppBaseRecyclerViewAdapter(requireActivity() as BaseActivity<*, *>,soicalConnList)
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
            TodaysPickFragment.newInstance(callbacks = this),
            browseTabFragment,
            CreatePostFragment.newInstance()
        )
        val viewPagerAdapter = TabAdapter(fragmentList, this)
        binding?.viewPager?.apply {
            isUserInputEnabled = false
            adapter = viewPagerAdapter
            offscreenPageLimit = 1
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {

                    when(position){
                        0->{


                        }
                        1->{
                            WebEngageController.trackEvent(Promotional_Update_Browse_All_Click)


                        }
                    }
                }
            })
        }


        binding?.tabLayout?.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val tabIconColor: Int =
                    ContextCompat.getColor(requireActivity(), R.color.colorPrimary)
                tab!!.icon!!.setColorFilter2(tabIconColor,BlendMode.SRC_IN, PorterDuff.Mode.SRC_IN)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val tabIconColor: Int =
                    ContextCompat.getColor(requireActivity(), R.color.colorB3B3B3)
                tab!!.icon!!.setColorFilter2(tabIconColor,BlendMode.SRC_IN, PorterDuff.Mode.SRC_IN)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })

        // New way of interaction with TabLayout and page title setting
        TabLayoutMediator(binding!!.tabLayout, binding!!.viewPager) { currentTab, currentPosition ->
            currentTab.icon=when(currentPosition){
                0->AppCompatResources.getDrawable(requireActivity(),R.drawable.ic_sundim)
                1->AppCompatResources.getDrawable(requireActivity(),R.drawable.ic_circlesfour)
                2->AppCompatResources.getDrawable(requireActivity(),R.drawable.ic_magnifyingglass)
                else -> null
            }
            currentTab.text = when (currentPosition) {
                0 -> getString(R.string.for_today)
                1 -> getString(R.string.browse)
                2 -> getString(R.string.find)
                else -> ""
            }
        }.attach()


    }

    override fun onDataLoaded(data: ArrayList<PosterPackModel>) {
       // browseTabFragment.setRealData(data)
    }
}
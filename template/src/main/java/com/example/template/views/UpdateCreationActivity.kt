package com.example.template.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.dashboard.base.AppBaseActivity
import com.example.template.R
import com.example.template.databinding.ActivityUpdateCreationBinding
import com.framework.models.BaseViewModel
import com.google.android.material.tabs.TabLayoutMediator

class UpdateCreationActivity : AppBaseActivity<ActivityUpdateCreationBinding, BaseViewModel>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getLayout(): Int {
        return R.layout.activity_update_creation
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        setupViewPager()
    }

    private fun setupViewPager() {
        val fragmentList = arrayListOf(
            TodaysPickFragment.newInstance(),
            BrowseAllTemplateFragment.newInstance(),
            CreatePostFragment.newInstance()

        )
        val viewPagerAdapter = TabAdapter(fragmentList, this)
        binding?.viewPager?.apply {
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
            when(currentPosition){
                1->{
                    currentTab.setCustomView(R.layout.tab_item_with_count)
                }
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
package com.appservice.ui.catalog.catalogProduct.listing

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentServiceHomeContainerBinding
import com.appservice.ui.catalog.startFragmentActivity
import com.appservice.ui.staffs.UserSession
import com.framework.models.BaseViewModel

class FragmentProductHome : AppBaseFragment<FragmentServiceHomeContainerBinding, BaseViewModel>() {
    private val NUM_PAGES = 2
    private var isNonPhysicalExperience: Boolean? = null
    private var currencyType: String? = "INR"
    private var fpId: String? = null
    private var fpTag: String? = null
    private var clientId: String? = null
    private var externalSourceId: String? = null
    private var applicationId: String? = null
    private var userProfileId: String? = null
    override fun getLayout(): Int {
        return R.layout.fragment_service_home_container
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    companion object {
        fun newInstance(): FragmentProductHome {
            return FragmentProductHome()
        }

    }

    override fun onCreateView() {
        super.onCreateView()
        // The pager adapter, which provides the pages to the view pager widget.
        getBundleData()
        val pagerAdapter = ScreenSlidePagerAdapter(childFragmentManager)
        binding?.pager?.adapter = pagerAdapter
        binding?.tabLayout?.setupWithViewPager(binding?.pager)


    }

    public fun setTabTitle(title: String, tabIndex: Int) {
        binding?.tabLayout?.getTabAt(tabIndex)?.text = title
    }

    private fun getBundleData() {
        isNonPhysicalExperience = arguments?.getBoolean(IntentConstant.NON_PHYSICAL_EXP_CODE.name)
        currencyType = arguments?.getString(IntentConstant.CURRENCY_TYPE.name) ?: "INR"
        fpId = arguments?.getString(IntentConstant.FP_ID.name)
        fpTag = arguments?.getString(IntentConstant.FP_TAG.name)
        clientId = arguments?.getString(IntentConstant.CLIENT_ID.name)
        externalSourceId = arguments?.getString(IntentConstant.EXTERNAL_SOURCE_ID.name)
        applicationId = arguments?.getString(IntentConstant.APPLICATION_ID.name)
        userProfileId = arguments?.getString(IntentConstant.USER_PROFILE_ID.name)
        UserSession.fpTag = fpTag
        UserSession.clientId = clientId
        UserSession.fpId = fpId
    }

    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getCount(): Int = NUM_PAGES

        override fun getItem(position: Int): Fragment {

            return when (position) {
                0 -> FragmentProductListing.newInstance(currencyType, fpId, fpTag, clientId, externalSourceId, applicationId, userProfileId)
                1 -> FragmentProductCategory.newInstance(fpTag)
                else -> requireParentFragment()
            }
        }

        override fun getPageTitle(position: Int): CharSequence {
            return when (position) {
                0 -> "PRODUCTS"
                1 -> "CATEGORIES"
                else -> {
                    return ""
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_product_listing, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_product_configuration -> {
                startFragmentActivity(FragmentType.ECOMMERCE_SETTINGS)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}

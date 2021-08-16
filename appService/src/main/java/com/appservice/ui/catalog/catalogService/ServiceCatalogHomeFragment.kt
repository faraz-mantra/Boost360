package com.appservice.ui.catalog.catalogService

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.appservice.R
import com.appservice.appointment.ui.CreateCategoryFragment
import com.appservice.base.AppBaseFragment
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentServiceHomeContainerBinding
import com.appservice.ui.catalog.catalogService.listing.ServiceListingFragment
import com.appservice.ui.staffs.UserSession
import com.framework.models.BaseViewModel

class ServiceCatalogHomeFragment : AppBaseFragment<FragmentServiceHomeContainerBinding, BaseViewModel>() {
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
        fun newInstance(): ServiceCatalogHomeFragment {
            return ServiceCatalogHomeFragment()
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

    @SuppressLint("WrongConstant")
    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getCount(): Int = NUM_PAGES

        override fun getItem(position: Int): Fragment {

            return when (position) {
                0 -> ServiceListingFragment.newInstance()
                1 -> CreateCategoryFragment.newInstance(fpTag)
                else -> requireParentFragment()
            }
        }
        override fun getPageTitle(position: Int): CharSequence {
            return when (position) {
                0 -> "SERVICES"
                1 -> "CATEGORIES"
                else -> {
                    return ""
                }
            }
        }
    }

}

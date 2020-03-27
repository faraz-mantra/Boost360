package com.onboarding.nowfloats.ui.registration

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.databinding.ViewDataBinding
import com.framework.base.BaseFragment
import com.framework.exceptions.IllegalFragmentTypeException
import com.framework.views.DotProgressBar
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.constant.FragmentType
import com.onboarding.nowfloats.constant.IntentConstant
import com.onboarding.nowfloats.extensions.addInt
import com.onboarding.nowfloats.extensions.addParcelable
import com.onboarding.nowfloats.extensions.getInt
import com.onboarding.nowfloats.extensions.getParcelable
import com.onboarding.nowfloats.model.RequestFloatsModel
import com.onboarding.nowfloats.model.category.CategoryDataModel
import com.onboarding.nowfloats.model.channel.ChannelModel
import com.onboarding.nowfloats.model.contactinfo.ContactInfo
import com.onboarding.nowfloats.ui.startFragmentActivity

open class BaseRegistrationFragment<binding : ViewDataBinding> : BaseFragment<binding, ContactInfo>() {

    protected val channels: ArrayList<ChannelModel>
        get() {
            return requestFloatsModel?.channels ?: ArrayList()
        }

    protected val categoryDataModel: CategoryDataModel?
        get() {
            return requestFloatsModel?.categoryDataModel
        }

    protected var requestFloatsModel: RequestFloatsModel? = null

    protected var totalPages = 1
    protected var currentPage = 1

    override fun getLayout(): Int {
        return when (this) {
            is RegistrationBusinessContactInfoFragment -> R.layout.fragment_registration_business_contact_info
            is RegistrationBusinessWebsiteFragment -> R.layout.fragment_registration_business_website
            is RegistrationBusinessFacebookPageFragment -> R.layout.fragment_registration_business_facebook_page
            is RegistrationBusinessFacebookShopFragment -> R.layout.fragment_registration_business_facebook_shop
            is RegistrationBusinessTwitterDetailsFragment -> R.layout.fragment_registration_business_twitter_details
            is RegistrationBusinessWhatsAppFragment -> R.layout.fragment_registration_business_whatsapp
            is RegistrationBusinessApiFragment -> R.layout.fragment_registration_business_api
            is RegistrationCompleteFragment -> R.layout.fragment_registration_complete
            else -> throw IllegalFragmentTypeException()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        when (this) {
            is RegistrationCompleteFragment -> inflater.inflate(R.menu.menu_facebook_profile, menu)
        }
    }

    override fun getViewModelClass(): Class<ContactInfo> {
        return ContactInfo::class.java
    }

    override fun onCreateView() {
        requestFloatsModel = arguments?.getParcelable(IntentConstant.REQUEST_FLOATS_INTENT)
        currentPage = arguments?.getInt(IntentConstant.CURRENT_PAGES) ?: currentPage
        totalPages = arguments?.getInt(IntentConstant.TOTAL_PAGES) ?: totalPages
        if (this !is RegistrationCompleteFragment) {
            if (this !is RegistrationBusinessApiFragment) setToolbarTitle(resources.getString(R.string.step) + " $currentPage/$totalPages")
        }
    }

    fun getDotProgress(): DotProgressBar? {
        return DotProgressBar.Builder().setMargin(0).setAnimationDuration(800)
            .setDotBackground(R.drawable.ic_dot).setMaxScale(.7f).setMinScale(0.3f)
            .setNumberOfDots(3).setdotRadius(8).build(baseActivity)
    }

    protected fun gotoBusinessWebsite() {
        startFragmentActivity(FragmentType.REGISTRATION_BUSINESS_WEBSITE, getBundle())
    }

    protected fun gotoFacebookShop() {
        startFragmentActivity(FragmentType.REGISTRATION_BUSINESS_FACEBOOK_SHOP, getBundle())
    }

    protected fun gotoFacebookPage() {
        startFragmentActivity(FragmentType.REGISTRATION_BUSINESS_FACEBOOK_PAGE, getBundle())
    }

    protected fun gotoTwitterDetails() {
        startFragmentActivity(FragmentType.REGISTRATION_BUSINESS_TWITTER_DETAILS, getBundle())
    }

    protected fun gotoWhatsAppCallDetails() {
        startFragmentActivity(FragmentType.REGISTRATION_BUSINESS_WHATSAPP, getBundle())
    }

    protected fun gotoBusinessApiCallDetails() {
        startFragmentActivity(FragmentType.REGISTRATION_BUSINESS_API_CALL, getBundle())
    }

    protected fun gotoRegistrationComplete() {
        startFragmentActivity(FragmentType.REGISTRATION_COMPLETE, getBundle())
    }

    private fun getBundle(): Bundle {
        val bundle = Bundle()
        bundle.addParcelable(IntentConstant.REQUEST_FLOATS_INTENT, requestFloatsModel)
            .addInt(IntentConstant.TOTAL_PAGES, totalPages)
            .addInt(IntentConstant.CURRENT_PAGES, currentPage + 1)
        return bundle
    }
}
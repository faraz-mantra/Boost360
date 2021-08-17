package com.appservice.offers

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.appservice.R
import com.appservice.base.AppBaseActivity
import com.appservice.constant.FragmentType
import com.appservice.constant.IntentConstant
import com.appservice.offers.additionalinfo.AdditionalInfoFragment
import com.appservice.offers.details.AddNewOfferFragment
import com.appservice.offers.offerlisting.OfferListingFragment
import com.appservice.staffs.ui.setFragmentType
import com.appservice.ui.staffs.UserSession
import com.framework.base.BaseFragment
import com.framework.base.FRAGMENT_TYPE
import com.framework.databinding.ActivityFragmentContainerBinding
import com.framework.exceptions.IllegalFragmentTypeException
import com.framework.models.BaseViewModel
import com.framework.pref.UserSessionManager
import com.framework.views.customViews.CustomToolbar

class FragmentOffersContainerActivity : AppBaseActivity<ActivityFragmentContainerBinding, BaseViewModel>() {
    private var fragmentType: FragmentType? = null
    private var addNewOfferFragment: AddNewOfferFragment? = null
    private var additionalInfoFragment: AdditionalInfoFragment? = null
    private var offerListingFragment: OfferListingFragment? = null
    override fun getLayout(): Int {
        return R.layout.activity_fragment_container
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun getToolbarTitleSize(): Float? {
        return resources.getDimension(R.dimen.heading_7)
    }

    override fun getNavIconScale(): Float {
        return 1.0f
    }

    override fun getToolbar(): CustomToolbar? {
        return binding?.appBarLayout?.toolbar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        intent?.extras?.getInt(FRAGMENT_TYPE)?.let { fragmentType = FragmentType.values()[it] }
        super.onCreate(savedInstanceState)
    }

    override fun getToolbarBackgroundColor(): Int? {
        return ContextCompat.getColor(this, R.color.yellow_ffb900)
    }

    override fun getToolbarTitleColor(): Int? {
        return ContextCompat.getColor(this, R.color.white)
    }

    override fun customTheme(): Int? {
        return when (fragmentType) {
            FragmentType.OFFER_ADDITIONAL_INFO -> R.style.OffersThemeBase_AdditionalInfo
            else -> R.style.OffersThemeBase
        }
    }
    override fun onCreateView() {
        super.onCreateView()
        getBundle()
        setFragment()
    }

    private fun getBundle() {
        val userSessionManager = UserSessionManager(this)
        intent.getStringExtra(IntentConstant.FP_TAG.name)?.let {
            UserSession.apply {
                fpTag = userSessionManager.fpTag
                fpId = userSessionManager.fPID
                customerID = clientId
                clientId = customerID
            }
        }
    }

    override fun getNavigationIcon(): Drawable? {
        return ContextCompat.getDrawable(this, R.drawable.ic_back_arrow_new)
    }

    private fun shouldAddToBackStack(): Boolean {
        return when (fragmentType) {
            else -> false
        }
    }

    private fun setFragment() {
        val fragment = getFragmentInstance(fragmentType)
        fragment?.arguments = intent.extras
        binding?.container?.id.let { addFragmentReplace(it, fragment, shouldAddToBackStack()) }
    }

    private fun getFragmentInstance(type: FragmentType?): BaseFragment<*, *>? {
        return when (type) {
            FragmentType.OFFER_LISTING_FRAGMENT -> {
                offerListingFragment = OfferListingFragment.newInstance()
                offerListingFragment
            }
            FragmentType.OFFER_DETAILS_FRAGMENT -> {
                addNewOfferFragment = AddNewOfferFragment.newInstance()
                addNewOfferFragment
            }
            FragmentType.OFFER_ADDITIONAL_INFO -> {
                additionalInfoFragment = AdditionalInfoFragment.newInstance()
                additionalInfoFragment
            }
            else -> throw IllegalFragmentTypeException()
        }
    }

    override fun getToolbarTitleGravity(): Int {
        return Gravity.START
    }

    override fun getToolbarTitle(): String? {
        return when (fragmentType) {
            FragmentType.OFFER_DETAILS_FRAGMENT -> getString(R.string.add_a_new_offer)
            FragmentType.OFFER_ADDITIONAL_INFO -> getString(R.string.additional_info)
            FragmentType.OFFER_LISTING_FRAGMENT -> getString(R.string.offers)
            else -> super.getToolbarTitle()

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        additionalInfoFragment?.onActivityResult(requestCode,resultCode,data)
        addNewOfferFragment?.onActivityResult(requestCode,resultCode,data)
        offerListingFragment?.onActivityResult(requestCode,resultCode,data)
    }
}

fun Fragment.startOfferFragmentActivity(activity: Activity, type: FragmentType, bundle: Bundle = Bundle(), clearTop: Boolean = false, isResult: Boolean? = false, requestCode: Int = 101) {
    val intent = Intent(activity, FragmentOffersContainerActivity::class.java)
    intent.putExtras(bundle)
    intent.setFragmentType(type)
    if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    if (isResult?.not() == true) activity.startActivity(intent) else activity.startActivityForResult(intent, requestCode)
}

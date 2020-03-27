package com.onboarding.nowfloats.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.framework.base.BaseFragment
import com.framework.base.FRAGMENT_TYPE
import com.framework.databinding.ActivityFragmentContainerBinding
import com.framework.exceptions.IllegalFragmentTypeException
import com.framework.models.BaseViewModel
import com.framework.views.customViews.CustomToolbar
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.base.AppBaseActivity
import com.onboarding.nowfloats.constant.FragmentType
import com.onboarding.nowfloats.ui.category.CategorySelectorFragment
import com.onboarding.nowfloats.ui.channel.ChannelPickerFragment
import com.onboarding.nowfloats.ui.registration.*

open class AppFragmentContainerActivity : AppBaseActivity<ActivityFragmentContainerBinding, BaseViewModel>() {

    private var type: FragmentType? = null
    private var exitToast: Toast? = null

    override fun getToolbarBackgroundColor(): Int? {
        return when (type) {
            FragmentType.CHANNEL_PICKER -> ContextCompat.getColor(this, R.color.white_two)
            FragmentType.CATEGORY_VIEW,
            FragmentType.REGISTRATION_BUSINESS_BASIC_DETAILS -> ContextCompat.getColor(this, R.color.white)

            FragmentType.REGISTRATION_BUSINESS_WEBSITE,
            FragmentType.REGISTRATION_BUSINESS_WHATSAPP,
            FragmentType.REGISTRATION_BUSINESS_FACEBOOK_SHOP,
            FragmentType.REGISTRATION_BUSINESS_FACEBOOK_PAGE,
            FragmentType.REGISTRATION_BUSINESS_TWITTER_DETAILS -> ContextCompat.getColor(this, R.color.white_two)

            FragmentType.REGISTRATION_BUSINESS_API_CALL -> ContextCompat.getColor(this, R.color.white_four)
            else -> super.getToolbarBackgroundColor()
        }
    }

    override fun isVisibleBackButton(): Boolean {
        return when (type) {
            FragmentType.REGISTRATION_BUSINESS_API_CALL,
            FragmentType.REGISTRATION_COMPLETE -> false
            else -> super.isVisibleBackButton()
        }
    }

    override fun getToolbarTitleColor(): Int? {
        return when (type) {
            FragmentType.REGISTRATION_BUSINESS_BASIC_DETAILS,
            FragmentType.REGISTRATION_BUSINESS_WEBSITE,
            FragmentType.REGISTRATION_BUSINESS_FACEBOOK_SHOP,
            FragmentType.REGISTRATION_BUSINESS_FACEBOOK_PAGE,
            FragmentType.REGISTRATION_BUSINESS_WHATSAPP,
            FragmentType.REGISTRATION_BUSINESS_TWITTER_DETAILS ->
                ContextCompat.getColor(this, R.color.dodger_blue_two)
            else -> super.getToolbarTitleColor()
        }
    }

    override fun isHideToolbar(): Boolean {
        return when (type) {
            FragmentType.REGISTRATION_COMPLETE,
            FragmentType.REGISTRATION_BUSINESS_API_CALL -> true
            else -> super.isHideToolbar()
        }
    }

    override fun getToolbarTitle(): String? {
        return when (type) {
            else -> super.getToolbarTitle()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val toolbarMenu = menu ?: return super.onCreateOptionsMenu(menu)
        val menuRes = getMenuRes() ?: return super.onCreateOptionsMenu(menu)
        menuInflater.inflate(menuRes, toolbarMenu)
        return true
    }

    open fun getMenuRes(): Int? {
        return when (type) {
            else -> null
        }
    }


    override fun getLayout(): Int {
        return com.framework.R.layout.activity_fragment_container
    }

    override fun onCreateView() {
        super.onCreateView()
        intent?.extras?.getInt(FRAGMENT_TYPE)?.let { type = FragmentType.values()[it] }
        exitToast = makeText(this, R.string.press_again_exit, Toast.LENGTH_SHORT)
        setFragment()
    }

    override fun getToolbar(): CustomToolbar? {
        return binding?.appBarLayout?.toolbar
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    private fun setFragment() {
        val fragment = getFragmentInstance(type)
        fragment.arguments = intent.extras
        binding?.container?.id?.let { addFragmentReplace(it, fragment, shouldAddToBackStack()) }
    }

    private fun getFragmentInstance(type: FragmentType?): BaseFragment<*, *> {
        return when (type) {
            FragmentType.CATEGORY_VIEW -> CategorySelectorFragment.newInstance()
            FragmentType.CHANNEL_PICKER -> ChannelPickerFragment.newInstance()
            FragmentType.REGISTRATION_BUSINESS_BASIC_DETAILS -> RegistrationBusinessContactInfoFragment.newInstance()
            FragmentType.REGISTRATION_BUSINESS_WEBSITE -> RegistrationBusinessWebsiteFragment.newInstance()
            FragmentType.REGISTRATION_BUSINESS_FACEBOOK_PAGE -> RegistrationBusinessFacebookPageFragment.newInstance()
            FragmentType.REGISTRATION_BUSINESS_FACEBOOK_SHOP -> RegistrationBusinessFacebookShopFragment.newInstance()
            FragmentType.REGISTRATION_BUSINESS_TWITTER_DETAILS -> RegistrationBusinessTwitterDetailsFragment.newInstance()
            FragmentType.REGISTRATION_BUSINESS_WHATSAPP -> RegistrationBusinessWhatsAppFragment.newInstance()
            FragmentType.REGISTRATION_BUSINESS_API_CALL -> RegistrationBusinessApiFragment.newInstance()
            FragmentType.REGISTRATION_COMPLETE -> RegistrationCompleteFragment.newInstance()
            else -> throw IllegalFragmentTypeException()
        }
    }

    private fun shouldAddToBackStack(): Boolean {
        return when (type) {
            else -> false
        }
    }

//    override fun onBackPressed() {
//        when (type) {
//            FragmentType.REGISTRATION_BUSINESS_API_CALL,
//            FragmentType.REGISTRATION_COMPLETE -> showMessageBackPress()
//            else -> super.onBackPressed()
//        }
//    }

    private fun showMessageBackPress() {
//        if (exitToast?.view?.windowToken != null) {
//            this@AppFragmentContainerActivity.finish()
//            exitProcess(0)
//        } else exitToast?.show()
    }
}

fun Fragment.startFragmentActivity(
    type: FragmentType, bundle: Bundle = Bundle(),
    clearTop: Boolean = false
) {

    val intent = Intent(activity, AppFragmentContainerActivity::class.java)
    intent.putExtras(bundle)
    intent.setFragmentType(type)
    if (clearTop) Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    startActivity(intent)
}

fun AppCompatActivity.startFragmentActivity(
    type: FragmentType, bundle: Bundle = Bundle(),
    clearTop: Boolean = false
) {
    val intent = Intent(this, AppFragmentContainerActivity::class.java)
    intent.putExtras(bundle)
    intent.setFragmentType(type)
    if (clearTop) Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    startActivity(intent)
}

fun Intent.setFragmentType(type: FragmentType) {
    this.putExtra(FRAGMENT_TYPE, type.ordinal)
}
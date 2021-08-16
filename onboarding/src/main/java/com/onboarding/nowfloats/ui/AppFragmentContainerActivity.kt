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
import com.framework.utils.ConversionUtils
import com.framework.views.customViews.CustomToolbar
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.base.AppBaseActivity
import com.onboarding.nowfloats.constant.FragmentType
import com.onboarding.nowfloats.managers.NavigatorManager
import com.onboarding.nowfloats.model.navigator.ScreenModel
import com.onboarding.nowfloats.ui.category.CategorySelectorFragment
import com.onboarding.nowfloats.ui.channel.ChannelPickerFragment
import com.onboarding.nowfloats.ui.registration.*

open class AppFragmentContainerActivity : AppBaseActivity<ActivityFragmentContainerBinding, BaseViewModel>() {

  private var type: FragmentType? = null
  private var exitToast: Toast? = null

  override fun onCreateView() {
    super.onCreateView()
    intent?.extras?.getInt(FRAGMENT_TYPE)?.let { type = FragmentType.values()[it] }
    exitToast = makeText(this, R.string.press_again_exit, Toast.LENGTH_SHORT)
    setFragment()
  }

  private var categorySelectorFragment: CategorySelectorFragment? = null
  private var channelPickerFragment: ChannelPickerFragment? = null
  private var registrationBusinessContactInfoFragment: RegistrationBusinessContactInfoFragment? = null
  private var registrationBusinessWebsiteFragment: RegistrationBusinessWebsiteFragment? = null
  private var registrationBusinessGoogleBusinessFragment: RegistrationBusinessGoogleBusinessFragment? = null
  private var registrationBusinessFacebookPageFragment: RegistrationBusinessFacebookPageFragment? = null
  private var registrationBusinessFacebookShopFragment: RegistrationBusinessFacebookShopFragment? = null
  private var registrationBusinessTwitterDetailsFragment: RegistrationBusinessTwitterDetailsFragment? = null
  private var registrationBusinessWhatsAppFragment: RegistrationBusinessWhatsAppFragment? = null
  private var registrationBusinessApiFragment: RegistrationBusinessApiFragment? = null
  private var registrationCompleteFragment: RegistrationCompleteFragment? = null


  override fun getToolbarBackgroundColor(): Int? {
    return when (type) {
      FragmentType.CHANNEL_PICKER -> ContextCompat.getColor(this, R.color.white_two)
      FragmentType.CATEGORY_VIEW,
      FragmentType.REGISTRATION_BUSINESS_BASIC_DETAILS -> ContextCompat.getColor(this, R.color.white)
      FragmentType.REGISTRATION_BUSINESS_WEBSITE,
      FragmentType.REGISTRATION_BUSINESS_WHATSAPP,
      FragmentType.REGISTRATION_BUSINESS_GOOGLE_PAGE,
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
      FragmentType.REGISTRATION_BUSINESS_GOOGLE_PAGE,
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

  override fun getToolbar(): CustomToolbar? {
    return binding?.appBarLayout?.toolbar
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  private fun setFragment() {
    val fragment = getFragmentInstance(type)
    fragment?.arguments = intent.extras
    binding?.container?.id?.let { addFragmentReplace(it, fragment, shouldAddToBackStack()) }
  }

  private fun getFragmentInstance(type: FragmentType?): BaseFragment<*, *>? {
    return when (type) {
      FragmentType.CATEGORY_VIEW -> {
        categorySelectorFragment = CategorySelectorFragment.newInstance()
        categorySelectorFragment
      }
      FragmentType.CHANNEL_PICKER -> {
        channelPickerFragment = ChannelPickerFragment.newInstance()
        channelPickerFragment
      }
      FragmentType.REGISTRATION_BUSINESS_BASIC_DETAILS -> {
        registrationBusinessContactInfoFragment =
          RegistrationBusinessContactInfoFragment.newInstance()
        registrationBusinessContactInfoFragment
      }
      FragmentType.REGISTRATION_BUSINESS_WEBSITE -> {
        registrationBusinessWebsiteFragment = RegistrationBusinessWebsiteFragment.newInstance()
        registrationBusinessWebsiteFragment
      }
      FragmentType.REGISTRATION_BUSINESS_GOOGLE_PAGE -> {
        registrationBusinessGoogleBusinessFragment =
          RegistrationBusinessGoogleBusinessFragment.newInstance()
        registrationBusinessGoogleBusinessFragment
      }
      FragmentType.REGISTRATION_BUSINESS_FACEBOOK_PAGE -> {
        registrationBusinessFacebookPageFragment =
          RegistrationBusinessFacebookPageFragment.newInstance()
        registrationBusinessFacebookPageFragment
      }
      FragmentType.REGISTRATION_BUSINESS_FACEBOOK_SHOP -> {
        registrationBusinessFacebookShopFragment =
          RegistrationBusinessFacebookShopFragment.newInstance()
        registrationBusinessFacebookShopFragment
      }
      FragmentType.REGISTRATION_BUSINESS_TWITTER_DETAILS -> {
        registrationBusinessTwitterDetailsFragment =
          RegistrationBusinessTwitterDetailsFragment.newInstance()
        registrationBusinessTwitterDetailsFragment
      }
      FragmentType.REGISTRATION_BUSINESS_WHATSAPP -> {
        registrationBusinessWhatsAppFragment = RegistrationBusinessWhatsAppFragment.newInstance()
        registrationBusinessWhatsAppFragment
      }
      FragmentType.REGISTRATION_BUSINESS_API_CALL -> {
        registrationBusinessApiFragment = RegistrationBusinessApiFragment.newInstance()
        registrationBusinessApiFragment
      }
      FragmentType.REGISTRATION_COMPLETE -> {
        registrationCompleteFragment = RegistrationCompleteFragment.newInstance()
        registrationCompleteFragment
      }
      else -> throw IllegalFragmentTypeException()
    }
  }

  override fun onBackPressed() {
    when (type) {
      FragmentType.REGISTRATION_BUSINESS_BASIC_DETAILS -> {
        registrationBusinessContactInfoFragment?.updateInfo()
        NavigatorManager.popCurrentScreen(ScreenModel.Screen.BUSINESS_INFO)
        super.onBackPressed()
      }
      FragmentType.REGISTRATION_BUSINESS_WEBSITE -> {
        registrationBusinessWebsiteFragment?.updateInfo()
        NavigatorManager.popCurrentScreen(ScreenModel.Screen.BUSINESS_SUBDOMAIN)
        super.onBackPressed()
      }
      FragmentType.REGISTRATION_BUSINESS_GOOGLE_PAGE -> {
        registrationBusinessGoogleBusinessFragment?.updateInfo()
        NavigatorManager.popCurrentScreen(ScreenModel.Screen.BUSINESS_GOOGLE_PAGE)
        super.onBackPressed()
      }
      FragmentType.REGISTRATION_BUSINESS_FACEBOOK_PAGE -> {
        registrationBusinessFacebookPageFragment?.updateInfo()
        NavigatorManager.popCurrentScreen(ScreenModel.Screen.BUSINESS_FACEBOOK_PAGE)
        super.onBackPressed()
      }
      FragmentType.REGISTRATION_BUSINESS_FACEBOOK_SHOP -> {
        registrationBusinessFacebookShopFragment?.updateInfo()
        NavigatorManager.popCurrentScreen(ScreenModel.Screen.BUSINESS_FACEBOOK_SHOP)
        super.onBackPressed()
      }
      FragmentType.REGISTRATION_BUSINESS_TWITTER_DETAILS -> {
        registrationBusinessTwitterDetailsFragment?.updateInfo()
        NavigatorManager.popCurrentScreen(ScreenModel.Screen.BUSINESS_TWITTER)
        super.onBackPressed()
      }
      FragmentType.REGISTRATION_BUSINESS_WHATSAPP -> {
        registrationBusinessWhatsAppFragment?.updateInfo()
        NavigatorManager.popCurrentScreen(ScreenModel.Screen.BUSINESS_WHATSAPP)
        super.onBackPressed()
      }
      FragmentType.REGISTRATION_BUSINESS_API_CALL -> {
        if (exitToast?.view?.windowToken != null) {
          if (registrationBusinessApiFragment?.isDigitalChannel() == false && registrationBusinessApiFragment?.isBackBlock() == false) {
            registrationBusinessApiFragment?.updateInfo()
            NavigatorManager.popCurrentScreen(ScreenModel.Screen.REGISTERING)
            super.onBackPressed()
          }
        } else if (registrationBusinessApiFragment?.isBackBlock() == false) exitToast?.show()
      }
      FragmentType.REGISTRATION_COMPLETE -> {
        if (exitToast?.view?.windowToken != null) {
          super.onBackPressed()
        } else exitToast?.show()
      }
      else -> super.onBackPressed()
    }
  }

  private fun shouldAddToBackStack(): Boolean {
    return when (type) {
      else -> false
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    registrationCompleteFragment?.onActivityResult(requestCode, resultCode, data)
    registrationBusinessGoogleBusinessFragment?.onActivityResult(requestCode, resultCode, data)
    registrationBusinessTwitterDetailsFragment?.onActivityResult(requestCode, resultCode, data)
    registrationBusinessContactInfoFragment?.onActivityResult(requestCode, resultCode, data)
  }
}

fun Fragment.startFragmentActivity(
  type: FragmentType,
  bundle: Bundle = Bundle(),
  clearTop: Boolean = false,
  isResult: Boolean? = false
) {
  val intent = Intent(activity, AppFragmentContainerActivity::class.java)
  intent.putExtras(bundle)
  intent.setFragmentType(type)
  if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
  if (isResult==true)
  startActivityForResult(intent,9792)
  else startActivity(intent)
}

fun AppCompatActivity.startFragmentActivity(
  type: FragmentType,
  bundle: Bundle = Bundle(),
  clearTop: Boolean = false
) {
  val intent = Intent(this, AppFragmentContainerActivity::class.java)
  intent.putExtras(bundle)
  intent.setFragmentType(type)
  if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
  startActivity(intent)
}

fun Intent.setFragmentType(type: FragmentType): Intent {
  return this.putExtra(FRAGMENT_TYPE, type.ordinal)
}
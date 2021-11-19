package com.onboarding.nowfloats.ui.registration

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.databinding.ViewDataBinding
import com.framework.exceptions.IllegalFragmentTypeException
import com.framework.views.DotProgressBar
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.base.AppBaseFragment
import com.onboarding.nowfloats.constant.FragmentType
import com.onboarding.nowfloats.constant.IntentConstant
import com.onboarding.nowfloats.constant.PreferenceConstant
import com.onboarding.nowfloats.extensions.addInt
import com.onboarding.nowfloats.extensions.addString
import com.onboarding.nowfloats.extensions.getInt
import com.onboarding.nowfloats.managers.NavigatorManager
import com.onboarding.nowfloats.model.RequestFloatsModel
import com.onboarding.nowfloats.model.category.CategoryDataModel
import com.onboarding.nowfloats.model.channel.ChannelModel
import com.onboarding.nowfloats.model.navigator.ScreenModel
import com.onboarding.nowfloats.model.navigator.ScreenModel.Screen
import com.onboarding.nowfloats.model.navigator.ScreenModel.Screen.BUSINESS_INFO
import com.onboarding.nowfloats.ui.startFragmentActivity
import com.onboarding.nowfloats.viewmodel.business.BusinessCreateViewModel

open class BaseRegistrationFragment<binding : ViewDataBinding> :
  AppBaseFragment<binding, BusinessCreateViewModel>() {

  protected val channels: ArrayList<ChannelModel>
    get() {
      return requestFloatsModel?.channels ?: ArrayList()
    }

  protected val categoryDataModel: CategoryDataModel?
    get() {
      return requestFloatsModel?.categoryDataModel
    }

  protected val pref: SharedPreferences?
    get() {
      return baseActivity.getSharedPreferences(
        PreferenceConstant.NOW_FLOATS_PREFS,
        Context.MODE_PRIVATE
      )
    }

  protected val userProfileId: String?
    get() {
      return pref?.getString(PreferenceConstant.USER_PROFILE_ID, "")
    }
  protected val clientId: String?
    get() {
      return pref?.getString(
        PreferenceConstant.CLIENT_ID,
        "2D5C6BB4F46457422DA36B4977BD12E37A92EEB13BB4423A548387BA54DCEBD5"
      )
    }

  protected var requestFloatsModel: RequestFloatsModel? = null

  protected var totalPages = 1
  protected var currentPage = 1

  override fun getLayout(): Int {
    return when (this) {
      is RegistrationBusinessContactInfoFragment -> R.layout.fragment_registration_business_contact_info
      is RegistrationBusinessWebsiteFragment -> R.layout.fragment_registration_business_website
      is RegistrationBusinessGoogleBusinessFragment -> R.layout.fragment_registration_business_google
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

  protected open fun setSavedData() {

  }

  override fun onCreateView() {
    requestFloatsModel = NavigatorManager.getRequest()
    val title = arguments?.getString(IntentConstant.TOOLBAR_TITLE.name)
    if (title != null) {
      setToolbarTitle(title)

      try {
        val stringBuilder = StringBuilder(title.removePrefix("Step "))
        val split = stringBuilder.split('/')
        currentPage = split.firstOrNull()?.toInt() ?: return
        totalPages = split.lastOrNull()?.toInt() ?: return
        currentPage = currentPage
      } catch (e: Exception) {
        e.printStackTrace()
      }
    } else {
      currentPage = arguments?.getInt(IntentConstant.CURRENT_PAGES) ?: currentPage
      totalPages = arguments?.getInt(IntentConstant.TOTAL_PAGES) ?: totalPages
      if (this !is RegistrationCompleteFragment) {
        if (this !is RegistrationBusinessApiFragment) setToolbarTitle(resources.getString(R.string.step) + " $currentPage/$totalPages")
      }
    }
  }

  fun getDotProgress(): DotProgressBar? {
    return DotProgressBar.Builder().setMargin(0).setAnimationDuration(800)
      .setDotBackground(R.drawable.ic_dot).setMaxScale(.7f).setMinScale(0.3f)
      .setNumberOfDots(3).setdotRadius(8).build(baseActivity)
  }

  protected fun gotoBusinessWebsite() {
    NavigatorManager.pushToStackAndSaveRequest(
      ScreenModel(getPreviousScreen(), getToolbarTitle()),
      requestFloatsModel
    )
    startFragmentActivity(FragmentType.REGISTRATION_BUSINESS_WEBSITE, getBundle())
  }

  protected fun gotoFacebookShop() {
    NavigatorManager.pushToStackAndSaveRequest(
      ScreenModel(getPreviousScreen(), getToolbarTitle()),
      requestFloatsModel
    )
    startFragmentActivity(FragmentType.REGISTRATION_BUSINESS_FACEBOOK_SHOP, getBundle())
  }

  protected fun gotoFacebookPage() {
    NavigatorManager.pushToStackAndSaveRequest(
      ScreenModel(getPreviousScreen(), getToolbarTitle()),
      requestFloatsModel
    )
    startFragmentActivity(FragmentType.REGISTRATION_BUSINESS_FACEBOOK_PAGE, getBundle())
  }

  protected fun gotoGoogleBusinessPage() {
    NavigatorManager.pushToStackAndSaveRequest(
      ScreenModel(getPreviousScreen(), getToolbarTitle()),
      requestFloatsModel
    )
    startFragmentActivity(FragmentType.REGISTRATION_BUSINESS_GOOGLE_PAGE, getBundle())
  }

  protected fun gotoTwitterDetails() {
    NavigatorManager.pushToStackAndSaveRequest(
      ScreenModel(getPreviousScreen(), getToolbarTitle()),
      requestFloatsModel
    )
    startFragmentActivity(FragmentType.REGISTRATION_BUSINESS_TWITTER_DETAILS, getBundle())
  }

  protected fun gotoWhatsAppCallDetails() {
    NavigatorManager.pushToStackAndSaveRequest(
      ScreenModel(getPreviousScreen(), getToolbarTitle()),
      requestFloatsModel
    )
    startFragmentActivity(FragmentType.REGISTRATION_BUSINESS_WHATSAPP, getBundle())
  }

  protected open fun gotoBusinessApiCallDetails() {
    NavigatorManager.pushToStackAndSaveRequest(
      ScreenModel(Screen.REGISTERING, getToolbarTitle()),
      requestFloatsModel
    )
    startFragmentActivity(FragmentType.REGISTRATION_BUSINESS_API_CALL, getBundle())
  }

  protected fun gotoRegistrationComplete() {
    NavigatorManager.pushToStackAndSaveRequest(
      ScreenModel(
        Screen.REGISTRATION_COMPLETE,
        getToolbarTitle()
      ), requestFloatsModel
    )
    startFragmentActivity(FragmentType.REGISTRATION_COMPLETE, getBundle(), clearTop = true)
  }

  protected open fun setProfileDetails(name: String?, profilePicture: String?) {

  }

  open fun updateInfo() {
    NavigatorManager.updateRequest(requestFloatsModel)
  }

  private fun getBundle(): Bundle {
    val bundle = Bundle()
    bundle.addInt(IntentConstant.TOTAL_PAGES, totalPages)
      .addInt(IntentConstant.CURRENT_PAGES, currentPage + 1)
      .addString(IntentConstant.PREVIOUS_SCREEN, getCurrentScreenType()?.name)
    return bundle
  }

  private fun getPreviousScreen(): Screen? {
    return if (this is RegistrationBusinessContactInfoFragment) {
      BUSINESS_INFO
    } else {
      getCurrentScreenType()
    }
  }

  private fun getCurrentScreenType(): Screen? {
    return when (this) {
      is RegistrationBusinessContactInfoFragment -> BUSINESS_INFO
      is RegistrationBusinessWebsiteFragment -> Screen.BUSINESS_SUBDOMAIN
      is RegistrationBusinessGoogleBusinessFragment -> Screen.BUSINESS_GOOGLE_PAGE
      is RegistrationBusinessFacebookPageFragment -> Screen.BUSINESS_FACEBOOK_PAGE
      is RegistrationBusinessFacebookShopFragment -> Screen.BUSINESS_FACEBOOK_SHOP
      is RegistrationBusinessTwitterDetailsFragment -> Screen.BUSINESS_TWITTER
      is RegistrationBusinessWhatsAppFragment -> Screen.BUSINESS_WHATSAPP
      is RegistrationBusinessApiFragment -> Screen.REGISTERING
      is RegistrationCompleteFragment -> Screen.REGISTRATION_COMPLETE
      else -> null
    }
  }

  override fun getViewModelClass(): Class<BusinessCreateViewModel> {
    return BusinessCreateViewModel::class.java
  }

  protected fun setDataLogin() {
    val editor = pref?.edit()
    editor?.putString(PreferenceConstant.GET_FP_DETAILS_TAG, requestFloatsModel?.getWebSiteId())
    editor?.putString(PreferenceConstant.KEY_FP_ID, requestFloatsModel?.floatingPointId)
    editor?.putString(PreferenceConstant.KEY_sourceClientId, clientId)
    editor?.putString(
      PreferenceConstant.GET_FP_EXPERIENCE_CODE,
      requestFloatsModel?.categoryDataModel?.experience_code
    )
    editor?.putBoolean(PreferenceConstant.IS_USER_LOGIN, true)
    editor?.putBoolean(PreferenceConstant.IS_USER_SIGN_UP, false)
    editor?.apply()
  }

}
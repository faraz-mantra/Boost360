package com.dashboard.controller.ui.more

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.*
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.LinearLayoutCompat
import com.appservice.model.accountDetails.getBankDetail
import com.appservice.model.kycData.DataKyc
import com.appservice.model.kycData.getBusinessKycDetail
import com.appservice.ui.updatesBusiness.showDialog
import com.boost.presignin.model.other.KYCDetails

import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.controller.ui.more.model.AboutAppSectionItem
import com.dashboard.controller.ui.more.model.AboutAppSectionItem.IconType.*
import com.dashboard.controller.ui.more.model.MoreSettingsResponse
import com.dashboard.controller.ui.more.model.UsefulLinksItem
import com.dashboard.databinding.FragmentMoreBinding
import com.dashboard.recyclerView.AppBaseRecyclerViewAdapter
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.dashboard.recyclerView.RecyclerItemClickListener
import com.dashboard.utils.*
import com.dashboard.viewmodel.DashboardViewModel
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.glide.util.glideLoad
import com.framework.models.UserProfileData
import com.framework.models.UserProfileDataResult
import com.framework.models.UserProfileDataResult.Companion.getMerchantProfileDetails
import com.framework.models.UserProfileDataResult.Companion.saveMerchantProfileDetails
import com.framework.pref.*
import com.framework.pref.Key_Preferences.GET_FP_DETAILS_IMAGE_URI
import com.framework.pref.Key_Preferences.GET_FP_DETAILS_LogoUrl
import com.framework.webengageconstant.*

class MoreFragment : AppBaseFragment<FragmentMoreBinding, DashboardViewModel>(), RecyclerItemClickListener {

  private var session: UserSessionManager? = null
  val TWITTER_URL = "https://twitter.com/Nowfloats"
  val TWITTER_ID_URL = "twitter://user?screen_name=nowfloats"
  private var usefulLinks: ArrayList<UsefulLinksItem>? = null

  override fun getLayout(): Int {
    return R.layout.fragment_more
  }

  override fun getViewModelClass(): Class<DashboardViewModel> {
    return DashboardViewModel::class.java
  }

  override fun onCreateView() {
    this.session = UserSessionManager(baseActivity)
    WebEngageController.trackEvent(DASHBOARD_MORE_PAGE, PAGE_VIEW, session?.fpTag)
    setOnClickListener(
      binding?.rivUsersImage, binding?.rivBusinessImage, binding?.civProfile,
      binding?.ctvContent, binding?.businessProfile, binding?.ctvName, binding?.boostSubscription
    )
    setData()
  }

  private fun setData() {
    var featureImageUrl = session?.getFPDetails(GET_FP_DETAILS_IMAGE_URI)
    if (featureImageUrl.isNullOrEmpty().not() && featureImageUrl!!.contains("BizImages") && featureImageUrl.contains("http").not()) {
      featureImageUrl = BASE_IMAGE_URL + featureImageUrl
    }
    var businessLogoUrl = session?.getFPDetails(GET_FP_DETAILS_LogoUrl)
    if (businessLogoUrl.isNullOrEmpty().not() && businessLogoUrl!!.contains("BizImages") && businessLogoUrl.contains("http").not()) {
      businessLogoUrl = BASE_IMAGE_URL + businessLogoUrl
    }

    binding?.rivBusinessImage?.apply {
      baseActivity.glideLoad(this, url = businessLogoUrl, placeholder = R.drawable.placeholder_image, isCrop = true)
    }
    var bgImageUri = session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_BG_IMAGE)
    if (bgImageUri.isNullOrEmpty().not() && bgImageUri!!.contains("http").not()) {
      bgImageUri = BASE_IMAGE_URL + bgImageUri
    }
    binding?.rivCurrentlyManage?.apply {
      baseActivity.glideLoad(this, url = bgImageUri, placeholder = R.drawable.general_services_background_img_d, isCrop = true)
    }
    val city = session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_CITY)
    val country = session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY)
    val location = if (city.isNullOrEmpty().not() && country.isNullOrEmpty().not()) "$city, $country" else "$city$country"
    binding?.ctvContent?.text = session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_DESCRIPTION)
    binding?.ctvBusinessName?.text = session?.fPName ?: session?.fpTag
    binding?.ctvBusinessAddress?.text = location
    setRecyclerView()
  }

  private fun refreshUserDetail() {
    val merchantProfileDetails = getMerchantProfileDetails()
    setUserDetail(merchantProfileDetails)
    fetchUserData()
  }

  private fun setUserDetail(merchantProfileDetails: UserProfileDataResult?) {
    if (merchantProfileDetails?.ImageUrl.isNullOrEmpty().not()) {
      binding?.rivUsersImage?.let { baseActivity.glideLoad(it, merchantProfileDetails?.ImageUrl ?: "", R.drawable.placeholder_image_n) }
    } else {
      binding?.rivUsersImage?.setImageResource(R.drawable.ic_user_circle_dark_grey)
    }
    if (merchantProfileDetails?.isNullAllValue() == false) {
      binding?.ctvType?.gone()
      binding?.ctvName?.text = (merchantProfileDetails.getUserNameN() ?: getUserNumber()).capitalizeUtil()
      binding?.ctvContent?.text = session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_DESCRIPTION)
    } else {
      binding?.ctvType?.gone()
      binding?.ctvName?.text = getUserNumber()
      binding?.ctvContent?.text = getString(R.string.profile_info_content)
    }
  }

  private fun getUserNumber(): String {
    val number = when {
      session?.userProfileMobile.isNullOrEmpty().not() && session?.userProfileMobile!!.length > 9 -> session?.userProfileMobile
      session?.userPrimaryMobile.isNullOrEmpty().not() && session?.userPrimaryMobile!!.length > 9 -> session?.userPrimaryMobile
      session?.fPPrimaryContactNumber.isNullOrEmpty().not() && session?.fPPrimaryContactNumber!!.length > 9 -> session?.fPPrimaryContactNumber
      else -> "N/A"
    }
    return if (!number.equals("N/A") && number?.contains("+91") == true) number else "+91 $number"
  }

  private fun fetchUserData() {
    viewModel?.getUserProfileData(session?.userProfileId)?.observe(viewLifecycleOwner, {
      val userProfileResult = (it as? UserProfileData)?.Result
      if (it.isSuccess() && userProfileResult != null) {
        saveMerchantProfileDetails(userProfileResult)
      }
      setUserDetail(userProfileResult)
    })
  }

  private fun setRecyclerView() {
    viewModel?.getMoreSettings(baseActivity)?.observeOnce(viewLifecycleOwner, { it0 ->
      val data = it0 as? MoreSettingsResponse
      if (data != null) {
        usefulLinks = data.usefulLinks
        setHelpfulResources()
        binding?.rvAbout?.adapter = AppBaseRecyclerViewAdapter(baseActivity, data.aboutAppSection ?: arrayListOf(), this)
      } else showShortToast(getString(R.string.error_loading_more_page))
    })
  }

  private fun setHelpfulResources() {
    if (usefulLinks.isNullOrEmpty().not()) {
      usefulLinks?.map {
        when (it.icon) {
          UsefulLinksItem.IconType.my_bank_acccount.name -> {
            val bankData = getBankDetail()
            if (bankData?.isValidAccount() == true) {
              if (bankData.kYCDetails?.verificationStatus == KYCDetails.Status.PENDING.name) {
                it.actionBtn?.color = "#EB5757"
                it.actionBtn?.textColor = "#FFFFFF"
                it.actionBtn?.title = "Unverified"
              } else {
                it.actionBtn?.color = "#F2FBE9"
                it.actionBtn?.textColor = "#7ED321"
                it.actionBtn?.title = "Verified"
              }
            }
            it
          }
          UsefulLinksItem.IconType.business_kyc.name -> {
            val dataKyc = getBusinessKycDetail()
            if (dataKyc != null) {
              if (dataKyc.isVerified == DataKyc.Verify.YES.name) {
                it.actionBtn?.color = "#F2FBE9"
                it.actionBtn?.textColor = "#7ED321"
                it.actionBtn?.title = "Verified"
              } else {
                it.actionBtn?.color = "#EB5757"
                it.actionBtn?.textColor = "#FFFFFF"
                it.actionBtn?.title = "Unverified"
              }
            }
            it
          }
          else -> it
        }
      }
      binding?.rvUsefulLinks?.adapter = AppBaseRecyclerViewAdapter(baseActivity, usefulLinks ?: arrayListOf(), this)
    }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.ABOUT_VIEW_CLICK.ordinal -> {
        val aboutAppSectionItem = item as? AboutAppSectionItem
        aboutAppSectionItem?.icon?.let { AboutAppSectionItem.IconType.fromName(it) }?.let { clickAppActionButton(it) }
      }
      RecyclerViewActionType.USEFUL_LINKS_CLICK.ordinal -> {
        val usefulLinksItem = item as? UsefulLinksItem
        usefulLinksItem?.icon?.let { UsefulLinksItem.IconType.fromName(it) }?.let { clickUsefulButton(it) }
      }
    }
  }

  private fun clickUsefulButton(type: UsefulLinksItem.IconType) {
    when (type) {
      UsefulLinksItem.IconType.business_kyc -> baseActivity.startBusinessKycBoost(session)
      UsefulLinksItem.IconType.boost_extension -> baseActivity.startBoostExtension(session)
      UsefulLinksItem.IconType.boost_academy -> showShortToast("Coming soon.")
      UsefulLinksItem.IconType.boost_keyboard -> baseActivity.startKeyboardActivity(session!!)
      UsefulLinksItem.IconType.my_bank_acccount -> baseActivity.startMyBankAccount(session!!)
      UsefulLinksItem.IconType.refer_and_earn -> baseActivity.startReferralView(session!!)
      UsefulLinksItem.IconType.ria_digital_assistant -> baseActivity.startHelpAndSupportActivity(session!!)
      UsefulLinksItem.IconType.training_and_certification -> trainingCertification()
      UsefulLinksItem.IconType.custom_website_domain -> baseActivity.startDomainDetail(session)
    }
  }

  private fun clickAppActionButton(type: AboutAppSectionItem.IconType) {
    when (type) {
      whats_new_version -> showShortToast("Coming soon")
      frequently_asked_question -> {
        baseActivity.startMobileSite(session, getString(R.string.setting_faq_url), ABOUT_BOOST_FAQS)
      }
      terms_of_usages -> {
        baseActivity.startMobileSite(session, resources.getString(R.string.settings_tou_url), ABOUT_BOOST_TNC)
      }
      follow_us_on_twitter -> {
        followUsTwitter()
      }
      help_us_make_boost_better -> {
        businessGoogleForm()
      }
      privacy_policy -> {
        baseActivity.startMobileSite(session, resources.getString(R.string.settings_privacy_url), ABOUT_BOOST_PRIVACY)
      }
      like_us_on_facebook -> {
        likeUsFacebook(baseActivity, "")
      }
      rate_us_on_google_play -> {
        rateGooglePlayStore()
      }
      else -> {
      }
    }
  }

  private fun trainingCertification() {
    if (session?.getStoreWidgets()?.contains("MERCHANT_TRAINING") == true) {
      baseActivity.startMobileSite(session, getString(R.string.product_training_link), ABOUT_BOOST_TRAINING)
    } else {
      WebEngageController.trackEvent(ABOUT_BOOST_TRAINING, CLICK, NO_EVENT_VALUE)
      showDialog(baseActivity, getString(R.string.restricted_access), getString(R.string.you_need_to_buy_the_one_time_pack_for_boost)) { dialog, _ -> dialog?.dismiss() }
    }
  }

  private fun followUsTwitter() {
    WebEngageController.trackEvent(ABOUT_BOOST_TWITTER_LIKE, CLICK, NO_EVENT_VALUE)
    val intent = Intent(Intent.ACTION_VIEW)
    try {
      requireActivity().packageManager.getPackageInfo(getString(R.string.twitter_package), 0)
      intent.data = Uri.parse(TWITTER_ID_URL)
    } catch (e1: PackageManager.NameNotFoundException) {
      intent.data = Uri.parse(TWITTER_URL)
      e1.printStackTrace()
    }
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY
    startActivity(intent)
  }

  private fun rateGooglePlayStore() {
    val uri = Uri.parse("market://details?id=" + baseActivity.applicationContext?.packageName)
    val goToMarket = Intent(Intent.ACTION_VIEW, uri)
    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    try {
      startActivity(goToMarket)
      WebEngageController.trackEvent(ABOUT_BOOST_PLAY_STORE_RATING, CLICK, NO_EVENT_VALUE)
    } catch (e: ActivityNotFoundException) {
      val url = resources.getString(R.string.settings_rate_us_link)
      baseActivity.startMobileSite(session, url, ABOUT_BOOST_PLAY_STORE_RATING)
    }
  }

  private fun businessGoogleForm() {
    WebEngageController.trackEvent(BOOST_FEEDBACK_GOOGLE_FORM, CLICK, NO_EVENT_VALUE)
    try {
      val i = Intent(Intent.ACTION_VIEW)
      i.data = Uri.parse(getString(R.string.google_form_help_business_boost))
      startActivity(i)
    } catch (e: Exception) {
      e.printStackTrace()
      showShortToast("Feedback Google form loading error, please try again!.")
    }
  }

  private fun logoutUser() {
    AlertDialog.Builder(ContextThemeWrapper(baseActivity, R.style.CustomAlertDialogTheme))
      .setCancelable(false)
      .setMessage(R.string.are_you_sure)
      .setPositiveButton(R.string.logout) { dialog: DialogInterface, _: Int ->
        WebEngageController.trackEvent(BOOST_LOGOUT_CLICK, CLICK, NO_EVENT_VALUE)
        baseActivity.startLogoutActivity()
        dialog.dismiss()
      }.setNegativeButton(R.string.cancel) { dialog: DialogInterface, _: Int ->
        dialog.dismiss()
      }.show()
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.rivBusinessImage -> {
        baseActivity.startBusinessLogo(session)
      }
      binding?.rivUsersImage, binding?.civProfile, binding?.ctvContent, binding?.ctvName -> {
        baseActivity.startUserProfileDetail(session)
      }
      binding?.boostSubscription -> {
        baseActivity.initiateAddonMarketplace(session!!, false, "", "")
      }
      binding?.businessProfile -> baseActivity.startBusinessProfileDetailEdit(session)
    }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
//    inflater.inflate(R.menu.menu_more, menu)
//     menu.findItem(R.id.menu_more)?.icon?.setTint(Color.WHITE)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.menu_more -> {
        val view: View? = baseActivity.findViewById(item.itemId)
        view?.let { showPopupWindow(it) }
        return true
      }
      else -> {
      }
    }
    return super.onOptionsItemSelected(item)
  }

  private fun showPopupWindow(anchor: View) {
    val view = LayoutInflater.from(baseActivity).inflate(R.layout.popup_window_logout_menu, null)
    val popupWindow = PopupWindow(view, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true)
    val more = popupWindow.contentView?.findViewById<LinearLayoutCompat>(R.id.ll_logout)
    more?.setOnClickListener {
      logoutUser()
      popupWindow.dismiss()
    }
    popupWindow.elevation = 5.0F
    popupWindow.showAsDropDown(anchor, 0, 10)
  }

  private fun likeUsFacebook(context: Context, review: String) {
    WebEngageController.trackEvent(ABOUT_BOOST_FB_LIKE, CLICK, NO_EVENT_VALUE)
    val facebookIntent: Intent = try {
      context.packageManager.getPackageInfo(context.getString(R.string.facebook_package), 0)
      Intent(Intent.ACTION_VIEW, Uri.parse(FACEBOOK_PAGE_WITH_ID))
    } catch (e: Exception) {
      Intent(Intent.ACTION_VIEW, Uri.parse(FACEBOOK_URL + review))
    }
    facebookIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY
    try {
      context.startActivity(facebookIntent)
    } catch (e: Exception) {
      Toast.makeText(context, context.getString(R.string.unable_to_open_facebook), Toast.LENGTH_SHORT).show()
    }
  }

  override fun onResume() {
    super.onResume()
    setHelpfulResources()
    refreshUserDetail()
  }
}
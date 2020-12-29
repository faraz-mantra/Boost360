package com.dashboard.utils

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.appservice.model.SessionData
import com.appservice.model.StatusKyc
import com.appservice.ui.bankaccount.startFragmentAccountActivityNew
import com.appservice.ui.paymentgateway.startFragmentPaymentActivityNew
import com.dashboard.R
import com.dashboard.controller.getDomainName
import com.dashboard.model.live.premiumBanner.PromoAcademyBanner
import com.dashboard.pref.*
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.model.PreferenceData
import com.inventoryorder.ui.startFragmentOrderActivity
import com.onboarding.nowfloats.constant.FragmentType
import com.onboarding.nowfloats.ui.updateChannel.startFragmentActivity
import com.onboarding.nowfloats.ui.webview.WebViewActivity

const val VISITS_TYPE_STRING = "visits_type_string"

const val RIA_NODE_DATA = "riaNodeDatas"

fun AppCompatActivity.startDigitalChannel(session: UserSessionManager) {
  try {
    val bundle = Bundle()
    val rootAlisasURI: String = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI) ?: ""
    session.setHeader(WA_KEY)
    bundle.putString(UserSessionManager.KEY_FP_ID, session.fPID)
    bundle.putString(Key_Preferences.GET_FP_DETAILS_TAG, session.fpTag)
    bundle.putString(Key_Preferences.GET_FP_EXPERIENCE_CODE, session.fP_AppExperienceCode)
    bundle.putBoolean(Key_Preferences.IS_UPDATE, true)
    bundle.putString(Key_Preferences.BUSINESS_NAME, session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME))
    var imageUri = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_IMAGE_URI)
    if (imageUri.isNullOrEmpty().not() && imageUri!!.contains("http").not()) {
      imageUri = BASE_IMAGE_URL + imageUri
    }
    bundle.putString(Key_Preferences.BUSINESS_IMAGE, imageUri)
    bundle.putString(Key_Preferences.BUSINESS_TYPE, session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY))

    val city = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CITY)
    val country = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY)
    bundle.putString(Key_Preferences.LOCATION, if (city.isNullOrEmpty().not() && country.isNullOrEmpty().not()) "$city, $country" else "$city$country")
    bundle.putString(Key_Preferences.WEBSITE_URL, session.getDomainName(false))
    bundle.putString(Key_Preferences.PRIMARY_NUMBER, session.userPrimaryMobile)
    bundle.putString(Key_Preferences.PRIMARY_EMAIL, session.fPEmail)
    startFragmentActivity(FragmentType.MY_DIGITAL_CHANNEL, bundle)
  } catch (e: Exception) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startVmnCallCard(session: UserSessionManager?) {
  try {
    val i = Intent(this, Class.forName("com.nowfloats.Analytics_Screen.VmnCallCardsActivity"))
    startActivity(i)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: Exception) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startBusinessEnquiry(session: UserSessionManager?) {
  try {
    val queries = Intent(this, Class.forName("com.nowfloats.Business_Enquiries.BusinessEnquiryActivity"))
    startActivity(queries)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: Exception) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startSearchQuery(session: UserSessionManager?) {
  try {
    val queries = Intent(this, Class.forName("com.nowfloats.Analytics_Screen.SearchQueriesActivity"))
    startActivity(queries)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: Exception) {
    e.printStackTrace()
  }
}


fun AppCompatActivity.startRevenueSummary(session: UserSessionManager?) {
  try {
    val queries = Intent(this, Class.forName("com.nowfloats.Analytics_Screen.RevenueSummaryActivity"))
    startActivity(queries)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: Exception) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startAptOrderSummary(session: UserSessionManager?) {
  try {
    val queries = Intent(this, Class.forName("com.nowfloats.Analytics_Screen.OrderSummaryActivity"))
    startActivity(queries)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: Exception) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startBackgroundImageGallery(session: UserSessionManager?) {
  try {
    val queries = Intent(this, Class.forName("com.nowfloats.Image_Gallery.BackgroundImageGalleryActivity"))
    startActivity(queries)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: Exception) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startFeviconImage(session: UserSessionManager?) {
  try {
    val queries = Intent(this, Class.forName("com.nowfloats.BusinessProfile.UI.UI.FaviconImageActivity"))
    startActivity(queries)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: Exception) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startSiteViewAnalytic(session: UserSessionManager?, type: String) {
  try {
    val intent = Intent(this, Class.forName("com.nowfloats.Analytics_Screen.Graph.SiteViewsAnalytics"))
    intent.putExtra(VISITS_TYPE_STRING, type);
    startActivity(intent)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: Exception) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startSubscriber(session: UserSessionManager?) {
  try {
    val subscribers = Intent(this, Class.forName("com.nowfloats.Analytics_Screen.SubscribersActivity"))
    startActivity(subscribers)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: Exception) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startAnalytics(session: UserSessionManager?, table_name: Int?) {
  try {
    val intent = Intent(this, Class.forName("com.nowfloats.Analytics_Screen.Graph.AnalyticsActivity"))
    if (table_name != null) intent.putExtra("table_name", table_name)
    startActivity(intent)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: Exception) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.promoBannerMarketplace(session: UserSessionManager, promoBanner: PromoAcademyBanner?) {
  val bundle = Bundle()
  bundle.putString("_kid", promoBanner?.kid)
  bundle.putString("_parentClassId", promoBanner?.parentClassId)
  bundle.putString("_parentClassName", promoBanner?.parentClassName)
  bundle.putString("_propertyName", promoBanner?.propertyName)
  bundle.putString("createdon", promoBanner?.createdon)
  bundle.putString("cta_feature_key", promoBanner?.ctaFeatureKey)
  bundle.putString("cta_web_link", promoBanner?.ctaWebLink)
  bundle.putString("cta_bundle_identifier", promoBanner?.ctaBundleIdentifier)
  bundle.putStringArrayList("exclusive_to_categories", promoBanner?.exclusiveToCategories)
  bundle.putStringArrayList("exclusive_to_customers", promoBanner?.exclusiveToCustomers)
  bundle.putBoolean("isarchived", promoBanner?.isarchived ?: false)
  bundle.putString("title", promoBanner?.title)
  bundle.putString("updatedon", promoBanner?.updatedon)
  bundle.putString("websiteid", promoBanner?.websiteid)
  this.initiateAddonMarketplace(session, false, "", "", promoBundle = bundle)
}

fun AppCompatActivity.initiateAddonMarketplace(session: UserSessionManager, isOpenCardFragment: Boolean, screenType: String, buyItemKey: String?, promoBundle: Bundle? = null) {
  try {
    val intent = Intent(this, Class.forName("com.boost.upgrades.UpgradeActivity"))
    promoBundle?.let { intent.putExtra("PROMO_BANNER_CLICK", it) }
    intent.putExtra("expCode", session.fP_AppExperienceCode)
    intent.putExtra("fpName", session.fPName)
    intent.putExtra("fpid", session.fPID)
    intent.putExtra("fpTag", session.fpTag)
    intent.putExtra("isOpenCardFragment", isOpenCardFragment)
    intent.putExtra("screenType", screenType)
    intent.putExtra("accountType", session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY))
    intent.putStringArrayListExtra("userPurchsedWidgets", StoreWidgets)
    if (session.fPEmail != null) {
      intent.putExtra("email", session.fPEmail)
    } else {
      intent.putExtra("email", "ria@nowfloats.com")
    }
    if (session.fPPrimaryContactNumber != null) {
      intent.putExtra("mobileNo", session.fPPrimaryContactNumber)
    } else {
      intent.putExtra("mobileNo", "9160004303")
    }
    if (buyItemKey != null && buyItemKey.isNotEmpty()) intent.putExtra("buyItemKey", buyItemKey)
    intent.putExtra("profileUrl", session.fPLogo)
    startActivity(intent)
  } catch (e: Exception) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startSettingActivity(session: UserSessionManager) {
  startAppActivity(fragmentType = "ACCOUNT_SETTING")
}

fun AppCompatActivity.startKeyboardActivity(session: UserSessionManager) {
  startAppActivity(fragmentType = "ACCOUNT_KEYBOARD")
}

fun AppCompatActivity.startManageContentActivity(session: UserSessionManager) {
  startAppActivity(fragmentType = "MANAGE_CONTENT")
}

fun AppCompatActivity.startManageInventoryActivity(session: UserSessionManager) {
  startAppActivity(fragmentType = "MANAGE_INVENTORY")
}

fun AppCompatActivity.startHelpAndSupportActivity(session: UserSessionManager) {
  startAppActivity(fragmentType = "HELP_AND_SUPPORT")
}

fun AppCompatActivity.startAboutBoostActivity(session: UserSessionManager) {
  startAppActivity(fragmentType = "ABOUT_BOOST")
}

fun AppCompatActivity.startManageCustomer(session: UserSessionManager) {
  startAppActivity(fragmentType = "MANAGE_CUSTOMER_VIEW")
}

fun AppCompatActivity.startAppActivity(bundle: Bundle = Bundle(), fragmentType: String) {
  try {
    val intent = Intent(this, Class.forName("com.nowfloats.helper.AppFragmentContainerActivity"))
    intent.putExtras(bundle)
    intent.putExtra("FRAGMENT_TYPE", fragmentType)
    startActivity(intent)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startPostUpdate(session: UserSessionManager?) {
  try {
    val webIntent = Intent(this, Class.forName("com.nowfloats.NavigationDrawer.Create_Message_Activity"))
    startActivity(webIntent)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startThirdPartyQueries(session: UserSessionManager?) {
  try {
    val webIntent = Intent(this, Class.forName("om.nowfloats.customerassistant.ThirdPartyQueriesActivity"))
    startActivity(webIntent)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startBoostExtension(session: UserSessionManager?) {
  try {
    val webIntent = Intent(this, Class.forName("com.nowfloats.NavigationDrawer.Boost360ExtensionsActivity"))
    startActivity(webIntent)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startReferralView(session: UserSessionManager?) {
  try {
    val webIntent = Intent(this, Class.forName("com.nowfloats.helper.ReferralTransActivity"))
    startActivity(webIntent)
    overridePendingTransition(0,0)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startMobileSite(session: UserSessionManager?, website: String) {
  try {
    val webIntent = Intent(this, Class.forName("com.nowfloats.NavigationDrawer.Mobile_Site_Activity"))
    webIntent.putExtra("WEBSITE_NAME", website)
    startActivity(webIntent)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startAddImageGallery(session: UserSessionManager?,isCreate:Boolean=true) {
  try {
    val webIntent = Intent(this, Class.forName("com.nowfloats.Image_Gallery.ImageGalleryActivity"))
    webIntent.putExtra("create_image", isCreate)
    startActivity(webIntent)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startProductGallery(session: UserSessionManager?) {
  try {
    val webIntent = Intent(this, Class.forName("com.nowfloats.ProductGallery.ProductGalleryActivity"))
    startActivity(webIntent)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startAddTestimonial(session: UserSessionManager?, isAdd: Boolean) {
  try {
    val webIntent = Intent(this, Class.forName("com.nowfloats.AccrossVerticals.Testimonials.TestimonialsActivity"))
    webIntent.putExtra("IS_ADD", isAdd)
    startActivity(webIntent)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startCreateCustomPage(session: UserSessionManager?, isAdd: Boolean) {
  try {
    val webIntent = Intent(this, Class.forName("com.nowfloats.CustomPage.CustomPageActivity"))
    webIntent.putExtra("IS_ADD", isAdd)
    startActivity(webIntent)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startListServiceProduct(session: UserSessionManager?) {
  try {
    val webIntent = Intent(this, Class.forName("com.nowfloats.ProductGallery.ProductCatalogActivity"))
    startActivity(webIntent)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startBookTable(session: UserSessionManager?) {
  try {
    val webIntent = Intent(this, Class.forName("com.nowfloats.Restaurants.BookATable.BookATableActivity"))
    startActivity(webIntent)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startPreSignUp(session: UserSessionManager?) {
  try {
    val webIntent = Intent(this, Class.forName("com.boost.presignup.PreSignUpActivity"))
    startActivity(webIntent)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startAddServiceProduct(session: UserSessionManager?) {
  val type: String = getProductType(session?.fP_AppExperienceCode)
  try {
    val webIntent = Intent(this, Class.forName("com.nowfloats.ProductGallery.ProductCatalogActivity"))
    webIntent.putExtra("IS_ADD", true)
    startActivity(webIntent)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startBookAppointmentConsult(session: UserSessionManager?, isConsult: Boolean = true) {
  try {
    val data = PreferenceData(clientId_ORDER, session?.userProfileId, WA_KEY, session?.fpTag, session?.userPrimaryMobile,
        session?.getDomainName(false), session?.fPEmail, session?.getFPDetails(Key_Preferences.LATITUDE),
        session?.getFPDetails(Key_Preferences.LONGITUDE), session?.fP_AppExperienceCode)
    val bundle = Bundle()
    bundle.putSerializable(IntentConstant.PREFERENCE_DATA.name, data)
    bundle.putBoolean(IntentConstant.IS_VIDEO.name, isConsult)
    this.startFragmentOrderActivity(com.inventoryorder.constant.FragmentType.CREATE_APPOINTMENT_VIEW, bundle, isResult = true)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startOrderAptConsultList(session: UserSessionManager?, isOrder: Boolean = false, isConsult: Boolean = false) {
  try {
    val data = PreferenceData(clientId_ORDER, session?.userProfileId, WA_KEY, session?.fpTag, session?.userPrimaryMobile,
        session?.getDomainName(false), session?.fPEmail, session?.getFPDetails(Key_Preferences.LATITUDE),
        session?.getFPDetails(Key_Preferences.LONGITUDE), session?.fP_AppExperienceCode)
    val bundle = Bundle()
    bundle.putSerializable(IntentConstant.PREFERENCE_DATA.name, data)
    val fragmentType = when {
      isOrder -> com.inventoryorder.constant.FragmentType.ALL_ORDER_VIEW
      isConsult -> com.inventoryorder.constant.FragmentType.ALL_VIDEO_CONSULT_VIEW
      else -> com.inventoryorder.constant.FragmentType.ALL_APPOINTMENT_VIEW
    }
    this.startFragmentOrderActivity(fragmentType, bundle, isResult = true)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startBusinessLogo(session: UserSessionManager?) {
  try {
    val webIntent = Intent(this, Class.forName("com.nowfloats.BusinessProfile.UI.UI.Business_Logo_Activity"))
    startActivity(webIntent)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startFeatureLogo(session: UserSessionManager?) {
  try {
    val webIntent = Intent(this, Class.forName("com.nowfloats.BusinessProfile.UI.UI.FeaturedImageActivity"))
    startActivity(webIntent)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startBusinessAddress(session: UserSessionManager?) {
  try {
    val webIntent = Intent(this, Class.forName("com.nowfloats.BusinessProfile.UI.UI.Business_Address_Activity"))
    startActivity(webIntent)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startBusinessEmail(session: UserSessionManager?) {
  try {
    val webIntent = Intent(this, Class.forName("com.nowfloats.BusinessProfile.UI.UI.ContactInformationActivity"))
    startActivity(webIntent)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startBusinessDescriptionEdit(session: UserSessionManager?) {
  try {
    val webIntent = Intent(this, Class.forName("com.nowfloats.BusinessProfile.UI.UI.Edit_Profile_Activity"))
    startActivity(webIntent)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}


fun AppCompatActivity.startBusinessContactInfo(session: UserSessionManager?) {
  try {
    val webIntent = Intent(this, Class.forName("com.nowfloats.BusinessProfile.UI.UI.ContactInformationActivity"))
    startActivity(webIntent)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startBusinessHours(session: UserSessionManager?) {
  try {
    val webIntent = Intent(this, Class.forName("com.nowfloats.BusinessProfile.UI.UI.BusinessHoursActivity"))
    startActivity(webIntent)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startFragmentsFactory(session: UserSessionManager?, fragmentType: String) {
  try {
    val webIntent = Intent(this, Class.forName("com.nowfloats.NavigationDrawer.businessApps.FragmentsFactoryActivity"))
    webIntent.putExtra("fragmentName", fragmentType)
    startActivity(webIntent)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startPricingPlan(session: UserSessionManager?) {
  try {
    val webIntent = Intent(this, Class.forName("com.nowfloats.Store.NewPricingPlansActivity"))
    startActivity(webIntent)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startWebViewPageLoad(session: UserSessionManager?, url: String?) {
  try {
    val intent = Intent(this, WebViewActivity::class.java)
    intent.putExtra(com.onboarding.nowfloats.constant.IntentConstant.DOMAIN_URL.name, url)
    startActivity(intent)
  } catch (e: Exception) {
    e.printStackTrace()
  }
}


fun AppCompatActivity.startSelfBrandedGateway(session: UserSessionManager?) {
  try {
    session?.getBundleDataKyc()?.let { startFragmentPaymentActivityNew(this, com.appservice.constant.FragmentType.PAYMENT_GATEWAY, it, false) }
  } catch (e: Exception) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startBusinessKycBoost(session: UserSessionManager?) {
  try {
    session?.getBundleDataKyc()?.let {
      if (session.isSelfBrandedKycAdd == true) {
        startFragmentPaymentActivityNew(this, com.appservice.constant.FragmentType.KYC_STATUS, it, false)
      } else startFragmentPaymentActivityNew(this, com.appservice.constant.FragmentType.BUSINESS_KYC_VIEW, it, false)
    }
  } catch (e: Exception) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startMyBankAccount(session: UserSessionManager?) {
  try {
    val bundle = Bundle()
    bundle.putString(com.appservice.constant.IntentConstant.CLIENT_ID.name, clientId)
    bundle.putString(com.appservice.constant.IntentConstant.USER_PROFILE_ID.name, session?.userProfileId)
    bundle.putString(com.appservice.constant.IntentConstant.FP_ID.name, session?.fPID)
    if (session?.isAccountSave() == true) {
      startFragmentAccountActivityNew(this, com.appservice.constant.FragmentType.BANK_ACCOUNT_DETAILS, bundle, false)
    } else {
      startFragmentAccountActivityNew(this, com.appservice.constant.FragmentType.ADD_BANK_ACCOUNT_START, bundle, false)
    }
  } catch (e: Exception) {
    e.printStackTrace()
  }
}

fun UserSessionManager.getBundleDataKyc(): Bundle? {
  val session = SessionData()
  session.clientId = clientId
  session.userProfileId = userProfileId
  session.fpId = fPID
  session.fpTag = fpTag
  session.experienceCode = fP_AppExperienceCode
  session.fpLogo = fPLogo
  session.fpEmail = fPEmail
  session.fpNumber = fPPrimaryContactNumber
  session.isSelfBrandedAdd = isSelfBrandedKycAdd ?: false
  session.isPaymentGateway = getStoreWidgets()?.contains(StatusKyc.CUSTOM_PAYMENTGATEWAY.name) ?: false
  val bundle = Bundle()
  bundle.putSerializable(com.appservice.constant.IntentConstant.SESSION_DATA.name, session)
  return bundle
}

fun AppCompatActivity.startListDigitalBrochure(session: UserSessionManager?) {
  try {
    val webIntent = Intent(this, Class.forName("com.nowfloats.manufacturing.digitalbrochures.DigitalBrochuresActivity"))
    startActivity(webIntent)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

//check is premium
fun AppCompatActivity.startAddDigitalBrochure(session: UserSessionManager?) {
  try {
    val webIntent = Intent(this, Class.forName("com.nowfloats.manufacturing.digitalbrochures.DigitalBrochuresDetailsActivity"))
    webIntent.putExtra("ScreenState", "new")
    startActivity(webIntent)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startListProjectAndTeams(session: UserSessionManager?) {
  try {
    val webIntent = Intent(this, Class.forName("com.nowfloats.manufacturing.projectandteams.ui.home.ProjectAndTermsActivity"))
    startActivity(webIntent)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startListTripAdvisor(session: UserSessionManager?) {
  try {
    val webIntent = Intent(this, Class.forName("ccom.nowfloats.hotel.tripadvisor.TripAdvisorActivity"))
    startActivity(webIntent)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startListProject(session: UserSessionManager?) {
  try {
    val webIntent = Intent(this, Class.forName("com.nowfloats.manufacturing.projectandteams.ui.project.ProjectActivity"))
    startActivity(webIntent)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startListTeams(session: UserSessionManager?) {
  try {
    val webIntent = Intent(this, Class.forName("com.nowfloats.manufacturing.projectandteams.ui.teams.TeamsActivity"))
    startActivity(webIntent)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startListSeasonalOffer(session: UserSessionManager?) {
  try {
    val webIntent = Intent(this, Class.forName("com.nowfloats.hotel.seasonalOffers.SeasonalOffersActivity"))
    startActivity(webIntent)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startAddSeasonalOffer(session: UserSessionManager?) {
  try {
    val webIntent = Intent(this, Class.forName("com.nowfloats.hotel.seasonalOffers.SeasonalOffersDetailsActivity"))
    webIntent.putExtra("ScreenState", "new")
    startActivity(webIntent)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startListToppers(session: UserSessionManager?) {
  try {
    val webIntent = Intent(this, Class.forName("com.nowfloats.education.toppers.ToppersActivity"))
    startActivity(webIntent)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startListBatches(session: UserSessionManager?) {
  try {
    val webIntent = Intent(this, Class.forName("com.nowfloats.education.batches.BatchesActivity"))
    startActivity(webIntent)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startNearByView(session: UserSessionManager?) {
  try {
    val webIntent = Intent(this, Class.forName("com.nowfloats.hotel.placesnearby.PlacesNearByActivity"))
    startActivity(webIntent)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startFacultyMember(session: UserSessionManager?) {
  try {
    val webIntent = Intent(this, Class.forName("com.nowfloats.education.faculty.FacultyActivity"))
    startActivity(webIntent)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}
//fun AppCompatActivity.referFriend(session: UserSessionManager){
//  if (!TextUtils.isEmpty(session.fPEmail)) {
//    InviteReferralsApi.getInstance(applicationContext).userDetails(
//        session.userProfileName,
//        session.fPEmail,
//        session.userPrimaryMobile,
//        REFERRAL_CAMPAIGN_CODE, null, null
//    )
//    inviteReferralLogin(session)
//  } else if (!TextUtils.isEmpty(session.userProfileEmail)) {
//    InviteReferralsApi.getInstance(applicationContext).userDetails(
//        session.userProfileName,
//        session.userProfileEmail,
//        session.userPrimaryMobile,
//        REFERRAL_CAMPAIGN_CODE, null, null
//    )
//    inviteReferralLogin(session)
//  } else {
//    Toast.makeText(applicationContext, "An unexpected error occured.", Toast.LENGTH_LONG).show()
//  }
//}
//
//fun AppCompatActivity.inviteReferralLogin(session: UserSessionManager) {
//  InviteReferralsApi.getInstance(this).userDetailListener(object : UserDetailsCallback() {
//    fun userDetails(jsonObject: JSONObject) {
//      Log.d("Referral Details", jsonObject.toString())
//      try {
//        val status = jsonObject["Authentication"].toString()
//        if (status.toLowerCase() == "success") {
//          InviteReferralsApi.getInstance(this).inline_btn(REFERRAL_CAMPAIGN_CODE)
//        } else {
//          Toast.makeText(applicationContext, "Authentication failed. Please try later.", Toast.LENGTH_SHORT).show()
//        }
//      } catch (e: JSONException) {
//        Toast.makeText(applicationContext, "Authentication failed. Please try later.", Toast.LENGTH_SHORT).show()
//      }
//    }
//  })
//}
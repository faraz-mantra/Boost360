package com.framework.pref

import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.text.TextUtils
import com.framework.pref.Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME
import com.framework.pref.Key_Preferences.GET_FP_DETAILS_TAG
import com.framework.pref.Key_Preferences.GET_FP_EXPERIENCE_CODE
import com.framework.pref.Key_Preferences.MAIN_PRIMARY_CONTACT_NUM
import com.framework.pref.Key_Preferences.WEBSITE_SHARE
import com.framework.utils.convertStringToList
import java.util.*
import kotlin.collections.ArrayList

class UserSessionManager(var activity: Context) {

  // Shared Preferences reference
  var pref = activity.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

  // Editor reference for Shared preferences
  var editor: SharedPreferences.Editor = pref.edit()

  private val PROFILE_ID = "user_profile_id"
  private val PROFILE_EMAIL = "user_profile_email"
  private val PROFILE_NUMBER = "user_profile_mobile"
  private val PROFILE_NAME = "user_profile_name"
  private val KEY_FP_NAME = "fpname"
  private val KEY_sourceClientId = "source_clientid"
  private val KEY_Visit_Count = "visitCount"
  private val KEY_Visitors_Count = "visitorsCount"
  private val KEY_Subcribers_Count = "subcribersCount"
  private val KEY_Search_Count = "SearchQueryCount"
  private val KEY_Enq_Count = "EnquiryCount"
  private val KEY_Order_Count = "OrderCount"
  private val KEY_Map_Visits_Count = "MapVisitsCount"
  private val KEY_Call_Count = "VmnCallCount"
  private val KEY_LATEST_ENQ_COUNT = "LatestEnquiryCount"
  private val KEY_LS = "local_store"
  private val KEY_website = "website_share"
  private val KEY_FP_EMAIL = "fpemail"
  private val KEY_LOGO_URI = "fplogouri"
  private val KEY_FP_LOGO: String? = null
  private val KEY_FIRST_TIME_Details = "firsttime"
  private val KEY_LAST_TIME = "popuptime"
  private val KEY_SUNDAY = "sunday"
  private val KEY_MONDAY = "monday"
  private val KEY_TUESDAY = "tuesday"
  private val KEY_WEDNESDAY = "wednesday"
  private val KEY_THURSDAY = "thursday"
  private val KEY_FRIDAY = "friday"
  private val KEY_SATURDAY = "saturdaty"
  private val KEY_START_TIME = "starttime"
  private val KEY_END_TIME = "endtime"
  private val KEY_FACEBOOK_NAME = "facebookname"
  private val KEY_FACEBOOK_IMPRESSIONS = "facebook_impressions"
  private val KEY_FACEBOOK_ACCESS_TOKEN = "facebookaccesstoken"
  private val KEY_FACEBOOK_PAGE = "facebookpage"
  private val KEY_FACEBOOK_PAGE_ID = "facebookpageid"
  private val KEY_SHOW_UPDATES = "showupdates"
  private val KEY_PAGE_ACCESS_TOKEN = "pagetoken"
  private val KEY_USER_ACCESS_TOKEN = "usertoken"
  private val KEY_FB_LIKE = "fblikekey"
  private val KEY_IS_ENTERPRISE = "isenterprise"
  private val KEY_IS_RESTRICTED = "ISrestricted"
  private val KEY_IS_AUTO_POST_ENABLED = "IsAutoPostEnabled"
  private val KEY_IS_SIGNUP_FROM_FACEBOOK = "SignUpFacebook"
  private val KEY_FACEBOOK_IMAGE_URL = "FacebookImageURL"
  private val IS_ACCOUNT_SAVE = "isAccountSave"
  private val IS_SELF_BRANDED_KYC_ADD = "isSelfBrandedKycAdd"
  private val KEY_FACEBOOK_PROFILE_DESCRIPTION = "FacebookProfileDescription"
  private val KEY_IS_THINKSITY = "isThinksity"
  private val KEY_IS_FREE_DOMAIN = "isFreeDomain"
  private val KEY_FP_TAG = "fptag"
  private val KEY_WEB_TEMPLATE_TYPE = "webTemplateType"
  private val KEY_BUSINESS_HOURS = "BusinessHoursMainKey"
  private val KEY_FP_SHARE_ENABLE = "fbShareEnabled"
  private val KEY_FP_PAGE_SHARE_ENABLE = "fbPageShareEnabled"


  fun Context.getPreferenceTwitter(): SharedPreferences {
    return getSharedPreferences(Key_Preferences.PREF_NAME_TWITTER, Context.MODE_PRIVATE)
  }

  //Create login session
  fun createUserLoginSession(name: String?, email: String?) {
    // Storing login value as TRUE
    editor.putBoolean(IS_USER_LOGIN, true)
    // Storing name in pref
    editor.putString(KEY_NAME, name)
    // Storing email in pref
    editor.putString(KEY_EMAIL, email)
    // commit changes
    editor.commit()
  }

  var fbShareEnabled: Boolean
    get() = pref.getBoolean(KEY_FP_SHARE_ENABLE, false)
    set(cnt) {
      editor.putBoolean(KEY_FP_SHARE_ENABLE, cnt)
      editor.apply()
    }

  var fbPageShareEnabled: Boolean
    get() = pref.getBoolean(KEY_FP_PAGE_SHARE_ENABLE, false)
    set(cnt) {
      editor.putBoolean(KEY_FP_PAGE_SHARE_ENABLE, cnt)
      editor.apply()
    }

  fun storeFPName(fpName: String?) {
    editor.putString(KEY_FP_NAME, fpName)
    editor.commit()
  }

  var userProfileId: String?
    get() = pref.getString(PROFILE_ID, null)
    set(profileId) {
      editor.putString(PROFILE_ID, profileId)
      editor.commit()
    }
  var userProfileEmail: String?
    get() = pref.getString(PROFILE_EMAIL, null)
    set(email) {
      editor.putString(PROFILE_EMAIL, email)
      editor.commit()
    }
  var userProfileMobile: String?
    get() = pref.getString(PROFILE_NUMBER, null)
    set(mobile) {
      editor.putString(PROFILE_NUMBER, mobile)
      editor.commit()
    }
  val userPrimaryMobile: String?
    get() = pref.getString(MAIN_PRIMARY_CONTACT_NUM, "")
  var userProfileName: String?
    get() = pref.getString(PROFILE_NAME, null)
    set(name) {
      editor.putString(PROFILE_NAME, name)
      editor.commit()
    }

  fun setUserLogin(`val`: Boolean) {
    editor.putBoolean(IS_USER_LOGIN, `val`).apply()
  }

  fun storeFpWebTempalteType(type: String?) {
    editor.putString(KEY_WEB_TEMPLATE_TYPE, type)
    editor.commit()
  }

  val webTemplateType: String?
    get() = pref.getString(KEY_WEB_TEMPLATE_TYPE, null)
  var visitsCount: String?
    get() = pref.getString(KEY_Visit_Count, "")
    set(cnt) {
      editor.putString(KEY_Visit_Count, cnt)
      editor.commit()
    }
  var visitorsCount: String?
    get() = pref.getString(KEY_Visitors_Count, "")
    set(cnt) {
      editor.putString(KEY_Visitors_Count, cnt)
      editor.commit()
    }
  var subcribersCount: String?
    get() = pref.getString(KEY_Subcribers_Count, "")
    set(cnt) {
      editor.putString(KEY_Subcribers_Count, cnt)
      editor.commit()
    }
  var searchCount: String?
    get() = pref.getString(KEY_Search_Count, "")
    set(cnt) {
      editor.putString(KEY_Search_Count, cnt)
      editor.apply()
    }
  var enquiryCount: String?
    get() = pref.getString(KEY_Enq_Count, "")
    set(count) {
      editor.putString(KEY_Enq_Count, count)
      editor.apply()
    }
  var orderCount: String?
    get() = pref.getString(KEY_Order_Count, "")
    set(count) {
      editor.putString(KEY_Order_Count, count)
      editor.apply()
    }
  var mapVisitsCount: String?
    get() = pref.getString(KEY_Map_Visits_Count, "")
    set(count) {
      editor.putString(KEY_Map_Visits_Count, count)
      editor.apply()
    }
  var vmnCallsCount: String?
    get() = pref.getString(KEY_Call_Count, "")
    set(count) {
      editor.putString(KEY_Call_Count, count)
      editor.apply()
    }
  var latestEnqCount: String?
    get() = pref.getString(KEY_LATEST_ENQ_COUNT, "0")
    set(count) {
      editor.putString(KEY_LATEST_ENQ_COUNT, count)
      editor.apply()
    }

  fun storeFpTag(tag: String?) {
    editor.putString(KEY_FP_TAG, tag)
    editor.apply()
  }

  var localStorePurchase: String?
    get() = pref.getString(KEY_LS, "")
    set(cnt) {
      editor.putString(KEY_LS, cnt)
      editor.apply()
    }
  var websiteshare: Boolean
    get() = pref.getBoolean(WEBSITE_SHARE, false)
    set(cnt) {
      editor.putBoolean(WEBSITE_SHARE, cnt)
      editor.apply()
    }
  var businessHours: Boolean
    get() = pref.getBoolean(KEY_BUSINESS_HOURS, false)
    set(cnt) {
      editor.putBoolean(KEY_BUSINESS_HOURS, cnt)
      editor.apply()
    }
  var isSignUpFromFacebook: String?
    get() = pref.getString(KEY_IS_SIGNUP_FROM_FACEBOOK, "")
    set(isSignUpFromFacebook) {
      editor.putString(KEY_IS_SIGNUP_FROM_FACEBOOK, isSignUpFromFacebook)
      editor.apply()
    }

  fun storeSourceClientId(`val`: String?) {
    editor.putString(KEY_sourceClientId, `val`)
    editor.apply()
  }

  fun getStoreWidgets(): List<String>? {
    val str = pref.getString(Key_Preferences.STORE_WIDGETS, "")
    return if (str.isNullOrEmpty()) ArrayList<String>() else convertStringToList(str)
  }

  val sourceClientId: String?
    get() = pref.getString(KEY_sourceClientId, "")
  val fPName: String?
    get() = pref.getString(GET_FP_DETAILS_BUSINESS_NAME, null)
  val fpTag: String?
    get() = pref.getString(GET_FP_DETAILS_TAG, null)
  val fP_AppExperienceCode: String?
    get() = pref.getString(GET_FP_EXPERIENCE_CODE, null)
  val isNonPhysicalProductExperienceCode: Boolean
    get() {
      val code = pref.getString(GET_FP_EXPERIENCE_CODE, null)
      return if (code != null) {
        when (code) {
          "RTL", "CAF", "MFG" -> false
          else -> true
        }
      } else false
    }
  val isLoginCheck: Boolean
    get() = fpTag != null && fPID != null && fP_AppExperienceCode != null && userProfileId != null
  val fPPrimaryContactNumber: String?
    get() = pref.getString(Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER, null)
  val rootAliasURI: String?
    get() = pref.getString(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI, null)
  val shareWebsite: Boolean
    get() = pref.getBoolean("shareWebsite", false)

  fun storeISEnterprise(isEnterprise: String?) {
    editor.putString(KEY_IS_ENTERPRISE, isEnterprise)
    editor.apply()
  }

  val iSEnterprise: String?
    get() = pref.getString(KEY_IS_ENTERPRISE, "false")

  fun storeIsRestricted(isRestricted: String?) {
    editor.putString(KEY_IS_RESTRICTED, isRestricted)
    editor.apply()
  }

  val isRestricted: String?
    get() = pref.getString(KEY_IS_RESTRICTED, "false")

  fun storeIsThinksity(isThinksity: String?) {
    editor.putString(KEY_IS_THINKSITY, isThinksity)
    editor.apply()
  }

  val isThinksity: String?
    get() = pref.getString(KEY_IS_THINKSITY, "false")

  fun storeIsAutoPostEnabled(isEnabled: String?) {
    editor.putString(KEY_IS_AUTO_POST_ENABLED, isEnabled)
    editor.apply()
  }

  val isAutoPostEnabled: String?
    get() = pref.getString(KEY_IS_AUTO_POST_ENABLED, "false")

  fun storePageAccessToken(pageAccessToken: String?) {
    editor.putString(KEY_PAGE_ACCESS_TOKEN, pageAccessToken)
    editor.apply()
  }

  val pageAccessToken: String?
    get() = pref.getString(KEY_PAGE_ACCESS_TOKEN, null)

  fun storeUserAccessToken(userAccessToken: String?) {
    editor.putString(KEY_USER_ACCESS_TOKEN, userAccessToken)
    editor.apply()
  }

  val userAccessToken: String?
    get() = pref.getString(KEY_USER_ACCESS_TOKEN, null)

  fun storeFBLikeBoxInfo(isFBLikeBoxPresent: String?) {
    editor.putString(KEY_FB_LIKE, isFBLikeBoxPresent)
    editor.apply()
  }

  val storeFBLikeBoxInfo: String?
    get() = pref.getString(KEY_FB_LIKE, null)

  fun storeShowUpdates(showUpdates: Boolean) {
    editor.putBoolean(KEY_SHOW_UPDATES, showUpdates)
    editor.apply()
  }

  var showUpdates: Boolean
    get() = pref.getBoolean(KEY_SHOW_UPDATES, true)
    set(showUpdates) {
      editor.putBoolean(KEY_SHOW_UPDATES, showUpdates)
      editor.apply()
    }

  fun storeFacebookName(facebookName: String?) {
    editor.putString(KEY_FACEBOOK_NAME, facebookName)
    editor.apply()
  }

  val facebookName: String?
    get() = pref.getString(KEY_FACEBOOK_NAME, null)

  fun storeFacebookImpressions(facebookImpression: String?) {
    editor.putString(KEY_FACEBOOK_IMPRESSIONS, facebookImpression)
    editor.apply()
  }

  val facebookImpressions: String?
    get() = pref.getString(KEY_FACEBOOK_IMPRESSIONS, null)

  fun storeFacebookAccessToken(token: String?) {
    editor.putString(KEY_FACEBOOK_ACCESS_TOKEN, token)
    editor.apply()
  }

  val facebookAccessToken: String?
    get() = pref.getString(KEY_FACEBOOK_ACCESS_TOKEN, null)

  fun storeFacebookPage(facebookName: String?) {
    editor.putString(KEY_FACEBOOK_PAGE, facebookName)
    editor.apply()
  }

  val facebookPage: String?
    get() = pref.getString(KEY_FACEBOOK_PAGE, null)

  fun storeFacebookPageID(id: String?) {
    editor.putString(KEY_FACEBOOK_PAGE_ID, id)
    editor.apply()
  }

  val facebookPageID: String?
    get() = pref.getString(KEY_FACEBOOK_PAGE_ID, null)
  val fPEmail: String?
    get() = pref.getString(Key_Preferences.GET_FP_DETAILS_EMAIL, null)

  fun storeLogoURI(fpLogoURI: String?) {
    editor.putString(KEY_LOGO_URI, fpLogoURI)
    editor.apply()
  }

  fun storeMondayChecked(value: Boolean) {
    editor.putBoolean(KEY_MONDAY, value)
    editor.apply()
  }

  val monayChecked: Boolean
    get() = pref.getBoolean(KEY_MONDAY, true)

  fun storeTuesdayChecked(value: Boolean) {
    editor.putBoolean(KEY_TUESDAY, value)
    editor.apply()
  }

  val tuesdayChecked: Boolean
    get() = pref.getBoolean(KEY_TUESDAY, false)

  fun storeWednesdayChecked(value: Boolean) {
    editor.putBoolean(KEY_WEDNESDAY, value)
    editor.apply()
  }

  val wednesdayChecked: Boolean
    get() = pref.getBoolean(KEY_WEDNESDAY, false)

  fun storeThursdayChecked(value: Boolean) {
    editor.putBoolean(KEY_THURSDAY, value)
    editor.apply()
  }

  val thursdayChecked: Boolean
    get() = pref.getBoolean(KEY_THURSDAY, false)

  fun storeFridayChecked(value: Boolean) {
    editor.putBoolean(KEY_FRIDAY, value)
    editor.apply()
  }

  val fridayChecked: Boolean
    get() = pref.getBoolean(KEY_FRIDAY, false)

  fun storeSaturdayChecked(value: Boolean) {
    editor.putBoolean(KEY_SATURDAY, value)
    editor.apply()
  }

  val saturdayChecked: Boolean
    get() = pref.getBoolean(KEY_SATURDAY, false)

  fun storeSundayChecked(value: Boolean) {
    editor.putBoolean(KEY_SUNDAY, value)
    editor.apply()
  }

  val sundayChecked: Boolean
    get() = pref.getBoolean(KEY_SUNDAY, false)

  fun storeStartTime(startTime: String?) {
    editor.putString(KEY_START_TIME, startTime)
    editor.apply()
  }

  val startTime: String?
    get() = pref.getString(KEY_START_TIME, "10:00 AM")

  fun storeEndTime(startTime: String?) {
    editor.putString(KEY_END_TIME, startTime)
    editor.apply()
  }

  val endTime: String?
    get() = pref.getString(KEY_END_TIME, "06:00 PM")
  val logoURI: String?
    get() = pref.getString(KEY_LOGO_URI, null)

  fun storeFPID(fpID: String?) {
    if (fpID == null) return
    val fpId = fpID.replace("\"".toRegex(), "")
    // fpId = fpId.replace
    editor.putString(UserSessionManager.Companion.KEY_FP_ID, fpId)
    editor.apply()
  }

  fun isFacebookAuthDone(done: Boolean) {
    editor.putBoolean("FACEBOOK", done)
    editor.apply()
  }

  val facebookAuthDone: Boolean
    get() = pref.getBoolean("FACEBOOK", false)

  fun isOTPAuthDone(done: Boolean) {
    editor.putBoolean("OTP", done)
    editor.apply()
  }

  fun isAuthdone(key: String?, done: Boolean) {
    editor.putBoolean(key, done)
    editor.apply()
  }

  val oTPAuthDone: Boolean
    get() = pref.getBoolean("OTP", false)

  fun isGoogleAuthDone(done: Boolean) {
    editor.putBoolean("GOOGLE", done)
    editor.apply()
  }

  val googleAuthDone: Boolean
    get() = pref.getBoolean("GOOGLE", false)

  val fPID: String?
    get() = pref.getString(KEY_FP_ID, null)

  val isAllAuthSet: Boolean
    get() = googleAuthDone && oTPAuthDone && facebookAuthDone

  fun storeFacebookPageURL(imageURL: String?) {
    editor.putString(KEY_FACEBOOK_IMAGE_URL, imageURL)
    editor.apply()
  }

  val facebookPageURL: String?
    get() = pref.getString(KEY_FACEBOOK_IMAGE_URL, "")

  var isSelfBrandedKycAdd: Boolean?
    get() = pref.getBoolean(IS_SELF_BRANDED_KYC_ADD, false)
    set(b) {
      editor.putBoolean(IS_SELF_BRANDED_KYC_ADD, b!!)
      editor.apply()
    }

  fun setAccountSave(b: Boolean?) {
    editor.putBoolean(IS_ACCOUNT_SAVE, b!!)
    editor.apply()
  }

  fun isAccountSave(): Boolean {
    return pref.getBoolean(IS_ACCOUNT_SAVE, false)
  }

  fun storeFacebookProfileDescription(description: String?) {
    editor.putString(KEY_FACEBOOK_PROFILE_DESCRIPTION, description)
    editor.apply()
  }

  val facebookProfileDescription: String?
    get() = pref.getString(KEY_FACEBOOK_PROFILE_DESCRIPTION, "")

  fun storeIsFreeDomainDisplayed(isFreeDomain: String?) {
    editor.putString(KEY_IS_FREE_DOMAIN, isFreeDomain)
    editor.apply()
  }

  val isFreeDomainDisplayed: String?
    get() = pref.getString(KEY_IS_FREE_DOMAIN, "")

  fun savePackageStatus(packegeId: String?, `val`: Boolean) {
    try {
      editor.putBoolean(packegeId, `val`)
      editor.apply()
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  fun getPackageStatus(packageId: String?): Boolean {
    return pref.getBoolean(packageId, false)
  }

  fun storeBooleanDetails(key: String, value: Boolean) {
    editor.putBoolean(key.trim { it <= ' ' }, value).apply()
  }

  fun getBooleanDetails(key: String?): Boolean {
    return pref.getBoolean(key, false)
  }

  fun storeIntDetails(key: String, value: Int) {
    editor.putInt(key.trim { it <= ' ' }, value).apply()
  }

  fun getIntDetails(key: String?): Int {
    return pref.getInt(key, 0)
  }

  fun storeFPDetails(key: String, value: String?) {
    try {
      editor.putString(key.trim { it <= ' ' }, value?.trim { it <= ' ' } ?: "")
      editor.apply()
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  fun getFPDetails(key: String): String? {
    return pref.getString(key.trim { it <= ' ' }, "")
  }

  fun storeAccessToken(tokenValue: String?) {
    editor.putString(Key_Preferences.ACCESS_TOKEN_AUTH, tokenValue)
    editor.apply()
  }

  val getAccessToken: String?
    get() = pref.getString(Key_Preferences.ACCESS_TOKEN_AUTH, "")

  val isBoostBubbleEnabled: Boolean
    get() = pref.getBoolean(Key_Preferences.IS_BOOST_BUBBLE_ENABLED, false)
  val isCustomerAssistantEnabled: Boolean
    get() = pref.getBoolean(Key_Preferences.IS_CUSTOMER_ASSISTANT_ENABLED, false)

  fun setBubbleStatus(flag: Boolean) {
    pref.edit().putBoolean(Key_Preferences.IS_BOOST_BUBBLE_ENABLED, flag).apply()
  }

  fun setHeader(auth: String?) {
    pref.edit().putString(Key_Preferences.AUTHORIZATION, auth).apply()
  }

  fun clearHeader() {
    pref.edit().putString(Key_Preferences.AUTHORIZATION, "").apply()
  }

  fun setCustomerAssistantStatus(flag: Boolean) {
    pref.edit().putBoolean(Key_Preferences.IS_CUSTOMER_ASSISTANT_ENABLED, flag).apply()
  }

  fun setBubbleShareProducts(flag: Boolean) {
    pref.edit().putBoolean(Key_Preferences.HAS_BUBBLE_SHARE_PRODUCTS, flag).apply()
  }

  var bubbleTime: Long
    get() = pref.getLong(Key_Preferences.SHOW_BUBBLE_TIME, 0)
    set(time) {
      pref.edit().putLong(Key_Preferences.SHOW_BUBBLE_TIME, time).apply()
    }

  fun storeGalleryImages(imagePath: String?) {
    editor.putString(UserSessionManager.Companion.KEY_GALLLERY_IMAGES, imagePath)
    editor.apply()
  }

  val fPLogo: String?
    get() = pref.getString(Key_Preferences.GET_FP_DETAILS_LogoUrl, null)
  val fPParentId: String?
    get() {
      var id = pref.getString(Key_Preferences.GET_FP_DETAILS_PARENTID, null)
      if (TextUtils.isEmpty(id)) id = pref.getString(UserSessionManager.Companion.KEY_FP_ID, null)
      return id
    }

  fun storeFPLogo(logo: String?) {
    editor.putString(Key_Preferences.GET_FP_DETAILS_LogoUrl, logo)
    editor.apply()
  }

  val storedGalleryImages: ArrayList<String>
    get() {
      val imagesList = pref.getString(UserSessionManager.Companion.KEY_GALLLERY_IMAGES, null)
      val replace = imagesList!!.replace("[", "")
      val replace1 = replace.replace("]", "")
      val replace2 = replace1.replace(" ", "")
      return ArrayList(listOf(*replace2.split(",".toRegex()).toTypedArray()))
    }

  /**
   * sent_check login method will check user login status
   * If false it will redirect user to login page
   * Else do anything
   */
  fun checkLogin(): Boolean {
    // sent_check login status
    val db = DataBase(activity)
    val cursor: Cursor = db.loginStatus
    var isLogin = false
    if (cursor.count > 0) {
      if (cursor.moveToNext()) {
        val LoginStatus = cursor.getString(0)
        val fpid = cursor.getString(1)
        val facebookName = cursor.getString(2)
        val facebookPage = cursor.getString(3)
        val facebookAccessToken = cursor.getString(4)
        val facebookpageToken = cursor.getString(5)
        val facepageId = cursor.getString(6)
        val isRestricted = cursor.getString(9)
        val isEnterprise = cursor.getString(8)
        if (LoginStatus == "true") {
          isLogin = true
          storeFPID(fpid)
          storePageAccessToken(facebookpageToken)
          storeIsRestricted(isRestricted)
          storeISEnterprise(isEnterprise)
          //                    Constants.FACEBOOK_PAGE_ID = facepageId;
          storeFacebookPageID(facepageId)
          if (facebookName != null && facebookName.trim { it <= ' ' }.isNotEmpty()) {
            storeFacebookName(facebookName)
            //                        Constants.FACEBOOK_USER_NAME = facebookName;
          }
          if (facebookPage != null && facebookPage.trim { it <= ' ' }.isNotEmpty()) storeFacebookPage(facebookPage)
          if (facebookAccessToken != null && facebookAccessToken.trim { it <= ' ' }.isNotEmpty()) {
            storeFacebookAccessToken(facebookAccessToken)
          }
        } else isLogin = false
      }
    }
    setUserLogin(isLogin)
    return isLogin
  }

  fun store_FIRST_TIME(value: Boolean) {
    editor.putBoolean(KEY_FIRST_TIME_Details, value)
    editor.apply()
  }

  fun get_FIRST_TIME(): Boolean {
    return pref.getBoolean(KEY_FIRST_TIME_Details, true)
  }

  fun store_SHOW_POP_UP_TIME(time: Long) {
    editor.putLong(KEY_LAST_TIME, time)
    editor.apply()
  }

  fun get_SHOW_POP_UP_TIME(): Long {
    return pref.getLong(KEY_LAST_TIME, 0)
  }//Use hashmap to store user credentials

  // return user name, user email id
  /**
   * Get stored session data
   */
  val userDetails: HashMap<String, String?>
    get() {
      //Use hashmap to store user credentials
      val user = HashMap<String, String?>()
      // user name
      user[KEY_NAME] = pref.getString(KEY_NAME, null)
      // user email id
      user[KEY_EMAIL] = pref.getString(KEY_EMAIL, null)
      // return user
      return user
    }

  /**
   * Clear session details
   */
  fun logoutUser() {

  }

  var isSiteAppearanceShown: Boolean
    get() = pref.getBoolean(Key_Preferences.IS_FP_SITE_APPEARNCE_SHOWN, false)
    set(valSiteAppearance) {
      editor.putBoolean(Key_Preferences.IS_FP_SITE_APPEARNCE_SHOWN, true)
      editor.apply()
    }

  // sent_check for login
  val isUserLoggedIn: Boolean
    get() = pref.getBoolean(IS_USER_LOGIN, false)

  fun setUserSignUpComplete(`val`: Boolean) {
    editor.putBoolean(IS_SIGN_UP_COMPLETE, `val`).apply()
  }

  val isUserSignUpComplete: Boolean
    get() = pref.getBoolean(IS_SIGN_UP_COMPLETE, false)

  var siteHealth: Int
    get() = pref.getInt(Key_Preferences.SITE_HEALTH, 0)
    set(siteMeterTotalWeight) {
      editor.putInt(Key_Preferences.SITE_HEALTH, siteMeterTotalWeight).apply()
    }
  var productsCount: Int
    get() = pref.getInt(Key_Preferences.PRODUCTS_COUNT, 0)
    set(size) {
      editor.putInt(Key_Preferences.PRODUCTS_COUNT, size).apply()
    }
  var onBoardingStatus: Boolean
    get() = pref.getBoolean(Key_Preferences.ON_BOARDING_STATUS, false)
    set(flag) {
      editor.putBoolean(Key_Preferences.ON_BOARDING_STATUS, flag).apply()
    }
  var customPageCount: Int
    get() = pref.getInt(Key_Preferences.CUSTOM_PAGE, 0)
    set(size) {
      editor.putInt(Key_Preferences.CUSTOM_PAGE, size).apply()
    }

  companion object {
    // All Shared Preferences Keys
    private const val IS_USER_LOGIN = "IsUserLoggedIn"
    private const val IS_SIGN_UP_COMPLETE = "IsSignUpComplete"

    // User name (make variable public to access from outside)
    const val KEY_NAME = "name"

    // Email address (make variable public to access from outside)
    const val KEY_EMAIL = "email"
    const val KEY_FP_ID = "fpid"
    const val KEY_FP_Details = "fpdetails"
    private const val KEY_FP_Messages = "fpmessages"
    private const val KEY_GALLLERY_IMAGES = "gallery"
  }
}
fun UserSessionManager.getDomainName(isRemoveHttp: Boolean = false): String? {
  val rootAliasUri = getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI)?.toLowerCase(Locale.ROOT)
  val normalUri = "https://${getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG)?.toLowerCase(Locale.ROOT)}.nowfloats.com"
  return if (rootAliasUri.isNullOrEmpty().not() && rootAliasUri != "null") {
    return if (isRemoveHttp && rootAliasUri!!.contains("http://")) rootAliasUri.replace("http://", "")
    else if (isRemoveHttp && rootAliasUri!!.contains("https://")) rootAliasUri.replace("https://", "") else rootAliasUri
  } else normalUri
}
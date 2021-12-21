package com.dashboard.rest

object EndPoints {

  //TODO NFX API WITH FLOAT
  const val WITH_FLOATS_BASE = "https://api.withfloats.com/"
  const val USER_ACCOUNT_DETAIL = "discover/v9/business/paymentProfile/{fpId}"
  const val CREATE_PAYMENT = "discover/v9/business/paymentProfile/create"
  const val UPDATE_PAYMENT = "discover/v9/business/paymentProfile/bankDetails/update/{fpId}"

  // NFX APIs
  const val DEV_BOOST_KIT_URL = "https://developer.api.boostkit.dev/"
  const val UPGRADE_PREMIUM_BANNER = "language/v1/upgrade/get-data"
  const val UPGRADE_DASHBOARD_BANNER = "language/v1/dashboard/get-data"

  // Plugin APIs
  const val PLUGIN_FLOATS_URL = "https://plugin.withfloats.com/"
  const val DOMAIN_DETAIL = "DomainService/v3/GetDomainDetailsForFloatingPoint/{fpTag}"


  //TODO NFX API WITH FLOAT
  const val BOOST_KIT_NEW_BASE = "https://developers.api.boostkit.dev/"
  const val SEARCH_ANALYTICS = "api/webanalytics/GetDetailedSearchAnalyticsForDateRange"

  //TODO NFX API WITH FLOAT
  const val WITH_FLOATS_TWO_BASE = "https://api2.withfloats.com/"
  const val CREATE_BUSINESS_LOGO = "Discover/v1/floatingPoint/createLogoImage/"
  //FIREBASE TOKEN
  const val GET_FIREBASE_TOKEN = "discover/v1/FloatingPoint/GetFirebaseAuthToken"


  //TODO NOWFLOAT API
  const val NOW_FLOATS_BASE = "https://api.nowfloats.com/"
  const val WEBSITE_THEME_GET = "/Discover/v1/floatingPoint/getThemeCustomization"
  const val WEBSITE_THEME_UPDATE = "/Discover/v1/floatingPoint/updateThemeCustomization"

  //TODO FP UPADTE API
  const val FLOATING_POINT_UPDATE = "Discover/v1/FloatingPoint/update/"
  //TODO WEBACTION API
  const val WEB_ACTION_API_BASE = "https://webaction.api.boostkit.dev/api/v1/"
  const val OWNER_INFO_DATA = "about_us/get-data"
  const val OWNER_INFO_ADD_DATA = "about_us/add-data"
  const val OWNER_INFO_UPDATE_DATA = "about_us/update-data"

  // TODO WEBACTIONS KITSUNE TOOLS
  const val WEB_ACTION_KITSUNE_BASE = "https://webactions.kitsune.tools/api/v1/"
  const val WEB_ACTION_KITSUNE_UPLOAD_FILE = "about_us/upload-file"

  //upload user profile image
  const val UPLOAD_USER_PROFILE_IMAGE="/user/v9/floatingpoint/createUserProfileImage"
  //get user profile details
  const val GET_USER_PROFILE_DETAILS="/user/v9/floatingpoint/GetUserProfileDetails"
  //update user name
  const val UPDATE_USER_NAME="/user/v9/floatingpoint/updateUserName"
  //update email
  const val SEND_OTP_EMAIL="discover/v1/floatingpoint/SendOTPToEmail"
  const val UPDATE_EMAIL="/user/v9/floatingPoint/updateRegisteredEmail"

  //update mobile
  const val SEND_OTP_MOBILE="discover/v1/floatingpoint/SendOTPIndia"
  const val UPDATE_MOBILE="/user/v9/floatingPoint/updateRegisteredMobile"

  //update whatsapp
  const val UPDATE_WHATSAPP="/user/v9/floatingPoint/updateWhatsapp"

  // TODO US CENTRAL NOW FLOATS API
  const val US_CENTRAL_NOW_FLOATS_CLOUD_FUNCTIONS = "https://us-central1-nowfloats-boost.cloudfunctions.net/"
  const val DISABLE_NOTIFICATION = "disableNotification"

  //TODO API 2 WITH FLOATS BASE
  const val API_2_WITH_FLOATS_BASE = "http://api2.withfloats.com/"
  const val REPUBLISH_WEBSITE = "kitsune/v2/InvalidateFLMCache"

}


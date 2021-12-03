package com.boost.presignin.rest

object EndPoints {

  const val BOOST_KIT_DEV_BASE = "https://developer.api.boostkit.dev/"
  const val GET_CATEGORIES="language/v1/categories/get-data"

  //TODO NFX API WITH FLOAT
  const val WITH_FLOATS_BASE = "https://api.withfloats.com/"
  const val PUT_CREATE_BUSINESS_URL = "discover/v5/FloatingPoint/create"
  const val PUT_CREATE_BUSINESS_V6_URL = "discover/v6/FloatingPoint/create"
  const val POST_ACTIVATE_PURCHASED_ORDER = "Payment/v9/floatingpoint/ActivatePurchaseOrder"

  const val USER_DETAILS_WITH_FLOAT = "/discover/v9/business/paymentProfile/{fpId}"
  const val VERIFY_PHONE = "/discover/v1/floatingPoint/verifyPrimaryNumber"
  const val VERIFY_EMAIL = "/discover/v1/floatingPoint/verifyEmail"

  //TODO NFX API WITH FLOAT TWO
  const val WITH_FLOATS_TWO_BASE = "https://api2.withfloats.com/"
  const val CREATE_MERCHANT_PROFILE = "/user/v9/floatingPoint/CreateMerchantProfile"
  const val VERIFY_LOGIN = "/discover/v1/floatingPoint/verifyLogin"
  const val CHANGE_PASSWORD = "/discover/v1/floatingpoint/changePassword"
  const val FORGET_PASSWORD = "/Discover/v1/floatingpoint/forgotPassword"
  const val LOG_OUT = "/Discover/v1/floatingpoint/notification/unregisterChannel"
  const val CONNECT_MERCHANT_AUTH_PROVIDER = "/user/v9/floatingPoint/ConnectMerchantAuthProvider"
  const val CHECK_MOBILE_IS_REGISTERED = "/discover/v1/floatingpoint/CheckIfMobileIsRegistered"
  const val GET_FP_DETAILS_BY_PHONE = "/discover/v1/floatingPoint/getfpdetailsbynumber"
  const val SEND_OTP_INDIA = "/discover/v1/floatingpoint/SendOTPIndia"
  const val VERIFY_OTP = "/discover/v1/floatingpoint/VerifyOTP"
  const val VERIFY_LOGIN_OTP = "/discover/v1/floatingpoint/VerifyLoginOTP"
  const val CREATE_ACCESS_TOKEN = "/discover/v1/FloatingPoint/AccessToken/Create"
  const val FP_LIST_REGISTERED_MOBILE = "/discover/v1/floatingpoint/GetFPListforRegisteredMobile"
  const val GET_FP_DETAILS = "/Discover/v3/floatingPoint/nf-app/{fpid}"
  const val POST_BUSINESS_DOMAIN_URL = "discover/v1/floatingPoint/verifyUniqueTag"
  const val POST_BUSINESS_DOMAIN_SUGGEST = "discover/v1/floatingPoint/suggestTag"

  const val REGISTER_CHANNEL = "/Discover/v1/floatingpoint/notification/registerChannel"

  //todo NFX API
  var NFX_WITH_NOWFLOATS: String = "https://nfx.withfloats.com"

  //todo webaction boost kit api url
  const val WEB_ACTION_BOOST_KIT_API_URL = "https://webaction.api.boostkit.dev"
  const val GET_DATA = "/api/v1/kycdoc/get-data"

  const val RIA_WITH_FLOATS_BASE = "https://ria.withfloats.com"
  const val WHATSAPP_OPT_IN="/api/WhatsApp/OptIn"

}



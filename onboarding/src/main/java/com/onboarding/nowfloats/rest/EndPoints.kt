package com.onboarding.nowfloats.rest

object EndPoints {

  // Base With Floats APIs
  const val WITH_FLOATS_BASE_URL = "https://api.withfloats.com/"
  const val POST_BUSINESS_DOMAIN_URL = "discover/v1/floatingPoint/verifyUniqueTag"
  const val POST_BUSINESS_DOMAIN_SUGGEST = "discover/v1/floatingPoint/suggestTag"
  const val PUT_CREATE_BUSINESS_URL = "discover/v5/FloatingPoint/create"
  const val PUT_UPLOAD_CREATE_IMAGE = "discover/v1/floatingpoint/createImage"
  const val PUT_UPLOAD_SECONDARY_IMAGE = "discover/v1/floatingpoint/createSecondaryImage"
  const val PUT_UPLOAD_BUSINESS_LOGO = "discover/v1/floatingpoint/createLogoImage"
  const val PUT_UPLOAD_PROFILE = "user/v9/floatingpoint/createUserProfileImage"
  const val POST_ACTIVATE_PURCHASED_ORDER = "Payment/v9/floatingpoint/ActivatePurchaseOrder"

  //todo validation email phone
  const val VERIFY_PHONE = "/discover/v1/floatingPoint/verifyPrimaryNumber"
  const val VERIFY_EMAIL = "/discover/v1/floatingPoint/verifyEmail"

  // Base With Floats APIs
  const val RIA_WITH_FLOATS_BASE_URL = "https://ria.withfloats.com/"
  const val RIA_WHATSAPP = "api/WhatsApp/OptIn"

  // Base With Floats APIs
  const val BOOST_FLOATS_BASE_URL = "https://boost.nowfloats.com"
  const val MERCHANT_PROFILE = "/Home/MerchantProfileStatus"

  // NFX APIs
  const val NFX_BASE_URL = "https://nfx.withfloats.com/"
  const val POST_UPDATE_CHANNEL_ACCESS_TOKENS_URL = "dataexchange/v1/updateAccessTokens"
  const val GET_CHANNELS_ACCESS_TOKEN = "dataexchange/v1/getAccessTokens"
  const val NFX_PROCESS_URL = "dataexchange/v1/process"
  const val NFX_CHANNELS_STATUS = "dataexchange/v2/channelstatus"
  const val NFX_CHANNELS_INSIGHTS = "dataexchange/v1/account/insights"

  // Web Action APIs
  const val WEB_ACTION_BASE_URL = "https://webaction.api.boostkit.dev/"
  const val POST_UPDATE_WHATSAPP_URL = "api/v1/whatsapp_number/add-data"
  const val GET_WHATSAPP_BUSINESS = "api/v1/whatsapp_number/get-data"

  //Google my business(GMB)
  const val GOOGLE_BASE_URL = "https://www.googleapis.com/"
  const val GMB_BASE_URL = "https://mybusiness.googleapis.com/"
  const val GET_GMB_ACCOUNT = "v4/accounts/{user_id}"
  const val GET_GMB_ACCOUNT_LIST = "v4/accounts"
  const val GET_GMB_ACCOUNT_LOCATIONS = "v4/accounts/{user_id}/locations"
  const val POST_GOOGLE_AUTH_TOKENS = "oauth2/v4/token"

  // TODO DEVELOPER API BOOST KIT DEV
  const val DEVELOPER_API_BOOST_KIT_DEV_BASE_URL = "https://developer.api.boostkit.dev/language/v1/"
  const val GET_FEATURE_SUPPORT_VIDEOS = "featurevideos/get-data"
}
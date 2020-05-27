package com.onboarding.nowfloats.rest

object EndPoints {

    // Base With Floats APIs
    const val WITH_FLOATS_BASE_URL = "https://api.withfloats.com/"
    const val POST_BUSINESS_DOMAIN_URL = "discover/v1/floatingPoint/verifyUniqueTag"
    const val POST_BUSINESS_DOMAIN_SUGGEST = "discover/v1/floatingPoint/suggestTag"

    // NFX APIs
    const val NFX_BASE_URL = "https://nfx.withfloats.com/"
    const val POST_UPDATE_CHANNEL_ACCESS_TOKENS_URL = "dataexchange/v1/updateAccessTokens"
  const val GET_CHANNELS_ACCESS_TOKEN = "dataexchange/v1/getAccessTokens"
    const val PUT_CREATE_BUSINESS_URL = "discover/v5/FloatingPoint/create"
    const val PUT_UPLOAD_BUSINESS_LOGO = "discover/v1/floatingpoint/createLogoImage"
    const val PUT_UPLOAD_PROFILE = "user/v9/floatingpoint/createUserProfileImage"

    // Web Action APIs
    const val WEB_ACTION_BASE_URL = "https://webaction.api.boostkit.dev/"
    const val POST_UPDATE_WHATSAPP_URL = "api/v1/whatsapp_number/add-data"
  const val GET_WHATSAPP_BUSINESS = "api/v1/whatsapp_number/get-data"
}
package com.dashboard.rest

object EndPoints {

  //TODO NFX API WITH FLOAT
  const val WITH_FLOATS_BASE = "https://api.withfloats.com/"
  const val USER_ACCOUNT_DETAIL = "discover/v9/business/paymentProfile/{fpId}"
  const val CREATE_PAYMENT = "discover/v9/business/paymentProfile/create"
  const val UPDATE_PAYMENT = "discover/v9/business/paymentProfile/bankDetails/update/{fpId}"

  // NFX APIs
  const val NFX_BASE_URL = "https://nfx.withfloats.com/"
  const val POST_UPDATE_CHANNEL_ACCESS_TOKENS_URL = "dataexchange/v1/updateAccessTokens"
  const val GET_CHANNELS_ACCESS_TOKEN = "dataexchange/v1/getAccessTokens"
  const val NFX_PROCESS_URL = "dataexchange/v1/process"

  // NFX APIs
  const val DEV_BOOST_KIT_URL = "https://developer.api.boostkit.dev/"
  const val UPGRADE_PREMIUM_BANNER = "language/v1/upgrade/get-data"
  // Plugin APIs
  const val PLUGIN_FLOATS_URL = "https://plugin.withfloats.com/"
  const val DOMAIN_DETAIL = "DomainService/v3/GetDomainDetailsForFloatingPoint/{fpTag}"



}


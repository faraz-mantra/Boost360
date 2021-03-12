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
  const val UPGRADE_DASHBOARD_BANNER = "language/v1/dashboard/get-data"


  // Plugin APIs
  const val PLUGIN_FLOATS_URL = "https://plugin.withfloats.com/"
  const val DOMAIN_DETAIL = "DomainService/v3/GetDomainDetailsForFloatingPoint/{fpTag}"


  //TODO NFX API WITH FLOAT
  const val BOOST_KIT_NEW_BASE = "https://developers.api.boostkit.dev/"
  const val SEARCH_ANALYTICS = "api/webanalytics/GetDetailedSearchAnalyticsForDateRange"

  //TODO WEBACTION API
  const val WEB_ACTION_API_BASE = "https://webaction.api.boostkit.dev/api/v1/"
  const val OWNER_INFO_DATA = "about_us/get-data"
  const val OWNER_INFO_ADD_DATA = "about_us/add-data"
  const val OWNER_INFO_UPDATE_DATA = "about_us/update-data"

  // TODO WEBACTIONS KITSUNE TOOLS
  const val WEB_ACTION_KITSUNE_BASE = "https://webactions.kitsune.tools/api/v1/"
  const val WEB_ACTION_KITSUNE_UPLOAD_FILE = "about_us/upload-file"

}


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


  //TODO NOWFLOAT API
  const val NOW_FLOATS_BASE = "https://api.nowfloats.com/"
  const val WEBSITE_THEME_GET = "/Discover/v1/floatingPoint/getThemeCustomization"
  const val WEBSITE_THEME_UPDATE = "/Discover/v1/floatingPoint/updateThemeCustomization"

  //TODO FP UPADTE API
  const val FLOATING_POINT_UPDATE = "Discover/v1/FloatingPoint/update/"

}


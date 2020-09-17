package com.inventoryorder.rest

object EndPoints {

  // NFX APIs
  const val WITH_FLOATS_BASE_URL = "https://assuredpurchase.withfloats.com/"



  const val ASSURED_PURCHASE_BASE_URL = "https://assuredpurchase.withfloats.com/"



  const val POST_INITIATE_ORDER = "api/assuredPurchase/v2/InitiateOrder"

  //  const val GET_SELLER_SUMMARY_URL = "api/AssuredPurchase/SellerSummary"
  const val GET_SELLER_SUMMARY_URL = "api/assuredPurchase/v2/SellerSummary"

  //  const val GET_LIST_ORDER_URL = "api/AssuredPurchase/ListOrders"
  const val GET_LIST_ORDER_URL = "api/assuredPurchase/v2/ListOrders"
  const val GET_LIST_ORDER_FILTER_URL = "api/assuredPurchase/v2/ListAllOrders"
//  const val GET_LIST_ORDER_FILTER_URL = "api/assuredPurchase/v2.1/ListAllOrders"

  // API 2 with float
  const val BOOST_API2_WITH_FLOAT = "https://api2.withfloats.com"
  const val GET_LIST_INVENTORY_SYNC = "product/v1/GetListingsWithInventorySync"
  const val SEND_SMS = "discover/v1/floatingpoint/CreateSMSHighPriorityIndia"


  //Boost now float
  const val BOOST_FLOATS_BASE_URL = "https://boost.nowfloats.com/"
  const val GET_DOCTORS_API = "Home/GetDoctorData"
  const val GET_PRODUCT_DETAILS = "Home/GetProductDetails"
  const val SEND_MAIL= "Home/SendEmail"

  const val GET_LIST_ASSURE_PURCHASE_ORDER = "api/assuredPurchase/v2/ListInProgressOrders"
  const val GET_LIST_CANCELLED_ORDER = "api/assuredPurchase/v2/ListCancelledOrders"
  const val GET_ORDER_DETAIL = "api/assuredPurchase/v2/GetOrderDetails"
  const val GET_LIST_IN_COMPLETE_ORDER = "api/assuredPurchase/v2/ListIncompleteOrders"

  const val GET_CONFIRM_ORDER = "api/assuredPurchase/v2/ConfirmOrder"
  const val GET_CANCEL_ORDER = "api/assuredPurchase/v2/CancelOrder"


  // Boost Kit APIs
  const val WEB_ACTION_BOOST_KIT_BASE_URL = "https://webaction.api.boostkit.dev/"
  const val WEEKLY_SCHEDULE_DOCTOR = "api/v1/weeklyschedule/get-data"
  const val ALL_APT_DOCTOR = "api/v1/appointment/get-data"
  const val ADD_APT_CONSULT_DATA = "api/v1/appointment/add-data"

}


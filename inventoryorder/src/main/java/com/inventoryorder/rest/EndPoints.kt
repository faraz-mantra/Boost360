package com.inventoryorder.rest

object EndPoints {

  const val WITH_FLOATS_BASE_URL = "https://assuredpurchase.withfloats.com/"

  // API 2 with float
  const val BOOST_API2_WITH_FLOAT = "https://api2.withfloats.com"
  const val GET_LIST_INVENTORY_SYNC = "product/v1/GetListingsWithInventorySync"
  const val SEND_SMS = "discover/v1/floatingpoint/CreateSMSHighPriorityIndia"
  const val GET_BIZ_FLOATS_MESSAGE = "/Discover/v1/floatingPoint/bizFloats"
  const val GET_PRODUCT_LIST = "Product/v1/GetListings"

  // API with float
  const val BOOST_API_WITH_FLOAT = "https://api.withfloats.com"
  const val GET_USER_SUMMARY_FILTER = "/Dashboard/v1/{fpTag}/summary"
  const val GET_USER_MESSAGE_COUNT_FILTER = "/discover/v2/floatingPoint/usermessagescount/{fpId}"
  const val GET_MAP_ADDRESS_DETAILS_FILTER = "/Dashboard/v1/{fpTag}/totaladdressviewdetails"
  const val GET_USER_CALL_SUMMARY_FILTER = "/WildFire/v1/calls/summary"
  const val GET_SUBSCRIBER_COUNT_FILTER = "/discover/v1/floatingPoint/{fpTag}/subscriberCount"

  //Boost now float
  const val BOOST_FLOATS_BASE_URL = "https://boost.nowfloats.com/"
  const val GET_DOCTORS_API = "Home/GetDoctorData"
  const val GET_PRODUCT_DETAILS = "Home/GetProductDetails"
  const val SEND_MAIL = "Home/SendEmail"


  // NFX APIs
  const val ASSURED_PURCHASE_BASE_URL = "https://assuredpurchase.withfloats.com/"

  const val POST_INITIATE_ORDER = "api/assuredPurchase/v2/InitiateOrder"
  const val POST_UPDATE_ORDER = "api/assuredPurchase/v2/UpdateOrder"

  const val GET_SELLER_SUMMARY_URL = "api/assuredPurchase/v2/SellerSummary"
  const val GET_SELLER_SUMMARY_V2_5_URL = "api/assuredPurchase/v2.5/SellerSummary"
  const val GET_LIST_ORDER_URL = "api/assuredPurchase/v2/ListOrders"
  const val GET_LIST_ORDER_FILTER_URL = "api/assuredPurchase/v2/ListAllOrders"
  const val GET_LIST_ASSURE_PURCHASE_ORDER = "api/assuredPurchase/v2/ListInProgressOrders"
  const val GET_LIST_CANCELLED_ORDER = "api/assuredPurchase/v2/ListCancelledOrders"
  const val GET_ORDER_DETAIL = "api/assuredPurchase/v2/GetOrderDetails"
  const val GET_LIST_IN_COMPLETE_ORDER = "api/assuredPurchase/v2/ListIncompleteOrders"

  const val POST_UPDATE_EXTRA_FIELD_ORDER = "api/assuredPurchase/v2/UpdateExtraPropertiesInformation"
  const val GET_CONFIRM_ORDER = "api/assuredPurchase/v2/ConfirmOrder"
  const val GET_CANCEL_ORDER = "api/assuredPurchase/v2/CancelOrder"
  const val MARK_AS_DELIVERED = "api/assuredPurchase/v2/MarkOrderAsDelivered"
  const val MARK_AS_SHIPPED = "api/assuredPurchase/v2/MarkOrderAsShipped"
  const val SEND_PAYMENT_REMINDER = "api/assuredPurchase/v2/SendPaymentReminder"
  const val MARK_COD_PAYMENT_DONE = "api/assuredPurchase/v2/MarkPaymentReceivedForCODOrder"
  //new API order



  // Boost Kit APIs
  const val WEB_ACTION_BOOST_KIT_BASE_URL = "https://webaction.api.boostkit.dev/"
  const val WEEKLY_SCHEDULE_DOCTOR = "api/v1/weeklyschedule/get-data"
  const val ALL_APT_DOCTOR = "api/v1/appointment/get-data"
  const val ADD_APT_CONSULT_DATA = "api/v1/appointment/add-data"
  const val UPDATE_APT_CONSULT_DATA = "api/v1/appointment/update-data"

}


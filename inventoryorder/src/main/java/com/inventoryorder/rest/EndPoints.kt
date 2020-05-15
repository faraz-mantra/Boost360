package com.inventoryorder.rest

object EndPoints {

  // NFX APIs
  const val WITH_FLOATS_BASE_URL = "https://assuredpurchase.withfloats.com/"

  //  const val GET_SELLER_SUMMARY_URL = "api/AssuredPurchase/SellerSummary"
  const val GET_SELLER_SUMMARY_URL = "api/assuredPurchase/v2/SellerSummary"
  //  const val GET_LIST_ORDER_URL = "api/AssuredPurchase/ListOrders"
  const val GET_LIST_ORDER_URL = "api/assuredPurchase/v2/ListOrders"

  const val GET_LIST_ASSURE_PURCHASE_ORDER = "api/assuredPurchase/v2/ListInProgressOrders"
  const val GET_LIST_CANCELLED_ORDER = "api/assuredPurchase/v2/ListCancelledOrders"
  const val GET_LIST_IN_COMPLETE_ORDER = "api/assuredPurchase/v2/ListIncompleteOrders"

  const val GET_CONFIRM_ORDER = "api/assuredPurchase/v2/ConfirmOrder"
  const val GET_CANCEL_ORDER = "api/assuredPurchase/v2/CancelOrder"
}


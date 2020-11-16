package com.appservice.rest

object EndPoints {

  //TODO NFX API WITH FLOAT
  const val WITH_FLOATS_BASE = "https://api.withfloats.com/"
  const val USER_ACCOUNT_DETAIL = "discover/v9/business/paymentProfile/{fpId}"
  const val CREATE_PAYMENT = "discover/v9/business/paymentProfile/create"
  const val UPDATE_PAYMENT = "discover/v9/business/paymentProfile/bankDetails/update/{fpId}"


  //TODO NFX API 2 WITH FLOAT
  const val WITH_FLOATS_TWO_BASE = "https://api2.withfloats.com/"
  const val CREATE_SERVICE = "Product/v1/Create"
  const val UPDATE_SERVICE = "Product/v1/Update"
  const val DELETE_SERVICE = "Product/v1/Delete"
  const val ADD_IMAGE = "Product/v1/AddImage"
  const val GET_TAGS = "Product/v1/tags"
  const val GET_NOTIFICATION = "Discover/v1/floatingpoint/notificationscount"

  //TODO NFX API 2 WITH FLOAT
  const val RAZOR_API_BASE = "https://ifsc.razorpay.com/"
  const val RAZOR_IFSC_DETAIL = "{ifsc}"

  //TODO NFX API 2 WITH FLOAT
  const val WEB_ACTION_BOOST_KIT_BASE = "https://webaction.api.boostkit.dev/"
  const val GET_ALL_KYC_DATA = "api/v1/List"
  const val GET_DATA_KYC = "api/v1/kycdoc/get-data"
  const val ADD_DATA_KYC = "api/v1/kycdoc/add-data"
  const val UPDATE_DATA_KYC = "api/v1/kycdoc/update-data"
  const val UPLOAD_FILE = "api/v1/ourdoctors/upload-file"

  //TODO Assured with float NFX APIs
  const val ASSURED_WITH_FLOATS_BASE_URL = "https://assuredpurchase.withfloats.com/"
  const val PICK_UP_ADDRESS = "api/Seller/PickupAddressList"
  const val GET_PRODUCT_INFORMATION_FETCH = "api/Seller/InformationFetch"

  //TODO KIT WEB ACTION with float NFX APIs
  const val KIT_WEB_ACTION_WITH_FLOATS_BASE_URL = "https://kit-webaction-api.withfloats.com/"
  const val ADD_PRODUCT_DETAIL = "api/v1/product_details/add-data"
  const val UPDATE_PRODUCT_DETAIL = "api/v1/product_details/update-data"
  const val GET_PRODUCT_DETAIL = "api/v1/product_details/get-data"
  const val UPLOAD_FILE_PRODUCT = "api/v1/product_images/upload-file"
  const val ADD_PRODUCT_IMAGE = "api/v1/product_images/add-data"
  const val GET_PRODUCT_IMAGE = "api/v1/product_images/get-data"
  const val DELETE_PRODUCT_IMAGE = "api/v1/product_images/delete-data"
  const val FP_ONBOARDING_UPDATE_DATA = "api/v1/fp_onboarding/update-data"

}


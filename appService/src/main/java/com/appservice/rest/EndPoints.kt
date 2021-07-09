package com.appservice.rest

object EndPoints {

  //TODO NFX API WITH FLOAT
  const val WITH_FLOATS_BASE = "https://api.withfloats.com/"
  const val USER_ACCOUNT_DETAIL = "discover/v9/business/paymentProfile/{fpId}"
  const val CREATE_PAYMENT = "discover/v9/business/paymentProfile/create"
  const val UPDATE_PAYMENT = "discover/v9/business/paymentProfile/bankDetails/update/{fpId}"

  //TODO STAFF API WITH FLOAT
  const val STAFF_BASE_URL = "https://api.nowfloats.com/"
  const val CREATE_STAFF_PROFILE = "Staff/v1/Create"
  const val FETCH_STAFF_SERVICES = "product/v13/GetServiceListings"
  const val GET_STAFF_LISTING = "staff/v1/GetStaffListing"
  const val STAFF_PROFILE_UPDATE ="staff/v1/Update"
  const val STAFF_PROFILE_DELETE ="staff/v1/Delete"
  const val STAFF_ADD_TIMING ="staff/v1/AddStaffTiming"
  const val STAFF_UPDATE_TIMING ="staff/v1/UpdateStaffTiming"
  const val STAFF_UPDATE_IMAGE ="staff/v1/UpdateStaffImage"
  const val STAFF_DELETE_IMAGE ="staff/v1/DeleteStaffImage"
  const val GET_STAFF_DETAILS ="Staff/v1/GetStaffDetails"
  //TODO WEEKLY APPOINTMENT APIS
  const val POST_UPDATE_SERVICE_TIMING = "service/v1/UpdateServiceTiming"
  const val POST_ADD_SERVICE_TIMING = "service/v1/AddServiceTiming"
  const val GET_SERVICE_TIMING = "service/v1/GetServiceTiming"

  //TODO API.NOWFLOATS.COM FLOAT
  const val API_NOWFLOATS_COM_BASE = "https://api.nowfloats.com/"
  const val CREATE_SERVICE_V1 = "service/v1/create"
  const val UPDATE_SERVICE_V1 = "service/v1/update"
  const val DELETE_SERVICE_V1 = "service/v1/delete"
  const val ADD_IMAGE_V1 = "service/v1/AddImage"
  const val GET_TAGS_V1 = "service/v1/tags"
  const val GET_SERVICE_DETAILS = "Service/v1/GetServiceDetails"
  const val  DELETE_SECONDARY_IMAGE = "service/v1/DeleteSecondaryImage"

  //TODO NFX API 2 WITH FLOAT
  const val WITH_FLOATS_TWO_BASE = "https://api2.withfloats.com/"
  const val CREATE_SERVICE = "Product/v1/Create"
  const val UPDATE_SERVICE = "Product/v1/Update"
  const val DELETE_SERVICE = "Product/v1/Delete"
  const val ADD_IMAGE = "Product/v1/AddImage"
  const val GET_TAGS = "Product/v1/tags"
  const val GET_NOTIFICATION = "Discover/v1/floatingpoint/notificationscount"
  const val GET_LATEST_UPDATES = "Discover/v1/floatingPoint/bizFloats"
  const val PUT_BIZ_MESSAGE = "discover/v1/FloatingPoint/createBizMessage"
  const val PUT_BIZ_IMAGE = "discover/v1/FloatingPoint/createBizImage"
  const val GET_BIZ_WEB_UPDATE_BY_ID = "discover/v1/bizFloatForWeb/{id}"
  const val DELETE_BIZ_MESSAGE_UPDATE= "discover/v1/floatingpoint/archiveMessage"

  //TODO PRODUCT API 2 WITH FLOAT
  const val CREATE_PRODUCT = "Product/v1/Create"
  const val GET_LISTING_INVENTORY_SYNC = "Product/v1/GetListingsWithInventorySync"
  const val UPDATE_PRODUCT = "Product/v1/Update"
  const val DELETE_PRODUCT = "Product/v1/Delete"

  //TODO SERVICE API WITH NOWFLOATS API
  const val GET_SERVICE_LISTING = "product/v13/GetAllServiceListings"
  const val GET_SEARCH_LISTING = "/Service/v1/GetSearchListings"


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


  //TODO BOOST KIT DEV API
  const val BOOST_KIT_DEV_BASE = "https://developer.api.boostkit.dev/"
  const val WEB_ACTION_TESTIMONIAL = "kitsune/v1/ListWebActionDetails/{themeID}"
  const val GET_TESTIMONIAL = "api/v1/{testimonials}/get-data"
  const val ADD_TESTIMONIAL = "api/v1/{testimonials}/add-data"
  const val UPDATE_TESTIMONIAL = "api/v1/{testimonials}/update-data"
  const val DELETE_TESTIMONIAL = "api/v1/{testimonials}/update-data"
}


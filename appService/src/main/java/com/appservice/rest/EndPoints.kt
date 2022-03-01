package com.appservice.rest

object EndPoints {
  //TODO BOOST NOW FLOATS API
  const val BOOST_NOW_FLOATS_BASE = "https://boost.nowfloats.com/"
  const val UPDATE_INFO_POST = "Home/updateInfo"
  const val FP_DETAILS_BY_ID = "Home/GetFloatingPointDetailsById"

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
  const val STAFF_PROFILE_UPDATE = "staff/v1/Update"
  const val STAFF_PROFILE_DELETE = "staff/v1/Delete"
  const val STAFF_ADD_TIMING = "staff/v1/AddStaffTiming"
  const val STAFF_UPDATE_TIMING = "staff/v1/UpdateStaffTiming"
  const val STAFF_UPDATE_IMAGE = "staff/v1/UpdateStaffImage"
  const val STAFF_DELETE_IMAGE = "staff/v1/DeleteStaffImage"
  const val GET_STAFF_DETAILS = "Staff/v1/GetStaffDetails"

  //TODO WEEKLY APPOINTMENT APIS
  const val POST_UPDATE_SERVICE_TIMING = "service/v1/UpdateServiceTiming"
  const val POST_ADD_SERVICE_TIMING = "service/v1/AddServiceTiming"
  const val GET_SERVICE_TIMING = "service/v1/GetServiceTiming"

  //TODO OFFERS NOW FLOATS APIS
  const val POST_GET_OFFER_LISTING = "offers/v1/GetListing"
  const val POST_CREATE_OFFER = "offers/v1/Create"
  const val POST_UPDATE_OFFER = "offers/v1/Update"
  const val POST_OFFER_DETAILS = "offers/v1/GetDetails"
  const val POST_OFFER_DELETE = "offers/v1/Delete"
  const val POST_OFFER_ADD_IMAGE = "offers/v1/AddImage"
  const val POST_OFFER_DELETE_IMAGE = "offers/v1/DeleteImage"
  const val POST_GET_TOP_OFFERS_FOR_FP = "offers/v1/GetTopOffersForFloatingPoint"
  const val POST_GET_TOP_OFFERS_BY_CATEGORIES = "offers/v1/GetTopOffersByCategories"
  const val POST_GET_TOP_OFFERS = "offers/v1/GetTopOffers"

  //TODO API.NOWFLOATS.COM FLOAT
  const val API_NOWFLOATS_COM_BASE = "https://api.nowfloats.com/"
  const val CREATE_SERVICE_V1 = "service/v1/create"
  const val UPDATE_SERVICE_V1 = "service/v1/update"
  const val DELETE_SERVICE_V1 = "service/v1/delete"
  const val ADD_IMAGE_V1 = "service/v1/AddImage"
  const val GET_TAGS_V1 = "service/v1/tags"
  const val GET_SERVICE_DETAILS = "Service/v1/GetServiceDetails"
  const val DELETE_SECONDARY_IMAGE = "service/v1/DeleteSecondaryImage"

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
  const val DELETE_BIZ_MESSAGE_UPDATE = "discover/v1/floatingpoint/archiveMessage"
  const val GET_PRODUCT_LIST = "Product/v1/GetListings"
  const val GET_FP_DETAILS = "/Discover/v3/floatingPoint/nf-app/{fpid}"
  const val GET_CATALOG_STATUS = "discover/v9/business/settings/status/{fpid}"
  const val UPDATE_GST_SLAB = "discover/v9/business/paymentProfile/gstSlab/update"
  const val POST_PRODUCT_CATEGORY_VERB = "Discover/v1/FloatingPoint/update/"
  const val GET_MERCHANT_SUMMARY = "/Support/v1/dashboard/GetMerchantSummary"

  //TODO PRODUCT API 2 WITH FLOAT
  const val CREATE_PRODUCT = "Product/v1/Create"
  const val GET_LISTING_INVENTORY_SYNC = "Product/v1/GetListingsWithInventorySync"
  const val UPDATE_PRODUCT = "Product/v1/Update"
  const val DELETE_PRODUCT = "Product/v1/Delete"
  const val GET_PRODUCT_LISTING = "Product/v1/GetListings"
  const val GET_PRODUCT_LISTING_COUNT = "Product/v1/GetListingsWithCount"


  //TODO Appointment Settings API 2 WITH FLOAT
  const val ACCEPT_COD = "discover/v9/business/paymentProfile/acceptCod/update"
  const val ADD_BANK_ACCOUNT = "discover/v9/business/paymentProfile/bankDetails/update"
  const val DELIVERY_SETUP = "discover/v9/business/deliveryDetails/update"
  const val GET_DELIVERY_CONFIG = "discover/v9/business/deliveryDetails"
  const val ADD_WARE_HOUSE = "discover/v9/business/warehouse/add"
  const val GET_WARE_HOUSE = "discover/v9/business/warehouse/"
  const val INVOICE_SETUP = "discover/v9/business/paymentProfile/taxDetails/update"
  const val UPLOAD_MERCHANT_SIGNATURE = "discover/v9/business/paymentProfile/uploadSignature"
  const val ADD_MERCHANT_UPI = "discover/v9/business/paymentProfile/upiid/update"
  const val GET_PAYMENT_PROFILE_DETAILS = "discover/v9/business/paymentProfile"

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

  //TODO BOOST PLUGIN with floats APIs
  const val BOOST_KIT_PLUGIN_WITH_FLOATS_NEW_BASE = "https://plugin.withfloats.com/"
  const val DOMAIN_DETAILS = "DomainService/v3/GetDomainDetailsForFloatingPoint/{fpTag}"
  const val SEARCH_DOMAIN = "DomainService/v1/checkAvailability/{domain}"
  const val CREATE_DOMAIN = "DomainService/v2/domainWithWebsite/create"

  //TODO RIA WITH FLOATS APIs
  const val RIA_WITH_FLOATS_BASE = "https://ria.withfloats.com"
  const val ADD_EXISTING_DOMAIN_DETAILS = "api/Service/EmailRIASupportTeamV2"

  // TODO AZURE WEBSITE NET APIs
  const val AZURE_WEBSITE_NET_URL = "https://withfloats-feature-processor-api.azurewebsites.net/"
  const val GET_FEATURE_DETAILS = "Features/v1/GetFeatureDetils"
  const val GET_FEATURE_DETAILS_2 = "Features/v1/GetFeatureDetails"

  // RIA MEMORY APIs
  const val RIA_MEMORY_BASE_URL="https://riamemory.withfloats.com"
}


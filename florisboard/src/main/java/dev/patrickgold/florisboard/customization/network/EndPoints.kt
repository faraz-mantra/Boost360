package dev.patrickgold.florisboard.customization.network

object EndPoints {
    const val BUSINESS_FEATURE_BASE_URL = "https://api2.withfloats.com/"
    const val BUSINESS_UPDATES_LISTING = "/Discover/v3/floatingPoint/bizFloats"
    const val PRODUCT_LISTING = "/Product/v1/GetListingsWithCount"
    const val USER_ALL_DETAILS = "/discover/v2/floatingPoint/nf-web/{fpTag}"
    const val CREATE_PRODUCT_OFFER = "/api/Offers/CreateOffer"

    //NFX float APIs
    const val NFX_FLOAT_BASE_URL = "https://nfx.withfloats.com/"
    const val NFX_CHANNELS_STATUS = "dataexchange/v2/channelstatus"

    // Web Action APIs
    const val WEB_ACTION_BASE_URL = "https://webaction.api.boostkit.dev/"
    const val GET_WHATSAPP_BUSINESS = "api/v1/whatsapp_number/get-data"

    // Base With Floats APIs
    const val BOOST_FLOATS_BASE_URL = "https://boost.nowfloats.com"
    const val MERCHANT_PROFILE = "/Home/MerchantProfileStatus"

    //API NOW FLOATS
    const val API_NOW_FLOATS_BASE = "https://api.nowfloats.com/"
    const val GET_STAFF_LISTING = "staff/v1/GetStaffListing"
    const val GET_SEARCH_LISTING = "/Service/v1/GetSearchListings"

    //Merchant Summary
    const val GET_MERCHANT_SUMMARY = "/Support/v1/dashboard/GetMerchantSummary"


}


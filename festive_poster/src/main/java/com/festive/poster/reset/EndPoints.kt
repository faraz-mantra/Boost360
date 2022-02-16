package com.festive.poster.reset

object EndPoints {


    const val NOW_FLOATS_BASE = "https://api.nowfloats.com/"
    const val WITH_FLOATS_TWO_BASE = "https://api2.withfloats.com/"


    const val TEMPLATE_VIEW_CONFIG="Templates/v1/GetTemplatesViewConfig"
    const val GET_TEMPLATES="Templates/v1/GetTemplates"

    const val FEATURE_PROCESSOR_BASE = "https://featureprocessor.withfloats.com/"
    const val GET_FEATURE_DETAILS="Features/v1/GetFeatureDetils"

    const val BOOST_KIT_DEV_BASE = "https://developer.api.boostkit.dev/"
    const val GET_UPGRADE_DATA="language/v1/upgrade/get-data"

    const val UPLOAD_IMAGE="Templates/v1/UploadImage"
    const val SAVE_KEY_VALUE="Templates/v1/SaveSVGTemplateKeys"

    const val UPDATE_PURCHASE_STATUS="Templates/v1/UpdatePurchaseStatus"

    const val UPLOAD_USER_PROFILE_IMAGE="/user/v9/floatingpoint/createUserProfileImage"

    const val PUT_BIZ_IMAGE = "discover/v1/FloatingPoint/createBizImage"

    const val GET_BIZ_WEB_UPDATE_BY_ID = "discover/v1/bizFloatForWeb/{id}"
    const val PUT_BIZ_MESSAGE = "discover/v1/FloatingPoint/createBizMessage"
    const val GET_MERCHANT_SUMMARY = "/Support/v1/dashboard/GetMerchantSummary"

    const val USER_ALL_DETAILS = "/discover/v2/floatingPoint/nf-web/{fpTag}"

    const val GET_PAST_UPDATES_LIST = "discover/v1/floatingPoint/bizFloats"


}
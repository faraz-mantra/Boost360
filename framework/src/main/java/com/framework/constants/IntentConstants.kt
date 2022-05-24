package com.framework.constants

object IntentConstants {

    val MARKET_PLACE_ORIGIN_NAV_DATA="MP_NAV_DATA"
    val MARKET_PLACE_ORIGIN_ACTIVITY="MP_ORIGIN_ACTIVITY"
    val IK_CAPTION_KEY="IK_CAPTION_KEY"
    val IK_POSTER="IK_POSTER"
    val IK_TAGS="IK_TAGS"
    val IK_UPDATE_TYPE="IK_UPDATE_TYPE"

    enum class UpdateType(val value:Int){
        ALL(0),
        UPDATE_PROMO_POST(1),
        UPDATE_IMAGE_TEXT(2),
        UPDATE_TEXT(3)
    }
}
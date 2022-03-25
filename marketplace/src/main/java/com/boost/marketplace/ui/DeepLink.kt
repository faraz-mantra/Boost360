package com.boost.marketplace.ui

enum class DeepLink(var screenType:String){
    PRODUCTCATALOGUE("digital_channels"),
    BOOSTKEYBOARD("Keyboard"),
    CUSTOMERSUPPORT("Testimonials"),
    FEATURE_CODE_4("contact"),
    FEATURE_CODE_5("analytics"),
    FEATURE_CODE_6("profile"),
    FEATURE_CODE_7("profile"),
    FEATURE_CODE_8("call_tracker_add_on"),
    FEATURE_CODE_9("all_custom_pages"),
    FEATURE_CODE_10("festive_poster"),
    FEATURE_CODE_11("newsubscriber");

    companion object{
        fun getScreenType(featureKey:String?):String?= values().firstOrNull { featureKey == it.name }?.screenType
    }
}
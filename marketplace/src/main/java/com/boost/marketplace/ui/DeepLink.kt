package com.boost.marketplace.ui

enum class DeepLink(var screenType:String){

    BOOSTKEYBOARD("Keyboard"),
    TESTIMONIALS("Testimonials"),
    facebookpage("facebookpage"),
    addProduct("addProduct"),
    keyboardSettings("keyboardSettings"),
    CUSTOMPAGES("addCustomPage"),
    enquiries("enquiries_tab"),
    subscribers("subscribers"),
    new_subscribers("newsubscriber"),
    IMAGEGALLERY("featuredimage"),
    SOCIALSHARE("social"),
    LIMITED_CONTENT("manage_content"),
    RIASUPPORTTEAM("ria_digital_assistant"),
    CONTACTDETAILS("contact_details"),
    STAFFPROFILE("ic_staff_profile_d"),
    CUSTOMERSUPPORT("premium_boost_support"),
    ANALYTICS("search_analytics"),
    visits("visits");

    companion object{
        fun getScreenType(featureKey:String?):String?= values().firstOrNull { featureKey == it.name }?.screenType
    }
}
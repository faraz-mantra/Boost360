package com.boost.marketplace.ui

enum class DeepLink(var screenType:String){

    BOOSTKEYBOARD("Keyboard"),
    TESTIMONIALS("Testimonials"),
    CUSTOMPAGES("all_custom_pages"),
    TOB("customerenquires"),
    IMAGEGALLERY("imagegallery"),
    SOCIALSHARE("social"),
    PAYMENTGATEWAY("Payment_Gateway"),
    ANALYTICS("total_website_visitors"),
    PRODUCTCATALOGUESVC("service_catalogue"),
    RIASUPPORTTEAM("Ria_Digital"),
    CONTACTDETAILS("contact_details"),

    LIMITED_CONTENT("manage_content"),
    STAFFPROFILE("Staff_profile"),
    STAFFPROFILE15("Staff_profile"),
    CUSTOMERSUPPORT("expert_connect"),

    ;

    companion object{
        fun getScreenType(featureKey:String?):String?= values().firstOrNull { featureKey == it.name }?.screenType
    }
}
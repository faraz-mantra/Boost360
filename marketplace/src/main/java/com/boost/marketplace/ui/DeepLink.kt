package com.boost.marketplace.ui

enum class DeepLink(var screenType:String){

    BOOSTKEYBOARD("Keyboard"),
    TESTIMONIALS("Testimonials"),
    CUSTOMPAGES("all_custom_pages"),
    TOB("customerenquires"),
    IMAGEGALLERY("imagegallery"),
    SOCIALSHARE("digital_channels"),
    PAYMENTGATEWAY("Payment_Gateway"),
    ANALYTICS("total_website_visitors"),
    PRODUCTCATALOGUESVC("service_catalogue"),
    RIASUPPORTTEAM("feedbackchat"),
    CONTACTDETAILS("contact_details"),
    STAFFPROFILE("Staff_Profile"),
    STAFFPROFILE15("Staff_Profile"),
    TIMINGS("hours"),
    PRODUCTCATALOGUE("service_catalogue"),
    SUBSCRIBERCOUNT("newsubscriber"),
    DOMAINPURCHASE("domain_booking"),
    CALLTRACKER("callTracker"),
    BROCHURE("Digital_Brochure"),
    OURTOPPERS("Our_Toppers"),
    FEATUREDIMAGE("Featured_Image"),
    FACULTY("Faculty"),
    UPCOMING_BATCHES("Upcoming_Batches"),
    LATESTUPDATES("Latest_Updates"),
    BOOKTABLE("book_table"),
    VIDEO_CONSULTING("create_consultation"),
    DOCTORBIO("Doctor_Profile"),
    LIMITED_CONTENT("manage_content"),
    //premium support
    CUSTOMERSUPPORT("expert_connect"),
    PROJECTTEAM("ProjectTeam"),
    FBLIKEBOX("facebookpage"),
    //Auto-seo
    SITESENSE("nfstoreseo"),
    EMAILACCOUNTS("contact"),
    TRIPADVISORREVIEWS("Trip_Advisor"),
    PLACESTOLOOKAROUND("Nearby_Places"),
    IVR("call_tracker_add_on"),
    APPOINTMENTENGINE("APPOINTMENT_FRAGMENT"),
    PRODUCTCATALOGUEMENU("service_catalogue"),
    BOOKINGENGINE("APPOINTMENT_FRAGMENT"),
    DEFAULTSSL("Business_Kyc"),
    PRODUCTCATALOGUECOURSES("service_catalogue"),
   // PRODUCTCATALOGUE("service_catalogue"),

    //AppointmentSettings
    //product onboarding and training
    //unlimitedwebsitebandwidth  WEBSITEBANDWIDTH

    ;

    companion object{
        fun getScreenType(featureKey:String):String?= values().firstOrNull { featureKey.replace( " ","").replace("-", "") == it.name }?.screenType
    }
}
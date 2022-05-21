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
    PRODUCTCATALOGUE("deeplink_service_catalogue"),
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


    //comingsoon
    //AppointmentSettings
    //MembershipPlans
    //product onboarding and training
    //Business info email?
    //Facultymanagement
    //popularcourses
    //Digitalmenu
    //DigitalAppointmentBookingand management
    //nowfloatsdatasecurity
    //unlimitedwebsitebandwidth  WEBSITEBANDWIDTH
    //Room booking  BOOKINGENGINE
    // Room listing

    ;

    companion object{
        fun getScreenType(featureKey:String):String?= values().firstOrNull { featureKey.replace( " ","").replace("-", "") == it.name }?.screenType
    }
}
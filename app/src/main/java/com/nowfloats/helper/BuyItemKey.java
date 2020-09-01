package com.nowfloats.helper;

public enum BuyItemKey {

    PROJECTTEAM("PROJECTTEAM"), // Got the message: This add on is not available for your product.
    WILDFIRE_FB_LEAD_ADS("WILDFIRE_FB_LEAD_ADS"),
    DOMAINPURCHASE("DOMAINPURCHASE"),
    CALLTRACKER("CALLTRACKER"), // Working
    MERCHANT_TRAINING("MERCHANT_TRAINING"),
    CUSTOMERSUPPORT("CUSTOMERSUPPORT"),
    BOOKTABLE("BOOKTABLE"), // Got the message: This add on is not available for your product.
    PLACES_TO_LOOK_AROUND("PLACES-TO-LOOK-AROUND"), // Got the message: This add on is not available for your product.
    BROCHURE("BROCHURE"), // Got the message: This add on is not available for your product.
    TRIPADVISOR_REVIEWS("TRIPADVISOR-REVIEWS"), // Not working. Went to home
    OUR_TOPPERS("OUR-TOPPERS"), // Got the message: This add on is not available for your product.
    FACULTY("ACULTY"), // Got the message: This add on is not available for your product.
    FEATUREDIMAGE("FEATUREDIMAGE"),
    TOB("TOB"), // Took to CUSTOMER ENQUIRIES ADD ON
    CONTACTDETAILS("CONTACTDETAILS"),
    SITESENSE("SITESENSE"),
    SOCIALSHARE("SOCIALSHARE"),
    FBLIKEBOX("FBLIKEBOX"),
    CUSTOMPAGES("CUSTOMPAGES"),
    VISITORCOUNT("VISITORCOUNT"), // Got the message: This add on is not available for your product.
    SUBSCRIBERCOUNT("SUBSCRIBERCOUNT"),
    PRODUCTCATALOGUE("PRODUCTCATALOGUE"), // Got the message: This add on is not available for your product.
    IMAGEGALLERY("IMAGEGALLERY"),
    TIMINGS("TIMINGS"),
    DEFAULTSSL("DEFAULTSSL"),
    MEDIAMANAGEMENT("MEDIAMANAGEMENT"), // Got the message: This add on is not available for your product.
    AUTOFBMSGUPDATE("AUTOFBMSGUPDATE"), // Got the message: This add on is not available for your product.
    BOOKING_ENGINE("BOOKING ENGINE"), // SPACE IN URL
    PRICE_COMPARISON("PRICE COMPARISON"), // SPACE IN URL
    FACILITIES("FACILITIES"), // Got the message: This add on is not available for your product.
    GALLERYVIDEO("GALLERYVIDEO"), // Got the message: This add on is not available for your product.
    RIASUPPORTTEAM("RIASUPPORTTEAM"), // Took to the RIA digital assistant add on
    PACKAGE_PROPERTIES("PACKAGE-PROPERTIES"), // Got the message: This add on is not available for your product.
    BOOSTKEYBOARD("BOOSTKEYBOARD"),
    DOCTORBIO("DOCTORBIO"),
    APPOINTMENTENGINE("APPOINTMENTENGINE"),
    PAYMENTGATEWAY("PAYMENTGATEWAY"),
    CUSTOM_PAYMENTGATEWAY("CUSTOM_PAYMENTGATEWAY"),
    CHATBOT("CHATBOT");

    private String itemValue;

    BuyItemKey(String itemValue) {
        this.itemValue = itemValue;
    }

    public static BuyItemKey by(String itemValue) {
        for (BuyItemKey m : values()) {
            if (m.itemValue.equals(itemValue)) return m;
        }
        return null;
    }

    public String getItemValue() {
        return itemValue;
    }
}

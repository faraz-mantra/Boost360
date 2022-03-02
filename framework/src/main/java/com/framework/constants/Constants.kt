package com.framework.constants

import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable



    private val shimmer = Shimmer.AlphaHighlightBuilder()// The attributes for a ShimmerDrawable is set by this builder
        .setDuration(1800) // how long the shimmering animation takes to do one full sweep
        .setBaseAlpha(0.7f) //the alpha of the underlying children
        .setHighlightAlpha(0.6f) // the shimmer alpha amount
        .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
        .setAutoStart(true)
        .build()

    // This is the placeholder for the imageView
    val shimmerDrawable = ShimmerDrawable().apply {
        setShimmer(shimmer)
    }

    const val AUTH_KEY="597ee93f5d64370820a6127c"

    enum class SupportVideoType (val value :String) {
        CUSTOM_PAGES("feature.CUSTOMPAGES"),
        GALLERY_IMAGE("feature.GALLERYIMAGE"),
        LATEST_UPDATES("feature.LATESTUPDATES"), // Added to Website > Updates > How it works
        PAYMENT_GATEWAY("feature.PAYMENTGATEWAY"),
        ANALYTICS("feature.ANALYTICS"), //hold, doubts
        TOB("feature.TOB"),             //Applied in Enquiries Calls, messages, newsletter subscribers
        TESTIMONIALS("feature.TESTIMONIALS"),
        PRODUCT_CATALOGUE("feature.PRODUCTCATALOGUE"),
        FEATURED_IMAGE("feature.FEATUREDIMAGE"),
        CONTACT_DETAILS("feature.CONTACTDETAILS")  //hold, doubts
    }

    enum class PremiumCode(val value: String) {
        OUR_TOPPERS("OUR-TOPPERS"), FACULTY("FACULTY"), WILDFIRE_FB_LEAD_ADS(" WILDFIRE_FB_LEAD_ADS"),
        userPurchsedWidgets("userPurchsedWidgets"), DOMAINPURCHASE("DOMAINPURCHASE"),
        CALLTRACKER("CALLTRACKER"), MERCHANT_TRAINING("MERCHANT_TRAINING"), CUSTOM_PAYMENTGATEWAY("CUSTOM_PAYMENTGATEWAY"),
        CUSTOMERSUPPORT("CUSTOMERSUPPORT"), StoreWidgets("StoreWidgets"), BOOSTKEYBOARD("BOOSTKEYBOARD"),
        BOOKTABLE("BOOKTABLE"), BROCHURE("BROCHURE"), TRIPADVISOR_REVIEWS("TRIPADVISOR-REVIEWS"),
        PLACES_TO_LOOK_AROUND("PLACES-TO-LOOK-AROUND"), PROJECTTEAM("PROJECTTEAM"), get_fp_details_mode("get_fp_details_mode");

        companion object {
            fun fromValue(value: String?): PremiumCode? = values().firstOrNull { it.value == value }
        }
    }

package com.dashboard.utils


fun getDefaultTrasactionsTaxonomyFromServiceCode(category_code: String?): String? {
  return when (category_code) {
    "SVC", "DOC", "HOS", "SPA", "SAL" -> "Appointments"
    "EDU" -> "Admission Requests"
    "HOT" -> "Room Bookings"
    "RTL", "MFG" -> "Orders"
    "CAF" -> "Food Orders"
    else -> "Orders"
  }
}

fun getSecondTypeTrasactionsTaxonomyFromServiceCode(category_code: String?): String? {
  return when (category_code) {
    "DOC", "HOS" -> "Video Consultations"
    else -> ""
  }
}

fun getOrderAnalyticsTaxonomyFromServiceCode(category_code: String?): String? {
  return when (category_code) {
    "DOC", "HOS", "SVC", "SPA", "SAL" -> "Appointment Analytics"
    "EDU" -> "Admission Request Analytics"
    "HOT" -> "Booking Analytics"
    "RTL", "MFG", "CAF" -> "Order Analytics"
    else -> "Order Analytics"
  }
}

fun getCustomerAppointmentTaxonomyFromServiceCode(category_code: String?): String? {
  //" Customer Appointments" for "SVC","DOC", "HOS","SPA", "SAL"  & "Customer Orders" for all others.
  return when (category_code) {
    "DOC", "HOS" -> "Customer Appointments"
    "SVC", "SPA", "SAL", "HOT" -> "Customer Bookings"
    else -> "Customer Orders"
  }
}


fun getCustomerTypeFromServiceCode(category_code: String?): String {
  //" Customer Appointments" for "SVC","DOC", "HOS","SPA", "SAL"  & "Customer Orders" for all others.
  return when (category_code) {
    "DOC", "HOS" -> "Appointments"
    "SVC", "SPA", "SAL", "HOT" -> "Bookings"
    else -> "Orders"
  }
}


fun isRoomBooking(category_code: String): Boolean {
  return "HOT" == category_code
}

fun getProductCatalogTaxonomyFromServiceCode(category_code: String?): String? {
  return when (category_code) {
    "SVC", "DOC", "HOS", "SPA", "SAL" -> "Service Catalogue"
    "EDU" -> "Course Catalogue"
    "HOT" -> "Room Inventory"
    "RTL", "MFG" -> "Products Catalogue"
    "CAF" -> "Digital Food Menu"
    else -> "Catalogue"
  }
}

fun getLatestUpdatesTaxonomyFromServiceCode(category_code: String?): String? {
  return when (category_code) {
    "DOC", "HOS" -> "Latest Updates & Health Tips"
    "SPA", "SAL" -> "Latest Updates & Offers"
    "HOT" -> "Latest Updates, News & Events"
    "MFG" -> "Latest Updates & News"
    "CAF", "EDU" -> "Latest Updates & Tips"
    else -> "Latest Updates"
  }
}

fun getSingleProductTaxonomyFromServiceCode(category_code: String?): String? {
  return when (category_code) {
    "SVC", "DOC", "HOS", "SPA", "SAL" -> "Service"
    "EDU" -> "Course"
    "HOT" -> "Room"
    "RTL", "MFG" -> "Product"
    "CAF" -> "Food Item"
    else -> "Catalogue"
  }
}


fun getProductType(category_code: String?): String {
  return when (category_code) {
    "SVC", "DOC", "HOS", "SPA", "SAL" -> "SERVICES"
    else -> "PRODUCTS"
  }
}

fun isDoctor(category_code: String?): Boolean {
  return getAptType(category_code) == "DOC_HOS"
}

fun getAptType(category_code: String?): String {
  return when (category_code) {
    "SPA", "SAL", "SVC" -> "SPA_SAL_SVC"
    "DOC", "HOS" -> "DOC_HOS"
    else -> "OTHERS"
  }
}

fun getRoiSummaryType(category_code: String?): String? {
  return category_code
//  return when (category_code) {
//    "DOC", "HOS" -> "DOC"
//    "SVC", "SPA", "SAL" -> "SPA"
//    else -> "MFG"
//  }
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

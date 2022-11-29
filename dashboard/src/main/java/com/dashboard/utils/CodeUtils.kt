package com.dashboard.utils

import com.framework.pref.UserSessionManager


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

fun getAptType(category_code: String?): String {
  return when (category_code) {
    "SPA", "SAL", "SVC" -> "SPA_SAL_SVC"
    "DOC", "HOS" -> "DOC_HOS"
    else -> "OTHERS"
  }
}

fun getRoiSummaryType(category_code: String?): String {
  return when (category_code) {
    "DOC", "HOS" -> "DOC"
    "SVC", "SPA", "SAL" -> "SPA"
    else -> "MFG"
  }
}

fun UserSessionManager.isProduct(): Boolean {
  return getProductType(this.fP_AppExperienceCode) == "PRODUCTS"
}

fun UserSessionManager.isService(): Boolean {
  return getProductType(this.fP_AppExperienceCode) == "SERVICES"
}

fun UserSessionManager.isHotel(): Boolean {
  return this.fP_AppExperienceCode.equals("HOT")
}

fun UserSessionManager.isDocHos(): Boolean {
  return this.fP_AppExperienceCode.equals("DOC") || this.fP_AppExperienceCode.equals("HOS")
}

fun UserSessionManager.isRestaurant(): Boolean {
  return this.fP_AppExperienceCode.equals("CAF")
}

fun UserSessionManager.isSpa(): Boolean {
  return this.fP_AppExperienceCode.equals("SPA")
}

fun UserSessionManager.isSpaSal(): Boolean {
  return this.fP_AppExperienceCode.equals("SPA") || this.fP_AppExperienceCode.equals("SAL")
}

fun UserSessionManager.isSpaSalSvc(): Boolean {
  return this.fP_AppExperienceCode.equals("SPA") || this.fP_AppExperienceCode.equals("SAL") || this.fP_AppExperienceCode.equals("SVC")
}

fun UserSessionManager.isManufacturing(): Boolean {
  return this.fP_AppExperienceCode.equals("MFG")
}

fun UserSessionManager.isEducation(): Boolean {
  return this.fP_AppExperienceCode.equals("EDU")
}


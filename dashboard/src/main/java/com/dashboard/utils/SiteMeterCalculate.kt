package com.dashboard.utils

import com.dashboard.AppDashboardApplication
import com.dashboard.R
import com.dashboard.pref.Key_Preferences
import com.dashboard.pref.Key_Preferences.PREF_KEY_TWITTER_LOGIN
import com.dashboard.pref.UserSessionManager
import com.inventoryorder.model.floatMessage.MessageModel

private val businessNameWeight = 5
private val businessDescriptionWeight = 10
private val businessCategoryWeight = 5
private val featuredImageWeight = 10
private val phoneWeight = 5
private val emailWeight = 5
private val businessAddressWeight = 10
private val businessTimingWeight = 5
private val facebookWeight = 10
private val twitterWeight = 10
private val shareWebsiteWeight = 5
private val firstUpdatesWeight = 5
private val logoWeight = 5
private val onUpdate = 4

fun UserSessionManager.siteMeterCalculation(): Int {
  val res = AppDashboardApplication.instance.resources
  val prefTwitter = getPreferenceTwitter()
  var siteMeterTotalWeight = 0
  if (getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI).isNullOrEmpty().not()) {
    siteMeterTotalWeight += 10
  }
  if (getFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER).isNullOrEmpty().not() && res.getString(R.string.phoneNumber_percentage) != "0") {
    siteMeterTotalWeight += phoneWeight
  }
  if (getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY).isNullOrEmpty().not() && res.getString(R.string.businessCategory_percentage) != "0") {
    siteMeterTotalWeight += businessCategoryWeight
  }
  if (getFPDetails(Key_Preferences.GET_FP_DETAILS_IMAGE_URI).isNullOrEmpty().not() && res.getString(R.string.featuredImage_percentage) != "0") {
    siteMeterTotalWeight += featuredImageWeight
  }
  if (getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME).isNullOrEmpty().not() && res.getString(R.string.businessName_percentage) != "0") {
    siteMeterTotalWeight += businessNameWeight
  }
  if (getFPDetails(Key_Preferences.GET_FP_DETAILS_DESCRIPTION).isNullOrEmpty().not() && res.getString(R.string.businessdescription_percentage) != "0") {
    siteMeterTotalWeight += businessDescriptionWeight
  }
  if (prefTwitter.getBoolean(PREF_KEY_TWITTER_LOGIN, false) && fbShareEnabled && fbPageShareEnabled) {
    siteMeterTotalWeight += twitterWeight
  }
  if (getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS).isNullOrEmpty().not() && getFPDetails(Key_Preferences.LATITUDE).isNullOrEmpty().not() &&
      getFPDetails(Key_Preferences.LONGITUDE).isNullOrEmpty().not() && res.getString(R.string.address_percentage) != "0") {
    siteMeterTotalWeight += businessAddressWeight
  }
  if (getFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL).isNullOrEmpty().not() && res.getString(R.string.email_percentage) != "0") {
    siteMeterTotalWeight += emailWeight
  }
  if (MessageModel().getStoreBizFloatSize() < 5) {
    siteMeterTotalWeight += MessageModel().getStoreBizFloatSize() * onUpdate
  } else {
    siteMeterTotalWeight += 20
  }
  if (getFPDetails(Key_Preferences.GET_FP_DETAILS_LogoUrl).isNullOrEmpty().not() && res.getString(R.string.Logo_percentage) != "0") {
    siteMeterTotalWeight += logoWeight
  }
  if (businessHours) {
    siteMeterTotalWeight += businessTimingWeight
  }
  return siteMeterTotalWeight
}
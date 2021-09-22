package com.dashboard.utils

import android.content.res.Resources
import com.dashboard.AppDashboardApplication
import com.dashboard.R
import com.dashboard.model.live.drScore.siteMeter.SiteMeterModel
import com.dashboard.model.live.drScore.siteMeter.SiteMeterScoreDetails
import com.framework.pref.Key_Preferences
import com.framework.pref.Key_Preferences.PREF_KEY_TWITTER_LOGIN
import com.framework.pref.UserSessionManager
import com.inventoryorder.model.floatMessage.MessageModel
import java.util.*
import kotlin.collections.ArrayList

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

const val businessHoursWeight = 5
var siteMeterTotalWeight = 0
var fiveUpdatesDone = false

const val businessName = 0
const val description = 1
const val category = 2
const val email = 3
const val phone = 4
const val address = 5
const val businessHoursV = 6
const val image = 7
const val logo = 8
const val post = 9
const val social = 10
const val domain = 11

fun UserSessionManager.siteMeterCalculation(): Int {
  val res = AppDashboardApplication.instance.resources
  val prefTwitter = AppDashboardApplication.instance.getPreferenceTwitter()
  siteMeterTotalWeight = 0
  if (getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI).isNullOrEmpty().not()) {
    siteMeterTotalWeight += 10
  }
  if (getFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER).isNullOrEmpty()
      .not() && res.getString(R.string.phoneNumber_percentage) != "0"
  ) {
    siteMeterTotalWeight += phoneWeight
  }
  if (getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY).isNullOrEmpty()
      .not() && res.getString(R.string.businessCategory_percentage) != "0"
  ) {
    siteMeterTotalWeight += businessCategoryWeight
  }
  if (getFPDetails(Key_Preferences.GET_FP_DETAILS_IMAGE_URI).isNullOrEmpty().not() && res.getString(
      R.string.featuredImage_percentage
    ) != "0"
  ) {
    siteMeterTotalWeight += featuredImageWeight
  }
  if (getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME).isNullOrEmpty()
      .not() && res.getString(R.string.businessName_percentage) != "0"
  ) {
    siteMeterTotalWeight += businessNameWeight
  }
  if (getFPDetails(Key_Preferences.GET_FP_DETAILS_DESCRIPTION).isNullOrEmpty()
      .not() && res.getString(R.string.businessdescription_percentage) != "0"
  ) {
    siteMeterTotalWeight += businessDescriptionWeight
  }
  if (prefTwitter.getBoolean(
      PREF_KEY_TWITTER_LOGIN,
      false
    ) && fbShareEnabled && fbPageShareEnabled
  ) {
    siteMeterTotalWeight += twitterWeight
  }
  if (getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS).isNullOrEmpty().not() && getFPDetails(
      Key_Preferences.LATITUDE
    ).isNullOrEmpty().not() &&
    getFPDetails(Key_Preferences.LONGITUDE).isNullOrEmpty()
      .not() && res.getString(R.string.address_percentage) != "0"
  ) {
    siteMeterTotalWeight += businessAddressWeight
  }
  if (getFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL).isNullOrEmpty()
      .not() && res.getString(R.string.email_percentage) != "0"
  ) {
    siteMeterTotalWeight += emailWeight
  }
  if (MessageModel().getStoreBizFloatSize() < 5) {
    siteMeterTotalWeight += MessageModel().getStoreBizFloatSize() * onUpdate
  } else {
    siteMeterTotalWeight += 20
  }
  if (getFPDetails(Key_Preferences.GET_FP_DETAILS_LogoUrl).isNullOrEmpty()
      .not() && res.getString(R.string.Logo_percentage) != "0"
  ) {
    siteMeterTotalWeight += logoWeight
  }
  if (businessHours) {
    siteMeterTotalWeight += businessTimingWeight
  }
  return siteMeterTotalWeight
}


private fun loadData(res: Resources): ArrayList<SiteMeterModel> {
  val siteData = ArrayList<SiteMeterModel>()

  if (res.getString(R.string.buydomain_percentage) != "0") siteData.add(
    SiteMeterModel(
      domain,
      "Buy/Link a Domain",
      "Give your business an identity",
      "+10%",
      false,
      12
    )
  )
  //1
  if (res.getString(R.string.phoneNumber_percentage) != "0") siteData.add(
    SiteMeterModel(
      phone,
      "Phone Number",
      "Help customers to reach you instantly",
      "+5%",
      false,
      5
    )
  )
  //2
  if (res.getString(R.string.businessCategory_percentage) != "0") siteData.add(
    SiteMeterModel(
      category,
      "Business Category",
      "Choose a business category",
      "+5%",
      false,
      3
    )
  )
  //3
  if (res.getString(R.string.featuredImage_percentage) != "0") siteData.add(
    SiteMeterModel(
      image,
      "Featured Image",
      "Add a relevant image",
      "+10%",
      false,
      8
    )
  )
  //4
  if (res.getString(R.string.businessName_percentage) != "0") siteData.add(
    SiteMeterModel(
      businessName,
      "Business Name",
      "Add business name",
      "+5%",
      false,
      1
    )
  )
  //5
  if (res.getString(R.string.businessdescription_percentage) != "0") siteData.add(
    SiteMeterModel(
      description,
      "Business Description",
      "Describe your business",
      "+10%",
      false,
      2
    )
  )
  //6
  if (res.getString(R.string.social_percentage) != "0") siteData.add(
    SiteMeterModel(
      social,
      "Social Share",
      "Connect to Facebook and Twitter",
      "+10%",
      false,
      11
    )
  )
  //7
  siteData.add(
    SiteMeterModel(
      address,
      "Business Address",
      "Help your customers find you",
      "+10%",
      false,
      6
    )
  )
  //8
  if (res.getString(R.string.email_percentage) != "0") siteData.add(
    SiteMeterModel(
      email,
      "Email",
      "Add your email",
      "+5%",
      false,
      4
    )
  )
  //9
  if (res.getString(R.string.postUpdate_percentage) != "0") {
    val `val` =
      if (MessageModel().getStoreBizFloatSize() < 5) 20 - MessageModel().getStoreBizFloatSize() * onUpdate else 20
    siteData.add(
      SiteMeterModel(
        post,
        "Post 5 Updates",
        "Message regularly and relevantly",
        "+$`val`%",
        false,
        10
      )
    )
  }
  //10
  if (res.getString(R.string.share_percentage) != "0") {
    siteData.add(SiteMeterModel(logo, "Business Logo", "Add a business logo", "+5%", false, 9))
  }
  if (res.getString(R.string.business_hours) != "0") siteData.add(
    SiteMeterModel(
      businessHoursV,
      "Business Hours",
      "Display business hours",
      "+5%",
      false,
      7
    )
  )
  Collections.sort(siteData, Collections.reverseOrder())
  return siteData
}

fun UserSessionManager.siteMeterData(callback: (data: SiteMeterScoreDetails?) -> Unit) {
  val context = AppDashboardApplication.instance
  val res = context.resources
  val prefTwitter = context.getPreferenceTwitter()
  val siteData = loadData(res)
  val siteMeterScoreDetails = SiteMeterScoreDetails()
  siteMeterTotalWeight = 0
  if (siteData.isNullOrEmpty()) return callback(null)
  siteData.forEach {
    when (it.position) {
      domain -> {
        if (getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI).isNullOrEmpty().not()) {
          siteMeterTotalWeight += 10
          it.status = true
          it.sortChar = 1
          siteMeterScoreDetails.channelSync.add(it)
        } else {
          it.status = false
          it.sortChar = 2
        }
        siteMeterScoreDetails.contentManagement.add(it)
      }
      phone -> {
        if (getFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER).isNullOrEmpty()
            .not() && res.getString(R.string.phoneNumber_percentage) != "0"
        ) {
          siteMeterTotalWeight += phoneWeight
          it.status = true
          it.sortChar = 1
          siteMeterScoreDetails.channelSync.add(it)
        } else {
          it.status = false
          it.sortChar = 2
        }
        siteMeterScoreDetails.businessProfile.add(it)
      }
      category -> {
        if (getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY).isNullOrEmpty()
            .not() && res.getString(R.string.businessCategory_percentage) != "0"
        ) {
          siteMeterTotalWeight += businessCategoryWeight
          it.status = true
          it.sortChar = 1
          siteMeterScoreDetails.channelSync.add(it)
        } else {
          it.status = false
          it.sortChar = 2
        }
        siteMeterScoreDetails.contentManagement.add(it)
      }
      image -> {
        if (getFPDetails(Key_Preferences.GET_FP_DETAILS_IMAGE_URI).isNullOrEmpty()
            .not() && res.getString(R.string.featuredImage_percentage) != "0"
        ) {
          siteMeterTotalWeight += featuredImageWeight
          it.status = true
          it.sortChar = 1
          siteMeterScoreDetails.channelSync.add(it)
        } else {
          it.status = false
          it.sortChar = 2
        }
        siteMeterScoreDetails.businessProfile.add(it)
      }
      businessName -> {
        if (getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME).isNullOrEmpty()
            .not() && res.getString(R.string.businessName_percentage) != "0"
        ) {
          siteMeterTotalWeight += businessNameWeight
          it.status = true
          it.sortChar = 1
          siteMeterScoreDetails.channelSync.add(it)
        } else {
          it.status = false
          it.sortChar = 2
        }
        siteMeterScoreDetails.businessProfile.add(it)
      }
      description -> {
        if (getFPDetails(Key_Preferences.GET_FP_DETAILS_DESCRIPTION).isNullOrEmpty()
            .not() && res.getString(R.string.businessdescription_percentage) != "0"
        ) {
          siteMeterTotalWeight += businessDescriptionWeight
          it.status = true
          it.sortChar = 1
          siteMeterScoreDetails.channelSync.add(it)
        } else {
          it.status = false
          it.sortChar = 2
        }
        siteMeterScoreDetails.businessProfile.add(it)
      }
      social -> {
        if (prefTwitter.getBoolean(
            PREF_KEY_TWITTER_LOGIN,
            false
          ) && fbShareEnabled && fbPageShareEnabled
        ) {
          siteMeterTotalWeight += twitterWeight
          it.status = true
          it.sortChar = 1
          siteMeterScoreDetails.channelSync.add(it)
        } else {
          it.status = false
          it.sortChar = 2
        }
        siteMeterScoreDetails.contentManagement.add(it)
      }
      address -> {
        if (getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS).isNullOrEmpty()
            .not() && getFPDetails(Key_Preferences.LATITUDE).isNullOrEmpty().not() &&
          getFPDetails(Key_Preferences.LONGITUDE).isNullOrEmpty()
            .not() && res.getString(R.string.address_percentage) != "0"
        ) {
          siteMeterTotalWeight += businessAddressWeight
          it.status = true
          it.sortChar = 1
          siteMeterScoreDetails.channelSync.add(it)
        } else {
          it.status = false
          it.sortChar = 2
        }
        siteMeterScoreDetails.businessProfile.add(it)
      }
      email -> {
        if (getFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL).isNullOrEmpty()
            .not() && res.getString(R.string.email_percentage) != "0"
        ) {
          siteMeterTotalWeight += emailWeight
          it.status = true
          it.sortChar = 1
          siteMeterScoreDetails.channelSync.add(it)
        } else {
          it.status = false
          it.sortChar = 2
        }
        siteMeterScoreDetails.businessProfile.add(it)
      }
      post -> {
        val bizFloat = MessageModel().getStoreBizFloatSize()
        if (bizFloat < 5 && !fiveUpdatesDone) {
          siteMeterTotalWeight += bizFloat * onUpdate
          it.status = false
          it.sortChar = 2
          it.isPost = true
        } else {
          fiveUpdatesDone = true
          siteMeterTotalWeight += 20
          it.status = true
          it.sortChar = 1
          it.isPost = true
        }
        siteMeterScoreDetails.contentManagement.add(it)
        val data = it.copy()
        data.Percentage = "+${if (bizFloat < 5 && !fiveUpdatesDone) bizFloat * onUpdate else 20}%"
        siteMeterScoreDetails.channelSync.add(data)
      }
      logo -> {
        if (getFPDetails(Key_Preferences.GET_FP_DETAILS_LogoUrl).isNullOrEmpty()
            .not() && res.getString(R.string.Logo_percentage) != "0"
        ) {
          siteMeterTotalWeight += logoWeight
          it.status = true
          it.sortChar = 1
          siteMeterScoreDetails.channelSync.add(it)
        } else {
          it.status = false
          it.sortChar = 2
        }
        siteMeterScoreDetails.businessProfile.add(it)
      }
      businessHoursV -> {
        if (businessHours) {
          siteMeterTotalWeight += businessHoursWeight
          it.status = true
          it.sortChar = 1
          siteMeterScoreDetails.channelSync.add(it)
        } else {
          it.status = false
          it.sortChar = 2
        }
        siteMeterScoreDetails.contentManagement.add(it)
      }
    }
  }
  siteMeterScoreDetails.siteMeterTotalWeight = siteMeterTotalWeight
  callback(siteMeterScoreDetails)
}

private fun SiteMeterModel.copy(): SiteMeterModel {
  return SiteMeterModel(position, Title, Desc, Percentage, status, sortChar, isPost)
}

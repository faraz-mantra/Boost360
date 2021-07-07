package com.inventoryorder.model

import java.io.Serializable

const val AUTHORIZATION_3 = "59f6bc18dd304110e0972777"
const val CLIENT_ID_1 = "2D5C6BB4F46457422DA36B4977BD12E37A92EEB13BB4423A548387BA54DCEBD5"
const val CLIENT_ID_3 = "A816E08AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F51770E46AD86"

data class PreferenceData(
  val clientId: String? = null,
  val userProfileId: String? = null,
  val authorization: String? = null,
  val fpTag: String? = null,
  var userPrimaryMobile: String? = null,
  val webSiteUrl: String? = null,
  val emailDoctor: String? = null,
  val latitude: String? = null,
  val longitude: String? = null,
  val experienceCode: String? = null,
) : Serializable
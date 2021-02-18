package com.dashboard.model.live.premiumBanner


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LearnMoreLink(
//  @SerializedName("createdon")
//  var createdon: String? = null,
//  @SerializedName("isarchived")
//  var isarchived: Boolean? = null,
//  @SerializedName("_kid")
//  var kid: String? = null,
  @SerializedName("link")
  var link: String? = null,
  @SerializedName("link_description")
  var linkDescription: String? = null,
//  @SerializedName("_parentClassId")
//  var parentClassId: String? = null,
//  @SerializedName("_parentClassName")
//  var parentClassName: String? = null,
//  @SerializedName("_propertyName")
//  var propertyName: String? = null,
//  @SerializedName("updatedon")
//  var updatedon: String? = null,
//  @SerializedName("websiteid")
//  var websiteid: String? = null
): Serializable
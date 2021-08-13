package com.marketplace.model.features


import com.google.gson.annotations.SerializedName

data class ExpertConnect(
  @SerializedName("contact_number")
  var contactNumber: String? = null,
  @SerializedName("createdon")
  var createdon: String? = null,
  @SerializedName("is_online")
  var isOnline: Boolean? = null,
  @SerializedName("isarchived")
  var isarchived: Boolean? = null,
  @SerializedName("_kid")
  var kid: String? = null,
  @SerializedName("line1")
  var line1: String? = null,
  @SerializedName("line2")
  var line2: String? = null,
  @SerializedName("offline_message")
  var offlineMessage: String? = null,
  @SerializedName("_parentClassId")
  var parentClassId: String? = null,
  @SerializedName("_parentClassName")
  var parentClassName: String? = null,
  @SerializedName("_propertyName")
  var propertyName: String? = null,
  @SerializedName("updatedon")
  var updatedon: String? = null,
  @SerializedName("websiteid")
  var websiteid: String? = null
)
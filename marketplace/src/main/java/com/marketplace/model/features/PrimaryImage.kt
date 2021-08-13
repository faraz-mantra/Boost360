package com.marketplace.model.features


import com.google.gson.annotations.SerializedName

data class PrimaryImage(
  @SerializedName("createdon")
  var createdon: String? = null,
  @SerializedName("isarchived")
  var isarchived: Boolean? = null,
  @SerializedName("_kid")
  var kid: String? = null,
  @SerializedName("_parentClassId")
  var parentClassId: String? = null,
  @SerializedName("_parentClassName")
  var parentClassName: String? = null,
  @SerializedName("_propertyName")
  var propertyName: String? = null,
  @SerializedName("updatedon")
  var updatedon: String? = null,
  @SerializedName("url")
  var url: String? = null,
  @SerializedName("websiteid")
  var websiteid: String? = null
)
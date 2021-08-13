package com.marketplace.model.features


import com.google.gson.annotations.SerializedName

data class ExtendedProperty(
  @SerializedName("createdon")
  var createdon: String? = null,
  @SerializedName("isarchived")
  var isarchived: Boolean? = null,
  @SerializedName("key")
  var key: String? = null,
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
  @SerializedName("value")
  var value: String? = null,
  @SerializedName("websiteid")
  var websiteid: String? = null
)
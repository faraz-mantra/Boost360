package com.festive.poster.models

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName

data class CustomerDetails(
  @SerializedName("ImageUri")
  var imageUri: String? = null,
  @SerializedName("Name")
  var name: String? = null,
  @SerializedName("Tag")
  var tag: String? = null,
  @SerializedName("Address")
  var address: String? = null,
  @SerializedName("ContactName")
  var contactName: String? = null,
  @SerializedName("Email")
  var email: String? = null,
  @SerializedName("PrimaryNumber")
  var primaryNumber: String? = null,
  @SerializedName("RootAliasUri")
  var rootAliasUri: String,
  var lat: Double = 0.0,
  var lng: Double = 0.0,
  var FPWebWidgets:ArrayList<String>?=null,
) :BaseResponse()
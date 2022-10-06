package com.boost.dbcenterapi.data.api_model.gst


import com.google.gson.annotations.SerializedName

data class Result(
  @SerializedName("Address")
  var address: Address? = null,
  @SerializedName("GSTIN")
  var gSTIN: String? = null,
  @SerializedName("GSTINStatus")
  var gSTINStatus: String? = null,
  @SerializedName("GSTINStatusCode")
  var gSTINStatusCode: Int? = null,
  @SerializedName("LastUpdatedDate")
  var lastUpdatedDate: String? = null,
  @SerializedName("LegalName")
  var legalName: String? = null,
  @SerializedName("NatureOfBusinessActivity")
  var natureOfBusinessActivity: List<String>? = null,
  @SerializedName("PAN")
  var pAN: String? = null,
  @SerializedName("PrimaryNatureOfBusinessActivity")
  var primaryNatureOfBusinessActivity: String? = null,
  @SerializedName("TradeName")
  var tradeName: String? = null
)
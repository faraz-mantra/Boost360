package com.appservice.model.kycData.kycList


import com.google.gson.annotations.SerializedName

data class PropertyKyc(
  @SerializedName("DataType")
  var dataType: String? = null,
  @SerializedName("DisplayName")
  var displayName: String? = null,
  @SerializedName("IsRequired")
  var isRequired: Boolean? = null,
  @SerializedName("PropertyName")
  var propertyName: String? = null,
  @SerializedName("PropertyType")
  var propertyType: Int? = null,
  @SerializedName("ValidationRegex")
  var validationRegex: Any? = null
)
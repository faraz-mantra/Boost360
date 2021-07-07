package com.appservice.model.pickUpAddress

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PickUpData(
  @SerializedName("AddressProof")
  var addressProof: String? = null,
  @SerializedName("AreaName")
  var areaName: String? = null,
  @SerializedName("City")
  var city: String? = null,
  @SerializedName("ContactNumber")
  var contactNumber: String? = null,
  @SerializedName("Country")
  var country: String? = null,
  @SerializedName("CreatedOn")
  var createdOn: String? = null,
  @SerializedName("_id")
  var id: String? = null,
  @SerializedName("IsArchived")
  var isArchived: Boolean? = null,
  @SerializedName("PinCode")
  var pinCode: String? = null,
  @SerializedName("State")
  var state: String? = null,
  @SerializedName("StreetAddress")
  var streetAddress: String? = null,
  @SerializedName("UpdatedOn")
  var updatedOn: String? = null,
  @SerializedName("WebsiteId")
  var websiteId: String? = null
) : Serializable
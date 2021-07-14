package com.appservice.model.serviceProduct.addProductImage.response


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DataImage(
  @SerializedName("ActionId")
  var actionId: String? = null,
  @SerializedName("CreatedOn")
  var createdOn: String? = null,
  @SerializedName("_id")
  var id: String? = null,
  @SerializedName("image")
  var image: ImageResponse? = null,
  @SerializedName("IsArchived")
  var isArchived: Boolean? = null,
  @SerializedName("_pid")
  var pid: String? = null,
  @SerializedName("UpdatedOn")
  var updatedOn: String? = null,
  @SerializedName("UserId")
  var userId: String? = null,
  @SerializedName("WebsiteId")
  var websiteId: String? = null
) : Serializable
package com.appservice.model.panGst

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PanDetails(
  @SerializedName("documentName")
  var documentName: String? = null,
  @SerializedName("imageLink")
  var imageLink: String? = null,
  @SerializedName("name")
  var name: String? = null,
  @SerializedName("number")
  var number: String? = null,
  @SerializedName("status")
  var status: String? = null
) : Serializable {

  fun getImageUrl(): String {
    return imageLink ?: ""
  }

  fun imageLinkExtension(): String {
    return getImageUrl().substring(getImageUrl().lastIndexOf(".") + 1)
  }
}
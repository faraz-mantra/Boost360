package com.appservice.model.testimonial

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProfileImage(
  @SerializedName("description")
  var description: String? = null,
  @SerializedName("fileName")
  var fileName: String? = null,
  @SerializedName("image")
  var image: String? = null,
  @SerializedName("imageFileType")
  var imageFileType: String? = null
) : Serializable
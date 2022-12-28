package com.appservice.model.testimonial.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProfileImageN(
  @SerializedName("ActualImage")
  var actualImage: String? = null,
  @SerializedName("Description")
  var description: String? = null,
  @SerializedName("ImageId")
  var imageId: String? = null,
  @SerializedName("TileImage")
  var tileImage: String? = null
) : Serializable
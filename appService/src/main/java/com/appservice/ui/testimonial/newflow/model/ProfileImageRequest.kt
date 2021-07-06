package com.appservice.ui.testimonial.newflow.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProfileImageRequest(

  @field:SerializedName("ProfileImage")
  var profileImage: ProfileImageTestimonial? = null
):Serializable

data class ProfileImageTestimonial(
  @field:SerializedName("Description")
  var description: String? = "",
  @field:SerializedName("ImageFileType")
  var imageFileType: String? = null,

  @field:SerializedName("FileName")
  var fileName: String? = null,

  @field:SerializedName("Image")
  var image: String? = null,
  @field:SerializedName("ImageId")
  var imageId: String? = null,

  @field:SerializedName("ActualImage")
  var actualImage: String? = null,

  @field:SerializedName("TileImage")
  var tileImage: String? = null
):Serializable

package com.appservice.staffs.model

import com.google.gson.annotations.SerializedName

data class StaffUpdateImageRequest(
  @field:SerializedName("StaffId")
  var staffId: String? = null,
  @field:SerializedName("Image")
  var image: StaffImage? = null,
  @field:SerializedName("ImageType")
  var imageType: Int? = IMAGETYPE.PROFILE.ordinal,
)
enum class IMAGETYPE{
  PROFILE,
  SIGNATURE
}
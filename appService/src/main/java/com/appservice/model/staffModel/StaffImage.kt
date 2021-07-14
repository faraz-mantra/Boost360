package com.appservice.model.staffModel

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class StaffImage(
  @field:SerializedName("Image")
  var image: String? = null,
  @field:SerializedName("FileName")
  var fileName: String? = null,
  @field:SerializedName("ImageFileType")
  var imageFileType: String? = null,
) : Serializable, BaseResponse()
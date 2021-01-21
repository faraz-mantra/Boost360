package com.appservice.staffs.model

import com.google.gson.annotations.SerializedName

data class StaffUpdateImageRequest(

	@field:SerializedName("StaffId")
	val staffId: String? = null,

	@field:SerializedName("Image")
	val image: Image? = null
)

data class Image(

	@field:SerializedName("ImageFileType")
	val imageFileType: String? = null,

	@field:SerializedName("FileName")
	val fileName: String? = null,

	@field:SerializedName("Image")
	val image: String? = null
)

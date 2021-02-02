package com.appservice.staffs.model

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class StaffImage(

		@field:SerializedName("image")
		var image: String? = null,

		@field:SerializedName("fileName")
		var fileName: String? = null,

		@field:SerializedName("imageFileType")
		var imageFileType: String? = null,
) : Serializable, BaseResponse()
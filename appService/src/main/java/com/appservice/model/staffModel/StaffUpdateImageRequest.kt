package com.appservice.model.staffModel

import com.google.gson.annotations.SerializedName

data class StaffUpdateImageRequest(
		@field:SerializedName("StaffId")
		var staffId: String? = null,

		@field:SerializedName("Image")
		var image: StaffImage? = null,
)


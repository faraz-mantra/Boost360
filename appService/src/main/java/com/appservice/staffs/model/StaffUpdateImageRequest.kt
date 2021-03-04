package com.appservice.staffs.model

import com.google.gson.annotations.SerializedName

data class StaffUpdateImageRequest(
		@field:SerializedName("StaffId")
		var staffId: String? = null,

		@field:SerializedName("Image")
		var image: StaffImage? = null,
)


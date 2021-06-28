package com.appservice.model.staffModel

import com.google.gson.annotations.SerializedName

data class ResponseUpdateProfile(
		@field:SerializedName("Error")
		val error: Any? = null,
		@field:SerializedName("StatusCode")
		val statusCode: Int? = null,
		@field:SerializedName("Result")
		val result: String? = null,
)

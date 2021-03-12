package com.dashboard.model

import com.google.gson.annotations.SerializedName

data class UpdateOwnersDataRequest(

	@field:SerializedName("Query")
	val query: String? = null,

	@field:SerializedName("UpdateValue")
	val updateValue: String? = null
)

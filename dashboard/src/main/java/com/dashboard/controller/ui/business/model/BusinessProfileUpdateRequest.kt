package com.dashboard.controller.ui.business.model

import com.google.gson.annotations.SerializedName

data class BusinessProfileUpdateRequest(

	@field:SerializedName("fpTag")
	val fpTag: String? = null,

	@field:SerializedName("clientId")
	val clientId: String? = null,

	@field:SerializedName("updates")
	val updates: List<UpdatesItem?>? = null
)

data class UpdatesItem(

	@field:SerializedName("value")
	val value: String? = null,

	@field:SerializedName("key")
	val key: String? = null
)

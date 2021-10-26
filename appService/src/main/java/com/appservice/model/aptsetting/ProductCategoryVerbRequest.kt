package com.appservice.model.aptsetting

import com.google.gson.annotations.SerializedName

data class ProductCategoryVerbRequest(

	@field:SerializedName("fpTag")
	var fpTag: String? = null,

	@field:SerializedName("clientId")
	var clientID: String? = null,

	@field:SerializedName("enterpriseId")
	var enterpriseId: Any? = null,

	@field:SerializedName("updates")
	var updates: List<UpdatesItem?>? = null
)

data class UpdatesItem(

	@field:SerializedName("value")
	var value: String? = null,

	@field:SerializedName("key")
	var key: String? = null
)

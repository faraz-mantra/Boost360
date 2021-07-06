package com.appservice.model.staffModel

import com.google.gson.annotations.SerializedName

data class ServiceListRequest(
		@field:SerializedName("FilterBy")
		val filterBy: FilterBy? = FilterBy("ALL", 0, 0),
		@field:SerializedName("Category")
		val category: String? = null,
		@field:SerializedName("FloatingPointTag")
		val floatingPointTag: String? = null,
)

data class FilterBy(
		@field:SerializedName("ServiceType")
		val serviceType: String? = null,
		@field:SerializedName("Limit")
		val limit: Int? = null,
		@field:SerializedName("Offset")
		var offset: Int? = null,
)

package com.appservice.offers.models

import com.google.gson.annotations.SerializedName

data class OfferListingRequest(

	@field:SerializedName("Filter")
	val filter: Filter? = null,

	@field:SerializedName("SortBy")
	val sortBy: SortBy? = null,

	@field:SerializedName("FloatingPointTag")
	val floatingPointTag: String? = null,

	@field:SerializedName("Limit")
	val limit: Int? = null,

	@field:SerializedName("Offset")
	val offset: Int? = null
)

data class SortBy(

	@field:SerializedName("SortingOrder")
	val sortingOrder: Int? = null,

	@field:SerializedName("SortingFactor")
	val sortingFactor: Int? = null
)

data class Filter(

	@field:SerializedName("IsAvailable")
	val isAvailable: Boolean? = null
)

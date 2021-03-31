package com.appservice.offers.models

import com.google.gson.annotations.SerializedName

data class OfferListingRequest(

	@field:SerializedName("Filter")
	var filter: Filter? = null,

	@field:SerializedName("SortBy")
	var sortBy: SortBy? = null,

	@field:SerializedName("FloatingPointTag")
	var floatingPointTag: String? = null,

	@field:SerializedName("Limit")
	var limit: Int? = null,

	@field:SerializedName("Offset")
	var offset: Int? = null
)

data class SortBy(

	@field:SerializedName("SortingOrder")
	var sortingOrder: Int? = null,

	@field:SerializedName("SortingFactor")
	var sortingFactor: Int? = null
)

data class Filter(

	@field:SerializedName("IsAvailable")
	var isAvailable: Boolean? = null
)

package com.appservice.offers.models

import com.google.gson.annotations.SerializedName

data class OfferDetailsRequest(

	@field:SerializedName("OfferId")
	val offerId: String? = null
)

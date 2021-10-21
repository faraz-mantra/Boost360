package com.appservice.offers.models

import com.google.gson.annotations.SerializedName

data class DeleteOfferRequest(

	@field:SerializedName("OfferId")
	val offerId: String? = null,

	@field:SerializedName("ClientId")
	val clientId: String? = null
)

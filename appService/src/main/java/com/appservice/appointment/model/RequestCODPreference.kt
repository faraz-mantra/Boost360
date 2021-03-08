package com.appservice.appointment.model

import com.google.gson.annotations.SerializedName

data class RequestCODPreference(

	@field:SerializedName("AcceptCodForHomeDelivery")
	var acceptCodForHomeDelivery: Boolean? = null,

	@field:SerializedName("ClientId")
	var clientId: String? = null,

	@field:SerializedName("AcceptCodForStorePickup")
	var acceptCodForStorePickup: Boolean? = null,

	@field:SerializedName("FloatingPointId")
	var floatingPointId: String? = null
)

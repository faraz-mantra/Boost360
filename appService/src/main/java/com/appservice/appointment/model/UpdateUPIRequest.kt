package com.appservice.appointment.model

import com.google.gson.annotations.SerializedName

data class UpdateUPIRequest(

	@field:SerializedName("ClientId")
	var clientId: String? = null,

	@field:SerializedName("UPIId")
	var uPIId: String? = null,

	@field:SerializedName("FloatingPointId")
	var floatingPointId: String? = null
)

package com.appservice.ui.catalog

import com.appservice.ui.catalog.common.TimingsItem
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RequestWeeklyAppointment(

		@field:SerializedName("duration")
	val duration: Int? = null,

		@field:SerializedName("timings")
	val timings: List<TimingsItem?>? = null,

		@field:SerializedName("serviceId")
	val serviceId: String? = null
)


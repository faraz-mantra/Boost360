package com.appservice.ui.catalog

import com.google.gson.annotations.SerializedName

data class RequestWeeklyAppointment(

	@field:SerializedName("duration")
	val duration: Int? = null,

	@field:SerializedName("timings")
	val timings: List<TimingsItem?>? = null,

	@field:SerializedName("serviceId")
	val serviceId: String? = null
)

data class TimingsItem(

	@field:SerializedName("time")
	val time: Time? = null,

	@field:SerializedName("day")
	val day: String? = null
)

data class Time(

	@field:SerializedName("from")
	val from: String? = null,

	@field:SerializedName("to")
	val to: String? = null
)

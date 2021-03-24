package com.inventoryorder.model.ordersdetails

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SpaAppointmentStaff(

	@field:SerializedName("duration")
	val duration: String? = null,

	@field:SerializedName("scheduledDateTime")
	val scheduledDateTime: String? = null,

	@field:SerializedName("staffName")
	val staffName: String? = null,

	@field:SerializedName("startTime")
	val startTime: String? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("endTime")
	val endTime: String? = null,

	@field:SerializedName("staffId")
	val staffId: String? = null
):Serializable{

	fun startTime(): String {
		return startTime ?: ""
	}
	fun startTimeRemoveAMPM(): String {
		return (startTime?.replace("AM","")?.replace("PM","")?:"").trim()
	}

	fun endTime(): String {
		return endTime ?: ""
	}

}

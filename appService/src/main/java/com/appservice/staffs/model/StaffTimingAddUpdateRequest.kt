package com.appservice.staffs.model

import com.google.gson.annotations.SerializedName

data class StaffTimingAddUpdateRequest(

	@field:SerializedName("StaffId")
	val staffId: String? = null,

	@field:SerializedName("WorkTimings")
	val workTimings: List<WorkTimingsItem?>? = null
)

data class WorkTimingsItem(

	@field:SerializedName("Timing")
	val timing: List<TimingItem?>? = null,

	@field:SerializedName("Day")
	val day: String? = null
)

data class TimingItem(

	@field:SerializedName("From")
	val from: String? = null,

	@field:SerializedName("To")
	val to: String? = null
)

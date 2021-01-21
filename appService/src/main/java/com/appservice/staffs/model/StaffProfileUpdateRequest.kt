package com.appservice.staffs.model

import com.google.gson.annotations.SerializedName

data class StaffProfileUpdateRequest(

	@field:SerializedName("ServiceIds")
	val serviceIds: List<String?>? = null,

	@field:SerializedName("Experience")
	val experience: Int? = null,

	@field:SerializedName("Description")
	val description: String? = null,

	@field:SerializedName("Specialisations")
	val specialisations: List<SpecialisationsItem?>? = null,

	@field:SerializedName("StaffId")
	val staffId: String? = null,

	@field:SerializedName("IsAvailable")
	val isAvailable: Boolean? = null,

	@field:SerializedName("FloatingPointTag")
	val floatingPointTag: String? = null,

	@field:SerializedName("Name")
	val name: String? = null
)


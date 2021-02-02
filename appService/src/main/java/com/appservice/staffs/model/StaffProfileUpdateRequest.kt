package com.appservice.staffs.model

import com.google.gson.annotations.SerializedName

data class StaffProfileUpdateRequest(

	@field:SerializedName("isAvailable")
	var isAvailable: Boolean? = null,

	@field:SerializedName("serviceIds")
	var serviceIds: List<String?>? = null,

	@field:SerializedName("gender")
	var gender: String? = null,

	@field:SerializedName("floatingPointTag")
	var floatingPointTag: String? = null,

	@field:SerializedName("name")
	var name: String? = null,

	@field:SerializedName("description")
	var description: String? = null,

	@field:SerializedName("experience")
	var experience: Int? = null,

	@field:SerializedName("staffId")
	var staffId: String? = null,

	@field:SerializedName("age")
	var age: Int? = null,

	@field:SerializedName("specialisations")
	var specialisations: List<SpecialisationsItem?>? = null
)



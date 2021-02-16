package com.appservice.staffs.model

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class StaffCreateProfileRequest(
		@field:SerializedName("Image")
		var image: StaffImage? = null,

		@field:SerializedName("IsAvailable")
		var isAvailable: Boolean? = null,

		@field:SerializedName("ServiceIds")
		var serviceIds: List<String?>? = null,

		@field:SerializedName("Gender")
		var gender: String? = null,

		@field:SerializedName("FloatingPointTag")
		var floatingPointTag: String? = null,

		@field:SerializedName("Name")
		var name: String? = null,

		@field:SerializedName("Description")
		var description: String? = null,

		@field:SerializedName("Experience")
		var experience: Int? = null,

		@field:SerializedName("Age")
		var age: Int? = null,

		@field:SerializedName("Specialisations")
		var specialisations: List<SpecialisationsItem?>? = null,
) : Serializable, BaseResponse()


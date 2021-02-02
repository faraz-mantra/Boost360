package com.appservice.staffs.model

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class StaffCreateProfileRequest(

		@field:SerializedName("image")
		var image: StaffImage? = null,

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

		@field:SerializedName("age")
		var age: Int? = null,

		@field:SerializedName("specialisations")
		var specialisations: List<SpecialisationsItem?>? = null,
) : Serializable, BaseResponse()


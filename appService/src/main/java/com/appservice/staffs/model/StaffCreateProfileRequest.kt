package com.appservice.staffs.model

import com.google.gson.annotations.SerializedName

data class StaffCreateProfileRequest(

		@field:SerializedName("image")
		val image: StaffImage? = null,

		@field:SerializedName("isAvailable")
		val isAvailable: Boolean? = null,

		@field:SerializedName("serviceIds")
		val serviceIds: List<String?>? = null,

		@field:SerializedName("gender")
		val gender: String? = null,

		@field:SerializedName("floatingPointTag")
		val floatingPointTag: String? = null,

		@field:SerializedName("name")
		val name: String? = null,

		@field:SerializedName("description")
		val description: String? = null,

		@field:SerializedName("experience")
		val experience: Int? = null,

		@field:SerializedName("age")
		val age: Int? = null,

		@field:SerializedName("specialisations")
		val specialisations: List<StaffSpecialisationsItem?>? = null
)

data class StaffSpecialisationsItem(

		@field:SerializedName("value")
		val value: String? = null,

		@field:SerializedName("key")
		val key: String? = null
)

data class StaffImage(

		@field:SerializedName("image")
		val image: String? = null,

		@field:SerializedName("fileName")
		val fileName: String? = null,

		@field:SerializedName("imageFileType")
		val imageFileType: String? = null
)

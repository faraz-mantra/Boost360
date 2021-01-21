package com.appservice.staffs.model

import com.framework.base.BaseRequest
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class StaffCreateProfileRequest (

	@SerializedName("ServiceIds")
	var serviceIds: List<String?>? = null,
	@SerializedName("Experience")
	var experience: Double? = null,

	@SerializedName("Description")
	var description: String? = null,

	@SerializedName("Specialisations")
	var specialisations: List<SpecialisationsItemStaffRequest?>? = null,

	@SerializedName("IsAvailable")
	var isAvailable: Boolean? = null,

	@SerializedName("FloatingPointTag")
	var floatingPointTag: String? = null,

	@SerializedName("Image")
	var image: Image? = null,

	@SerializedName("Name")
	var name: String? = null
)

data class SpecialisationsItemStaffRequest(

	@SerializedName("Value")
	var value: String? = null,

	@SerializedName("Key")
	var key: String? = null
): BaseRequest(), Serializable {}



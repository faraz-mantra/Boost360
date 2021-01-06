package com.appservice.staffs.model

import com.framework.base.BaseRequest
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class StaffCreateProfileRequest (

	@SerializedName("ServiceIds")
	val serviceIds: List<String?>? = null,

	@SerializedName("Experience")
	val experience: String? = null,

	@SerializedName("Description")
	val description: String? = null,

	@SerializedName("Specialisations")
	val specialisations: List<SpecialisationsItem?>? = null,

	@SerializedName("IsAvailable")
	val isAvailable: Boolean? = null,

	@SerializedName("FloatingPointTag")
	val floatingPointTag: String? = null,

	@SerializedName("Image")
	val image: Image? = null,

	@SerializedName("Name")
	val name: String? = null
)

data class SpecialisationsItem(

	@SerializedName("Value")
	val value: String? = null,

	@SerializedName("Key")
	val key: String? = null
): BaseRequest(), Serializable {}

data class Image(

	@SerializedName("ImageFileType")
	val imageFileType: String? = null,

	@SerializedName("FileName")
	val fileName: String? = null,

	@SerializedName("Image")
	val image: String? = null
)

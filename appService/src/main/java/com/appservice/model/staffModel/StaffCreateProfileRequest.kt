package com.appservice.model.staffModel

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class StaffCreateProfileRequest(
  @field:SerializedName("Image", alternate = ["image"])
  var image: StaffImage? = null,

  @field:SerializedName("IsAvailable", alternate = ["isAvailable"])
  var isAvailable: Boolean? = null,

  @field:SerializedName("ServiceIds", alternate = ["serviceIds"])
  var serviceIds: ArrayList<String>? = null,

  @field:SerializedName("Gender", alternate = ["gender"])
  var gender: String? = null,

  @field:SerializedName("FloatingPointTag", alternate = ["floatingPointTag"])
  var floatingPointTag: String? = null,

  @field:SerializedName("Name", alternate = ["name"])
  var name: String? = null,

  @field:SerializedName("Description", alternate = ["description"])
  var description: String? = null,

  @field:SerializedName("Experience", alternate = ["experience"])
  var experience: Int? = null,

  @field:SerializedName("Age", alternate = ["sge"])
  var age: Int? = null,

  @field:SerializedName("Specialisations", alternate = ["specialisations"])
  var specialisations: List<SpecialisationsItem?>? = null,
) : Serializable, BaseResponse()


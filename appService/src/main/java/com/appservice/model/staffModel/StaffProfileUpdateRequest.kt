package com.appservice.model.staffModel

import com.framework.base.BaseRequest
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class StaffProfileUpdateRequest(
  @field:SerializedName("isAvailable")
  var isAvailable: Boolean? = null,

  @field:SerializedName("serviceIds")
  var serviceIds: ArrayList<String>? = null,

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
  var specialisations: ArrayList<SpecialisationsItem>? = null,

  @field:SerializedName("Memberships")
  var memberships: String? = null,

  @field:SerializedName("BusinessLicence")
  var businessLicence: String? = null,


  @field:SerializedName("Speciality")
  var speciality: String? = null,

  @field:SerializedName("BookingWindow")
  var bookingWindow: Int? = null,


  @field:SerializedName("AppointmentType")
  var appointmentType: Int? = null,


  @field:SerializedName("Education")
  var education: String? = null,

  @field:SerializedName("Registration")
  var registration: String? = null,


  @field:SerializedName("ContactNumber")
  var contactNumber: String? = null
) : BaseRequest(), Serializable



package com.appservice.staffs.model

import com.google.gson.annotations.SerializedName

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
  val memberships: String? = null,

  @field:SerializedName("BusinessLicence")
  val businessLicence: String? = null,


  @field:SerializedName("Speciality")
  val speciality: String? = null,

  @field:SerializedName("BookingWindow")
  val bookingWindow: String? = null,


  @field:SerializedName("AppointmentType")
  val appointmentType: String? = null,


  @field:SerializedName("Education")
  val education: String? = null,

  @field:SerializedName("Registration")
  val registration: String? = null,


  @field:SerializedName("ContactNumber")
  val contactNumber: String? = null
)



package com.appservice.model.staffModel

import com.appservice.model.staffModel.SpecialisationsItem
import com.appservice.ui.catalog.common.AppointmentModel
import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class StaffDetailsResponse(
  @field:SerializedName("StatusCode")
  var statusCode: Int? = null,
  @field:SerializedName("Result")
  var result: StaffDetailsResult? = null,
) : Serializable, BaseResponse()


data class StaffDetailsResult(
  @field:SerializedName("Timings")
  var timings: ArrayList<AppointmentModel>? = null,

  @field:SerializedName("ServiceIds")
  var serviceIds: ArrayList<String>? = null,

  @field:SerializedName("Memberships")
  var memberships: String? = null,

  @field:SerializedName("BusinessLicence")
  var businessLicence: String? = null,

  @field:SerializedName("Description")
  var description: String? = null,

  @field:SerializedName("Speciality")
  var speciality: String? = null,

  @field:SerializedName("BookingWindow")
  var bookingWindow: Int? = null,

  @field:SerializedName("Gender")
  var gender: String? = null,

  @field:SerializedName("Image")
  var image: String? = null,

  @field:SerializedName("Name")
  var name: String? = null,

  @field:SerializedName("Experience")
  var experience: Double? = null,

  @field:SerializedName("TileImageUrl")
  var tileImageUrl: String? = null,

  @field:SerializedName("AppointmentType")
  var appointmentType: Int? = null,

  @field:SerializedName("Specialisations")
  var specialisations: ArrayList<SpecialisationsItem>? = null,

  @field:SerializedName("Education")
  var education: String? = null,

  @field:SerializedName("Registration")
  var registration: String? = null,

  @field:SerializedName("IsAvailable")
  var isAvailable: Boolean? = null,

  @field:SerializedName("Signature")
  var signature: String? = null,

  @field:SerializedName("ContactNumber")
  var contactNumber: String? = null,

  @field:SerializedName("_id")
  var id: String? = null,

  @field:SerializedName("Age")
  var age: Int? = null
) : Serializable {

  fun getAge(): String {
    return if (age == null || age == 0) "" else age.toString()
  }

  fun getExperienceN(): String {
    return if (experience == null) "" else if (experience!! > 5) "5+ years" else "$experience ${if (experience!! > 1) "years" else "year"}"
  }

  fun getExperienceValue(): Int {
    val experience = experience?.toInt() ?: 0
    return if (experience > 5) 6 else experience
  }

  fun getBookingWindowN(): String {
    return if (bookingWindow ?: 0 == 0) "0 day" else "$bookingWindow days"
  }

  fun getGenderAndAge(): String {
    return if (gender.isNullOrEmpty() || gender == "null") getAge() else "$gender, ${getAge()}"
  }

  fun getUrlImage(): String {
    return if (image.equals("null").not() && image.isNullOrEmpty().not()) image!! else ""
  }

  fun getUrlTileImageUrl(): String {
    return if (tileImageUrl.equals("null").not() && tileImageUrl.isNullOrEmpty().not()) tileImageUrl!! else ""
  }

  fun getUrlSignature(): String {
    return if (signature.equals("null").not() && signature.isNullOrEmpty().not()) signature!! else ""
  }
}

class AppointmentType {
  companion object {
    val typeMap = mapOf(1 to "Video consultation", 0 to "In-person consultation", 2 to "In-person & video consultation")
  }
}
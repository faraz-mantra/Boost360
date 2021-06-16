package com.appservice.staffs.model

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
    var memberships: Any? = null,

    @field:SerializedName("BusinessLicence")
    var businessLicence: Any? = null,

    @field:SerializedName("Description")
    var description: String? = null,

    @field:SerializedName("Speciality")
    var speciality: Any? = null,

    @field:SerializedName("BookingWindow")
    var bookingWindow: Double? = null,

    @field:SerializedName("Gender")
    var gender: String? = null,

    @field:SerializedName("Image")
    var image: String? = null,

    @field:SerializedName("Name")
    var name: String? = null,

    @field:SerializedName("Experience")
    var experience: Int? = null,

    @field:SerializedName("TileImageUrl")
    var tileImageUrl: String? = null,

    @field:SerializedName("AppointmentType")
    var appointmentType: Int? = null,

    @field:SerializedName("Specialisations")
    var specialisations: ArrayList<SpecialisationsItem>? = null,

    @field:SerializedName("Education")
    var education: Any? = null,

    @field:SerializedName("Registration")
    var registration: Any? = null,

    @field:SerializedName("IsAvailable")
    var isAvailable: Boolean? = null,

    @field:SerializedName("Signature")
    var signature: Any? = null,

    @field:SerializedName("ContactNumber")
    var contactNumber: Any? = null,

    @field:SerializedName("_id")
    var id: String? = null,

    @field:SerializedName("Age")
    var age: Int? = null
) : Serializable

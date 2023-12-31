package com.inventoryorder.model.doctorsData

import com.framework.utils.*
import com.google.gson.annotations.SerializedName
import java.io.Serializable

const val DOCTOR_STAFF_LIST_DATA="DOCTOR_STAFF_LIST_DATA"

data class GetStaffListingResponse(

	@field:SerializedName("Error")
	var error: Any? = null,

	@field:SerializedName("StatusCode")
	var statusCode: Int? = null,

	@field:SerializedName("Result")
	var result: Result? = null
):Serializable

data class Result(

	@field:SerializedName("Paging")
	var paging: Paging? = null,
	@field:SerializedName("Data")
	var data: ArrayList<DataItem>? = null
):Serializable

fun List<DataItem>.saveDoctorList() {
	PreferencesUtils.instance.saveData(DOCTOR_STAFF_LIST_DATA, convertListObjToString(this ?: ArrayList()) ?: "")
}

fun getDoctorStaffList(): List<DataItem>? {
	return convertStringToList(PreferencesUtils.instance.getData(DOCTOR_STAFF_LIST_DATA, "") ?: "")
}


data class DataItem(

	@field:SerializedName("Timings")
	var timings: List<TimingsItem?>? = null,

	@field:SerializedName("ServiceIds")
	var serviceIds: List<String>? = null,

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
	var specialisations: List<SpecialisationsItem?>? = null,

	@field:SerializedName("Education")
	var education: String? = null,

	@field:SerializedName("Registration")
	var registration: String? = null,

	@field:SerializedName("IsAvailable")
	var isAvailable: Boolean? = null,

	@field:SerializedName("Signature")
	var signature: Any? = null,

	@field:SerializedName("ContactNumber")
	var contactNumber: String? = null,

	@field:SerializedName("_id")
	var id: String? = null,

	@field:SerializedName("Age")
	var age: Int? = null
):Serializable

data class TimingsItem(

	@field:SerializedName("Timing")
	var timing: List<TimingItem?>? = null,

	@field:SerializedName("Day")
	var day: String? = null
):Serializable

data class TimingItem(

	@field:SerializedName("From")
	var from: String? = null,

	@field:SerializedName("To")
	var to: String? = null
):Serializable

data class Paging(

	@field:SerializedName("Skip")
	var skip: Int? = null,

	@field:SerializedName("Limit")
	var limit: Int? = null,

	@field:SerializedName("Count")
	var count: Int? = null
):Serializable

data class SpecialisationsItem(

	@field:SerializedName("Position")
	var position: String? = null,

	@field:SerializedName("Graduated")
	var graduated: String? = null
):Serializable

package com.inventoryorder.model.orderRequest

import com.framework.utils.DateUtils
import com.google.gson.annotations.SerializedName
import com.inventoryorder.model.spaAppointment.bookingslot.request.AppointmentRequestModel
import java.io.Serializable

data class ExtraProperties(
    @SerializedName("businessLicense")
    val businessLicense: String = "",
    @SerializedName("patientName")
    val patientName: String = "",
    @SerializedName("doctorSpeciality")
    val doctorSpeciality: String = "",
    @SerializedName("patientMobileNumber")
    val patientMobileNumber: String = "",
    @SerializedName("consultationFor")
    val consultationFor: String = "",
    @SerializedName("gender")
    val gender: String = "",
    @SerializedName("patientEmailId")
    val patientEmailId: String = "",
    @SerializedName("scheduledDateTime")
    val scheduledDateTime: String = "",
    @SerializedName("businessLogo")
    val businessLogo: String = "",
    @SerializedName("doctorQualification")
    val doctorQualification: String = "",
    @SerializedName("doctorSignature")
    val doctorSignature: String = "",
    @SerializedName("referenceId")
    val referenceId: String = "",
    @SerializedName("duration")
    val duration: Int = 0,
    @SerializedName("doctorName")
    val doctorName: String = "",
    @SerializedName("doctorId")
    val doctorId: String = "",
    @SerializedName("startTime")
    val startTime: String = "",
    @SerializedName("endTime")
    val endTime: String = "",
    @SerializedName("age")
    val age: String = "",
    @SerializedName("Appointment")
    var appointment : ArrayList<AppointmentRequestModel> ?= null
) : Serializable {

  fun startTime(): String {
    return startTime ?: ""
  }

  fun endTime(): String {
    return endTime ?: ""
  }

  fun getScheduledDateN(): String? {
    var dateString = DateUtils.parseDate(scheduledDateTime, DateUtils.FORMAT_SERVER_DATE, DateUtils.FORMAT_YYYY_MM_DD)
    if (dateString.isNullOrEmpty()) dateString = DateUtils.parseDate(scheduledDateTime, DateUtils.FORMAT_SERVER_1_DATE, DateUtils.FORMAT_YYYY_MM_DD)
    return dateString ?: ""
  }
}
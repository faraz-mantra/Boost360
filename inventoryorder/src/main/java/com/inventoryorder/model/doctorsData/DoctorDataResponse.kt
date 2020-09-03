package com.inventoryorder.model.doctorsData

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DoctorDataResponse(
    @SerializedName("businessLicense")
    val businessLicense: String = "",
    @SerializedName("WebsiteId")
    val websiteId: String = "",
    @SerializedName("bookingwindow")
    val bookingwindow: String = "",
    @SerializedName("mobile")
    val mobile: String = "",
    @SerializedName("services")
    val services: List<String>?,
    @SerializedName("experience")
    val experience: String = "",
    @SerializedName("profileimage")
    val profileimage: Profileimage,
    @SerializedName("degrees")
    val degrees: String = "",
    @SerializedName("memberships")
    val memberships: String = "",
    @SerializedName("duration")
    val duration: String = "",
    @SerializedName("IsArchived")
    val isArchived: Boolean = false,
    @SerializedName("speciality")
    val speciality: String = "",
    @SerializedName("ActionId")
    val actionId: String = "",
    @SerializedName("awards")
    val awards: String = "",
    @SerializedName("UserId")
    val userId: String = "",
    @SerializedName("timings")
    val timings: String = "",
    @SerializedName("UpdatedOn")
    val updatedOn: String = "",
    @SerializedName("registration")
    val registration: String = "",
    @SerializedName("_id")
    val Id: String = "",
    @SerializedName("CreatedOn")
    val createdOn: String = "",
    @SerializedName("doctorsignature")
    val doctorsignature: Doctorsignature,
    @SerializedName("appointmenttype")
    val appointmenttype: String = "",
    @SerializedName("username")
    val username: String = ""
):BaseResponse(),Serializable
package com.appservice.model.staffModel

import com.appservice.ui.catalog.common.AppointmentModel
import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class StaffDetailsResponse(
    @field:SerializedName("StatusCode")
    val statusCode: Int? = null,
    @field:SerializedName("Result")
    val result: StaffDetailsResult? = null,
) : Serializable, BaseResponse()


data class StaffDetailsResult(

    @field:SerializedName("Timings")
    var timings: ArrayList<AppointmentModel>? = null,

    @field:SerializedName("ServiceIds")
    var serviceIds: ArrayList<String>? = null,

    @field:SerializedName("Experience")
    var experience: Int? = null,

    @field:SerializedName("TileImageUrl")
    val tileImageUrl: String? = null,

    @field:SerializedName("Description")
    val description: String? = null,

    @field:SerializedName("Specialisations")
    val specialisations: ArrayList<SpecialisationsItem>? = null,

    @field:SerializedName("IsAvailable")
    var isAvailable: Boolean? = null,

    @field:SerializedName("_id")
    val id: String? = null,

    @field:SerializedName("Gender")
    val gender: String? = null,

    @field:SerializedName("Image")
    val image: String? = null,

    @field:SerializedName("Age")
    val age: Int? = null,

    @field:SerializedName("Name")
    val name: String? = null,
) : Serializable{

    fun getSpecialization():String{
       return if (specialisations.isNullOrEmpty().not()) specialisations?.first()?.value!! else ""
    }
}

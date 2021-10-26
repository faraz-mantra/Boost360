package com.appservice.model.staffModel

import com.appservice.constant.RecyclerViewItemType
import com.appservice.model.staffModel.SpecialisationsItem
import com.appservice.recyclerView.AppBaseRecyclerViewItem
import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GetStaffListingResponse(

    @field:SerializedName("StatusCode")
    var statusCode: Int? = null,

    @field:SerializedName("Result")
    var result: Result? = null,
) : Serializable, BaseResponse()


data class Result(
    @field:SerializedName("Paging")
    var paging: Paging? = null,
    @field:SerializedName("Data")
    var data: ArrayList<DataItem>? = null,
) : Serializable, BaseResponse()

data class DataItem(
    @field:SerializedName("Timings")
    var timings: Any? = null,
    @field:SerializedName("ServiceIds")
    var serviceIds: List<String?>? = null,
    @field:SerializedName("Experience")
    var experience: Double? = null,
    @field:SerializedName("TileImageUrl")
    var tileImageUrl: Any? = null,
    @field:SerializedName("Description")
    var description: String? = null,
    @field:SerializedName("Specialisations")
    var specialisations: ArrayList<SpecialisationsItem>? = null,
    @field:SerializedName("IsAvailable")
    var isAvailable: Boolean? = null,
    @field:SerializedName("_id")
    var id: String? = null,
    @field:SerializedName("Image")
    var image: Any? = null,
    @field:SerializedName("Name")
    var name: String? = null,
    @field:SerializedName("Memberships")
    var memberships: Any? = null,
    @field:SerializedName("BusinessLicence")
    var businessLicence: Any? = null,
    @field:SerializedName("Speciality")
    var speciality: Any? = null,
    @field:SerializedName("BookingWindow")
    var bookingWindow: Double? = null,
    @field:SerializedName("Gender")
    var gender: String? = null,
    @field:SerializedName("AppointmentType")
    var appointmentType: Int? = null,
    @field:SerializedName("Education")
    var education: Any? = null,
    @field:SerializedName("Registration")
    var registration: Any? = null,
    @field:SerializedName("Signature")
    var signature: Any? = null,
    @field:SerializedName("ContactNumber")
    var contactNumber: Any? = null,
    @field:SerializedName("Age")
    var age: Int? = null
) : Serializable, BaseResponse(), AppBaseRecyclerViewItem {

  var recyclerViewItem = RecyclerViewItemType.STAFF_LISTING_VIEW.getLayout()

  override fun getViewType(): Int {
    return recyclerViewItem
  }

  fun getLoaderItem(): DataItem {
    this.recyclerViewItem = RecyclerViewItemType.PAGINATION_LOADER.getLayout()
    return this
  }
}

data class Paging(
    @field:SerializedName("Skip")
    var skip: Int? = null,
    @field:SerializedName("Limit")
    var limit: Int? = null,
    @field:SerializedName("Count")
    var count: Int? = null,
) : Serializable, BaseResponse()


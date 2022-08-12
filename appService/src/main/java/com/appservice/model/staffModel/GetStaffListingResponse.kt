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
  var tileImageUrl: String? = null,
  @field:SerializedName("Description")
  var description: String? = null,
  @field:SerializedName("Specialisations")
  var specialisations: ArrayList<SpecialisationsItem>? = null,
  @field:SerializedName("IsAvailable")
  var isAvailable: Boolean? = null,
  @field:SerializedName("_id")
  var id: String? = null,
  @field:SerializedName("Image")
  var image: String? = null,
  @field:SerializedName("Name")
  var name: String? = null,
  @field:SerializedName("Memberships")
  var memberships: Any? = null,
  @field:SerializedName("BusinessLicence")
  var businessLicence: String? = null,
  @field:SerializedName("Speciality")
  var speciality: String? = null,
  @field:SerializedName("BookingWindow")
  var bookingWindow: Double? = null,
  @field:SerializedName("Gender")
  var gender: String? = null,
  @field:SerializedName("AppointmentType")
  var appointmentType: Int? = null,
  @field:SerializedName("Education")
  var education: String? = null,
  @field:SerializedName("Registration")
  var registration: String? = null,
  @field:SerializedName("Signature")
  var signature: String? = null,
  @field:SerializedName("ContactNumber")
  var contactNumber: String? = null,
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

  fun getUrlImage(): String {
    return if (image.equals("null").not() && image.isNullOrEmpty().not()) image!! else ""
  }
  fun getUrlTileImageUrl(): String {
    return if (tileImageUrl.equals("null").not() && tileImageUrl.isNullOrEmpty().not()) tileImageUrl!! else ""
  }

  fun getUrlSignature(): String {
    return if (signature!="null" && signature.isNullOrEmpty().not()) signature!! else ""
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


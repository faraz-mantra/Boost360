package dev.patrickgold.florisboard.customization.model.response.staff

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import dev.patrickgold.florisboard.customization.adapter.BaseRecyclerItem
import dev.patrickgold.florisboard.customization.adapter.FeaturesEnum
import java.io.Serializable

data class StaffListingResponse(

    @field:SerializedName("StatusCode")
    val statusCode: Int? = null,
    @field:SerializedName("Result")
    val result: StaffResult? = null,
) : Serializable, BaseResponse()


data class StaffResult(
    @field:SerializedName("Paging")
    val paging: Paging? = null,
    @field:SerializedName("Data")
    val data: ArrayList<DataItem>? = null,
) : Serializable

data class DataItem(
    @field:SerializedName("Timings")
    val timings: Any? = null,
    @field:SerializedName("ServiceIds")
    val serviceIds: List<String?>? = null,
    @field:SerializedName("Experience")
    val experience: Double? = null,
    @field:SerializedName("TileImageUrl")
    val tileImageUrl: Any? = null,
    @field:SerializedName("Description")
    val description: String? = null,
    @field:SerializedName("Specialisations")
    val specialisations: ArrayList<SpecialisationsItem>? = null,
    @field:SerializedName("IsAvailable")
    val isAvailable: Boolean? = null,
    @field:SerializedName("_id")
    val id: String? = null,
    @field:SerializedName("Image")
    val image: String? = null,
    @field:SerializedName("Name")
    val name: String? = null,
) : Serializable, BaseRecyclerItem() {

  var recyclerViewItem = FeaturesEnum.STAFF_LISTING_VIEW.ordinal

  override fun getViewType(): Int {
    return recyclerViewItem
  }

  fun getLoaderItem(): DataItem {
    this.recyclerViewItem = FeaturesEnum.LOADER.ordinal
    return this
  }
}

data class Paging(
    @field:SerializedName("Skip")
    val skip: Int? = null,
    @field:SerializedName("Limit")
    val limit: Int? = null,
    @field:SerializedName("Count")
    val count: Int? = null,
) : Serializable


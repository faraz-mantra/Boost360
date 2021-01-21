package com.appservice.staffs.model

import com.appservice.R
import com.appservice.recyclerView.AppBaseRecyclerViewItem
import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GetStaffListingResponse(

        @field:SerializedName("StatusCode")
        val statusCode: Int? = null,

        @field:SerializedName("Result")
        val result: Result? = null
): Serializable, BaseResponse()


data class Result(

        @field:SerializedName("Paging")
        val paging: Paging? = null,

        @field:SerializedName("Data")
        val data: List<DataItem?>? = null
): Serializable, BaseResponse()

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
        val specialisations: List<SpecialisationsItem>? = null,

        @field:SerializedName("IsAvailable")
        val isAvailable: Boolean? = null,

        @field:SerializedName("_id")
        val id: String? = null,

        @field:SerializedName("Image")
        val image: Any? = null,

        @field:SerializedName("Name")
        val name: String? = null
): Serializable, BaseResponse(), AppBaseRecyclerViewItem {
        override fun getViewType(): Int {
                return R.layout.recycler_item_staff_listing
        }

}

data class Paging(

        @field:SerializedName("Skip")
        val skip: Int? = null,

        @field:SerializedName("Limit")
        val limit: Int? = null,

        @field:SerializedName("Count")
        val count: Int? = null
): Serializable, BaseResponse()

data class SpecialisationsItem(

        @field:SerializedName("Value")
        val value: String? = null,

        @field:SerializedName("Key")
        val key: String? = null
) : Serializable, BaseResponse() {
}

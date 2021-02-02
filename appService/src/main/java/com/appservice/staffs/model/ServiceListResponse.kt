package com.appservice.staffs.model

import com.appservice.R
import com.appservice.constant.RecyclerViewItemType
import com.appservice.recyclerView.AppBaseRecyclerViewItem
import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ServiceListResponse(

		@field:SerializedName("StatusCode")
		val statusCode: Int? = null,

		@field:SerializedName("Result")
		val result: ResultService? = null
) : BaseResponse(), AppBaseRecyclerViewItem {
    override fun getViewType(): Int {
        return R.layout.recycler_item_service
    }
}

data class ResultService(

		@field:SerializedName("Paging")
		val paging: PagingService? = null,

		@field:SerializedName("Data")
		val data: List<DataItemService?>? = null
)

data class PagingService(

		@field:SerializedName("Skip")
		val skip: Int? = null,

		@field:SerializedName("Limit")
		val limit: Int? = null,

		@field:SerializedName("Count")
		val count: Int? = null
)

data class DataItemService(
		var isChecked: Boolean? = null,
		@field:SerializedName("Type")
		val type: Any? = null,

		@field:SerializedName("Category")
		val category: String? = null,

		@field:SerializedName("SecondaryTileImages")
		val secondaryTileImages: List<Any?>? = null,

		@field:SerializedName("Price")
		val price: Double? = null,

		@field:SerializedName("DiscountedPrice")
		val discountedPrice: Double? = null,

		@field:SerializedName("Duration")
		val duration: Int? = null,

		@field:SerializedName("_id")
		val id: String? = null,

		@field:SerializedName("Image")
		val image: String? = null,

		@field:SerializedName("SecondaryImages")
		val secondaryImages: List<Any?>? = null,

		@field:SerializedName("DiscountAmount")
		val discountAmount: Double? = null,

		@field:SerializedName("Name")
		val name: String? = null,

		@field:SerializedName("TileImage")
		val tileImage: String? = null,
		var recyclerViewItem: Int = RecyclerViewItemType.SERVICE_ITEM_VIEW.getLayout()
) : AppBaseRecyclerViewItem,Serializable {
    override fun getViewType(): Int {
        return recyclerViewItem
    }
}

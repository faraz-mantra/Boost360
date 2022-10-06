package com.appservice.model.updateBusiness.pastupdates

import com.appservice.constant.RecyclerViewItemType
import com.appservice.recyclerView.AppBaseRecyclerViewItem
import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PastUpdatesNewListingResponse(

	@field:SerializedName("imageCount")
	val imageCount: Int? = null,

	@field:SerializedName("floats")
	val floats: List<PastPostItem>? = null,

	@field:SerializedName("postCount")
	val postCount: Int? = null,

	@field:SerializedName("textCount")
	val textCount: Int? = null,

	@field:SerializedName("totalCount")
	val totalCount: Int? = null,

	@field:SerializedName("moreFloatsAvailable")
	val moreFloatsAvailable: Boolean? = null

) : BaseResponse()

data class Extradetails(

	@field:SerializedName("socialparameter")
	val socialparameter: String? = null
)

data class PastPostItem(

	@field:SerializedName("imageUri")
	val imageUri: String? = null,

	@field:SerializedName("groupMessageId")
	val groupMessageId: Any? = null,

	@field:SerializedName("extradetails")
	val extradetails: Extradetails? = null,

	@field:SerializedName("isHtmlString")
	val isHtmlString: Boolean? = null,

	@field:SerializedName("tileImageUri")
	val tileImageUri: String? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("createdOn")
	val createdOn: String? = null,

	@field:SerializedName("url")
	val url: String? = null,

	@field:SerializedName("htmlString")
	val htmlString: Any? = null,

	@field:SerializedName("tags")
	val tags: List<String>? = null

) : AppBaseRecyclerViewItem {

	var category:PastPromotionalCategoryModel?=null

	override fun getViewType(): Int {
		return RecyclerViewItemType.PAST_UPDATE_ITEM.getLayout()
	}
}

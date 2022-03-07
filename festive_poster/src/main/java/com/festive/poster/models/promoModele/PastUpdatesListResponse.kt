package com.festive.poster.models.promoModele

import com.google.gson.annotations.SerializedName

data class PastUpdatesListResponse(

	@field:SerializedName("floats")
	val floats: List<FloatsItem?>? = null,

	@field:SerializedName("totalCount")
	val totalCount: Int? = null,

	@field:SerializedName("moreFloatsAvailable")
	val moreFloatsAvailable: Boolean? = null
)

data class FloatsItem(

	@field:SerializedName("imageUri")
	val imageUri: Any? = null,

	@field:SerializedName("groupMessageId")
	val groupMessageId: Any? = null,

	@field:SerializedName("extradetails")
	val extradetails: Any? = null,

	@field:SerializedName("isHtmlString")
	val isHtmlString: Boolean? = null,

	@field:SerializedName("tileImageUri")
	val tileImageUri: Any? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("type")
	val type: Any? = null,

	@field:SerializedName("createdOn")
	val createdOn: String? = null,

	@field:SerializedName("url")
	val url: String? = null,

	@field:SerializedName("htmlString")
	val htmlString: Any? = null,

	@field:SerializedName("tags")
	val tags: Any? = null
)

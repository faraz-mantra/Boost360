package com.dashboard.model

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class OwnersDataResponse(

	@field:SerializedName("Extra")
	var extra: Extra? = null,

	@field:SerializedName("Data")
	var data: List<DataItem?>? = null
):BaseResponse(),Serializable

data class Extra(

	@field:SerializedName("TotalCount")
	var totalCount: Int? = null,

	@field:SerializedName("PageSize")
	var pageSize: Int? = null,

	@field:SerializedName("CurrentIndex")
	var currentIndex: Int? = null
)


data class DataItem(

	@field:SerializedName("WebsiteId")
	var websiteId: String? = null,

	@field:SerializedName("IsArchived")
	var isArchived: Boolean? = null,

	@field:SerializedName("ActionId")
	var actionId: String? = null,

	@field:SerializedName("ourStory")
	var ourStory: String? = null,

	@field:SerializedName("UserId")
	var userId: String? = null,

	@field:SerializedName("name")
	var name: String? = null,

	@field:SerializedName("UpdatedOn")
	var updatedOn: String? = null,

	@field:SerializedName("_id")
	var id: String? = null,

	@field:SerializedName("CreatedOn")
	var createdOn: String? = null,

	@field:SerializedName("title")
	var title: String? = null,

	@field:SerializedName("profileimage")
	var profileimage: Profileimage? = null
)

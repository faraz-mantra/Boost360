package com.dashboard.model

import com.google.gson.annotations.SerializedName

data class RequestAddOwnersInfo(

	@field:SerializedName("WebsiteId")
	var websiteId: String? = null,

	@field:SerializedName("ActionData")
	var actionData: ActionData? = null
)

data class ActionData(

	@field:SerializedName("ourStory")
	var ourStory: String? = null,

	@field:SerializedName("name")
	var name: String? = null,

	@field:SerializedName("title")
	var title: String? = null,

	@field:SerializedName("profileimage")
	var profileimage: Profileimage? = null
)

data class Profileimage(

	@field:SerializedName("description")
	var description: String? = null,

	@field:SerializedName("url")
	var url: String? = null
)

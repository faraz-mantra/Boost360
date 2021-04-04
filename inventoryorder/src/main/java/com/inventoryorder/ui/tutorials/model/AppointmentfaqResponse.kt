package com.inventoryorder.ui.tutorials.model

import com.google.gson.annotations.SerializedName

data class AppointmentfaqResponse(

		@field:SerializedName("Contents")
		val contents: List<ContentsItem?>? = null,

		@field:SerializedName("Fragment Data")
		val fragmentData: FragmentData? = null,
)


data class ContentsItem(

		@field:SerializedName("question")
		val question: String? = null,

		@field:SerializedName("answer")
		val answer: String? = null,
)

package com.inventoryorder.ui.tutorials.model

import com.google.gson.annotations.SerializedName

data class AppointmentMgmtTutorialVideoResponse(

		@field:SerializedName("Contents")
		val contents: Contents? = null,

		@field:SerializedName("Fragment Data")
		val fragmentData: FragmentData? = null,
)


data class TutorialContentsItem(

		@field:SerializedName("video url")
		val videoUrl: String? = null,

		@field:SerializedName("video title")
		val videoTitle: String? = null,
)

data class AllTutorialsItem(

		@field:SerializedName("video url")
		val videoUrl: String? = null,

		@field:SerializedName("video title")
		val videoTitle: String? = null,
)

data class Menu(

		@field:SerializedName("All tutorials")
		val allTutorials: List<AllTutorialsItem?>? = null,

		@field:SerializedName("Tutorial contents")
		val tutorialContents: List<TutorialContentsItem?>? = null,
)

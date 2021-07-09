package com.inventoryorder.ui.tutorials.model

import com.google.gson.annotations.SerializedName

data class VideoDescAppointmentMgmt(

		@field:SerializedName("Fragment Data")
		val fragmentData: FragmentData? = null,

		@field:SerializedName("Content Video")
		val contentVideo: ContentVideo? = null,
)


data class ContentVideo(

		@field:SerializedName("video url")
		val videoUrl: String? = null,

		@field:SerializedName("Heading")
		val heading: String? = null,

		@field:SerializedName("Tutorial contents")
		val tutorialContents: List<String?>? = null,

		@field:SerializedName("video title")
		val videoTitle: String? = null,
		@field:SerializedName("video length")
		var videoLength: String? = null,
		@field:SerializedName("video thumbnails")
		var videoThumbnails: String? = null,
)

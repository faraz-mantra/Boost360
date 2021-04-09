package com.inventoryorder.ui.tutorials.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LearnAboutAppointmentMgmt(

        @field:SerializedName("Contents")
        var contents: Contents? = null,

        @field:SerializedName("Tips:")
        var tips: List<String?>? = null,

        @field:SerializedName("Menus")
        var menus: List<String?>? = null,

        @field:SerializedName("Fragment Data")
        var fragmentData: FragmentData? = null,
)

data class FragmentData(

        @field:SerializedName("subject")
        var subject: String? = null,

        @field:SerializedName("title")
        var title: String? = null,
)

data class VIDEOSItem(

        @field:SerializedName("video url")
        var videoUrl: String? = null,

        @field:SerializedName("video title")
        var videoTitle: String? = null,
        @field:SerializedName("video thumbnails")
        var videoThumbnails: String? = null,
        @field:SerializedName("video length")
        var videoLength: String? = null,
) : Serializable

data class Contents(

        @field:SerializedName("Description")
        var description: String? = null,

        @field:SerializedName("Title")
        var title: String? = null,

        @field:SerializedName("VIDEOS")
        var videos: List<VIDEOSItem?>? = null,
)

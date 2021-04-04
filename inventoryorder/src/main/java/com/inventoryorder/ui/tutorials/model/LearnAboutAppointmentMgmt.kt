package com.inventoryorder.ui.tutorials.model

import com.google.gson.annotations.SerializedName

data class LearnAboutAppointmentMgmt(

        @field:SerializedName("Contents")
        val contents: Contents? = null,

        @field:SerializedName("Tips:")
        val tips: List<String?>? = null,

        @field:SerializedName("Menus")
        val menus: List<String?>? = null,

        @field:SerializedName("Fragment Data")
        val fragmentData: FragmentData? = null,
)

data class FragmentData(

        @field:SerializedName("subject")
        val subject: String? = null,

        @field:SerializedName("title")
        val title: String? = null,
)

data class VIDEOSItem(

        @field:SerializedName("video url")
        val videoUrl: String? = null,

        @field:SerializedName("video title")
        val videoTitle: String? = null,
)

data class Contents(

        @field:SerializedName("Description")
        val description: String? = null,

        @field:SerializedName("Title")
        val title: String? = null,

        @field:SerializedName("VIDEOS")
        val vIDEOS: List<VIDEOSItem?>? = null,
)

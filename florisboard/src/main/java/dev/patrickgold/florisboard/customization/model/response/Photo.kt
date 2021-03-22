package dev.patrickgold.florisboard.customization.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Photo(
        @SerializedName("ImageURL")
        @Expose
        var imageUri: String? = null,

        @SerializedName("selected")
        @Expose
        var selected: Boolean = false
)
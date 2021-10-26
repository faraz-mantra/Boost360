package dev.patrickgold.florisboard.customization.model.response.staff

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SpecialisationsItem(
    @field:SerializedName("Value")
    var value: String? = null,
    @field:SerializedName("Key")
    var key: String? = null,
) : Serializable
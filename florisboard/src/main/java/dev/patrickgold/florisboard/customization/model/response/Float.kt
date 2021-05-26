package dev.patrickgold.florisboard.customization.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import dev.patrickgold.florisboard.customization.adapter.BaseRecyclerItem
import dev.patrickgold.florisboard.customization.adapter.FeaturesEnum
import java.util.*

data class Float(
        @SerializedName("_id")
        @Expose
        var id: String? = null,

        @SerializedName("createdOn")
        @Expose
        var createdOn: Date? = null,

        @SerializedName("htmlString")
        @Expose
        var htmlString: String? = null,

        @SerializedName("imageUri")
        @Expose
        var imageUri: String? = null,

        @SerializedName("isHtmlString")
        @Expose
        private var isHtmlString: Boolean? = null,

        @SerializedName("keywords")
        @Expose
        var keywords: List<String>? = null,

        @SerializedName("message")
        @Expose
        var message: String? = null,

        @SerializedName("messageIndex")
        @Expose
        var messageIndex: Int? = null,

        @SerializedName("tileImageUri")
        @Expose
        var tileImageUri: Any? = null,

        @SerializedName("type")
        @Expose
        var type: Any? = null,

        @SerializedName("url")
        @Expose
        var url: String? = null) : BaseRecyclerItem() {

        override fun getViewType(): Int = FeaturesEnum.UPDATES.ordinal
}
data class dateFloat(
        @SerializedName("Date")
        var date:Long?=null
)
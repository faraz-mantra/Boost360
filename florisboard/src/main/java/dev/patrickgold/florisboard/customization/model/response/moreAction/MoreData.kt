package dev.patrickgold.florisboard.customization.model.response.moreAction

import androidx.annotation.DrawableRes
import com.google.gson.annotations.SerializedName
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.customization.adapter.BaseRecyclerItem
import dev.patrickgold.florisboard.customization.adapter.FeaturesEnum
import java.io.Serializable

data class MoreData(
  @SerializedName("items")
  var items: ArrayList<ActionItem>? = null,
  @SerializedName("title")
  var title: String? = null,
  @SerializedName("type")
  var type: String? = null
) : Serializable, BaseRecyclerItem() {

  enum class MoreActionData(@DrawableRes var icon: Int) {
    QUICK_ACTION_ID(R.drawable.ic_quick_action),
    CUSTOMER_POLICIES_ID(R.drawable.ic_customer_policies),
    CUSTOMER_INTERACTION_ID(R.drawable.ic_website_content),
    WEBSITE_CONTENT_ID(R.drawable.ic_website_content);

    companion object {
      fun fromType(type: String?): MoreActionData? = values().firstOrNull { it.name.equals(type, true) }
    }
  }

  fun getAllMoreItemType(): ArrayList<String> {
    val list = ArrayList<String>()
    items?.forEach { list.add(it.type ?: "") }
    return list
  }

  override fun getViewType(): Int {
    return FeaturesEnum.MORE_ACTION_VIEW_ITEM.ordinal
  }
}
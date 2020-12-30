package com.dashboard.model.live.customerItem

import com.dashboard.R
import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.recyclerView.AppBaseRecyclerViewItem
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class CustomerActionItem(
    @SerializedName("title")
    var title: String? = null,
    @SerializedName("type")
    var type: String? = null,
    @SerializedName("isLock")
    var isLock: Boolean? = null,
    @SerializedName("premiumCode")
    var premiumCode: String? = null
) : Serializable, AppBaseRecyclerViewItem {

  var recyclerViewItemType: Int = RecyclerViewItemType.BOOST_CUSTOMER_ITEM_VIEW.getLayout()
  override fun getViewType(): Int {
    return recyclerViewItemType
  }

  enum class IconType(var icon: Int) {
    in_clinic_appointments(R.drawable.ic_all_orders_d),
    video_consultations(R.drawable.ic_consult_d),
    patient_customer_calls(R.drawable.ic_nav_calls_d),
    patient_customer_messages(R.drawable.ic_nav_enquiries_d),
    newsletter_subscribers(R.drawable.ic_nav_inbox_d),
    customer_orders(R.drawable.ic_all_orders_d);

    companion object {
      fun fromName(name: String): IconType? = values().firstOrNull { it.name.toLowerCase(Locale.ROOT) == name.toLowerCase(Locale.ROOT) }
    }
  }
}
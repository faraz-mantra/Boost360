package com.dashboard.model.live.customerItem

import com.dashboard.R
import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.recyclerView.AppBaseRecyclerViewItem
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CustomerActionItem(
    @SerializedName("title")
    var title: String? = null,
    @SerializedName("type")
    var type: String? = null,
    @SerializedName("isLock")
    var isLock: Boolean? = null,
    @SerializedName("premiumCode")
    var premiumCode: String? = null,
    @SerializedName("orderCount")
    var orderCount: String? = null,
    @SerializedName("consultCount")
    var consultCount: String? = null,
    @SerializedName("customerCalls")
    var customerCalls: String? = null,
    @SerializedName("messageCount")
    var messageCount: String? = null,
    @SerializedName("subscriptionCount")
    var subscriptionCount: String? = null,
) : Serializable, AppBaseRecyclerViewItem {

  var recyclerViewItemType: Int = RecyclerViewItemType.BOOST_CUSTOMER_ITEM_VIEW.getLayout()
  override fun getViewType(): Int {
    return recyclerViewItemType
  }

  fun getCountValue(): String? {
    return when (CustomerActionItem.IconType.fromName(type)) {
      CustomerActionItem.IconType.customer_orders,
      CustomerActionItem.IconType.in_clinic_appointments,
      -> orderCount
      CustomerActionItem.IconType.video_consultations -> consultCount
      CustomerActionItem.IconType.patient_customer_calls -> customerCalls
      CustomerActionItem.IconType.patient_customer_messages -> messageCount
      CustomerActionItem.IconType.newsletter_subscribers -> subscriptionCount
      else -> ""
    }
  }

  enum class IconType(var icon: Int) {
    in_clinic_appointments(R.drawable.in_clinic_appointments),
    video_consultations(R.drawable.video_consultations),
    patient_customer_calls(R.drawable.ic_customer_call_d),
    patient_customer_messages(R.drawable.ic_customer_enquiries_d),
    newsletter_subscribers(R.drawable.newsletter_subscription),
    customer_orders(R.drawable.in_clinic_appointments);

    companion object {
      fun fromName(name: String?): IconType? = values().firstOrNull { it.name.equals(name, ignoreCase = true) }
    }
  }
}
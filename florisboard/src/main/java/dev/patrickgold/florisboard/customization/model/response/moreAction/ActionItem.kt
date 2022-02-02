package dev.patrickgold.florisboard.customization.model.response.moreAction

import androidx.annotation.DrawableRes
import com.google.gson.annotations.SerializedName
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.customization.adapter.BaseRecyclerItem
import dev.patrickgold.florisboard.customization.adapter.FeaturesEnum
import java.io.Serializable

data class ActionItem(
  @SerializedName("title")
  var title: String? = null,
  @SerializedName("type")
  var type: String? = null
) : Serializable, BaseRecyclerItem() {

  enum class ActionData(@DrawableRes var icon: Int?, @DrawableRes var arrowIcon: Int) {
    BOOK_ORDER_ID(R.drawable.ic_order_f, R.drawable.ic_arrow_right_f),
    BOOK_CLINIC_APPOINTMENT_ID(R.drawable.ic_appointment_f, R.drawable.ic_arrow_right_f),
    BOOK_VIDEO_CONSULTATION_ID(R.drawable.ic_video_consultation_f, R.drawable.ic_arrow_right_f),
    ADD_TESTIMONIAL_ID(R.drawable.ic_testimonial_f, R.drawable.ic_arrow_right_f),
    REFUND_CANCELLATION_ID(R.drawable.ic_eye_f, R.drawable.ic_sharenetwork_f),
    ALL_ORDERS_ID(null, R.drawable.ic_arrow_up_right_f),
    IN_CLINIC_APPOINTMENT_ID(null, R.drawable.ic_arrow_up_right_f),
    VIDEO_CONSULTATION_ID(null, R.drawable.ic_arrow_up_right_f),
    CUSTOMER_MESSAGE_ID(null, R.drawable.ic_arrow_up_right_f),
    CUSTOMER_CALLS_ID(null, R.drawable.ic_arrow_up_right_f),
    SERVICES_CATALOG_ID(null, R.drawable.ic_arrow_up_right_f),
    UPDATES_TIPS_ID(null, R.drawable.ic_arrow_up_right_f),
    STAFF_PROFILES_ID(null, R.drawable.ic_arrow_up_right_f),
    ALL_IMAGES_ID(null, R.drawable.ic_arrow_up_right_f),
    CUSTOMER_TESTIMONIALS_ID(null, R.drawable.ic_arrow_up_right_f),
    CUSTOM_PAGES_ID(null, R.drawable.ic_arrow_up_right_f),
    BUSINESS_TIMINGS_ID(null, R.drawable.ic_arrow_up_right_f),
    BUSINESS_ADDRESS_ID(null, R.drawable.ic_arrow_up_right_f),
    CONTACT_DETAILS_ID(null, R.drawable.ic_arrow_up_right_f),
    BASIC_BUSINESS_ID(null, R.drawable.ic_arrow_up_right_f);

    companion object {
      fun fromType(type: String?): ActionData? = values().firstOrNull { it.name.equals(type, true) }
    }
  }

  override fun getViewType(): Int {
    return if (type == ActionData.REFUND_CANCELLATION_ID.name) FeaturesEnum.MORE_SECOND_VIEW_ITEM.ordinal else FeaturesEnum.MORE_FIRST_VIEW_ITEM.ordinal
  }
}
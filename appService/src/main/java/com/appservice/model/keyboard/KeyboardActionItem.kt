package com.appservice.model.keyboard

import com.appservice.AppServiceApplication
import com.appservice.R
import com.appservice.constant.RecyclerViewItemType
import com.appservice.recyclerView.AppBaseRecyclerViewItem
import com.framework.pref.UserSessionManager
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class KeyboardActionItem(
  @SerializedName("description")
  var description: String? = null,
  @SerializedName("isEnabled")
  var isEnabled: Boolean? = null,
  @SerializedName("premiumCode")
  var premiumCode: String? = null,
  @SerializedName("title")
  var title: String? = null,
  @SerializedName("type")
  var type: String? = null
) : Serializable, AppBaseRecyclerViewItem {

  fun isPremium(): Boolean {
    val session = UserSessionManager(AppServiceApplication.instance)
    return session.getStoreWidgets()?.contains(premiumCode) ?: false
  }

  fun isPremiumKeyAvailable(): Boolean {
    return premiumCode.isNullOrEmpty().not()
  }

  fun getColorActive(): Int {
    return if (isEnabled == false || (isPremiumKeyAvailable() && isPremium().not())) R.color.light_grey_bcbcbc else R.color.black_4a4a4a
  }

  fun getEyeActive(): Int {
    return if (isEnabled == false || (isPremiumKeyAvailable() && isPremium().not())) R.drawable.ic_eye_inactive else R.drawable.ic_eye_active
  }

  enum class ActionTypeId {
    service_product_id, updates_id, photos_id, business_card_id, staff_listing_id, more_id
  }

  override fun getViewType(): Int {
    return RecyclerViewItemType.KEYBOARD_TAB_ITEM.getLayout()
  }
}
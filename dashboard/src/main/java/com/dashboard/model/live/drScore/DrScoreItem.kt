package com.dashboard.model.live.drScore

import com.dashboard.R
import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.recyclerView.AppBaseRecyclerViewItem
import java.io.Serializable

class DrScoreItem(
  var drScoreUiData: DrScoreUiData? = null,
  var isUpdate: Boolean = false,
  var sortChar: Int = 0,
) : AppBaseRecyclerViewItem, Serializable, Comparable<Any?> {


  override fun getViewType(): Int {
    return RecyclerViewItemType.ITEMS_CONTENT_SETUP_ITEM_VIEW.getLayout()
  }

  override fun compareTo(other: Any?): Int {
    val cVal = (other as? DrScoreItem)?.sortChar ?: 0
    return cVal - sortChar
  }

  fun getIcon(): Int {
    return if (isUpdate) R.drawable.ic_ok_11_d else R.drawable.ic_alert_error
  }

  enum class DrScoreItemType(var priority:Int) {
    boolean_add_business_name(1),
    boolean_add_business_description(3),
    boolean_add_clinic_logo(4),
    boolean_add_featured_image_video(5),
    boolean_select_what_you_sell(10),
    boolean_add_business_hours(10),
    boolean_add_contact_details(2),
    boolean_add_custom_domain_name_and_ssl(6),
    number_updates_posted(10),
    boolean_social_channel_connected(10),
    number_services_added(10),
    number_products_added(10),
    boolean_create_staff(10),
    boolean_add_bank_account(10),
    boolean_catalog_setup(10),
    boolean_general_appointments(10),
    boolean_business_verification(10),
    boolean_image_uploaded_to_gallery(10),
    boolean_create_custom_page(10),
    boolean_share_business_card(10),
    boolean_create_doctor_e_profile(10),
    boolean_create_sample_in_clinic_appointment(10),
    boolean_create_sample_video_consultation(10),
    boolean_manage_appointment_settings(10),
    boolean_respond_to_customer_enquiries(10);

    companion object {
      fun fromName(name: String?): DrScoreItemType? =
        values().firstOrNull { it.name.equals(name, ignoreCase = true) }
    }
  }
}
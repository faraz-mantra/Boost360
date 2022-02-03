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

  enum class DrScoreItemType {
    boolean_add_business_name,
    boolean_add_business_description,
    boolean_add_clinic_logo,
    boolean_add_featured_image_video,
    boolean_select_what_you_sell,
    boolean_add_business_hours,
    boolean_add_contact_details,
    boolean_add_custom_domain_name_and_ssl,
    number_updates_posted,
    boolean_social_channel_connected,
    number_services_added,
    number_products_added,
    boolean_create_staff,
    boolean_add_bank_account,
    boolean_catalog_setup,
    boolean_general_appointments,
    boolean_business_verification,
    boolean_image_uploaded_to_gallery,
    boolean_create_custom_page,
    boolean_share_business_card,
    boolean_create_doctor_e_profile,
    boolean_create_sample_in_clinic_appointment,
    boolean_create_sample_video_consultation,
    boolean_manage_appointment_settings,
    boolean_respond_to_customer_enquiries;

    companion object {
      fun fromName(name: String?): DrScoreItemType? =
        values().firstOrNull { it.name.equals(name, ignoreCase = true) }
    }
  }
}
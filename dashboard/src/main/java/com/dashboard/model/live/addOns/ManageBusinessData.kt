package com.dashboard.model.live.addOns

import com.dashboard.R
import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.recyclerView.AppBaseRecyclerViewItem
import com.framework.base.BaseResponse

class ManageBusinessData(
    var title: String? = null,
    var iconType: String? = null,
    var isLock: Boolean = false,
) : BaseResponse(), AppBaseRecyclerViewItem {

  var recyclerViewItemType: Int = RecyclerViewItemType.MANAGE_BUSINESS_ITEM_VIEW.getLayout()

  override fun getViewType(): Int {
    return recyclerViewItemType
  }

  fun getData(): ArrayList<ManageBusinessData> {
    val list = ArrayList<ManageBusinessData>()
    list.add(ManageBusinessData(title = "Projects and Teams", iconType = "ic_project_terms_d", isLock = true))
    list.add(ManageBusinessData(title = "Unlimited Digital Brochures", iconType = "ic_digital_brochures_d"))
    list.add(ManageBusinessData(title = "Customer Calls", iconType = "ic_customer_call_d"))
    list.add(ManageBusinessData(title = "Customer Enquiries", iconType = "ic_customer_enquiries_d"))
    list.add(ManageBusinessData(title = "Daily Business Updates", iconType = "ic_daily_business_update_d"))
    list.add(ManageBusinessData(title = "Products Catalogue", iconType = "ic_product_cataloge_d"))
    list.add(ManageBusinessData(title = "Customer Testimonials", iconType = "ic_customer_testimonial_d"))
    list.add(ManageBusinessData(title = "Business Keyboard", iconType = "ic_business_keyboard_d", isLock = true))
    return list
  }


  enum class IconType(var type: String, var icon: Int) {
    ic_project_terms_d("ic_project_terms_d", R.drawable.ic_project_terms_d),
    ic_digital_brochures_d("ic_digital_brochures_d", R.drawable.ic_digital_brochures_d),
    ic_customer_call_d("ic_customer_call_d", R.drawable.ic_customer_call_d),
    ic_customer_enquiries_d("ic_customer_enquiries_d", R.drawable.ic_customer_enquiries_d),
    ic_daily_business_update_d("ic_daily_business_update_d", R.drawable.ic_daily_business_update_d),
    ic_product_cataloge_d("ic_product_cataloge_d", R.drawable.ic_product_cataloge_d),
    ic_customer_testimonial_d("ic_customer_testimonial_d", R.drawable.ic_customer_testimonial_d),
    ic_business_keyboard_d("ic_business_keyboard_d", R.drawable.ic_business_keyboard_d),
    clinic_logo("clinic_logo", R.drawable.clinic_logo),
    advertising_google_fb("advertising_google_fb", R.drawable.advertising_google_fb),
    appointment_settings("appointment_settings", R.drawable.appointment_settings),
    assisted_content_creation("assisted_content_creation", R.drawable.assisted_content_creation),
    autamated_seo_d("autamated_seo_d", R.drawable.autamated_seo_d),
    boost_data_security("boost_data_security", R.drawable.boost_data_security),
    boost_payment_gateway("boost_payment_gateway", R.drawable.boost_payment_gateway),
    business_hours("business_hours", R.drawable.business_hours),
    contact_details("contact_details", R.drawable.contact_details),
    content_sync_acros_channels("content_sync_acros_channels", R.drawable.content_sync_acros_channels),
    ic_custom_page_add("ic_custom_page_add", R.drawable.ic_custom_page_add),
    custom_payment_gateway("custom_payment_gateway", R.drawable.custom_payment_gateway),
    doctor_profile("doctor_profile", R.drawable.doctor_profile),
    domain_name_ssl("domain_name_ssl", R.drawable.domain_name_ssl),
    e_commerce_website("e_commerce_website", R.drawable.e_commerce_website),
    facebook_lead_ads("facebook_lead_ads", R.drawable.facebook_lead_ads),
    facebook_likebox_plugin("facebook_likebox_plugin", R.drawable.facebook_likebox_plugin),
    in_clinic_appointments("in_clinic_appointments", R.drawable.in_clinic_appointments),
    my_email_accounts("my_email_accounts", R.drawable.my_email_accounts),
    newsletter_subscription("newsletter_subscription", R.drawable.newsletter_subscription),
    picture_gallery("picture_gallery", R.drawable.picture_gallery),
    premium_boost_support("premium_boost_support", R.drawable.premium_boost_support),
    ria_digital_assistant("ria_digital_assistant", R.drawable.ria_digital_assistant),
    unlimited_content_updates("unlimited_content_updates", R.drawable.unlimited_content_updates),
    unlimited_website_bandwidth("unlimited_website_bandwidth", R.drawable.unlimited_website_bandwidth),
    video_consultations("video_consultations", R.drawable.video_consultations),
    website_chatbot("website_chatbot", R.drawable.website_chatbot),
    website_social_share_plugin("website_social_share_plugin", R.drawable.website_social_share_plugin);

    companion object {
      fun fromType(type: String?): IconType? = values().firstOrNull { it.type == type }
    }
  }
}
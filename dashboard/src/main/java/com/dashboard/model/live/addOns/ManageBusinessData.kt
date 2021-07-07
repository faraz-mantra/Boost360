package com.dashboard.model.live.addOns

import com.dashboard.R
import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.recyclerView.AppBaseRecyclerViewItem
import com.framework.base.BaseResponse
import com.framework.utils.*
import com.google.gson.annotations.SerializedName
import java.io.Serializable

const val LAST_SEEN_DATA = "LAST_SEEN_DATA"

class ManageBusinessData(
  var title: String? = null,
  var businessType: String? = null,
  var premiumCode: String? = null,
  var isLock: Boolean = false,
  var isHide: Boolean = false,
) : BaseResponse(), AppBaseRecyclerViewItem, Serializable {

  override fun getViewType(): Int {
    return RecyclerViewItemType.MANAGE_BUSINESS_ITEM_VIEW.getLayout()
  }


  enum class BusinessType(var type: String, var icon: Int) {
    video_consultations("video_consultations", R.drawable.video_consultations),
    in_clinic_appointments("in_clinic_appointments", R.drawable.in_clinic_appointments),
    ic_customer_call_d("ic_customer_call_d", R.drawable.ic_customer_call_d),
    ic_customer_enquiries_d("ic_customer_enquiries_d", R.drawable.ic_customer_enquiries_d),
    ic_service_cataloge_d("ic_service_cataloge_d", R.drawable.ic_product_cataloge_d),
    doctor_profile("doctor_profile", R.drawable.doctor_profile),
    appointment_settings("appointment_settings", R.drawable.appointment_settings),
    ic_daily_business_update_d("ic_daily_business_update_d", R.drawable.ic_daily_business_update_d),
    picture_gallery("picture_gallery", R.drawable.picture_gallery),
    ic_custom_page_add("ic_custom_page_add", R.drawable.ic_custom_page_add),
    ic_customer_testimonial_d("ic_customer_testimonial_d", R.drawable.ic_customer_testimonial_d),
    unlimited_content_updates("unlimited_content_updates", R.drawable.unlimited_content_updates),
    website_social_share_plugin(
      "website_social_share_plugin",
      R.drawable.website_social_share_plugin
    ),
    autamated_seo_d("autamated_seo_d", R.drawable.autamated_seo_d),
    unlimited_website_bandwidth(
      "unlimited_website_bandwidth",
      R.drawable.unlimited_website_bandwidth
    ),
    clinic_basic_info("clinic_basic_info", R.drawable.ic_business_info_n),
    clinic_logo("clinic_logo", R.drawable.clinic_logo),
    featured_image_video("featured_image_video", R.drawable.picture_gallery),
    business_hours("business_hours", R.drawable.business_hours),
    contact_details("contact_details", R.drawable.contact_details),
    business_kyc_verification("business_kyc_verification", R.drawable.boost_payment_gateway),
    boost_payment_gateway("boost_payment_gateway", R.drawable.boost_payment_gateway),
    domain_name_ssl("domain_name_ssl", R.drawable.domain_name_ssl),
    my_email_accounts("my_email_accounts", R.drawable.my_email_accounts),
    boost_data_security("boost_data_security", R.drawable.boost_data_security),
    my_bank_account("my_bank_account", R.drawable.my_bank_account),
    custom_payment_gateway("custom_payment_gateway", R.drawable.custom_payment_gateway),
    e_commerce_website("e_commerce_website", R.drawable.e_commerce_website),
    content_sync_acros_channels(
      "content_sync_acros_channels",
      R.drawable.content_sync_acros_channels
    ),
    facebook_likebox_plugin("facebook_likebox_plugin", R.drawable.facebook_likebox_plugin),
    ic_customer_call_tracker_d("ic_customer_call_tracker_d", R.drawable.ic_customer_call_d),
    ic_ivr_faculty("ic_ivr_faculty", R.drawable.ic_tracker_ivr_d),
    ic_business_keyboard_d("ic_business_keyboard_d", R.drawable.ic_business_keyboard_d),
    newsletter_subscription("newsletter_subscription", R.drawable.newsletter_subscription),
    website_chatbot("website_chatbot", R.drawable.website_chatbot),
    assisted_content_creation("assisted_content_creation", R.drawable.assisted_content_creation),
    advertising_google_fb("advertising_google_fb", R.drawable.advertising_google_fb),
    facebook_lead_ads("facebook_lead_ads", R.drawable.facebook_lead_ads),
    website_visits_visitors("website_visits_visitors", R.drawable.website_visits_visitors_d),
    social_sharing_analytics("social_sharing_analytics", R.drawable.social_sharing_analytics),
    sales_analytics("sales_analytics", R.drawable.sales_analytics),
    search_analytics("search_analytics", R.drawable.search_analytics),
    chatbot_analytics("chatbot_analytics", R.drawable.ic_chatbot_v2_d),
    ria_digital_assistant("ria_digital_assistant", R.drawable.ria_digital_assistant),
    premium_boost_support("premium_boost_support", R.drawable.premium_boost_support),
    ic_digital_brochures("ic_digital_brochures", R.drawable.ic_digital_brochures_d),
    ic_product_cataloge_d("ic_product_cataloge_d", R.drawable.ic_product_cataloge_d),
    project_portfolio_d("project_portfolio_d", R.drawable.ic_project_teams_d),
    team_page_d("team_page_d", R.drawable.ic_project_teams_d),
    ic_my_business_faqs("ic_my_business_faqs", R.drawable.ic_faqs_d),
    membership_plans("membership_plans", R.drawable.ic_memberships_d),
    upcoming_batches_d("upcoming_batches_d", R.drawable.ic_upcoming_batch_d),
    toppers_institute_d("toppers_institute_d", R.drawable.toppers_institute_d),
    business_name_d("business_name_d", R.drawable.ic_business_info_n),
    restaurant_story_d("restaurant_story_d", R.drawable.restaurant_story),
    faculty_profiles_d("faculty_profiles_d", R.drawable.faculty_profiles),
    client_logos_d("client_logos_d", R.drawable.client_logos),
    customer_order_d("customer_order_d", R.drawable.in_clinic_appointments),
    customer_booking_d("customer_booking_d", R.drawable.customer_booking_d),
    sync_otas_channel_manager_d("sync_otas_channel_manager_d", 0),
    places_look_around_d("places_look_around_d", R.drawable.places_look_around_d),
    trip_advisor_reviews_d("trip_advisor_reviews_d", R.drawable.trip_advisor_reviews_d),
    room_booking_engine_d("room_booking_engine_d", R.drawable.room_inventory_d),
    table_reservations_d("table_reservations_d", R.drawable.table_reservations_d),
    ic_staff_profile_d("ic_staff_profile_d", R.drawable.ic_staff_profile_d);

    //R.drawable.ic_project_terms_d

    companion object {
      fun fromName(name: String?): BusinessType? = values().firstOrNull { it.name == name }
    }
  }

  fun getLastSeenData(): ArrayList<ManageBusinessData> {
    return ArrayList(
      convertStringToList(
        PreferencesUtils.instance.getData(LAST_SEEN_DATA, "") ?: ""
      ) ?: ArrayList()
    )
  }

  fun saveLastSeenData(data: ManageBusinessData) {
    val addOnsLast = getLastSeenData()
    val matchData = addOnsLast.firstOrNull { it.businessType == data.businessType }
    matchData?.let { addOnsLast.remove(matchData) }
    addOnsLast.add(0, data)
    if (addOnsLast.size > 8) addOnsLast.removeLast()
    PreferencesUtils.instance.saveData(LAST_SEEN_DATA, convertListObjToString(addOnsLast) ?: "")
  }

}

data class ManageBusinessDataResponse(
  var data: ArrayList<ManageActionData>? = null,
) : BaseResponse(), Serializable

data class ManageActionData(
  @SerializedName("action_item")
  var actionItem: ArrayList<ManageBusinessData>? = null,
  @SerializedName("type")
  var type: String? = null,
) : Serializable

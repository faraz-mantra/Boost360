package com.framework.firebaseUtils.firestore

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Metricdetail(
  var testName: String = "Metricdetail"
) : Serializable {

  @SerializedName("boolean_add_business_name")
  var boolean_add_business_name: Boolean? = null
    set(value) {
      field = value
      currentValueUpdate = CurrentValueUpdate(key = "boolean_add_business_name", value = value)
    }

  @SerializedName("boolean_add_business_description")
  var boolean_add_business_description: Boolean? = null
    set(value) {
      field = value
      currentValueUpdate =
        CurrentValueUpdate(key = "boolean_add_business_description", value = value)
    }

  @SerializedName("boolean_add_clinic_logo")
  var boolean_add_clinic_logo: Boolean? = null
    set(value) {
      field = value
      currentValueUpdate = CurrentValueUpdate(key = "boolean_add_clinic_logo", value = value)
    }

  @SerializedName("boolean_add_featured_image_video")
  var boolean_add_featured_image_video: Boolean? = null
    set(value) {
      field = value
      currentValueUpdate =
        CurrentValueUpdate(key = "boolean_add_featured_image_video", value = value)
    }

  @SerializedName("boolean_select_what_you_sell")
  var boolean_select_what_you_sell: Boolean? = null
    set(value) {
      field = value
      currentValueUpdate = CurrentValueUpdate(key = "boolean_select_what_you_sell", value = value)
    }

  @SerializedName("boolean_add_business_hours")
  var boolean_add_business_hours: Boolean? = null
    set(value) {
      field = value
      currentValueUpdate = CurrentValueUpdate(key = "boolean_add_business_hours", value = value)
    }

  @SerializedName("boolean_add_contact_details")
  var boolean_add_contact_details: Boolean? = null
    set(value) {
      field = value
      currentValueUpdate = CurrentValueUpdate(key = "boolean_add_contact_details", value = value)
    }

  @SerializedName("boolean_add_custom_domain_name_and_ssl")
  var boolean_add_custom_domain_name_and_ssl: Boolean? = null
    set(value) {
      field = value
      currentValueUpdate =
        CurrentValueUpdate(key = "boolean_add_custom_domain_name_and_ssl", value = value)
    }

  @SerializedName("number_updates_posted")
  var number_updates_posted: Int? = null
    set(value) {
      field = value
      currentValueUpdate = CurrentValueUpdate(key = "number_updates_posted", value = value)
    }

  @SerializedName("boolean_social_channel_connected")
  var boolean_social_channel_connected: Boolean? = null
    set(value) {
      field = value
      currentValueUpdate =
        CurrentValueUpdate(key = "boolean_social_channel_connected", value = value)
    }

  @SerializedName("boolean_create_staff")
  var boolean_create_staff: Boolean? = null
    set(value) {
      field = value
      currentValueUpdate = CurrentValueUpdate(key = "boolean_create_staff", value = value)
    }

  @SerializedName("number_services_added")
  var number_services_added: Int? = null
    set(value) {
      field = value
      currentValueUpdate = CurrentValueUpdate(key = "number_services_added", value = value)
    }

  @SerializedName("number_products_added")
  var number_products_added: Int? = null
    set(value) {
      field = value
      currentValueUpdate = CurrentValueUpdate(key = "number_products_added", value = value)
    }

  @SerializedName("boolean_add_bank_account")
  var boolean_add_bank_account: Boolean? = null
    set(value) {
      field = value
      currentValueUpdate = CurrentValueUpdate(key = "boolean_add_bank_account", value = value)
    }

  @SerializedName("boolean_catalog_setup")
  var boolean_catalog_setup: Boolean? = null
    set(value) {
      field = value
      currentValueUpdate = CurrentValueUpdate(key = "boolean_catalog_setup", value = value)
    }

  @SerializedName("boolean_general_appointments")
  var boolean_general_appointments: Boolean? = null
    set(value) {
      field = value
      currentValueUpdate = CurrentValueUpdate(key = "boolean_general_appointments", value = value)
    }

  @SerializedName("boolean_business_verification")
  var boolean_business_verification: Boolean? = null
    set(value) {
      field = value
      currentValueUpdate = CurrentValueUpdate(key = "boolean_business_verification", value = value)
    }

  @SerializedName("boolean_image_uploaded_to_gallery")
  var boolean_image_uploaded_to_gallery: Boolean? = null
    set(value) {
      field = value
      currentValueUpdate =
        CurrentValueUpdate(key = "boolean_image_uploaded_to_gallery", value = value)
    }

  @SerializedName("boolean_create_custom_page")
  var boolean_create_custom_page: Boolean? = null
    set(value) {
      field = value
      currentValueUpdate = CurrentValueUpdate(key = "boolean_create_custom_page", value = value)
    }

  @SerializedName("boolean_share_business_card")
  var boolean_share_business_card: Boolean? = null
    set(value) {
      field = value
      currentValueUpdate = CurrentValueUpdate(key = "boolean_share_business_card", value = value)
    }

  @SerializedName("boolean_create_doctor_e_profile")
  var boolean_create_doctor_e_profile: Boolean? = null
    set(value) {
      field = value
      currentValueUpdate =
        CurrentValueUpdate(key = "boolean_create_doctor_e_profile", value = value)
    }

  @SerializedName("boolean_create_sample_in_clinic_appointment")
  var boolean_create_sample_in_clinic_appointment: Boolean? = null
    set(value) {
      field = value
      currentValueUpdate =
        CurrentValueUpdate(key = "boolean_create_sample_in_clinic_appointment", value = value)
    }

  @SerializedName("boolean_create_sample_video_consultation")
  var boolean_create_sample_video_consultation: Boolean? = null
    set(value) {
      field = value
      currentValueUpdate =
        CurrentValueUpdate(key = "boolean_create_sample_video_consultation", value = value)
    }

  @SerializedName("boolean_manage_appointment_settings")
  var boolean_manage_appointment_settings: Boolean? = null
    set(value) {
      field = value
      currentValueUpdate =
        CurrentValueUpdate(key = "boolean_manage_appointment_settings", value = value)
    }

  @SerializedName("boolean_respond_to_customer_enquiries")
  var boolean_respond_to_customer_enquiries: Boolean? = null
    set(value) {
      field = value
      currentValueUpdate =
        CurrentValueUpdate(key = "boolean_respond_to_customer_enquiries", value = value)
    }

  var currentValueUpdate: CurrentValueUpdate? = null
}

class CurrentValueUpdate(
  @SerializedName("Key")
  var key: String? = null,
  @SerializedName("value")
  var value: Any? = null
)
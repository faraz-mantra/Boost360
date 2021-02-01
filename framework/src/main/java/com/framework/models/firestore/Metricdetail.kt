package com.framework.models.firestore;
import com.google.gson.annotations.SerializedName

data class Metricdetail (

		@SerializedName("boolean_add_business_name") val boolean_add_business_name : Boolean,
		@SerializedName("boolean_add_business_description") val boolean_add_business_description : Boolean,
		@SerializedName("boolean_add_clinic_logo") val boolean_add_clinic_logo : Boolean,
		@SerializedName("boolean_add_featured_image_video") val boolean_add_featured_image_video : Boolean,
		@SerializedName("boolean_select_what_you_sell") val boolean_select_what_you_sell : Boolean,
		@SerializedName("boolean_add_business_hours") val boolean_add_business_hours : Boolean,
		@SerializedName("boolean_add_contact_details") val boolean_add_contact_details : Boolean,
		@SerializedName("boolean_add_custom_domain_name_and_ssl") val boolean_add_custom_domain_name_and_ssl : Boolean,
		@SerializedName("number_updates_posted") val number_updates_posted : Int,
		@SerializedName("boolean_social_channel_connected") val boolean_social_channel_connected : Boolean,
		@SerializedName("number_services_added") val number_services_added : Int,
		@SerializedName("number_products_added") val number_products_added : Int,
		@SerializedName("boolean_add_bank_account") val boolean_add_bank_account : Boolean,
		@SerializedName("boolean_image_uploaded_to_gallery") val boolean_image_uploaded_to_gallery : Boolean,
		@SerializedName("boolean_create_custom_page") val boolean_create_custom_page : Boolean,
		@SerializedName("boolean_share_business_card") val boolean_share_business_card : Boolean,
		@SerializedName("boolean_create_doctor_e_profile") val boolean_create_doctor_e_profile : Boolean,
		@SerializedName("boolean_create_sample_in_clinic_appointment") val boolean_create_sample_in_clinic_appointment : Boolean,
		@SerializedName("boolean_create_sample_video_consultation") val boolean_create_sample_video_consultation : Boolean,
		@SerializedName("boolean_manage_appointment_settings") val boolean_manage_appointment_settings : Boolean,
		@SerializedName("boolean_respond_to_customer_enquiries") val boolean_respond_to_customer_enquiries : Boolean
)
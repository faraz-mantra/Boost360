package com.framework.models.firestore;
import com.google.gson.annotations.SerializedName

data class Metricdetail (

		@SerializedName("boolean_add_business_name") var boolean_add_business_name : Boolean,
		@SerializedName("boolean_add_business_description") var boolean_add_business_description : Boolean,
		@SerializedName("boolean_add_clinic_logo") var boolean_add_clinic_logo : Boolean,
		@SerializedName("boolean_add_featured_image_video") var boolean_add_featured_image_video : Boolean,
		@SerializedName("boolean_select_what_you_sell") var boolean_select_what_you_sell : Boolean,
		@SerializedName("boolean_add_business_hours") var boolean_add_business_hours : Boolean,
		@SerializedName("boolean_add_contact_details") var boolean_add_contact_details : Boolean,
		@SerializedName("boolean_add_custom_domain_name_and_ssl") var boolean_add_custom_domain_name_and_ssl : Boolean,
		@SerializedName("number_updates_posted") var number_updates_posted : Int,
		@SerializedName("boolean_social_channel_connected") var boolean_social_channel_connected : Boolean,
		@SerializedName("boolean_create_staff") var boolean_create_staff : Boolean,
		@SerializedName("number_services_added") var number_services_added : Int,
		@SerializedName("number_products_added") var number_products_added : Int,
		@SerializedName("boolean_add_bank_account") var boolean_add_bank_account : Boolean,
		@SerializedName("boolean_image_uploaded_to_gallery") var boolean_image_uploaded_to_gallery : Boolean,
		@SerializedName("boolean_create_custom_page") var boolean_create_custom_page : Boolean,
		@SerializedName("boolean_share_business_card") var boolean_share_business_card : Boolean,
		@SerializedName("boolean_create_doctor_e_profile") var boolean_create_doctor_e_profile : Boolean,
		@SerializedName("boolean_create_sample_in_clinic_appointment") var boolean_create_sample_in_clinic_appointment : Boolean,
		@SerializedName("boolean_create_sample_video_consultation") var boolean_create_sample_video_consultation : Boolean,
		@SerializedName("boolean_manage_appointment_settings") var boolean_manage_appointment_settings : Boolean,
		@SerializedName("boolean_respond_to_customer_enquiries") var boolean_respond_to_customer_enquiries : Boolean
)
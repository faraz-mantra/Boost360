package com.framework.models.firestore;
import com.google.gson.annotations.SerializedName



data class Events (

		@SerializedName("boolean_add_business_name") val boolean_add_business_name : Boolean_add_business_name,
		@SerializedName("boolean_add_business_description") val boolean_add_business_description : Boolean_add_business_description,
		@SerializedName("number_services_added") val number_services_added : Number_services_added,
		@SerializedName("boolean_create_sample_video_consultation") val boolean_create_sample_video_consultation : Boolean_create_sample_video_consultation
)
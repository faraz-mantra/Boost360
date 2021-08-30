package com.appservice.appointment.model

import com.google.gson.annotations.SerializedName

data class UpdateInfoRequest(

	@field:SerializedName("Timings")
	var timings: String? = null,

	@field:SerializedName("pincode")
	var pincode: String? = null,

	@field:SerializedName("Description")
	var description: String? = null,

	@field:SerializedName("Email")
	var email: String? = null,

	@field:SerializedName("Category")
	var category: String? = null,

	@field:SerializedName("Address")
	var address: String? = null,

	@field:SerializedName("__RequestVerificationToken")
	var requestVerificationToken: String? = null,

	@field:SerializedName("city")
	var city: String? = null,

	@field:SerializedName("GEOLOCATION")
	var gEOLOCATION: String? = null,

	@field:SerializedName("URL")
	var uRL: String? = null,

	@field:SerializedName("productCategoryVerb")
	var productCategoryVerb: String? = null,

	@field:SerializedName("Name")
	var name: String? = null,

	@field:SerializedName("isVMN")
	var isVMN: Boolean? = null,

	@field:SerializedName("Contacts")
	var contacts: List<String?>? = null,

	@field:SerializedName("Tag")
	var tag: String? = null,

	@field:SerializedName("FB")
	var fB: String? = null
)

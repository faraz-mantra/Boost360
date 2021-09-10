package com.appservice.appointment.model

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GetWareHouseResponse(

		@field:SerializedName("StatusCode")
		var statusCode: Int? = null,

		@field:SerializedName("Result")
		var result: Result? = null,
) : BaseResponse(), Serializable

data class DataItem(

		@field:SerializedName("FullAddress")
		var fullAddress: String? = null,

		@field:SerializedName("IsPickupAllowed")
		var isPickupAllowed: String? = null,

		@field:SerializedName("Country")
		var country: String? = null,

		@field:SerializedName("ContactNumber")
		var contactNumber: String? = null,

		@field:SerializedName("City")
		var city: String? = null,

		@field:SerializedName("Name")
		var name: String? = null,

		@field:SerializedName("PinCode")
		var pinCode: String? = null,

		@field:SerializedName("Location")
		var location: Location? = null,
)

data class Result(

		@field:SerializedName("Paging")
		var paging: Paging? = null,

		@field:SerializedName("Data")
		var data: List<DataItem?>? = null,
)


data class Location(

		@field:SerializedName("Latitude")
		var latitude: String? = null,

		@field:SerializedName("Longitude")
		var longitude: String? = null,
)


data class Paging(

		@field:SerializedName("Skip")
		var skip: String? = null,

		@field:SerializedName("Limit")
		var limit: String? = null,

		@field:SerializedName("Count")
		var count: String? = null,
)

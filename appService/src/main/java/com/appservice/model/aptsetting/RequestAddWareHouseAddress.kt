package com.appservice.model.aptsetting

import com.google.gson.annotations.SerializedName

data class RequestAddWareHouseAddress(

        @field:SerializedName("FullAddress")
        var fullAddress: String? = null,

        @field:SerializedName("IsPickupAllowed")
        var isPickupAllowed: String? = null,

        @field:SerializedName("ClientId")
        var clientId: String? = null,

        @field:SerializedName("Country")
        var country: String? = null,

        @field:SerializedName("ContactNumber")
        var contactNumber: String? = null,

        @field:SerializedName("City")
        var city: String? = null,

        @field:SerializedName("FloatingPointId")
        var floatingPointId: String? = null,

        @field:SerializedName("Name")
        var name: String? = null,

        @field:SerializedName("PinCode")
        var pinCode: String? = null,

        @field:SerializedName("Location")
        var location: WarehouseLocation? = null,
)

data class WarehouseLocation(

        @field:SerializedName("Latitude")
        var latitude: String? = null,

        @field:SerializedName("Longitude")
        var longitude: String? = null,
)

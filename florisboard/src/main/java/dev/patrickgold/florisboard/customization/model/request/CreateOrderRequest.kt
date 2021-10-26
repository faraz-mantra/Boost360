package dev.patrickgold.florisboard.customization.model.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CreateOrderRequest(
        @SerializedName("ProductId")
        @Expose
        var productId: String? = null,

        @SerializedName("ExpiresOn")
        @Expose
        var expiresOn: String? = null,

        @SerializedName("Quantity")
        @Expose
        var quantity: Int = 0,

        @SerializedName("SalePrice")
        @Expose
        var salePrice: Double = 0.0,

        @SerializedName("MaxQuantityPerOrder")
        @Expose
        var maxQuantityPerOrder: Int = 0,

        @SerializedName("Seller")
        @Expose
        var sellers: Seller? = null)

data class Seller(
        @SerializedName("Identifier")
        @Expose
        var identifier: String? = null,

        @SerializedName("ContactDetails")
        @Expose
        var contactDetails: ContactDetails? = null,

        @SerializedName("Address")
        @Expose
        var addresses: Address? = null
)

data class Address(
        @SerializedName("AddressLine1")
        @Expose
        var addressLine1: String? = null,

        @SerializedName("AddressLine2")
        @Expose
        var addressLine2: String? = null,

        @SerializedName("City")
        @Expose
        var city: String? = null,

        @SerializedName("Region")
        @Expose
        var region: String? = null,

        @SerializedName("Country")
        @Expose
        var country: String? = null,

        @SerializedName("Zipcode")
        @Expose
        var zipcode: String? = null)

data class ContactDetails(
        @SerializedName("FullName")
        @Expose
        var fullName: String? = null,

        @SerializedName("PrimaryContactNumber")
        @Expose
        var primaryContactNumber: String? = null,

        @SerializedName("SecondaryContactNumber")
        @Expose
        var secondaryContactNumber: String? = null,

        @SerializedName("EmailId")
        @Expose
        var emailId: String? = null
)
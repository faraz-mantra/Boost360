package dev.patrickgold.florisboard.customization.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CreatedOffer(
        @SerializedName("Status")
        @Expose
        var status: String? = null,

        @SerializedName("Message")
        @Expose
        var message: String? = null,

        @SerializedName("Data")
        @Expose
        var data: Data? = null) {
}

data class Data(
        @SerializedName("_id")
        @Expose
        var id: String? = null,

        @SerializedName("Product")
        @Expose
        var product: Product? = null,

        @SerializedName("Price")
        @Expose
        var price: Double = 0.0,

        @SerializedName("Quantity")
        @Expose
        var quantity: Int = 0,

        @SerializedName("MaxQuantityPerOrder")
        @Expose
        var maxQuantityPerOrder: Int = 0,

        @SerializedName("Url")
        @Expose
        var url: String? = null,

        @SerializedName("ExpiresOn")
        @Expose
        var expiresOn: String? = null,

        @SerializedName("CreatedOn")
        @Expose
        var createdOn: String? = null,

        @SerializedName("UpdatedOn")
        @Expose
        var updatedOn: String? = null,

        @SerializedName("IsArchived")
        @Expose
        var isArchived: Boolean = false,

        @SerializedName("SellerId")
        @Expose
        var sellerId: String? = null)
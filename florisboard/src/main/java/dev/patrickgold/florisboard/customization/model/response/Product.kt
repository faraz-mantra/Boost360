package dev.patrickgold.florisboard.customization.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import dev.patrickgold.florisboard.customization.adapter.BaseRecyclerItem
import dev.patrickgold.florisboard.customization.adapter.FeaturesEnum
import java.lang.Exception
import kotlin.math.roundToInt

data class Product(
  @SerializedName("ApplicationId")
  @Expose
  var applicationId: String? = null,

  @SerializedName("BuyOnlineLink")
  @Expose
  var buyOnlineLink: String? = null,

  @SerializedName("CreatedOn")
  @Expose
  var createdOn: String? = null,

  @SerializedName("CurrencyCode")
  @Expose
  var currencyCode: String? = null,

  @SerializedName("CustomWidgets")
  @Expose
  var customWidgets: Any? = null,

  @SerializedName("Description")
  @Expose
  var description: String? = null,

  @SerializedName("DiscountAmount")
  @Expose
  var discountAmount: String? = null,

  @SerializedName("ExternalSourceId")
  @Expose
  var externalSourceId: Any? = null,

  @SerializedName("FPTag")
  @Expose
  var fPTag: String? = null,

  @SerializedName("GPId")
  @Expose
  var gPId: Any? = null,

  @SerializedName("GroupProductId")
  @Expose
  var groupProductId: Any? = null,

  @SerializedName("ImageUri")
  @Expose
  var imageUri: String? = null,

  @SerializedName("Images")
  @Expose
  var images: Any? = null,

  @SerializedName("IsArchived")
  @Expose
  var isArchived: Boolean? = null,

  @SerializedName("IsAvailable")
  @Expose
  var isAvailable: Boolean? = null,
  @SerializedName("IsFreeShipmentAvailable")
  @Expose
  var isFreeShipmentAvailable: Boolean? = null,

  @SerializedName("MerchantName")
  @Expose
  var merchantName: String? = null,

  @SerializedName("Name")
  @Expose
  var name: String? = null,

  @SerializedName("Price")
  @Expose
  var price: String? = null,

  @SerializedName("Priority")
  @Expose
  var priority: Int? = null,

  @SerializedName("ProductIndex")
  @Expose
  var productIndex: Int? = null,

  @SerializedName("ProductUrl")
  @Expose
  var productUrl: String? = null,

  @SerializedName("ShipmentDuration")
  @Expose
  var shipmentDuration: Int? = null,

  @SerializedName("TileImageUri")
  @Expose
  var tileImageUri: Any? = null,

  @SerializedName("TotalQueries")
  @Expose
  var totalQueries: Int? = null,

  @SerializedName("UpdatedOn")
  @Expose
  var updatedOn: String? = null,

  @SerializedName("_id")
  @Expose
  var id: String? = null,

  @SerializedName("_keywords")
  @Expose
  var keywords: List<String>? = null,

  @SerializedName("availableUnits")
  @Expose
  var availableUnits: Int? = null,

  @SerializedName("sharedPlatforms")
  @Expose
  var sharedPlatforms: List<Any>? = null

) : BaseRecyclerItem() {

  var recyclerViewItem: Int = FeaturesEnum.PRODUCTS.ordinal

  override fun getViewType(): Int {
    return recyclerViewItem
  }


  fun getProductPrice(): String {
    return "${getCurrencySymbol()}${price?.toDoubleOrNull() ?: 0}"
  }

  fun getProductDiscountedPrice(): String {
    val priceD = this.price?.toDoubleOrNull()
    val discountD = this.discountAmount?.toDoubleOrNull()
    return if (priceD != null && discountD != null && priceD >= discountD) {
      return "${getCurrencySymbol()}${priceD - discountD}"
    } else ""
  }

  fun getDiscountedPrice(): Double {
    val priceD = this.price?.toDoubleOrNull() ?: 0.0
    val discountD = this.discountAmount?.toDoubleOrNull() ?: 0.0
    return priceD - discountD
  }

  fun getProductDiscountPrice(): String {
    return if (this.discountAmount == null || this.discountAmount?.isEmpty() == true) ""
    else "${getCurrencySymbol()}$discountAmount"
  }

  fun getProductOffPrice(): String {
    return if (this.discountAmount == null || this.discountAmount?.isEmpty() == true || price.isNullOrEmpty()) ""
    else {
      return try {
        val offPercent = ((discountAmount?.toDouble())?.div((price?.toDouble()!!)))?.times(100)
        if (offPercent?.toInt() ?: 0 <= 0) {
          ""
        } else "${offPercent?.roundToInt()}% OFF"
      } catch (e: Exception) {
        ""
      }
    }
  }

  fun getProductDiscountedPriceOrPrice(): String {
    val priceDis = getProductDiscountedPrice()
    return if (priceDis.isNotEmpty()) priceDis else getProductPrice()
  }

  fun getCurrencySymbol(): String? {
    if (currencyCode == "INR" || currencyCode == "" || currencyCode == null) {
      currencyCode = "₹"
    }
    return currencyCode
  }

  fun getLoaderItem(): Product {
    this.recyclerViewItem = FeaturesEnum.LOADER.ordinal
    return this
  }
}

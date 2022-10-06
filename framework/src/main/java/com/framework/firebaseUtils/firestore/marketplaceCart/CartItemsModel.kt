package com.framework.firebaseUtils.firestore.marketplaceCart

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CartItemsModel(
  @SerializedName( "item_id")
  var item_id: String? = null,

  @SerializedName( "boost_widget_key")
  var boost_widget_key: String? = null,

  @SerializedName( "feature_code")
  var feature_code: String? = null,

  @SerializedName( "item_name")
  var item_name: String? = null,

  @SerializedName( "description_title")
  var description_title: String? = null,

  @SerializedName( "link")
  var link: String? = null,

  @SerializedName( "price")
  var price: Double = 0.0,

  @SerializedName( "MRPPrice")
  var MRPPrice: Double = 0.0,

  @SerializedName( "discount")
  var discount: Int = 0,

  @SerializedName( "quantity")
  var quantity: Int = 1,

  @SerializedName( "min_purchase_months")
  var min_purchase_months: Int = 1,

  @SerializedName( "item_type")
  var item_type: String? = null,

  @SerializedName( "extended_properties")
  var extended_properties: String? = null
)
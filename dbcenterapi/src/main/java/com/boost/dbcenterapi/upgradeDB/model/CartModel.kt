package com.boost.dbcenterapi.upgradeDB.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Cart")
data class CartModel(

  @PrimaryKey
  @ColumnInfo(name = "item_id")
  var item_id: String,

  @ColumnInfo(name = "boost_widget_key")
  var boost_widget_key: String? = null,

  @ColumnInfo(name = "feature_code")
  var feature_code: String? = null,

  @ColumnInfo(name = "item_name")
  var item_name: String? = null,

  @ColumnInfo(name = "description_title")
  var description_title: String? = null,

  @ColumnInfo(name = "link")
  var link: String? = null,

  @ColumnInfo(name = "price")
  var price: Double = 0.0,

  @ColumnInfo(name = "MRPPrice")
  var MRPPrice: Double = 0.0,

  @ColumnInfo(name = "discount")
  var discount: Int = 0,

  @ColumnInfo(name = "quantity")
  var quantity: Int = 1,

  @ColumnInfo(name = "min_purchase_months")
  var min_purchase_months: Int = 1,

  @ColumnInfo(name = "item_type")
  var item_type: String,

  @ColumnInfo(name = "extended_properties")
  var extended_properties: String? = null,

  @ColumnInfo(name = "widget_type")
  var widget_type: String? = "" ,

  @ColumnInfo(name = "addon_title")
  var addon_title: String? = ""


)
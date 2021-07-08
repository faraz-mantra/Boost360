package com.boost.upgrades.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import java.io.Serializable

@Entity(tableName = "Widget")
data class WidgetModel(

  @Json(name = "id")
  @PrimaryKey
  @ColumnInfo(name = "id")
  val id: String,

  @Json(name = "title")
  @ColumnInfo(name = "title")
  val title: String?,

  @Json(name = "name")
  @ColumnInfo(name = "name")
  val name: String?,

  @Json(name = "price")
  @ColumnInfo(name = "price")
  var price: Int = 0,

  @Json(name = "MRPPrice")
  @ColumnInfo(name = "MRPPrice")
  var MRPPrice: Int = 0,

  @Json(name = "discount")
  @ColumnInfo(name = "discount")
  var discount: Int = 0,

  @Json(name = "image")
  @ColumnInfo(name = "image")
  val image: String? = null,

  @Json(name = "featureDetails")
  @ColumnInfo(name = "featureDetails")
  val featureDetails: String? = null

) : Serializable
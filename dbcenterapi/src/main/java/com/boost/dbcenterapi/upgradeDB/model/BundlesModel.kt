package com.boost.dbcenterapi.upgradeDB.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Bundles")
data class BundlesModel(

  @PrimaryKey
  @ColumnInfo(name = "bundle_id")
  var bundle_id: String,

  @ColumnInfo(name = "name")
  var name: String? = null,

  @ColumnInfo(name = "min_purchase_months")
  var min_purchase_months: Int = 1,

  @ColumnInfo(name = "overall_discount_percent")
  var overall_discount_percent: Int = 0,

  @ColumnInfo(name = "primary_image")
  var primary_image: String? = null,

  @ColumnInfo(name = "included_features")
  var included_features: String? = null,

  @ColumnInfo(name = "target_business_usecase")
  var target_business_usecase: String? = null,

  @ColumnInfo(name = "exclusive_to_categories")
  var exclusive_to_categories: String? = null,

  @ColumnInfo(name = "frequently_asked_questions")
  var frequently_asked_questions: String? = null,

  @ColumnInfo(name = "how_to_activate")
  var how_to_activate: String? = null,

  @ColumnInfo(name = "testimonials")
  var testimonials: String? = null,

  @ColumnInfo(name = "benefits")
  var benefits: String? = null,

  @ColumnInfo(name = "desc")
  var desc: String? = null,
)
package com.boost.upgrades.data.api_model.GetAllFeatures.response

data class DiscountCoupons(
  val _kid: String,
  val _parentClassId: String,
  val _parentClassName: String,
  val _propertyName: String,
  val code: String,
  val createdon: String,
  val discount_percent: Int,
  val isarchived: Boolean,
  val updatedon: String,
  val websiteid: String
)
package com.boost.cart.data.api_model.GetAllFeatures.response

data class PartnerZone(
  val _kid: String,
  val _parentClassId: String,
  val _parentClassName: String,
  val _propertyName: String,
  val createdon: String,
  val cta_feature_key: String,
  val cta_web_link: String,
  val cta_bundle_identifier: String,
  val exclusive_to_categories: List<String>,
  val exclusive_to_customers: List<String>,
  val image: Image,
  val importance: Int,
  val isarchived: Boolean,
  val title: String,
  val updatedon: String,
  val websiteid: String
)
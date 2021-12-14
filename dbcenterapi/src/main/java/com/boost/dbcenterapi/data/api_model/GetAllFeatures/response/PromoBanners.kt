package com.boost.dbcenterapi.data.api_model.GetAllFeatures.response

import com.framework.models.BaseRecyclerViewItem
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

data class PromoBanners(
  val _kid: String,
  val _parentClassId: String,
  val _parentClassName: String,
  val _propertyName: String,
  @SerializedName("createdon")
  val createdon: String,
  @SerializedName("cta_feature_key")
  val cta_feature_key: String,
  @SerializedName("cta_web_link")
  val cta_web_link: String,
  @SerializedName("cta_bundle_identifier")
  val cta_bundle_identifier: String,
  @SerializedName("exclusive_to_categories")
  val exclusive_to_categories: List<String>,
  @SerializedName("exclusive_to_customers")
  val exclusive_to_customers: List<String>,
  val image: Image? = null,
  val importance: Int? = null,
  val isarchived: Boolean,
  @SerializedName("title")
  val title: String,
  @SerializedName("updatedon")
  val updatedon: String,
  val websiteid: String,
  @SerializedName("coupon_code")
  val coupon_code: String,
  @SerializedName("extra_information")
  val extra_information: String,
  @SerializedName("expiry_date")
  val expiry_date: String,
  @SerializedName("cta_offer_identifier")
  val cta_offer_identifier: String,
) : Serializable,BaseRecyclerViewItem {

  override fun getRecyclerViewType(): Int {
    TODO("Not yet implemented")
  }
}

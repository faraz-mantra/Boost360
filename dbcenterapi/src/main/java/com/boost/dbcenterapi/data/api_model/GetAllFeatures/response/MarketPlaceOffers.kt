package com.boost.dbcenterapi.data.api_model.GetAllFeatures.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

data class MarketPlaceOffers(
  val _kid: String,
  @SerializedName("createdon")
  val createdon: String,
  @SerializedName("exclusive_to_categories")
  val exclusive_to_categories: List<String>,
  @SerializedName("Image")
  val image: PrimaryImage? = null,
  val isarchived: Boolean,
  @SerializedName("title")
  val title: String,
  @SerializedName("updatedon")
  val updatedon: String,
  @SerializedName("websiteid")
  val websiteid: String,
  @SerializedName("coupon_code")
  val coupon_code: String,
  @SerializedName("extra_information")
  val extra_information: String,
  @SerializedName("expiry_date")
  val expiry_date: String
) : Serializable

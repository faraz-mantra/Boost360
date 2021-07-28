package com.boost.upgrades.data.api_model.GetAllFeatures.response

import android.util.Log
import com.google.gson.annotations.SerializedName
import java.util.*
import kotlin.collections.ArrayList

data class Data(
  @SerializedName("_kid")
  val _kid: String,
  @SerializedName("createdon")
  val createdon: String,
  @SerializedName("feature_deals")
  val feature_deals: List<FeatureDeals>,
  @SerializedName("features")
  val features: List<Feature>,
  @SerializedName("bundles")
  val bundles: List<Bundles>,
  @SerializedName("discount_coupons")
  val discount_coupons: List<DiscountCoupons>,
  @SerializedName("promo_banners")
  val promo_banners: ArrayList<PromoBanners>,
  @SerializedName("partner_zone")
  val partner_zone: List<PartnerZone>,

  @SerializedName("isarchived")
  val isarchived: Boolean,
  @SerializedName("rootaliasurl")
  val rootaliasurl: Rootaliasurl,
  @SerializedName("schemaid")
  val schemaid: String,
  @SerializedName("updatedon")
  val updatedon: String,
  @SerializedName("userid")
  val userid: String,
  @SerializedName("websiteid")
  val websiteid: String,
  @SerializedName("expert_connect")
  val expert_connect: ExpertConnect,
  @SerializedName("video_gallery")
  val video_gallery: List<VideoGallery>,
  @SerializedName("feedback_link")
  val feedback_link: String,
  @SerializedName("_propertyName")
  val propertyName: String,
  @SerializedName("marketplace_offers")
  val marketplace_offers: ArrayList<PromoBanners>,
)

fun ArrayList<PromoBanners>.promoBannerFilter(
  expCode: String?,
  fpTag: String?
): ArrayList<PromoBanners> {
  if (isNullOrEmpty()) return ArrayList()
  val list = ArrayList<PromoBanners>()
  forEach {
    if (it.exclusive_to_categories.isNullOrEmpty().not()) {
      if (it.exclusive_to_categories!!.firstOrNull { it1 ->
          it1.toLowerCase(Locale.ROOT).trim() == expCode?.toLowerCase()
        } != null) list.add(it)
    } else if (it.exclusive_to_customers.isNullOrEmpty().not()) {
      if (it.exclusive_to_customers!!.firstOrNull { it1 ->
          it1.toLowerCase(Locale.ROOT).trim() == fpTag?.toLowerCase()
        } != null) list.add(it)
    } else if (it.exclusive_to_categories.isNullOrEmpty()) {
      if (it.exclusive_to_categories.firstOrNull { it1 ->
          it1.toLowerCase(Locale.ROOT).trim() == expCode?.toLowerCase()
        } == null) list.add(it)
    } else {
      if (it.cta_feature_key.isNullOrEmpty().not() || it.cta_feature_key.isNullOrBlank().not()
        || it.cta_bundle_identifier.isNullOrEmpty()
          .not() || it.cta_bundle_identifier.isNullOrBlank().not()
        || it.cta_web_link.isNullOrEmpty().not() || it.cta_web_link.isNullOrBlank().not()
        || it.cta_offer_identifier.isNullOrEmpty().not() || it.cta_offer_identifier.isNullOrBlank()
          .not()
      ) {
        list.add(it)
      }
    }
  }
  return list
}

fun ArrayList<PromoBanners>.promoMarketOfferFilter(
  expCode: String?,
  fpTag: String?
): ArrayList<PromoBanners> {
  if (isNullOrEmpty()) return ArrayList()
  val list = ArrayList<PromoBanners>()
  forEach {
    if (it.exclusive_to_categories.isNullOrEmpty().not()) {
      if (it.exclusive_to_categories!!.firstOrNull { it1 ->
          it1.toLowerCase(Locale.ROOT).trim() == expCode?.toLowerCase()
        } != null) list.add(it)
    } else if (it.exclusive_to_customers.isNullOrEmpty().not()) {
      if (it.exclusive_to_customers!!.firstOrNull { it1 ->
          it1.toLowerCase(Locale.ROOT).trim() == fpTag?.toLowerCase()
        } != null) {
        list.add(it)
      }
    } else if (it.exclusive_to_categories.isNullOrEmpty()) {
      list.add(it)
    }
  }
  return list
}

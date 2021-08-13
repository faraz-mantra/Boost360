package com.marketplace.model.features


import com.google.gson.annotations.SerializedName

data class Data(
  @SerializedName("bundles")
  var bundles: List<Bundle>? = null,
  @SerializedName("createdon")
  var createdon: String? = null,
  @SerializedName("discount_coupons")
  var discountCoupons: List<DiscountCoupon>? = null,
  @SerializedName("expert_connect")
  var expertConnect: ExpertConnect? = null,
  @SerializedName("feature_deals")
  var featureDeals: List<Any>? = null,
  @SerializedName("features")
  var features: List<Feature>? = null,
  @SerializedName("feedback_link")
  var feedbackLink: String? = null,
  @SerializedName("isarchived")
  var isarchived: Boolean? = null,
  @SerializedName("_kid")
  var kid: String? = null,
  @SerializedName("marketplace_offers")
  var marketplaceOffers: List<MarketplaceOffer>? = null,
  @SerializedName("partner_zone")
  var partnerZone: List<PartnerZone>? = null,
  @SerializedName("promo_banners")
  var promoBanners: List<PromoBanner>? = null,
  @SerializedName("rootaliasurl")
  var rootaliasurl: Rootaliasurl? = null,
  @SerializedName("schemaid")
  var schemaid: String? = null,
  @SerializedName("updatedon")
  var updatedon: String? = null,
  @SerializedName("userid")
  var userid: String? = null,
  @SerializedName("video_gallery")
  var videoGallery: List<VideoGallery>? = null,
  @SerializedName("websiteid")
  var websiteid: String? = null
)
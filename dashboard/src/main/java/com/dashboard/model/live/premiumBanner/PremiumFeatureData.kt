package com.dashboard.model.live.premiumBanner

import com.framework.pref.UserSessionManager
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

data class PremiumFeatureData(
  @SerializedName("bundles")
  var bundles: ArrayList<PremiumBundle>? = null,
  @SerializedName("createdon")
  var createdon: String? = null,
  @SerializedName("discount_coupons")
  var discountCoupons: ArrayList<DiscountCoupon>? = null,
  @SerializedName("expert_connect")
  var expertConnect: ExpertConnect? = null,
  @SerializedName("feature_deals")
  var featureDeals: ArrayList<Any>? = null,
  @SerializedName("features")
  var features: ArrayList<Feature>? = null,
  @SerializedName("feedback_link")
  var feedbackLink: String? = null,
  @SerializedName("isarchived")
  var isarchived: Boolean? = null,
  @SerializedName("_kid")
  var kid: String? = null,
  @SerializedName("partner_zone")
  var partnerZone: ArrayList<PartnerZone>? = null,
  @SerializedName(value = "promo_banners", alternate = ["marketplace_banners"])
  var promoBanners: ArrayList<PromoAcademyBanner>? = null,
  @SerializedName("academy_banners")
  var academyBanner: ArrayList<PromoAcademyBanner>? = null,
  @SerializedName("rootAliasUrl")
  var rootaliasurl: Rootaliasurl? = null,
  @SerializedName("schemaid")
  var schemaid: String? = null,
  @SerializedName("updatedon")
  var updatedon: String? = null,
  @SerializedName("userid")
  var userid: String? = null,
  @SerializedName("video_gallery")
  var videoGallery: ArrayList<VideoGallery>? = null,
  @SerializedName("websiteid")
  var websiteid: String? = null,
) : Serializable {

}

fun ArrayList<PromoAcademyBanner>.marketBannerFilter(session: UserSessionManager?): ArrayList<PromoAcademyBanner> {
  if (isNullOrEmpty()) return ArrayList()
  val list = ArrayList<PromoAcademyBanner>()
  val expCode = session?.fP_AppExperienceCode?.toLowerCase(Locale.ROOT)?.trim()
  forEach {
    if (it.exclusiveToCategories.isNullOrEmpty().not()) {
      if (it.exclusiveToCategories!!.firstOrNull { it1 ->
          it1.toLowerCase(Locale.ROOT).trim() == expCode
        } != null) list.add(it)
    } else list.add(it)
  }
  return list
}
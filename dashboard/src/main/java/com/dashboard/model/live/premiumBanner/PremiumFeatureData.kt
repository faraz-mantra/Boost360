package com.dashboard.model.live.premiumBanner

import com.dashboard.pref.UserSessionManager
import com.framework.utils.PreferencesUtils
import com.framework.utils.convertListObjToString
import com.framework.utils.convertStringToList
import com.framework.utils.getData
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

const val MARKETPLACE_BANNER_DATA = "MARKETPLACE_BANNER_DATA"
const val ACADEMY_BANNER_DATA = "ACADEMY_BANNER_DATA"

data class PremiumFeatureData(
    @SerializedName("bundles")
    var bundles: ArrayList<Bundle>? = null,
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
    @SerializedName("promo_banners")
    var promoBanners: ArrayList<PromoBanner>? = null,
    @SerializedName("rootaliasurl")
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

  fun getMarketPlaceBanners(): ArrayList<PromoBanner>? {
    val resp = PreferencesUtils.instance.getData(MARKETPLACE_BANNER_DATA, "") ?: ""
    return ArrayList(convertStringToList(resp) ?: ArrayList())
  }

  fun getAcademyBanners(): ArrayList<PromoBanner>? {
    val resp = PreferencesUtils.instance.getData(ACADEMY_BANNER_DATA, "") ?: ""
    return ArrayList(convertStringToList(resp) ?: ArrayList())
  }

  fun saveDataMarketPlace() {
    PreferencesUtils.instance.saveDataN(MARKETPLACE_BANNER_DATA, convertListObjToString(promoBanners ?: ArrayList()) ?: "")
  }

  fun saveDataAcademy() {
    PreferencesUtils.instance.saveDataN(ACADEMY_BANNER_DATA, convertListObjToString(promoBanners ?: ArrayList()) ?: "")
  }
}

fun ArrayList<PromoBanner>.marketBannerFilter(session: UserSessionManager?): ArrayList<PromoBanner> {
  if (isNullOrEmpty()) return ArrayList()
  val list = ArrayList<PromoBanner>()
  val expCode = session?.fP_AppExperienceCode?.toLowerCase(Locale.ROOT)?.trim()
  forEach {
    if (it.exclusiveToCategories.isNullOrEmpty().not()) {
      if (it.exclusiveToCategories!!.firstOrNull { it1 -> it1.toLowerCase(Locale.ROOT).trim() == expCode } != null) list.add(it)
    }
  }
  return list
}
package com.dashboard.model.live.dashboardBanner

import com.framework.pref.UserSessionManager
import com.framework.utils.*
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

const val ACADEMY_BANNER_DATA = "ACADEMY_BANNER_DATA"

const val MARKETPLACE_BANNER_DATA = "MARKETPLACE_BANNER_DATA"

data class DashboardBannerData(
  @SerializedName("academy_banners")
  var academyBanners: ArrayList<DashboardAcademyBanner>? = null,
//    @SerializedName("createdon")
//    var createdon: String? = null,
//    @SerializedName("isarchived")
//    var isarchived: Boolean? = null,
//    @SerializedName("_kid")
//    var kid: String? = null,
  @SerializedName("marketplace_banners")
  var marketplaceBanners: ArrayList<DashboardMarketplaceBanner>? = null,
//    @SerializedName("rootAliasUrl")
//    var rootAliasUrl: RootAliasUrl? = null,
//    @SerializedName("schemaid")
//    var schemaid: String? = null,
//    @SerializedName("updatedon")
//    var updatedon: String? = null,
//    @SerializedName("userid")
//    var userid: String? = null,
//    @SerializedName("websiteid")
//    var websiteid: String? = null
) : Serializable

fun getAcademyBanners(): ArrayList<DashboardAcademyBanner>? {
  val resp = PreferencesUtils.instance.getData(ACADEMY_BANNER_DATA, "") ?: ""
  return ArrayList(convertStringToList(resp) ?: ArrayList())
}

fun saveDataAcademy(academyBanner: ArrayList<DashboardAcademyBanner>?) {
  PreferencesUtils.instance.saveData(
    ACADEMY_BANNER_DATA,
    convertListObjToString(academyBanner ?: ArrayList()) ?: ""
  )
}

fun getMarketPlaceBanners(): ArrayList<DashboardMarketplaceBanner>? {
  val resp = PreferencesUtils.instance.getData(MARKETPLACE_BANNER_DATA, "") ?: ""
  return ArrayList(convertStringToList(resp) ?: ArrayList())
}

fun saveDataMarketPlace(marketBanners: ArrayList<DashboardMarketplaceBanner>?) {
  PreferencesUtils.instance.saveData(
    MARKETPLACE_BANNER_DATA,
    convertListObjToString(marketBanners ?: ArrayList()) ?: ""
  )
}

fun ArrayList<DashboardMarketplaceBanner>.marketBannerFilter(session: UserSessionManager?): ArrayList<DashboardMarketplaceBanner> {
  if (isNullOrEmpty()) return ArrayList()
  val list = ArrayList<DashboardMarketplaceBanner>()
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
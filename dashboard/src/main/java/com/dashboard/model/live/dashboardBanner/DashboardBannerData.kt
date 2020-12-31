package com.dashboard.model.live.dashboardBanner

import com.framework.utils.PreferencesUtils
import com.framework.utils.convertListObjToString
import com.framework.utils.convertStringToList
import com.framework.utils.getData
import com.google.gson.annotations.SerializedName
import java.io.Serializable

const val ACADEMY_BANNER_DATA = "ACADEMY_BANNER_DATA"
data class DashboardBannerData(
    @SerializedName("academy_banners")
    var academyBanners:ArrayList<DashboardAcademyBanner>? = null,
    @SerializedName("createdon")
    var createdon: String? = null,
    @SerializedName("isarchived")
    var isarchived: Boolean? = null,
    @SerializedName("_kid")
    var kid: String? = null,
    @SerializedName("marketplace_banners")
    var marketplaceBanners: ArrayList<DashboardMarketplaceBanner>? = null,
    @SerializedName("rootaliasurl")
    var rootaliasurl: Rootaliasurl? = null,
    @SerializedName("schemaid")
    var schemaid: String? = null,
    @SerializedName("updatedon")
    var updatedon: String? = null,
    @SerializedName("userid")
    var userid: String? = null,
    @SerializedName("websiteid")
    var websiteid: String? = null
): Serializable

fun getAcademyBanners(): ArrayList<DashboardAcademyBanner>? {
    val resp = PreferencesUtils.instance.getData(ACADEMY_BANNER_DATA, "") ?: ""
    return ArrayList(convertStringToList(resp) ?: ArrayList())
}

fun saveDataAcademy(academyBanner: ArrayList<DashboardAcademyBanner>?) {
    PreferencesUtils.instance.saveDataN(ACADEMY_BANNER_DATA, convertListObjToString(academyBanner ?: ArrayList()) ?: "")
}
package com.dashboard.model.live.dashboardBanner

import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.recyclerView.AppBaseRecyclerViewItem
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DashboardAcademyBanner(
  @SerializedName("banner_image")
  var bannerImage: BannerImage? = null,
//    @SerializedName("createdon")
//    var createdon: String? = null,
  @SerializedName("cta_file_link")
  var ctaFileLink: String? = null,
  @SerializedName("cta_web_link")
  var ctaWebLink: String? = null,
  @SerializedName("cta_youtube_link")
  var ctaYoutubeLink: String? = null,
//    @SerializedName("importance")
//    var importance: Double? = null,
//    @SerializedName("isarchived")
//    var isarchived: Boolean? = null,
//    @SerializedName("_kid")
//    var kid: String? = null,
//    @SerializedName("_parentClassId")
//    var parentClassId: String? = null,
//    @SerializedName("_parentClassName")
//    var parentClassName: String? = null,
//    @SerializedName("_propertyName")
//    var propertyName: String? = null,
//    @SerializedName("updatedon")
//    var updatedon: String? = null,
//    @SerializedName("websiteid")
//    var websiteid: String? = null
) : Serializable, AppBaseRecyclerViewItem {

  var recyclerViewItemType: Int = RecyclerViewItemType.RIA_ACADEMY_ITEM_VIEW.getLayout()

  override fun getViewType(): Int {
    return recyclerViewItemType
  }

}
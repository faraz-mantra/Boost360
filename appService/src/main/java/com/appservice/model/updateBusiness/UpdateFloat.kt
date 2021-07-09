package com.appservice.model.updateBusiness

import com.appservice.constant.RecyclerViewItemType
import com.appservice.recyclerView.AppBaseRecyclerViewItem
import com.framework.utils.DateUtils.FORMAT_SERVER_DATE
import com.framework.utils.DateUtils.FORMAT_SERVER_TO_LOCAL_3
import com.framework.utils.DateUtils.parseDate
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class UpdateFloat(
  @SerializedName("createdOn")
  var createdOn: String? = null,
  @SerializedName("groupMessageId")
  var groupMessageId: Any? = null,
  @SerializedName("htmlString")
  var htmlString: Any? = null,
  @SerializedName("_id")
  var id: String? = null,
  @SerializedName("imageUri")
  var imageUri: String? = null,
  @SerializedName("isHtmlString")
  var isHtmlString: Boolean? = null,
  @SerializedName("message")
  var message: String? = null,
  @SerializedName("tileImageUri")
  var tileImageUri: String? = null,
  @SerializedName("type")
  var type: Any? = null,
  @SerializedName("url")
  var url: String? = null
) : Serializable, AppBaseRecyclerViewItem {

  var recyclerViewItem: Int = RecyclerViewItemType.UPDATE_BUSINESS_ITEM_VIEW.getLayout()
  override fun getViewType(): Int {
    return recyclerViewItem
  }

  fun getDateValue(): String {
    var sDate = this.createdOn
    if (sDate?.contains("/Date") == true) sDate = sDate.replace("/Date(", "").replace(")/", "")
    val epochTime = sDate?.toLongOrNull()
    return if (epochTime != null) Date(epochTime).parseDate(FORMAT_SERVER_TO_LOCAL_3) ?: ""
    else parseDate(sDate, FORMAT_SERVER_DATE, FORMAT_SERVER_TO_LOCAL_3, timeZone = TimeZone.getTimeZone("UTC")) ?: ""
  }

  fun getLoaderItem(): UpdateFloat {
    this.recyclerViewItem = RecyclerViewItemType.PAGINATION_LOADER.getLayout()
    return this
  }
}
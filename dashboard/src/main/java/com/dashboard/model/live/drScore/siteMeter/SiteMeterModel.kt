package com.dashboard.model.live.drScore.siteMeter

import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.recyclerView.AppBaseRecyclerViewItem
import com.dashboard.utils.*
import java.io.Serializable

class SiteMeterModel(
    var position: Int? = null,
    var Title: String? = null,
    var Desc: String? = null,
    var Percentage: String? = null,
    var status: Boolean? = null,
    var sortChar: Int = 0,
    var isPost: Boolean = false,
) : AppBaseRecyclerViewItem, Serializable, Comparable<Any?> {

  var recyclerViewItemType: Int = RecyclerViewItemType.ITEMS_CONTENT_SETUP_ITEM_VIEW.getLayout()

  override fun getViewType(): Int {
    return recyclerViewItemType
  }

  override fun compareTo(other: Any?): Int {
    val cVal = (other as? SiteMeterModel)?.sortChar ?: 0
    return cVal - sortChar
  }


  enum class TypePosition(var value: Int) {
    BUSINESS_NAME(businessName),
    DESCRIPTION(description),
    CATEGORY(category),
    EMAIL(email),
    PHONE(phone),
    ADDRESS(address),
    BUSINESS_HOURS(businessHoursV),
    IMAGE(image),
    LOGO(logo),
    POST(post),
    SOCIAL(social),
    DOMAIN(domain);

    companion object {
      fun fromValue(value: Int?): TypePosition? = values().firstOrNull { it.value == value }
    }
  }
}
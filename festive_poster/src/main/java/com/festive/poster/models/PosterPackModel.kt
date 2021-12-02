package com.festive.poster.models

import com.festive.poster.FestivePosterApplication
import com.festive.poster.constant.RecyclerViewItemType
import com.festive.poster.recyclerView.AppBaseRecyclerViewItem
import com.framework.pref.Key_Preferences
import com.framework.pref.UserSessionManager
import com.framework.utils.convertStringToList

open class PosterPackModel(
  val tagsModel: PosterPackTagModel,
  var posterList: ArrayList<PosterModel>? = null,
  var price: Double = 0.0,
  var isPurchased: Boolean = false,
  var list_layout:Int
) : AppBaseRecyclerViewItem {
  override fun getViewType(): Int {
    return list_layout
  }

  var isSelected:Boolean = false


  fun isPurchasedN(): Boolean {
    val oldTags: List<String> = convertStringToList(UserSessionManager(FestivePosterApplication.instance).getFPDetails(Key_Preferences.FESTIVE_POSTER_PURCHASED_TAG) ?: "") ?: arrayListOf()
    val isContain = oldTags.contains(tagsModel?.tag ?: "")
    return isPurchased || isContain
  }
}
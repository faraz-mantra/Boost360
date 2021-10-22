package com.festive.poster.models

import com.festive.poster.FestivePosterApplication
import com.festive.poster.constant.RecyclerViewItemType
import com.festive.poster.recyclerView.AppBaseRecyclerViewItem
import com.framework.pref.Key_Preferences
import com.framework.pref.UserSessionManager
import com.framework.utils.convertStringToList

open class PurchasedPosterPackModel(
  tagsModel: PosterPackTagModel,
  posterList: ArrayList<PosterModel>? = null,
  price: Double = 0.0,
) : PosterPackModel(tagsModel,posterList,price,true),AppBaseRecyclerViewItem {
  override fun getViewType(): Int {
    return RecyclerViewItemType.POSTER_PACK_PURCHASED.getLayout()
  }

}
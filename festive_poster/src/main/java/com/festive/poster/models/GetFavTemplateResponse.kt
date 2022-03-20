package com.festive.poster.models

import com.festive.poster.R
import com.festive.poster.constant.RecyclerViewItemType
import com.festive.poster.recyclerView.AppBaseRecyclerViewItem
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.framework.base.BaseResponse

data class GetFavTemplateResponse(
    val Result: GetFavTemplateResponseResult?,
):BaseResponse()

data class GetFavTemplateResponseResult(
    val FavouriteTemplatesDetails: List<FavouriteTemplatesDetail>?
)

class FavouriteTemplatesDetail(
    val count: Int,
    val tag: String,
    val tagIcon: String?,
    val tagName: String?,
    val templateDetails: ArrayList<PosterModel>?,
    var isSelected:Boolean
):AppBaseRecyclerViewItem {
    override fun getViewType(): Int {
        return RecyclerViewItemType.FAV_CAT.getLayout()
    }
}
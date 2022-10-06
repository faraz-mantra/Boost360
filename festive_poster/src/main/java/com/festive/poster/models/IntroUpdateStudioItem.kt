package com.festive.poster.models

import android.graphics.drawable.Drawable
import com.festive.poster.constant.RecyclerViewItemType
import com.festive.poster.recyclerView.AppBaseRecyclerViewItem

class IntroUpdateStudioItem(
    val title:String,
    val icon:Int,
    val desc:String
):AppBaseRecyclerViewItem {
    override fun getViewType(): Int {
        return RecyclerViewItemType.UPDATE_STUDIO_INTRO_SCREEN_ITEM.getLayout()
    }
}
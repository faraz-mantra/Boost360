package com.festive.poster.models.promoModele

import android.content.Context
import androidx.annotation.DrawableRes
import com.festive.poster.R
import com.festive.poster.constant.RecyclerViewItemType
import com.festive.poster.models.PosterModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewItem
import java.io.Serializable

class SocialPreviewModel(
    val posterImg: String?,
    val title:String?,
    val desc:String?,
    val layout_id:Int,
    var shouldShow:Boolean,
    var channelType:SocialPreviewChannel
) : Serializable, AppBaseRecyclerViewItem {

    companion object{
        fun getData(): ArrayList<SocialPreviewModel> {
            return arrayListOf(
                /*SocialPreviewModel("","",RecyclerViewItemType.VIEWPAGER_TWITTER_PREVIEW.getLayout()),
                SocialPreviewModel("","",RecyclerViewItemType.FB_PREVIEW.getLayout()),
                SocialPreviewModel("","",RecyclerViewItemType.WEBSITE_PREVIEW.getLayout()),
                SocialPreviewModel("","",RecyclerViewItemType.GMB_PREVIEW.getLayout())*/

            )
        }
    }


    override fun getViewType(): Int {
        return layout_id
    }
}

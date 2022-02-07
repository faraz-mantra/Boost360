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
       return when(channelType){
            SocialPreviewChannel.INSTAGRAM->{
                RecyclerViewItemType.INSTAGRAM_PREVIEW.getLayout()
            }
           SocialPreviewChannel.TWITTER->{
               RecyclerViewItemType.VIEWPAGER_TWITTER_PREVIEW.getLayout()
           }
           SocialPreviewChannel.GMB->{
               RecyclerViewItemType.GMB_PREVIEW.getLayout()
           }
           SocialPreviewChannel.FACEBOOK->{
               RecyclerViewItemType.FB_PREVIEW.getLayout()
           }
           SocialPreviewChannel.WEBSITE->{
               RecyclerViewItemType.WEBSITE_PREVIEW.getLayout()
           }
           SocialPreviewChannel.EMAIL->{
               RecyclerViewItemType.EMAIL_PREVIEW.getLayout()
           }
           else->{
               RecyclerViewItemType.WEBSITE_PREVIEW.getLayout()
           }

        }

    }
}

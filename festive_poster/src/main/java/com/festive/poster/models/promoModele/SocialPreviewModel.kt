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
    var channelType:SocialPreviewChannel,
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
            SocialPreviewChannel.INSTAGRAM-> if (posterImg!=null){
                RecyclerViewItemType.INSTAGRAM_PREVIEW.getLayout()
            }else{
                -1
            }
           SocialPreviewChannel.TWITTER->if (posterImg!=null){
               RecyclerViewItemType.TWITTER_PREVIEW.getLayout()
           }else{
               RecyclerViewItemType.TWITTER_PREVIEW_NO_IMAGE.getLayout()
           }
           SocialPreviewChannel.GMB->if (posterImg!=null){
               RecyclerViewItemType.GMB_PREVIEW.getLayout()
           }else{
               RecyclerViewItemType.GMB_PREVIEW_NO_IMAGE.getLayout()
           }
           SocialPreviewChannel.FACEBOOK->if (posterImg!=null){
               RecyclerViewItemType.FB_PREVIEW.getLayout()
           }else{
               RecyclerViewItemType.FB_PREVIEW_NO_IMAGE.getLayout()
           }
           SocialPreviewChannel.WEBSITE->if (posterImg!=null){
               RecyclerViewItemType.WEBSITE_PREVIEW.getLayout()
           }else{
               RecyclerViewItemType.WEBSITE_PREVIEW_NO_IMAGE.getLayout()
           }
           SocialPreviewChannel.EMAIL->if (posterImg!=null){
               RecyclerViewItemType.EMAIL_PREVIEW.getLayout()
           }else{
               RecyclerViewItemType.EMAIL_PREVIEW_NO_IMAGE.getLayout()
           }
           else->{
               RecyclerViewItemType.WEBSITE_PREVIEW.getLayout()
           }

        }

    }
}

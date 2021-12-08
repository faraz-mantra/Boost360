package com.festive.poster.models.promoModele

import android.content.Context
import androidx.annotation.DrawableRes
import com.festive.poster.R
import com.festive.poster.constant.RecyclerViewItemType
import com.festive.poster.recyclerView.AppBaseRecyclerViewItem
import java.io.Serializable

class SocialPreviewModel(
    @DrawableRes val previewPageResource: Int? = null,
    @DrawableRes val socialIconResource: Int? = null
) : Serializable, AppBaseRecyclerViewItem {

    fun getData(context: Context): ArrayList<SocialPreviewModel> {
        return arrayListOf(
            SocialPreviewModel(
                previewPageResource = R.drawable.post_preview_promo_1,
                socialIconResource = R.drawable.ic_instagram_promo_grey_boundary
            ),
            SocialPreviewModel(
                previewPageResource = R.drawable.post_preview_promo_2,
                socialIconResource = R.drawable.ic_twitter_promo_grey_boundary
            ),
            SocialPreviewModel(
                previewPageResource = R.drawable.post_preview_promo_1,
                socialIconResource = R.drawable.ic_myshop_promo_grey_boundary
            ),
            SocialPreviewModel(
                previewPageResource = R.drawable.post_preview_promo_2,
                socialIconResource = R.drawable.ic_instagram_promo_grey_boundary
            )
        )
    }

    override fun getViewType(): Int {
        return RecyclerViewItemType.VIEWPAGER_SOCIAL_PREVIEW.getLayout()
    }
}

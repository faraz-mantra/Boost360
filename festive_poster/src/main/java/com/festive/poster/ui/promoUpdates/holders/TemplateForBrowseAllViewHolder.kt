package com.festive.poster.ui.promoUpdates.holders

import androidx.core.view.isVisible
import android.graphics.drawable.Drawable
import com.airbnb.lottie.LottieDrawable
import com.bumptech.glide.RequestBuilder
import com.festive.poster.R
import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.databinding.ListItemTemplateForRvBinding
import com.festive.poster.models.BrowseAllTemplate
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.ui.promoUpdates.edit_post.EditPostActivity
import com.festive.poster.utils.SvgUtils
import com.festive.poster.utils.WebEngageController
import com.framework.glide.GlideApp
import com.framework.webengageconstant.Promotional_Update_Edit_Click
import com.framework.webengageconstant.Promotional_Update_Post_Click
import com.framework.webengageconstant.Promotional_Update_WhatsApp_Share_Click


class TemplateForBrowseAllViewHolder(binding: ListItemTemplateForRvBinding):
    AppBaseRecyclerViewHolder<ListItemTemplateForRvBinding>(binding) {


    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val model = item as BrowseAllTemplate


        binding.isNew.isVisible = model.isFeatured

        if (model.isFavourite){
            binding.ivLove.setTintColor(getColor(R.color.colorEB5757)!!)
        }else{
            binding.ivLove.setTintColor(getColor(R.color.colorDBDBDB)!!)

        }
        val thumbnailRequest = GlideApp
            .with(binding.ivSvg)
            .`as`(LottieDrawable::class.java)
            .load("https://assets7.lottiefiles.com/packages/lf20_ehegqmwn.json")

        GlideApp.with(binding.ivSvg)
            .`as`(LottieDrawable::class.java)
            //   .thumbnail(thumbnailRequest)
            .load(variant?.svgUrl)
            .into(binding.ivSvg)
        SvgUtils.loadImage(model.primarySvgUrl, binding.ivSvg)
        binding.btnShare.setOnClickListener {
            WebEngageController.trackEvent(Promotional_Update_WhatsApp_Share_Click)
            listener?.onItemClick(position,item, RecyclerViewActionType.WHATSAPP_SHARE_CLICKED.ordinal)
        }

        binding.cardLove.setOnClickListener {
            listener?.onItemClick(position,item,RecyclerViewActionType.POSTER_LOVE_CLICKED.ordinal)
        }
        binding.tvTemplateDesc.text =model.primaryText

        binding.btnPost.setOnClickListener {
            WebEngageController.trackEvent(Promotional_Update_Post_Click)
           // activity?.let { it1 -> PostPreviewSocialActivity.launchActivity(it1,null,model) }
           // EditPostActivity.launchActivity(binding.root.context,model)
            listener?.onItemClick(position,item, RecyclerViewActionType.POST_CLICKED.ordinal)


        }
        binding.btnEdit.setOnClickListener {
            WebEngageController.trackEvent(Promotional_Update_Edit_Click)

            EditPostActivity.launchActivity(binding.root.context,model)
        }
        super.bind(position, item)

    }
}